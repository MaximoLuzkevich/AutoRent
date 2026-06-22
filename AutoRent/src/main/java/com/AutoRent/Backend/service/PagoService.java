package com.AutoRent.Backend.service;

import lombok.RequiredArgsConstructor;

import com.AutoRent.Backend.dto.pago.PagoDto;
import com.AutoRent.Backend.dto.pago.PagoRespuestaDto;
import com.AutoRent.Backend.exception.IdNoEncontradoException;
import com.AutoRent.Backend.exception.ParametroIncorrectoException;
import com.AutoRent.Backend.exception.PermisoInsuficienteException;
import com.AutoRent.Backend.model.Pago;
import com.AutoRent.Backend.model.Reserva;
import com.AutoRent.Backend.model.Usuario;
import com.AutoRent.Backend.model.enums.EstadoPago;
import com.AutoRent.Backend.model.enums.EstadoReserva;
import com.AutoRent.Backend.model.enums.MetodoPago;
import com.AutoRent.Backend.model.enums.NombreRol;
import com.AutoRent.Backend.repository.PagoRepository;
import com.mercadopago.resources.payment.Payment;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final ReservaService reservaService;
    private final UsuarioService usuarioService;
    private final MercadoPagoService mercadoPagoService;

    @Transactional
    public PagoRespuestaDto registrarPago(PagoDto dto) {
        Reserva reserva = reservaService.obtenerReservaPorId(dto.getIdReserva());
        validarClienteDeReserva(reserva, "No podes pagar una reserva de otro cliente");
        validarReservaPuedePagarse(reserva);
        validarMonto(reserva, dto.getMonto());
        validarDatosMetodoPago(dto);

        if (pagoRepository.existsByReservaIdReservaAndEstado(reserva.getIdReserva(), EstadoPago.APROBADO)) {
            throw new ParametroIncorrectoException("La reserva ya tiene un pago aprobado");
        }

        if (pagoRepository.existsByReservaIdReservaAndEstado(reserva.getIdReserva(), EstadoPago.PENDIENTE)) {
            throw new ParametroIncorrectoException("La reserva ya tiene un pago pendiente");
        }

        Pago pago = new Pago();
        pago.setMonto(dto.getMonto());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setEstado(EstadoPago.PENDIENTE);
        pago.setReserva(reserva);

        Pago pagoGuardado = pagoRepository.save(pago);
        String linkPago = generarLinkPago(pagoGuardado);
        return convertirARespuesta(pagoGuardado, linkPago);
    }

    public List<PagoRespuestaDto> listarPagosPorReserva(Integer idReserva) {
        Reserva reserva = reservaService.obtenerReservaPorId(idReserva);
        validarClientePropietarioOAdministrador(reserva, "No podes consultar pagos de esta reserva");

        return pagoRepository.findByReservaIdReservaOrderByFechaPagoDesc(idReserva).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<PagoRespuestaDto> listarMisPagos() {
        Integer idUsuario = usuarioService.obtenerUsuarioAutenticado().getIdUsuario();

        return pagoRepository.findByReservaClienteIdUsuarioOrderByFechaPagoDesc(idUsuario).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<PagoRespuestaDto> listarPagosDeMisAutos() {
        Integer idUsuario = usuarioService.obtenerUsuarioAutenticado().getIdUsuario();

        return pagoRepository.findByReservaAutoPropietarioIdUsuarioOrderByFechaPagoDesc(idUsuario).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<PagoRespuestaDto> listarTodosLosPagos() {
        validarAdministrador("Solo un administrador puede consultar todos los pagos");

        return pagoRepository.findAllByOrderByFechaPagoDesc().stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<PagoRespuestaDto> listarPagosPorCliente(Integer idCliente) {
        usuarioService.validarUsuarioActualOAdministrador(
                idCliente,
                "No podes consultar pagos de otro cliente"
        );

        return pagoRepository.findByReservaClienteIdUsuarioOrderByFechaPagoDesc(idCliente).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<PagoRespuestaDto> listarPagosPorPropietario(Integer idPropietario) {
        usuarioService.validarUsuarioActualOAdministrador(
                idPropietario,
                "No podes consultar pagos de otro propietario"
        );

        return pagoRepository.findByReservaAutoPropietarioIdUsuarioOrderByFechaPagoDesc(idPropietario).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<PagoRespuestaDto> listarPagosPorEstado(EstadoPago estado) {
        validarAdministrador("Solo un administrador puede consultar pagos por estado");

        return pagoRepository.findByEstadoOrderByFechaPagoDesc(estado).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<PagoRespuestaDto> listarPagosEntreFechas(LocalDate desde, LocalDate hasta) {
        validarAdministrador("Solo un administrador puede consultar pagos por fechas");

        if (desde == null || hasta == null) {
            throw new ParametroIncorrectoException("Las fechas son obligatorias");
        }

        if (hasta.isBefore(desde)) {
            throw new ParametroIncorrectoException("La fecha hasta no puede ser anterior a la fecha desde");
        }

        LocalDateTime fechaDesde = desde.atStartOfDay();
        LocalDateTime fechaHasta = hasta.atTime(23, 59, 59);

        return pagoRepository.findByFechaPagoBetweenOrderByFechaPagoDesc(fechaDesde, fechaHasta).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    @Transactional
    public PagoRespuestaDto aprobarPago(Integer idPago) {
        validarAdministrador("Solo un administrador puede aprobar pagos");
        Pago pago = obtenerPagoPorId(idPago);
        validarPagoPendiente(pago);
        validarReservaPuedePagarse(pago.getReserva());
        validarMonto(pago.getReserva(), pago.getMonto());

        if (pagoRepository.existsByReservaIdReservaAndEstado(pago.getReserva().getIdReserva(), EstadoPago.APROBADO)) {
            throw new ParametroIncorrectoException("La reserva ya tiene un pago aprobado");
        }

        pago.setEstado(EstadoPago.APROBADO);
        Pago pagoGuardado = pagoRepository.save(pago);
        reservaService.confirmarReservaPorPago(pagoGuardado.getReserva());
        return convertirARespuesta(pagoGuardado);
    }

    @Transactional
    public PagoRespuestaDto procesarPagoMercadoPago(Long idPagoMercadoPago) {
        Payment pagoMercadoPago = mercadoPagoService.obtenerPago(idPagoMercadoPago);
        Integer idPago = obtenerIdPagoInterno(pagoMercadoPago);
        Pago pago = obtenerPagoPorId(idPago);

        if (pago.getMetodoPago() != MetodoPago.MERCADO_PAGO) {
            throw new ParametroIncorrectoException("El pago no corresponde a Mercado Pago");
        }

        String estadoMercadoPago = pagoMercadoPago.getStatus();
        if ("approved".equalsIgnoreCase(estadoMercadoPago)) {
            return aprobarPagoConfirmadoPorMercadoPago(pago);
        }

        if ("rejected".equalsIgnoreCase(estadoMercadoPago)
                || "cancelled".equalsIgnoreCase(estadoMercadoPago)) {
            pago.setEstado(EstadoPago.RECHAZADO);
            return convertirARespuesta(pagoRepository.save(pago));
        }

        return convertirARespuesta(pago);
    }

    @Transactional
    public PagoRespuestaDto rechazarPago(Integer idPago) {
        validarAdministrador("Solo un administrador puede rechazar pagos");
        Pago pago = obtenerPagoPorId(idPago);
        validarPagoPendiente(pago);
        pago.setEstado(EstadoPago.RECHAZADO);
        return convertirARespuesta(pagoRepository.save(pago));
    }

    public Pago obtenerPagoPorId(Integer idPago) {
        return pagoRepository.findById(idPago)
                .orElseThrow(() -> new IdNoEncontradoException("Pago no encontrado"));
    }

    private PagoRespuestaDto convertirARespuesta(Pago pago) {
        return convertirARespuesta(pago, null);
    }

    private PagoRespuestaDto convertirARespuesta(Pago pago, String linkPago) {
        return new PagoRespuestaDto(
                pago.getIdPago(),
                pago.getMonto(),
                pago.getMetodoPago(),
                pago.getEstado(),
                pago.getFechaPago(),
                pago.getReserva().getIdReserva(),
                linkPago
        );
    }

    private void validarReservaPuedePagarse(Reserva reserva) {
        if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new ParametroIncorrectoException("Solo se pueden pagar reservas confirmadas por el propietario");
        }
    }

    private void validarMonto(Reserva reserva, BigDecimal monto) {
        if (monto.compareTo(reserva.getPrecioTotal()) != 0) {
            throw new ParametroIncorrectoException("El monto del pago debe coincidir con el total de la reserva");
        }
    }

    private void validarDatosMetodoPago(PagoDto dto) {
        if (dto.getMetodoPago() != MetodoPago.TARJETA) {
            return;
        }

        if (estaVacio(dto.getTitularTarjeta())) {
            throw new ParametroIncorrectoException("El titular de la tarjeta es obligatorio");
        }

        String numero = normalizarNumeroTarjeta(dto.getNumeroTarjeta());
        if (!numero.matches("\\d{13,19}")) {
            throw new ParametroIncorrectoException("El numero de tarjeta debe tener entre 13 y 19 digitos");
        }

        if (estaVacio(dto.getVencimientoTarjeta())
                || !dto.getVencimientoTarjeta().matches("(0[1-9]|1[0-2])/(\\d{2}|\\d{4})")) {
            throw new ParametroIncorrectoException("El vencimiento debe tener formato MM/AA o MM/AAAA");
        }

        if (estaVacio(dto.getCodigoSeguridad()) || !dto.getCodigoSeguridad().matches("\\d{3,4}")) {
            throw new ParametroIncorrectoException("El codigo de seguridad debe tener 3 o 4 digitos");
        }
    }

    private String generarLinkPago(Pago pago) {
        if (pago.getMetodoPago() != MetodoPago.MERCADO_PAGO) {
            return null;
        }

        return mercadoPagoService.crearPreferencia(pago);
    }

    private PagoRespuestaDto aprobarPagoConfirmadoPorMercadoPago(Pago pago) {
        if (pago.getEstado() == EstadoPago.APROBADO) {
            return convertirARespuesta(pago);
        }

        validarPagoPendiente(pago);
        validarReservaPuedePagarse(pago.getReserva());
        validarMonto(pago.getReserva(), pago.getMonto());

        pago.setEstado(EstadoPago.APROBADO);
        Pago pagoGuardado = pagoRepository.save(pago);
        reservaService.confirmarReservaPorPago(pagoGuardado.getReserva());
        return convertirARespuesta(pagoGuardado);
    }

    private Integer obtenerIdPagoInterno(Payment pagoMercadoPago) {
        if (pagoMercadoPago == null
                || pagoMercadoPago.getExternalReference() == null
                || pagoMercadoPago.getExternalReference().isBlank()) {
            throw new ParametroIncorrectoException("Mercado Pago no informo el pago interno");
        }

        try {
            return Integer.valueOf(pagoMercadoPago.getExternalReference());
        } catch (NumberFormatException e) {
            throw new ParametroIncorrectoException("La referencia de Mercado Pago no es valida");
        }
    }

    private boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    private String normalizarNumeroTarjeta(String numeroTarjeta) {
        if (numeroTarjeta == null) {
            return "";
        }
        return numeroTarjeta.replace(" ", "").replace("-", "");
    }

    private void validarPagoPendiente(Pago pago) {
        if (pago.getEstado() != EstadoPago.PENDIENTE) {
            throw new ParametroIncorrectoException("Solo se pueden modificar pagos pendientes");
        }
    }

    private void validarClienteDeReserva(Reserva reserva, String mensaje) {
        Integer idUsuario = usuarioService.obtenerUsuarioAutenticado().getIdUsuario();
        Integer idCliente = reserva.getCliente().getIdUsuario();

        if (!idCliente.equals(idUsuario)) {
            throw new PermisoInsuficienteException(mensaje);
        }
    }

    private void validarClientePropietarioOAdministrador(Reserva reserva, String mensaje) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();
        Integer idUsuario = usuario.getIdUsuario();
        Integer idCliente = reserva.getCliente().getIdUsuario();
        Integer idPropietario = reserva.getAuto().getPropietario().getIdUsuario();

        if (!idCliente.equals(idUsuario)
                && !idPropietario.equals(idUsuario)
                && !usuarioService.tieneRol(usuario, NombreRol.ADMINISTRADOR)) {
            throw new PermisoInsuficienteException(mensaje);
        }
    }

    private void validarAdministrador(String mensaje) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();

        if (!usuarioService.tieneRol(usuario, NombreRol.ADMINISTRADOR)) {
            throw new PermisoInsuficienteException(mensaje);
        }
    }
}

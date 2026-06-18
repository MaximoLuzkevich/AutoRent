package com.AutoRent.Backend.service;

import lombok.RequiredArgsConstructor;

import com.AutoRent.Backend.dto.reserva.ReservaDto;
import com.AutoRent.Backend.dto.reserva.ReservaRespuestaDto;
import com.AutoRent.Backend.exception.IdNoEncontradoException;
import com.AutoRent.Backend.exception.ParametroIncorrectoException;
import com.AutoRent.Backend.exception.PermisoInsuficienteException;
import com.AutoRent.Backend.model.Auto;
import com.AutoRent.Backend.model.Reserva;
import com.AutoRent.Backend.model.Usuario;
import com.AutoRent.Backend.model.enums.EstadoReserva;
import com.AutoRent.Backend.model.enums.NombreRol;
import com.AutoRent.Backend.repository.AutoRepository;
import com.AutoRent.Backend.repository.ReservaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final AutoRepository autoRepository;
    private final UsuarioService usuarioService;


    @Transactional
    public ReservaRespuestaDto crearReservaAutenticada(ReservaDto dto) {
        Usuario cliente = usuarioService.obtenerUsuarioAutenticado();
        return crearReservaParaCliente(cliente, dto);
    }

    @Transactional
    public ReservaRespuestaDto crearReserva(Integer idCliente, ReservaDto dto) {
        usuarioService.validarUsuarioActualOAdministrador(
                idCliente,
                "No podes crear reservas para otro usuario"
        );

        Usuario cliente = usuarioService.obtenerUsuarioPorId(idCliente);
        return crearReservaParaCliente(cliente, dto);
    }

    private ReservaRespuestaDto crearReservaParaCliente(Usuario cliente, ReservaDto dto) {
        validarFechas(dto);

        Auto auto = autoRepository.findById(dto.getIdAuto())
                .orElseThrow(() -> new IdNoEncontradoException("Auto no encontrado"));

        if (!Boolean.TRUE.equals(auto.getActivo())) {
            throw new ParametroIncorrectoException("El auto no esta disponible");
        }

        if (auto.getPropietario().getIdUsuario().equals(cliente.getIdUsuario())) {
            throw new ParametroIncorrectoException("No podes reservar tu propio auto");
        }

        if (existeReservaSuperpuesta(dto)) {
            throw new ParametroIncorrectoException("El auto ya esta reservado en esas fechas");
        }

        Reserva reserva = new Reserva();
        reserva.setFechaInicio(dto.getFechaInicio());
        reserva.setFechaFin(dto.getFechaFin());
        reserva.setPrecioTotal(calcularPrecioTotal(auto, dto));
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setCliente(cliente);
        reserva.setAuto(auto);

        Reserva reservaGuardada = reservaRepository.save(reserva);
        return convertirARespuesta(reservaGuardada);
    }

    public List<ReservaRespuestaDto> listarReservasPorCliente(Integer idCliente) {
        usuarioService.validarUsuarioActualOAdministrador(
                idCliente,
                "No podes consultar reservas de otro cliente"
        );
        usuarioService.obtenerUsuarioPorId(idCliente);

        return reservaRepository.findByClienteIdUsuarioOrderByFechaInicioDesc(idCliente).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<ReservaRespuestaDto> listarMisReservas() {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();
        return reservaRepository.findByClienteIdUsuarioOrderByFechaInicioDesc(usuario.getIdUsuario()).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<ReservaRespuestaDto> listarMisReservasPorEstado(EstadoReserva estado) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();
        return reservaRepository.findByClienteIdUsuarioAndEstadoOrderByFechaInicioDesc(
                        usuario.getIdUsuario(),
                        estado
                ).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<ReservaRespuestaDto> listarMisReservasPorFechas(LocalDate desde, LocalDate hasta) {
        validarRangoFechas(desde, hasta);

        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();
        return reservaRepository.findByClienteIdUsuarioAndFechaInicioBetweenOrderByFechaInicioDesc(
                        usuario.getIdUsuario(),
                        desde,
                        hasta
                ).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<ReservaRespuestaDto> listarReservasPorPropietario(Integer idPropietario) {
        usuarioService.validarUsuarioActualOAdministrador(
                idPropietario,
                "No podes consultar reservas de otro propietario"
        );
        usuarioService.obtenerUsuarioPorId(idPropietario);

        return reservaRepository.findByAutoPropietarioIdUsuarioOrderByFechaInicioDesc(idPropietario).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<ReservaRespuestaDto> listarReservasPorAutoDePropietario(Integer idPropietario, Integer idAuto) {
        usuarioService.validarUsuarioActualOAdministrador(
                idPropietario,
                "No podes consultar reservas de otro propietario"
        );
        usuarioService.obtenerUsuarioPorId(idPropietario);

        return reservaRepository.findByAutoPropietarioIdUsuarioAndAutoIdAutoOrderByFechaInicioDesc(
                        idPropietario,
                        idAuto
                ).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<ReservaRespuestaDto> listarReservasPorEstado(EstadoReserva estado) {
        validarAdministrador("Solo un administrador puede consultar reservas por estado");

        return reservaRepository.findByEstadoOrderByFechaReservaDesc(estado).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<ReservaRespuestaDto> listarMisReservasPendientesComoPropietario() {
        Usuario propietario = usuarioService.obtenerUsuarioAutenticado();

        return reservaRepository.findByAutoPropietarioIdUsuarioAndEstadoOrderByFechaReservaDesc(
                        propietario.getIdUsuario(),
                        EstadoReserva.PENDIENTE
                ).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public ReservaRespuestaDto buscarPorId(Integer idReserva) {
        Reserva reserva = obtenerReservaPorId(idReserva);
        validarClientePropietarioOAdministrador(reserva);
        return convertirARespuesta(reserva);
    }

    @Transactional
    public ReservaRespuestaDto confirmarReserva(Integer idReserva) {
        Reserva reserva = obtenerReservaPorId(idReserva);
        validarAdministrador("Solo un administrador puede confirmar reservas");
        validarEstadoActual(reserva, EstadoReserva.PENDIENTE, "Solo se pueden confirmar reservas pendientes");
        reserva.setEstado(EstadoReserva.CONFIRMADA);
        return convertirARespuesta(reservaRepository.save(reserva));
    }

    @Transactional
    public ReservaRespuestaDto cancelarReserva(Integer idReserva) {
        Reserva reserva = obtenerReservaPorId(idReserva);
        validarClienteOAdministrador(reserva);
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new ParametroIncorrectoException("La reserva ya esta cancelada");
        }
        if (reserva.getEstado() == EstadoReserva.FINALIZADA) {
            throw new ParametroIncorrectoException("No se puede cancelar una reserva finalizada");
        }
        reserva.setEstado(EstadoReserva.CANCELADA);
        return convertirARespuesta(reservaRepository.save(reserva));
    }

    @Transactional
    public ReservaRespuestaDto finalizarReserva(Integer idReserva) {
        Reserva reserva = obtenerReservaPorId(idReserva);
        validarAdministrador("Solo un administrador puede finalizar reservas");
        validarEstadoActual(reserva, EstadoReserva.CONFIRMADA, "Solo se pueden finalizar reservas confirmadas");
        reserva.setEstado(EstadoReserva.FINALIZADA);
        return convertirARespuesta(reservaRepository.save(reserva));
    }

    public Reserva obtenerReservaPorId(Integer idReserva) {
        return reservaRepository.findById(idReserva)
                .orElseThrow(() -> new IdNoEncontradoException("Reserva no encontrada"));
    }

    private void validarFechas(ReservaDto dto) {
        if (dto.getFechaInicio().isBefore(LocalDate.now())) {
            throw new ParametroIncorrectoException("La fecha de inicio no puede ser anterior a hoy");
        }

        if (!dto.getFechaFin().isAfter(dto.getFechaInicio())) {
            throw new ParametroIncorrectoException("La fecha de fin debe ser posterior a la fecha de inicio");
        }
    }

    private boolean existeReservaSuperpuesta(ReservaDto dto) {
        return reservaRepository.existsByAutoIdAutoAndEstadoInAndFechaInicioBeforeAndFechaFinAfter(
                dto.getIdAuto(),
                List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA),
                dto.getFechaFin(),
                dto.getFechaInicio()
        );
    }

    private BigDecimal calcularPrecioTotal(Auto auto, ReservaDto dto) {
        long dias = ChronoUnit.DAYS.between(dto.getFechaInicio(), dto.getFechaFin());
        return auto.getPrecioDia().multiply(BigDecimal.valueOf(dias));
    }

    @Transactional
    public void confirmarReservaPorPago(Reserva reserva) {
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new ParametroIncorrectoException("No se puede aprobar un pago de una reserva cancelada");
        }
        if (reserva.getEstado() == EstadoReserva.FINALIZADA) {
            throw new ParametroIncorrectoException("No se puede aprobar un pago de una reserva finalizada");
        }
        if (reserva.getEstado() == EstadoReserva.PENDIENTE) {
            reserva.setEstado(EstadoReserva.CONFIRMADA);
            reservaRepository.save(reserva);
        }
    }

    private void validarRangoFechas(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new ParametroIncorrectoException("Las fechas son obligatorias");
        }

        if (hasta.isBefore(desde)) {
            throw new ParametroIncorrectoException("La fecha hasta no puede ser anterior a la fecha desde");
        }
    }

    private void validarEstadoActual(Reserva reserva, EstadoReserva estadoEsperado, String mensaje) {
        if (reserva.getEstado() != estadoEsperado) {
            throw new ParametroIncorrectoException(mensaje);
        }
    }

    private void validarClienteOAdministrador(Reserva reserva) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();
        Integer idCliente = reserva.getCliente().getIdUsuario();

        if (!idCliente.equals(usuario.getIdUsuario())
                && !usuarioService.tieneRol(usuario, NombreRol.ADMINISTRADOR)) {
            throw new PermisoInsuficienteException("No podes modificar esta reserva");
        }
    }

    private void validarClientePropietarioOAdministrador(Reserva reserva) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();
        Integer idCliente = reserva.getCliente().getIdUsuario();
        Integer idPropietario = reserva.getAuto().getPropietario().getIdUsuario();

        if (!idCliente.equals(usuario.getIdUsuario())
                && !idPropietario.equals(usuario.getIdUsuario())
                && !usuarioService.tieneRol(usuario, NombreRol.ADMINISTRADOR)) {
            throw new PermisoInsuficienteException("No podes modificar esta reserva");
        }
    }

    private void validarAdministrador(String mensaje) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();

        if (!usuarioService.tieneRol(usuario, NombreRol.ADMINISTRADOR)) {
            throw new PermisoInsuficienteException(mensaje);
        }
    }

    private ReservaRespuestaDto convertirARespuesta(Reserva reserva) {
        Auto auto = reserva.getAuto();

        return new ReservaRespuestaDto(
                reserva.getIdReserva(),
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                reserva.getPrecioTotal(),
                reserva.getEstado(),
                reserva.getFechaReserva(),
                reserva.getCliente().getIdUsuario(),
                reserva.getCliente().getNombre(),
                auto.getIdAuto(),
                auto.getMarca() + " " + auto.getModelo()
        );
    }
}

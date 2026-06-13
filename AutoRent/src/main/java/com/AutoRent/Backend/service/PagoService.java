package com.AutoRent.Backend.service;

import lombok.RequiredArgsConstructor;

import com.AutoRent.Backend.dto.pago.PagoDto;
import com.AutoRent.Backend.dto.pago.PagoRespuestaDto;
import com.AutoRent.Backend.exception.IdNoEncontradoException;
import com.AutoRent.Backend.exception.ParametroIncorrectoException;
import com.AutoRent.Backend.model.Pago;
import com.AutoRent.Backend.model.Reserva;
import com.AutoRent.Backend.model.enums.EstadoPago;
import com.AutoRent.Backend.model.enums.EstadoReserva;
import com.AutoRent.Backend.repository.PagoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final ReservaService reservaService;


    public PagoRespuestaDto registrarPago(PagoDto dto) {
        Reserva reserva = reservaService.obtenerReservaPorId(dto.getIdReserva());
        validarReservaPuedePagarse(reserva);
        validarMonto(reserva, dto.getMonto());

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

        return convertirARespuesta(pagoRepository.save(pago));
    }

    public List<PagoRespuestaDto> listarPagosPorReserva(Integer idReserva) {
        reservaService.obtenerReservaPorId(idReserva);

        return pagoRepository.findByReservaIdReservaOrderByFechaPagoDesc(idReserva).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<PagoRespuestaDto> listarPagosPorCliente(Integer idCliente) {
        return pagoRepository.findByReservaClienteIdUsuarioOrderByFechaPagoDesc(idCliente).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<PagoRespuestaDto> listarPagosPorPropietario(Integer idPropietario) {
        return pagoRepository.findByReservaAutoPropietarioIdUsuarioOrderByFechaPagoDesc(idPropietario).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<PagoRespuestaDto> listarPagosPorEstado(EstadoPago estado) {
        return pagoRepository.findByEstadoOrderByFechaPagoDesc(estado).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<PagoRespuestaDto> listarPagosEntreFechas(LocalDate desde, LocalDate hasta) {
        if (hasta.isBefore(desde)) {
            throw new ParametroIncorrectoException("La fecha hasta no puede ser anterior a la fecha desde");
        }

        LocalDateTime fechaDesde = desde.atStartOfDay();
        LocalDateTime fechaHasta = hasta.atTime(23, 59, 59);

        return pagoRepository.findByFechaPagoBetweenOrderByFechaPagoDesc(fechaDesde, fechaHasta).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public PagoRespuestaDto aprobarPago(Integer idPago) {
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

    public PagoRespuestaDto rechazarPago(Integer idPago) {
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
        return new PagoRespuestaDto(
                pago.getIdPago(),
                pago.getMonto(),
                pago.getMetodoPago(),
                pago.getEstado(),
                pago.getFechaPago(),
                pago.getReserva().getIdReserva()
        );
    }

    private void validarReservaPuedePagarse(Reserva reserva) {
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new ParametroIncorrectoException("No se puede pagar una reserva cancelada");
        }
        if (reserva.getEstado() == EstadoReserva.FINALIZADA) {
            throw new ParametroIncorrectoException("No se puede pagar una reserva finalizada");
        }
    }

    private void validarMonto(Reserva reserva, BigDecimal monto) {
        if (monto.compareTo(reserva.getPrecioTotal()) != 0) {
            throw new ParametroIncorrectoException("El monto del pago debe coincidir con el total de la reserva");
        }
    }

    private void validarPagoPendiente(Pago pago) {
        if (pago.getEstado() != EstadoPago.PENDIENTE) {
            throw new ParametroIncorrectoException("Solo se pueden modificar pagos pendientes");
        }
    }
}

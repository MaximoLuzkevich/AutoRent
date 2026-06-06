package com.AutoRent.Backend.service;

import lombok.RequiredArgsConstructor;

import com.AutoRent.Backend.dto.pago.PagoDto;
import com.AutoRent.Backend.dto.pago.PagoRespuestaDto;
import com.AutoRent.Backend.exception.IdNoEncontradoException;
import com.AutoRent.Backend.model.Pago;
import com.AutoRent.Backend.model.Reserva;
import com.AutoRent.Backend.model.enums.EstadoPago;
import com.AutoRent.Backend.repository.PagoRepository;
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
        LocalDateTime fechaDesde = desde.atStartOfDay();
        LocalDateTime fechaHasta = hasta.atTime(23, 59, 59);

        return pagoRepository.findByFechaPagoBetweenOrderByFechaPagoDesc(fechaDesde, fechaHasta).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public PagoRespuestaDto aprobarPago(Integer idPago) {
        Pago pago = obtenerPagoPorId(idPago);
        pago.setEstado(EstadoPago.APROBADO);
        return convertirARespuesta(pagoRepository.save(pago));
    }

    public PagoRespuestaDto rechazarPago(Integer idPago) {
        Pago pago = obtenerPagoPorId(idPago);
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
}

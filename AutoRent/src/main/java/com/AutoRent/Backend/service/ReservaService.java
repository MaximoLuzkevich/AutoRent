package com.AutoRent.Backend.service;

import lombok.RequiredArgsConstructor;

import com.AutoRent.Backend.dto.reserva.ReservaDto;
import com.AutoRent.Backend.dto.reserva.ReservaRespuestaDto;
import com.AutoRent.Backend.exception.IdNoEncontradoException;
import com.AutoRent.Backend.exception.ParametroIncorrectoException;
import com.AutoRent.Backend.model.Auto;
import com.AutoRent.Backend.model.Reserva;
import com.AutoRent.Backend.model.Usuario;
import com.AutoRent.Backend.model.enums.EstadoReserva;
import com.AutoRent.Backend.repository.AutoRepository;
import com.AutoRent.Backend.repository.ReservaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final AutoRepository autoRepository;
    private final UsuarioService usuarioService;


    public ReservaRespuestaDto crearReserva(Integer idCliente, ReservaDto dto) {
        validarFechas(dto);

        Usuario cliente = usuarioService.obtenerUsuarioPorId(idCliente);
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
        usuarioService.obtenerUsuarioPorId(idCliente);

        return reservaRepository.findByClienteIdUsuarioOrderByFechaInicioDesc(idCliente).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<ReservaRespuestaDto> listarReservasPorPropietario(Integer idPropietario) {
        usuarioService.obtenerUsuarioPorId(idPropietario);

        return reservaRepository.findByAutoPropietarioIdUsuarioOrderByFechaInicioDesc(idPropietario).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<ReservaRespuestaDto> listarReservasPorAutoDePropietario(Integer idPropietario, Integer idAuto) {
        usuarioService.obtenerUsuarioPorId(idPropietario);

        return reservaRepository.findByAutoPropietarioIdUsuarioAndAutoIdAutoOrderByFechaInicioDesc(
                        idPropietario,
                        idAuto
                ).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<ReservaRespuestaDto> listarReservasPorEstado(EstadoReserva estado) {
        return reservaRepository.findByEstadoOrderByFechaReservaDesc(estado).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public ReservaRespuestaDto buscarPorId(Integer idReserva) {
        Reserva reserva = obtenerReservaPorId(idReserva);
        return convertirARespuesta(reserva);
    }

    public ReservaRespuestaDto confirmarReserva(Integer idReserva) {
        Reserva reserva = obtenerReservaPorId(idReserva);
        validarEstadoActual(reserva, EstadoReserva.PENDIENTE, "Solo se pueden confirmar reservas pendientes");
        reserva.setEstado(EstadoReserva.CONFIRMADA);
        return convertirARespuesta(reservaRepository.save(reserva));
    }

    public ReservaRespuestaDto cancelarReserva(Integer idReserva) {
        Reserva reserva = obtenerReservaPorId(idReserva);
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new ParametroIncorrectoException("La reserva ya esta cancelada");
        }
        if (reserva.getEstado() == EstadoReserva.FINALIZADA) {
            throw new ParametroIncorrectoException("No se puede cancelar una reserva finalizada");
        }
        reserva.setEstado(EstadoReserva.CANCELADA);
        return convertirARespuesta(reservaRepository.save(reserva));
    }

    public ReservaRespuestaDto finalizarReserva(Integer idReserva) {
        Reserva reserva = obtenerReservaPorId(idReserva);
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

    private void validarEstadoActual(Reserva reserva, EstadoReserva estadoEsperado, String mensaje) {
        if (reserva.getEstado() != estadoEsperado) {
            throw new ParametroIncorrectoException(mensaje);
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

package com.AutoRent.Backend.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.AutoRent.Backend.dto.reserva.ReservaDto;
import com.AutoRent.Backend.exception.ParametroIncorrectoException;
import com.AutoRent.Backend.exception.PermisoInsuficienteException;
import com.AutoRent.Backend.model.Auto;
import com.AutoRent.Backend.model.Reserva;
import com.AutoRent.Backend.model.Usuario;
import com.AutoRent.Backend.model.enums.EstadoReserva;
import com.AutoRent.Backend.repository.AutoRepository;
import com.AutoRent.Backend.repository.ReservaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private AutoRepository autoRepository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private ReservaService reservaService;

    @Test
    void crearReservaDeAutoPropioLanzaParametroIncorrecto() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);

        Auto auto = new Auto();
        auto.setIdAuto(10);
        auto.setActivo(true);
        auto.setPrecioDia(BigDecimal.valueOf(10000));
        auto.setPropietario(usuario);

        ReservaDto dto = new ReservaDto(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                10
        );

        when(usuarioService.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(autoRepository.findById(10)).thenReturn(Optional.of(auto));

        assertThrows(ParametroIncorrectoException.class, () -> reservaService.crearReservaAutenticada(dto));
    }

    @Test
    void buscarReservaAjenaLanzaPermisoInsuficiente() {
        Usuario cliente = new Usuario();
        cliente.setIdUsuario(1);

        Usuario propietario = new Usuario();
        propietario.setIdUsuario(2);

        Usuario otroUsuario = new Usuario();
        otroUsuario.setIdUsuario(3);

        Auto auto = new Auto();
        auto.setIdAuto(10);
        auto.setPropietario(propietario);

        Reserva reserva = new Reserva();
        reserva.setIdReserva(5);
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setCliente(cliente);
        reserva.setAuto(auto);

        when(reservaRepository.findById(5)).thenReturn(Optional.of(reserva));
        when(usuarioService.obtenerUsuarioAutenticado()).thenReturn(otroUsuario);

        assertThrows(PermisoInsuficienteException.class, () -> reservaService.buscarPorId(5));
    }

    @Test
    void crearReservaSuperpuestaLanzaParametroIncorrecto() {
        Usuario cliente = new Usuario();
        cliente.setIdUsuario(1);

        Usuario propietario = new Usuario();
        propietario.setIdUsuario(2);

        Auto auto = new Auto();
        auto.setIdAuto(10);
        auto.setActivo(true);
        auto.setPrecioDia(BigDecimal.valueOf(10000));
        auto.setPropietario(propietario);

        ReservaDto dto = new ReservaDto(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(4),
                10
        );

        when(usuarioService.obtenerUsuarioAutenticado()).thenReturn(cliente);
        when(autoRepository.findById(10)).thenReturn(Optional.of(auto));
        when(reservaRepository.existsByAutoIdAutoAndEstadoInAndFechaInicioBeforeAndFechaFinAfter(
                10,
                java.util.List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA),
                dto.getFechaFin(),
                dto.getFechaInicio()
        )).thenReturn(true);

        assertThrows(ParametroIncorrectoException.class, () -> reservaService.crearReservaAutenticada(dto));
    }
}

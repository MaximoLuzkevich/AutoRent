package com.AutoRent.Backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.AutoRent.Backend.dto.pago.PagoDto;
import com.AutoRent.Backend.dto.pago.PagoRespuestaDto;
import com.AutoRent.Backend.model.Pago;
import com.AutoRent.Backend.exception.ParametroIncorrectoException;
import com.AutoRent.Backend.exception.PermisoInsuficienteException;
import com.AutoRent.Backend.model.Reserva;
import com.AutoRent.Backend.model.Usuario;
import com.AutoRent.Backend.model.enums.EstadoReserva;
import com.AutoRent.Backend.model.enums.MetodoPago;
import com.AutoRent.Backend.repository.PagoRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private ReservaService reservaService;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private MercadoPagoService mercadoPagoService;

    @InjectMocks
    private PagoService pagoService;

    @Test
    void registrarPagoConMontoDistintoAlTotalLanzaParametroIncorrecto() {
        Reserva reserva = new Reserva();
        Usuario cliente = new Usuario();
        cliente.setIdUsuario(1);

        reserva.setIdReserva(1);
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setPrecioTotal(BigDecimal.valueOf(40000));
        reserva.setCliente(cliente);

        PagoDto dto = new PagoDto(BigDecimal.valueOf(30000), MetodoPago.TARJETA, 1);

        when(reservaService.obtenerReservaPorId(1)).thenReturn(reserva);
        when(usuarioService.obtenerUsuarioAutenticado()).thenReturn(cliente);

        assertThrows(ParametroIncorrectoException.class, () -> pagoService.registrarPago(dto));
    }

    @Test
    void registrarPagoDeReservaAjenaLanzaPermisoInsuficiente() {
        Usuario clienteReserva = new Usuario();
        clienteReserva.setIdUsuario(1);

        Usuario otroCliente = new Usuario();
        otroCliente.setIdUsuario(2);

        Reserva reserva = new Reserva();
        reserva.setIdReserva(1);
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setPrecioTotal(BigDecimal.valueOf(40000));
        reserva.setCliente(clienteReserva);

        PagoDto dto = new PagoDto(BigDecimal.valueOf(40000), MetodoPago.TARJETA, 1);

        when(reservaService.obtenerReservaPorId(1)).thenReturn(reserva);
        when(usuarioService.obtenerUsuarioAutenticado()).thenReturn(otroCliente);

        assertThrows(PermisoInsuficienteException.class, () -> pagoService.registrarPago(dto));
    }

    @Test
    void registrarPagoConTarjetaSinDatosLanzaParametroIncorrecto() {
        Usuario cliente = new Usuario();
        cliente.setIdUsuario(1);

        Reserva reserva = new Reserva();
        reserva.setIdReserva(1);
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setPrecioTotal(BigDecimal.valueOf(40000));
        reserva.setCliente(cliente);

        PagoDto dto = new PagoDto(BigDecimal.valueOf(40000), MetodoPago.TARJETA, 1);

        when(reservaService.obtenerReservaPorId(1)).thenReturn(reserva);
        when(usuarioService.obtenerUsuarioAutenticado()).thenReturn(cliente);

        assertThrows(ParametroIncorrectoException.class, () -> pagoService.registrarPago(dto));
    }

    @Test
    void registrarPagoConMercadoPagoDevuelveLinkDePreferencia() {
        Usuario cliente = new Usuario();
        cliente.setIdUsuario(1);

        Reserva reserva = new Reserva();
        reserva.setIdReserva(1);
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setPrecioTotal(BigDecimal.valueOf(40000));
        reserva.setCliente(cliente);

        PagoDto dto = new PagoDto(BigDecimal.valueOf(40000), MetodoPago.MERCADO_PAGO, 1);

        when(reservaService.obtenerReservaPorId(1)).thenReturn(reserva);
        when(usuarioService.obtenerUsuarioAutenticado()).thenReturn(cliente);
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> {
            Pago pago = invocation.getArgument(0);
            pago.setIdPago(9);
            return pago;
        });
        when(mercadoPagoService.crearPreferencia(any(Pago.class))).thenReturn("https://mp.test/pagar/9");

        PagoRespuestaDto respuesta = pagoService.registrarPago(dto);

        assertEquals("https://mp.test/pagar/9", respuesta.getLinkPago());
    }

    @Test
    void aprobarPagoSinRolAdministradorLanzaPermisoInsuficiente() {
        Usuario cliente = new Usuario();
        cliente.setIdUsuario(1);

        when(usuarioService.obtenerUsuarioAutenticado()).thenReturn(cliente);

        assertThrows(PermisoInsuficienteException.class, () -> pagoService.aprobarPago(1));
    }

    @Test
    void listarPagosPorPropietarioAjenoLanzaPermisoInsuficiente() {
        doThrow(new PermisoInsuficienteException("No podes consultar pagos de otro propietario"))
                .when(usuarioService)
                .validarUsuarioActualOAdministrador(2, "No podes consultar pagos de otro propietario");

        assertThrows(PermisoInsuficienteException.class, () -> pagoService.listarPagosPorPropietario(2));
    }
}

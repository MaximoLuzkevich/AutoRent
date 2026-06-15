package com.AutoRent.Backend.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.AutoRent.Backend.dto.auto.AutoDto;
import com.AutoRent.Backend.exception.DatoDuplicadoException;
import com.AutoRent.Backend.exception.ParametroIncorrectoException;
import com.AutoRent.Backend.exception.PermisoInsuficienteException;
import com.AutoRent.Backend.model.Auto;
import com.AutoRent.Backend.model.Usuario;
import com.AutoRent.Backend.model.enums.NombreCategoriaAuto;
import com.AutoRent.Backend.model.enums.NombreRol;
import com.AutoRent.Backend.model.enums.TipoCombustible;
import com.AutoRent.Backend.model.enums.TipoTransmision;
import com.AutoRent.Backend.repository.AutoRepository;
import com.AutoRent.Backend.repository.CategoriaAutoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AutoServiceTest {

    @Mock
    private AutoRepository autoRepository;

    @Mock
    private CategoriaAutoRepository categoriaAutoRepository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private AutoService autoService;

    @Test
    void crearAutoConPatenteDuplicadaLanzaDatoDuplicado() {
        Usuario propietario = new Usuario();
        propietario.setIdUsuario(1);

        AutoDto dto = crearAutoDto("ABC123");

        when(usuarioService.obtenerUsuarioAutenticado()).thenReturn(propietario);
        when(usuarioService.tieneRol(propietario, NombreRol.PROPIETARIO)).thenReturn(true);
        when(autoRepository.existsByPatenteIgnoreCase("ABC123")).thenReturn(true);

        assertThrows(DatoDuplicadoException.class, () -> autoService.crearAutoAutenticado(dto));
    }

    @Test
    void modificarAutoAjenoLanzaPermisoInsuficiente() {
        Usuario propietario = new Usuario();
        propietario.setIdUsuario(1);

        Usuario otroUsuario = new Usuario();
        otroUsuario.setIdUsuario(2);

        Auto auto = new Auto();
        auto.setIdAuto(10);
        auto.setPropietario(propietario);

        when(autoRepository.findById(10)).thenReturn(Optional.of(auto));
        when(usuarioService.obtenerUsuarioAutenticado()).thenReturn(otroUsuario);

        assertThrows(
                PermisoInsuficienteException.class,
                () -> autoService.modificarAutoAutenticado(10, crearAutoDto("ABC123"))
        );
    }

    @Test
    void buscarDisponiblesConFechaFinAnteriorLanzaParametroIncorrecto() {
        LocalDate fechaInicio = LocalDate.now().plusDays(5);
        LocalDate fechaFin = LocalDate.now().plusDays(3);

        assertThrows(
                ParametroIncorrectoException.class,
                () -> autoService.buscarDisponibles(
                        "Cordoba",
                        fechaInicio,
                        fechaFin,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                )
        );
    }

    private AutoDto crearAutoDto(String patente) {
        return new AutoDto(
                "Toyota",
                "Corolla",
                2022,
                patente,
                "Blanco",
                5,
                4,
                TipoTransmision.AUTOMATICA,
                TipoCombustible.NAFTA,
                BigDecimal.valueOf(30000),
                "Auto de prueba",
                "Cordoba",
                "Cordoba",
                "Av Siempre Viva 123",
                NombreCategoriaAuto.ECONOMICO
        );
    }
}

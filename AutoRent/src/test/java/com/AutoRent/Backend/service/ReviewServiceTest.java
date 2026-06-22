package com.AutoRent.Backend.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.AutoRent.Backend.dto.review.ReviewDto;
import com.AutoRent.Backend.exception.ParametroIncorrectoException;
import com.AutoRent.Backend.exception.PermisoInsuficienteException;
import com.AutoRent.Backend.model.Auto;
import com.AutoRent.Backend.model.Review;
import com.AutoRent.Backend.model.Usuario;
import com.AutoRent.Backend.model.enums.EstadoReserva;
import com.AutoRent.Backend.model.enums.NombreRol;
import com.AutoRent.Backend.repository.AutoRepository;
import com.AutoRent.Backend.repository.ReservaRepository;
import com.AutoRent.Backend.repository.ReviewRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private AutoRepository autoRepository;

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void crearReviewSinReservaFinalizadaLanzaParametroIncorrecto() {
        Usuario cliente = crearUsuario(1);

        Auto auto = new Auto();
        auto.setIdAuto(10);

        ReviewDto dto = new ReviewDto(5, "Muy bueno", 10);

        when(usuarioService.obtenerUsuarioAutenticado()).thenReturn(cliente);
        when(reviewRepository.existsByClienteIdUsuarioAndAutoIdAuto(1, 10)).thenReturn(false);
        when(autoRepository.findById(10)).thenReturn(Optional.of(auto));
        when(reservaRepository.existsByClienteIdUsuarioAndAutoIdAutoAndEstado(1, 10, EstadoReserva.FINALIZADA))
                .thenReturn(false);

        assertThrows(ParametroIncorrectoException.class, () -> reviewService.crearReviewAutenticada(dto));
    }

    @Test
    void eliminarReviewDeOtroUsuarioLanzaPermisoInsuficiente() {
        Usuario autor = crearUsuario(1);
        Usuario otroUsuario = crearUsuario(2);

        Review review = new Review();
        review.setIdReview(20);
        review.setCliente(autor);

        when(reviewRepository.findById(20)).thenReturn(Optional.of(review));
        when(usuarioService.obtenerUsuarioAutenticado()).thenReturn(otroUsuario);
        when(usuarioService.tieneRol(otroUsuario, NombreRol.ADMINISTRADOR)).thenReturn(false);

        assertThrows(PermisoInsuficienteException.class, () -> reviewService.eliminarReview(20));
    }

    private Usuario crearUsuario(Integer idUsuario) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        return usuario;
    }
}

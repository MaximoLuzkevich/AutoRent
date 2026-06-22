package com.AutoRent.Backend.service;

import lombok.RequiredArgsConstructor;

import com.AutoRent.Backend.dto.review.ReviewDto;
import com.AutoRent.Backend.dto.review.ReviewRespuestaDto;
import com.AutoRent.Backend.exception.DatoDuplicadoException;
import com.AutoRent.Backend.exception.IdNoEncontradoException;
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
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AutoRepository autoRepository;
    private final ReservaRepository reservaRepository;
    private final UsuarioService usuarioService;

    public ReviewRespuestaDto crearReviewAutenticada(ReviewDto dto) {
        Usuario cliente = usuarioService.obtenerUsuarioAutenticado();
        return crearReviewParaCliente(cliente, dto);
    }

    public ReviewRespuestaDto crearReview(Integer idCliente, ReviewDto dto) {
        usuarioService.validarUsuarioActualOAdministrador(
                idCliente,
                "No podes crear reviews para otro usuario"
        );

        Usuario cliente = usuarioService.obtenerUsuarioPorId(idCliente);
        return crearReviewParaCliente(cliente, dto);
    }

    private ReviewRespuestaDto crearReviewParaCliente(Usuario cliente, ReviewDto dto) {
        Integer idCliente = cliente.getIdUsuario();

        if (reviewRepository.existsByClienteIdUsuarioAndAutoIdAuto(idCliente, dto.getIdAuto())) {
            throw new DatoDuplicadoException("Ya hiciste una review para este auto");
        }

        Auto auto = autoRepository.findById(dto.getIdAuto())
                .orElseThrow(() -> new IdNoEncontradoException("Auto no encontrado"));

        if (!reservaRepository.existsByClienteIdUsuarioAndAutoIdAutoAndEstado(
                idCliente,
                dto.getIdAuto(),
                EstadoReserva.FINALIZADA
        )) {
            throw new ParametroIncorrectoException("Solo podes hacer review de autos con reservas finalizadas");
        }

        Review review = new Review();
        review.setPuntuacion(dto.getPuntuacion());
        review.setComentario(dto.getComentario());
        review.setCliente(cliente);
        review.setAuto(auto);

        Review reviewGuardada = reviewRepository.save(review);
        return convertirARespuesta(reviewGuardada);
    }

    public List<ReviewRespuestaDto> listarReviews() {
        return reviewRepository.findAllByOrderByFechaDesc().stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<ReviewRespuestaDto> listarReviewsPorAuto(Integer idAuto) {
        if (!autoRepository.existsById(idAuto)) {
            throw new IdNoEncontradoException("Auto no encontrado");
        }

        return reviewRepository.findByAutoIdAutoOrderByFechaDesc(idAuto).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public ReviewRespuestaDto buscarPorId(Integer idReview) {
        Review review = obtenerReviewPorId(idReview);
        return convertirARespuesta(review);
    }

    public void eliminarReview(Integer idReview) {
        Review review = obtenerReviewPorId(idReview);
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();

        if (!review.getCliente().getIdUsuario().equals(usuario.getIdUsuario())
                && !usuarioService.tieneRol(usuario, NombreRol.ADMINISTRADOR)) {
            throw new PermisoInsuficienteException("No podes eliminar esta review");
        }

        reviewRepository.delete(review);
    }

    private Review obtenerReviewPorId(Integer idReview) {
        return reviewRepository.findById(idReview)
                .orElseThrow(() -> new IdNoEncontradoException("Review no encontrada"));
    }

    private ReviewRespuestaDto convertirARespuesta(Review review) {
        return new ReviewRespuestaDto(
                review.getIdReview(),
                review.getPuntuacion(),
                review.getComentario(),
                review.getFecha(),
                review.getCliente().getIdUsuario(),
                review.getCliente().getNombre(),
                review.getAuto().getIdAuto()
        );
    }
}

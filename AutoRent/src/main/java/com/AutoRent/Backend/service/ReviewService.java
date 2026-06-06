package com.AutoRent.Backend.service;

import com.AutoRent.Backend.dto.review.ReviewDto;
import com.AutoRent.Backend.dto.review.ReviewRespuestaDto;
import com.AutoRent.Backend.exception.DatoDuplicadoException;
import com.AutoRent.Backend.exception.IdNoEncontradoException;
import com.AutoRent.Backend.model.Auto;
import com.AutoRent.Backend.model.Review;
import com.AutoRent.Backend.model.Usuario;
import com.AutoRent.Backend.repository.AutoRepository;
import com.AutoRent.Backend.repository.ReviewRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AutoRepository autoRepository;
    private final UsuarioService usuarioService;

    public ReviewService(
            ReviewRepository reviewRepository,
            AutoRepository autoRepository,
            UsuarioService usuarioService
    ) {
        this.reviewRepository = reviewRepository;
        this.autoRepository = autoRepository;
        this.usuarioService = usuarioService;
    }

    public ReviewRespuestaDto crearReview(Integer idCliente, ReviewDto dto) {
        if (reviewRepository.existsByClienteIdUsuarioAndAutoIdAuto(idCliente, dto.getIdAuto())) {
            throw new DatoDuplicadoException("Ya hiciste una review para este auto");
        }

        Usuario cliente = usuarioService.obtenerUsuarioPorId(idCliente);
        Auto auto = autoRepository.findById(dto.getIdAuto())
                .orElseThrow(() -> new IdNoEncontradoException("Auto no encontrado"));

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

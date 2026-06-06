package com.AutoRent.Backend.controller;

import com.AutoRent.Backend.dto.review.ReviewDto;
import com.AutoRent.Backend.dto.review.ReviewRespuestaDto;
import com.AutoRent.Backend.service.ReviewService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewRespuestaDto> crearReview(
            @RequestParam Integer idCliente,
            @Valid @RequestBody ReviewDto dto
    ) {
        return ResponseEntity.ok(reviewService.crearReview(idCliente, dto));
    }

    @GetMapping
    public ResponseEntity<List<ReviewRespuestaDto>> listarReviews() {
        return ResponseEntity.ok(reviewService.listarReviews());
    }

    @GetMapping("/{idReview}")
    public ResponseEntity<ReviewRespuestaDto> buscarPorId(@PathVariable Integer idReview) {
        return ResponseEntity.ok(reviewService.buscarPorId(idReview));
    }

    @GetMapping("/auto/{idAuto}")
    public ResponseEntity<List<ReviewRespuestaDto>> listarReviewsPorAuto(@PathVariable Integer idAuto) {
        return ResponseEntity.ok(reviewService.listarReviewsPorAuto(idAuto));
    }

    @DeleteMapping("/{idReview}")
    public ResponseEntity<Void> eliminarReview(@PathVariable Integer idReview) {
        reviewService.eliminarReview(idReview);
        return ResponseEntity.noContent().build();
    }
}

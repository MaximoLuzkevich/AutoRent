package com.AutoRent.Backend.controller;

import lombok.RequiredArgsConstructor;

import com.AutoRent.Backend.dto.imagenauto.ImagenAutoDto;
import com.AutoRent.Backend.dto.imagenauto.ImagenAutoRespuestaDto;
import com.AutoRent.Backend.service.ImagenAutoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/autos/{idAuto}/imagenes")
@Tag(name = "Imagenes de autos")
@RequiredArgsConstructor
public class ImagenAutoController {

    private final ImagenAutoService imagenAutoService;


    @PostMapping
    public ResponseEntity<ImagenAutoRespuestaDto> agregarImagen(
            @PathVariable Integer idAuto,
            @Valid @RequestBody ImagenAutoDto dto
    ) {
        return ResponseEntity.ok(imagenAutoService.agregarImagen(idAuto, dto));
    }

    @GetMapping
    public ResponseEntity<List<ImagenAutoRespuestaDto>> listarImagenesPorAuto(@PathVariable Integer idAuto) {
        return ResponseEntity.ok(imagenAutoService.listarImagenesPorAuto(idAuto));
    }

    @GetMapping("/principal")
    public ResponseEntity<ImagenAutoRespuestaDto> obtenerImagenPrincipal(@PathVariable Integer idAuto) {
        return ResponseEntity.ok(imagenAutoService.obtenerImagenPrincipal(idAuto));
    }

    @DeleteMapping("/{idImagen}")
    public ResponseEntity<Void> eliminarImagen(@PathVariable Integer idAuto, @PathVariable Integer idImagen) {
        imagenAutoService.eliminarImagen(idAuto, idImagen);
        return ResponseEntity.noContent().build();
    }
}

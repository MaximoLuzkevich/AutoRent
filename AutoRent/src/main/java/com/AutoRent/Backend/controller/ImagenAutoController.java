package com.AutoRent.Backend.controller;

import lombok.RequiredArgsConstructor;

import com.AutoRent.Backend.dto.imagenauto.ImagenAutoDto;
import com.AutoRent.Backend.dto.imagenauto.ImagenAutoRespuestaDto;
import com.AutoRent.Backend.service.ImagenAutoService;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/autos/{idAuto}/imagenes")
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

    @PostMapping("/upload")
    public ResponseEntity<ImagenAutoRespuestaDto> subirImagen(
            @PathVariable Integer idAuto,
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "false") Boolean principal
    ) {
        return ResponseEntity.ok(imagenAutoService.subirImagen(idAuto, file, principal));
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

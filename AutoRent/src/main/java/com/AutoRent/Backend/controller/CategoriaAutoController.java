package com.AutoRent.Backend.controller;

import lombok.RequiredArgsConstructor;

import com.AutoRent.Backend.dto.categoria.CategoriaAutoRespuestaDto;
import com.AutoRent.Backend.model.enums.NombreCategoriaAuto;
import com.AutoRent.Backend.service.CategoriaAutoService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaAutoController {

    private final CategoriaAutoService categoriaAutoService;


    @GetMapping
    public ResponseEntity<List<CategoriaAutoRespuestaDto>> listarCategorias() {
        return ResponseEntity.ok(categoriaAutoService.listarCategorias());
    }

    @GetMapping("/{nombre}")
    public ResponseEntity<CategoriaAutoRespuestaDto> buscarPorNombre(@PathVariable NombreCategoriaAuto nombre) {
        return ResponseEntity.ok(categoriaAutoService.buscarPorNombre(nombre));
    }
}

package com.AutoRent.Backend.controller;

import com.AutoRent.Backend.dto.lugar.LugarSugerenciaDto;
import com.AutoRent.Backend.service.GeoapifyService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lugares")
@RequiredArgsConstructor
public class LugarController {

    private final GeoapifyService geoapifyService;

    @GetMapping("/autocomplete")
    public ResponseEntity<List<LugarSugerenciaDto>> autocompletar(@RequestParam String texto) {
        return ResponseEntity.ok(geoapifyService.autocompletar(texto));
    }
}

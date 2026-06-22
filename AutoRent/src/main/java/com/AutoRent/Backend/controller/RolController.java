package com.AutoRent.Backend.controller;

import lombok.RequiredArgsConstructor;

import com.AutoRent.Backend.model.Rol;
import com.AutoRent.Backend.model.enums.NombreRol;
import com.AutoRent.Backend.service.RolService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController {

    private final RolService rolService;

    @GetMapping
    public ResponseEntity<List<Rol>> listarRoles() {
        return ResponseEntity.ok(rolService.listarRoles());
    }

    @GetMapping("/{nombreRol}")
    public ResponseEntity<Rol> buscarPorNombre(@PathVariable NombreRol nombreRol) {
        return ResponseEntity.ok(rolService.buscarPorNombre(nombreRol));
    }
}

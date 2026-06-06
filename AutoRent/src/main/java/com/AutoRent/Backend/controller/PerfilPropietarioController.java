package com.AutoRent.Backend.controller;

import com.AutoRent.Backend.dto.perfilpropietario.PerfilPropietarioDto;
import com.AutoRent.Backend.dto.perfilpropietario.PerfilPropietarioRespuestaDto;
import com.AutoRent.Backend.service.PerfilPropietarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/propietarios")
public class PerfilPropietarioController {

    private final PerfilPropietarioService perfilPropietarioService;

    public PerfilPropietarioController(PerfilPropietarioService perfilPropietarioService) {
        this.perfilPropietarioService = perfilPropietarioService;
    }

    @PostMapping("/{idUsuario}")
    public ResponseEntity<PerfilPropietarioRespuestaDto> crearPerfil(
            @PathVariable Integer idUsuario,
            @Valid @RequestBody PerfilPropietarioDto dto
    ) {
        return ResponseEntity.ok(perfilPropietarioService.crearPerfil(idUsuario, dto));
    }

    @PutMapping("/{idUsuario}")
    public ResponseEntity<PerfilPropietarioRespuestaDto> modificarPerfil(
            @PathVariable Integer idUsuario,
            @Valid @RequestBody PerfilPropietarioDto dto
    ) {
        return ResponseEntity.ok(perfilPropietarioService.modificarPerfil(idUsuario, dto));
    }

    @PutMapping("/{idUsuario}/verificar")
    public ResponseEntity<PerfilPropietarioRespuestaDto> verificarPropietario(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(perfilPropietarioService.verificarPropietario(idUsuario));
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<PerfilPropietarioRespuestaDto> buscarPorUsuario(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(perfilPropietarioService.buscarPorUsuario(idUsuario));
    }

    @GetMapping("/verificados")
    public ResponseEntity<List<PerfilPropietarioRespuestaDto>> listarPorVerificado(
            @RequestParam Boolean verificado
    ) {
        return ResponseEntity.ok(perfilPropietarioService.listarPorVerificado(verificado));
    }

    @GetMapping("/ciudad")
    public ResponseEntity<List<PerfilPropietarioRespuestaDto>> listarPorCiudad(@RequestParam String ciudad) {
        return ResponseEntity.ok(perfilPropietarioService.listarPorCiudad(ciudad));
    }

    @GetMapping("/provincia")
    public ResponseEntity<List<PerfilPropietarioRespuestaDto>> listarPorProvincia(@RequestParam String provincia) {
        return ResponseEntity.ok(perfilPropietarioService.listarPorProvincia(provincia));
    }

    @GetMapping("/nombre")
    public ResponseEntity<List<PerfilPropietarioRespuestaDto>> listarPorNombreUsuario(@RequestParam String nombre) {
        return ResponseEntity.ok(perfilPropietarioService.listarPorNombreUsuario(nombre));
    }
}

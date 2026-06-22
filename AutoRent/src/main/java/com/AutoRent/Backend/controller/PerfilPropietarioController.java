package com.AutoRent.Backend.controller;

import lombok.RequiredArgsConstructor;

import com.AutoRent.Backend.dto.perfilpropietario.PerfilPropietarioDto;
import com.AutoRent.Backend.dto.perfilpropietario.PerfilPropietarioRespuestaDto;
import com.AutoRent.Backend.service.PerfilPropietarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/propietarios")
@RequiredArgsConstructor
public class PerfilPropietarioController {

    private final PerfilPropietarioService perfilPropietarioService;

    @PostMapping("/me")
    public ResponseEntity<PerfilPropietarioRespuestaDto> crearMiPerfil(
            @Valid @RequestBody PerfilPropietarioDto dto
    ) {
        return ResponseEntity.ok(perfilPropietarioService.crearMiPerfil(dto));
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

    @PutMapping("/me")
    public ResponseEntity<PerfilPropietarioRespuestaDto> modificarMiPerfil(
            @Valid @RequestBody PerfilPropietarioDto dto
    ) {
        return ResponseEntity.ok(perfilPropietarioService.modificarMiPerfil(dto));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> desactivarMiPerfil() {
        perfilPropietarioService.desactivarMiPerfil();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<Void> desactivarPerfil(@PathVariable Integer idUsuario) {
        perfilPropietarioService.desactivarPerfil(idUsuario);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{idUsuario}/verificar")
    public ResponseEntity<PerfilPropietarioRespuestaDto> verificarPropietario(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(perfilPropietarioService.verificarPropietario(idUsuario));
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<PerfilPropietarioRespuestaDto> buscarPorUsuario(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(perfilPropietarioService.buscarPorUsuario(idUsuario));
    }

    @GetMapping("/me")
    public ResponseEntity<PerfilPropietarioRespuestaDto> buscarMiPerfil() {
        return ResponseEntity.ok(perfilPropietarioService.buscarMiPerfil());
    }

    @GetMapping("/verificados/{verificado}")
    public ResponseEntity<List<PerfilPropietarioRespuestaDto>> listarPorVerificado(
            @PathVariable Boolean verificado
    ) {
        return ResponseEntity.ok(perfilPropietarioService.listarPorVerificado(verificado));
    }

    @GetMapping("/activos/{activo}")
    public ResponseEntity<List<PerfilPropietarioRespuestaDto>> listarPorActivo(@PathVariable Boolean activo) {
        return ResponseEntity.ok(perfilPropietarioService.listarPorActivo(activo));
    }

    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<List<PerfilPropietarioRespuestaDto>> listarPorCiudad(@PathVariable String ciudad) {
        return ResponseEntity.ok(perfilPropietarioService.listarPorCiudad(ciudad));
    }

    @GetMapping("/provincia/{provincia}")
    public ResponseEntity<List<PerfilPropietarioRespuestaDto>> listarPorProvincia(@PathVariable String provincia) {
        return ResponseEntity.ok(perfilPropietarioService.listarPorProvincia(provincia));
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<List<PerfilPropietarioRespuestaDto>> listarPorNombreUsuario(@PathVariable String nombre) {
        return ResponseEntity.ok(perfilPropietarioService.listarPorNombreUsuario(nombre));
    }
}

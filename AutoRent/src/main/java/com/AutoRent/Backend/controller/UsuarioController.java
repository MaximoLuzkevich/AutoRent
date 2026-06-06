package com.AutoRent.Backend.controller;

import com.AutoRent.Backend.dto.usuario.LoginDto;
import com.AutoRent.Backend.dto.usuario.RegistroUsuarioDto;
import com.AutoRent.Backend.dto.usuario.UsuarioRespuestaDto;
import com.AutoRent.Backend.model.enums.NombreRol;
import com.AutoRent.Backend.service.UsuarioService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioRespuestaDto> registrarUsuario(@Valid @RequestBody RegistroUsuarioDto dto) {
        return ResponseEntity.ok(usuarioService.registrarUsuario(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioRespuestaDto> iniciarSesion(@Valid @RequestBody LoginDto dto) {
        return ResponseEntity.ok(usuarioService.iniciarSesion(dto));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioRespuestaDto>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<UsuarioRespuestaDto> buscarPorId(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(usuarioService.buscarPorId(idUsuario));
    }

    @GetMapping("/email")
    public ResponseEntity<UsuarioRespuestaDto> buscarPorEmail(@RequestParam String email) {
        return ResponseEntity.ok(usuarioService.buscarPorEmail(email));
    }

    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioRespuestaDto>> listarPorRol(@PathVariable NombreRol rol) {
        return ResponseEntity.ok(usuarioService.listarPorRol(rol));
    }

    @GetMapping("/rol/{rol}/estado")
    public ResponseEntity<List<UsuarioRespuestaDto>> listarPorRolYEstado(
            @PathVariable NombreRol rol,
            @RequestParam Boolean activo
    ) {
        return ResponseEntity.ok(usuarioService.listarPorRolYEstado(rol, activo));
    }

    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<Void> desactivarUsuario(@PathVariable Integer idUsuario) {
        usuarioService.desactivarUsuario(idUsuario);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{idUsuario}/roles/{rol}")
    public ResponseEntity<Void> agregarRol(@PathVariable Integer idUsuario, @PathVariable NombreRol rol) {
        usuarioService.agregarRol(idUsuario, rol);
        return ResponseEntity.noContent().build();
    }
}

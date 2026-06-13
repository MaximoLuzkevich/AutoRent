package com.AutoRent.Backend.controller;

import lombok.RequiredArgsConstructor;

import com.AutoRent.Backend.dto.usuario.AuthRespuestaDto;
import com.AutoRent.Backend.dto.usuario.LoginDto;
import com.AutoRent.Backend.dto.usuario.RegistroUsuarioDto;
import com.AutoRent.Backend.dto.usuario.UsuarioRespuestaDto;
import com.AutoRent.Backend.model.enums.NombreRol;
import com.AutoRent.Backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;


    @PostMapping("/registro")
    public ResponseEntity<UsuarioRespuestaDto> registrarUsuario(@Valid @RequestBody RegistroUsuarioDto dto) {
        return ResponseEntity.ok(usuarioService.registrarUsuario(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthRespuestaDto> iniciarSesion(@Valid @RequestBody LoginDto dto) {
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

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioRespuestaDto> buscarPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(usuarioService.buscarPorEmail(email));
    }

    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioRespuestaDto>> listarPorRol(@PathVariable NombreRol rol) {
        return ResponseEntity.ok(usuarioService.listarPorRol(rol));
    }

    @GetMapping("/rol/{rol}/estado/{activo}")
    public ResponseEntity<List<UsuarioRespuestaDto>> listarPorRolYEstado(
            @PathVariable NombreRol rol,
            @PathVariable Boolean activo
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

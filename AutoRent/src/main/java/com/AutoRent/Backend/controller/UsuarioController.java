package com.AutoRent.Backend.controller;

import lombok.RequiredArgsConstructor;

import com.AutoRent.Backend.dto.usuario.AuthRespuestaDto;
import com.AutoRent.Backend.dto.usuario.LoginDto;
import com.AutoRent.Backend.dto.usuario.RegistroUsuarioDto;
import com.AutoRent.Backend.dto.usuario.UsuarioRespuestaDto;
import com.AutoRent.Backend.model.enums.NombreRol;
import com.AutoRent.Backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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


    @Operation(
            summary = "Registrar usuario",
            description = "Crea un usuario nuevo con rol CLIENTE. No permite emails duplicados."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "409", description = "Email ya registrado")
    })
    @PostMapping("/registro")
    public ResponseEntity<UsuarioRespuestaDto> registrarUsuario(@Valid @RequestBody RegistroUsuarioDto dto) {
        return ResponseEntity.ok(usuarioService.registrarUsuario(dto));
    }

    @Operation(
            summary = "Iniciar sesion",
            description = "Valida email y password. Si las credenciales son correctas, devuelve un token JWT Bearer."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login correcto"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas o usuario inactivo")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthRespuestaDto> iniciarSesion(@Valid @RequestBody LoginDto dto) {
        return ResponseEntity.ok(usuarioService.iniciarSesion(dto));
    }

    @Operation(summary = "Listar usuarios", description = "Endpoint administrativo para consultar todos los usuarios.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuarios obtenidos correctamente"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido"),
            @ApiResponse(responseCode = "403", description = "Requiere rol ADMINISTRADOR")
    })
    @GetMapping
    public ResponseEntity<List<UsuarioRespuestaDto>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @Operation(summary = "Buscar usuario por ID", description = "Endpoint administrativo para consultar un usuario puntual.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "403", description = "Requiere rol ADMINISTRADOR"),
            @ApiResponse(responseCode = "404", description = "Usuario inexistente")
    })
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

    @Operation(summary = "Desactivar usuario", description = "Marca un usuario como inactivo sin borrarlo fisicamente.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario desactivado"),
            @ApiResponse(responseCode = "403", description = "Requiere rol ADMINISTRADOR"),
            @ApiResponse(responseCode = "404", description = "Usuario inexistente")
    })
    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<Void> desactivarUsuario(@PathVariable Integer idUsuario) {
        usuarioService.desactivarUsuario(idUsuario);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Agregar rol a usuario", description = "Permite que un administrador agregue CLIENTE, PROPIETARIO o ADMINISTRADOR a un usuario.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Rol agregado"),
            @ApiResponse(responseCode = "403", description = "Requiere rol ADMINISTRADOR"),
            @ApiResponse(responseCode = "404", description = "Usuario o rol inexistente")
    })
    @PutMapping("/{idUsuario}/roles/{rol}")
    public ResponseEntity<Void> agregarRol(@PathVariable Integer idUsuario, @PathVariable NombreRol rol) {
        usuarioService.agregarRol(idUsuario, rol);
        return ResponseEntity.noContent().build();
    }
}

package com.AutoRent.Backend.service;

import lombok.RequiredArgsConstructor;

import com.AutoRent.Backend.dto.usuario.AuthRespuestaDto;
import com.AutoRent.Backend.dto.usuario.LoginDto;
import com.AutoRent.Backend.dto.usuario.RegistroUsuarioDto;
import com.AutoRent.Backend.dto.usuario.UsuarioRespuestaDto;
import com.AutoRent.Backend.exception.DatoDuplicadoException;
import com.AutoRent.Backend.exception.IdNoEncontradoException;
import com.AutoRent.Backend.exception.LoginRequeridoException;
import com.AutoRent.Backend.exception.PermisoInsuficienteException;
import com.AutoRent.Backend.model.Rol;
import com.AutoRent.Backend.model.Usuario;
import com.AutoRent.Backend.model.enums.NombreRol;
import com.AutoRent.Backend.repository.UsuarioRepository;
import com.AutoRent.Backend.security.JwtService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolService rolService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    @Transactional
    public UsuarioRespuestaDto registrarUsuario(RegistroUsuarioDto dto) {
        if (usuarioRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new DatoDuplicadoException("El email ya esta registrado");
        }

        Rol rolCliente = rolService.buscarPorNombre(NombreRol.CLIENTE);

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setTelefono(dto.getTelefono());
        usuario.getRoles().add(rolCliente);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return convertirARespuesta(usuarioGuardado);
    }

    public AuthRespuestaDto iniciarSesion(LoginDto dto) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCaseConRoles(dto.getEmail())
                .orElseThrow(() -> new LoginRequeridoException("Credenciales incorrectas"));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new LoginRequeridoException("Usuario inactivo");
        }

        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            throw new LoginRequeridoException("Credenciales incorrectas");
        }

        return new AuthRespuestaDto(
                jwtService.generarToken(usuario),
                "Bearer",
                convertirARespuesta(usuario)
        );
    }

    public List<UsuarioRespuestaDto> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<UsuarioRespuestaDto> listarPorRol(NombreRol rol) {
        return usuarioRepository.findDistinctByRolesNombreOrderByFechaRegistroDesc(rol).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<UsuarioRespuestaDto> listarPorRolYEstado(NombreRol rol, Boolean activo) {
        return usuarioRepository.findDistinctByRolesNombreAndActivoOrderByFechaRegistroDesc(rol, activo).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public UsuarioRespuestaDto buscarPorId(Integer idUsuario) {
        Usuario usuario = obtenerUsuarioPorId(idUsuario);
        return convertirARespuesta(usuario);
    }

    public UsuarioRespuestaDto buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IdNoEncontradoException("Usuario no encontrado"));

        return convertirARespuesta(usuario);
    }

    public void desactivarUsuario(Integer idUsuario) {
        Usuario usuario = obtenerUsuarioPorId(idUsuario);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void agregarRol(Integer idUsuario, NombreRol nombreRol) {
        Usuario usuario = obtenerUsuarioPorIdConRoles(idUsuario);
        Rol rol = rolService.buscarPorNombre(nombreRol);

        usuario.getRoles().add(rol);
        usuarioRepository.save(usuario);
    }

    public Usuario obtenerUsuarioPorId(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IdNoEncontradoException("Usuario no encontrado"));
    }

    public Usuario obtenerUsuarioPorIdConRoles(Integer idUsuario) {
        return usuarioRepository.findByIdConRoles(idUsuario)
                .orElseThrow(() -> new IdNoEncontradoException("Usuario no encontrado"));
    }

    public Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new LoginRequeridoException("Usuario no autenticado");
        }

        String email = authentication.getName();
        return usuarioRepository.findByEmailIgnoreCaseConRoles(email)
                .orElseThrow(() -> new LoginRequeridoException("Usuario no autenticado"));
    }

    public void validarUsuarioActualOAdministrador(Integer idUsuario, String mensaje) {
        Usuario usuario = obtenerUsuarioAutenticado();

        if (!usuario.getIdUsuario().equals(idUsuario) && !tieneRol(usuario, NombreRol.ADMINISTRADOR)) {
            throw new PermisoInsuficienteException(mensaje);
        }
    }

    public boolean tieneRol(Usuario usuario, NombreRol nombreRol) {
        return usuario.getRoles().stream()
                .map(Rol::getNombre)
                .anyMatch(nombreRol::equals);
    }

    private UsuarioRespuestaDto convertirARespuesta(Usuario usuario) {
        Set<NombreRol> roles = usuario.getRoles().stream()
                .map(Rol::getNombre)
                .collect(Collectors.toSet());

        return new UsuarioRespuestaDto(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getFechaRegistro(),
                usuario.getActivo(),
                roles
        );
    }
}

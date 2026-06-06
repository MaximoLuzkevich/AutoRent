package com.AutoRent.Backend.service;

import com.AutoRent.Backend.dto.usuario.LoginDto;
import com.AutoRent.Backend.dto.usuario.RegistroUsuarioDto;
import com.AutoRent.Backend.dto.usuario.UsuarioRespuestaDto;
import com.AutoRent.Backend.exception.DatoDuplicadoException;
import com.AutoRent.Backend.exception.IdNoEncontradoException;
import com.AutoRent.Backend.exception.LoginRequeridoException;
import com.AutoRent.Backend.model.Rol;
import com.AutoRent.Backend.model.Usuario;
import com.AutoRent.Backend.model.enums.NombreRol;
import com.AutoRent.Backend.repository.UsuarioRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolService rolService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            RolService rolService,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolService = rolService;
        this.passwordEncoder = passwordEncoder;
    }

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

    public UsuarioRespuestaDto iniciarSesion(LoginDto dto) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(dto.getEmail())
                .orElseThrow(() -> new LoginRequeridoException("Credenciales incorrectas"));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new LoginRequeridoException("Usuario inactivo");
        }

        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            throw new LoginRequeridoException("Credenciales incorrectas");
        }

        return convertirARespuesta(usuario);
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

    public void agregarRol(Integer idUsuario, NombreRol nombreRol) {
        Usuario usuario = obtenerUsuarioPorId(idUsuario);
        Rol rol = rolService.buscarPorNombre(nombreRol);

        usuario.getRoles().add(rol);
        usuarioRepository.save(usuario);
    }

    public Usuario obtenerUsuarioPorId(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IdNoEncontradoException("Usuario no encontrado"));
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

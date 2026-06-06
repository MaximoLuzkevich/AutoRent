package com.AutoRent.Backend.service;

import com.AutoRent.Backend.dto.perfilpropietario.PerfilPropietarioDto;
import com.AutoRent.Backend.dto.perfilpropietario.PerfilPropietarioRespuestaDto;
import com.AutoRent.Backend.exception.DatoDuplicadoException;
import com.AutoRent.Backend.exception.IdNoEncontradoException;
import com.AutoRent.Backend.model.PerfilPropietario;
import com.AutoRent.Backend.model.Usuario;
import com.AutoRent.Backend.model.enums.NombreRol;
import com.AutoRent.Backend.repository.PerfilPropietarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PerfilPropietarioService {

    private final PerfilPropietarioRepository perfilPropietarioRepository;
    private final UsuarioService usuarioService;

    public PerfilPropietarioService(
            PerfilPropietarioRepository perfilPropietarioRepository,
            UsuarioService usuarioService
    ) {
        this.perfilPropietarioRepository = perfilPropietarioRepository;
        this.usuarioService = usuarioService;
    }

    public PerfilPropietarioRespuestaDto crearPerfil(Integer idUsuario, PerfilPropietarioDto dto) {
        if (perfilPropietarioRepository.existsById(idUsuario)) {
            throw new DatoDuplicadoException("El usuario ya tiene perfil de propietario");
        }

        Usuario usuario = usuarioService.obtenerUsuarioPorId(idUsuario);

        PerfilPropietario perfil = new PerfilPropietario();
        perfil.setUsuario(usuario);
        cargarDatosPerfil(perfil, dto);

        PerfilPropietario perfilGuardado = perfilPropietarioRepository.save(perfil);
        usuarioService.agregarRol(idUsuario, NombreRol.PROPIETARIO);

        return convertirARespuesta(perfilGuardado);
    }

    public PerfilPropietarioRespuestaDto modificarPerfil(Integer idUsuario, PerfilPropietarioDto dto) {
        PerfilPropietario perfil = obtenerPerfilPorUsuario(idUsuario);
        cargarDatosPerfil(perfil, dto);

        return convertirARespuesta(perfilPropietarioRepository.save(perfil));
    }

    public PerfilPropietarioRespuestaDto verificarPropietario(Integer idUsuario) {
        PerfilPropietario perfil = obtenerPerfilPorUsuario(idUsuario);
        perfil.setVerificado(true);

        return convertirARespuesta(perfilPropietarioRepository.save(perfil));
    }

    public PerfilPropietarioRespuestaDto buscarPorUsuario(Integer idUsuario) {
        return convertirARespuesta(obtenerPerfilPorUsuario(idUsuario));
    }

    public List<PerfilPropietarioRespuestaDto> listarPorVerificado(Boolean verificado) {
        return perfilPropietarioRepository.findByVerificadoOrderByFechaAltaDesc(verificado).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<PerfilPropietarioRespuestaDto> listarPorCiudad(String ciudad) {
        return perfilPropietarioRepository.findByCiudadContainingIgnoreCaseOrderByFechaAltaDesc(ciudad).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<PerfilPropietarioRespuestaDto> listarPorProvincia(String provincia) {
        return perfilPropietarioRepository.findByProvinciaContainingIgnoreCaseOrderByFechaAltaDesc(provincia).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<PerfilPropietarioRespuestaDto> listarPorNombreUsuario(String nombre) {
        return perfilPropietarioRepository.findByUsuarioNombreContainingIgnoreCaseOrderByFechaAltaDesc(nombre).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    private PerfilPropietario obtenerPerfilPorUsuario(Integer idUsuario) {
        return perfilPropietarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IdNoEncontradoException("Perfil de propietario no encontrado"));
    }

    private void cargarDatosPerfil(PerfilPropietario perfil, PerfilPropietarioDto dto) {
        perfil.setDni(dto.getDni());
        perfil.setCuit(dto.getCuit());
        perfil.setDireccion(dto.getDireccion());
        perfil.setCiudad(dto.getCiudad());
        perfil.setProvincia(dto.getProvincia());
    }

    private PerfilPropietarioRespuestaDto convertirARespuesta(PerfilPropietario perfil) {
        Usuario usuario = perfil.getUsuario();

        return new PerfilPropietarioRespuestaDto(
                perfil.getIdUsuario(),
                usuario.getNombre(),
                usuario.getEmail(),
                perfil.getDni(),
                perfil.getCuit(),
                perfil.getDireccion(),
                perfil.getCiudad(),
                perfil.getProvincia(),
                perfil.getFechaAlta(),
                perfil.getVerificado()
        );
    }
}

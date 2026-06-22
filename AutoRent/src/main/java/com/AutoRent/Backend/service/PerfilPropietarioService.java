package com.AutoRent.Backend.service;

import lombok.RequiredArgsConstructor;

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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PerfilPropietarioService {

    private final PerfilPropietarioRepository perfilPropietarioRepository;
    private final UsuarioService usuarioService;

    @Transactional
    public PerfilPropietarioRespuestaDto crearMiPerfil(PerfilPropietarioDto dto) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();
        return crearPerfilParaUsuario(usuario, dto);
    }

    @Transactional
    public PerfilPropietarioRespuestaDto crearPerfil(Integer idUsuario, PerfilPropietarioDto dto) {
        usuarioService.validarUsuarioActualOAdministrador(
                idUsuario,
                "No podes crear un perfil para otro usuario"
        );

        Usuario usuario = usuarioService.obtenerUsuarioPorId(idUsuario);
        return crearPerfilParaUsuario(usuario, dto);
    }

    private PerfilPropietarioRespuestaDto crearPerfilParaUsuario(Usuario usuario, PerfilPropietarioDto dto) {
        Integer idUsuario = usuario.getIdUsuario();

        if (perfilPropietarioRepository.existsById(idUsuario)) {
            PerfilPropietario perfilExistente = obtenerPerfilPorUsuario(idUsuario);
            if (Boolean.TRUE.equals(perfilExistente.getActivo())) {
                throw new DatoDuplicadoException("El usuario ya tiene perfil de propietario");
            }

            cargarDatosPerfil(perfilExistente, dto);
            perfilExistente.setActivo(true);
            usuarioService.agregarRol(idUsuario, NombreRol.PROPIETARIO);
            return convertirARespuesta(perfilPropietarioRepository.save(perfilExistente));
        }

        PerfilPropietario perfil = new PerfilPropietario();
        perfil.setUsuario(usuario);
        cargarDatosPerfil(perfil, dto);

        PerfilPropietario perfilGuardado = perfilPropietarioRepository.save(perfil);
        usuarioService.agregarRol(idUsuario, NombreRol.PROPIETARIO);

        return convertirARespuesta(perfilGuardado);
    }

    public PerfilPropietarioRespuestaDto modificarPerfil(Integer idUsuario, PerfilPropietarioDto dto) {
        usuarioService.validarUsuarioActualOAdministrador(
                idUsuario,
                "No podes modificar un perfil de otro usuario"
        );

        PerfilPropietario perfil = obtenerPerfilPorUsuario(idUsuario);
        cargarDatosPerfil(perfil, dto);

        return convertirARespuesta(perfilPropietarioRepository.save(perfil));
    }

    public PerfilPropietarioRespuestaDto modificarMiPerfil(PerfilPropietarioDto dto) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();
        return modificarPerfil(usuario.getIdUsuario(), dto);
    }

    public PerfilPropietarioRespuestaDto verificarPropietario(Integer idUsuario) {
        PerfilPropietario perfil = obtenerPerfilPorUsuario(idUsuario);
        perfil.setVerificado(true);

        return convertirARespuesta(perfilPropietarioRepository.save(perfil));
    }

    @Transactional
    public void desactivarMiPerfil() {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();
        desactivarPerfil(usuario.getIdUsuario());
    }

    @Transactional
    public void desactivarPerfil(Integer idUsuario) {
        usuarioService.validarUsuarioActualOAdministrador(
                idUsuario,
                "No podes dar de baja el perfil de otro usuario"
        );

        PerfilPropietario perfil = obtenerPerfilPorUsuario(idUsuario);
        perfil.setActivo(false);
        perfil.setVerificado(false);
        perfilPropietarioRepository.save(perfil);
        usuarioService.quitarRol(idUsuario, NombreRol.PROPIETARIO);
    }

    public PerfilPropietarioRespuestaDto buscarPorUsuario(Integer idUsuario) {
        usuarioService.validarUsuarioActualOAdministrador(
                idUsuario,
                "No podes consultar el perfil de otro usuario"
        );

        return convertirARespuesta(obtenerPerfilPorUsuario(idUsuario));
    }

    public PerfilPropietarioRespuestaDto buscarMiPerfil() {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();
        return convertirARespuesta(obtenerPerfilPorUsuario(usuario.getIdUsuario()));
    }

    public List<PerfilPropietarioRespuestaDto> listarPorVerificado(Boolean verificado) {
        return perfilPropietarioRepository.findByVerificadoOrderByFechaAltaDesc(verificado).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<PerfilPropietarioRespuestaDto> listarPorActivo(Boolean activo) {
        return perfilPropietarioRepository.findByActivoOrderByFechaAltaDesc(activo).stream()
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
                perfil.getVerificado(),
                perfil.getActivo()
        );
    }
}

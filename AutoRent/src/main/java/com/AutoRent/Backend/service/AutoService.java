package com.AutoRent.Backend.service;

import com.AutoRent.Backend.dto.auto.AutoDto;
import com.AutoRent.Backend.dto.auto.AutoRespuestaDto;
import com.AutoRent.Backend.exception.DatoDuplicadoException;
import com.AutoRent.Backend.exception.IdNoEncontradoException;
import com.AutoRent.Backend.exception.PermisoInsuficienteException;
import com.AutoRent.Backend.model.Auto;
import com.AutoRent.Backend.model.CategoriaAuto;
import com.AutoRent.Backend.model.Rol;
import com.AutoRent.Backend.model.Usuario;
import com.AutoRent.Backend.model.enums.NombreCategoriaAuto;
import com.AutoRent.Backend.model.enums.NombreRol;
import com.AutoRent.Backend.repository.AutoRepository;
import com.AutoRent.Backend.repository.CategoriaAutoRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AutoService {

    private final AutoRepository autoRepository;
    private final CategoriaAutoRepository categoriaAutoRepository;
    private final UsuarioService usuarioService;

    public AutoService(
            AutoRepository autoRepository,
            CategoriaAutoRepository categoriaAutoRepository,
            UsuarioService usuarioService
    ) {
        this.autoRepository = autoRepository;
        this.categoriaAutoRepository = categoriaAutoRepository;
        this.usuarioService = usuarioService;
    }

    public AutoRespuestaDto crearAuto(Integer idPropietario, AutoDto dto) {
        Usuario propietario = usuarioService.obtenerUsuarioPorId(idPropietario);
        validarPermisoParaPublicar(propietario);

        if (autoRepository.existsByPatenteIgnoreCase(dto.getPatente())) {
            throw new DatoDuplicadoException("La patente ya esta registrada");
        }

        CategoriaAuto categoria = buscarCategoria(dto.getCategoria());

        Auto auto = new Auto();
        cargarDatosAuto(auto, dto);
        auto.setPropietario(propietario);
        auto.setCategoria(categoria);

        Auto autoGuardado = autoRepository.save(auto);
        return convertirARespuesta(autoGuardado);
    }

    public List<AutoRespuestaDto> listarAutosActivos() {
        return autoRepository.findByActivoTrueOrderByFechaPublicacionDesc().stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<AutoRespuestaDto> listarAutosPorPropietario(Integer idPropietario) {
        usuarioService.obtenerUsuarioPorId(idPropietario);

        return autoRepository.findByPropietarioIdUsuarioOrderByFechaPublicacionDesc(idPropietario).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<AutoRespuestaDto> listarPorCategoria(NombreCategoriaAuto categoria) {
        return autoRepository.findByCategoriaNombreAndActivoTrue(categoria).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<AutoRespuestaDto> listarPorMarca(String marca) {
        return autoRepository.findByMarcaContainingIgnoreCaseAndActivoTrue(marca).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<AutoRespuestaDto> listarPorCiudad(String ciudad) {
        return autoRepository.findByCiudadContainingIgnoreCaseAndActivoTrue(ciudad).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public AutoRespuestaDto buscarPorId(Integer idAuto) {
        Auto auto = obtenerAutoPorId(idAuto);
        return convertirARespuesta(auto);
    }

    public AutoRespuestaDto modificarAuto(Integer idAuto, Integer idPropietario, AutoDto dto) {
        Auto auto = obtenerAutoPorId(idAuto);
        validarPropietario(auto, idPropietario);

        if (!auto.getPatente().equalsIgnoreCase(dto.getPatente())
                && autoRepository.existsByPatenteIgnoreCase(dto.getPatente())) {
            throw new DatoDuplicadoException("La patente ya esta registrada");
        }

        CategoriaAuto categoria = buscarCategoria(dto.getCategoria());
        cargarDatosAuto(auto, dto);
        auto.setCategoria(categoria);

        return convertirARespuesta(autoRepository.save(auto));
    }

    public void desactivarAuto(Integer idAuto, Integer idPropietario) {
        Auto auto = obtenerAutoPorId(idAuto);
        validarPropietario(auto, idPropietario);

        auto.setActivo(false);
        autoRepository.save(auto);
    }

    public Auto obtenerAutoPorId(Integer idAuto) {
        return autoRepository.findById(idAuto)
                .orElseThrow(() -> new IdNoEncontradoException("Auto no encontrado"));
    }

    private CategoriaAuto buscarCategoria(NombreCategoriaAuto nombreCategoria) {
        return categoriaAutoRepository.findByNombre(nombreCategoria)
                .orElseThrow(() -> new IdNoEncontradoException("Categoria no encontrada"));
    }

    private void cargarDatosAuto(Auto auto, AutoDto dto) {
        auto.setMarca(dto.getMarca());
        auto.setModelo(dto.getModelo());
        auto.setAnio(dto.getAnio());
        auto.setPatente(dto.getPatente());
        auto.setColor(dto.getColor());
        auto.setCapacidadPasajeros(dto.getCapacidadPasajeros());
        auto.setCantidadPuertas(dto.getCantidadPuertas());
        auto.setTransmision(dto.getTransmision());
        auto.setCombustible(dto.getCombustible());
        auto.setPrecioDia(dto.getPrecioDia());
        auto.setDescripcion(dto.getDescripcion());
        auto.setCiudad(dto.getCiudad());
        auto.setProvincia(dto.getProvincia());
        auto.setDireccionRetiro(dto.getDireccionRetiro());
    }

    private void validarPermisoParaPublicar(Usuario usuario) {
        if (!tieneRol(usuario, NombreRol.PROPIETARIO) && !tieneRol(usuario, NombreRol.ADMINISTRADOR)) {
            throw new PermisoInsuficienteException("El usuario no puede publicar autos");
        }
    }

    private void validarPropietario(Auto auto, Integer idUsuario) {
        if (!auto.getPropietario().getIdUsuario().equals(idUsuario)) {
            throw new PermisoInsuficienteException("No podes modificar este auto");
        }
    }

    private boolean tieneRol(Usuario usuario, NombreRol nombreRol) {
        return usuario.getRoles().stream()
                .map(Rol::getNombre)
                .anyMatch(nombreRol::equals);
    }

    private AutoRespuestaDto convertirARespuesta(Auto auto) {
        return new AutoRespuestaDto(
                auto.getIdAuto(),
                auto.getMarca(),
                auto.getModelo(),
                auto.getAnio(),
                auto.getPatente(),
                auto.getColor(),
                auto.getCapacidadPasajeros(),
                auto.getCantidadPuertas(),
                auto.getTransmision(),
                auto.getCombustible(),
                auto.getPrecioDia(),
                auto.getDescripcion(),
                auto.getCiudad(),
                auto.getProvincia(),
                auto.getDireccionRetiro(),
                auto.getActivo(),
                auto.getFechaPublicacion(),
                auto.getPropietario().getIdUsuario(),
                auto.getPropietario().getNombre(),
                auto.getCategoria().getNombre()
        );
    }
}

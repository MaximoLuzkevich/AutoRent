package com.AutoRent.Backend.service;

import lombok.RequiredArgsConstructor;

import com.AutoRent.Backend.dto.auto.AutoDto;
import com.AutoRent.Backend.dto.auto.AutoRespuestaDto;
import com.AutoRent.Backend.exception.DatoDuplicadoException;
import com.AutoRent.Backend.exception.IdNoEncontradoException;
import com.AutoRent.Backend.exception.ParametroIncorrectoException;
import com.AutoRent.Backend.exception.PermisoInsuficienteException;
import com.AutoRent.Backend.model.Auto;
import com.AutoRent.Backend.model.CategoriaAuto;
import com.AutoRent.Backend.model.PerfilPropietario;
import com.AutoRent.Backend.model.Usuario;
import com.AutoRent.Backend.model.enums.EstadoReserva;
import com.AutoRent.Backend.model.enums.NombreCategoriaAuto;
import com.AutoRent.Backend.model.enums.NombreRol;
import com.AutoRent.Backend.model.enums.TipoCombustible;
import com.AutoRent.Backend.model.enums.TipoTransmision;
import com.AutoRent.Backend.repository.AutoRepository;
import com.AutoRent.Backend.repository.CategoriaAutoRepository;
import com.AutoRent.Backend.repository.PerfilPropietarioRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutoService {

    private final AutoRepository autoRepository;
    private final CategoriaAutoRepository categoriaAutoRepository;
    private final PerfilPropietarioRepository perfilPropietarioRepository;
    private final UsuarioService usuarioService;

    public AutoRespuestaDto crearAutoAutenticado(AutoDto dto) {
        Usuario propietario = usuarioService.obtenerUsuarioAutenticado();
        return crearAutoParaPropietario(propietario, dto);
    }

    public AutoRespuestaDto crearAuto(Integer idPropietario, AutoDto dto) {
        usuarioService.validarUsuarioActualOAdministrador(
                idPropietario,
                "No podes publicar autos para otro usuario"
        );

        Usuario propietario = usuarioService.obtenerUsuarioPorIdConRoles(idPropietario);
        return crearAutoParaPropietario(propietario, dto);
    }

    private AutoRespuestaDto crearAutoParaPropietario(Usuario propietario, AutoDto dto) {
        validarPermisoParaPublicar(propietario);
        validarUbicacionDelPropietario(propietario, dto);

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

    public List<AutoRespuestaDto> listarMisAutos() {
        Usuario propietario = usuarioService.obtenerUsuarioAutenticado();

        return autoRepository.findByPropietarioIdUsuarioOrderByFechaPublicacionDesc(propietario.getIdUsuario())
                .stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<AutoRespuestaDto> listarMisAutosPorEstado(Boolean activo) {
        Usuario propietario = usuarioService.obtenerUsuarioAutenticado();

        return autoRepository.findByPropietarioIdUsuarioAndActivoOrderByFechaPublicacionDesc(
                        propietario.getIdUsuario(),
                        activo
                ).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<AutoRespuestaDto> listarMisAutosPorCategoria(NombreCategoriaAuto categoria) {
        Usuario propietario = usuarioService.obtenerUsuarioAutenticado();

        return autoRepository.findByPropietarioIdUsuarioAndCategoriaNombreOrderByFechaPublicacionDesc(
                        propietario.getIdUsuario(),
                        categoria
                ).stream()
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

    public List<AutoRespuestaDto> buscarConFiltros(
            String ciudad,
            String marca,
            NombreCategoriaAuto categoria,
            BigDecimal precioMax,
            Integer pasajeros,
            TipoTransmision transmision,
            TipoCombustible combustible
    ) {
        return autoRepository.buscarConFiltros(
                        normalizarTexto(ciudad),
                        normalizarTexto(marca),
                        categoria,
                        precioMax,
                        pasajeros,
                        transmision,
                        combustible
                ).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public List<AutoRespuestaDto> buscarDisponibles(
            String ciudad,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            String marca,
            NombreCategoriaAuto categoria,
            BigDecimal precioMax,
            Integer pasajeros,
            TipoTransmision transmision,
            TipoCombustible combustible
    ) {
        validarBusquedaDisponibles(ciudad, fechaInicio, fechaFin);

        return autoRepository.buscarDisponibles(
                        ciudad.trim(),
                        fechaInicio,
                        fechaFin,
                        List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA),
                        normalizarTexto(marca),
                        categoria,
                        precioMax,
                        pasajeros,
                        transmision,
                        combustible
                ).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public AutoRespuestaDto buscarPorId(Integer idAuto) {
        Auto auto = obtenerAutoPorId(idAuto);
        return convertirARespuesta(auto);
    }

    public AutoRespuestaDto modificarAuto(Integer idAuto, Integer idPropietario, AutoDto dto) {
        Auto auto = obtenerAutoPorId(idAuto);
        usuarioService.validarUsuarioActualOAdministrador(
                idPropietario,
                "No podes modificar autos de otro usuario"
        );
        validarPropietario(auto, idPropietario);

        return modificarAutoValidado(auto, dto);
    }

    public AutoRespuestaDto modificarAutoAutenticado(Integer idAuto, AutoDto dto) {
        Auto auto = obtenerAutoPorId(idAuto);
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();
        validarPropietarioOAdministrador(auto, usuario);

        return modificarAutoValidado(auto, dto);
    }

    private AutoRespuestaDto modificarAutoValidado(Auto auto, AutoDto dto) {
        validarUbicacionDelPropietario(auto.getPropietario(), dto);

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
        usuarioService.validarUsuarioActualOAdministrador(
                idPropietario,
                "No podes desactivar autos de otro usuario"
        );
        validarPropietario(auto, idPropietario);

        auto.setActivo(false);
        autoRepository.save(auto);
    }

    public void desactivarAutoAutenticado(Integer idAuto) {
        Auto auto = obtenerAutoPorId(idAuto);
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();
        validarPropietarioOAdministrador(auto, usuario);

        auto.setActivo(false);
        autoRepository.save(auto);
    }

    public AutoRespuestaDto activarAutoAutenticado(Integer idAuto) {
        Auto auto = obtenerAutoPorId(idAuto);
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();
        validarPropietarioOAdministrador(auto, usuario);

        auto.setActivo(true);
        return convertirARespuesta(autoRepository.save(auto));
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

    private void validarPropietarioOAdministrador(Auto auto, Usuario usuario) {
        if (!auto.getPropietario().getIdUsuario().equals(usuario.getIdUsuario())
                && !usuarioService.tieneRol(usuario, NombreRol.ADMINISTRADOR)) {
            throw new PermisoInsuficienteException("No podes modificar este auto");
        }
    }

    private void validarUbicacionDelPropietario(Usuario propietario, AutoDto dto) {
        PerfilPropietario perfil = perfilPropietarioRepository.findById(propietario.getIdUsuario())
                .orElseThrow(() -> new ParametroIncorrectoException(
                        "Para publicar autos primero tenes que cargar tus datos de propietario"
                ));

        if (!Boolean.TRUE.equals(perfil.getActivo())) {
            throw new PermisoInsuficienteException("El perfil de propietario no esta activo");
        }

        if (!mismoTexto(dto.getCiudad(), perfil.getCiudad())
                || !mismoTexto(dto.getProvincia(), perfil.getProvincia())) {
            throw new ParametroIncorrectoException(
                    "El auto debe publicarse en la misma ciudad y provincia del propietario"
            );
        }
    }

    private boolean mismoTexto(String primero, String segundo) {
        String primeroNormalizado = normalizarTexto(primero);
        String segundoNormalizado = normalizarTexto(segundo);

        return primeroNormalizado != null && primeroNormalizado.equalsIgnoreCase(segundoNormalizado);
    }

    private boolean tieneRol(Usuario usuario, NombreRol nombreRol) {
        return usuarioService.tieneRol(usuario, nombreRol);
    }

    private void validarBusquedaDisponibles(String ciudad, LocalDate fechaInicio, LocalDate fechaFin) {
        if (ciudad == null || ciudad.isBlank()) {
            throw new ParametroIncorrectoException("La ciudad es obligatoria");
        }

        if (fechaInicio == null || fechaFin == null) {
            throw new ParametroIncorrectoException("Las fechas son obligatorias");
        }

        if (fechaInicio.isBefore(LocalDate.now())) {
            throw new ParametroIncorrectoException("La fecha de inicio no puede ser anterior a hoy");
        }

        if (!fechaFin.isAfter(fechaInicio)) {
            throw new ParametroIncorrectoException("La fecha de fin debe ser posterior a la fecha de inicio");
        }
    }

    private String normalizarTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return texto.trim();
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

package com.AutoRent.Backend.controller;

import lombok.RequiredArgsConstructor;

import com.AutoRent.Backend.dto.auto.AutoDto;
import com.AutoRent.Backend.dto.auto.AutoRespuestaDto;
import com.AutoRent.Backend.model.enums.NombreCategoriaAuto;
import com.AutoRent.Backend.model.enums.TipoCombustible;
import com.AutoRent.Backend.model.enums.TipoTransmision;
import com.AutoRent.Backend.service.AutoService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/api/autos")
@RequiredArgsConstructor
public class AutoController {

    private final AutoService autoService;

    @PostMapping("/me")
    public ResponseEntity<AutoRespuestaDto> crearMiAuto(@Valid @RequestBody AutoDto dto) {
        return ResponseEntity.ok(autoService.crearAutoAutenticado(dto));
    }

    @PostMapping("/propietario/{idPropietario}")
    public ResponseEntity<AutoRespuestaDto> crearAuto(
            @PathVariable Integer idPropietario,
            @Valid @RequestBody AutoDto dto
    ) {
        return ResponseEntity.ok(autoService.crearAuto(idPropietario, dto));
    }

    @GetMapping
    public ResponseEntity<List<AutoRespuestaDto>> listarAutosActivos() {
        return ResponseEntity.ok(autoService.listarAutosActivos());
    }

    @GetMapping("/filtrar")
    public ResponseEntity<List<AutoRespuestaDto>> buscarConFiltros(
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) NombreCategoriaAuto categoria,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) Integer pasajeros,
            @RequestParam(required = false) TipoTransmision transmision,
            @RequestParam(required = false) TipoCombustible combustible
    ) {
        return ResponseEntity.ok(autoService.buscarConFiltros(
                ciudad,
                marca,
                categoria,
                precioMax,
                pasajeros,
                transmision,
                combustible
        ));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<AutoRespuestaDto>> buscarDisponibles(
            @RequestParam String ciudad,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) NombreCategoriaAuto categoria,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) Integer pasajeros,
            @RequestParam(required = false) TipoTransmision transmision,
            @RequestParam(required = false) TipoCombustible combustible
    ) {
        return ResponseEntity.ok(autoService.buscarDisponibles(
                ciudad,
                fechaInicio,
                fechaFin,
                marca,
                categoria,
                precioMax,
                pasajeros,
                transmision,
                combustible
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<List<AutoRespuestaDto>> listarMisAutos() {
        return ResponseEntity.ok(autoService.listarMisAutos());
    }

    @GetMapping("/me/estado/{activo}")
    public ResponseEntity<List<AutoRespuestaDto>> listarMisAutosPorEstado(@PathVariable Boolean activo) {
        return ResponseEntity.ok(autoService.listarMisAutosPorEstado(activo));
    }

    @GetMapping("/me/categoria/{categoria}")
    public ResponseEntity<List<AutoRespuestaDto>> listarMisAutosPorCategoria(
            @PathVariable NombreCategoriaAuto categoria
    ) {
        return ResponseEntity.ok(autoService.listarMisAutosPorCategoria(categoria));
    }

    @GetMapping("/{idAuto}")
    public ResponseEntity<AutoRespuestaDto> buscarPorId(@PathVariable Integer idAuto) {
        return ResponseEntity.ok(autoService.buscarPorId(idAuto));
    }

    @GetMapping("/propietario/{idPropietario}")
    public ResponseEntity<List<AutoRespuestaDto>> listarAutosPorPropietario(@PathVariable Integer idPropietario) {
        return ResponseEntity.ok(autoService.listarAutosPorPropietario(idPropietario));
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<AutoRespuestaDto>> listarPorCategoria(@PathVariable NombreCategoriaAuto categoria) {
        return ResponseEntity.ok(autoService.listarPorCategoria(categoria));
    }

    @GetMapping("/marca/{marca}")
    public ResponseEntity<List<AutoRespuestaDto>> listarPorMarca(@PathVariable String marca) {
        return ResponseEntity.ok(autoService.listarPorMarca(marca));
    }

    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<List<AutoRespuestaDto>> listarPorCiudad(@PathVariable String ciudad) {
        return ResponseEntity.ok(autoService.listarPorCiudad(ciudad));
    }

    @PutMapping("/{idAuto}/propietario/{idPropietario}")
    public ResponseEntity<AutoRespuestaDto> modificarAuto(
            @PathVariable Integer idAuto,
            @PathVariable Integer idPropietario,
            @Valid @RequestBody AutoDto dto
    ) {
        return ResponseEntity.ok(autoService.modificarAuto(idAuto, idPropietario, dto));
    }

    @PutMapping("/{idAuto}/me")
    public ResponseEntity<AutoRespuestaDto> modificarMiAuto(
            @PathVariable Integer idAuto,
            @Valid @RequestBody AutoDto dto
    ) {
        return ResponseEntity.ok(autoService.modificarAutoAutenticado(idAuto, dto));
    }

    @DeleteMapping("/{idAuto}/propietario/{idPropietario}")
    public ResponseEntity<Void> desactivarAuto(
            @PathVariable Integer idAuto,
            @PathVariable Integer idPropietario
    ) {
        autoService.desactivarAuto(idAuto, idPropietario);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{idAuto}/me")
    public ResponseEntity<Void> desactivarMiAuto(@PathVariable Integer idAuto) {
        autoService.desactivarAutoAutenticado(idAuto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{idAuto}/me/activar")
    public ResponseEntity<AutoRespuestaDto> activarMiAuto(@PathVariable Integer idAuto) {
        return ResponseEntity.ok(autoService.activarAutoAutenticado(idAuto));
    }
}

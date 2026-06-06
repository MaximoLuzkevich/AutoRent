package com.AutoRent.Backend.controller;

import com.AutoRent.Backend.dto.auto.AutoDto;
import com.AutoRent.Backend.dto.auto.AutoRespuestaDto;
import com.AutoRent.Backend.model.enums.NombreCategoriaAuto;
import com.AutoRent.Backend.service.AutoService;
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
@RequestMapping("/api/autos")
public class AutoController {

    private final AutoService autoService;

    public AutoController(AutoService autoService) {
        this.autoService = autoService;
    }

    @PostMapping
    public ResponseEntity<AutoRespuestaDto> crearAuto(
            @RequestParam Integer idPropietario,
            @Valid @RequestBody AutoDto dto
    ) {
        return ResponseEntity.ok(autoService.crearAuto(idPropietario, dto));
    }

    @GetMapping
    public ResponseEntity<List<AutoRespuestaDto>> listarAutosActivos() {
        return ResponseEntity.ok(autoService.listarAutosActivos());
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

    @GetMapping("/marca")
    public ResponseEntity<List<AutoRespuestaDto>> listarPorMarca(@RequestParam String marca) {
        return ResponseEntity.ok(autoService.listarPorMarca(marca));
    }

    @GetMapping("/ciudad")
    public ResponseEntity<List<AutoRespuestaDto>> listarPorCiudad(@RequestParam String ciudad) {
        return ResponseEntity.ok(autoService.listarPorCiudad(ciudad));
    }

    @PutMapping("/{idAuto}")
    public ResponseEntity<AutoRespuestaDto> modificarAuto(
            @PathVariable Integer idAuto,
            @RequestParam Integer idPropietario,
            @Valid @RequestBody AutoDto dto
    ) {
        return ResponseEntity.ok(autoService.modificarAuto(idAuto, idPropietario, dto));
    }

    @DeleteMapping("/{idAuto}")
    public ResponseEntity<Void> desactivarAuto(
            @PathVariable Integer idAuto,
            @RequestParam Integer idPropietario
    ) {
        autoService.desactivarAuto(idAuto, idPropietario);
        return ResponseEntity.noContent().build();
    }
}

package com.AutoRent.Backend.controller;

import lombok.RequiredArgsConstructor;

import com.AutoRent.Backend.dto.auto.AutoDto;
import com.AutoRent.Backend.dto.auto.AutoRespuestaDto;
import com.AutoRent.Backend.model.enums.NombreCategoriaAuto;
import com.AutoRent.Backend.service.AutoService;
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
@RequestMapping("/api/autos")
@Tag(name = "Autos")
@RequiredArgsConstructor
public class AutoController {

    private final AutoService autoService;


    @Operation(summary = "Publicar mi auto", description = "Publica un auto usando el usuario autenticado como propietario.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Auto publicado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido"),
            @ApiResponse(responseCode = "403", description = "El usuario no puede publicar autos"),
            @ApiResponse(responseCode = "409", description = "Patente duplicada")
    })
    @PostMapping("/me")
    public ResponseEntity<AutoRespuestaDto> crearMiAuto(@Valid @RequestBody AutoDto dto) {
        return ResponseEntity.ok(autoService.crearAutoAutenticado(dto));
    }

    @Operation(summary = "Publicar auto para propietario", description = "Publica un auto indicando el ID del propietario. Recomendado solo para administracion.")
    @PostMapping("/propietario/{idPropietario}")
    public ResponseEntity<AutoRespuestaDto> crearAuto(
            @PathVariable Integer idPropietario,
            @Valid @RequestBody AutoDto dto
    ) {
        return ResponseEntity.ok(autoService.crearAuto(idPropietario, dto));
    }

    @Operation(summary = "Listar autos activos", description = "Endpoint publico para consultar autos disponibles.")
    @ApiResponse(responseCode = "200", description = "Autos obtenidos correctamente")
    @GetMapping
    public ResponseEntity<List<AutoRespuestaDto>> listarAutosActivos() {
        return ResponseEntity.ok(autoService.listarAutosActivos());
    }

    @Operation(summary = "Buscar auto por ID", description = "Endpoint publico para ver el detalle de un auto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Auto encontrado"),
            @ApiResponse(responseCode = "404", description = "Auto inexistente")
    })
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

    @Operation(summary = "Modificar auto", description = "Modifica un auto validando que pertenezca al propietario indicado o al administrador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Auto modificado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "403", description = "No puede modificar este auto"),
            @ApiResponse(responseCode = "404", description = "Auto inexistente"),
            @ApiResponse(responseCode = "409", description = "Patente duplicada")
    })
    @PutMapping("/{idAuto}/propietario/{idPropietario}")
    public ResponseEntity<AutoRespuestaDto> modificarAuto(
            @PathVariable Integer idAuto,
            @PathVariable Integer idPropietario,
            @Valid @RequestBody AutoDto dto
    ) {
        return ResponseEntity.ok(autoService.modificarAuto(idAuto, idPropietario, dto));
    }

    @Operation(summary = "Modificar mi auto", description = "Modifica un auto propio usando el usuario autenticado.")
    @PutMapping("/{idAuto}/me")
    public ResponseEntity<AutoRespuestaDto> modificarMiAuto(
            @PathVariable Integer idAuto,
            @Valid @RequestBody AutoDto dto
    ) {
        return ResponseEntity.ok(autoService.modificarAutoAutenticado(idAuto, dto));
    }

    @Operation(summary = "Desactivar auto", description = "Desactiva una publicacion sin borrar el registro.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Auto desactivado"),
            @ApiResponse(responseCode = "403", description = "No puede desactivar este auto"),
            @ApiResponse(responseCode = "404", description = "Auto inexistente")
    })
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
}

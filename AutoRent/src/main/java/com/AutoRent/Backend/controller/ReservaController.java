package com.AutoRent.Backend.controller;

import lombok.RequiredArgsConstructor;

import com.AutoRent.Backend.dto.reserva.ReservaDto;
import com.AutoRent.Backend.dto.reserva.ReservaRespuestaDto;
import com.AutoRent.Backend.model.enums.EstadoReserva;
import com.AutoRent.Backend.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;


    @Operation(summary = "Crear mi reserva", description = "Crea una reserva para el cliente autenticado. Valida fechas, disponibilidad y que no sea auto propio.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva creada"),
            @ApiResponse(responseCode = "400", description = "Fechas invalidas, auto no disponible o reserva superpuesta"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido"),
            @ApiResponse(responseCode = "403", description = "Requiere rol CLIENTE"),
            @ApiResponse(responseCode = "404", description = "Auto inexistente")
    })
    @PostMapping("/me")
    public ResponseEntity<ReservaRespuestaDto> crearMiReserva(@Valid @RequestBody ReservaDto dto) {
        return ResponseEntity.ok(reservaService.crearReservaAutenticada(dto));
    }

    @Operation(summary = "Crear reserva para cliente", description = "Crea una reserva indicando ID de cliente. Recomendado para administracion.")
    @PostMapping("/cliente/{idCliente}")
    public ResponseEntity<ReservaRespuestaDto> crearReserva(
            @PathVariable Integer idCliente,
            @Valid @RequestBody ReservaDto dto
    ) {
        return ResponseEntity.ok(reservaService.crearReserva(idCliente, dto));
    }

    @Operation(summary = "Buscar reserva", description = "Devuelve una reserva si el usuario autenticado es cliente, propietario del auto o administrador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva encontrada"),
            @ApiResponse(responseCode = "403", description = "No puede consultar esta reserva"),
            @ApiResponse(responseCode = "404", description = "Reserva inexistente")
    })
    @GetMapping("/{idReserva}")
    public ResponseEntity<ReservaRespuestaDto> buscarPorId(@PathVariable Integer idReserva) {
        return ResponseEntity.ok(reservaService.buscarPorId(idReserva));
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<ReservaRespuestaDto>> listarReservasPorCliente(@PathVariable Integer idCliente) {
        return ResponseEntity.ok(reservaService.listarReservasPorCliente(idCliente));
    }

    @Operation(summary = "Listar mis reservas", description = "Lista las reservas del usuario autenticado como cliente.")
    @GetMapping("/me")
    public ResponseEntity<List<ReservaRespuestaDto>> listarMisReservas() {
        return ResponseEntity.ok(reservaService.listarMisReservas());
    }

    @GetMapping("/propietario/{idPropietario}")
    public ResponseEntity<List<ReservaRespuestaDto>> listarReservasPorPropietario(
            @PathVariable Integer idPropietario
    ) {
        return ResponseEntity.ok(reservaService.listarReservasPorPropietario(idPropietario));
    }

    @GetMapping("/propietario/{idPropietario}/auto/{idAuto}")
    public ResponseEntity<List<ReservaRespuestaDto>> listarReservasPorAutoDePropietario(
            @PathVariable Integer idPropietario,
            @PathVariable Integer idAuto
    ) {
        return ResponseEntity.ok(reservaService.listarReservasPorAutoDePropietario(idPropietario, idAuto));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ReservaRespuestaDto>> listarReservasPorEstado(@PathVariable EstadoReserva estado) {
        return ResponseEntity.ok(reservaService.listarReservasPorEstado(estado));
    }

    @Operation(summary = "Confirmar reserva", description = "Confirma una reserva pendiente. Solo propietario del auto o administrador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva confirmada"),
            @ApiResponse(responseCode = "400", description = "La reserva no esta pendiente"),
            @ApiResponse(responseCode = "403", description = "No puede modificar esta reserva"),
            @ApiResponse(responseCode = "404", description = "Reserva inexistente")
    })
    @PutMapping("/{idReserva}/confirmar")
    public ResponseEntity<ReservaRespuestaDto> confirmarReserva(@PathVariable Integer idReserva) {
        return ResponseEntity.ok(reservaService.confirmarReserva(idReserva));
    }

    @PutMapping("/{idReserva}/estado/confirmada")
    public ResponseEntity<ReservaRespuestaDto> marcarReservaConfirmada(@PathVariable Integer idReserva) {
        return ResponseEntity.ok(reservaService.confirmarReserva(idReserva));
    }

    @Operation(summary = "Cancelar reserva", description = "Cancela una reserva pendiente o confirmada. Puede hacerlo cliente, propietario o administrador.")
    @PutMapping("/{idReserva}/cancelar")
    public ResponseEntity<ReservaRespuestaDto> cancelarReserva(@PathVariable Integer idReserva) {
        return ResponseEntity.ok(reservaService.cancelarReserva(idReserva));
    }

    @PutMapping("/{idReserva}/estado/cancelada")
    public ResponseEntity<ReservaRespuestaDto> marcarReservaCancelada(@PathVariable Integer idReserva) {
        return ResponseEntity.ok(reservaService.cancelarReserva(idReserva));
    }

    @Operation(summary = "Finalizar reserva", description = "Finaliza una reserva confirmada. Solo propietario del auto o administrador.")
    @PutMapping("/{idReserva}/finalizar")
    public ResponseEntity<ReservaRespuestaDto> finalizarReserva(@PathVariable Integer idReserva) {
        return ResponseEntity.ok(reservaService.finalizarReserva(idReserva));
    }

    @PutMapping("/{idReserva}/estado/finalizada")
    public ResponseEntity<ReservaRespuestaDto> marcarReservaFinalizada(@PathVariable Integer idReserva) {
        return ResponseEntity.ok(reservaService.finalizarReserva(idReserva));
    }
}

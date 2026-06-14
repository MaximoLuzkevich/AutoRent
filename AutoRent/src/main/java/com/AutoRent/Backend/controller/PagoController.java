package com.AutoRent.Backend.controller;

import lombok.RequiredArgsConstructor;

import com.AutoRent.Backend.dto.pago.PagoDto;
import com.AutoRent.Backend.dto.pago.PagoRespuestaDto;
import com.AutoRent.Backend.model.enums.EstadoPago;
import com.AutoRent.Backend.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pagos")
@Tag(name = "Pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;


    @Operation(summary = "Registrar pago", description = "Registra un pago pendiente para una reserva propia. El monto debe coincidir con el total de la reserva.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago registrado"),
            @ApiResponse(responseCode = "400", description = "Monto incorrecto o reserva no pagable"),
            @ApiResponse(responseCode = "403", description = "No puede pagar una reserva ajena"),
            @ApiResponse(responseCode = "404", description = "Reserva inexistente")
    })
    @PostMapping
    public ResponseEntity<PagoRespuestaDto> registrarPago(@Valid @RequestBody PagoDto dto) {
        return ResponseEntity.ok(pagoService.registrarPago(dto));
    }

    @Operation(summary = "Listar mis pagos", description = "Lista los pagos del usuario autenticado como cliente.")
    @GetMapping("/me")
    public ResponseEntity<List<PagoRespuestaDto>> listarMisPagos() {
        return ResponseEntity.ok(pagoService.listarMisPagos());
    }

    @Operation(summary = "Listar pagos de mis autos", description = "Lista los pagos recibidos por reservas sobre autos del propietario autenticado.")
    @GetMapping("/me/propietario")
    public ResponseEntity<List<PagoRespuestaDto>> listarPagosDeMisAutos() {
        return ResponseEntity.ok(pagoService.listarPagosDeMisAutos());
    }

    @GetMapping("/reserva/{idReserva}")
    public ResponseEntity<List<PagoRespuestaDto>> listarPagosPorReserva(@PathVariable Integer idReserva) {
        return ResponseEntity.ok(pagoService.listarPagosPorReserva(idReserva));
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<PagoRespuestaDto>> listarPagosPorCliente(@PathVariable Integer idCliente) {
        return ResponseEntity.ok(pagoService.listarPagosPorCliente(idCliente));
    }

    @GetMapping("/propietario/{idPropietario}")
    public ResponseEntity<List<PagoRespuestaDto>> listarPagosPorPropietario(@PathVariable Integer idPropietario) {
        return ResponseEntity.ok(pagoService.listarPagosPorPropietario(idPropietario));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PagoRespuestaDto>> listarPagosPorEstado(@PathVariable EstadoPago estado) {
        return ResponseEntity.ok(pagoService.listarPagosPorEstado(estado));
    }

    @GetMapping("/fechas/{desde}/{hasta}")
    public ResponseEntity<List<PagoRespuestaDto>> listarPagosEntreFechas(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        return ResponseEntity.ok(pagoService.listarPagosEntreFechas(desde, hasta));
    }

    @Operation(summary = "Aprobar pago", description = "Aprueba un pago pendiente. Solo administrador. Si la reserva estaba pendiente, queda confirmada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago aprobado"),
            @ApiResponse(responseCode = "400", description = "El pago no esta pendiente o la reserva no puede pagarse"),
            @ApiResponse(responseCode = "403", description = "Requiere rol ADMINISTRADOR"),
            @ApiResponse(responseCode = "404", description = "Pago inexistente")
    })
    @PutMapping("/{idPago}/aprobar")
    public ResponseEntity<PagoRespuestaDto> aprobarPago(@PathVariable Integer idPago) {
        return ResponseEntity.ok(pagoService.aprobarPago(idPago));
    }

    @PutMapping("/{idPago}/estado/aprobado")
    public ResponseEntity<PagoRespuestaDto> marcarPagoAprobado(@PathVariable Integer idPago) {
        return ResponseEntity.ok(pagoService.aprobarPago(idPago));
    }

    @Operation(summary = "Rechazar pago", description = "Rechaza un pago pendiente. Solo administrador.")
    @PutMapping("/{idPago}/rechazar")
    public ResponseEntity<PagoRespuestaDto> rechazarPago(@PathVariable Integer idPago) {
        return ResponseEntity.ok(pagoService.rechazarPago(idPago));
    }

    @PutMapping("/{idPago}/estado/rechazado")
    public ResponseEntity<PagoRespuestaDto> marcarPagoRechazado(@PathVariable Integer idPago) {
        return ResponseEntity.ok(pagoService.rechazarPago(idPago));
    }
}

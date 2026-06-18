package com.AutoRent.Backend.controller;

import lombok.RequiredArgsConstructor;

import com.AutoRent.Backend.dto.reserva.ReservaDto;
import com.AutoRent.Backend.dto.reserva.ReservaRespuestaDto;
import com.AutoRent.Backend.model.enums.EstadoReserva;
import com.AutoRent.Backend.service.ReservaService;
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
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;


    @PostMapping("/me")
    public ResponseEntity<ReservaRespuestaDto> crearMiReserva(@Valid @RequestBody ReservaDto dto) {
        return ResponseEntity.ok(reservaService.crearReservaAutenticada(dto));
    }

    @PostMapping("/cliente/{idCliente}")
    public ResponseEntity<ReservaRespuestaDto> crearReserva(
            @PathVariable Integer idCliente,
            @Valid @RequestBody ReservaDto dto
    ) {
        return ResponseEntity.ok(reservaService.crearReserva(idCliente, dto));
    }

    @GetMapping("/{idReserva}")
    public ResponseEntity<ReservaRespuestaDto> buscarPorId(@PathVariable Integer idReserva) {
        return ResponseEntity.ok(reservaService.buscarPorId(idReserva));
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<ReservaRespuestaDto>> listarReservasPorCliente(@PathVariable Integer idCliente) {
        return ResponseEntity.ok(reservaService.listarReservasPorCliente(idCliente));
    }

    @GetMapping("/me")
    public ResponseEntity<List<ReservaRespuestaDto>> listarMisReservas() {
        return ResponseEntity.ok(reservaService.listarMisReservas());
    }

    @GetMapping("/me/estado/{estado}")
    public ResponseEntity<List<ReservaRespuestaDto>> listarMisReservasPorEstado(
            @PathVariable EstadoReserva estado
    ) {
        return ResponseEntity.ok(reservaService.listarMisReservasPorEstado(estado));
    }

    @GetMapping("/me/fechas/{desde}/{hasta}")
    public ResponseEntity<List<ReservaRespuestaDto>> listarMisReservasPorFechas(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        return ResponseEntity.ok(reservaService.listarMisReservasPorFechas(desde, hasta));
    }

    @GetMapping("/me/propietario/pendientes")
    public ResponseEntity<List<ReservaRespuestaDto>> listarMisReservasPendientesComoPropietario() {
        return ResponseEntity.ok(reservaService.listarMisReservasPendientesComoPropietario());
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

    @PutMapping("/{idReserva}/confirmar")
    public ResponseEntity<ReservaRespuestaDto> confirmarReserva(@PathVariable Integer idReserva) {
        return ResponseEntity.ok(reservaService.confirmarReserva(idReserva));
    }

    @PutMapping("/{idReserva}/estado/confirmada")
    public ResponseEntity<ReservaRespuestaDto> marcarReservaConfirmada(@PathVariable Integer idReserva) {
        return ResponseEntity.ok(reservaService.confirmarReserva(idReserva));
    }

    @PutMapping("/{idReserva}/cancelar")
    public ResponseEntity<ReservaRespuestaDto> cancelarReserva(@PathVariable Integer idReserva) {
        return ResponseEntity.ok(reservaService.cancelarReserva(idReserva));
    }

    @PutMapping("/{idReserva}/estado/cancelada")
    public ResponseEntity<ReservaRespuestaDto> marcarReservaCancelada(@PathVariable Integer idReserva) {
        return ResponseEntity.ok(reservaService.cancelarReserva(idReserva));
    }

    @PutMapping("/{idReserva}/finalizar")
    public ResponseEntity<ReservaRespuestaDto> finalizarReserva(@PathVariable Integer idReserva) {
        return ResponseEntity.ok(reservaService.finalizarReserva(idReserva));
    }

    @PutMapping("/{idReserva}/estado/finalizada")
    public ResponseEntity<ReservaRespuestaDto> marcarReservaFinalizada(@PathVariable Integer idReserva) {
        return ResponseEntity.ok(reservaService.finalizarReserva(idReserva));
    }
}

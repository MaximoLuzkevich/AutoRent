package com.AutoRent.Backend.controller;

import com.AutoRent.Backend.dto.reserva.ReservaDto;
import com.AutoRent.Backend.dto.reserva.ReservaRespuestaDto;
import com.AutoRent.Backend.model.enums.EstadoReserva;
import com.AutoRent.Backend.service.ReservaService;
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
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
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

    @PutMapping("/{idReserva}/cancelar")
    public ResponseEntity<ReservaRespuestaDto> cancelarReserva(@PathVariable Integer idReserva) {
        return ResponseEntity.ok(reservaService.cancelarReserva(idReserva));
    }

    @PutMapping("/{idReserva}/finalizar")
    public ResponseEntity<ReservaRespuestaDto> finalizarReserva(@PathVariable Integer idReserva) {
        return ResponseEntity.ok(reservaService.finalizarReserva(idReserva));
    }
}

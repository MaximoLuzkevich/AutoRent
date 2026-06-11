package com.AutoRent.Backend.dto.reserva;

import com.AutoRent.Backend.model.enums.EstadoReserva;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRespuestaDto {

    private Integer idReserva;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal precioTotal;
    private EstadoReserva estado;
    private LocalDateTime fechaReserva;
    private Integer idCliente;
    private String nombreCliente;
    private Integer idAuto;
    private String auto;
}

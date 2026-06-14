package com.AutoRent.Backend.dto.reserva;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para solicitar una reserva de auto")
public class ReservaDto {

    @NotNull
    @Schema(description = "Fecha de inicio de la reserva", example = "2026-07-01")
    private LocalDate fechaInicio;

    @NotNull
    @Schema(description = "Fecha de fin de la reserva", example = "2026-07-05")
    private LocalDate fechaFin;

    @NotNull
    @Schema(description = "ID del auto a reservar", example = "1")
    private Integer idAuto;
}

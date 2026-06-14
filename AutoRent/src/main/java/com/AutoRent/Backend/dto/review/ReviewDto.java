package com.AutoRent.Backend.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para publicar una review sobre un auto reservado y finalizado")
public class ReviewDto {

    @NotNull
    @Min(1)
    @Max(5)
    @Schema(description = "Calificacion entre 1 y 5", example = "5")
    private Integer puntuacion;

    @Schema(example = "Excelente auto, muy comodo y limpio")
    private String comentario;

    @NotNull
    @Schema(description = "ID del auto evaluado", example = "1")
    private Integer idAuto;
}

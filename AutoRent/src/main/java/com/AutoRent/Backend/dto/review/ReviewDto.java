package com.AutoRent.Backend.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

    @NotNull
    @Min(1)
    @Max(5)
    private Integer puntuacion;

    private String comentario;

    @NotNull
    private Integer idAuto;
}

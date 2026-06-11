package com.AutoRent.Backend.dto.review;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRespuestaDto {

    private Integer idReview;
    private Integer puntuacion;
    private String comentario;
    private LocalDateTime fecha;
    private Integer idCliente;
    private String nombreCliente;
    private Integer idAuto;
}

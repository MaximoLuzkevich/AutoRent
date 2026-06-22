package com.AutoRent.Backend.dto.imagenauto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImagenAutoRespuestaDto {

    private Integer idImagen;
    private String nombreArchivo;
    private String urlImagen;
    private String publicId;
    private Boolean principal;
    private LocalDateTime fechaCarga;
    private Integer idAuto;
}

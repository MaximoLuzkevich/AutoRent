package com.AutoRent.Backend.dto.imagenauto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para registrar una imagen asociada a un auto")
public class ImagenAutoDto {

    @NotBlank
    @Size(max = 255)
    @Schema(example = "corolla-frente.jpg")
    private String nombreArchivo;

    @NotBlank
    @Size(max = 500)
    @Schema(example = "https://imagenes.autorent.com/autos/corolla-frente.jpg")
    private String urlImagen;

    @Schema(description = "Indica si sera la imagen principal del auto", example = "true")
    private Boolean principal;
}

package com.AutoRent.Backend.dto.imagenauto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImagenAutoDto {

    @NotBlank(message = "El nombre del archivo es obligatorio")
    @Size(max = 255, message = "El nombre del archivo no puede superar los 255 caracteres")
    @Pattern(
            regexp = "^[A-Za-z0-9ÁÉÍÓÚáéíóúÑñ._\\- ]+$",
            message = "El nombre del archivo contiene caracteres no validos"
    )
    private String nombreArchivo;

    @NotBlank(message = "La URL de la imagen es obligatoria")
    @Size(max = 500, message = "La URL de la imagen no puede superar los 500 caracteres")
    @Pattern(regexp = "^(http|https)://.+$", message = "La URL de la imagen debe comenzar con http o https")
    private String urlImagen;

    private Boolean principal;
}

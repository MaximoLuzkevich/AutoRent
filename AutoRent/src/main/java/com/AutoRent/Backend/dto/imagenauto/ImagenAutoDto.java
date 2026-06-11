package com.AutoRent.Backend.dto.imagenauto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImagenAutoDto {

    @NotBlank
    @Size(max = 255)
    private String nombreArchivo;

    @NotBlank
    @Size(max = 500)
    private String urlImagen;

    private Boolean principal;
}

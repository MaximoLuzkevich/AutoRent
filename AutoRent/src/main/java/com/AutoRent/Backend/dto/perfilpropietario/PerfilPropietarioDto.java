package com.AutoRent.Backend.dto.perfilpropietario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos adicionales para que un usuario pueda publicar autos como propietario")
public class PerfilPropietarioDto {

    @Size(max = 30)
    @Schema(example = "30123456")
    private String dni;

    @Size(max = 30)
    @Schema(example = "20-30123456-7")
    private String cuit;

    @Size(max = 150)
    @Schema(example = "Av Siempre Viva 123")
    private String direccion;

    @Size(max = 100)
    @Schema(example = "Cordoba")
    private String ciudad;

    @Size(max = 100)
    @Schema(example = "Cordoba")
    private String provincia;
}

package com.AutoRent.Backend.dto.auto;

import com.AutoRent.Backend.model.enums.NombreCategoriaAuto;
import com.AutoRent.Backend.model.enums.TipoCombustible;
import com.AutoRent.Backend.model.enums.TipoTransmision;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para publicar o modificar un auto")
public class AutoDto {

    @NotBlank
    @Size(max = 100)
    @Schema(example = "Toyota")
    private String marca;

    @NotBlank
    @Size(max = 100)
    @Schema(example = "Corolla")
    private String modelo;

    @NotNull
    @Schema(example = "2022")
    private Integer anio;

    @NotBlank
    @Size(max = 20)
    @Schema(example = "ABC123")
    private String patente;

    @Size(max = 50)
    @Schema(example = "Blanco")
    private String color;

    @NotNull
    @Min(1)
    @Schema(example = "5")
    private Integer capacidadPasajeros;

    @NotNull
    @Min(1)
    @Schema(example = "4")
    private Integer cantidadPuertas;

    @NotNull
    @Schema(example = "AUTOMATICA")
    private TipoTransmision transmision;

    @NotNull
    @Schema(example = "NAFTA")
    private TipoCombustible combustible;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Schema(example = "30000")
    private BigDecimal precioDia;

    @Schema(example = "Auto comodo para ciudad y ruta")
    private String descripcion;

    @NotBlank
    @Size(max = 100)
    @Schema(example = "Cordoba")
    private String ciudad;

    @Size(max = 100)
    @Schema(example = "Cordoba")
    private String provincia;

    @NotBlank
    @Size(max = 150)
    @Schema(example = "Av Siempre Viva 123")
    private String direccionRetiro;

    @NotNull
    @Schema(example = "ECONOMICO")
    private NombreCategoriaAuto categoria;
}

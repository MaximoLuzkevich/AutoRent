package com.AutoRent.Backend.dto.auto;

import com.AutoRent.Backend.model.enums.NombreCategoriaAuto;
import com.AutoRent.Backend.model.enums.TipoCombustible;
import com.AutoRent.Backend.model.enums.TipoTransmision;
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
public class AutoDto {

    @NotBlank
    @Size(max = 100)
    private String marca;

    @NotBlank
    @Size(max = 100)
    private String modelo;

    @NotNull
    private Integer anio;

    @NotBlank
    @Size(max = 20)
    private String patente;

    @Size(max = 50)
    private String color;

    @NotNull
    @Min(1)
    private Integer capacidadPasajeros;

    @NotNull
    @Min(1)
    private Integer cantidadPuertas;

    @NotNull
    private TipoTransmision transmision;

    @NotNull
    private TipoCombustible combustible;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal precioDia;

    private String descripcion;

    @NotBlank
    @Size(max = 100)
    private String ciudad;

    @Size(max = 100)
    private String provincia;

    @NotBlank
    @Size(max = 150)
    private String direccionRetiro;

    @NotNull
    private NombreCategoriaAuto categoria;
}

package com.AutoRent.Backend.dto.auto;

import com.AutoRent.Backend.model.enums.NombreCategoriaAuto;
import com.AutoRent.Backend.model.enums.TipoCombustible;
import com.AutoRent.Backend.model.enums.TipoTransmision;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutoDto {

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 100, message = "La marca no puede superar los 100 caracteres")
    @Pattern(
            regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ]+(?: [A-Za-zÁÉÍÓÚáéíóúÑñ]+)*$",
            message = "La marca solo puede contener letras y espacios"
    )
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(max = 100, message = "El modelo no puede superar los 100 caracteres")
    @Pattern(
            regexp = "^[A-Za-z0-9ÁÉÍÓÚáéíóúÑñ]+(?: [A-Za-z0-9ÁÉÍÓÚáéíóúÑñ]+)*$",
            message = "El modelo solo puede contener letras, numeros y espacios"
    )
    private String modelo;

    @NotNull(message = "El anio es obligatorio")
    private Integer anio;

    @NotBlank(message = "La patente es obligatoria")
    @Size(max = 20, message = "La patente no puede superar los 20 caracteres")
    @Pattern(regexp = "^[A-Za-z0-9]{6,8}$", message = "La patente debe tener entre 6 y 8 letras o numeros")
    private String patente;

    @Size(max = 50, message = "El color no puede superar los 50 caracteres")
    @Pattern(
            regexp = "^$|[A-Za-zÁÉÍÓÚáéíóúÑñ]+(?: [A-Za-zÁÉÍÓÚáéíóúÑñ]+)*",
            message = "El color solo puede contener letras y espacios"
    )
    private String color;

    @NotNull(message = "La cantidad de pasajeros es obligatoria")
    @Min(value = 1, message = "La cantidad de pasajeros debe ser mayor a cero")
    private Integer capacidadPasajeros;

    @NotNull(message = "La cantidad de puertas es obligatoria")
    @Min(value = 1, message = "La cantidad de puertas debe ser mayor a cero")
    private Integer cantidadPuertas;

    @NotNull(message = "La transmision es obligatoria")
    private TipoTransmision transmision;

    @NotNull(message = "El combustible es obligatorio")
    private TipoCombustible combustible;

    @NotNull(message = "El precio por dia es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio por dia debe ser mayor a cero")
    private BigDecimal precioDia;

    private String descripcion;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 100, message = "La ciudad no puede superar los 100 caracteres")
    @Pattern(
            regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ]+(?: [A-Za-zÁÉÍÓÚáéíóúÑñ]+)*$",
            message = "La ciudad solo puede contener letras y espacios"
    )
    private String ciudad;

    @NotBlank(message = "La provincia es obligatoria")
    @Size(max = 100, message = "La provincia no puede superar los 100 caracteres")
    @Pattern(
            regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ]+(?: [A-Za-zÁÉÍÓÚáéíóúÑñ]+)*$",
            message = "La provincia solo puede contener letras y espacios"
    )
    private String provincia;

    @NotBlank(message = "La direccion de retiro es obligatoria")
    @Size(max = 150, message = "La direccion de retiro no puede superar los 150 caracteres")
    @Pattern(
            regexp = "^[A-Za-z0-9ÁÉÍÓÚáéíóúÑñ.,°º\\- ]+$",
            message = "La direccion de retiro contiene caracteres no validos"
    )
    private String direccionRetiro;

    @NotNull(message = "La categoria es obligatoria")
    private NombreCategoriaAuto categoria;
}

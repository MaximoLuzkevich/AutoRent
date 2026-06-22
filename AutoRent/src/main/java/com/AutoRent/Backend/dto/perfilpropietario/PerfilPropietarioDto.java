package com.AutoRent.Backend.dto.perfilpropietario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilPropietarioDto {

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^\\d{8}$", message = "El DNI debe tener 8 numeros")
    private String dni;

    @NotBlank(message = "El CUIT es obligatorio")
    @Pattern(regexp = "^\\d{11}$", message = "El CUIT debe tener 11 numeros")
    private String cuit;

    @NotBlank(message = "La direccion es obligatoria")
    @Size(max = 150, message = "La direccion no puede superar los 150 caracteres")
    @Pattern(
            regexp = "^[A-Za-z0-9ÁÉÍÓÚáéíóúÑñ.,°º\\- ]+$",
            message = "La direccion contiene caracteres no validos"
    )
    private String direccion;

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
}

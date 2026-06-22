package com.AutoRent.Backend.dto.perfilpropietario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilPropietarioDto {

    @Size(max = 30, message = "El DNI no puede superar los 30 caracteres")
    private String dni;

    @Size(max = 30, message = "El CUIT no puede superar los 30 caracteres")
    private String cuit;

    @Size(max = 150, message = "La direccion no puede superar los 150 caracteres")
    private String direccion;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 100, message = "La ciudad no puede superar los 100 caracteres")
    private String ciudad;

    @NotBlank(message = "La provincia es obligatoria")
    @Size(max = 100, message = "La provincia no puede superar los 100 caracteres")
    private String provincia;
}

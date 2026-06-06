package com.AutoRent.Backend.dto.perfilpropietario;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilPropietarioDto {

    @Size(max = 30)
    private String dni;

    @Size(max = 30)
    private String cuit;

    @Size(max = 150)
    private String direccion;

    @Size(max = 100)
    private String ciudad;

    @Size(max = 100)
    private String provincia;
}

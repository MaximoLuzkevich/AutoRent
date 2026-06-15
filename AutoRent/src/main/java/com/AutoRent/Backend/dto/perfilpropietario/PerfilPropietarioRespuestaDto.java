package com.AutoRent.Backend.dto.perfilpropietario;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilPropietarioRespuestaDto {

    private Integer idUsuario;
    private String nombreUsuario;
    private String emailUsuario;
    private String dni;
    private String cuit;
    private String direccion;
    private String ciudad;
    private String provincia;
    private LocalDateTime fechaAlta;
    private Boolean verificado;
    private Boolean activo;
}

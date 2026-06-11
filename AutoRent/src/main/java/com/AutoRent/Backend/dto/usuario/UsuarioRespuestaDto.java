package com.AutoRent.Backend.dto.usuario;

import com.AutoRent.Backend.model.enums.NombreRol;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRespuestaDto {

    private Integer idUsuario;
    private String nombre;
    private String email;
    private String telefono;
    private LocalDateTime fechaRegistro;
    private Boolean activo;
    private Set<NombreRol> roles;
}

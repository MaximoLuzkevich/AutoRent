package com.AutoRent.Backend.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRespuestaDto {

    private String token;
    private String tipoToken;
    private UsuarioRespuestaDto usuario;
}

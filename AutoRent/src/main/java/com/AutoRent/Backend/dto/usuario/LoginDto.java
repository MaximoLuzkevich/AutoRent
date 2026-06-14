package com.AutoRent.Backend.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Credenciales para iniciar sesion y obtener un token JWT")
public class LoginDto {

    @NotBlank
    @Email
    @Schema(description = "Email registrado del usuario", example = "cliente@test.com")
    private String email;

    @NotBlank
    @Schema(description = "Password del usuario", example = "123456")
    private String password;
}

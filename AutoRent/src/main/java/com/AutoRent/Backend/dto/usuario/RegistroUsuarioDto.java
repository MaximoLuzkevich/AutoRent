package com.AutoRent.Backend.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos necesarios para registrar un nuevo usuario cliente")
public class RegistroUsuarioDto {

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Nombre completo del usuario", example = "Cliente Demo")
    private String nombre;

    @NotBlank
    @Email
    @Size(max = 150)
    @Schema(description = "Email unico del usuario", example = "cliente@test.com")
    private String email;

    @NotBlank
    @Size(min = 6, max = 100)
    @Schema(description = "Password con al menos 6 caracteres", example = "123456")
    private String password;

    @Size(max = 30)
    @Schema(description = "Telefono de contacto", example = "1122334455")
    private String telefono;
}

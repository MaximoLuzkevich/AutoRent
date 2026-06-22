package com.AutoRent.Backend.dto.lugar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LugarSugerenciaDto {

    private String ciudad;
    private String provincia;
    private String pais;
    private String textoCompleto;
}

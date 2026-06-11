package com.AutoRent.Backend.dto.categoria;

import com.AutoRent.Backend.model.enums.NombreCategoriaAuto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaAutoRespuestaDto {

    private Integer idCategoria;
    private NombreCategoriaAuto nombre;
    private String descripcion;
}

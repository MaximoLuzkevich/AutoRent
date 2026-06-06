package com.AutoRent.Backend.dto.auto;

import com.AutoRent.Backend.model.enums.NombreCategoriaAuto;
import com.AutoRent.Backend.model.enums.TipoCombustible;
import com.AutoRent.Backend.model.enums.TipoTransmision;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutoRespuestaDto {

    private Integer idAuto;
    private String marca;
    private String modelo;
    private Integer anio;
    private String patente;
    private String color;
    private Integer capacidadPasajeros;
    private Integer cantidadPuertas;
    private TipoTransmision transmision;
    private TipoCombustible combustible;
    private BigDecimal precioDia;
    private String descripcion;
    private String ciudad;
    private String provincia;
    private String direccionRetiro;
    private Boolean activo;
    private LocalDateTime fechaPublicacion;
    private Integer idPropietario;
    private String nombrePropietario;
    private NombreCategoriaAuto categoria;
}

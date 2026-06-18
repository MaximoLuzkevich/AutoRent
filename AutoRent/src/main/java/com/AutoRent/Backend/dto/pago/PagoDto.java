package com.AutoRent.Backend.dto.pago;

import com.AutoRent.Backend.model.enums.MetodoPago;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoDto {

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal monto;

    @NotNull
    private MetodoPago metodoPago;

    @NotNull
    private Integer idReserva;

    private String titularTarjeta;

    private String numeroTarjeta;

    private String vencimientoTarjeta;

    private String codigoSeguridad;

    public PagoDto(BigDecimal monto, MetodoPago metodoPago, Integer idReserva) {
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.idReserva = idReserva;
    }
}

package com.AutoRent.Backend.dto.pago;

import com.AutoRent.Backend.model.enums.MetodoPago;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para registrar un pago de una reserva")
public class PagoDto {

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Schema(description = "Monto exacto a pagar, debe coincidir con el total de la reserva", example = "120000")
    private BigDecimal monto;

    @NotNull
    @Schema(description = "Metodo utilizado para pagar", example = "TARJETA")
    private MetodoPago metodoPago;

    @NotNull
    @Schema(description = "ID de la reserva asociada al pago", example = "1")
    private Integer idReserva;
}

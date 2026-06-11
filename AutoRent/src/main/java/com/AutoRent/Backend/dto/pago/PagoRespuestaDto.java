package com.AutoRent.Backend.dto.pago;

import com.AutoRent.Backend.model.enums.EstadoPago;
import com.AutoRent.Backend.model.enums.MetodoPago;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoRespuestaDto {

    private Integer idPago;
    private BigDecimal monto;
    private MetodoPago metodoPago;
    private EstadoPago estado;
    private LocalDateTime fechaPago;
    private Integer idReserva;
}

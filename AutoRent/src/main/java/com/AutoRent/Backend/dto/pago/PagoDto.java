package com.AutoRent.Backend.dto.pago;

import com.AutoRent.Backend.model.enums.MetodoPago;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoDto {

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a cero")
    private BigDecimal monto;

    @NotNull(message = "El metodo de pago es obligatorio")
    private MetodoPago metodoPago;

    @NotNull(message = "La reserva es obligatoria")
    private Integer idReserva;

    @Size(max = 100, message = "El titular no puede superar los 100 caracteres")
    @Pattern(
            regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ]+(?: [A-Za-zÁÉÍÓÚáéíóúÑñ]+)*$",
            message = "El titular de la tarjeta solo puede contener letras y espacios"
    )
    private String titularTarjeta;

    @Pattern(regexp = "^\\d{13,19}$", message = "El numero de tarjeta debe tener entre 13 y 19 numeros")
    private String numeroTarjeta;

    @Pattern(regexp = "^(0[1-9]|1[0-2])/(\\d{2}|\\d{4})$", message = "El vencimiento debe tener formato MM/AA o MM/AAAA")
    private String vencimientoTarjeta;

    @Pattern(regexp = "^\\d{3,4}$", message = "El codigo de seguridad debe tener 3 o 4 numeros")
    private String codigoSeguridad;

    public PagoDto(BigDecimal monto, MetodoPago metodoPago, Integer idReserva) {
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.idReserva = idReserva;
    }
}

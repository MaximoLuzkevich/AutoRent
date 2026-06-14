package com.AutoRent.Backend.exception;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorRespuesta {

    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String mensaje;
    private Map<String, String> detalles;
}

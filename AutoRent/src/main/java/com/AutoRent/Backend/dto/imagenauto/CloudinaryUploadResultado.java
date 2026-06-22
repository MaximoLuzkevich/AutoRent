package com.AutoRent.Backend.dto.imagenauto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloudinaryUploadResultado {

    private String secureUrl;
    private String publicId;
    private String nombreArchivo;
}

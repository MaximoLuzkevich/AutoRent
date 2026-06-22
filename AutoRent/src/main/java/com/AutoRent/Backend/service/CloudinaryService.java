package com.AutoRent.Backend.service;

import com.AutoRent.Backend.dto.imagenauto.CloudinaryUploadResultado;
import com.AutoRent.Backend.exception.ParametroIncorrectoException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${cloudinary.api-key:}")
    private String apiKey;

    @Value("${cloudinary.api-secret:}")
    private String apiSecret;

    public CloudinaryUploadResultado subirImagen(MultipartFile file) {
        validarConfiguracion();
        validarArchivo(file);

        try {
            Map<?, ?> resultado = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "autorent/autos",
                            "resource_type", "image"
                    )
            );

            return new CloudinaryUploadResultado(
                    String.valueOf(resultado.get("secure_url")),
                    String.valueOf(resultado.get("public_id")),
                    file.getOriginalFilename()
            );
        } catch (IOException e) {
            throw new ParametroIncorrectoException("No se pudo subir la imagen a Cloudinary");
        }
    }

    public void eliminarImagen(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            return;
        }

        validarConfiguracion();

        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new ParametroIncorrectoException("No se pudo eliminar la imagen de Cloudinary");
        }
    }

    private void validarConfiguracion() {
        if (cloudName == null || cloudName.isBlank()
                || apiKey == null || apiKey.isBlank()
                || apiSecret == null || apiSecret.isBlank()) {
            throw new ParametroIncorrectoException("Falta configurar Cloudinary");
        }
    }

    private void validarArchivo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ParametroIncorrectoException("Debe seleccionar una imagen");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ParametroIncorrectoException("El archivo debe ser una imagen");
        }
    }
}

package com.AutoRent.Backend.service;

import com.AutoRent.Backend.dto.imagenauto.ImagenAutoDto;
import com.AutoRent.Backend.dto.imagenauto.ImagenAutoRespuestaDto;
import com.AutoRent.Backend.exception.IdNoEncontradoException;
import com.AutoRent.Backend.model.Auto;
import com.AutoRent.Backend.model.ImagenAuto;
import com.AutoRent.Backend.repository.ImagenAutoRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ImagenAutoService {

    private final ImagenAutoRepository imagenAutoRepository;
    private final AutoService autoService;

    public ImagenAutoService(ImagenAutoRepository imagenAutoRepository, AutoService autoService) {
        this.imagenAutoRepository = imagenAutoRepository;
        this.autoService = autoService;
    }

    public ImagenAutoRespuestaDto agregarImagen(Integer idAuto, ImagenAutoDto dto) {
        Auto auto = autoService.obtenerAutoPorId(idAuto);

        ImagenAuto imagen = new ImagenAuto();
        imagen.setNombreArchivo(dto.getNombreArchivo());
        imagen.setUrlImagen(dto.getUrlImagen());
        imagen.setPrincipal(dto.getPrincipal());
        imagen.setAuto(auto);

        return convertirARespuesta(imagenAutoRepository.save(imagen));
    }

    public List<ImagenAutoRespuestaDto> listarImagenesPorAuto(Integer idAuto) {
        autoService.obtenerAutoPorId(idAuto);

        return imagenAutoRepository.findByAutoIdAutoOrderByPrincipalDescFechaCargaAsc(idAuto).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public ImagenAutoRespuestaDto obtenerImagenPrincipal(Integer idAuto) {
        autoService.obtenerAutoPorId(idAuto);

        ImagenAuto imagen = imagenAutoRepository.findByAutoIdAutoAndPrincipalTrue(idAuto)
                .orElseThrow(() -> new IdNoEncontradoException("Imagen principal no encontrada"));

        return convertirARespuesta(imagen);
    }

    public void eliminarImagen(Integer idImagen) {
        ImagenAuto imagen = imagenAutoRepository.findById(idImagen)
                .orElseThrow(() -> new IdNoEncontradoException("Imagen no encontrada"));

        imagenAutoRepository.delete(imagen);
    }

    private ImagenAutoRespuestaDto convertirARespuesta(ImagenAuto imagen) {
        return new ImagenAutoRespuestaDto(
                imagen.getIdImagen(),
                imagen.getNombreArchivo(),
                imagen.getUrlImagen(),
                imagen.getPrincipal(),
                imagen.getFechaCarga(),
                imagen.getAuto().getIdAuto()
        );
    }
}

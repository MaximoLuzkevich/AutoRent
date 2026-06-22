package com.AutoRent.Backend.service;

import lombok.RequiredArgsConstructor;

import com.AutoRent.Backend.dto.imagenauto.CloudinaryUploadResultado;
import com.AutoRent.Backend.dto.imagenauto.ImagenAutoDto;
import com.AutoRent.Backend.dto.imagenauto.ImagenAutoRespuestaDto;
import com.AutoRent.Backend.exception.IdNoEncontradoException;
import com.AutoRent.Backend.exception.ParametroIncorrectoException;
import com.AutoRent.Backend.exception.PermisoInsuficienteException;
import com.AutoRent.Backend.model.Auto;
import com.AutoRent.Backend.model.ImagenAuto;
import com.AutoRent.Backend.model.Usuario;
import com.AutoRent.Backend.model.enums.NombreRol;
import com.AutoRent.Backend.repository.ImagenAutoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImagenAutoService {

    private final ImagenAutoRepository imagenAutoRepository;
    private final AutoService autoService;
    private final UsuarioService usuarioService;
    private final CloudinaryService cloudinaryService;

    public ImagenAutoRespuestaDto agregarImagen(Integer idAuto, ImagenAutoDto dto) {
        Auto auto = autoService.obtenerAutoPorId(idAuto);
        validarPropietarioOAdministrador(auto);

        ImagenAuto imagen = new ImagenAuto();
        imagen.setNombreArchivo(dto.getNombreArchivo());
        imagen.setUrlImagen(dto.getUrlImagen());
        imagen.setPrincipal(dto.getPrincipal());
        imagen.setAuto(auto);

        return convertirARespuesta(imagenAutoRepository.save(imagen));
    }

    public ImagenAutoRespuestaDto subirImagen(Integer idAuto, MultipartFile file, Boolean principal) {
        Auto auto = autoService.obtenerAutoPorId(idAuto);
        validarPropietarioOAdministrador(auto);

        CloudinaryUploadResultado resultado = cloudinaryService.subirImagen(file);

        ImagenAuto imagen = new ImagenAuto();
        imagen.setNombreArchivo(resultado.getNombreArchivo());
        imagen.setUrlImagen(resultado.getSecureUrl());
        imagen.setPublicId(resultado.getPublicId());
        imagen.setPrincipal(principal);
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

    public void eliminarImagen(Integer idAuto, Integer idImagen) {
        Auto auto = autoService.obtenerAutoPorId(idAuto);
        validarPropietarioOAdministrador(auto);

        ImagenAuto imagen = imagenAutoRepository.findById(idImagen)
                .orElseThrow(() -> new IdNoEncontradoException("Imagen no encontrada"));

        if (!imagen.getAuto().getIdAuto().equals(idAuto)) {
            throw new ParametroIncorrectoException("La imagen no pertenece al auto indicado");
        }

        cloudinaryService.eliminarImagen(imagen.getPublicId());
        imagenAutoRepository.delete(imagen);
    }

    private void validarPropietarioOAdministrador(Auto auto) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();

        if (!auto.getPropietario().getIdUsuario().equals(usuario.getIdUsuario())
                && !usuarioService.tieneRol(usuario, NombreRol.ADMINISTRADOR)) {
            throw new PermisoInsuficienteException("No podes modificar imagenes de este auto");
        }
    }

    private ImagenAutoRespuestaDto convertirARespuesta(ImagenAuto imagen) {
        return new ImagenAutoRespuestaDto(
                imagen.getIdImagen(),
                imagen.getNombreArchivo(),
                imagen.getUrlImagen(),
                imagen.getPublicId(),
                imagen.getPrincipal(),
                imagen.getFechaCarga(),
                imagen.getAuto().getIdAuto()
        );
    }
}

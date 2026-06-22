package com.AutoRent.Backend.service;

import lombok.RequiredArgsConstructor;

import com.AutoRent.Backend.dto.categoria.CategoriaAutoRespuestaDto;
import com.AutoRent.Backend.exception.IdNoEncontradoException;
import com.AutoRent.Backend.model.CategoriaAuto;
import com.AutoRent.Backend.model.enums.NombreCategoriaAuto;
import com.AutoRent.Backend.repository.CategoriaAutoRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoriaAutoService {

    private final CategoriaAutoRepository categoriaAutoRepository;

    public List<CategoriaAutoRespuestaDto> listarCategorias() {
        return categoriaAutoRepository.findAll().stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    public CategoriaAutoRespuestaDto buscarPorNombre(NombreCategoriaAuto nombre) {
        CategoriaAuto categoria = obtenerCategoriaPorNombre(nombre);
        return convertirARespuesta(categoria);
    }

    public CategoriaAuto obtenerCategoriaPorNombre(NombreCategoriaAuto nombre) {
        return categoriaAutoRepository.findByNombre(nombre)
                .orElseThrow(() -> new IdNoEncontradoException("Categoria no encontrada"));
    }

    private CategoriaAutoRespuestaDto convertirARespuesta(CategoriaAuto categoria) {
        return new CategoriaAutoRespuestaDto(
                categoria.getIdCategoria(),
                categoria.getNombre(),
                categoria.getDescripcion()
        );
    }
}

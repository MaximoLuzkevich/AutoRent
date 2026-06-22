package com.AutoRent.Backend.service;

import lombok.RequiredArgsConstructor;

import com.AutoRent.Backend.exception.IdNoEncontradoException;
import com.AutoRent.Backend.model.Rol;
import com.AutoRent.Backend.model.enums.NombreRol;
import com.AutoRent.Backend.repository.RolRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;

    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    public Rol buscarPorNombre(NombreRol nombreRol) {
        return rolRepository.findByNombre(nombreRol)
                .orElseThrow(() -> new IdNoEncontradoException("Rol no encontrado"));
    }

    public boolean existeRol(NombreRol nombreRol) {
        return rolRepository.existsByNombre(nombreRol);
    }
}

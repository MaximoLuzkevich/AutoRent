package com.AutoRent.Backend.repository;

import com.AutoRent.Backend.model.Rol;
import com.AutoRent.Backend.model.enums.NombreRol;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

    Optional<Rol> findByNombre(NombreRol nombre);

    boolean existsByNombre(NombreRol nombre);
}

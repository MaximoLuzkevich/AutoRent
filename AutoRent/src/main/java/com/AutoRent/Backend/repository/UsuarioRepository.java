package com.AutoRent.Backend.repository;

import com.AutoRent.Backend.model.Usuario;
import com.AutoRent.Backend.model.enums.NombreRol;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<Usuario> findDistinctByRolesNombreOrderByFechaRegistroDesc(NombreRol rol);

    List<Usuario> findDistinctByRolesNombreAndActivoOrderByFechaRegistroDesc(NombreRol rol, Boolean activo);
}

package com.AutoRent.Backend.repository;

import com.AutoRent.Backend.model.CategoriaAuto;
import com.AutoRent.Backend.model.enums.NombreCategoriaAuto;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaAutoRepository extends JpaRepository<CategoriaAuto, Integer> {

    Optional<CategoriaAuto> findByNombre(NombreCategoriaAuto nombre);

    boolean existsByNombre(NombreCategoriaAuto nombre);
}

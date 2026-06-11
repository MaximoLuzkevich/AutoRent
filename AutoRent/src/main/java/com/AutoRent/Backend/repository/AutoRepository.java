package com.AutoRent.Backend.repository;

import com.AutoRent.Backend.model.Auto;
import com.AutoRent.Backend.model.enums.NombreCategoriaAuto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoRepository extends JpaRepository<Auto, Integer> {

    Optional<Auto> findByPatenteIgnoreCase(String patente);

    boolean existsByPatenteIgnoreCase(String patente);

    List<Auto> findByActivoTrueOrderByFechaPublicacionDesc();

    List<Auto> findByPropietarioIdUsuarioOrderByFechaPublicacionDesc(Integer idPropietario);

    List<Auto> findByCategoriaNombreAndActivoTrue(NombreCategoriaAuto categoria);

    List<Auto> findByMarcaContainingIgnoreCaseAndActivoTrue(String marca);

    List<Auto> findByCiudadContainingIgnoreCaseAndActivoTrue(String ciudad);
}

package com.AutoRent.Backend.repository;

import com.AutoRent.Backend.model.PerfilPropietario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerfilPropietarioRepository extends JpaRepository<PerfilPropietario, Integer> {

    List<PerfilPropietario> findByVerificadoOrderByFechaAltaDesc(Boolean verificado);

    List<PerfilPropietario> findByActivoOrderByFechaAltaDesc(Boolean activo);

    List<PerfilPropietario> findByCiudadContainingIgnoreCaseOrderByFechaAltaDesc(String ciudad);

    List<PerfilPropietario> findByProvinciaContainingIgnoreCaseOrderByFechaAltaDesc(String provincia);

    List<PerfilPropietario> findByUsuarioNombreContainingIgnoreCaseOrderByFechaAltaDesc(String nombre);
}

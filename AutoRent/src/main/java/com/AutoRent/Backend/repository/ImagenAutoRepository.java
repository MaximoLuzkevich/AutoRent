package com.AutoRent.Backend.repository;

import com.AutoRent.Backend.model.ImagenAuto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImagenAutoRepository extends JpaRepository<ImagenAuto, Integer> {

    List<ImagenAuto> findByAutoIdAutoOrderByPrincipalDescFechaCargaAsc(Integer idAuto);

    Optional<ImagenAuto> findByAutoIdAutoAndPrincipalTrue(Integer idAuto);
}

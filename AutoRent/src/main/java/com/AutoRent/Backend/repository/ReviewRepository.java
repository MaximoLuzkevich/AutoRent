package com.AutoRent.Backend.repository;

import com.AutoRent.Backend.model.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findAllByOrderByFechaDesc();

    List<Review> findByAutoIdAutoOrderByFechaDesc(Integer idAuto);

    boolean existsByClienteIdUsuarioAndAutoIdAuto(Integer idCliente, Integer idAuto);
}

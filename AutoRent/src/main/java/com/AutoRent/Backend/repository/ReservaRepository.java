package com.AutoRent.Backend.repository;

import com.AutoRent.Backend.model.Reserva;
import com.AutoRent.Backend.model.enums.EstadoReserva;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    List<Reserva> findByClienteIdUsuarioOrderByFechaInicioDesc(Integer idCliente);

    List<Reserva> findByAutoPropietarioIdUsuarioOrderByFechaInicioDesc(Integer idPropietario);

    List<Reserva> findByAutoPropietarioIdUsuarioAndAutoIdAutoOrderByFechaInicioDesc(
            Integer idPropietario,
            Integer idAuto
    );

    List<Reserva> findByEstadoOrderByFechaReservaDesc(EstadoReserva estado);

    boolean existsByAutoIdAutoAndEstadoInAndFechaInicioBeforeAndFechaFinAfter(
            Integer idAuto,
            Collection<EstadoReserva> estados,
            LocalDate fechaFin,
            LocalDate fechaInicio
    );
}

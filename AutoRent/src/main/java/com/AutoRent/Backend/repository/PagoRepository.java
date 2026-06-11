package com.AutoRent.Backend.repository;

import com.AutoRent.Backend.model.Pago;
import com.AutoRent.Backend.model.enums.EstadoPago;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    List<Pago> findByReservaIdReservaOrderByFechaPagoDesc(Integer idReserva);

    List<Pago> findByReservaClienteIdUsuarioOrderByFechaPagoDesc(Integer idCliente);

    List<Pago> findByReservaAutoPropietarioIdUsuarioOrderByFechaPagoDesc(Integer idPropietario);

    List<Pago> findByEstadoOrderByFechaPagoDesc(EstadoPago estado);

    List<Pago> findByFechaPagoBetweenOrderByFechaPagoDesc(LocalDateTime desde, LocalDateTime hasta);
}

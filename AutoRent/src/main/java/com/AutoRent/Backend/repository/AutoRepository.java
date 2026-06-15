package com.AutoRent.Backend.repository;

import com.AutoRent.Backend.model.Auto;
import com.AutoRent.Backend.model.enums.EstadoReserva;
import com.AutoRent.Backend.model.enums.NombreCategoriaAuto;
import com.AutoRent.Backend.model.enums.TipoCombustible;
import com.AutoRent.Backend.model.enums.TipoTransmision;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoRepository extends JpaRepository<Auto, Integer> {

    Optional<Auto> findByPatenteIgnoreCase(String patente);

    boolean existsByPatenteIgnoreCase(String patente);

    List<Auto> findByActivoTrueOrderByFechaPublicacionDesc();

    List<Auto> findByPropietarioIdUsuarioOrderByFechaPublicacionDesc(Integer idPropietario);

    List<Auto> findByPropietarioIdUsuarioAndActivoOrderByFechaPublicacionDesc(Integer idPropietario, Boolean activo);

    List<Auto> findByPropietarioIdUsuarioAndCategoriaNombreOrderByFechaPublicacionDesc(
            Integer idPropietario,
            NombreCategoriaAuto categoria
    );

    List<Auto> findByCategoriaNombreAndActivoTrue(NombreCategoriaAuto categoria);

    List<Auto> findByMarcaContainingIgnoreCaseAndActivoTrue(String marca);

    List<Auto> findByCiudadContainingIgnoreCaseAndActivoTrue(String ciudad);

    @Query("""
            select a from Auto a
            where a.activo = true
            and (:ciudad is null or lower(a.ciudad) like lower(concat('%', :ciudad, '%')))
            and (:marca is null or lower(a.marca) like lower(concat('%', :marca, '%')))
            and (:categoria is null or a.categoria.nombre = :categoria)
            and (:precioMax is null or a.precioDia <= :precioMax)
            and (:pasajeros is null or a.capacidadPasajeros >= :pasajeros)
            and (:transmision is null or a.transmision = :transmision)
            and (:combustible is null or a.combustible = :combustible)
            order by a.fechaPublicacion desc
            """)
    List<Auto> buscarConFiltros(
            @Param("ciudad") String ciudad,
            @Param("marca") String marca,
            @Param("categoria") NombreCategoriaAuto categoria,
            @Param("precioMax") BigDecimal precioMax,
            @Param("pasajeros") Integer pasajeros,
            @Param("transmision") TipoTransmision transmision,
            @Param("combustible") TipoCombustible combustible
    );

    @Query("""
            select a from Auto a
            where a.activo = true
            and lower(a.ciudad) like lower(concat('%', :ciudad, '%'))
            and (:marca is null or lower(a.marca) like lower(concat('%', :marca, '%')))
            and (:categoria is null or a.categoria.nombre = :categoria)
            and (:precioMax is null or a.precioDia <= :precioMax)
            and (:pasajeros is null or a.capacidadPasajeros >= :pasajeros)
            and (:transmision is null or a.transmision = :transmision)
            and (:combustible is null or a.combustible = :combustible)
            and not exists (
                select r from Reserva r
                where r.auto = a
                and r.estado in :estadosOcupados
                and r.fechaInicio < :fechaFin
                and r.fechaFin > :fechaInicio
            )
            order by a.fechaPublicacion desc
            """)
    List<Auto> buscarDisponibles(
            @Param("ciudad") String ciudad,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("estadosOcupados") Collection<EstadoReserva> estadosOcupados,
            @Param("marca") String marca,
            @Param("categoria") NombreCategoriaAuto categoria,
            @Param("precioMax") BigDecimal precioMax,
            @Param("pasajeros") Integer pasajeros,
            @Param("transmision") TipoTransmision transmision,
            @Param("combustible") TipoCombustible combustible
    );
}

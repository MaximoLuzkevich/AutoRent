package com.AutoRent.Backend.model;

import com.AutoRent.Backend.model.enums.TipoCombustible;
import com.AutoRent.Backend.model.enums.TipoTransmision;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

@Entity
@Table(name = "auto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Auto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auto")
    @EqualsAndHashCode.Include
    private Integer idAuto;

    @NotBlank
    @Size(max = 100)
    @Column(name = "marca", nullable = false, length = 100)
    private String marca;

    @NotBlank
    @Size(max = 100)
    @Column(name = "modelo", nullable = false, length = 100)
    private String modelo;

    @NotNull
    @Column(name = "anio", nullable = false)
    private Integer anio;

    @NotBlank
    @Size(max = 20)
    @Column(name = "patente", nullable = false, unique = true, length = 20)
    private String patente;

    @Size(max = 50)
    @Column(name = "color", length = 50)
    private String color;

    @NotNull
    @Min(1)
    @Column(name = "capacidad_pasajeros", nullable = false)
    private Integer capacidadPasajeros;

    @NotNull
    @Min(1)
    @Column(name = "cantidad_puertas", nullable = false)
    private Integer cantidadPuertas;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "transmision", nullable = false)
    private TipoTransmision transmision;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "combustible", nullable = false)
    private TipoCombustible combustible;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "precio_dia", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioDia;

    @Lob
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @NotBlank
    @Size(max = 100)
    @Column(name = "ciudad", nullable = false, length = 100)
    private String ciudad;

    @Size(max = 100)
    @Column(name = "provincia", length = 100)
    private String provincia;

    @NotBlank
    @Size(max = 150)
    @Column(name = "direccion_retiro", nullable = false, length = 150)
    private String direccionRetiro;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Column(name = "fecha_publicacion", nullable = false)
    private LocalDateTime fechaPublicacion;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_propietario", nullable = false)
    private Usuario propietario;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private CategoriaAuto categoria;

    @PrePersist
    private void prePersist() {
        if (activo == null) {
            activo = true;
        }
        if (fechaPublicacion == null) {
            fechaPublicacion = LocalDateTime.now();
        }
    }
}

package com.AutoRent.Backend.model;

import com.AutoRent.Backend.model.enums.NombreCategoriaAuto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categoria_auto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CategoriaAuto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    @EqualsAndHashCode.Include
    private Integer idCategoria;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "nombre", nullable = false, unique = true)
    private NombreCategoriaAuto nombre;

    @Lob
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
}

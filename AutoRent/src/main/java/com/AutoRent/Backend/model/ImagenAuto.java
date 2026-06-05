package com.AutoRent.Backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "imagen_auto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ImagenAuto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imagen")
    @EqualsAndHashCode.Include
    private Integer idImagen;

    @NotBlank
    @Size(max = 255)
    @Column(name = "nombre_archivo", nullable = false, length = 255)
    private String nombreArchivo;

    @NotBlank
    @Size(max = 500)
    @Column(name = "url_imagen", nullable = false, length = 500)
    private String urlImagen;

    @Column(name = "principal", nullable = false)
    private Boolean principal;

    @Column(name = "fecha_carga", nullable = false)
    private LocalDateTime fechaCarga;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_auto", nullable = false)
    private Auto auto;

    @PrePersist
    private void prePersist() {
        if (principal == null) {
            principal = false;
        }
        if (fechaCarga == null) {
            fechaCarga = LocalDateTime.now();
        }
    }
}

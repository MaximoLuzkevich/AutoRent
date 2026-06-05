package com.AutoRent.Backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "perfil_propietario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PerfilPropietario {

    @Id
    @Column(name = "id_usuario")
    @EqualsAndHashCode.Include
    private Integer idUsuario;

    @Size(max = 30)
    @Column(name = "dni", length = 30)
    private String dni;

    @Size(max = 30)
    @Column(name = "cuit", length = 30)
    private String cuit;

    @Size(max = 150)
    @Column(name = "direccion", length = 150)
    private String direccion;

    @Size(max = 100)
    @Column(name = "ciudad", length = 100)
    private String ciudad;

    @Size(max = 100)
    @Column(name = "provincia", length = 100)
    private String provincia;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDateTime fechaAlta;

    @Column(name = "verificado", nullable = false)
    private Boolean verificado;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @PrePersist
    private void prePersist() {
        if (fechaAlta == null) {
            fechaAlta = LocalDateTime.now();
        }
        if (verificado == null) {
            verificado = false;
        }
    }
}

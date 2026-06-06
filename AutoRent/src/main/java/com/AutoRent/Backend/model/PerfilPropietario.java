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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "perfil_propietario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilPropietario {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Size(max = 30)
    @Column(length = 30)
    private String dni;

    @Size(max = 30)
    @Column(length = 30)
    private String cuit;

    @Size(max = 150)
    @Column(length = 150)
    private String direccion;

    @Size(max = 100)
    @Column(length = 100)
    private String ciudad;

    @Size(max = 100)
    @Column(length = 100)
    private String provincia;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDateTime fechaAlta;

    @Column(nullable = false)
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

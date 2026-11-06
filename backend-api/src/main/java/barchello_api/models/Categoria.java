package barchello_api.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Categoria")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCategoria;

    private String nombre;
    private String descripcion;
    private String usuarioCreacion; // Agregado

    // Esto se ejecuta justo ANTES de guardar en la BD
    @PrePersist
    public void prePersist() {
        if (this.usuarioCreacion == null) {
            this.usuarioCreacion = "Sistema";
        }
    }
}
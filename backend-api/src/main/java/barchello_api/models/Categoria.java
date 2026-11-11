package barchello_api.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "categorias") // Forzamos plural y minúscula
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria") // Snake_case para la PK
    private Integer idCategoria;

    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "usuario_creacion")
    private String usuarioCreacion;

    // Esto se ejecuta justo ANTES de guardar en la BD
    @PrePersist
    public void prePersist() {
        if (this.usuarioCreacion == null) {
            this.usuarioCreacion = "Sistema";
        }
    }
}
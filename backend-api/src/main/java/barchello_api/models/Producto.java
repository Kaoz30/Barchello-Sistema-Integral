package barchello_api.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(name = "precio_venta") // Mapeo crítico
    private Double precioVenta;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "estado")
    private String estado;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(name = "es_bebida")
    private Boolean esBebida = false; // Por defecto false

    @Column(name = "stock_minimo")
    private Integer stockMinimo = 0;  // Por defecto 0

    @Column(name = "usuario_creacion")
    private String usuarioCreacion;   // Agregado

    @ManyToOne
    @JoinColumn(name = "id_categoria") // Llave foránea
    private Categoria categoria;

    @PrePersist
    public void prePersist() {
        if (this.usuarioCreacion == null) this.usuarioCreacion = "Sistema";
        if (this.esBebida == null) this.esBebida = false;
        if (this.stockMinimo == null) this.stockMinimo = 0;
    }
}
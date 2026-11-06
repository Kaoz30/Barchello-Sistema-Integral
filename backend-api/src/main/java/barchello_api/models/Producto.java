package barchello_api.models;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "Producto")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProducto;

    private String nombre;
    private String descripcion;
    private BigDecimal precioVenta;
    private String estado;
    private String imagenUrl;
    private Boolean esBebida = false; // Por defecto false
    private Integer stockMinimo = 0;  // Por defecto 0
    private String usuarioCreacion;   // Agregado

    @ManyToOne
    @JoinColumn(name = "idCategoria")
    private Categoria categoria;

    @PrePersist
    public void prePersist() {
        if (this.usuarioCreacion == null) this.usuarioCreacion = "Sistema";
        if (this.esBebida == null) this.esBebida = false;
        if (this.stockMinimo == null) this.stockMinimo = 0;
    }
}
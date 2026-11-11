package barchello_api.models;

import com.fasterxml.jackson.annotation.JsonIgnore; // Para ocultar la contraseña al hacer un GET
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "rol")
    private String rol;

    @Column(name = "usuario_login", unique = true)
    private String usuarioLogin;

    @JsonIgnore // <-- SEGURIDAD: Oculta la contraseña en el JSON de respuesta
    @Column(name = "contrasena")
    private String contrasena;

    @Column(name = "estado")
    private String estado;

    @Column(name = "usuario_creacion")
    private String usuarioCreacion;

    @PrePersist
    public void prePersist() {
        if (this.usuarioCreacion == null) this.usuarioCreacion = "Sistema";
        if (this.estado == null) this.estado = "activo";
    }
}
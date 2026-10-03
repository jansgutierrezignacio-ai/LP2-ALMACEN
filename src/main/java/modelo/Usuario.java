package modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "usuario") // Asegúrate de que el nombre coincida con tu tabla en MySQL
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUsuario")
    private int idUsuario;
    
    @Column(name = "idTipo")
    private int idTipo;
    
    @Column(name = "username", length = 50, nullable = false)
    private String username;
    
    @Column(name = "clave", length = 100, nullable = false)
    private String clave;
    
    @Transient // Permite usarlo en joins o listados sin buscarlo como columna física en la tabla
    private String nombreTipo;
    
    @Column(name = "nombres", length = 100)
    private String nombres;
    
    @Column(name = "apellidos", length = 100)
    private String apellidos; //nuevo

    public Usuario() {
    }

    public Usuario(int idUsuario, int idTipo, String username, String clave, String nombreTipo,
            String nombres, String apellidos) {
        this.idUsuario = idUsuario;
        this.idTipo = idTipo;
        this.username = username;
        this.clave = clave;
        this.nombreTipo = nombreTipo;
        this.nombres = nombres;
        this.apellidos = apellidos;
    }
    
    public Usuario(int idTipo, String username, String clave) {
        this.idTipo = idTipo;
        this.username = username;
        this.clave = clave;
    }

    public Usuario(int idUsuario, int idTipo, String username, String clave, String nombreTipo) {
        this.idUsuario = idUsuario;
        this.idTipo = idTipo;
        this.username = username;
        this.clave = clave;
        this.nombreTipo = nombreTipo;
    }
    
    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(int idTipo) {
        this.idTipo = idTipo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }
}
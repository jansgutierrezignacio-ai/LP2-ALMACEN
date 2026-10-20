package com.autopartes.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "Usuarios") 
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUsuario")
    private Integer idUsuario;
    
    @Column(name = "idTipo", nullable = false)
    private Integer idTipo;
    
    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;
    
    @Column(name = "clave", length = 100, nullable = false)
    private String clave;
    
    @Column(name = "nombres", length = 50)
    private String nombres;
    
    @Column(name = "apellidos", length = 50)
    private String apellidos;
    
    @Transient // Permite usarlo en consultas con JOIN sin buscarlo como columna física en la tabla
    private String nombreTipo;
    
    public Usuario() {
    }

    public Usuario(Integer idUsuario, Integer idTipo, String username, String clave, 
            String nombres, String apellidos, String nombreTipo) {
        this.idUsuario = idUsuario;
        this.idTipo = idTipo;
        this.username = username;
        this.clave = clave;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.nombreTipo = nombreTipo;
    }
    
    public Usuario(Integer idTipo, String username, String clave) {
        this.idTipo = idTipo;
        this.username = username;
        this.clave = clave;
    }

    public Usuario(Integer idUsuario, Integer idTipo, String username, String clave, String nombreTipo) {
        this.idUsuario = idUsuario;
        this.idTipo = idTipo;
        this.username = username;
        this.clave = clave;
        this.nombreTipo = nombreTipo;
    }
    
    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(Integer idTipo) {
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

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    @Override
    public String toString() {
        return "Usuario [idUsuario=" + idUsuario 
                + ", idTipo=" + idTipo 
                + ", username=" + username 
                + ", nombres=" + nombres 
                + ", apellidos=" + apellidos 
                + ", nombreTipo=" + nombreTipo + "]";
    }
}
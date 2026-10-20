package com.autopartes.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "proveedor")
public class Proveedor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Integer idProveedor;
    
    @Column(name = "ruc", length = 11, nullable = false, unique = true)
    private String ruc;
    
    @Column(name = "razon_social", length = 150, nullable = false)
    private String razonSocial;
    
    @Column(name = "telefono", length = 15)
    private String telefono;
    
    @Column(name = "estado", nullable = false)
    private Integer estado;
    
    public Proveedor() {
    }

    public Proveedor(Integer idProveedor, String ruc, String razonSocial, String telefono, Integer estado) {
        this.idProveedor = idProveedor;
        this.ruc = ruc;
        this.razonSocial = razonSocial;
        this.telefono = telefono;
        this.estado = estado;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Proveedor [idProveedor=" + idProveedor 
                + ", ruc=" + ruc 
                + ", razonSocial=" + razonSocial 
                + ", telefono=" + telefono 
                + ", estado=" + estado + "]";
    }
}
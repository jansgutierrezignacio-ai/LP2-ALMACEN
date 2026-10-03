package modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "proveedor")
public class Proveedor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private int id_proveedor;
    
    @Column(name = "ruc", length = 20)
    private String ruc;
    
    @Column(name = "razon_social", length = 150)
    private String razon_social;
    
    @Column(name = "telefono", length = 20)
    private String telefono;
    
    @Column(name = "estado")
    private int estado;
    
    public Proveedor() {
    }

    public Proveedor(int id_proveedor, String ruc, String razon_social, String telefono, int estado) {
        this.id_proveedor = id_proveedor;
        this.ruc = ruc;
        this.razon_social = razon_social;
        this.telefono = telefono;
        this.estado = estado;
    }

    public int getId_proveedor() {
        return id_proveedor;
    }

    public void setId_proveedor(int id_proveedor) {
        this.id_proveedor = id_proveedor;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getRazon_social() {
        return razon_social;
    }

    public void setRazon_social(String razon_social) {
        this.razon_social = razon_social;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
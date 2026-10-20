package com.autopartes.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "producto")
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;
    
    @Column(name = "codigo", length = 20, nullable = false, unique = true)
    private String codigo;
    
    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;
    
    @Column(name = "descripcion", length = 200, nullable = false)
    private String descripcion;
    
    @Column(name = "precio", nullable = false)
    private Double precio;
    
    @Column(name = "stock", nullable = false)
    private Integer stock;
    
    @Column(name = "id_proveedor", nullable = false)
    private Integer idProveedor;
    
    @Transient // Permite mapear dinámicamente el nombre del proveedor en las consultas con JOIN sin afectar la BD
    private String nombreProveedor;
    
    public Producto() {
    }

    public Producto(Integer idProducto, String codigo, String nombre, String descripcion,
            Double precio, Integer stock, Integer idProveedor, String nombreProveedor) {
        this.idProducto = idProducto;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.idProveedor = idProveedor;
        this.nombreProveedor = nombreProveedor;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    @Override
    public String toString() {
        return "Producto [idProducto=" + idProducto 
                + ", codigo=" + codigo 
                + ", nombre=" + nombre 
                + ", descripcion=" + descripcion 
                + ", precio=" + precio 
                + ", stock=" + stock 
                + ", idProveedor=" + idProveedor 
                + ", nombreProveedor=" + nombreProveedor + "]";
    }
}

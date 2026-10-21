package com.autopartes.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reporte") 
public class Reporte {

    @Id
    @Column(name = "id_venta")
    private Integer idVenta;
    
    @Column(name = "id_cliente")
    private Integer idCliente;   
    
    @Column(name = "id_producto")
    private Integer idProducto;
    
    @Column(name = "nombre_cliente", length = 100)
    private String nombreCliente;
    
    @Column(name = "nombre_producto", length = 100)
    private String nombreProducto;
    
    @Column(name = "fecha", length = 50)
    private String fecha;

    @Column(name = "cantidad")
    private Integer cantidad;
    
    @Column(name = "precio_unit")
    private Double precioUnit;
    
    @Column(name = "total")
    private Double total;

    public Reporte() {
    }

    public Reporte(Integer idVenta, Integer idCliente, Integer idProducto, String nombreCliente, 
            String nombreProducto, String fecha, Integer cantidad, Double precioUnit, Double total) {
        this.idVenta = idVenta;
        this.idCliente = idCliente;
        this.idProducto = idProducto;
        this.nombreCliente = nombreCliente;
        this.nombreProducto = nombreProducto;
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.precioUnit = precioUnit;
        this.total = total;
    }

    public Integer getIdVenta() { 
        return idVenta; 
    }
    
    public void setIdVenta(Integer idVenta) { 
        this.idVenta = idVenta; 
    }

    public Integer getIdCliente() { 
        return idCliente; 
    }
    
    public void setIdCliente(Integer idCliente) { 
        this.idCliente = idCliente; 
    }

    public Integer getIdProducto() { 
        return idProducto; 
    }
    
    public void setIdProducto(Integer idProducto) { 
        this.idProducto = idProducto; 
    }

    public String getNombreCliente() { 
        return nombreCliente; 
    }
    
    public void setNombreCliente(String nombreCliente) { 
        this.nombreCliente = nombreCliente; 
    }

    public String getNombreProducto() { 
        return nombreProducto; 
    }
    
    public void setNombreProducto(String nombreProducto) { 
        this.nombreProducto = nombreProducto; 
    }

    public String getFecha() { 
        return fecha; 
    }
    
    public void setFecha(String fecha) { 
        this.fecha = fecha; 
    }

    public Integer getCantidad() { 
        return cantidad; 
    }
    
    public void setCantidad(Integer cantidad) { 
        this.cantidad = cantidad; 
    }

    public Double getPrecioUnit() { 
        return precioUnit; 
    }
    
    public void setPrecioUnit(Double precioUnit) { 
        this.precioUnit = precioUnit; 
    }

    public Double getTotal() { 
        return total; 
    }
    
    public void setTotal(Double total) { 
        this.total = total; 
    }

    @Override
    public String toString() {
        return "Reporte [idVenta=" + idVenta 
                + ", idCliente=" + idCliente 
                + ", idProducto=" + idProducto 
                + ", nombreCliente=" + nombreCliente 
                + ", nombreProducto=" + nombreProducto 
                + ", fecha=" + fecha 
                + ", cantidad=" + cantidad 
                + ", precioUnit=" + precioUnit 
                + ", total=" + total + "]";
    }
}
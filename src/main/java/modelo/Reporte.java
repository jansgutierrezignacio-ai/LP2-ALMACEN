package modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "reporte") // Ajusta el nombre si apunta a una vista o tabla específica en tu BD
public class Reporte {

    @Id
    @Column(name = "idVenta")
    private int idVenta;
    
    @Column(name = "idCliente")
    private int idCliente;  
    
    @Column(name = "idProducto")
    private int idProducto;
    
    @Column(name = "nombreCliente", length = 100)
    private String nombreCliente;
    
    @Column(name = "nombreProducto", length = 100)
    private String nombreProducto;
    
    @Column(name = "fecha", length = 50)
    private String fecha;

    @Column(name = "cantidad")
    private int cantidad;
    
    @Column(name = "precioUnit")
    private double precioUnit;
    
    @Column(name = "total")
    private double total;

    public Reporte() {
    }

    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecioUnit() { return precioUnit; }
    public void setPrecioUnit(double precioUnit) { this.precioUnit = precioUnit; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
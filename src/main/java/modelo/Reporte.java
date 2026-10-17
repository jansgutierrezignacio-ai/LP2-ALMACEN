package modelo;

/**
 * Objeto de transferencia (DTO) que representa una fila de los reportes de venta.
 * NO es una entidad JPA: sus datos no provienen de una tabla, sino del conjunto
 * de resultados que devuelven los procedimientos sp_ReporteVentasPorFechas y
 * sp_ReporteDetalleVenta. Por eso la clase no lleva anotaciones de persistencia.
 */
public class Reporte {

    private int idVenta;
    private int idCliente;
    private int idProducto;
    private String nombreCliente;
    private String nombreProducto;
    private String fecha;
    private int cantidad;
    private double precioUnit;
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

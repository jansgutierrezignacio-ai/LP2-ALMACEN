package modelo;

/**
 * Fila del historial de ventas devuelta por {@code sp_listarVentas}.
 * Es un DTO (sin anotaciones de persistencia), igual que {@code Reporte}:
 * los datos vienen de una consulta nativa que ya trae cliente y usuario unidos.
 */
public class Venta {

    private int idVenta;
    private String fecha;
    private String nombreCliente;
    private String username;
    private double total;

    public Venta() {
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}

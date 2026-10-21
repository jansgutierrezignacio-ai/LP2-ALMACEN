package modelo;

/**
 * Elemento del carrito de venta mientras el usuario arma la venta en la
 * pantalla de punto de venta. Se guarda en la sesión HTTP y NO es una
 * entidad: la persistencia ocurre recién al confirmar la venta
 * (Venta + DetalleVenta vía sp_registrarVenta / sp_registrarDetalleVenta).
 */
public class ItemCarrito {

    private int idProducto;
    private String codigo;
    private String nombre;
    private int cantidad;
    private double precioUnitario;

    public ItemCarrito() {
    }

    public ItemCarrito(int idProducto, String codigo, String nombre, int cantidad, double precioUnitario) {
        this.idProducto = idProducto;
        this.codigo = codigo;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public double getSubtotal() {
        return cantidad * precioUnitario;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
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

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}

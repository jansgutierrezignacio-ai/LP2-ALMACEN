package interfaces;

import java.util.List;

import modelo.ItemCarrito;
import modelo.Venta;

public interface IntVenta {

    /**
     * Confirma una venta completa: cabecera (Venta), una fila por item
     * (DetalleVenta) y descuento de stock de cada producto, todo en una
     * única transacción. Devuelve el id de la venta registrada.
     */
    public int confirmarVenta(List<ItemCarrito> carrito, int idCliente, int idUsuario);

    public List<Venta> listarVentas();
}

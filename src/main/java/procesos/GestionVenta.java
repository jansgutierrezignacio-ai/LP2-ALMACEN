package procesos;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import interfaces.IntVenta;
import modelo.ItemCarrito;
import modelo.Venta;

/**
 * Módulo de ventas (punto de venta).
 *
 * Toda la confirmación de una venta transcurre en un único contexto
 * transaccional ({@code @Transactional} en la clase): cabecera, detalle y
 * descuento de stock se ejecutan como un todo — si cualquiera de los
 * procedimientos falla, todo se revierte (no quedan ventas a medias).
 */
@Service
@Transactional
public class GestionVenta implements IntVenta {

    @PersistenceContext
    private EntityManager em;

    @Override
    public int confirmarVenta(List<ItemCarrito> carrito, int idCliente, int idUsuario) {
        double total = 0;
        for (ItemCarrito item : carrito) {
            total += item.getSubtotal();
        }

        // 1) Cabecera: sp_registrarVenta inserta y devuelve el id generado.
        StoredProcedureQuery qVenta = em.createStoredProcedureQuery("sp_registrarVenta");
        qVenta.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        qVenta.registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN);
        qVenta.registerStoredProcedureParameter(3, Double.class, ParameterMode.IN);
        qVenta.setParameter(1, idUsuario);
        qVenta.setParameter(2, idCliente);
        qVenta.setParameter(3, total);
        List<?> filas = qVenta.getResultList();
        if (filas.isEmpty()) {
            throw new IllegalStateException("sp_registrarVenta no devolvió el id de la venta.");
        }
        Object primero = filas.get(0);
        Object valor = (primero instanceof Object[]) ? ((Object[]) primero)[0] : primero;
        int idVenta = ((Number) valor).intValue();

        // 2) Por cada item del carrito: guardar el detalle y descontar el stock.
        for (ItemCarrito item : carrito) {
            StoredProcedureQuery qDetalle = em.createStoredProcedureQuery("sp_registrarDetalleVenta");
            qDetalle.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
            qDetalle.registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN);
            qDetalle.registerStoredProcedureParameter(3, Integer.class, ParameterMode.IN);
            qDetalle.registerStoredProcedureParameter(4, Double.class, ParameterMode.IN);
            qDetalle.registerStoredProcedureParameter(5, Double.class, ParameterMode.IN);
            qDetalle.setParameter(1, idVenta);
            qDetalle.setParameter(2, item.getIdProducto());
            qDetalle.setParameter(3, item.getCantidad());
            qDetalle.setParameter(4, item.getPrecioUnitario());
            qDetalle.setParameter(5, item.getSubtotal());
            qDetalle.executeUpdate();

            StoredProcedureQuery qStock = em.createStoredProcedureQuery("sp_reducirStockProducto");
            qStock.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
            qStock.registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN);
            qStock.setParameter(1, item.getIdProducto());
            qStock.setParameter(2, item.getCantidad());
            qStock.executeUpdate();
        }

        return idVenta;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Venta> listarVentas() {
        List<Venta> lista = new ArrayList<>();
        List<Object[]> filas = em.createStoredProcedureQuery("sp_listarVentas").getResultList();
        for (Object[] f : filas) {
            Venta v = new Venta();
            v.setIdVenta(((Number) f[0]).intValue());
            v.setFecha(f[1] == null ? null : String.valueOf(f[1]));
            v.setNombreCliente(f[2] == null ? null : String.valueOf(f[2]));
            v.setUsername(f[3] == null ? null : String.valueOf(f[3]));
            v.setTotal(f[4] == null ? 0 : ((Number) f[4]).doubleValue());
            lista.add(v);
        }
        return lista;
    }
}

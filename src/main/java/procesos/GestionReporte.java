package procesos;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import interfaces.IntReporte;
import modelo.Reporte;

@Service
@Transactional
public class GestionReporte implements IntReporte {

    @PersistenceContext
    private EntityManager em;

    @Override
    @SuppressWarnings("unchecked")
    public List<Reporte> listarReportePorFechas(String inicio, String fin) {
        List<Reporte> lista = new ArrayList<>();
        if (inicio == null || inicio.trim().isEmpty() || fin == null || fin.trim().isEmpty()) {
            return lista; // Hibernate no puede pasar NULL a un parámetro IN sin configuración extra.
        }
        StoredProcedureQuery q = em.createStoredProcedureQuery("sp_ReporteVentasPorFechas");
        q.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
        q.setParameter(1, inicio);
        q.setParameter(2, fin);
        // sp_ReporteVentasPorFechas: id_venta, fecha, cliente, total
        List<Object[]> filas = q.getResultList();
        for (Object[] f : filas) {
            Reporte r = new Reporte();
            r.setIdVenta(((Number) f[0]).intValue());
            r.setFecha(f[1] == null ? null : String.valueOf(f[1]));
            r.setNombreCliente(f[2] == null ? null : String.valueOf(f[2]));
            r.setTotal(f[3] == null ? 0 : ((Number) f[3]).doubleValue());
            lista.add(r);
        }
        return lista;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Reporte> obtenerDetalleVenta(int idVenta) {
        List<Reporte> lista = new ArrayList<>();
        StoredProcedureQuery q = em.createStoredProcedureQuery("sp_ReporteDetalleVenta");
        q.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        q.setParameter(1, idVenta);
        // sp_ReporteDetalleVenta: ITEM, PRODUCTO, CANTIDAD, "PRECIO UNIT.", TOTAL
        List<Object[]> filas = q.getResultList();
        for (Object[] f : filas) {
            Reporte r = new Reporte();
            r.setNombreProducto(f[1] == null ? null : String.valueOf(f[1]));
            r.setCantidad(f[2] == null ? 0 : ((Number) f[2]).intValue());
            r.setPrecioUnit(f[3] == null ? 0 : ((Number) f[3]).doubleValue());
            r.setTotal(f[4] == null ? 0 : ((Number) f[4]).doubleValue());
            lista.add(r);
        }
        return lista;
    }

    @Override
    public int contarReporteVenta() {
        // sp_contarVentas devuelve un único valor (COUNT); Hibernate lo entrega como
        // escalar, no como fila Object[]. Se manejan ambos formatos.
        List<?> filas = em.createStoredProcedureQuery("sp_contarVentas").getResultList();
        if (filas.isEmpty()) {
            return 0;
        }
        Object primero = filas.get(0);
        Object valor = (primero instanceof Object[]) ? ((Object[]) primero)[0] : primero;
        return valor == null ? 0 : ((Number) valor).intValue();
    }
}

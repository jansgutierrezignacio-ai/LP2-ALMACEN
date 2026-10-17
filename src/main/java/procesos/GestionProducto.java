package procesos;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import interfaces.IntProducto;
import modelo.Producto;

@Service
@Transactional
public class GestionProducto implements IntProducto {

    @PersistenceContext
    private EntityManager em;

    @Override
    public int crearProducto(Producto p) {
        StoredProcedureQuery q = em.createStoredProcedureQuery("sp_registrarProducto");
        q.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(4, Double.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(5, Integer.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(6, Integer.class, ParameterMode.IN);
        q.setParameter(1, p.getCodigo());
        q.setParameter(2, p.getNombre());
        q.setParameter(3, p.getDescripcion());
        q.setParameter(4, p.getPrecio());
        q.setParameter(5, p.getStock());
        q.setParameter(6, p.getIdProveedor());
        return q.executeUpdate();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Producto> listarProducto() {
        List<Producto> lista = new ArrayList<>();
        List<Object[]> filas = em.createStoredProcedureQuery("sp_listarProducto").getResultList();
        for (Object[] f : filas) {
            lista.add(mapear(f));
        }
        return lista;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Producto obtenerProducto(int id) {
        StoredProcedureQuery q = em.createStoredProcedureQuery("sp_obtenerProducto");
        q.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        q.setParameter(1, id);
        List<Object[]> filas = q.getResultList();
        return filas.isEmpty() ? null : mapear(filas.get(0));
    }

    @Override
    public int eliminarProducto(int id) {
        StoredProcedureQuery q = em.createStoredProcedureQuery("sp_eliminarProducto");
        q.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        q.setParameter(1, id);
        return q.executeUpdate();
    }

    @Override
    public int editarProducto(Producto p) {
        StoredProcedureQuery q = em.createStoredProcedureQuery("sp_actualizarProducto");
        q.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(4, String.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(5, Double.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(6, Integer.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(7, Integer.class, ParameterMode.IN);
        q.setParameter(1, p.getIdProducto());
        q.setParameter(2, p.getCodigo());
        q.setParameter(3, p.getNombre());
        q.setParameter(4, p.getDescripcion());
        q.setParameter(5, p.getPrecio());
        q.setParameter(6, p.getStock());
        q.setParameter(7, p.getIdProveedor());
        return q.executeUpdate();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Producto> buscarProducto(String codigo) {
        List<Producto> lista = new ArrayList<>();
        StoredProcedureQuery q = em.createStoredProcedureQuery("sp_buscarProducto");
        q.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        q.setParameter(1, codigo);
        List<Object[]> filas = q.getResultList();
        for (Object[] f : filas) {
            lista.add(mapear(f));
        }
        return lista;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Producto> buscarProductoByNombre(String texto) {
        List<Producto> lista = new ArrayList<>();
        StoredProcedureQuery q = em.createStoredProcedureQuery("sp_buscarProductoByNombre");
        q.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        q.setParameter(1, texto);
        List<Object[]> filas = q.getResultList();
        for (Object[] f : filas) {
            lista.add(mapear(f));
        }
        return lista;
    }

    // sp_listarProducto / sp_obtenerProducto / sp_buscarProducto(ByNombre):
    // id_producto, codigo, nombre, descripcion, precio, stock, id_proveedor, razon_social
    private Producto mapear(Object[] f) {
        Producto p = new Producto();
        p.setIdProducto(((Number) f[0]).intValue());
        p.setCodigo(f[1] == null ? null : String.valueOf(f[1]));
        p.setNombre(f[2] == null ? null : String.valueOf(f[2]));
        p.setDescripcion(f[3] == null ? null : String.valueOf(f[3]));
        p.setPrecio(f[4] == null ? 0 : ((Number) f[4]).doubleValue());
        p.setStock(f[5] == null ? 0 : ((Number) f[5]).intValue());
        p.setIdProveedor(f[6] == null ? 0 : ((Number) f[6]).intValue());
        p.setNombreProveedor(f.length > 7 && f[7] != null ? String.valueOf(f[7]) : null);
        return p;
    }
}

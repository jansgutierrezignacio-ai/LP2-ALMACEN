package procesos;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import interfaces.IntProveedor;
import modelo.Proveedor;

@Service
@Transactional
public class GestionProveedor implements IntProveedor {

    @PersistenceContext
    private EntityManager em;

    @Override
    public int crearProveedor(Proveedor p) {
        StoredProcedureQuery q = em.createStoredProcedureQuery("sp_registrarProveedor");
        q.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(4, Integer.class, ParameterMode.IN);
        q.setParameter(1, p.getRuc());
        q.setParameter(2, p.getRazon_social());
        q.setParameter(3, p.getTelefono());
        q.setParameter(4, p.getEstado());
        return q.executeUpdate();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Proveedor> listarProveedor() {
        List<Proveedor> lista = new ArrayList<>();
        List<Object[]> filas = em.createStoredProcedureQuery("sp_listarProveedor").getResultList();
        for (Object[] f : filas) {
            lista.add(mapear(f));
        }
        return lista;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Proveedor obtenerProveedor(int id) {
        StoredProcedureQuery q = em.createStoredProcedureQuery("sp_obtenerProveedor");
        q.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        q.setParameter(1, id);
        List<Object[]> filas = q.getResultList();
        return filas.isEmpty() ? null : mapear(filas.get(0));
    }

    @Override
    public int eliminarProveedor(int id) {
        StoredProcedureQuery q = em.createStoredProcedureQuery("sp_eliminarProveedor");
        q.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        q.setParameter(1, id);
        return q.executeUpdate();
    }

    @Override
    public int editarProveedor(Proveedor p) {
        StoredProcedureQuery q = em.createStoredProcedureQuery("sp_actualizarProveedor");
        q.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(4, String.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(5, Integer.class, ParameterMode.IN);
        q.setParameter(1, p.getId_proveedor());
        q.setParameter(2, p.getRuc());
        q.setParameter(3, p.getRazon_social());
        q.setParameter(4, p.getTelefono());
        q.setParameter(5, p.getEstado());
        return q.executeUpdate();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Proveedor> buscarProveedor(String ruc) {
        List<Proveedor> lista = new ArrayList<>();
        StoredProcedureQuery q = em.createStoredProcedureQuery("sp_buscarProveedor");
        q.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        q.setParameter(1, ruc);
        List<Object[]> filas = q.getResultList();
        for (Object[] f : filas) {
            lista.add(mapear(f));
        }
        return lista;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Proveedor> buscarProveedorByNombre(String texto) {
        List<Proveedor> lista = new ArrayList<>();
        StoredProcedureQuery q = em.createStoredProcedureQuery("sp_buscarProveedorByNombre");
        q.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        q.setParameter(1, texto);
        List<Object[]> filas = q.getResultList();
        for (Object[] f : filas) {
            lista.add(mapear(f));
        }
        return lista;
    }

    // sp_listarProveedor / sp_obtenerProveedor / sp_buscarProveedor(ByNombre):
    // id_proveedor, ruc, razon_social, telefono, estado
    private Proveedor mapear(Object[] f) {
        Proveedor p = new Proveedor();
        p.setId_proveedor(((Number) f[0]).intValue());
        p.setRuc(f[1] == null ? null : String.valueOf(f[1]));
        p.setRazon_social(f[2] == null ? null : String.valueOf(f[2]));
        p.setTelefono(f[3] == null ? null : String.valueOf(f[3]));
        p.setEstado(f[4] == null ? 0 : ((Number) f[4]).intValue());
        return p;
    }
}

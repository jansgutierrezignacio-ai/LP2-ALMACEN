package procesos;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import interfaces.IntCliente;
import modelo.Cliente;

@Service
@Transactional
public class GestionCliente implements IntCliente {

    @PersistenceContext
    private EntityManager em;

    @Override
    public int crearCliente(Cliente c) {
        StoredProcedureQuery q = em.createStoredProcedureQuery("sp_registrarCliente");
        q.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(4, String.class, ParameterMode.IN);
        q.setParameter(1, c.getDni());
        q.setParameter(2, c.getNombre());
        q.setParameter(3, c.getApellido());
        q.setParameter(4, c.getTelefono());
        return q.executeUpdate();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Cliente> listarCliente() {
        List<Cliente> lista = new ArrayList<>();
        List<Object[]> filas = em.createStoredProcedureQuery("sp_listarCliente").getResultList();
        for (Object[] f : filas) {
            lista.add(mapear(f));
        }
        return lista;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Cliente obtenerCliente(int id) {
        StoredProcedureQuery q = em.createStoredProcedureQuery("sp_obtenerCliente");
        q.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        q.setParameter(1, id);
        List<Object[]> filas = q.getResultList();
        return filas.isEmpty() ? null : mapear(filas.get(0));
    }

    @Override
    public int eliminarCliente(int id) {
        StoredProcedureQuery q = em.createStoredProcedureQuery("sp_eliminarCliente");
        q.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        q.setParameter(1, id);
        return q.executeUpdate();
    }

    @Override
    public int editarCliente(Cliente c) {
        StoredProcedureQuery q = em.createStoredProcedureQuery("sp_actualizarCliente");
        q.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(4, String.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(5, String.class, ParameterMode.IN);
        q.setParameter(1, c.getIdCliente());
        q.setParameter(2, c.getDni());
        q.setParameter(3, c.getNombre());
        q.setParameter(4, c.getApellido());
        q.setParameter(5, c.getTelefono());
        return q.executeUpdate();
    }

    @Override
    public List<Cliente> buscarCliente(String texto) {
        return buscarClienteByNombre(texto);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Cliente> buscarClienteByNombre(String texto) {
        List<Cliente> lista = new ArrayList<>();
        StoredProcedureQuery q = em.createStoredProcedureQuery("sp_buscarClienteByNombre");
        q.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        q.setParameter(1, texto);
        List<Object[]> filas = q.getResultList();
        for (Object[] f : filas) {
            lista.add(mapear(f));
        }
        return lista;
    }

    // sp_listarCliente / sp_obtenerCliente / sp_buscarClienteByNombre:
    // id_cliente, dni, nombre, apellido, telefono
    private Cliente mapear(Object[] f) {
        Cliente c = new Cliente();
        c.setIdCliente(((Number) f[0]).intValue());
        c.setDni((String) f[1]);
        c.setNombre((String) f[2]);
        c.setApellido((String) f[3]);
        c.setTelefono(f[4] == null ? null : String.valueOf(f[4]));
        return c;
    }
}

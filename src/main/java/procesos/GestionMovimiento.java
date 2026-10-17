package procesos;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import interfaces.IntMovimiento;
import modelo.Movimiento;

@Service
@Transactional
public class GestionMovimiento implements IntMovimiento {

    @PersistenceContext
    private EntityManager em;

    @Override
    public int registrarMovimiento(Movimiento m) {
        StoredProcedureQuery q = em.createStoredProcedureQuery("sp_registrarMovimiento");
        q.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
        q.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
        q.setParameter(1, m.getTipo());
        q.setParameter(2, m.getDescripcion());
        q.setParameter(3, m.getUsuario());
        return q.executeUpdate();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Movimiento> listarMovimientos() {
        List<Movimiento> lista = new ArrayList<>();
        List<Object[]> filas = em.createStoredProcedureQuery("sp_listarMovimientos").getResultList();
        for (Object[] f : filas) {
            Movimiento m = new Movimiento();
            m.setIdMovimiento(((Number) f[0]).intValue());
            m.setTipo((String) f[1]);
            m.setDescripcion((String) f[2]);
            m.setFecha(f[3] instanceof java.util.Date ? (java.util.Date) f[3] : null);
            m.setUsuario(f[4] == null ? null : String.valueOf(f[4]));
            lista.add(m);
        }
        return lista;
    }

    @Override
    public int contarMovimientos() {
        // sp_contarMovimientos devuelve un único valor (COUNT); Hibernate lo entrega
        // como escalar, no como fila Object[]. Se manejan ambos formatos.
        List<?> filas = em.createStoredProcedureQuery("sp_contarMovimientos").getResultList();
        if (filas.isEmpty()) {
            return 0;
        }
        Object primero = filas.get(0);
        Object valor = (primero instanceof Object[]) ? ((Object[]) primero)[0] : primero;
        return valor == null ? 0 : ((Number) valor).intValue();
    }
}

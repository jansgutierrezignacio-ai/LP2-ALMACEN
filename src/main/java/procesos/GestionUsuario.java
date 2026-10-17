package procesos;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import interfaces.IntUsuario;
import modelo.Usuario;

@Service
@Transactional
public class GestionUsuario implements IntUsuario {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Usuario login(String username, String clave) {
        // Consulta JPQL parametrizada sobre la entidad Usuario (tabla Usuarios).
        TypedQuery<Usuario> q = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.username = :username AND u.clave = :clave", Usuario.class);
        q.setParameter("username", username);
        q.setParameter("clave", clave);
        List<Usuario> resultado = q.getResultList();
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    @Override
    public int crearUsuario(Usuario usuario) {
        javax.persistence.StoredProcedureQuery q = em.createStoredProcedureQuery("sp_RegistrarUsuario");
        q.registerStoredProcedureParameter(1, String.class, javax.persistence.ParameterMode.IN);
        q.registerStoredProcedureParameter(2, String.class, javax.persistence.ParameterMode.IN);
        q.registerStoredProcedureParameter(3, Integer.class, javax.persistence.ParameterMode.IN);
        q.registerStoredProcedureParameter(4, String.class, javax.persistence.ParameterMode.IN);
        q.registerStoredProcedureParameter(5, String.class, javax.persistence.ParameterMode.IN);
        q.setParameter(1, usuario.getUsername());
        q.setParameter(2, usuario.getClave());
        q.setParameter(3, usuario.getIdTipo());
        q.setParameter(4, usuario.getNombres());
        q.setParameter(5, usuario.getApellidos());
        return q.executeUpdate();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Usuario> listarUsuario() {
        List<Usuario> lista = new ArrayList<>();
        // Consulta nativa con el JOIN a TiposUsuario para completar el nombre del rol.
        List<Object[]> filas = em.createNativeQuery(
                "SELECT u.idUsuario, u.username, u.clave, u.idTipo, t.descripcion "
              + "FROM Usuarios u INNER JOIN TiposUsuario t ON u.idTipo = t.idTipo "
              + "ORDER BY u.idUsuario DESC").getResultList();
        for (Object[] f : filas) {
            Usuario u = new Usuario();
            u.setIdUsuario(((Number) f[0]).intValue());
            u.setUsername((String) f[1]);
            u.setClave((String) f[2]);
            u.setIdTipo(((Number) f[3]).intValue());
            u.setNombreTipo(f[4] == null ? null : String.valueOf(f[4]));
            lista.add(u);
        }
        return lista;
    }
}

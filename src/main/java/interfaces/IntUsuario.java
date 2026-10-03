package interfaces;
import java.util.List;

import modelo.Usuario;
public interface IntUsuario {
	public Usuario login(String username, String clave);	
	public int crearUsuario(Usuario usuario);
	public List<Usuario> listarUsuario();
}

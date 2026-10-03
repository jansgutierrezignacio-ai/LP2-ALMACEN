package interfaces;
import java.util.List;


import modelo.Cliente;
public interface IntCliente {
		
	public int crearCliente(Cliente cliente);
	public List<Cliente> listarCliente();
	public Cliente obtenerCliente(int id);	
	public int eliminarCliente(int codigo);
	public int editarCliente(Cliente cliente);
	public List<Cliente> buscarCliente(String cod);
	public List<Cliente> buscarClienteByNombre(String nom);

	
}

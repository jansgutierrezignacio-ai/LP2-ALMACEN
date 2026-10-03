package interfaces;
import java.util.List;

import modelo.Proveedor;

public interface IntProveedor {
	
	public int crearProveedor(Proveedor proveedor);
	public List<Proveedor> listarProveedor();
	public Proveedor obtenerProveedor(int id);	
	public int eliminarProveedor(int codigo);
	public int editarProveedor(Proveedor proveedor);
	public List<Proveedor> buscarProveedor(String cod);
	public List<Proveedor> buscarProveedorByNombre(String nom);

}

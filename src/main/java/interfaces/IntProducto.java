package interfaces;
import java.util.List;

import modelo.Producto;

public interface IntProducto {
	
	public int crearProducto(Producto producto);
	public List<Producto> listarProducto();
	public Producto obtenerProducto(int id);	
	public int eliminarProducto(int codigo);
	public int editarProducto(Producto producto);
	public List<Producto> buscarProducto(String cod);
	public List<Producto> buscarProductoByNombre(String nom);

}

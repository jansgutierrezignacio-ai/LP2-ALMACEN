package procesos;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import interfaces.IntProducto;
import modelo.Producto;
import util.MysqlConexion;

public class GestionProducto implements IntProducto {

	@Override
	public int crearProducto(Producto producto) {
		int value = 0;
		Connection cn = null;
		CallableStatement csm = null;
		try {
			cn = MysqlConexion.getConexion();
			String sql = "{call sp_registrarProducto(?,?,?,?,?,?)}";
			csm = cn.prepareCall(sql);
			csm.setString(1, producto.getCodigo());
			csm.setString(2, producto.getNombre());
			csm.setString(3, producto.getDescripcion());
			csm.setDouble(4, producto.getPrecio());
			csm.setInt(5, producto.getStock());
			csm.setInt(6, producto.getIdProveedor());
			value = csm.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (csm != null) csm.close();
				if (cn != null) cn.close();
			} catch (Exception e) { e.printStackTrace(); }
		}
		return value;
	}

	@Override
	public List<Producto> listarProducto() {
		List<Producto> listProducto = new ArrayList<Producto>();
		Connection cn = null;
		CallableStatement csm = null;
		ResultSet rs = null;
		try {
			cn = MysqlConexion.getConexion();
			String sql = "{call sp_listarProducto()}";
			csm = cn.prepareCall(sql);
			rs = csm.executeQuery();
			while (rs.next()) {
				Producto producto = new Producto();
				producto.setIdProducto(rs.getInt("id_producto"));
				producto.setCodigo(rs.getString("codigo"));
				producto.setNombre(rs.getString("nombre"));
				producto.setDescripcion(rs.getString("descripcion"));
				producto.setPrecio(rs.getDouble("precio"));
				producto.setStock(rs.getInt("stock"));
				producto.setIdProveedor(rs.getInt("id_proveedor"));
				// Si el SP hace join con proveedor, traerá razon_social
				try {
					producto.setNombreProveedor(rs.getString("razon_social"));
				} catch (Exception ignore) { /* columna opcional */ }
				listProducto.add(producto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (csm != null) csm.close();
				if (cn != null) cn.close();
			} catch (Exception e) { e.printStackTrace(); }
		}
		return listProducto;
	}

	@Override
	public Producto obtenerProducto(int id) {
		Producto producto = null;
		Connection cn = null;
		CallableStatement csm = null;
		ResultSet rs = null;
		try {
			cn = MysqlConexion.getConexion();
			String sql = "{call sp_obtenerProducto(?)}";
			csm = cn.prepareCall(sql);
			csm.setInt(1, id);
			rs = csm.executeQuery();
			if (rs.next()) {
				producto = new Producto();
				producto.setIdProducto(rs.getInt("id_producto"));
				producto.setCodigo(rs.getString("codigo"));
				producto.setNombre(rs.getString("nombre"));
				producto.setDescripcion(rs.getString("descripcion"));
				producto.setPrecio(rs.getDouble("precio"));
				producto.setStock(rs.getInt("stock"));
				producto.setIdProveedor(rs.getInt("id_proveedor"));
				try {
					producto.setNombreProveedor(rs.getString("razon_social"));
				} catch (Exception ignore) { }
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (csm != null) csm.close();
				if (cn != null) cn.close();
			} catch (Exception e) { e.printStackTrace(); }
		}
		return producto;
	}

	@Override
	public int eliminarProducto(int codigo) {
		int value = 0;
		Connection cn = null;
		CallableStatement csm = null;
		try {
			cn = MysqlConexion.getConexion();
			String sql = "{call sp_eliminarProducto(?)}";
			csm = cn.prepareCall(sql);
			csm.setInt(1, codigo);
			value = csm.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (csm != null) csm.close();
				if (cn != null) cn.close();
			} catch (Exception e) { e.printStackTrace(); }
		}
		return value;
	}

	@Override
	public int editarProducto(Producto producto) {
		int value = 0;
		Connection cn = null;
		CallableStatement csm = null;
		try {
			cn = MysqlConexion.getConexion();
			String sql = "{call sp_actualizarProducto(?,?,?,?,?,?,?)}";
			csm = cn.prepareCall(sql);
			csm.setInt(1, producto.getIdProducto());
			csm.setString(2, producto.getCodigo());
			csm.setString(3, producto.getNombre());
			csm.setString(4, producto.getDescripcion());
			csm.setDouble(5, producto.getPrecio());
			csm.setInt(6, producto.getStock());
			csm.setInt(7, producto.getIdProveedor());
			value = csm.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (csm != null) csm.close();
				if (cn != null) cn.close();
			} catch (Exception e) { e.printStackTrace(); }
		}
		return value;
	}

	@Override
	public List<Producto> buscarProducto(String cod) {
		// Búsqueda por código
		List<Producto> listProducto = new ArrayList<Producto>();
		Connection cn = null;
		CallableStatement csm = null;
		ResultSet rs = null;
		try {
			cn = MysqlConexion.getConexion();
			String sql = "{call sp_buscarProducto(?)}";
			csm = cn.prepareCall(sql);
			csm.setString(1, cod);
			rs = csm.executeQuery();
			while (rs.next()) {
				Producto producto = new Producto();
				producto.setIdProducto(rs.getInt("id_producto"));
				producto.setCodigo(rs.getString("codigo"));
				producto.setNombre(rs.getString("nombre"));
				producto.setDescripcion(rs.getString("descripcion"));
				producto.setPrecio(rs.getDouble("precio"));
				producto.setStock(rs.getInt("stock"));
				producto.setIdProveedor(rs.getInt("id_proveedor"));
				try {
					producto.setNombreProveedor(rs.getString("razon_social"));
				} catch (Exception ignore) { }
				listProducto.add(producto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (csm != null) csm.close();
				if (cn != null) cn.close();
			} catch (Exception e) { e.printStackTrace(); }
		}
		return listProducto;
	}

	@Override
	public List<Producto> buscarProductoByNombre(String nom) {
		List<Producto> listProducto = new ArrayList<Producto>();
		Connection cn = null;
		CallableStatement csm = null;
		ResultSet rs = null;
		try {
			cn = MysqlConexion.getConexion();
			String sql = "{call sp_buscarProductoByNombre(?)}";
			csm = cn.prepareCall(sql);
			csm.setString(1, nom);
			rs = csm.executeQuery();
			while (rs.next()) {
				Producto producto = new Producto();
				producto.setIdProducto(rs.getInt("id_producto"));
				producto.setCodigo(rs.getString("codigo"));
				producto.setNombre(rs.getString("nombre"));
				producto.setDescripcion(rs.getString("descripcion"));
				producto.setPrecio(rs.getDouble("precio"));
				producto.setStock(rs.getInt("stock"));
				producto.setIdProveedor(rs.getInt("id_proveedor"));
				try {
					producto.setNombreProveedor(rs.getString("razon_social"));
				} catch (Exception ignore) { }
				listProducto.add(producto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (csm != null) csm.close();
				if (cn != null) cn.close();
			} catch (Exception e) { e.printStackTrace(); }
		}
		return listProducto;
	}
}

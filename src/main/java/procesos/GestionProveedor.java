package procesos;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import interfaces.IntProveedor;
import modelo.Proveedor;
import util.MysqlConexion;

public class GestionProveedor implements IntProveedor {

	@Override
	public int crearProveedor(Proveedor proveedor) {
		int value = 0;
		Connection cn = null;
		CallableStatement csm = null;
		try {
			cn = MysqlConexion.getConexion();
			String sql = "{call sp_registrarProveedor(?,?,?,?)}";
			csm = cn.prepareCall(sql);
			csm.setString(1, proveedor.getRuc());
			csm.setString(2, proveedor.getRazon_social());
			csm.setString(3, proveedor.getTelefono());
			csm.setInt(4, proveedor.getEstado());
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
	public List<Proveedor> listarProveedor() {
		List<Proveedor> listProveedor = new ArrayList<Proveedor>();
		Connection cn = null;
		CallableStatement csm = null;
		ResultSet rs = null;
		try {
			cn = MysqlConexion.getConexion();
			String sql = "{call sp_listarProveedor()}";
			csm = cn.prepareCall(sql);
			rs = csm.executeQuery();
			while (rs.next()) {
				Proveedor proveedor = new Proveedor();
				proveedor.setId_proveedor(rs.getInt("id_proveedor"));
				proveedor.setRuc(rs.getString("ruc"));
				proveedor.setRazon_social(rs.getString("razon_social"));
				proveedor.setTelefono(rs.getString("telefono"));
				proveedor.setEstado(rs.getInt("estado"));
				listProveedor.add(proveedor);
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
		return listProveedor;
	}

	@Override
	public Proveedor obtenerProveedor(int id) {
		Proveedor proveedor = null;
		Connection cn = null;
		CallableStatement csm = null;
		ResultSet rs = null;
		try {
			cn = MysqlConexion.getConexion();
			String sql = "{call sp_obtenerProveedor(?)}";
			csm = cn.prepareCall(sql);
			csm.setInt(1, id);
			rs = csm.executeQuery();
			if (rs.next()) {
				proveedor = new Proveedor();
				proveedor.setId_proveedor(rs.getInt("id_proveedor"));
				proveedor.setRuc(rs.getString("ruc"));
				proveedor.setRazon_social(rs.getString("razon_social"));
				proveedor.setTelefono(rs.getString("telefono"));
				proveedor.setEstado(rs.getInt("estado"));
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
		return proveedor;
	}

	@Override
	public int eliminarProveedor(int codigo) {
		int value = 0;
		Connection cn = null;
		CallableStatement csm = null;
		try {
			cn = MysqlConexion.getConexion();
			String sql = "{call sp_eliminarProveedor(?)}";
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
	public int editarProveedor(Proveedor proveedor) {
		int value = 0;
		Connection cn = null;
		CallableStatement csm = null;
		try {
			cn = MysqlConexion.getConexion();
			String sql = "{call sp_actualizarProveedor(?,?,?,?,?)}";
			csm = cn.prepareCall(sql);
			csm.setInt(1, proveedor.getId_proveedor());
			csm.setString(2, proveedor.getRuc());
			csm.setString(3, proveedor.getRazon_social());
			csm.setString(4, proveedor.getTelefono());
			csm.setInt(5, proveedor.getEstado());
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
	public List<Proveedor> buscarProveedor(String cod) {
		// Búsqueda por RUC
		List<Proveedor> listProveedor = new ArrayList<Proveedor>();
		Connection cn = null;
		CallableStatement csm = null;
		ResultSet rs = null;
		try {
			cn = MysqlConexion.getConexion();
			String sql = "{call sp_buscarProveedor(?)}";
			csm = cn.prepareCall(sql);
			csm.setString(1, cod);
			rs = csm.executeQuery();
			while (rs.next()) {
				Proveedor proveedor = new Proveedor();
				proveedor.setId_proveedor(rs.getInt("id_proveedor"));
				proveedor.setRuc(rs.getString("ruc"));
				proveedor.setRazon_social(rs.getString("razon_social"));
				proveedor.setTelefono(rs.getString("telefono"));
				proveedor.setEstado(rs.getInt("estado"));
				listProveedor.add(proveedor);
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
		return listProveedor;
	}

	@Override
	public List<Proveedor> buscarProveedorByNombre(String nom) {
		// Búsqueda por razón social
		List<Proveedor> listProveedor = new ArrayList<Proveedor>();
		Connection cn = null;
		CallableStatement csm = null;
		ResultSet rs = null;
		try {
			cn = MysqlConexion.getConexion();
			String sql = "{call sp_buscarProveedorByNombre(?)}";
			csm = cn.prepareCall(sql);
			csm.setString(1, nom);
			rs = csm.executeQuery();
			while (rs.next()) {
				Proveedor proveedor = new Proveedor();
				proveedor.setId_proveedor(rs.getInt("id_proveedor"));
				proveedor.setRuc(rs.getString("ruc"));
				proveedor.setRazon_social(rs.getString("razon_social"));
				proveedor.setTelefono(rs.getString("telefono"));
				proveedor.setEstado(rs.getInt("estado"));
				listProveedor.add(proveedor);
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
		return listProveedor;
	}
}

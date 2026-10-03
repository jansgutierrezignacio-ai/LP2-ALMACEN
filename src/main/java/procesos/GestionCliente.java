package procesos;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import interfaces.IntCliente;
import modelo.Cliente;
import util.MysqlConexion;

public class GestionCliente implements IntCliente {

	@Override
	public int crearCliente(Cliente cliente) {
		int value = 0;
		Connection cn = null;
		CallableStatement csm = null;
		try {
			cn = MysqlConexion.getConexion();
			String sql = "{call sp_registrarCliente(?,?,?,?)}";
			csm = cn.prepareCall(sql);
			csm.setString(1, cliente.getDni());
			csm.setString(2, cliente.getNombre());
			csm.setString(3, cliente.getApellido());
			csm.setString(4, cliente.getTelefono());
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
	public List<Cliente> listarCliente() {
		List<Cliente> listCliente = new ArrayList<Cliente>();
		Connection cn = null;
		CallableStatement csm = null;
		ResultSet rs = null;
		try {
			cn = MysqlConexion.getConexion();
			String sql = "{call sp_listarCliente()}";
			csm = cn.prepareCall(sql);
			rs = csm.executeQuery();
			while (rs.next()) {
				Cliente cliente = new Cliente();
				cliente.setIdCliente(rs.getInt("id_cliente")); 
				cliente.setDni(rs.getString("dni"));
				cliente.setNombre(rs.getString("nombre"));
				cliente.setApellido(rs.getString("apellido"));
				cliente.setTelefono(rs.getString("telefono"));
				listCliente.add(cliente);
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
		return listCliente;
	}

	@Override
	public Cliente obtenerCliente(int id) {
		Cliente cliente = null;
		Connection cn = null;
		CallableStatement csm = null;
		ResultSet rs = null;
		try {
			cn = MysqlConexion.getConexion();
			String sql = "{call sp_obtenerCliente(?)}";
			csm = cn.prepareCall(sql);
			csm.setInt(1, id);
			rs = csm.executeQuery();
			if (rs.next()) {
				cliente = new Cliente();
				cliente.setIdCliente(rs.getInt("id_cliente"));
				cliente.setDni(rs.getString("dni"));
				cliente.setNombre(rs.getString("nombre"));
				cliente.setApellido(rs.getString("apellido"));
				cliente.setTelefono(rs.getString("telefono"));
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
		return cliente;
	}

	@Override
	public int eliminarCliente(int codigo) {
		int value = 0;
		Connection cn = null;
		CallableStatement csm = null;
		try {
			cn = MysqlConexion.getConexion();
			String sql = "{call sp_eliminarCliente(?)}";
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
	public int editarCliente(Cliente cliente) {
		int value = 0;
		Connection cn = null;
		CallableStatement csm = null;
		try {
			cn = MysqlConexion.getConexion();
			String sql = "{call sp_actualizarCliente(?,?,?,?,?)}";
			csm = cn.prepareCall(sql);
			csm.setInt(1, cliente.getIdCliente());
			csm.setString(2, cliente.getDni());
			csm.setString(3, cliente.getNombre());
			csm.setString(4, cliente.getApellido());
			csm.setString(5, cliente.getTelefono());
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
	public List<Cliente> buscarCliente(String cod) {
		return buscarClienteByNombre(cod);
	}

	@Override
	public List<Cliente> buscarClienteByNombre(String nom) {
		List<Cliente> listCliente = new ArrayList<Cliente>();
		Connection cn = null;
		CallableStatement csm = null;
		ResultSet rs = null;
		try {
			cn = MysqlConexion.getConexion();
			String sql = "{call sp_buscarClienteByNombre(?)}";
			csm = cn.prepareCall(sql);
			csm.setString(1, nom);
			rs = csm.executeQuery();
			while (rs.next()) {
				Cliente cliente = new Cliente();
				cliente.setIdCliente(rs.getInt("id_cliente"));
				cliente.setDni(rs.getString("dni"));
				cliente.setNombre(rs.getString("nombre"));
				cliente.setApellido(rs.getString("apellido"));
				cliente.setTelefono(rs.getString("telefono"));
				listCliente.add(cliente);
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
		return listCliente;
	}
}
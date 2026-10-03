package procesos;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import interfaces.IntMovimiento;
import modelo.Movimiento;
import util.MysqlConexion;

public class GestionMovimiento implements IntMovimiento{

	@Override
	public int registrarMovimiento(Movimiento m) {
		int value = 0;
		Connection cn = null;
		CallableStatement csm = null;
		try {
			cn = MysqlConexion.getConexion();
			String sql = "{call sp_registrarMovimiento(?,?,?)}";
			csm = cn.prepareCall(sql);
			csm.setString(1, m.getTipo());
			csm.setString(2, m.getDescripcion());
			csm.setString(3, m.getUsuario());
			value = csm.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (csm != null) csm.close();
				if (cn != null) cn.close();
			}
			catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return value;
	}

	@Override
	public List<Movimiento> listarMovimientos() {
		List<Movimiento> listMovimiento = new ArrayList<>();
		Connection cn = null;
		CallableStatement csm = null;
		ResultSet rs = null;
		try {
			cn = MysqlConexion.getConexion();
			String sql = "{call sp_listarMovimientos()}";
			csm = cn.prepareCall(sql);
			rs = csm.executeQuery();
			while (rs.next()) {
				Movimiento m = new Movimiento();
				m.setIdMovimiento(rs.getInt("id"));
				m.setTipo(rs.getString("tipo"));
				m.setDescripcion(rs.getString("descripcion"));
				m.setFecha(rs.getTimestamp("fecha"));
				m.setUsuario(rs.getString("usuario"));
				
				listMovimiento.add(m);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (rs != null) rs.close();
				if (csm != null) csm.close();
				if (cn != null) cn.close();
			}
			catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return listMovimiento;
	}
	
	@Override
	public int contarMovimientos() {
	    int total = 0;
	    Connection cn = null;
	    CallableStatement cs = null;
	    ResultSet rs = null;
	    try {
	        cn = MysqlConexion.getConexion();
	        cs = cn.prepareCall("{CALL sp_contarMovimientos()}");
	        rs = cs.executeQuery();
	        if (rs.next()) {
	            total = rs.getInt(1);
	        }
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }
	    finally {
	        try {
	            if (rs != null) rs.close();
	            if (cs != null) cs.close();
	            if (cn != null) cn.close();
	        }
	        catch (Exception e2) {
	        	e2.printStackTrace();
	        }
	    }
	    return total;
	}
}

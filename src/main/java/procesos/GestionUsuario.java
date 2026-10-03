package procesos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import interfaces.IntUsuario;
import modelo.Usuario;
import util.MysqlConexion;

public class GestionUsuario implements IntUsuario{	

	@Override
	public Usuario login(String username,String clave) {
		Usuario usuario = null;
		Connection cn = null;
		PreparedStatement psm = null;
		ResultSet rs = null;		
		try {
			cn = MysqlConexion.getConexion();
			String sql = "Select IdUsuario,username,clave,idTipo from Usuarios where username=? and clave=?";
			psm = cn.prepareStatement(sql);
			psm.setString(1, username);
			psm.setString(2, clave);
			rs = psm.executeQuery();			
			if(rs.next()) {
				usuario = new Usuario();
				usuario.setIdUsuario(rs.getInt("idUsuario"));
				usuario.setUsername(rs.getString("username"));
				usuario.setClave(rs.getString("clave"));
				usuario.setIdTipo(rs.getInt("idTipo"));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(rs!=null) rs.close();
				if(psm!=null) psm.close();
				if(cn!=null) cn.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}		
		return usuario;
	}

	@Override
	public int crearUsuario(Usuario usuario) {
	    int value = 0;
	    Connection cn = null;
	    java.sql.CallableStatement csm = null;        
	    try {
	        cn = MysqlConexion.getConexion();
	        // Ahora el procedimiento espera 5 parámetros
	        String sql = "{call sp_RegistrarUsuario(?, ?, ?, ?, ?)}";
	        csm = cn.prepareCall(sql);
	        
	        csm.setString(1, usuario.getUsername());
	        csm.setString(2, usuario.getClave());
	        csm.setInt(3, usuario.getIdTipo());
	        csm.setString(4, usuario.getNombres()); // Nuevo
	        csm.setString(5, usuario.getApellidos()); // Nuevo
	     
	        value = csm.executeUpdate();
	        
	    } catch(Exception e) {
	        System.out.println("Error en crearUsuario: " + e.getMessage());
	    } finally {
	        try {
	            if(csm != null) csm.close();
	            if(cn != null) cn.close();
	        } catch(Exception e) { 
	        	System.err.println("ERROR SQL: " + e.getMessage());
	        	e.printStackTrace();
	        }
	    }
	    return value;
	}
	@Override
	public List<Usuario> listarUsuario() {
		// TODO Auto-generated method stub
		List<Usuario> listUsuario = new ArrayList<Usuario>();
		Connection cn = null;
		PreparedStatement psm = null;
		ResultSet rs = null;		
		try {
			cn=MysqlConexion.getConexion();
			String sql="Select u.idUsuario,u.username,u.clave,u.idTipo, t.descripcion as nombreTipo "
					+ "from Usuarios u inner  join TiposUsuario t on u.idTipo=t.idTipo "
					+ "order by idUsuario desc";
			psm = cn.prepareStatement(sql);
			rs = psm.executeQuery();
			while(rs.next()) {
				Usuario usuario = new Usuario();
				usuario.setIdUsuario(rs.getInt("idUsuario"));//columna idUsuario de la tabla Usuario en la bd
				usuario.setUsername(rs.getString("username"));//columna username de la tabla Usuario en la bd
				usuario.setClave(rs.getString("clave"));//columna clave de la tabla Usuario en la bd
				usuario.setIdTipo(rs.getInt("idTipo"));//columna idTipo de la tabla Usuario en la bd
				usuario.setNombreTipo(rs.getString("nombreTipo"));//columna nombreTipo de la tabla TiposUsuarios en la bd
				listUsuario.add(usuario);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(rs!=null) rs.close();
				if(psm!=null) psm.close();
				if(cn!=null) cn.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return listUsuario;
	}	
}
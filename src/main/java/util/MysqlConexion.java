package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlConexion {
	
	public static Connection getConexion() {
		Connection con = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
			String url="jdbc:mysql://localhost:3306/BD_Almacen";
			String usr ="root";
			String psw="root";
			con=DriverManager.getConnection(url,usr,psw);
		}catch(ClassNotFoundException e){
			System.out.println("Error >> driver no instalado " + e.getMessage());
		}catch(SQLException e) {
			System.out.println("Error >> no hubo conexión a la BD " + e.getMessage());
		}catch(Exception e) {
			System.out.println("Error >> general " + e.getMessage());
		}
		return con;
	}
	
	public static void closeConnection(Connection con) {
		try {
			con.close();
		}catch (SQLException e) {
			System.out.println("Problemas al cerrar la conexión");
		}
	}
}

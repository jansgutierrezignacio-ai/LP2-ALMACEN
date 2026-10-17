package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlConexion {
    
    public static Connection getConexion() {
        Connection cn = null;
        try {
            // Cargamos el Driver moderno de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Reemplaza 'LP2_ALMACEN' por el nombre real de tu Base de Datos
            // Reemplaza 'tu_contraseña' por tu clave personal de MySQL
            String url = "jdbc:mysql://localhost:3306/LP2_ALMACEN?useSSL=false&serverTimezone=UTC";
            String user = "root";
            String password = "tu_contraseña"; 
            
            cn = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            System.out.println("Error: No se encontró el Driver de MySQL: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error: Falla al conectar a la Base de Datos: " + e.getMessage());
        }
        return cn;
    }
}

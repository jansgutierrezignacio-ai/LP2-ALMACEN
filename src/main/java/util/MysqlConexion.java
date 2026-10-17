package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MysqlConexion {

    // Credenciales del archivo .env (local, no versionado).
    private static final Map<String, String> ENTORNO = cargarEntorno();

    public static Connection getConexion() {
        Connection cn = null;
        try {
            // Cargamos el Driver moderno de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Los datos de conexion se toman de las variables de entorno o del
            // archivo .env local (ignorado por git); no hay credenciales en el codigo.
            String host = valor("MYSQL_HOST", "localhost");
            String puerto = valor("MYSQL_PORT", "3306");
            String baseDatos = valor("MYSQL_DATABASE", "BD_ALMACEN");
            String url = "jdbc:mysql://" + host + ":" + puerto + "/" + baseDatos
                    + "?useSSL=false&serverTimezone=UTC";
            String user = valor("MYSQL_ROOT_USER", "root");
            String password = valor("MYSQL_ROOT_PASSWORD", "");

            if (password.isEmpty()) {
                System.out.println("Aviso: no se encontro la clave de MySQL. Definir MYSQL_ROOT_PASSWORD "
                        + "en el archivo .env o como variable de entorno.");
            }

            cn = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            System.out.println("Error: No se encontró el Driver de MySQL: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error: Falla al conectar a la Base de Datos: " + e.getMessage());
        }
        return cn;
    }

    // Devuelve una credencial: primero la variable de entorno y luego el archivo .env.
    private static String valor(String clave, String defecto) {
        String dato = System.getenv(clave);
        if (dato == null || dato.trim().isEmpty()) {
            dato = ENTORNO.get(clave);
        }
        return (dato == null || dato.trim().isEmpty()) ? defecto : dato.trim();
    }

    // Lee el archivo .env ubicado en el directorio de trabajo o en sus padres (hasta 2 niveles).
    private static Map<String, String> cargarEntorno() {
        Map<String, String> datos = new HashMap<>();
        File archivo = buscarEnv();
        if (archivo == null) {
            return datos;
        }
        try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) {
                    continue;
                }
                int separador = linea.indexOf('=');
                if (separador <= 0) {
                    continue;
                }
                String clave = linea.substring(0, separador).trim();
                String valor = linea.substring(separador + 1).trim();
                if (valor.length() >= 2 && valor.startsWith("\"") && valor.endsWith("\"")) {
                    valor = valor.substring(1, valor.length() - 1);
                }
                datos.put(clave, valor);
            }
        } catch (IOException e) {
            System.out.println("Aviso: no se pudo leer el archivo .env: " + e.getMessage());
        }
        return datos;
    }

    private static File buscarEnv() {
        Path directorio = Paths.get(System.getProperty("user.dir", ".")).toAbsolutePath();
        for (int nivel = 0; nivel < 3 && directorio != null; nivel++) {
            File archivo = directorio.resolve(".env").toFile();
            if (archivo.isFile()) {
                return archivo;
            }
            directorio = directorio.getParent();
        }
        return null;
    }
}

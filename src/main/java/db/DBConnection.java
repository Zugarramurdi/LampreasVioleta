package db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utilidad para obtener conexiones JDBC a la base de datos.
 *
 * <p>Esta clase centraliza la creación de conexiones y evita duplicar lógica de configuración
 * en DAOs y servicios. Lee los parámetros de conexión desde el fichero
 * {@code config/db.properties}.</p>
 *
 * <h2>Configuración esperada</h2>
 * <ul>
 *   <li>{@code db.url} - URL JDBC (p. ej. {@code jdbc:postgresql://localhost:5432/LampreaDB})</li>
 *   <li>{@code db.user} - usuario de la BD</li>
 *   <li>{@code db.password} - contraseña</li>
 * </ul>
 *
 * <h2>Notas</h2>
 * <ul>
 *   <li>La ruta {@code config/db.properties} es relativa al <i>working directory</i> del proceso.</li>
 *   <li>Si el fichero no se puede leer o faltan claves obligatorias, se lanza {@link SQLException}
 *       con una descripción clara y, en el caso de lectura, con la causa original ({@link IOException}).</li>
 * </ul>
 *
 * @implNote Clase de utilidad: no se instancia.
 */
public final class DBConnection {

    // Localizar

    /**
     * Obtiene una conexión JDBC utilizando la configuración de {@code config/db.properties}.
     *
     * <p>Este método:
     * <ol>
     *   <li>Lee el fichero {@code config/db.properties}.</li>
     *   <li>Valida la presencia de {@code db.url}, {@code db.user} y {@code db.password}.</li>
     *   <li>Crea y devuelve la conexión mediante {@link DriverManager#getConnection(String, String, String)}.</li>
     * </ol>
     * </p>
     *
     * <p>El código que llama a este método es responsable de cerrar la conexión devuelta
     * (idealmente usando <i>try-with-resources</i>).</p>
     *
     * @return conexión JDBC abierta y lista para usarse.
     * @throws SQLException si falla la lectura del fichero de configuración, faltan claves obligatorias
     *         o no se puede establecer la conexión con la base de datos.
     */
    public static Connection getConnection() throws SQLException {
        Properties prop = new Properties();

        try (InputStream in = Files.newInputStream(Paths.get("config/db.properties"))) {
            prop.load(in);
        } catch (IOException e) {
            throw new SQLException("No se pudo leer config/db.properties.", e);
        }

        String url = prop.getProperty("db.url");
        String user = prop.getProperty("db.user");
        String password = prop.getProperty("db.password");

        if (url == null || user == null || password == null) {
            throw new SQLException("Faltan claves en config/db.properties: db.url, db.user, db.password");
        }

        return DriverManager.getConnection(url, user, password);
    }

    /** Constructor privado para evitar instanciación. */
    private DBConnection() {
    }


//    private static Properties loadProperties() {
//        Properties prop = new Properties();
//        try (InputStream in = Files.newInputStream(Paths.get("config/db.properties"))) {
//            prop.load(in);
//        }
//        return prop;
//    }
//
//    public static Connection getConnection() throws SQLException {
//        Properties prop = loadProperties();
//
//        String url = prop.getProperty("db.url");
//        String user = prop.getProperty("db.user");
//        String password = prop.getProperty("db.password");
//        return DriverManager.getConnection(url, user, password);
//    }



//    private static final String HOST = System.getenv().getOrDefault("PG_HOST", "juanmasegura.com");
//    private static final String PORT = System.getenv().getOrDefault("PG_PORT", "5432");
//    private static final String DB = System.getenv().getOrDefault("PG_DB", "LampreaDB");
//
//    // Conectar
//    private static final String USER = System.getenv().getOrDefault("PG_USER", "root");
//    private static final String PASS = System.getenv().getOrDefault("PG_PASS", "@@Zug4rr4murdi_89@@");
//
//    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB;



    // Funcion para crear conexion
//    public static Connection  getConnection() throws SQLException {
//        Properties p = new Properties();
//        p.setProperty("user", USER);
//        p.setProperty("password", PASS);
//
//        return DriverManager.getConnection(URL, p);
//    }

}

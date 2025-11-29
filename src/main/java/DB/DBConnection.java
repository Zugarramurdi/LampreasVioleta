package DB;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnection {

    // Localizar
    private static Properties loadProperties() throws IOException {
        Properties prop = new Properties();
        try (InputStream in = Files.newInputStream(Paths.get("config/db.properties"))) {
            prop.load(in);
        }
        return prop;
    }

    public static Connection getConnection() throws SQLException {
        try {
            Properties prop = loadProperties();

            String url = prop.getProperty("db.url");
            String user = prop.getProperty("db.user");
            String password = prop.getProperty("db.password");
            return DriverManager.getConnection(url, user, password);

        } catch (IOException ex) {
            System.err.println("No se ha podido leer properties"+ex.getMessage());
            return null;
        }

    }

//    private static final String HOST = System.getenv().getOrDefault("PG_HOST", "juanmasegura.com");
//    private static final String PORT = System.getenv().getOrDefault("PG_PORT", "5432");
//    private static final String DB = System.getenv().getOrDefault("PG_DB", "LampreaDB");
//
//    // Conectar
//    private static final String USER = System.getenv().getOrDefault("PG_USER", "root");
//    private static final String PASS = System.getenv().getOrDefault("PG_PASS", "@@Zug4rr4murdi_89@@");
//
//    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB;

    private DBConnection() {}

    // Funcion para crear conexion
//    public static Connection  getConnection() throws SQLException {
//        Properties p = new Properties();
//        p.setProperty("user", USER);
//        p.setProperty("password", PASS);
//
//        return DriverManager.getConnection(URL, p);
//    }

}

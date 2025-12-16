package db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class Db {

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
    private Db() {
    }

}

package DB;

public final class DBConnection {

    // Localizar
    private static final String HOST = System.getenv().getOrDefault("PG_HOST", "localhost");
    private static final String PORT = System.getenv().getOrDefault("PG_PORT", "5432");
    private static final String DB = System.getenv().getOrDefault("PG_DB", "violeta_db");

    // Conectar
    private static final String USER = System.getenv().getOrDefault("PG_USER", "root");
    private static final String PASS = System.getenv().getOrDefault("PG_PASS", "Toor");

    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB;

}

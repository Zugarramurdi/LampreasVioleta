package db;

public final class Db {

    private static final String HOST = System.getenv().getOrDefault("PG_HOST", "localhost");
    private static final String PORT = System.getenv().getOrDefault("PG_PORT", "5432");
    private static final String DB = System.getenv().getOrDefault("PG_DB", "violeta_db");
    private static final String USER = System.getenv().getOrDefault("PG_USER", "violeta_user");
    private static final String PASS = System.getenv().getOrDefault("PG_PASS", "violeta_1234");

    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB;


}

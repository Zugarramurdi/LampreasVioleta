package dao;

public class RepartidorDAO {

    private static final String INSERT_SQL = "INSERT INTO repartidor (id, nombre, telefono, matricula) VALUES" + "(?, ?, ?, ?)";
    private static final String SELECT_BY_ID_SQL = "SELECT id, nombre, telefono, matricula FROM repartidor WHERE id = ?";
    private static final String SELECT_ALL_SQL = "SELECT id, nombre, telefono, matricula FROM repartidor ORDER BY id";
    private static final String DELETE_SQL = "DELETE FROM repartidors WHERE id =?";
    private static final String UPDATE_SQL = "UPDATE repartidor SET nombre=?, telefono=?, matricula=? WHERE id= ?";
    private static final String SEARCH_SQL = """
            SELECT id, nombre, telefono, matricula
            FROM repartidor
            WHERE CAST(id AS TEXT) ILIKE ?
                OR nombre ILIKE ?
                OR telefono ILIKE ?
                OR matricula ILIKE ?
            ORDER BY id
            """;



}

package dao;

import db.DBConnection;
import model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * DAO (Data Access Object) para la entidad {@link Cliente}.
 *
 * <p>Esta clase encapsula el acceso a base de datos mediante JDBC para la tabla {@code cliente},
 * evitando que la capa de UI/servicio gestione SQL, conexiones o resultsets.</p>
 *
 * <h2>Responsabilidades</h2>
 * <ul>
 *   <li>Operaciones CRUD sobre {@code cliente}.</li>
 *   <li>Búsqueda por filtro (ID/nombre/email) usando {@code ILIKE}.</li>
 *   <li>Mapeo {@link ResultSet} → {@link Cliente}.</li>
 * </ul>
 *
 * @see Cliente
 * @see DBConnection
 */
public class ClienteDAO {

    private static final String INSERT_SQL = "INSERT INTO cliente (id, nombre, email) VALUES" + "(?, ?, ?)";
    private static final String SELECT_BY_ID_SQL = "SELECT id, nombre, email FROM cliente WHERE id=?";
    private static final String SELECT_ALL_SQL = "SELECT id, nombre, email FROM cliente ORDER BY id";
    private static final String DELETE_SQL = "DELETE FROM cliente WHERE id=?";
    private static final String UPDATE_SQL = "UPDATE cliente SET nombre=?, email=? WHERE id=?";
    private static final String SEARCH_SQL = """
                    SELECT id, nombre, email
                    FROM cliente
                    WHERE CAST(id AS TEXT) ILIKE ? 
                        OR nombre ILIKE ?  
                        OR email ILIKE ?
                    ORDER BY id                    
                    """;


    /**
     * Inserta un nuevo {@link Cliente} en la tabla {@code cliente}.
     *
     * @param cliente entidad a insertar.
     * @throws SQLException si falla el acceso a datos (conexión, SQL, constraints, etc.).
     */
    public void insert(Cliente cliente) throws SQLException {
        try(Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(INSERT_SQL)) {
            ps.setInt(1,cliente.getId());
            ps.setString(2,cliente.getNombre());
            ps.setString(3,cliente.getEmail());

            ps.executeUpdate();

        }

    }

    public void insert(Cliente cliente, Connection con) throws SQLException {
        try(PreparedStatement ps = con.prepareStatement(INSERT_SQL)) {
            ps.setInt(1,cliente.getId());
            ps.setString(2,cliente.getNombre());
            ps.setString(3,cliente.getEmail());

            ps.executeUpdate();

        }

    }

    /**
     * Busca un {@link Cliente} por su identificador.
     *
     * @param id identificador del cliente.
     * @return el cliente encontrado, o {@code null} si no existe.
     * @throws SQLException si falla la consulta.
     */
    public Cliente findById(int id) throws SQLException {
        try(Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setInt(1,id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
//                    return new Cliente(rs.getInt("id"), rs.getString("nombre"), rs.getString("email"));
                }
                return null;
            }

        }

    }

    /**
     * Devuelve todos los clientes ordenados por ID.
     *
     * @return lista de clientes (vacía si no hay registros).
     * @throws SQLException si falla la consulta.
     */
    public List<Cliente> findAll() throws SQLException {
        List<Cliente> out = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();PreparedStatement ps = con.prepareStatement(SELECT_ALL_SQL); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
//                Cliente c = new Cliente(rs.getInt("id"), rs.getString("nombre"), rs.getString("email"));
                out.add(mapRow(rs));
            }
        }
        return out;
    }

    /**
     * Actualiza un cliente existente (por ID).
     *
     * @param c entidad con los nuevos valores; su {@code id} identifica qué fila se actualiza.
     * @return número de filas afectadas (0 si no existe, 1 si se actualizó).
     * @throws SQLException si falla la actualización.
     */
    public int update(Cliente c) throws SQLException {
        try(Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getEmail());
            ps.setInt(3, c.getId());

            return ps.executeUpdate();
        }

    }

    /**
     * Elimina un cliente por ID.
     *
     * @param id identificador.
     * @return filas afectadas (0 si no existe, 1 si se borró).
     * @throws SQLException si falla el borrado.
     */
    public int deleteById(int id) throws SQLException {
        try(Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(DELETE_SQL)) {
            ps.setInt(1,id);

            return ps.executeUpdate();
        }
    }

    /**
     * Busca clientes por filtro en ID, nombre o email.
     *
     * <p>Utiliza un patrón {@code %filtro%} y comparaciones {@code ILIKE} (no sensible a mayúsculas).</p>
     *
     * @param filtro texto a buscar.
     * @return lista de coincidencias (vacía si no hay).
     * @throws SQLException si falla la consulta.
     */
    public List<Cliente> search(String filtro) throws SQLException {

        String patron = "%" + filtro + "%";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(SEARCH_SQL)) {
            pst.setString(1, patron);
            pst.setString(2, patron);
            pst.setString(3, patron);

            List<Cliente> out = new ArrayList<>();

            try(ResultSet rs = pst.executeQuery()){

                while (rs.next()){
                    out.add(mapRow(rs));
                }
            }
            return out;
        }
    }

    /**
     * Mapea la fila actual del {@link ResultSet} a un {@link Cliente}.
     *
     * @param rs resultset posicionado en una fila válida.
     * @return cliente mapeado.
     * @throws SQLException si falla la lectura de columnas.
     */
    private Cliente mapRow(ResultSet rs) throws SQLException {

        Cliente c = new Cliente(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("email")
        );

        return c;
    }


}

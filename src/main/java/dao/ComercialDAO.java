package dao;

import db.DBConnection;
import model.Comercial;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para la entidad {@link Comercial}.
 *
 * <p>Encapsula el acceso JDBC a la tabla {@code comercial}. Proporciona operaciones CRUD y
 * búsqueda por filtro usando {@code ILIKE}.</p>
 *
 * <h2>Responsabilidades</h2>
 * <ul>
 *   <li>CRUD sobre {@code comercial}.</li>
 *   <li>Búsqueda por filtro (ID/nombre/email/teléfono).</li>
 *   <li>Mapeo {@link ResultSet} → {@link Comercial}.</li>
 * </ul>
 *
 * @see Comercial
 * @see DBConnection
 */
public class ComercialDAO {

    private static final String INSERT_SQL = "INSERT INTO comercial (id, nombre, email, telefono) VALUES" + "(?,?,?,?)";
    private static final String SELECT_BY_ID_SQL = "SELECT id, nombre, email, telefono FROM comercial WHERE id=?";
    private static final String SELECT_ALL_SQL = "SELECT id, nombre, email, telefono FROM comercial ORDER BY id";
    private static final String DELETE_SQL = "DELETE FROM comercial WHERE id=?";
    private static final String UPDATE_SQL = "UPDATE comercial SET nombre=?, email=?, telefono=? WHERE id=?";
    private static final String SEARCH_SQL = """
            SELECT id, nombre, email, telefono
            FROM comercial
            WHERE CAST (id AS TEXT) ILIKE ?
                OR nombre ILIKE ?
                OR email ILIKE ?
                OR telefono ILIKE ?
            ORDER BY id
            """;

    /**
     * Inserta un {@link Comercial} en la tabla {@code comercial}.
     *
     * @param comercial entidad a insertar.
     * @throws SQLException si falla el acceso a datos.
     */
    public void insert(Comercial comercial) throws SQLException {
        try(Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(INSERT_SQL)) {
            ps.setInt(1, comercial.getId());
            ps.setString(2, comercial.getNombre());
            ps.setString(3, comercial.getEmail());
            ps.setString(4, comercial.getTelefono());

            ps.executeUpdate();
        }
    }

    /**
     * Busca un comercial por ID.
     *
     * @param id identificador.
     * @return comercial encontrado o {@code null} si no existe.
     * @throws SQLException si falla la consulta.
     */
    public Comercial findById(int id) throws SQLException {
        try(Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setInt(1, id);

            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    /**
     * Devuelve todos los comerciales ordenados por ID.
     *
     * @return lista de comerciales (vacía si no hay registros).
     * @throws SQLException si falla la consulta.
     */
    public List<Comercial> findAll() throws SQLException {
        List<Comercial> comerciales = new ArrayList<>();
        try(Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SELECT_ALL_SQL); ResultSet rs = ps.executeQuery()) {
            while(rs.next()) {
                comerciales.add(mapRow(rs));
            }
        }
        return comerciales;
    }

    /**
     * Actualiza un comercial existente (por ID).
     *
     * @param comercial entidad con los nuevos valores; su {@code id} identifica qué fila se actualiza.
     * @return número de filas afectadas (0 si no existe, 1 si se actualizó).
     * @throws SQLException si falla la actualización.
     */
    public int update(Comercial comercial) throws SQLException {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, comercial.getNombre());
            ps.setString(2, comercial.getEmail());
            ps.setString(3, comercial.getTelefono());
            ps.setInt(4, comercial.getId());

            return ps.executeUpdate();
        }
    }

    /**
     * Elimina un comercial por ID.
     *
     * @param id identificador.
     * @return filas afectadas (0 si no existe, 1 si se borró).
     * @throws SQLException si falla el borrado.
     */
    public int deleteById(int id) throws SQLException {
        try(Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(DELETE_SQL)) {
            ps.setInt(1, id);

            return ps.executeUpdate();
        }
    }

    /**
     * Busca comerciales por filtro en ID, nombre, email o teléfono.
     *
     * @param filtro texto de búsqueda.
     * @return lista de coincidencias (vacía si no hay).
     * @throws SQLException si falla la consulta.
     */
    public List<Comercial> search(String filtro) throws SQLException {
        String patron = "%"+filtro+"%";
        try(Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SEARCH_SQL)) {
            ps.setString(1, patron);
            ps.setString(2, patron);
            ps.setString(3, patron);
            ps.setString(4, patron);

            List<Comercial> comerciales = new ArrayList<>();

            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    comerciales.add(mapRow(rs));
                }
            }
            return comerciales;
        }
    }

    /**
     * Mapea la fila actual del {@link ResultSet} a un {@link Comercial}.
     *
     * @param rs resultset posicionado en una fila válida.
     * @return comercial mapeado.
     * @throws SQLException si falla la lectura de columnas.
     */
    private Comercial mapRow(ResultSet rs) throws SQLException {

        Comercial c = new Comercial(
          rs.getInt("id"),
          rs.getString("nombre"),
          rs.getString("email"),
          rs.getString("telefono")
        );
        return c;
    }


}

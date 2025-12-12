package dao;

import db.DBConnection;
import model.Repartidor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para la entidad {@link Repartidor}.
 *
 * <p>Encapsula el acceso JDBC a la tabla {@code repartidor} y proporciona operaciones CRUD
 * y búsqueda por múltiples campos.</p>
 *
 * <h2>Responsabilidades</h2>
 * <ul>
 *   <li>CRUD sobre {@code repartidor}.</li>
 *   <li>Búsqueda por filtro (ID/nombre/teléfono/matrícula) con {@code ILIKE}.</li>
 *   <li>Mapeo {@link ResultSet} → {@link Repartidor}.</li>
 * </ul>
 *
 * @see Repartidor
 * @see DBConnection
 */
public class RepartidorDAO {

    private static final String INSERT_SQL = "INSERT INTO repartidor (id, nombre, telefono, matricula) VALUES" + "(?, ?, ?, ?)";
    private static final String SELECT_BY_ID_SQL = "SELECT id, nombre, telefono, matricula FROM repartidor WHERE id = ?";
    private static final String SELECT_ALL_SQL = "SELECT id, nombre, telefono, matricula FROM repartidor ORDER BY id";
    private static final String DELETE_SQL = "DELETE FROM repartidor WHERE id =?";
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

    /**
     * Inserta un {@link Repartidor} en la tabla {@code repartidor}.
     *
     * @param repartidor entidad a insertar.
     * @throws SQLException si falla el acceso a datos.
     */
    public void insert(Repartidor repartidor) throws SQLException {
        try(Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(INSERT_SQL)){
            ps.setInt(1, repartidor.getId());
            ps.setString(2, repartidor.getNombre());
            ps.setString(3, repartidor.getTelefono());
            ps.setString(4, repartidor.getMatricula());

            ps.executeUpdate();
        }
    }

    /**
     * Busca un repartidor por ID.
     *
     * @param id identificador.
     * @return repartidor encontrado o {@code null} si no existe.
     * @throws SQLException si falla la consulta.
     */
    public Repartidor findById(int id) throws SQLException {
        try(Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SELECT_BY_ID_SQL)){
            ps.setInt(1, id);

            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    /**
     * Devuelve todos los repartidores ordenados por ID.
     *
     * @return lista de repartidores (vacía si no hay registros).
     * @throws SQLException si falla la consulta.
     */
    public List<Repartidor> findAll() throws SQLException {
        List<Repartidor> repartidores = new ArrayList<>();
        try(Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SELECT_ALL_SQL); ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                repartidores.add(mapRow(rs));
            }
        }
        return repartidores;
    }

    /**
     * Actualiza un repartidor existente (por ID).
     *
     * @param repartidor entidad con los nuevos valores; su {@code id} identifica qué fila se actualiza.
     * @return número de filas afectadas (0 si no existe, 1 si se actualizó).
     * @throws SQLException si falla la actualización.
     *
     * @implNote FIX aplicado: se establece el parámetro del {@code WHERE id=?}.
     */
    public int update(Repartidor repartidor) throws SQLException {
        try(Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(UPDATE_SQL)){
            ps.setString(1, repartidor.getNombre());
            ps.setString(2, repartidor.getTelefono());
            ps.setString(3, repartidor.getMatricula());
            ps.setInt(4, repartidor.getId());

            return ps.executeUpdate();
        }
    }

    /**
     * Elimina un repartidor por ID.
     *
     * @param id identificador.
     * @return filas afectadas (0 si no existe, 1 si se borró).
     * @throws SQLException si falla el borrado.
     */
    public int deleteById(int id) throws SQLException {
        try(Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(DELETE_SQL)){
            ps.setInt(1, id);

            return ps.executeUpdate();
        }
    }

    /**
     * Busca repartidores por filtro en ID, nombre, teléfono o matrícula.
     *
     * @param filtro texto de búsqueda.
     * @return lista de coincidencias (vacía si no hay resultados).
     * @throws SQLException si falla la consulta.
     */
    public List<Repartidor> search(String filtro) throws SQLException {
        String patron = "%"+filtro+"%";
        try(Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SEARCH_SQL)){
            ps.setString(1, patron);
            ps.setString(2, patron);
            ps.setString(3, patron);
            ps.setString(4, patron);

            List<Repartidor> repartidores = new ArrayList<>();
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    repartidores.add(mapRow(rs));
                }
            }
            return repartidores;
        }
    }

    /**
     * Mapea la fila actual del {@link ResultSet} a un {@link Repartidor}.
     *
     * @param rs resultset en una fila válida.
     * @return repartidor mapeado.
     * @throws SQLException si falla la lectura de columnas.
     */
    private Repartidor mapRow(ResultSet rs) throws SQLException {
        Repartidor repartidor = new Repartidor(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("telefono"),
                rs.getString("matricula")
        );
        return repartidor;
    }



}

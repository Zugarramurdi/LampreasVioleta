package dao;

import DB.DBConnection;
import model.Comercial;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public void insert(Comercial comercial) throws SQLException {
        try(Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(INSERT_SQL)) {
            ps.setInt(1, comercial.getId());
            ps.setString(2, comercial.getNombre());
            ps.setString(3, comercial.getEmail());
            ps.setString(4, comercial.getTelefono());

            ps.executeUpdate();
        }
    }

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

    public List<Comercial> findAll() throws SQLException {
        List<Comercial> comerciales = new ArrayList<>();
        try(Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SELECT_ALL_SQL); ResultSet rs = ps.executeQuery()) {
            while(rs.next()) {
                comerciales.add(mapRow(rs));
            }
        }
        return comerciales;
    }

    public int Update(Comercial comercial) throws SQLException {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, comercial.getNombre());
            ps.setString(2, comercial.getEmail());
            ps.setString(3, comercial.getTelefono());

            return ps.executeUpdate();
        }
    }

    public int deleteById(int id) throws SQLException {
        try(Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(DELETE_SQL)) {
            ps.setInt(1, id);

            return ps.executeUpdate();
        }
    }

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

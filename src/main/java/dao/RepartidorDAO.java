package dao;

import DB.DBConnection;
import model.Repartidor;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public void insert(Repartidor repartidor) throws SQLException {
        try(Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(INSERT_SQL)){
            ps.setInt(1, repartidor.getId());
            ps.setString(2, repartidor.getNombre());
            ps.setString(3, repartidor.getTelefono());
            ps.setString(4, repartidor.getMatricula());

            ps.executeUpdate();
        }
    }

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

    public List<Repartidor> findAll() throws SQLException {
        List<Repartidor> repartidores = new ArrayList<>();
        try(Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SELECT_ALL_SQL); ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                repartidores.add(mapRow(rs));
            }
        }
        return repartidores;
    }

    public int Update(Repartidor repartidor) throws SQLException {
        try(Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(UPDATE_SQL)){
            ps.setString(1, repartidor.getNombre());
            ps.setString(2, repartidor.getTelefono());
            ps.setString(3, repartidor.getMatricula());

            return ps.executeUpdate();
        }
    }

    public int deleteById(int id) throws SQLException {
        try(Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(DELETE_SQL)){
            ps.setInt(1, id);

            return ps.executeUpdate();
        }
    }

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

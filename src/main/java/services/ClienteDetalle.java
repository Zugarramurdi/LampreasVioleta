package services;

import dao.ClienteDAO;
import dao.DetalleClienteDAO;
import db.Db;
import model.Cliente;
import model.DetalleCliente;

import java.sql.Connection;
import java.sql.SQLException;

public class ClienteDetalle {
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final DetalleClienteDAO detalleClienteDAO = new DetalleClienteDAO();

    /*ACID -> Gestionar commits y en caso de algún fallo haya un rollback
                  Como vamos a gestionar dos tablas diferentes, puede que funcione una y la otra no o ninguna
                  y necesitamos que los datos de la BBDD sean íntegros.
                  Así que: - si falla, se queda como esta (rollback)
                           - si ambas están bien, se guarda (commit)
         */

    public void guardarClienteCompleto(Cliente c, DetalleCliente d) throws SQLException {

        try(Connection con = Db.getConnection()) {
            con.setAutoCommit(false);

            try {
                clienteDAO.insert(c, con);
                detalleClienteDAO.insert(d, con);
                con.commit();

            } catch (SQLException e) {
                con.rollback();
                throw e;

            } finally {
                con.setAutoCommit(true);

            }
        }

    }

}

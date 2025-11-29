package app;

import dao.ClienteDAO;
import model.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Crea datos en memoria para explicar:
 *  - 1:1  Cliente ↔ DetalleCliente
 *  - 1:N  Cliente ↔ Pedido
 *  - N:M  Pedido ↔ Producto (vía DetallePedido)
 */

public class DemoRelaciones1 {
    public static void main(String[] args) {

        ClienteDAO clienteDAO = new ClienteDAO();

        try {
            cargaDeDatos(clienteDAO);
            mostrarDatos(clienteDAO);
        } catch (SQLException e) {
            System.err.println("Algo ha salido mal..."+e.getMessage());
        }

    }

    private static void cargaDeDatos(ClienteDAO clienteDAO) throws SQLException {
        System.out.println("--- Cargando datos ---");
        Cliente c1 = new Cliente(1,"Roberto Rodriguez", "robert@rodri.com");
        Cliente c2 = new Cliente(2, "Andrea Valenti","andrea@valenti.com");

        clienteDAO.insert(c1);
        clienteDAO.insert(c2);

        System.out.println("\n--- Datos cargados correctamente ---");
    }

    private static void mostrarDatos(ClienteDAO clienteDAO) throws SQLException {
        System.out.println("---- Clientes ----");
        List<Cliente> clientes = clienteDAO.findAll();

        clientes.forEach(System.out::println);
    }

    private static void deleteCliente(ClienteDAO clienteDAO, int id) throws SQLException {
        String name = clienteDAO.findById(id).getNombre();
        clienteDAO.deleteById(id);

        System.out.println("Cliente "+name+" eliminado correctamente");
    }
}

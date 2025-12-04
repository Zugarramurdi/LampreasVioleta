package services;

import DB.DBConnection;
import dao.ClienteDAO;
import dao.DetalleClienteDAO;
import model.Cliente;
import model.DetalleCliente;

public class ClienteDetalle {
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final DetalleClienteDAO detalleClienteDAO = new DetalleClienteDAO();

    public void guardarClienteCompleto(Cliente c, DetalleCliente d) {

        /*ACID -> Gestionar commits y en caso de algún fallo haya un rollback
                  Como vamos a gestionar dos tablas diferentes, puede que funcione una y la otra no o ninguna
                  y necesitamos que los datos de la BBDD sean íntegros.
                  Así que: - si falla, se queda como esta (rollback)
                           - si ambas están bien, se guarda (commit)
         */


        try{

        }catch(Exception ex){

        }finally{

        }

    }

}

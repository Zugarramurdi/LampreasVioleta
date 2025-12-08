package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dao.ClienteDAO;
import model.Cliente;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ClienteService {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ObjectMapper mapper;

    public ClienteService(){
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Devuelve todos los clientes desde la BD (para la vista)
     */
    public List<Cliente> obtenerTodos() throws SQLException {
        return clienteDAO.findAll();
    }

    /**
     * Exportar a JSON la lista completa de clientes en la ruta indicada
     * @param nombreFichero "clientes.json"
     * @return fichero JSON generado
     * @throws SQLException si ocurre error al acceder a base de datos
     * @throws IOException si ocurre un error al escribir el fichero JSON
     */
    public File exportarClientesAJson(String nombreFichero) throws SQLException, IOException {
        List<Cliente> clientes = obtenerTodos();

        //Creamos carpeta donde guardar los exports
        File carpeta = new File("exports");

        //creamos la carpeta si no existe
        if(!carpeta.exists()){
            boolean creada = carpeta.mkdirs();
        }

        File destino = new File(carpeta,nombreFichero);
        mapper.writeValue(destino, clientes);

        return destino;
    }

}

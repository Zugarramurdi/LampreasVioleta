package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dao.ClienteDAO;
import dao.DetalleClienteDAO;
import dto.ClienteDetalleDTO;
import model.Cliente;
import model.DetalleCliente;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteService {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final DetalleClienteDAO detalleClienteDAO = new DetalleClienteDAO();
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
     * Exporta a JSON la lista completa de clientes junto con su detalle.
     *
     * <p>Para cada cliente se consulta, si existe, su {@link DetalleCliente} asociado
     * y se construye un {@link ClienteDetalleDTO} que agrupa toda la información
     * (id, nombre, email, dirección, teléfono, notas).</p>
     *
     * <p>El fichero se guarda dentro de la carpeta {@code exports} en el directorio
     * de trabajo del proyecto. Si la carpeta no existe, se crea.</p>
     *
     * @param nombreFichero nombre del fichero de salida (por ejemplo "clientes.json").
     * @return el fichero JSON generado.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     * @throws IOException  si ocurre un error al escribir el fichero JSON
     *                      o al crear la carpeta de destino.
     */
    public File exportarClientesAJson(String nombreFichero) throws SQLException, IOException {
        // Cargar clientes
        List<Cliente> clientes = obtenerTodos();

        // Construir Lista DTOs con cliente+detalle
        List<ClienteDetalleDTO> dtoList = new ArrayList<>();

        for (Cliente c : clientes) {
            DetalleCliente d = detalleClienteDAO.findById(c.getId());
            ClienteDetalleDTO dto = ClienteDetalleDTO.from(c,d);
            dtoList.add(dto);
        }

        //Creamos carpeta donde guardar los exports
        File carpeta = new File("exports");

        //creamos la carpeta si no existe
        if(!carpeta.exists()){
            boolean creada = carpeta.mkdirs();
        }

        File destino = new File(carpeta,nombreFichero);
        mapper.writeValue(destino, dtoList);

        return destino;
    }

}

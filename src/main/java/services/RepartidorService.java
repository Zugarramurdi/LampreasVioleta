package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dao.ComercialDAO;
import dao.RepartidorDAO;
import model.Comercial;
import model.Repartidor;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class RepartidorService {

    private final RepartidorDAO repartidorDAO = new RepartidorDAO();
    private final ObjectMapper mapper;

    public RepartidorService() {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Devuelve todos los clientes desde la BD (para la vista)
     */
    public List<Repartidor> obtenerTodos() throws SQLException {
        return repartidorDAO.findAll();
    }

    /**
     * Exportar a JSON la lista completa de clientes en la ruta indicada
     *
     * @param nombreFichero "clientes.json"
     * @return fichero JSON generado
     * @throws SQLException si ocurre error al acceder a base de datos
     * @throws IOException  si ocurre un error al escribir el fichero JSON
     */
    public File exportarRepartidoresAJson(String nombreFichero) throws SQLException, IOException {
        List<Repartidor> repartidores = obtenerTodos();

        //Creamos carpeta donde guardar los exports
        File carpeta = new File("exports");

        //creamos la carpeta si no existe
        if (!carpeta.exists()) {
            boolean creada = carpeta.mkdirs();
        }

        File destino = new File(carpeta, nombreFichero);
        mapper.writeValue(destino, repartidores);

        return destino;
    }
}
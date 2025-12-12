package services;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import dao.ComercialDAO;

import model.Comercial;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ComercialService {

    private final ComercialDAO comercialDAO = new ComercialDAO();
    private final ObjectMapper mapper;

    public ComercialService() {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Devuelve todos los clientes desde la BD (para la vista)
     */
    public List<Comercial> obtenerTodos() throws SQLException {
        return comercialDAO.findAll();
    }

    /**
     * Exportar a JSON la lista completa de clientes en la ruta indicada
     *
     * @param nombreFichero "clientes.json"
     * @return fichero JSON generado
     * @throws SQLException si ocurre error al acceder a base de datos
     * @throws IOException  si ocurre un error al escribir el fichero JSON
     */
    public File exportarComercialesAJson(String nombreFichero) throws SQLException, IOException {
        List<Comercial> comerciales = obtenerTodos();

        //Creamos carpeta donde guardar los exports
        File carpeta = new File("exports");

        //creamos la carpeta si no existe
        if (!carpeta.exists()) {
            boolean creada = carpeta.mkdirs();
        }

        File destino = new File(carpeta, nombreFichero);
        mapper.writeValue(destino, comerciales);

        return destino;
    }
}
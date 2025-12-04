package model;

// --------- CLASE DE EJERCICIO ENTREGABLE -----------
/*
 *   2- Implementar DAO para esta clase
 *           - Interface con metodos CRUD
 *           - Integrar clase en ComercialView
 *   3- Actualizar programa para incluir opciones que gestionen esta clase (Alta, consulta, listado y eliminacion)
 *   4- NUEVA FUNCION:
 *       - Usando libreria Jackson (ObjectMapper).
 *       - Exportar a un JSON bien estructurado la lista completa de una de las entidades -> ObjectMapper.writeValueAsString() o writeValue()
 *       - El fichero debe generarse en la carpeta del proyecto.
 *
 *     ---  ENTREGABLES  ---
 *   1- Codigo fuente completo del proyecto
 *   2- Ficheros de persistencia (TXT/CSV/JSON) usados
 *   3- Documento breve (1 pagina) explicando:
 *       - Las clases nuevas añadidas
 *       - Su relacion con las existentes
 *       - Como funciona la exportacion a JSON
 *
 *       ---- CRITERIOS DE EVALUACION ---
 *   * Creacion correcta de las nuevas clases:       2 Puntos
 *   * DAO e implementacion de persistencia:         3 Puntos
 *   * Integracion en el menu del programa:          2 Puntos
 *   * Exportacion funcional a JSON con Jackson:     2 Puntos
 *   * Orden, claridad y buenas practicas:           1 Punto
 *       TOTAL                                       10 Puntos
 *
 * */

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad principal "Comercial".
 * Relaciones:
 *  - 1:N con Cliente (un comercial tiene muchos clientes).
 */

public class Comercial {

    private Integer id;
    private String nombre;
    private String telefono;


    // 1:N
    private List<Cliente> clientes = new ArrayList<>();

    public Comercial() {}
    public Comercial(Integer id, String nombre, String telefono) {
        this.id = id; this.nombre = nombre; this.telefono = telefono;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    @Override
    public String toString() {
        return "Comercial{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", telefono='" + telefono + '\'' +
                ", clientes=" + clientes +
                '}';
    }
}

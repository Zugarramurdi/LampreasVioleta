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
 * Entidad de dominio {@code Comercial}.
 *
 * <p>Representa un comercial que puede estar asociado a múltiples {@link Cliente} (relación 1:N).
 * Se utiliza como modelo en capa de UI, servicios y persistencia (DAO).</p>
 *
 * <h2>Relaciones</h2>
 * <ul>
 *   <li><b>1:N</b> con {@link Cliente}: un comercial puede tener varios clientes.</li>
 * </ul>
 *
 * @implNote La lista {@link #clientes} se inicializa por defecto para evitar {@code NullPointerException}.
 * El método {@link #getClientes()} expone la lista mutable (lista “viva”).
 */
public class Comercial {

    private Integer id;
    private String nombre;
    private String telefono;
    private String email;


    // 1:N
    private List<Cliente> clientes = new ArrayList<>();

    /**
     * Constructor vacío requerido por ciertas librerías/frameworks (p. ej. serialización).
     */
    public Comercial() {}

    /**
     * Crea un comercial con identificador y datos principales.
     *
     * @param id identificador del comercial.
     * @param nombre nombre del comercial.
     * @param email email del comercial.
     * @param telefono teléfono del comercial.
     */
    public Comercial(Integer id, String nombre, String email, String telefono) {
        this.id = id; this.nombre = nombre;  this.email = email; this.telefono = telefono;
    }

    /**
     * Crea un comercial con ID, nombre y email (sin teléfono).
     *
     * @param id identificador.
     * @param nombre nombre.
     * @param email email.
     */
    public Comercial(int id,String nombre, String email){
        this.id = id; this.nombre = nombre; this.email = email;
    }

    /** @return identificador del comercial. */
    public Integer getId() {
        return id;
    }

    /** @param id identificador del comercial. */
    public void setId(Integer id) {
        this.id = id;
    }

    /** @return nombre del comercial. */
    public String getNombre() {
        return nombre;
    }

    /** @param nombre nombre del comercial. */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /** @return teléfono del comercial. */
    public String getTelefono() {
        return telefono;
    }

    /** @param telefono teléfono del comercial. */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /** @return email del comercial. */
    public String getEmail() {return email;}

    /** @param email email del comercial. */
    public void setEmail(String email) {this.email = email;}

    /**
     * Devuelve la lista de clientes asociados.
     *
     * @return lista mutable de clientes (puede modificarse desde fuera).
     * @implNote Si quieres proteger el encapsulamiento, puedes devolver una copia o una lista no modificable.
     */
    public List<Cliente> getClientes() {
        return clientes;
    }

    /** @param clientes lista de clientes asociados. */
    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    /**
     * Representación en texto para depuración.
     *
     * @implNote Evita incluir {@link #clientes} si puede ser grande o si existe riesgo de recursión.
     */
    @Override
    public String toString() {
        return "Comercial{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email=" + email +
                '}';
    }
}

package model;

// --------- CLASE DE EJERCICIO ENTREGABLE -----------
/*
 *   2- Implementar DAO para esta clase
 *           - Interface con metodos CRUD
 *           - Integrar clase en RepartidorView
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
 * Entidad de dominio {@code Repartidor}.
 *
 * <p>Representa un repartidor que puede estar asociado a múltiples {@link Pedido} (relación 1:N).
 * Se utiliza como modelo en capa de UI, servicios y persistencia (DAO).</p>
 *
 * <h2>Relaciones</h2>
 * <ul>
 *   <li><b>1:N</b> con {@link Pedido}: un repartidor puede repartir varios pedidos.</li>
 * </ul>
 *
 * @implNote La lista {@link #pedidos} se inicializa por defecto para evitar {@code NullPointerException}.
 * El método {@link #getPedidos()} expone la lista mutable (lista “viva”).
 */
public class Repartidor {

    private Integer id;
    private String nombre;
    private String telefono;
    private String matricula;


    // 1:N
    private List<Pedido> pedidos = new ArrayList<>();

    /** Constructor vacío requerido por ciertas librerías/frameworks. */
    public Repartidor(){}

    /**
     * Crea un repartidor con identificador y datos principales.
     *
     * @param id identificador del repartidor.
     * @param nombre nombre del repartidor.
     * @param telefono teléfono del repartidor.
     * @param matricula matrícula del vehículo asociado.
     */
    public Repartidor(Integer id, String nombre, String telefono, String matricula) {
        this.id = id; this.nombre = nombre; this.telefono = telefono; this.matricula = matricula;
    }

    /**
     * Crea un repartidor con ID, nombre y matrícula (sin teléfono).
     *
     * @param id identificador.
     * @param nombre nombre.
     * @param matricula matrícula.
     */
    public Repartidor(Integer id, String nombre, String matricula){
        this.id = id; this.nombre = nombre; this.matricula = matricula;
    }

    /** @return identificador del repartidor. */
    public Integer getId() {
        return id;
    }

    /** @param id identificador del repartidor. */
    public void setId(Integer id) {
        this.id = id;
    }

    /** @return nombre del repartidor. */
    public String getNombre() {
        return nombre;
    }

    /** @param nombre nombre del repartidor. */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /** @return teléfono del repartidor. */
    public String getTelefono() {
        return telefono;
    }

    /** @param telefono teléfono del repartidor. */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /** @return matrícula del repartidor/vehículo. */
    public String getMatricula() {
        return matricula;
    }

    /** @param matricula matrícula del repartidor/vehículo. */
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    /**
     * Devuelve la lista de pedidos asociados.
     *
     * @return lista mutable de pedidos (puede modificarse desde fuera).
     */
    public List<Pedido> getPedidos() {
        return pedidos;
    }

    /** @param pedidos lista de pedidos asociados. */
    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    /**
     * Representación en texto para depuración.
     *
     * @implNote Evita incluir {@link #pedidos} si puede ser grande o si existe riesgo de recursión.
     */
    @Override
    public String toString() {
        return "Repartidor{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", telefono='" + telefono + '\'' +
                ", matricula='" + matricula +
                '}';
    }
}

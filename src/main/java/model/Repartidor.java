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
 * Entidad principal "Repartidor".
 * Relaciones:
 *  - 1:N con Pedido (un repartidor reparte muchos pedidos).
 */

public class Repartidor {
    private Integer id;
    private String nombre;
    private String telefono;
    private Integer matricula;


    // 1:N
    private List<Pedido> pedidos = new ArrayList<>();

    public Repartidor(){}
    public Repartidor(Integer id, String nombre, String telefono, Integer matricula) {
        this.id = id; this.nombre = nombre; this.telefono = telefono; this.matricula = matricula;
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

    public Integer getMatricula() {
        return matricula;
    }

    public void setMatricula(Integer matricula) {
        this.matricula = matricula;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    @Override
    public String toString() {
        return "Repartidor{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", telefono='" + telefono + '\'' +
                ", matricula=" + matricula +
                ", pedidos=" + pedidos +
                '}';
    }
}

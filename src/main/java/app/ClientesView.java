package app;

import dao.ClienteDAO;
import dao.DetalleClienteDAO;
import model.Cliente;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import model.DetalleCliente;
import services.ClienteDetalle;
import services.ClienteService;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Vista JavaFX para gestionar clientes.
 *
 * Versión preparada para trabajar más adelante con DetalleCliente,
 * pero de momento:
 *  - SOLO usa ClienteDAO (insert, findById, findAll).
 *  - La tabla muestra únicamente datos de Cliente (id, nombre, email).
 *  - Los campos de detalle (dirección, teléfono, notas) se muestran en el
 *    formulario, pero aún NO se guardan en BD.
 *
 * Cuando exista DetalleClienteDAO, podrás:
 *  - Cargar el detalle al seleccionar un cliente.
 *  - Guardar/actualizar detalle junto con el cliente.
 *  - Borrar detalle cuando borres un cliente.
 */
public class ClientesView {

    private final BorderPane root = new BorderPane();

    // Tabla y datos
    private final TableView<Cliente> tabla = new TableView<>();
    private final ObservableList<Cliente> datos = FXCollections.observableArrayList();

    private final DetalleClienteDAO detalleClienteDAO = new DetalleClienteDAO();

    // Caché en memoria: idCliente -> detalle
    private final Map<Integer, DetalleCliente> cacheDetalles = new HashMap<>();
    // Campos de formulario (Cliente)
    private final TextField txtId = new TextField();
    private final TextField txtNombre = new TextField();
    private final TextField txtEmail = new TextField();

    // Campos de formulario (DetalleCliente) – por ahora solo visuales
    private final TextField txtDireccion = new TextField();
    private final TextField txtTelefono  = new TextField();
    private final TextField txtNotas     = new TextField();

    // Botones CRUD
    private final Button btnNuevo    = new Button("Nuevo");
    private final Button btnGuardar  = new Button("Guardar");
    private final Button btnBorrar   = new Button("Borrar");
    private final Button btnRecargar = new Button("Recargar");
    private final Button btnExportarJson = new Button("Exportar JSON");

    // Búsqueda
    private final TextField txtBuscar          = new TextField();
    private final Button    btnBuscar          = new Button("Buscar");
    private final Button    btnLimpiarBusqueda = new Button("Limpiar");

    // DAO (acceso a BD)
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ClienteDetalle clienteDetalle = new ClienteDetalle();

    // Service (exportacion a JSON)
    private final ClienteService clienteService = new ClienteService();

    public ClientesView() {
        configurarTabla();
        configurarFormulario();
        configurarEventos();
        recargarDatos(); // al iniciar la vista cargamos los clientes
    }

    public Parent getRoot() {
        return root;
    }

    /* =========================================================
       CONFIGURACIÓN INTERFAZ
       ========================================================= */

    private void configurarTabla() {
        TableColumn<Cliente, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c ->
                new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()));

        TableColumn<Cliente, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));

        TableColumn<Cliente, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getEmail()));

        // ===== Columnas “placeholder” para DetalleCliente =====
        TableColumn<Cliente, String> colDireccion = new TableColumn<>("Dirección");
        colDireccion.setCellValueFactory(c -> {
            DetalleCliente d = cacheDetalles.get(c.getValue().getId());
            String valor = (d != null) ? d.getDireccion() : "";
            return new javafx.beans.property.SimpleStringProperty(valor);
        });

        TableColumn<Cliente, String> colTelefono = new TableColumn<>("Teléfono");
        colTelefono.setCellValueFactory(c -> {
            DetalleCliente d = cacheDetalles.get(c.getValue().getId());
            String valor = (d != null) ? d.getTelefono() : "";
            return new javafx.beans.property.SimpleStringProperty(valor);
        });

        TableColumn<Cliente, String> colNotas = new TableColumn<>("Notas");
        colNotas.setCellValueFactory(c -> {
            DetalleCliente d = cacheDetalles.get(c.getValue().getId());
            String valor = (d != null) ? d.getNotas() : "";
            return new javafx.beans.property.SimpleStringProperty(valor);
        });

        tabla.getColumns().addAll(colId, colNombre, colEmail,
                colDireccion, colTelefono, colNotas);
        tabla.setItems(datos);

        root.setCenter(tabla);
    }

    private void configurarFormulario() {
        GridPane form = new GridPane();
        form.setPadding(new Insets(10));
        form.setHgap(10);
        form.setVgap(10);

        // ----- Cliente -----
        txtId.setPromptText("ID (entero)");
        txtNombre.setPromptText("Nombre");
        txtEmail.setPromptText("Email");

        form.add(new Label("ID:"), 0, 0);
        form.add(txtId, 1, 0);
        form.add(new Label("Nombre:"), 0, 1);
        form.add(txtNombre, 1, 1);
        form.add(new Label("Email:"), 0, 2);
        form.add(txtEmail, 1, 2);

        // ----- DetalleCliente (solo UI, sin BD de momento) -----
        txtDireccion.setPromptText("Dirección");
        txtTelefono.setPromptText("Teléfono");
        txtNotas.setPromptText("Notas");

        form.add(new Label("Dirección:"), 0, 3);
        form.add(txtDireccion, 1, 3);
        form.add(new Label("Teléfono:"), 0, 4);
        form.add(txtTelefono, 1, 4);
        form.add(new Label("Notas:"), 0, 5);
        form.add(txtNotas, 1, 5);

        // Zona botones CRUD
        HBox botonesCrud = new HBox(10, btnNuevo, btnGuardar, btnBorrar, btnRecargar, btnExportarJson);
        botonesCrud.setPadding(new Insets(10, 0, 0, 0));

        // Zona de búsqueda
        HBox zonaBusqueda = new HBox(10,
                new Label("Buscar:"), txtBuscar, btnBuscar, btnLimpiarBusqueda);
        zonaBusqueda.setPadding(new Insets(10, 0, 10, 0));

        BorderPane bottom = new BorderPane();
        bottom.setTop(zonaBusqueda);
        bottom.setCenter(form);
        bottom.setBottom(botonesCrud);

        root.setBottom(bottom);
    }

    private void configurarEventos(){
        // Cuando seleccionamos una fila en la tabla, pasamos los datos al formulario
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                // Cliente
                txtId.setText(String.valueOf(newSel.getId()));
                txtNombre.setText(newSel.getNombre());
                txtEmail.setText(newSel.getEmail());
                txtId.setDisable(true); // al editar, de momento, no dejamos cambiar el ID

                // DetalleCliente (cuando exista DetalleClienteDAO se cargará desde BD)
                // TODO: cuando implementéis DetalleClienteDAO, aquí:
                //   - detalleDAO.findById(newSel.getId())
                //   - rellenar txtDireccion, txtTelefono, txtNotas con sus valores
                try {
                    DetalleCliente d = detalleClienteDAO.findById(newSel.getId());
                    txtDireccion.setText(d.getDireccion());
                    txtTelefono.setText(d.getTelefono());
                    txtNotas.setText(d.getNotas());
                }catch(SQLException e){
                    mostrarError("Error al recuperar detalles de BD",e);
                }

            }
        });

        btnNuevo.setOnAction(e -> limpiarFormulario());

        btnGuardar.setOnAction(e -> guardarCliente());

        btnBorrar.setOnAction(e -> borrarClienteSeleccionado());

        btnRecargar.setOnAction(e -> {
            txtBuscar.clear();
            recargarDatos();
        });

        btnBuscar.setOnAction(e -> buscarClientesEnBBDD());

        btnLimpiarBusqueda.setOnAction(e -> {
            txtBuscar.clear();
            recargarDatos();
        });

        btnExportarJson.setOnAction(e -> exportarClientesJson());
    }

    /* =========================================================
       LÓGICA DE NEGOCIO (usando ClienteDAO actual)
       ========================================================= */

    /**
     * Carga todos los clientes desde la BD usando ClienteDAO.findAll()
     */
    private void recargarDatos() {
        try {
            List<Cliente> lista = clienteDAO.findAll();
            datos.setAll(lista);
            cacheDetalles.clear();
            for(Cliente c : lista){
                Integer id = c.getId();
                if(id == null) continue;

                DetalleCliente d = detalleClienteDAO.findById(id);
                if(d != null){
                    cacheDetalles.put(id, d);
                }
            }
            tabla.refresh();
        } catch (SQLException e) {
            mostrarError("Error al cargar clientes", e);
        }
    }

    /**
     * Búsqueda de momento hecha EN MEMORIA.
     *
     * Se carga toda la lista (findAll) y se filtra con streams.
     * Más adelante se puede cambiar para que use ClienteDAO.search()
     * cuando lo implementéis.
     */
    private void buscarClientesEnMemoria() {
        String filtro = txtBuscar.getText().trim();
        if (filtro.isEmpty()) {
            recargarDatos();
            return;
        }

        try {
            List<Cliente> lista = clienteDAO.findAll();
            String f = filtro.toLowerCase();

            List<Cliente> filtrados = lista.stream()
                    .filter(c ->
                            String.valueOf(c.getId()).contains(f) ||
                                    c.getNombre().toLowerCase().contains(f) ||
                                    c.getEmail().toLowerCase().contains(f)
                    )
                    .collect(Collectors.toList());

            datos.setAll(filtrados);
        } catch (SQLException e) {
            mostrarError("Error al buscar clientes", e);
        }
    }


    private void buscarClientesEnBBDD(){
        String filtro = txtBuscar.getText().trim();

        if ((filtro.isEmpty())){
            recargarDatos();
            return;
        }

        try {
            List<Cliente> lista = clienteDAO.search(filtro);
            datos.setAll(lista);

        } catch (SQLException e){
            mostrarError("Error al buscar", e);
        }

    }

    private void limpiarFormulario() {
        txtId.clear();
        txtNombre.clear();
        txtEmail.clear();
        txtDireccion.clear();
        txtTelefono.clear();
        txtNotas.clear();
        txtId.setDisable(false);
        tabla.getSelectionModel().clearSelection();
    }

    /**
     * Guardar cliente:
     *  - Si no existe en la BD → INSERT usando ClienteDAO.insert()
     *  - Si existe → por ahora solo muestra un aviso.
     *
     * NOTA:
     *  - Los datos de detalle (dirección, teléfono, notas) todavía NO se guardan.
     *  - Cuando tengáis DetalleClienteDAO y/o ClienteService, aquí se podrá:
     *      * insertar/actualizar también el detalle en una transacción.
     */
    private void guardarCliente() {
        // Con ID manual, vuelve a ser obligatorio
        if (txtId.getText().isBlank() ||
                txtNombre.getText().isBlank() ||
                txtEmail.getText().isBlank()) {

            mostrarAlerta("Campos obligatorios",
                    "Debes rellenar ID, nombre y email.");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(txtId.getText().trim());
        } catch (NumberFormatException ex) {
            mostrarAlerta("ID inválido", "El ID debe ser un número entero.");
            return;
        }

        // Cliente con ID escrito por el usuario
        Cliente c = new Cliente(
                id,
                txtNombre.getText().trim(),
                txtEmail.getText().trim()
        );

        // DetalleCliente con el MISMO ID
        DetalleCliente d = new DetalleCliente(
                id,
                txtDireccion.getText().trim(),
                txtTelefono.getText().trim(),
                txtNotas.getText().trim()
        );

        try {
            // Comprobamos en BD si ese ID ya existe
            Cliente existente = clienteDAO.findById(id);

            if (existente == null) {

                clienteDetalle.guardarClienteCompleto(c, d);

                mostrarInfo("Insertado",
                        "Cliente y detalle creados (sin transacción).");
            } else {

                mostrarAlerta("Actualizar pendiente",
                        "El cliente ya existe.\n" +
                                "Más adelante aquí haremos UPDATE desde el Service.");
            }

            recargarDatos();
            limpiarFormulario();

        } catch (SQLException e) {
            mostrarError("Error al guardar cliente y detalle", e);
        }
    }

    /**
     * Borra el cliente actualmente seleccionado en la tabla, solicitando confirmación previa.
     *
     * <p>Si no hay selección, se muestra un aviso y no se realiza ninguna acción.</p>
     *
     * <p>Si el usuario confirma el borrado y la operación se completa correctamente, la vista se
     * actualiza llamando a {@link #recargarDatos()} y se reinicia el formulario con
     * {@link #limpiarFormulario()}.</p>
     *
     * <p>Si no se borra ninguna fila (por ejemplo, porque el registro ya no existe), se informa al usuario.
     * Si se produce un error de base de datos, se muestra un diálogo de error.</p>
     *
     * <p>Nota: cuando exista persistencia del detalle (p. ej. DetalleClienteDAO), convendrá asegurar la
     * consistencia del borrado (transacción o {@code ON DELETE CASCADE}).</p>
     */
    private void borrarClienteSeleccionado() {
        Cliente sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Sin selección", "Selecciona un cliente en la tabla.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar borrado");
        confirm.setHeaderText("¿Eliminar cliente?");
        confirm.setContentText("Se borrará el cliente con ID " + sel.getId());
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        // TODO: implementar ClienteDAO.deleteById(int id) y llamarlo aquí.
        // TODO futuro: cuando haya DetalleClienteDAO, borrar primero detalle,
        //  después cliente, o delegarlo todo a ClienteService.deleteClienteCompleto(id).

//        mostrarAlerta("Borrado pendiente",
//                "Aún no existe deleteById en ClienteDAO.\n" +
//                        "Cuando lo implementemos, aquí se llamará al método.");

        // Ejemplo futuro:

        try {
            int borradas = clienteDAO.deleteById(sel.getId());
            if (borradas > 0) {
                mostrarInfo("Borrado", "Cliente eliminado.");
                recargarDatos();
                limpiarFormulario();
            } else {
                mostrarAlerta("No borrado", "No se encontró el cliente en la BD.");
            }
        } catch (SQLException e) {
            mostrarError("Error al borrar cliente", e);
        }

    }

    private void exportarClientesJson(){
        String nombreFichero = "clientes.json";
        try{
            // nombre de fichero
            File destino = clienteService.exportarClientesAJson(nombreFichero);
            String ruta = destino.getAbsolutePath();

            mostrarInfo("Exportacion correcta", "Se ha generado el fichero "+nombreFichero+" en "+ruta);

        }catch(SQLException | IOException e){
            mostrarError("Error al exportar clientes",e);
        }
    }

    /* =========================================================
       DIÁLOGOS AUXILIARES
       ========================================================= */

    private void mostrarError(String titulo, Exception e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

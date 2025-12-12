package app;

import dao.RepartidorDAO;
import model.Repartidor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import services.ComercialService;
import services.RepartidorService;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Vista JavaFX para gestionar repartidors.
 *
 * Versión preparada para trabajar más adelante con DetalleRepartidor,
 * pero de momento:
 *  - SOLO usa RepartidorDAO (insert, findById, findAll).
 *  - La tabla muestra únicamente datos de Repartidor (id, nombre, email).
 *  - Los campos de detalle (dirección, teléfono, notas) se muestran en el
 *    formulario, pero aún NO se guardan en BD.
 *
 * Cuando exista DetalleRepartidorDAO, podrás:
 *  - Cargar el detalle al seleccionar un repartidor.
 *  - Guardar/actualizar detalle junto con el repartidor.
 *  - Borrar detalle cuando borres un repartidor.
 */
public class RepartidoresView {

    private final BorderPane root = new BorderPane();

    // Tabla y datos
    private final TableView<Repartidor> tabla = new TableView<>();
    private final ObservableList<Repartidor> datos = FXCollections.observableArrayList();

    // Campos de formulario (Repartidor)
    private final TextField txtId = new TextField();
    private final TextField txtNombre = new TextField();
    private final TextField txtMatricula = new TextField();

    // Campos de formulario (DetalleRepartidor) – por ahora solo visuales
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
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();

    // Service (exportacion a JSON)
    private final RepartidorService repartidorService = new RepartidorService();

    public RepartidoresView() {
        configurarTabla();
        configurarFormulario();
        configurarEventos();
        recargarDatos(); // al iniciar la vista cargamos los repartidors
    }

    public Parent getRoot() {
        return root;
    }

    /* =========================================================
       CONFIGURACIÓN INTERFAZ
       ========================================================= */

    private void configurarTabla() {
        TableColumn<Repartidor, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c ->
                new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()));

        TableColumn<Repartidor, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));

        TableColumn<Repartidor, String> colEmail = new TableColumn<>("Matrícula");
        colEmail.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getMatricula()));

        // ===== Columnas “placeholder” para DetalleRepartidor =====
        TableColumn<Repartidor, String> colDireccion = new TableColumn<>("Dirección");
        colDireccion.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(""));

        TableColumn<Repartidor, String> colTelefono = new TableColumn<>("Teléfono");
        colTelefono.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(""));

        TableColumn<Repartidor, String> colNotas = new TableColumn<>("Notas");
        colNotas.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(""));

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

        // ----- Repartidor -----
        txtId.setPromptText("ID (entero)");
        txtNombre.setPromptText("Nombre");
        txtMatricula.setPromptText("Matrícula");

        form.add(new Label("ID:"), 0, 0);
        form.add(txtId, 1, 0);
        form.add(new Label("Nombre:"), 0, 1);
        form.add(txtNombre, 1, 1);
        form.add(new Label("Matrícula:"), 0, 2);
        form.add(txtMatricula, 1, 2);

        // ----- DetalleRepartidor (solo UI, sin BD de momento) -----
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

    private void configurarEventos() {
        // Cuando seleccionamos una fila en la tabla, pasamos los datos al formulario
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                // Repartidor
                txtId.setText(String.valueOf(newSel.getId()));
                txtNombre.setText(newSel.getNombre());
                txtMatricula.setText(newSel.getMatricula());
                txtId.setDisable(true); // al editar, de momento, no dejamos cambiar el ID

                // DetalleRepartidor (cuando exista DetalleRepartidorDAO se cargará desde BD)
                // TODO: cuando implementéis DetalleRepartidorDAO, aquí:
                //   - detalleDAO.findById(newSel.getId())
                //   - rellenar txtDireccion, txtTelefono, txtNotas con sus valores
                txtDireccion.clear();
                txtTelefono.clear();
                txtNotas.clear();
            }
        });

        btnNuevo.setOnAction(e -> limpiarFormulario());

        btnGuardar.setOnAction(e -> guardarRepartidor());

        btnBorrar.setOnAction(e -> borrarRepartidorSeleccionado());

        btnRecargar.setOnAction(e -> {
            txtBuscar.clear();
            recargarDatos();
        });

        btnBuscar.setOnAction(e -> buscarRepartidorsEnBBDD());

        btnLimpiarBusqueda.setOnAction(e -> {
            txtBuscar.clear();
            recargarDatos();
        });

        btnExportarJson.setOnAction(e -> exportarRepartidoresJson());
    }

    /* =========================================================
       LÓGICA DE NEGOCIO (usando RepartidorDAO actual)
       ========================================================= */

    /**
     * Carga todos los repartidors desde la BD usando RepartidorDAO.findAll()
     */
    private void recargarDatos() {
        try {
            List<Repartidor> lista = repartidorDAO.findAll();
            datos.setAll(lista);
        } catch (SQLException e) {
            mostrarError("Error al cargar repartidors", e);
        }
    }

    /**
     * Búsqueda de momento hecha EN MEMORIA.
     *
     * Se carga toda la lista (findAll) y se filtra con streams.
     * Más adelante se puede cambiar para que use RepartidorDAO.search()
     * cuando lo implementéis.
     */
    private void buscarRepartidorsEnMemoria() {
        String filtro = txtBuscar.getText().trim();
        if (filtro.isEmpty()) {
            recargarDatos();
            return;
        }

        try {
            List<Repartidor> lista = repartidorDAO.findAll();
            String f = filtro.toLowerCase();

            List<Repartidor> filtrados = lista.stream()
                    .filter(c ->
                            String.valueOf(c.getId()).contains(f) ||
                                    c.getNombre().toLowerCase().contains(f) ||
                                    c.getMatricula().toLowerCase().contains(f)
                    )
                    .collect(Collectors.toList());

            datos.setAll(filtrados);
        } catch (SQLException e) {
            mostrarError("Error al buscar repartidors", e);
        }
    }


    private void buscarRepartidorsEnBBDD(){
        String filtro = txtBuscar.getText().trim();

        if ((filtro.isEmpty())){
            recargarDatos();
            return;
        }

        try {
            List<Repartidor> lista = repartidorDAO.search(filtro);
            datos.setAll(lista);

        } catch (SQLException e){
            mostrarError("Error al buscar", e);
        }

    }

    private void limpiarFormulario() {
        txtId.clear();
        txtNombre.clear();
        txtMatricula.clear();
        txtDireccion.clear();
        txtTelefono.clear();
        txtNotas.clear();
        txtId.setDisable(false);
        tabla.getSelectionModel().clearSelection();
    }

    /**
     * Guardar repartidor:
     *  - Si no existe en la BD → INSERT usando RepartidorDAO.insert()
     *  - Si existe → por ahora solo muestra un aviso.
     *
     * NOTA:
     *  - Los datos de detalle (dirección, teléfono, notas) todavía NO se guardan.
     *  - Cuando tengáis DetalleRepartidorDAO y/o RepartidorService, aquí se podrá:
     *      * insertar/actualizar también el detalle en una transacción.
     */
    private void guardarRepartidor() {
        // Validación rápida
        if (txtId.getText().isBlank() ||
                txtNombre.getText().isBlank() ||
                txtMatricula.getText().isBlank()) {

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

        Repartidor c = new Repartidor(id,
                txtNombre.getText().trim(),
                txtMatricula.getText().trim());

        // En el futuro podrías crear aquí también un DetalleRepartidor con:
        //   id, txtDireccion.getText(), txtTelefono.getText(), txtNotas.getText()
        // y pasarlo a un RepartidorService.crearRepartidorConDetalle(...)

        try {
            Repartidor existente = repartidorDAO.findById(id);

            if (existente == null) {
                // No existe → INSERT real
                repartidorDAO.insert(c);

                // TODO: cuando exista DetalleRepartidorDAO, aquí insertar también el detalle.
                //  Ejemplo futuro:
                //  detalleDAO.insert(new DetalleRepartidor(id, dir, tlf, notas));

                mostrarInfo("Insertado", "Repartidor creado correctamente.");
            } else {
                // Ya existe → aquí en el futuro iría un UPDATE.
                // TODO: cuando implementéis RepartidorDAO.update(Repartidor),
                //  y DetalleRepartidorDAO.update(DetalleRepartidor),
                //  llamad aquí a esos métodos (idealmente a través de RepartidorService).
                mostrarAlerta("Actualizar pendiente",
                        "El repartidor ya existe.\n" +
                                "Más adelante aquí haremos UPDATE desde el DAO/Service.");
            }

            recargarDatos();
            limpiarFormulario();

        } catch (SQLException e) {
            mostrarError("Error al guardar repartidor", e);
        }
    }

    /**
     * Borrar repartidor seleccionado.
     * De momento solo muestra un aviso con un TODO.
     *
     * Cuando implementéis RepartidorDAO.deleteById(int id),
     * se puede llamar aquí a ese método.
     *
     * Y cuando exista DetalleRepartidorDAO, sería buena idea borrar primero
     * el detalle del repartidor y luego el repartidor (o usar ON DELETE CASCADE
     * + transacción en un Service).
     */
    private void borrarRepartidorSeleccionado() {
        Repartidor sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Sin selección", "Selecciona un repartidor en la tabla.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar borrado");
        confirm.setHeaderText("¿Eliminar repartidor?");
        confirm.setContentText("Se borrará el repartidor con ID " + sel.getId());
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        // TODO: implementar RepartidorDAO.deleteById(int id) y llamarlo aquí.
        // TODO futuro: cuando haya DetalleRepartidorDAO, borrar primero detalle,
        //  después repartidor, o delegarlo todo a RepartidorService.deleteRepartidorCompleto(id).

        mostrarAlerta("Borrado pendiente",
                "Aún no existe deleteById en RepartidorDAO.\n" +
                        "Cuando lo implementemos, aquí se llamará al método.");

        // Ejemplo futuro:
        /*
        try {
            int borradas = repartidorDAO.deleteById(sel.getId());
            if (borradas > 0) {
                mostrarInfo("Borrado", "Repartidor eliminado.");
                recargarDatos();
                limpiarFormulario();
            } else {
                mostrarAlerta("No borrado", "No se encontró el repartidor en la BD.");
            }
        } catch (SQLException e) {
            mostrarError("Error al borrar repartidor", e);
        }
        */
    }

    private void exportarRepartidoresJson(){
        String nombreFichero = "repartidores.json";
        try{
            // nombre de fichero
            File destino = repartidorService.exportarRepartidoresAJson(nombreFichero);
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

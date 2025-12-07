package app;

import dao.ComercialDAO;
import model.Comercial;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Vista JavaFX para gestionar comercials.
 *
 * Versión preparada para trabajar más adelante con DetalleComercial,
 * pero de momento:
 *  - SOLO usa ComercialDAO (insert, findById, findAll).
 *  - La tabla muestra únicamente datos de Comercial (id, nombre, email).
 *  - Los campos de detalle (dirección, teléfono, notas) se muestran en el
 *    formulario, pero aún NO se guardan en BD.
 *
 * Cuando exista DetalleComercialDAO, podrás:
 *  - Cargar el detalle al seleccionar un comercial.
 *  - Guardar/actualizar detalle junto con el comercial.
 *  - Borrar detalle cuando borres un comercial.
 */
public class ComercialesView {

    private final BorderPane root = new BorderPane();

    // Tabla y datos
    private final TableView<Comercial> tabla = new TableView<>();
    private final ObservableList<Comercial> datos = FXCollections.observableArrayList();

    // Campos de formulario (Comercial)
    private final TextField txtId = new TextField();
    private final TextField txtNombre = new TextField();
    private final TextField txtEmail = new TextField();

    // Campos de formulario (DetalleComercial) – por ahora solo visuales
    private final TextField txtDireccion = new TextField();
    private final TextField txtTelefono  = new TextField();
    private final TextField txtNotas     = new TextField();

    // Botones CRUD
    private final Button btnNuevo    = new Button("Nuevo");
    private final Button btnGuardar  = new Button("Guardar");
    private final Button btnBorrar   = new Button("Borrar");
    private final Button btnRecargar = new Button("Recargar");

    // Búsqueda
    private final TextField txtBuscar          = new TextField();
    private final Button    btnBuscar          = new Button("Buscar");
    private final Button    btnLimpiarBusqueda = new Button("Limpiar");

    // DAO (acceso a BD)
    private final ComercialDAO comercialDAO = new ComercialDAO();

    public ComercialesView() {
        configurarTabla();
        configurarFormulario();
        configurarEventos();
        recargarDatos(); // al iniciar la vista cargamos los comercials
    }

    public Parent getRoot() {
        return root;
    }

    /* =========================================================
       CONFIGURACIÓN INTERFAZ
       ========================================================= */

    private void configurarTabla() {
        TableColumn<Comercial, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c ->
                new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()));

        TableColumn<Comercial, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));

        TableColumn<Comercial, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getEmail()));

        // ===== Columnas “placeholder” para DetalleComercial =====
        TableColumn<Comercial, String> colDireccion = new TableColumn<>("Dirección");
        colDireccion.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(""));

        TableColumn<Comercial, String> colTelefono = new TableColumn<>("Teléfono");
        colTelefono.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(""));

        TableColumn<Comercial, String> colNotas = new TableColumn<>("Notas");
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

        // ----- Comercial -----
        txtId.setPromptText("ID (entero)");
        txtNombre.setPromptText("Nombre");
        txtEmail.setPromptText("Email");

        form.add(new Label("ID:"), 0, 0);
        form.add(txtId, 1, 0);
        form.add(new Label("Nombre:"), 0, 1);
        form.add(txtNombre, 1, 1);
        form.add(new Label("Email:"), 0, 2);
        form.add(txtEmail, 1, 2);

        // ----- DetalleComercial (solo UI, sin BD de momento) -----
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
        HBox botonesCrud = new HBox(10, btnNuevo, btnGuardar, btnBorrar, btnRecargar);
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
                // Comercial
                txtId.setText(String.valueOf(newSel.getId()));
                txtNombre.setText(newSel.getNombre());
                txtEmail.setText(newSel.getEmail());
                txtId.setDisable(true); // al editar, de momento, no dejamos cambiar el ID

                // DetalleComercial (cuando exista DetalleComercialDAO se cargará desde BD)
                // TODO: cuando implementéis DetalleComercialDAO, aquí:
                //   - detalleDAO.findById(newSel.getId())
                //   - rellenar txtDireccion, txtTelefono, txtNotas con sus valores
                txtDireccion.clear();
                txtTelefono.clear();
                txtNotas.clear();
            }
        });

        btnNuevo.setOnAction(e -> limpiarFormulario());

        btnGuardar.setOnAction(e -> guardarComercial());

        btnBorrar.setOnAction(e -> borrarComercialSeleccionado());

        btnRecargar.setOnAction(e -> {
            txtBuscar.clear();
            recargarDatos();
        });

        btnBuscar.setOnAction(e -> buscarComercialsEnBBDD());

        btnLimpiarBusqueda.setOnAction(e -> {
            txtBuscar.clear();
            recargarDatos();
        });
    }

    /* =========================================================
       LÓGICA DE NEGOCIO (usando ComercialDAO actual)
       ========================================================= */

    /**
     * Carga todos los comercials desde la BD usando ComercialDAO.findAll()
     */
    private void recargarDatos() {
        try {
            List<Comercial> lista = comercialDAO.findAll();
            datos.setAll(lista);
        } catch (SQLException e) {
            mostrarError("Error al cargar comercials", e);
        }
    }

    /**
     * Búsqueda de momento hecha EN MEMORIA.
     *
     * Se carga toda la lista (findAll) y se filtra con streams.
     * Más adelante se puede cambiar para que use ComercialDAO.search()
     * cuando lo implementéis.
     */
    private void buscarComercialsEnMemoria() {
        String filtro = txtBuscar.getText().trim();
        if (filtro.isEmpty()) {
            recargarDatos();
            return;
        }

        try {
            List<Comercial> lista = comercialDAO.findAll();
            String f = filtro.toLowerCase();

            List<Comercial> filtrados = lista.stream()
                    .filter(c ->
                            String.valueOf(c.getId()).contains(f) ||
                                    c.getNombre().toLowerCase().contains(f) ||
                                    c.getEmail().toLowerCase().contains(f)
                    )
                    .collect(Collectors.toList());

            datos.setAll(filtrados);
        } catch (SQLException e) {
            mostrarError("Error al buscar comercials", e);
        }
    }


    private void buscarComercialsEnBBDD(){
        String filtro = txtBuscar.getText().trim();

        if ((filtro.isEmpty())){
            recargarDatos();
            return;
        }

        try {
            List<Comercial> lista = comercialDAO.search(filtro);
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
     * Guardar comercial:
     *  - Si no existe en la BD → INSERT usando ComercialDAO.insert()
     *  - Si existe → por ahora solo muestra un aviso.
     *
     * NOTA:
     *  - Los datos de detalle (dirección, teléfono, notas) todavía NO se guardan.
     *  - Cuando tengáis DetalleComercialDAO y/o ComercialService, aquí se podrá:
     *      * insertar/actualizar también el detalle en una transacción.
     */
    private void guardarComercial() {
        // Validación rápida
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

        Comercial c = new Comercial(id,
                txtNombre.getText().trim(),
                txtEmail.getText().trim());

        // En el futuro podrías crear aquí también un DetalleComercial con:
        //   id, txtDireccion.getText(), txtTelefono.getText(), txtNotas.getText()
        // y pasarlo a un ComercialService.crearComercialConDetalle(...)

        try {
            Comercial existente = comercialDAO.findById(id);

            if (existente == null) {
                // No existe → INSERT real
                comercialDAO.insert(c);

                // TODO: cuando exista DetalleComercialDAO, aquí insertar también el detalle.
                //  Ejemplo futuro:
                //  detalleDAO.insert(new DetalleComercial(id, dir, tlf, notas));

                mostrarInfo("Insertado", "Comercial creado correctamente.");
            } else {
                // Ya existe → aquí en el futuro iría un UPDATE.
                // TODO: cuando implementéis ComercialDAO.update(Comercial),
                //  y DetalleComercialDAO.update(DetalleComercial),
                //  llamad aquí a esos métodos (idealmente a través de ComercialService).
                mostrarAlerta("Actualizar pendiente",
                        "El comercial ya existe.\n" +
                                "Más adelante aquí haremos UPDATE desde el DAO/Service.");
            }

            recargarDatos();
            limpiarFormulario();

        } catch (SQLException e) {
            mostrarError("Error al guardar comercial", e);
        }
    }

    /**
     * Borrar comercial seleccionado.
     * De momento solo muestra un aviso con un TODO.
     *
     * Cuando implementéis ComercialDAO.deleteById(int id),
     * se puede llamar aquí a ese método.
     *
     * Y cuando exista DetalleComercialDAO, sería buena idea borrar primero
     * el detalle del comercial y luego el comercial (o usar ON DELETE CASCADE
     * + transacción en un Service).
     */
    private void borrarComercialSeleccionado() {
        Comercial sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Sin selección", "Selecciona un comercial en la tabla.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar borrado");
        confirm.setHeaderText("¿Eliminar comercial?");
        confirm.setContentText("Se borrará el comercial con ID " + sel.getId());
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        // TODO: implementar ComercialDAO.deleteById(int id) y llamarlo aquí.
        // TODO futuro: cuando haya DetalleComercialDAO, borrar primero detalle,
        //  después comercial, o delegarlo todo a ComercialService.deleteComercialCompleto(id).

        mostrarAlerta("Borrado pendiente",
                "Aún no existe deleteById en ComercialDAO.\n" +
                        "Cuando lo implementemos, aquí se llamará al método.");

        // Ejemplo futuro:
        /*
        try {
            int borradas = comercialDAO.deleteById(sel.getId());
            if (borradas > 0) {
                mostrarInfo("Borrado", "Comercial eliminado.");
                recargarDatos();
                limpiarFormulario();
            } else {
                mostrarAlerta("No borrado", "No se encontró el comercial en la BD.");
            }
        } catch (SQLException e) {
            mostrarError("Error al borrar comercial", e);
        }
        */
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

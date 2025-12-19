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
import services.ComercialService;


import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Vista JavaFX para la gestión básica de {@link Comercial}.
 *
 * <p>Esta clase encapsula la construcción de la interfaz, el registro de eventos y la coordinación
 * con la capa de acceso a datos/servicios para listar, crear y buscar comerciales.</p>
 *
 * <h2>Responsabilidades</h2>
 * <ul>
 *   <li>Construir la UI: tabla, formulario, zona de búsqueda y botones.</li>
 *   <li>Sincronizar selección de la tabla → campos del formulario.</li>
 *   <li>Delegar lectura/escritura en {@link dao.ComercialDAO}.</li>
 *   <li>Delegar exportación a JSON en {@link services.ComercialService}.</li>
 *   <li>Mostrar feedback al usuario mediante {@link javafx.scene.control.Alert}.</li>
 * </ul>
 *
 * <h2>Limitaciones actuales / evolución</h2>
 * <ul>
 *   <li>Los campos de detalle (dirección, teléfono, notas) son solo UI: no se guardan en BD.</li>
 *   <li>La eliminación real está pendiente ({@code deleteById}).</li>
 *   <li>La actualización (UPDATE) está pendiente: actualmente solo inserta si no existe.</li>
 * </ul>
 *
 * <p><b>Nota de JavaFX:</b> esta vista debe usarse desde el JavaFX Application Thread, ya que
 * modifica controles y estado de la escena.</p>
 *
 * @see Comercial
 * @see dao.ComercialDAO
 * @see services.ComercialService
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

    private final TextField txtTelefono  = new TextField();


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
    private final ComercialDAO comercialDAO = new ComercialDAO();

    // Service (exportacion a JSON)
    private final ComercialService comercialService = new ComercialService();

    /**
     * Crea la vista, construye la interfaz, registra eventos y carga el listado inicial.
     *
     * <p>Durante la construcción se llama a:
     * {@link #configurarTabla()}, {@link #configurarFormulario()},
     * {@link #configurarEventos()} y {@link #recargarDatos()}.</p>
     */
    public ComercialesView() {
        configurarTabla();
        configurarFormulario();
        configurarEventos();
        recargarDatos(); // al iniciar la vista cargamos los comerciales
    }

    /**
     * Devuelve el nodo raíz de la vista para integrarlo en una escena o layout externo.
     *
     * @return nodo raíz ({@link BorderPane}) que contiene tabla y formulario.
     */
    public Parent getRoot() {
        return root;
    }

    /* =========================================================
       CONFIGURACIÓN INTERFAZ
       ========================================================= */

    /**
     * Configura la tabla principal: define columnas, enlaza propiedades y asigna la lista observable.
     *
     * <p>Incluye columnas "placeholder" para datos de detalle (dirección/teléfono/notas) que por ahora
     * no están conectadas a BD.</p>
     *
     * @implNote Este método modifica el layout principal llamando a {@code root.setCenter(tabla)}.
     */
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


        TableColumn<Comercial, String> colTelefono = new TableColumn<>("Teléfono");
        colTelefono.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getTelefono()));



        tabla.getColumns().addAll(colId, colNombre, colEmail,
                colTelefono);
        tabla.setItems(datos);

        root.setCenter(tabla);
    }

    /**
     * Construye el formulario inferior con campos de {@link Comercial}, campos de detalle (solo UI),
     * la zona de búsqueda y los botones de acción.
     *
     * @implNote Este método configura hints visuales usando {@code setPromptText(...)}
     * y coloca el bloque inferior en {@code root.setBottom(...)}.
     */
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

        txtTelefono.setPromptText("Teléfono");



        form.add(new Label("Teléfono:"), 0, 3);
        form.add(txtTelefono, 1, 3);


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

    /**
     * Registra los manejadores de eventos:
     * <ul>
     *   <li>Selección en tabla → rellena formulario y desactiva el campo ID.</li>
     *   <li>Acciones de botones (nuevo/guardar/borrar/recargar/buscar/limpiar/exportar).</li>
     * </ul>
     *
     * <p>También limpia los campos de detalle al seleccionar un comercial, a la espera de integrar
     * la carga real de detalle desde BD.</p>
     */
    private void configurarEventos() {
        // Cuando seleccionamos una fila en la tabla, pasamos los datos al formulario
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                // Comercial
                txtId.setText(String.valueOf(newSel.getId()));
                txtNombre.setText(newSel.getNombre());
                txtEmail.setText(newSel.getEmail());
                txtTelefono.setText(newSel.getTelefono());
                txtId.setDisable(true); // al editar, de momento, no dejamos cambiar el ID

                // DetalleComercial (cuando exista DetalleComercialDAO se cargará desde BD)
                // TODO: cuando implementéis DetalleComercialDAO, aquí:
                //   - detalleDAO.findById(newSel.getId())
                //   - rellenar txtDireccion, txtTelefono, txtNotas con sus valores



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

        btnExportarJson.setOnAction(e -> exportarComercialesJson());
    }

    /* =========================================================
       LÓGICA DE NEGOCIO (usando ComercialDAO actual)
       ========================================================= */

    /**
     * Recarga el listado completo de comerciales desde la base de datos.
     *
     * <p>Obtiene los registros mediante {@link ComercialDAO#findAll()} y actualiza la lista observable
     * usada por la tabla.</p>
     *
     * <p>Si ocurre un error SQL, se muestra un diálogo mediante {@link #mostrarError(String, Exception)}.</p>
     */
    private void recargarDatos() {
        try {
            List<Comercial> lista = comercialDAO.findAll();
            datos.setAll(lista);
        } catch (SQLException e) {
            mostrarError("Error al cargar comerciales", e);
        }
    }

    /**
     * Filtra comerciales en memoria a partir del texto de {@link #txtBuscar}.
     *
     * <p>Este enfoque:
     * <ul>
     *   <li>Carga todos los comerciales ({@link ComercialDAO#findAll()}).</li>
     *   <li>Aplica un filtrado por ID/nombre/email.</li>
     * </ul>
     * Es útil como solución rápida, pero para grandes volúmenes conviene delegar la búsqueda a BD.</p>
     *
     * @implNote La comparación no distingue mayúsculas/minúsculas para nombre y email.
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
            mostrarError("Error al buscar comerciales", e);
        }
    }

    /**
     * Busca comerciales delegando la consulta a la base de datos.
     *
     * <p>Si el filtro está vacío, recarga todos los datos. Si no, llama a
     * {@link ComercialDAO#search(String)} y actualiza la tabla con el resultado.</p>
     */
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

    /**
     * Limpia todos los campos del formulario y restablece el estado inicial de edición.
     *
     * <ul>
     *   <li>Vacía campos de comercial y de detalle.</li>
     *   <li>Vuelve a habilitar el campo ID.</li>
     *   <li>Elimina la selección actual de la tabla.</li>
     * </ul>
     */
    private void limpiarFormulario() {
        txtId.clear();
        txtNombre.clear();
        txtEmail.clear();

        txtTelefono.clear();

        txtId.setDisable(false);
        tabla.getSelectionModel().clearSelection();
    }

    /**
     * Guarda el comercial del formulario.
     *
     * <h2>Reglas actuales</h2>
     * <ul>
     *   <li>Valida que ID, nombre y email no estén vacíos.</li>
     *   <li>Convierte el ID a entero (si falla, muestra aviso).</li>
     *   <li>Si el comercial no existe ({@link ComercialDAO#findById(int)} devuelve {@code null}),
     *       inserta con {@link ComercialDAO#insert(Comercial)}.</li>
     *   <li>Si ya existe, muestra un aviso (UPDATE pendiente).</li>
     * </ul>
     *
     * <p>Los campos de detalle (dirección/teléfono/notas) aún no se persisten.</p>
     *
     * @implNote Este método muestra diálogos al usuario y, al finalizar, recarga datos y limpia el formulario.
     */
    private void guardarComercial() {
        // Validación rápida
        if (txtId.getText().isBlank() ||
                txtNombre.getText().isBlank() ||
                txtEmail.getText().isBlank() || txtTelefono.getText().isBlank()) {

            mostrarAlerta("Campos obligatorios",
                    "Debes rellenar ID, nombre, email y teléfono.");
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
                txtEmail.getText().trim(),txtTelefono.getText().trim());

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
                comercialDAO.update(c);
                mostrarInfo("Actualizado", "Comercial actualizado correctamente.");
            }

            recargarDatos();
            limpiarFormulario();

        } catch (SQLException e) {
            mostrarError("Error al guardar comercial", e);
        }
    }

    /**
     * Borra el comercial actualmente seleccionado en la tabla, solicitando confirmación previa.
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
     * <p>Nota: cuando exista persistencia del detalle (p. ej. DetalleComercialDAO), convendrá asegurar la
     * consistencia del borrado (transacción o {@code ON DELETE CASCADE}).</p>
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

//        mostrarAlerta("Borrado pendiente",
//                "Aún no existe deleteById en ComercialDAO.\n" +
//                        "Cuando lo implementemos, aquí se llamará al método.");

        // Ejemplo futuro:

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

    }

    /**
     * Exporta el listado de comerciales a un fichero JSON.
     *
     * <p>Delega la generación en {@link ComercialService#exportarComercialesAJson(String)}.
     * Si la operación finaliza correctamente, informa de la ruta absoluta del fichero.</p>
     *
     * <p>Las excepciones {@link SQLException} y {@link IOException} se capturan y se muestran
     * mediante {@link #mostrarError(String, Exception)}.</p>
     */
    private void exportarComercialesJson(){
        String nombreFichero = "comerciales.json";
        try{
            // nombre de fichero
            File destino = comercialService.exportarComercialesAJson(nombreFichero);
            String ruta = destino.getAbsolutePath();

            mostrarInfo("Exportacion correcta", "Se ha generado el fichero "+nombreFichero+ " en "+ruta);

        }catch(SQLException | IOException e){
            mostrarError("Error al exportar los datos",e);
        }
    }

    /* =========================================================
       DIÁLOGOS AUXILIARES
       ========================================================= */

    /**
     * Muestra un diálogo de error y registra el stacktrace en consola.
     *
     * @param titulo título/cabecera del diálogo para contextualizar el fallo.
     * @param e      excepción capturada.
     */
    private void mostrarError(String titulo, Exception e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    /**
     * Muestra un diálogo de advertencia al usuario.
     *
     * @param titulo  título/cabecera del aviso.
     * @param mensaje detalle del aviso.
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un diálogo informativo al usuario.
     *
     * @param titulo  título/cabecera del mensaje.
     * @param mensaje detalle informativo.
     */
    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

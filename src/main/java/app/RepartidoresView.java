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
import services.RepartidorService;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Vista JavaFX para la gestión básica de {@link Repartidor}.
 *
 * <p>Esta clase encapsula la construcción de la interfaz, el registro de eventos y la coordinación
 * con la capa de acceso a datos/servicios para listar, crear y buscar repartidores.</p>
 *
 * <h2>Responsabilidades</h2>
 * <ul>
 *   <li>Construir la UI: tabla, formulario, zona de búsqueda y botones.</li>
 *   <li>Sincronizar selección de la tabla → campos del formulario.</li>
 *   <li>Delegar lectura/escritura en {@link dao.RepartidorDAO}.</li>
 *   <li>Delegar exportación a JSON en {@link services.RepartidorService}.</li>
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
 * @see Repartidor
 * @see dao.RepartidorDAO
 * @see services.RepartidorService
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

    /**
     * Crea la vista, construye la interfaz, registra eventos y carga el listado inicial.
     *
     * <p>Durante la construcción se llama a:
     * {@link #configurarTabla()}, {@link #configurarFormulario()},
     * {@link #configurarEventos()} y {@link #recargarDatos()}.</p>
     */
    public RepartidoresView() {
        configurarTabla();
        configurarFormulario();
        configurarEventos();
        recargarDatos(); // al iniciar la vista cargamos los repartidores
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
        TableColumn<Repartidor, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c ->
                new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()));

        TableColumn<Repartidor, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));

        TableColumn<Repartidor, String> colMatricula = new TableColumn<>("Matrícula");
        colMatricula.setCellValueFactory(c ->
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

        tabla.getColumns().addAll(colId, colNombre, colMatricula,
                colDireccion, colTelefono, colNotas);
        tabla.setItems(datos);

        root.setCenter(tabla);
    }

    /**
     * Construye el formulario inferior con campos de {@link Repartidor}, campos de detalle (solo UI),
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

    /**
     * Registra los manejadores de eventos:
     * <ul>
     *   <li>Selección en tabla → rellena formulario y desactiva el campo ID.</li>
     *   <li>Acciones de botones (nuevo/guardar/borrar/recargar/buscar/limpiar/exportar).</li>
     * </ul>
     *
     * <p>También limpia los campos de detalle al seleccionar un repartidor, a la espera de integrar
     * la carga real de detalle desde BD.</p>
     */
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
     * Recarga el listado completo de repartidores desde la base de datos.
     *
     * <p>Obtiene los registros mediante {@link RepartidorDAO#findAll()} y actualiza la lista observable
     * usada por la tabla.</p>
     *
     * <p>Si ocurre un error SQL, se muestra un diálogo mediante {@link #mostrarError(String, Exception)}.</p>
     */
    private void recargarDatos() {
        try {
            List<Repartidor> lista = repartidorDAO.findAll();
            datos.setAll(lista);
        } catch (SQLException e) {
            mostrarError("Error al cargar repartidores", e);
        }
    }

    /**
     * Filtra repartidores en memoria a partir del texto de {@link #txtBuscar}.
     *
     * <p>Este enfoque:
     * <ul>
     *   <li>Carga todos los repartidores ({@link RepartidorDAO#findAll()}).</li>
     *   <li>Aplica un filtrado por ID/nombre/matrícula.</li>
     * </ul>
     * Es útil como solución rápida, pero para grandes volúmenes conviene delegar la búsqueda a BD.</p>
     *
     * @implNote La comparación no distingue mayúsculas/minúsculas para nombre y matrícula.
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
            mostrarError("Error al buscar repartidores", e);
        }
    }


    /**
     * Busca repartidores delegando la consulta a la base de datos.
     *
     * <p>Si el filtro está vacío, recarga todos los datos. Si no, llama a
     * {@link RepartidorDAO#search(String)} y actualiza la tabla con el resultado.</p>
     */
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

    /**
     * Limpia todos los campos del formulario y restablece el estado inicial de edición.
     *
     * <ul>
     *   <li>Vacía campos de repartidor y de detalle.</li>
     *   <li>Vuelve a habilitar el campo ID.</li>
     *   <li>Elimina la selección actual de la tabla.</li>
     * </ul>
     */
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
     * Guarda el repartidor del formulario.
     *
     * <h2>Reglas actuales</h2>
     * <ul>
     *   <li>Valida que ID, nombre y matrícula no estén vacíos.</li>
     *   <li>Convierte el ID a entero (si falla, muestra aviso).</li>
     *   <li>Si el repartidor no existe ({@link RepartidorDAO#findById(int)} devuelve {@code null}),
     *       inserta con {@link RepartidorDAO#insert(Repartidor)}.</li>
     *   <li>Si ya existe, muestra un aviso (UPDATE pendiente).</li>
     * </ul>
     *
     * <p>Los campos de detalle (dirección/teléfono/notas) aún no se persisten.</p>
     *
     * @implNote Este método muestra diálogos al usuario y, al finalizar, recarga datos y limpia el formulario.
     */
    private void guardarRepartidor() {
        // Validación rápida
        if (txtId.getText().isBlank() ||
                txtNombre.getText().isBlank() ||
                txtMatricula.getText().isBlank()) {

            mostrarAlerta("Campos obligatorios",
                    "Debes rellenar ID, nombre y matrícula.");
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
     * Solicita confirmación y gestiona el borrado del repartidor seleccionado en la tabla.
     *
     * <p>Actualmente el borrado real está pendiente de implementar. En el futuro debería llamar a
     * {@code RepartidorDAO.deleteById(int)} o delegar en un service (idealmente con transacción si hay detalle asociado).</p>
     *
     * <p>Si no hay selección, se muestra un aviso.</p>
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

//        mostrarAlerta("Borrado pendiente",
//                "Aún no existe deleteById en RepartidorDAO.\n" +
//                        "Cuando lo implementemos, aquí se llamará al método.");

        // Ejemplo futuro:

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

    }

    /**
     * Exporta el listado de repartidores a un fichero JSON.
     *
     * <p>Delega la generación en {@link RepartidorService#exportarRepartidoresAJson(String)}.
     * Si la operación finaliza correctamente, informa de la ruta absoluta del fichero.</p>
     *
     * <p>Las excepciones {@link SQLException} y {@link IOException} se capturan y se muestran
     * mediante {@link #mostrarError(String, Exception)}.</p>
     */
    private void exportarRepartidoresJson(){
        String nombreFichero = "repartidores.json";
        try{
            // nombre de fichero
            File destino = repartidorService.exportarRepartidoresAJson(nombreFichero);
            String ruta = destino.getAbsolutePath();

            mostrarInfo("Exportacion correcta", "Se ha generado el fichero "+nombreFichero+" en "+ruta);

        }catch(SQLException | IOException e){
            mostrarError("Error al exportar repartidores",e);
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

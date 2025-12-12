package app;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;


/**
 * Vista principal (dashboard) que actúa como contenedor y punto de navegación entre las distintas vistas
 * de la aplicación (Clientes, Comerciales y Repartidores).
 *
 * <p>Esta clase existe para centralizar:
 * <ul>
 *   <li>El layout común (sidebar + zona de contenido).</li>
 *   <li>La creación y reutilización de las vistas internas.</li>
 *   <li>La navegación entre vistas mediante botones.</li>
 * </ul>
 * De este modo, cada vista mantiene su lógica/controles y el dashboard se encarga de integrarlas en una única pantalla.</p>
 *
 * @apiNote Uso típico:
 * <ul>
 *   <li>Crear una instancia de {@code DashboardView}.</li>
 *   <li>Insertar {@link #getRoot()} en una {@code Scene}.</li>
 * </ul>
 *
 * @implNote Las vistas internas se instancian una sola vez y se reutilizan para mantener estado (p. ej. selección,
 * texto introducido, scroll, etc.). Si prefieres “refrescar” cada vez, deberías crear nuevas instancias al navegar
 * o proporcionar métodos de reset/recarga en cada vista.
 *
 * @see ClientesView
 * @see ComercialesView
 * @see RepartidoresView
 */
public class DashboardView {

    private final BorderPane root = new BorderPane();
    private final BorderPane contentPane = new BorderPane();

    // Vistas integradas en el dashboard (reutilizadas)
    private final ClientesView clientesView = new ClientesView();
    private final ComercialesView comercialesView = new ComercialesView();
    private final RepartidoresView repartidoresView = new RepartidoresView(); // nueva

    /**
     * Construye el dashboard, inicializa el layout y muestra la vista por defecto.
     *
     * <p>Durante la construcción se llama a {@link #initLayout()} y se establece como vista inicial
     * {@link #showClientes()}.</p>
     */
    public DashboardView() {
        initLayout();
        showClientes(); // vista por defecto
    }

    /**
     * Devuelve el nodo raíz del dashboard para integrarlo en una {@code Scene} u otro contenedor.
     *
     * @return nodo raíz ({@link BorderPane}) del dashboard.
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * Inicializa el layout principal del dashboard:
     * <ul>
     *   <li>Configura estilos y padding del contenedor raíz.</li>
     *   <li>Construye la barra lateral con el título y los botones de navegación.</li>
     *   <li>Prepara el contenedor central ({@link #contentPane}) donde se cargan las vistas.</li>
     *   <li>Registra los eventos de los botones para cambiar de vista.</li>
     * </ul>
     *
     * @implNote Este método define la estructura general de la pantalla:
     * sidebar en {@code root.setLeft(...)} y contenido en {@code root.setCenter(...)}.
     */
    private void initLayout() {
        root.setPadding(new Insets(15));
        root.getStyleClass().add("dashboard-root");

        // =================== LADO IZQUIERDO (MENÚ) ===================

        // TITULO
        Label menuTitle = new Label("Lampreas Violeta");
        menuTitle.getStyleClass().add("sidebar-title");
        menuTitle.setFont(Font.font(18));

        //BOTONES
        Button btnClientes      = new Button("Clientes");
        Button btnComerciales   = new Button("Comerciales");
        Button btnRepartidores  = new Button("Repartidores");
        VBox.setMargin(btnClientes, new Insets(0,0,30,0));
        VBox.setMargin(btnComerciales, new Insets(0,0,30,0));
        VBox.setMargin(btnRepartidores, new Insets(0,0,30,0));
        btnClientes.getStyleClass().add("sidebar-button");
        btnComerciales.getStyleClass().add("sidebar-button");
        btnRepartidores.getStyleClass().add("sidebar-button");
        btnClientes.setMaxWidth(Double.MAX_VALUE);
        btnComerciales.setMaxWidth(Double.MAX_VALUE);
        btnRepartidores.setMaxWidth(Double.MAX_VALUE);

        // VBox para botones, centrado
        VBox botonesBox = new VBox(15, btnClientes, btnComerciales, btnRepartidores);
        botonesBox.setAlignment(Pos.CENTER);
        botonesBox.setFillWidth(true);

        // Spacer flexible entre titulo y botones
        Region spacer = new Region();
        spacer.setPrefHeight(150);

        // VBox lateral completo (titulo arriba + espacio + botones centrados)
        VBox sidebarInner = new VBox();
        sidebarInner.setAlignment(Pos.TOP_CENTER);
        sidebarInner.getStyleClass().add("sidebar-inner");
        sidebarInner.getChildren().addAll(menuTitle,spacer,botonesBox);


        StackPane sidebarWrapper = new StackPane(sidebarInner);
        sidebarWrapper.getStyleClass().add("sidebar-wrapper");
        sidebarWrapper.setPrefWidth(220);
        root.setLeft(sidebarWrapper);
        BorderPane.setMargin(sidebarWrapper, new Insets(0, 20, 0, 0));


        // =================== ZONA CENTRAL (TABLE VIEW) ===================
        StackPane contentWrapper = new StackPane(contentPane);
        contentWrapper.getStyleClass().add("content-wrapper");
        root.setCenter(contentWrapper);

        // =================== EVENTOS BOTONES ===================
        btnClientes.setOnAction(e -> showClientes());
        btnComerciales.setOnAction(e -> showComerciales());
        btnRepartidores.setOnAction(e -> showRepartidores());
    }

    /**
     * Cambia la vista activa del dashboard.
     *
     * <p>Inserta el nodo recibido en el centro de {@link #contentPane}.</p>
     *
     * @param viewRoot nodo raíz de la vista que se quiere mostrar.
     *
     * @implNote Este método no crea vistas nuevas; únicamente “monta” en pantalla el nodo recibido.
     * Si necesitas un refresco forzado (p. ej. recargar datos al navegar), hazlo en el método showWhatever()
     * antes de llamar a {@code show(...)} o añade un método {@code refresh()} en cada vista.
     */
    private void show(Parent viewRoot){
        contentPane.setCenter(viewRoot);
    }

    /**
     * Muestra la vista de clientes en la zona central del dashboard.
     *
     * @implNote Reutiliza la misma instancia de {@link ClientesView} (mantiene estado).
     */
    private void showClientes() {

        show(clientesView.getRoot());
    }

    /**
     * Muestra la vista de comerciales en la zona central del dashboard.
     *
     * @implNote Reutiliza la misma instancia de {@link ComercialesView} (mantiene estado).
     */
    private void showComerciales() {

        show(comercialesView.getRoot());
    }

    /**
     * Muestra la vista de repartidores en la zona central del dashboard.
     *
     * @implNote Reutiliza la misma instancia de {@link RepartidoresView} (mantiene estado).
     */
    private void showRepartidores() {

        show(repartidoresView.getRoot());
    }
}

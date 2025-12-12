package app;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class DashboardView {

    private final BorderPane root = new BorderPane();
    private final BorderPane contentPane = new BorderPane();

    // Vistas que ya tienes
    private final ClientesView clientesView = new ClientesView();
    private final ComercialesView comercialesView = new ComercialesView();
    private final RepartidoresView repartidoresView = new RepartidoresView(); // nueva

    public DashboardView() {
        initLayout();
        showClientes(); // vista por defecto
    }

    public Parent getRoot() {
        return root;
    }

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

    private void showClientes() {

        contentPane.setCenter(clientesView.getRoot());
    }

    private void showComerciales() {

        contentPane.setCenter(comercialesView.getRoot());
    }

    private void showRepartidores() {
        contentPane.setCenter(repartidoresView.getRoot());
    }
}

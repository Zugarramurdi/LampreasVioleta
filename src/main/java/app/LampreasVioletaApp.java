package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Paths;

public class LampreasVioletaApp extends Application {
    @Override
    public void start(Stage stage) {
//        ClientesView vistaClientes = new ClientesView();
//        Scene scene = new Scene(vistaClientes.getRoot(), 900, 600);
//        stage.setTitle("Gestión de Clientes - Lampreas Violeta");
//        stage.setScene(scene);
//        stage.show();

//        ComercialesView vistaComerciales = new ComercialesView();
//        Scene scene2 = new Scene(vistaComerciales.getRoot(), 900, 600);
//        stage.setTitle("Gestión de Comerciales - Lampreas Violeta");
//        stage.setScene(scene2);
//        stage.show();

        DashboardView dashboard = new DashboardView();
        Scene scene = new Scene(dashboard.getRoot(), 1100,650);

       String cssPath = Paths.get("src\\main\\java\\styles\\dashboard.css").toUri().toString();
        System.out.println("cssPath: " + cssPath);
        scene.getStylesheets().add(cssPath);
        // EStilos? AQUI
//        scene.getStylesheets().add(
//                getClass().getResource("dashboard.css").toExternalForm()
//        );

        stage.setTitle("Lampreas Violeta - Dashboard");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}

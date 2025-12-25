package ma.tp.gestionacademique;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ma/tp/gestionacademique/main.fxml")
        );

        Scene scene = new Scene(loader.load());

        // ✅ AJOUT DU CSS ICI
        scene.getStylesheets().add(
                getClass().getResource("/ma/tp/gestionacademique/style.css").toExternalForm()
        );

        stage.setTitle("Gestion Académique");
        stage.setScene(scene);
        stage.show();
    }

    // Méthode pour changer de scène facilement
    public static void setScene(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxml));
            Scene scene = new Scene(loader.load());

            // ✅ AJOUT DU CSS ICI AUSSI
            scene.getStylesheets().add(
                    MainApp.class.getResource("/ma/tp/gestionacademique/style.css").toExternalForm()
            );

            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}

package fintrack.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FinApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fintrack/view/principal.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("FinTrack - Gestor de Finanças");
        primaryStage.show();
    }
    public static void main(String[] args) { launch(args); }
}

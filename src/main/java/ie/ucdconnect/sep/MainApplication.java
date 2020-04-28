package ie.ucdconnect.sep;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    private static Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        scene = new Scene(loadFXML("/sample"));
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setWidth(300);
        primaryStage.setHeight(300);
        primaryStage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

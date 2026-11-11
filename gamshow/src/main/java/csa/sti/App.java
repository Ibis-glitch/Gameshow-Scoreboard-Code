//Main App Class
//Period 6- CSA-Commander Schenk
//5 November 2026
package csa.sti;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/** Starts the JavaFX application and loads screens from their FXML layouts. */
public class App extends Application {

    // Kept as shared state so navigation can replace the current screen.
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // The landing page is the first screen shown when the application opens.
        scene = new Scene(loadFXML("landing"), 1000, 700);
        stage.setTitle("Select That Insertion!");
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        // Replacing the root keeps navigation in the same window and scene.
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        // FXML files are resolved relative to this class in the csa.sti package.
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    
    // Delegate startup and JavaFX lifecycle management to Application.
    public static void main(String[] args) {
        launch();
    }
}
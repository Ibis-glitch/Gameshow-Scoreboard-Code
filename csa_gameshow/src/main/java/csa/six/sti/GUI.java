package csa.six.sti;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GUI extends Application{

    @Override
    public void start(Stage primaryStage) {
        //UI Controls
        Label messageLabel = new Label("Starter Screen");
        messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");

        Button actionButton = new Button("Test Button");

        //Handle events (button click interaction)
        actionButton.setOnActio(event -> {
            messageLabel.setText("Button clicked");
        });

        //Create layout container
        VBox rootLayout = new VBox(15);
        rootLayout.setAlignment(Pos.CENTER);
        rootLayout.getChildren().addAll(messageLabel, actionButton);
        
        //Create scene
        Scene scene = new Scene(rootLayout, 350, 200);

        //Set up and display stage
        primaryStage.setTitle("Gameshow");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    launch(args);
}

//Install javafx Ig bro

package csa.sti;

import java.io.IOException;
import javafx.fxml.FXML;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FastMoney extends Application {

    @FXML
    private void switchToLanding() throws IOException {
        App.setRoot("landing");
    }

    //Contains all of the actual visual aspects of the fast money board
    @Override
    public void start(Stage stage) {

        //Creates a label at the top
        Label title = new Label("FAST MONEY");
        title.setStyle(
            "-fx-font-size: 40px;" + "-fx-font-weight: bold;" + "-fx-text-fill: gold;");

        //Defines the base for the grid
        GridPane grid = new GridPane();

        //Provides some space in between the labels we will make
        grid.setHgap(30);
        grid.setVgap(15);

        //Creates headers for each column
        Label questionHeader = new Label("QUESTION");
        Label answerHeader = new Label("ANSWER");
        Label pointsHeader = new Label("POINTS");

        questionHeader.setStyle("-fx-text-fill: gold; -fx-font-size: 18px;");
        answerHeader.setStyle("-fx-text-fill: gold; -fx-font-size: 18px;");
        pointsHeader.setStyle("-fx-text-fill: gold; -fx-font-size: 18px;");

        grid.add(questionHeader, 0, 0);
        grid.add(answerHeader, 1, 0);
        grid.add(pointsHeader, 2, 0);

        //This loop creates 5 rows of questions, answers, and points and places them
        //in a grid
        for (int x = 0; x < 5; x++) {

            //This label will represent the question
            Label question = new Label("Question " + (x + 1));
            question.setStyle("-fx-text-fill: white;" + "-fx-font-size: 18px;");

            //This label is a box representing the answer
            Label answer = new Label("        ");
            answer.setStyle("-fx-background-color: black;" + "-fx-text-fill: white;" + "-fx-border-color: gold;" + "-fx-border-width: 2;" + "-fx-padding: 5 25 5 25;");

            //This label represents the score alloted based on the answer
            Label points = new Label("0");
            points.setStyle("-fx-text-fill: gold;" + "-fx-font-size: 18px;");

            //Organizes them
            grid.add(question, 0, x + 1);
            grid.add(answer, 1, x + 1);
            grid.add(points, 2, x + 1);
        }

        //Creates a border around the board
        grid.setStyle( "-fx-border-color: gold;" + "-fx-border-width: 4;" + "-fx-padding: 20;");

        //Shows the total score achieved by the player
        Label total = new Label("TOTAL: 0");
        total.setStyle("-fx-font-size: 32px;" + "-fx-font-weight: bold;" + "-fx-text-fill: gold;");

        //Stacks our labels and grids vertically
        VBox root = new VBox();

        root.getChildren().addAll(title, grid, total);

        root.setSpacing(20);

        //Simply centers all of our labels and grids
        root.setAlignment(Pos.CENTER);
        grid.setAlignment(Pos.CENTER);

        //Creates a dark blue background similar to Family Feud
        root.setStyle("-fx-background-color: #001f6b;");

        //Forms the 800x500 scene that contains all the previously established labels
        //and the grid
        Scene scene = new Scene(root, 800, 500);

        //Establishes the scene and sets the title
        stage.setScene(scene);
        stage.setTitle("Fast Money");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
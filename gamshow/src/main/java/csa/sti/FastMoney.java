package csa.sti;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FastMoney extends Application{

    //Contains all of the actual visual aspects of the fast money board
    @Override
    public void start (Stage stage){
        //Creates a label at the top
        Label title = new Label("Fast Money");
        title.setStyle("-fx-font-size: 30px;");

        //Defines the base for the grid
        GridPane grid = new GridPane();
        //Provides some space in between the labels we will make
        grid.setHgap(20);
        grid.setVgap(10);

        //This loop creates 5 rows of questions, answers, and points and places them
        //in a grid
        for(int x=0; x<5; x++){
            Label question = new Label("Question" + (x+1));
            Label answer = new Label("________");
            Label points = new Label("0");
            grid.add(question, 0, x);
            grid.add(answer, 1, x);
            grid.add(points, 2, x);
        }

        //Shows the total score achieved by the player
        Label total = new Label("Total: 0");

        //Stacks our labels and grids vertically
        VBox root = new VBox();
        root.getChildren().addAll(title, grid, total);
        root.setSpacing(20);

        //Simply centers all of our labels and grids
        root.setAlignment(Pos.CENTER);
        grid.setAlignment(Pos.CENTER);

        //Forms the 800x500 scene that contains all the previously established labels
        //and the grid
        Scene scene = new Scene(root, 800, 500);
        stage.setScene(scene);
        stage.setTitle("Fast Money");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

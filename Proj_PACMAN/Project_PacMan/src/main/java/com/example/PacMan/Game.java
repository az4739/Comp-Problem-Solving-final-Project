package com.example.PacMan;

import javafx.application.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.geometry.*;;

public class Game extends Application implements EventHandler<ActionEvent> {
    // creating a stages and scenes for starting screen and instructions
    private Stage stage;
    private Scene starting;
    private Scene instruc;
    private VBox root;

    private static String[] args;

    final double LABEL_SIZE = 40.00;

    /** creating buttons for starting screen */
    private Button btnStart = new Button("START");
    private Button btnExit = new Button("EXIT");
    private Button btnHow = new Button("HOW TO PLAY?");
    private Label lblTitle = new Label("PACMAN");

    /** creating text and buttons for instructions page */
    private Text instructions = new Text("");
    private Button btnClose = new Button("CLOSE");

    /** main method */

    public static void main(String[] _args) {
        args = _args;
        launch(args);
    }

    /** setting the primary stage */

    public void start(Stage primaryStage) throws Exception {
        /** stage seteup */
        stage = primaryStage;

        stage.setTitle("Game2D Starter");
        stage.setOnCloseRequest(
                new EventHandler<WindowEvent>() {
                    public void handle(WindowEvent evt) {
                        System.exit(0);
                    }
                });

        /** describing the starting scene */
        VBox root = new VBox(30);
        root.setStyle("-fx-background-color: black;");

        /** first row- title for the game (PACMAN) */
        FlowPane fpTitle = new FlowPane();
        lblTitle.setStyle("-fx-text-fill: white;");
        fpTitle.setAlignment(Pos.CENTER);
        fpTitle.getChildren().add(lblTitle);
        lblTitle.setFont(new Font(LABEL_SIZE));

        /** second row - first button ( start) */
        FlowPane fpStart = new FlowPane();
        fpStart.setAlignment(Pos.CENTER);
        fpStart.getChildren().add(btnStart);
        btnStart.setStyle("-fx-background-color: black; -fx-border-color:lightgreen; -fx-text-fill: white;");
        btnStart.setPrefSize(200, 70);

        /** third row - how to play? */
        FlowPane fpHow = new FlowPane();
        fpHow.setAlignment(Pos.CENTER);
        fpHow.getChildren().add(btnHow);
        btnHow.setStyle("-fx-background-color: black; -fx-border-color:orange; -fx-text-fill: white;");
        btnHow.setPrefSize(200, 70);

        /** 4th row -exit the game */
        FlowPane fpExit = new FlowPane();
        fpExit.setAlignment(Pos.CENTER);
        fpExit.getChildren().add(btnExit);
        btnExit.setStyle("-fx-background-color: black; -fx-border-color:yellow; -fx-text-fill: white;");
        btnExit.setPrefSize(200, 70);

        /** setting buttons on action */
        btnStart.setOnAction(this);
        btnHow.setOnAction(this);
        btnExit.setOnAction(this);
        btnClose.setOnAction(this);

        /** adding to root */
        root.getChildren().addAll(fpTitle, fpStart, fpHow, fpExit);

        starting = new Scene(root, 800, 500);
        starting.getStylesheets().add("style.css");

        starting.setFill(Color.BLACK);
        stage.setScene(starting);
        stage.show();
    }

    /**
     * INSTRUCTIONS Scene
     */
    public void showInstructions() {
        VBox root2 = new VBox();

        /**
         * describing the instructions scene
         * //text
         */
        FlowPane fpInst = new FlowPane();
        fpInst.setAlignment(Pos.CENTER);
        instructions.setStyle("-fx-text-fill: white;");
        instructions.setFill(Color.WHITE);

        fpInst.getChildren().add(instructions);
        /** button for closing and returning to the starting screen */
        FlowPane fpInst2 = new FlowPane();
        fpInst2.setAlignment(Pos.CENTER);
        fpInst2.getChildren().add(btnClose);

        /** adding to root */
        root2.getChildren().addAll(fpInst, fpInst2);
        root2.setStyle("-fx-background-color: black;");
        instruc = new Scene(root2, 800, 500);
        stage.setScene(instruc);
        stage.show();
        instruc.getStylesheets().add("style.css");

        /** text */
        instructions.setText("  PACMAN is a simple game"
                + "\n    The player is represented by a yellow PACMAN"
                + "\n    To move, use WASD (W for up, A for left, S for down, or D for right,"
                + "\n   or 'up', 'down', 'left', or 'right' buttons on the keyboard."
                + "\n"
                + "\n   To grow the score, the player has to collect hearts."
                + " \n   The player has to avoid ghosts that are moving randomly."
                + " \n   At the beginning of each game, the payer starts off with 3 lives."
                + "\n   When the player looses all three lives, the game ends,"
                + "\n  and the player goes back to the opening screen"
                + "\n\n   MULTIPLAER"
                + "\n   When in multiplayer mode, another PACMAN is added."
                + "\n   The person hosting the game is spawned as a YELLOW pacman, while the second player is spawned as a WHITE pacman"
                + "The   winner is the plyer who survives longer"
                + "\n\n POWER-UP"
                + "\n   When a player collects a power-up ( a can of RedBull), the pacman increases speed"
                + "\n\n\n");

    }

    /** switch case for buttons */

    public void handle(ActionEvent avt) {
        Button btn = (Button) avt.getSource();
        switch (btn.getText()) {
            case "START":
                // new Game2DSTARTER(args);
                break;
            case "EXIT":
                System.exit(0);
                break;

            case "HOW TO PLAY?":
                showInstructions();

                /** showing instructions for playing */
                break;
            case "CLOSE":
                stage.setScene(starting);
                break;

        }

    }

}

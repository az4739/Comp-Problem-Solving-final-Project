package com.example.PacMan;

import java.io.*;
import javafx.application.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.geometry.*;

import java.io.*;
import java.net.*;
import java.util.*;

import com.example.PacMan.Game2DSTARTER.PacmanRacer;

/**
 * RegistrationServerStarter - Allows clients to register and find out who is
 * registered.
 * Name: <your name>
 * Course/Section: ISTE-121-<section>
 * Practice Practical #2
 * Date: <today’s date>
 */

public class Server extends Application implements EventHandler<ActionEvent> {
    // Window Attributes
    private Stage stage;
    private Scene scene;
    private VBox root = null;

    // GUI components
    private TextArea taList = new TextArea();
    private Button btnClear = new Button("Clear");

    // socket
    private static final int SERVER_PORT = 32002;
    List<ObjectOutputStream> nameOfWriters = new ArrayList<>();

    int clientIDCounter = 0;

    /** Main program */
    public static void main(String[] args) {
        launch(args);
    }

    /** start the server */
    public void start(Stage _stage) {
        stage = _stage;
        stage.setTitle("Registration Server (YOUR_NAME)");
        final int WIDTH = 450;
        final int HEIGHT = 400;
        final int X = 550;
        final int Y = 100;

        stage.setX(X);
        stage.setY(Y);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent evt) {
                System.exit(0);
            }
        });

        // Set up root
        root = new VBox();

        // Put clear button in North
        HBox hbNorth = new HBox();
        hbNorth.setAlignment(Pos.CENTER);
        hbNorth.getChildren().add(btnClear);

        // Set up rootis
        root.getChildren().addAll(hbNorth, taList);
        for (Node n : root.getChildren()) {
            VBox.setMargin(n, new Insets(10));
        }

        // Set the scene and show the stage
        scene = new Scene(root, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.show();

        // Adjust size of TextArea
        taList.setPrefHeight(HEIGHT - hbNorth.getPrefHeight());

        // do Server Stuff
        doServerStuff();
    }

    /** Server action */
    private void doServerStuff() {
        ServerThread st = new ServerThread();
        st.start();
    }

    // ServerThread
    class ServerThread extends Thread {
        @Override
        public void run() {
            try {
                System.out.println("Openning SOCKET PORT");
                ServerSocket sSocket = new ServerSocket(SERVER_PORT);

                while (true) {
                    System.out.println("Waiting client to connect...");
                    Socket cSocket = sSocket.accept();

                    ClientThread cT = new ClientThread(cSocket);
                    cT.start();
                }

            } catch (IOException e) {
                showAlert(AlertType.ERROR, e.getMessage());
            }
        }
    }

    // ClientThread
    class ClientThread extends Thread {
        private Socket cSocket;
        private ObjectOutputStream oos = null;
        private ObjectInputStream ois = null;

        public ClientThread(Socket cSocket) {
            this.cSocket = cSocket;
        }

        @Override
        public void run() {
            try {
                this.ois = new ObjectInputStream(this.cSocket.getInputStream());
                this.oos = new ObjectOutputStream(this.cSocket.getOutputStream());

                nameOfWriters.add(this.oos);

                while (true) {
                    Object obj = ois.readObject();
                    // messages in the format
                    // REGISTER@NAME - register command, STRING
                    // CHAT@MESSAGE - chatmessage, STRING
                    // Status package, Status class
                    if (obj instanceof String) {
                        String message = (String) obj;
                        String[] arrayOfMessage = message.split("@");
                        if (arrayOfMessage.length == 2) {
                            switch (arrayOfMessage[0]) {
                                case "REGISTER":
                                    oos.writeObject(clientIDCounter);
                                    oos.flush();
                                    clientIDCounter++;
                                    break;
                                case "CHAT":
                                    String chatMessage = arrayOfMessage[1];
                                    for (int i = 0; i < nameOfWriters.size(); i++) {
                                        nameOfWriters.get(i).writeObject(chatMessage);
                                        nameOfWriters.get(i).flush();
                                    }
                                    break;
                            }
                        }
                    } else if (obj instanceof Status) {
                        Status newStatus = (Status) obj;
                        for (int i = 0; i < nameOfWriters.size(); i++) {
                            // send to the others, and not back to me
                            if (nameOfWriters.get(i) != this.oos) {
                                nameOfWriters.get(i).writeObject(newStatus);
                                nameOfWriters.get(i).flush();

                            }
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException cnfe) {
                cnfe.getMessage();
            }
        }

    }

    /** Button handler */
    public void handle(ActionEvent ae) {
    }

    public void showAlert(AlertType type, String message) {
        Platform.runLater(new Runnable() {
            public void run() {
                Alert alert = new Alert(type, message);
                alert.showAndWait();
            }
        });
    }
}
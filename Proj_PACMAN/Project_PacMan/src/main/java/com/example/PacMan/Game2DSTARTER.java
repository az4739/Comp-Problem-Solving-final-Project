package com.example.PacMan;


import javafx.application.*;
import javafx.beans.value.ObservableValue;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.geometry.*;
import javafx.animation.*;
import java.io.*;
import java.lang.Thread.State;
import java.net.Socket;
import java.util.*;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * PackmanGEOStarter with JavaFX and Thread
 */

public class Game2DSTARTER extends Application implements EventHandler<ActionEvent> {
   // Window attributes
   private Stage stage;
   private Scene scene;
   private VBox root;

   private static String[] args;

   private final static String ICON_IMAGE = "left.gif"; // file with icon for a racer
   private final static String MULTI_IMAGE = "Wleft.gif"; // file with icon for a racer

   private final static String MAP_IMAGE = "backBlue.png"; // file with icon for a racer
   private final static String MAP_IMAGE2 = "nuMap.png"; // file with icon for a racer

   private final static String GHOST_IMAGE = "gG.png";
   private final static String HEART_IMAGE = "heart.png";
   private final static String BERI_IMAGE = "redbull.png";

   private int iconWidth; // width (in pixels) of the icon
   private int iconHeight; // height (in pixels) or the icon

   private int ghostSize = 20;

   private Label Score = null;
   private Label lives = null;

   private PacmanRacer racer = null; // array of racers
   private PacManMap mapa;
   private ghosts ghost1;
   private ghosts ghost2;
   private ghosts ghost3;

   private heart heart;
   private beri beri;

   // multiplayer

   private multi multi = null;

   private Image carImage = null;
   private Image mulImage = null;
   private Image mapImage = null;
   private Image ghostImage = null;
   private Image heartImage = null;
   private Image beriImage = null;

   private double speed = 5;

   private int heartCounter = 0;
   private int lifeCounter = 5;

   private int textX = 0;
   private int testY = 0;

   private AnimationTimer timer; // timer to control animation

   // gui stuff
   private TextField tfServer = null;
   private TextField tfName = null;
   private TextField tfMess = null;

   private VBox vboxs = null;
   private Button btnConnect = null;
   private Button btnSend = null;
   private TextArea taChat = null;

   boolean UP, DOWN, LEFT, RIGHT, SEND;

   // server stuff
   public static final int SERVER_PORT = 32002;
   private Socket socket = null;

   private ObjectOutputStream oos = null;
   private ObjectInputStream ois = null;

   private int currentID = -1;

   // multiplayer 2nd player
   boolean multiUP, multiDOWN, multiLEFT, multiRIGHT;
   private double multiSpeed = 5;
   private int multiHeartCounter = 0;
   private int multiLifeCounter = 3;

   // main program
   public static void main(String[] _args) {
      args = _args;
      launch(args);
   }

   // start() method, called via launch
   public void start(Stage _stage) {
      // stage seteup
      stage = _stage;
      stage.setTitle("Game2D Starter");
      stage.setOnCloseRequest(
            new EventHandler<WindowEvent>() {
               public void handle(WindowEvent evt) {
                  System.exit(0);
               }
            });

      // root pane
      root = new VBox();

      // create an array of Racers (Panes) and start
      initializeScene();

   }

   // start the race
   public void initializeScene() {

      // Use an animation to update the screen
      timer = new AnimationTimer() {
         public void handle(long now) {

            mapa.update();
            racer.update();
            multi.update();

            ghost1.update();
            ghost2.update();
            ghost3.update();
            heart.update();
            beri.update();

         }
      };

      // TimerTask to delay start of race for 2 seconds
      TimerTask task = new TimerTask() {
         public void run() {
            timer.start();
         }
      };

      // Make an icon image to find its size
      try {
         carImage = new Image(new FileInputStream(ICON_IMAGE));
         mulImage = new Image(new FileInputStream(MULTI_IMAGE), 30, 30, false, false);
         mapImage = new Image(new FileInputStream(MAP_IMAGE), 800, 500, false, false);
         ghostImage = new Image(new FileInputStream(GHOST_IMAGE), 20, 20, false, false);
         heartImage = new Image(new FileInputStream(HEART_IMAGE), 20, 20, false, false);
         beriImage = new Image(new FileInputStream(BERI_IMAGE), 20, 20, false, false);

      } catch (Exception e) {
         System.out.println("Exception: " + e);
         System.exit(1);
      }

      // Get image size
      iconWidth = (int) carImage.getWidth();
      iconHeight = (int) carImage.getHeight();

      Score = new Label("Score: " + heartCounter);
      Score.setMaxWidth(700);

      Score.setTextFill(Color.web("#ffff", 1));

      lives = new Label("Lives left:" + lifeCounter);
      lives.setTextFill(Color.web("#ffff", 1));
      lives.setMaxWidth(300);

      racer = new PacmanRacer();
      mapa = new PacManMap();

      // second player
      multi = new multi();

      // GUI for CHAT

      vboxs = new VBox();
      btnConnect = new Button("Connect");
      btnSend = new Button("Send");
      taChat = new TextArea();
      taChat.setPrefWidth(200);
      taChat.setPrefHeight(400);

      tfServer = new TextField("localhost");
      tfName = new TextField();
      tfMess = new TextField();

      btnConnect.setDisable(true);
      taChat.setDisable(true);
      tfServer.setDisable(true);
      tfName.setDisable(true);
      tfMess.setDisable(true);
      btnSend.setDisable(true);

      btnConnect.setOnAction(this);
      btnSend.setOnAction(this);

      vboxs.getChildren().addAll(btnConnect, new Label("IP Addres:"), tfServer, new Label("Your name:"), tfName,
            new Label("Chat:"), taChat,
            new Label("Message:"), tfMess, btnSend);

      // ghosts
      ghost1 = new ghosts();

      ghost2 = new ghosts();
      ghost2.setRacePosX(200);
      ghost2.setRacePosY(250);

      ghost3 = new ghosts();
      ghost3.setRacePosX(500);
      ghost3.setRacePosY(200);

      heart = new heart();
      StackPane stats = new StackPane(Score, lives);

      beri = new beri();

      StackPane sp = new StackPane(mapa, ghost1, ghost2, ghost3, heart, beri, racer, multi, stats);

      HBox pane = new HBox();

      vboxs.requestFocus();

      pane.getChildren().addAll(sp, vboxs);

      root.getChildren().addAll(pane);
      root.setId("pane");
      // display the window
      scene = new Scene(root, 1000, 500);

      stage.setScene(scene);
      stage.show();

      System.out.println("Starting race...");

      Timer startTimer = new Timer();
      long delay = 1000L;
      startTimer.schedule(task, delay);

      // ENTER PRESSED // import javafx.scene.input.KeyEvent;
      tfMess.setOnKeyPressed(new EventHandler<KeyEvent>() {
         @Override
         public void handle(KeyEvent t) {
            if (t.getCode() == KeyCode.ENTER) {
               sendMessage();
            }

         }
      });

   }

   private void sendPacRacer(int valueX, int valueY) {
      Status pacstatus = new Status(this.currentID, valueX, valueY);
      try {
         oos.writeObject(pacstatus);
         oos.flush();
      } catch (IOException e) {
         e.printStackTrace();
      }

   }

   public void connect() {

      try {
         socket = new Socket(tfServer.getText(), SERVER_PORT);
         this.oos = new ObjectOutputStream(this.socket.getOutputStream());
         this.ois = new ObjectInputStream(this.socket.getInputStream());

         this.oos.writeObject("REGISTER@" + tfName.getText());
         this.oos.flush();
         this.currentID = (Integer) ois.readObject();

         ClientThread cT = new ClientThread();
         cT.start();

      } catch (IOException e) {

         System.out.println(e);
      } catch (ClassNotFoundException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      System.out.println("connected \n");

   }

   @Override
   public void handle(ActionEvent event) {
      Button btn = (Button) event.getSource();

      switch (btn.getText()) {

         case "Connect":
            connect();
            break;
         case "Send":
            sendMessage();
            break;
      }

   }

   private void sendMessage() {
      // NAME:MESSAGE
      try {
         oos.writeObject("CHAT@" + tfName.getText() + ":" + tfMess.getText());
         this.oos.flush();
         tfMess.clear();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

   }

   class ClientThread extends Thread {

      @Override
      public void run() {

         try {
            while (true) {
               Object obj = ois.readObject();
               if (obj instanceof String) {
                  String message = (String) obj; // chat feedback
                  System.out.println(message);

                  Platform.runLater(new Runnable() {
                     @Override
                     public void run() {
                        taChat.appendText(message + "\n");
                     }
                  });
               } else if (obj instanceof Status) {
                  Status newStatus = (Status) obj;
                  if (newStatus.getID() != currentID) {
                     switch (newStatus.getID()) {
                        case 0:
                           Platform.runLater(new Runnable() {
                              @Override
                              public void run() {

                                 textX = newStatus.getSliderStatus();
                                 testY = newStatus.getCoordinateY();

                              }
                           });
                           break;
                        case 1:
                           Platform.runLater(new Runnable() {
                              @Override
                              public void run() {

                                 textX = newStatus.getSliderStatus();
                                 testY = newStatus.getCoordinateY();

                              }
                           });
                           break;
                     }
                  }
               }

            }

         } catch (ClassNotFoundException | IOException cnfe) {
            cnfe.getMessage();
         }
      }
   }

   /**
    * Racer creates the race lane (Pane) and the ability to
    * keep itself going (Runnable)
    */

   class PacManMap extends Pane {
      Image pacManMap;
      ImageView mapImg;

      public PacManMap() {
         mapImg = new ImageView(mapImage);
         mapImg.setFitWidth(800);
         mapImg.setFitHeight(500);
         this.getChildren().add(mapImg);

      }

      public Color getColor(int x, int y) throws FileNotFoundException {

         PixelReader px = mapImage.getPixelReader();
         return px.getColor(x, y);

      }

      public void update() {
         if (heartCounter == 5) {
            try {
               mapImage = new Image(new FileInputStream(MAP_IMAGE2), 800, 500, false, false);
               mapImg = new ImageView(mapImage);
               mapImg.setFitWidth(800);
               mapImg.setFitHeight(500);
               this.getChildren().add(mapImg);

            } catch (FileNotFoundException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }

         }

      }

   }

   protected class PacmanRacer extends Pane {
      private int racePosX = 400; // x position of the racer
      private int racePosY = 250; // x position of the racer

      public int getRacePosX() {
         return this.racePosX;
      }

      public void setRacePosX(int racePosX) {
         this.racePosX = racePosX;
      }

      public int getRacePosY() {
         return this.racePosY;
      }

      public void setRacePosY(int racePosY) {
         this.racePosY = racePosY;
      }

      private int raceROT = 0; // x position of the racer
      private ImageView aPicView; // a view of the icon ... used to display and move the image
      PacManMap pacMapa = new PacManMap();
      ghosts ghosts = new ghosts();

      public PacmanRacer() {
         // Draw the icon for the racer
         aPicView = new ImageView(carImage);
         this.getChildren().add(aPicView);

      }

      /**
       * update() method keeps the thread (racer) alive and moving.
       */
      public void update() {
         // updatuje x,y dummy pacmana
         multi.setMultiPosX(textX);
         multi.setMultiPosY(testY);

         scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
               switch (event.getCode()) {
                  case UP:
                     UP = true;
                     DOWN = false;
                     LEFT = false;
                     RIGHT = false;
                     break;
                  case DOWN:
                     UP = false;
                     DOWN = true;
                     LEFT = false;
                     RIGHT = false;
                     break;
                  case LEFT:
                     UP = false;
                     DOWN = false;
                     RIGHT = false;
                     LEFT = true;
                     break;
                  case CONTROL:
                     System.out.println("chat");
                     btnConnect.setDisable(false);
                     taChat.setDisable(false);
                     tfServer.setDisable(false);
                     tfName.setDisable(false);
                     tfMess.setDisable(false);
                     btnSend.setDisable(false);
                     UP = false;
                     DOWN = false;
                     LEFT = false;
                     RIGHT = false;

                     break;
                  case COMMA: {
                     System.out.println("igra");
                     btnConnect.setDisable(true);
                     taChat.setDisable(true);
                     tfServer.setDisable(true);
                     tfName.setDisable(true);
                     tfMess.setDisable(true);
                     btnSend.setDisable(true);

                  }
                  case RIGHT:
                     UP = false;
                     DOWN = false;
                     LEFT = false;
                     RIGHT = true;
                     break;

                  case K:
                     SEND = true;
                     break;

                  case H:
                     System.out.println("slovo H");
                     break;

                  default:
                     break;

               }

            }
         });

         if (UP == true) {
            racePosX += 0;
            racePosY += -speed;
         } else if (DOWN == true) {
            racePosX += 0;
            racePosY += speed;
         } else if (LEFT == true) {
            racePosX += -speed;
            racePosY += 0;
         } else if (RIGHT == true) {
            racePosX += speed;
            racePosY += 0;
         } else if (SEND == true) {
            sendPacRacer(getRacePosX(), getRacePosY());
         }

         try {
            if (pacMapa.getColor(getRacePosX() + 16, getRacePosY() + 16).getBlue() > 0.8) {
               UP = false;
               DOWN = false;
               LEFT = false;
               RIGHT = false;
            }

         } catch (FileNotFoundException e) {
            e.printStackTrace();
         }

         // ghost collision
         Point2D ghostPos = new Point2D(ghost1.getRacePosX(), ghost1.getRacePosY());

         Point2D RacerPos = new Point2D(racer.getRacePosX(), racer.getRacePosY());

         if (RacerPos.distance(ghostPos) < ghostSize) {
            racePosX = 400;
            racePosY = 250;
            lifeCounter--;
            lives.setText("Lives left:" + lifeCounter);
            speed = speed - 1;
            Platform.runLater(new Runnable() {
               @Override
               public void run() {

                  if (speed <= 0) {
                     Alert alert = new Alert(AlertType.INFORMATION, "Game over :(");
                     alert.showAndWait();
                     timer.stop();
                  }
               }
            });

         }

                  if (RacerPos.distance(ghostPos) < ghostSize) {
            racePosX = 400;
            racePosY = 250;
            lifeCounter--;
            lives.setText("Lives left:" + lifeCounter);
            speed = speed - 1;
            Platform.runLater(new Runnable() {
               @Override
               public void run() {

                  if (speed <= 0) {
                     Alert alert = new Alert(AlertType.INFORMATION, "Game over :(");
                     alert.showAndWait();
                     timer.stop();
                  }
               }
            });

         }


         // Putting heart in random position
         Random rand = new Random();
         int randomNumX = rand.nextInt((790 - 1) + 1) + 1;
         int randomNumY = rand.nextInt((490 - 1) + 1) + 1;

         int randomNumX2 = rand.nextInt((790 - 1) + 1) + 1;
         int randomNumY2 = rand.nextInt((470 - 1) + 1) + 1;

         try {
            if (pacMapa.getColor(heart.getRacePosX() + 16, heart.getRacePosY() + 16).getBlue() > 0.8) {
               heart.setRacePosX(randomNumX);
               heart.setRacePosY(randomNumY);

            }

         } catch (FileNotFoundException e) {
            e.printStackTrace();
         }

         // heart Collision
         Point2D heartPos = new Point2D(heart.getRacePosX(), heart.getRacePosY());

         if (RacerPos.distance(heartPos) < ghostSize) {

            heart.setRacePosX(randomNumX);
            heart.setRacePosY(randomNumY);
            heartCounter++;

            Score.setText("Score: " + heartCounter);

            Platform.runLater(new Runnable() {
               @Override
               public void run() {

                  if (heartCounter == 5) {
                     try {
                        mapImage = new Image(new FileInputStream(MAP_IMAGE2), 800, 500, false, false);
                     } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                     }

                  }
               }

            });

         }

         // beri movement
         Point2D beriPos = new Point2D(beri.getRacePosX(), beri.getRacePosY());

         try {
            if (pacMapa.getColor(beri.getRacePosX() + 16, beri.getRacePosY() + 16).getBlue() > 0.8) {
               beri.setRacePosX(randomNumX2);
               beri.setRacePosY(randomNumY2);

            }

         } catch (FileNotFoundException e) {
            e.printStackTrace();
         }

         if (RacerPos.distance(beriPos) < ghostSize) {

            beri.setRacePosX(randomNumX);
            beri.setRacePosY(randomNumY);
            speed += 0.1;
         }

         // moving
         aPicView.setTranslateX(racePosX);
         aPicView.setTranslateY(racePosY);
         aPicView.setRotate(raceROT);

         if (racePosX >= 800)
            racePosX = 790;
         if (racePosY >= 500)
            racePosY = 490;
         raceROT += 1;
         if (racePosX < 1)
            racePosX = 5;
         if (racePosY < 1)
            racePosY = 5;
         raceROT += 1;

      } // end update()

   } // end inner class Racer

   protected class ghosts extends Pane {
      private int racePosX = 200; // x position of the racer
      private int racePosY = 40; // x position of the racer

      boolean gUP;
      boolean gDOWN;
      boolean gLEFT;
      boolean gRIGHT;

      public void setGUP(boolean gUP) {
         this.gUP = gUP;
      }

      public void setGDOWN(boolean gDOWN) {
         this.gDOWN = gDOWN;
      }

      public void setGLEFT(boolean gLEFT) {
         this.gLEFT = gLEFT;
      }

      public void setGRIGHT(boolean gRIGHT) {
         this.gRIGHT = gRIGHT;
      }

      public int getRacePosX() {
         return this.racePosX;
      }

      public void setRacePosX(int racePosX) {
         this.racePosX = racePosX;
      }

      public int getRacePosY() {
         return this.racePosY;
      }

      public void setRacePosY(int racePosY) {
         this.racePosY = racePosY;
      }

      private int raceROT = 0; // x position of the racer
      private ImageView aPicView; // a view of the icon ... used to display and move the image
      PacManMap pacMapa = new PacManMap();

      public ghosts() {
         // Draw the icon for the racer
         aPicView = new ImageView(ghostImage);
         this.getChildren().add(aPicView);
      }

      /**
       * update() method keeps the thread (racer) alive and moving.
       */
      public void update() {

         Platform.runLater(new Runnable() {
            @Override
            public void run() {
               try {
                  gRIGHT = true;

                  if (pacMapa.getColor(getRacePosX() + 16, getRacePosY() + 16).getBlue() > 0.8) {
                     // move random
                     gRIGHT = false;
                     setRacePosX(racePosX - racePosX);

                  }
               } catch (FileNotFoundException e) {
                  e.printStackTrace();
               }
            }
         });

         aPicView.setTranslateX(racePosX);
         aPicView.setTranslateY(racePosY);
         aPicView.setRotate(raceROT);

         if (gUP == true) {
            racePosX += 0;
            racePosY += -5;
         } else if (gDOWN == true) {
            racePosX += 0;
            racePosY += 5;
         } else if (gLEFT == true) {
            racePosX += -5;
            racePosY += 0;
         } else if (gRIGHT == true) {
            racePosX += 5;
            racePosY += 0;
         }

      }
   } // end inner class Ghost

   protected class heart extends Pane {
      private int racePosX = 500; // x position of the racer
      private int racePosY = 300; // x position of the racer

      public int getRacePosX() {
         return this.racePosX;
      }

      public void setRacePosX(int racePosX) {
         this.racePosX = racePosX;
      }

      public int getRacePosY() {
         return this.racePosY;
      }

      public void setRacePosY(int racePosY) {
         this.racePosY = racePosY;
      }

      private int raceROT = 0; // x position of the racer
      private ImageView aPicView; // a view of the icon ... used to display and move the image
      PacManMap pacMapa = new PacManMap();

      public heart() {
         // Draw the icon for the racer
         aPicView = new ImageView(heartImage);
         this.getChildren().add(aPicView);
      }

      /**
       * update() method keeps the thread (racer) alive and moving.
       */
      public void update() {

         aPicView.setTranslateX(racePosX);
         aPicView.setTranslateY(racePosY);
         aPicView.setRotate(raceROT);

      }
   } // end inner class Racer

   protected class beri extends Pane {
      private int racePosX = 600; // x position of the racer
      private int racePosY = 250; // x position of the racer

      public int getRacePosX() {
         return this.racePosX;
      }

      public void setRacePosX(int racePosX) {
         this.racePosX = racePosX;
      }

      public int getRacePosY() {
         return this.racePosY;
      }

      public void setRacePosY(int racePosY) {
         this.racePosY = racePosY;
      }

      private int raceROT = 0; // x position of the racer
      private ImageView aPicView; // a view of the icon ... used to display and move the image
      PacManMap pacMapa = new PacManMap();

      public beri() {
         // Draw the icon for the racer
         aPicView = new ImageView(beriImage);
         this.getChildren().add(aPicView);
      }

      /**
       * update() method keeps the thread (racer) alive and moving.
       */
      public void update() {

         aPicView.setTranslateX(racePosX);
         aPicView.setTranslateY(racePosY);
         aPicView.setRotate(raceROT);

      }
   } // end inner class Racer

   // MULTIPLAYER

   class multi extends Pane {
      private int multiPosX = 400; // x position of the racer
      private int multiPosY = 250; // x position of the racer

      public int getMultiPosX() {
         return this.multiPosX;
      }

      public void setMultiPosX(int multiPosX) {
         this.multiPosX = multiPosX;
      }

      public int getMultiPosY() {
         return this.multiPosY;
      }

      public void setMultiPosY(int multiPosY) {
         this.multiPosY = multiPosY;
      }

      private int raceROT = 0; // x position of the racer
      private ImageView aPicView; // a view of the icon ... used to display and move the image
      PacManMap pacMapa = new PacManMap();
      ghosts ghosts = new ghosts();

      public multi() {
         // Draw the icon for the racer
         aPicView = new ImageView(mulImage);
         this.getChildren().add(aPicView);

      }

      /**
       * multiUPdate() method keeps the thread (racer) alive and moving.
       */
      public void update() {

         try {
            if (pacMapa.getColor(getMultiPosX() + 16, getMultiPosY() + 16).getBlue() > 0.8) {
               multiUP = false;
               multiDOWN = false;
               multiLEFT = false;
               multiRIGHT = false;
            }

         } catch (FileNotFoundException e) {
            e.printStackTrace();
         }

         // ghost collision
         Point2D ghostPos = new Point2D(ghost1.getRacePosX(), ghost1.getRacePosY());

         Point2D RacerPos = new Point2D(multi.getMultiPosX(), multi.getMultiPosY());

         if (RacerPos.distance(ghostPos) < ghostSize) {
            multiPosX = 400;
            multiPosY = 250;
            multiLifeCounter--;
            lives.setText("Lives multiLEFT:" + multiLifeCounter);
            multiSpeed = 1;
         }

         // Putting heart in random position
         Random rand = new Random();
         int randomNumX = rand.nextInt((790 - 1) + 1) + 1;
         int randomNumY = rand.nextInt((490 - 1) + 1) + 1;

         int randomNumX2 = rand.nextInt((790 - 1) + 1) + 1;
         int randomNumY2 = rand.nextInt((470 - 1) + 1) + 1;

         try {
            if (pacMapa.getColor(heart.getRacePosX() + 16, heart.getRacePosY() + 16).getBlue() > 0.8) {
               heart.setRacePosX(randomNumX);
               heart.setRacePosY(randomNumY);

            }

         } catch (FileNotFoundException e) {
            e.printStackTrace();
         }

         // heart Collision
         Point2D heartPos = new Point2D(heart.getRacePosX(), heart.getRacePosY());

         if (RacerPos.distance(heartPos) < ghostSize) {

            heart.setRacePosX(randomNumX);
            heart.setRacePosY(randomNumY);
            multiHeartCounter++;
            Score.setText("Score: " + multiHeartCounter);
         }

         // beri movement
         Point2D beriPos = new Point2D(beri.getRacePosX(), beri.getRacePosY());

         try {
            if (pacMapa.getColor(beri.getRacePosX() + 16, beri.getRacePosY() + 16).getBlue() > 0.8) {
               beri.setRacePosX(randomNumX2);
               beri.setRacePosY(randomNumY2);

            }

         } catch (FileNotFoundException e) {
            e.printStackTrace();
         }

         if (RacerPos.distance(beriPos) < ghostSize) {

            beri.setRacePosX(randomNumX);
            beri.setRacePosY(randomNumY);
            multiSpeed += 0.1;
         }

         // moving
         aPicView.setTranslateX(multiPosX);
         aPicView.setTranslateY(multiPosY);
         aPicView.setRotate(raceROT);

         if (multiPosX >= 800)
            multiPosX = 790;
         if (multiPosY >= 500)
            multiPosY = 490;
         raceROT += 1;
         if (multiPosX < 1)
            multiPosX = 5;
         if (multiPosY < 1)
            multiPosY = 5;
         raceROT += 1;

      } // end multiUPdate()

   } // end inner class Racer

}
// end class Races

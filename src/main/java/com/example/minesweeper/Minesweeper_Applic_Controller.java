package com.example.minesweeper;

import javafx.application.Application;
import javafx.beans.binding.When;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Minesweeper_Applic_Controller extends Application implements Initializable {
   private static Text gameOverLabel = null;
    private static Text winnerTextLabel = null;
    private static  int Y_FIELDS = 16;
    private static  int X_FIELDS = 16;
    private static Field[][] grid = new Field[X_FIELDS][Y_FIELDS];
    public static  int FIELD_SIZE = 35;
    private static Scene scene = null;
    private static boolean proof = false;
    private static boolean proofLabel = false;
    private static boolean proofLabel2 = false;
    private static boolean winnerCheck = false;
    private static boolean loose = false;
    private static Rectangle winnerLabel;
    private static Rectangle looseLabel;
    @FXML
    private ChoiceBox<String> choiceBoxLevel;
    @FXML
    AnchorPane anchorField;
    private final String[] levelChoice = {"Einfach", "Mittel", "Schwierig"};
    Pane root = new Pane();

/*
    Die initialize-Methode in Java ist eine spezielle Methode, die automatisch erstellt wird,
    wenn eine Klasse geladen wird.
    In ihr werden statische Methoden und Variablen initialisiert.
    Zusätzlich wird das neue Spielfeld (mithilfe von createField) aufgerufen.
*/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /*
         * Um die Elemente von einem Array mit den verschiedenen Schwierigkeitslevel zu meiner ChoiceBox hinzuzufügen
         */
        looseLabel = new Rectangle(567, 567, Color.LIGHTGREEN);
        winnerLabel = new Rectangle(567,567, Color.CADETBLUE);

        choiceBoxLevel.getItems().addAll(levelChoice);
        choiceBoxLevel.setOnAction((ActionEvent event) -> {
            if (choiceBoxLevel.getValue() != null){
                SelectLevelChoice();
            }
        });
        root = (Pane) createField();
    }

    @FXML
    public void SelectLevelChoice(){
        /*
            Abfrage, welches Level gewählt wurde - je nach dem soll ein kleineres bzw. größeres Raster verwendet werden:
         */
       String choice = choiceBoxLevel.getValue();
        root.getChildren().clear();
        switch (choice) {
            case "Einfach":
                Y_FIELDS = 8;
                X_FIELDS = 8;
                break;
            case "Mittel":
                Y_FIELDS = 16;
                X_FIELDS = 16;
                break;
            case "Schwierig":
                Y_FIELDS = 20;
                X_FIELDS = 20;
                break;
            default:
                Y_FIELDS = 8;
                X_FIELDS = 8;
        }

        FIELD_SIZE = (567/X_FIELDS);
        grid = new Field[X_FIELDS][Y_FIELDS];
        root.getChildren().clear();
        root.setPrefSize(567, 567);
        createField();
    }

    /*
               In diesem Teil des Programms bzw. in der createField-Methode, wird das Spielfeld mit einer zufälligen Platzierung an Bomben generiert.
               Dazu gibt es die Variablen Y_Fields und X_Fields, die sagen, wie viele Kästchen erstellt werden sollen.
               Danach dem Pane immer wieder in der Schleife ein Feld hinzugefügt, bis das Spielfeld seine geforderte Größe erreicht hat.
        */
    private Parent createField() {
        root = new Pane();
        root.setPrefSize(567, 567);
        anchorField.getChildren().add(root);
        if (!proof)  choiceBoxLevel.setValue("Einfach");
        proof = true;

        for (int y = 0; y < Y_FIELDS; y++) {
            for (int x = 0; x < X_FIELDS; x++) {

                //20% der Felder sollen Bomben sein! (Es liefert 20% true, 80% false)
                Field field = new Field(x, y, Math.random() < 0.2, this);
                grid[x][y] = field;
                root.getChildren().add(field);
            }
        }

        for (int y = 0; y < Y_FIELDS; y++) {
            for (int x = 0; x < X_FIELDS; x++) {
                Field field = grid[x][y];
                if (!field.hasBomb) {
                    int count = 0;
                    ArrayList<Field> fields = getNeighbours(grid[x][y]);

                    for (Field f : fields) {
                        if (f.hasBomb) {
                            count++;
                        }
                    }
                    if (count > 0) {
                        field.setBombCount(Integer.toString(count));
                    }
                }
            }
        }
        return root;
    }

    /*
            In der Methode werden zuerst alle Nachbarn in einem int-Array gespeichert.
            Dadurch kann später gezählt werden, wie viele Bomben rund um ein Feld liegen.
     */
    public static ArrayList<Field> getNeighbours(Field field) {
        ArrayList<Field> neighbours = new ArrayList<>();

        /*
                -1   -1          0   -1           1   -1
                -1    0          FIELD            1    0
                -1    1          0    1           1    1
         */

        int[] points = new int[]{
                -1, -1, -1, 0, -1, 1, 0, -1, 0, 1, 1, -1, 1, 0, 1, 1
        };

        for (int i = 0; i < points.length; i++) {
            int dx = points[i];   //delta X
            int dy = points[i + 1]; //delta Y

            int newX = field.getX() + dx;
            int newY = field.getY() + dy;

            /*
                add neighbour only if in bound:
             */

            if (newX >= 0 && newX < X_FIELDS && newY >= 0 && newY < Y_FIELDS) {
                neighbours.add(grid[newX][newY]);
            }
            i++;
        }
        return neighbours;
    }

    /*
        Funktionen der Gameover()-Methode:
        Die gameOver-Methe überprüft, ob man verloren hat. Dazu wird ein Mouseevent erstellt, wenn man eine Maustaste drückt.
        Ist das darunterliegende Feld eine Bombe, so wird die Methode gameOver() aufgerufen.
    */

    public void gameOver(){

        if (!proofLabel2){
            looseLabel.setLayoutX((scene.getWidth() - looseLabel.getWidth()) / 2 );
            looseLabel.setLayoutY((scene.getHeight() - looseLabel.getHeight()) / 2 );
            ((Pane) scene.getRoot()).getChildren().add(looseLabel);

            gameOverLabel = new Text("Game Over - Das war eine Bombe!\nStarten Sie darunter eine neue Runde:");
            gameOverLabel.setTranslateX(150);
            gameOverLabel.setTranslateY(300);
            gameOverLabel.setFont(Font.font(25));
            gameOverLabel.setTextAlignment(TextAlignment.CENTER);
            gameOverLabel.setFill(Color.WHITE);
            ((Pane)scene.getRoot()).getChildren().add(gameOverLabel);
            proofLabel2 = true;
        } else {
            gameOverLabel.setVisible(true);
            looseLabel.setVisible(true);
        }
        loose = true;
    }

    /*
    Funktion - WinnerProof():
        Die wichtige winnerProof-Methode überprüft nach jedem Feld, dass in der open()-Methode in der Field-Klasse
        geöffnet worden ist, ob der Spieler gewonnen hat. Dafür wird in den unteren Schleifen überprüft, ob alle Felder,
        die keine Bombe ist, aufgedeckt sind.

        Ist ein Gewinner gefunden, so wird ein neues AnchorPane erstellt und ein ein dazupassendes Label mit dem Text
        "Sie haben gewonnen! ..."
    */

    public static void winnerProof(){
             boolean winner = true;
        for (int i = 0; i < Y_FIELDS; i++){
            for (int y = 0; y < X_FIELDS; y++){
                Field field = grid[y][i];
                if (!grid[y][i].isHasBomb() && !field.isOpen()){
                    winner = false;
                    break;
                }
            }
        }
        if(winner){

            if (!proofLabel){
                winnerLabel.setLayoutX((scene.getWidth() - winnerLabel.getWidth()) / 2 );
                winnerLabel.setLayoutY((scene.getHeight() - winnerLabel.getHeight()) / 2 );
                ((Pane) scene.getRoot()).getChildren().add(winnerLabel);

                winnerTextLabel = new Text("Sie haben gewonnen!\n Starten Sie darunter eine neue Runde.");
                winnerTextLabel.setTranslateX(150);
                winnerTextLabel.setTranslateY(300);
                winnerTextLabel.setFont(Font.font(25));
                winnerTextLabel.setTextAlignment(TextAlignment.CENTER);
                winnerTextLabel.setFill(Color.WHITE);
                ((Pane)scene.getRoot()).getChildren().add(winnerTextLabel);
                proofLabel = true;
            } else {
                winnerLabel.setVisible(true);
                winnerTextLabel.setVisible(true);
            }
            winnerCheck = true;
        }
    }


    /*
            Wird der Button "Nächste Runde geklickt", wird die Methode newGame() aufgerufen und überprüft, ob man
            verloren oder gewonnen hat. Dabei wird dann je nachdem die Label wieder auf Visible(false) gesetzt und
            root erhält ein neues Spielfeld, nachdem es mit .clear() geleert wurde.
     */
     public void newGame(){

        if (loose){
            looseLabel.setVisible(false);
            gameOverLabel.setVisible(false);
            loose = false;
        }

        if (winnerCheck){
            winnerLabel.setVisible(false);
            winnerTextLabel.setVisible(false);
            winnerCheck = false;
        }
        root.getChildren().clear();
       // proof = false;

        createField();

        //Siehe FOTO -> Andere Möglichkeit!
    }


    /*
            Startet das Programm und lädt dabei die fxml-Datei inklusive der Scene und der Stage.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Minesweeper_Applic_Controller.class.getResource("hello-view.fxml"));
        scene = new Scene(fxmlLoader.load(), 700, 700);
        stage.setTitle("Minesweeper - Game!");
        scene.setFill(Color.rgb(151, 189, 0));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }


    /*
            Hier wird die Application gestartet.
     */
    public static void main(String[] args) {
        launch();
    }

}
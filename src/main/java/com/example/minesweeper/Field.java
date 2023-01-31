package com.example.minesweeper;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class Field extends StackPane {

    private int x;
    private int y;
    public boolean hasBomb;
    private String text = "";
    public Text bombCount;
    private Rectangle fieldNode = null;
    private boolean isOpen;
    private Minesweeper_Applic_Controller owner;

    private static int MAX_X = 100;
    private static int MAX_Y = 100;
    private boolean isMarked = false;


    public void setBombCount(String bombCount) {
        this.bombCount.setText(bombCount);
    }

    //SIEHE FOTO AM HANDY!


    /*
        Konstruktor der Klasse.
        Hier werden die Variablen x, y und owner initialisiert. Außerdem wird übergeben, ob ein Feld eine Bombe ist oder nicht.
     */
    public Field(int x, int y, boolean hasBomb, Minesweeper_Applic_Controller owner) {
        this.owner = owner;
        this.x = x;
        this.y = y;
        this.hasBomb = hasBomb;

        /*
               Jedes Field wird zu einem Rechteck gemacht und die passenden Farben für das Feld gesetzt.
               Dabei wird außerdem die Größe der einzelnen Felder definiert und der Platz zwischen den Feldern.
         */

        fieldNode = new Rectangle(Minesweeper_Applic_Controller.FIELD_SIZE - 5, Minesweeper_Applic_Controller.FIELD_SIZE - 5);
        fieldNode.setFill(Color.rgb(245, 255, 204));
        fieldNode.setStroke(Color.rgb(82, 102, 0));
        fieldNode.setVisible(true);

        bombCount = new Text();
        bombCount.setText(this.hasBomb ? "X" : ""); // => if else
        bombCount.setStroke(Color.rgb(99, 126, 7));
        bombCount.setVisible(false); //field default not opened

        /*
                Die Verdeckung und die Eigenschaften auf das Fieldobjekt schicken
         */
        getChildren().addAll(fieldNode, bombCount);
        setTranslateX(x* Minesweeper_Applic_Controller.FIELD_SIZE+5);
        setTranslateY(y* Minesweeper_Applic_Controller.FIELD_SIZE+5);
        setOnMouseClicked(this::handleMouseClick);
    }

    /*
         In der handleMouseClick()-Methode wird überprüft, welche Maustaste geklickt wurde. Denn je nach Taste soll das
         Feld aufgedeckt oder als Bombe gekennzeichnet werden. Dafür wird ein MouseEvent übergeben, das im Field-Konstruktur
         aufgerufen wird.
         Ist es die rechte Maustaste (Secondary-MouseButton), so kann die Kennzeichnung des Feldes aktiviert bzw.
         deaktiviert werden.
     */

    public void handleMouseClick(MouseEvent event){
        if (event.getButton() == MouseButton.PRIMARY) {
            onFieldClicked(event);
        } else if (event.getButton() == MouseButton.SECONDARY) {
            if (!isOpen){
                if (!isMarked) {
                    fieldNode.setFill(Color.rgb(255, 0, 0));
                    //fieldNode.setDisable(true);
                    isMarked = true;
                } else {
                    fieldNode.setFill(Color.rgb(245, 255, 204));
                    //fieldNode.setDisable(true);
                    isMarked = false;
                }
            }
        }
    }


    /*
            Das ist die Methode mit dem MouseEvent, die die open()-Methode aufruft,
            außer dieses Feld ist eine Bombe, dann wird meine Methode gameOver() aufgerufen.
     */
    private void onFieldClicked(MouseEvent event){
        if(this.hasBomb){
            owner.gameOver();

        }else{
            open();
        }
    }


    /*

        Die Funktion der open()-Methode:
        Diese Methode wird aufgerufen, wenn auf ein Feld geklickt wurde. (Mithilfe eines Events).
        In ihr wird überprüft, ob es schon offen ist oder das Feld bereits als Bombe gekennzeichnet ist.

        Ansonsten wird der BombCount und die Variable bombCount sichtbar gemacht.
        Darunter wird jedes Mal die winnerProof() Methode aufgerufen -> Erklärung in der Application-Klasse!

     */
    public void open(){
        if (this.isOpen || fieldNode.getFill().equals(Color.rgb(255, 0, 0))) {
            return;
        }
        this.isOpen = true;
        bombCount.setVisible(true);
        fieldNode.setFill(Color.rgb(184, 230, 0));
        fieldNode.setDisable(true);
        if (bombCount.getText().isEmpty()){
            Minesweeper_Applic_Controller.getNeighbours(this).forEach(Field::open);
        }
        Minesweeper_Applic_Controller.winnerProof();
    }

    /*
            Getter-Methode für isHasBomb:
     */
    public boolean isHasBomb() {
        return hasBomb;
    }

    /*
            Die Getter-Methode von isOpen();
     */
    public boolean isOpen() {
        return isOpen;
    }

    /*
            Die anderen Getter und Setter für x und y:
     */

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}

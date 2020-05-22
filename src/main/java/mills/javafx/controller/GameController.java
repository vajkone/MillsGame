package mills.javafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
public class GameController {

    @Inject
    private FXMLLoader fxmlLoader;

    @FXML
    private Label testLabel;

    @FXML
    private Shape spot11;

    @FXML
    private Label phaseText;

    private String playerNameOne, playerNameTwo;

    @FXML
    private Label playerNameOneLabel;

    @FXML
    private Label playerNameTwoLabel;





    private int phase;

    private Paint dragPaint;
    private Paint previousPaint;

    public void setPlayerNames(String playerName1, String playerName2) {
        this.playerNameTwo = playerName2;
        this.playerNameOne=playerName1;
        playerNameOneLabel.setText(playerName1);
        playerNameTwoLabel.setText(playerName2);
    }


    @FXML
    public void initialize() {
        phase=1;
        phaseText.setText("Phase 1");

    }



    public void slotClicked(MouseEvent mouseEvent){

        Shape clicked=(Shape) mouseEvent.getTarget();
        clicked.setFill(Color.TRANSPARENT);

    }

    public void dragStarted(MouseEvent mouseEvent){


        Shape clicked=(Shape) mouseEvent.getTarget();
        dragPaint=clicked.getFill();
        Dragboard db = clicked.startDragAndDrop(TransferMode.ANY);
        ClipboardContent cb = new ClipboardContent();
        cb.putString(dragPaint.toString());

        db.setContent(cb);
        mouseEvent.consume();

        System.out.println("Drag started");

    }


    public void dragEntered(DragEvent event){

        if (event.getDragboard().hasString()){
            event.acceptTransferModes(TransferMode.ANY);
        }
        Shape clicked=(Shape) event.getTarget();
        previousPaint= clicked.getFill();
        clicked.setFill(dragPaint);
        System.out.println(event.getDragboard().getString());
        System.out.println("Drag entered");
    }
    public void dragOver(DragEvent event){

        if (event.getDragboard().hasString()){
            event.acceptTransferModes(TransferMode.ANY);
        }


    }

    public void dragDropped(DragEvent event){

        Shape clicked=(Shape) event.getTarget();
        previousPaint=dragPaint;
        clicked.setFill(dragPaint);
        System.out.println("drag dropped");
    }

    public void handleDragDone(DragEvent event){
        Shape clicked=(Shape) event.getTarget();
        clicked.setFill(Color.TRANSPARENT);
    }

    public void dragExited(DragEvent event){

        Shape clicked=(Shape) event.getTarget();
        clicked.setFill(previousPaint);
        System.out.println("drag exited");
    }





}

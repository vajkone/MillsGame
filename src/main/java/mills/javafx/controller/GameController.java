package mills.javafx.controller;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import lombok.extern.slf4j.Slf4j;
import state.GameState;

import javax.inject.Inject;
import java.util.Arrays;

@Slf4j
public class GameController {

    @Inject
    private FXMLLoader fxmlLoader;

    @FXML
    private Label testLabel;

    private IntegerProperty turn = new SimpleIntegerProperty();
    private IntegerProperty playerOnePieces = new SimpleIntegerProperty();
    private IntegerProperty playerTwoPieces = new SimpleIntegerProperty();


    @FXML
    private Label phaseText;

    private String playerNameOne, playerNameTwo;

    private Paint playerOneColor=Color.BLACK;
    private Paint playerTwoColor=Color.DEEPSKYBLUE;
    private GameState gameState;
    private int playerTurn;

    @FXML
    private Label playerNameOneLabel;

    @FXML
    private Label playerOnePiecesLabel;
    @FXML
    private Label playerTwoPiecesLabel;

    @FXML
    private Label playerNameTwoLabel;
    @FXML
    private Label playerTurnLabel;





    private int phase;

    private Paint dragPaint;
    private Paint previousPaint;

    public void setPlayerNames(String playerName1, String playerName2) {
        this.playerNameTwo = playerName2;
        this.playerNameOne=playerName1;
        playerNameOneLabel.setText(playerName1);
        playerNameTwoLabel.setText(playerName2);
        playerTurnLabel.setText(playerNameOne+"'s turn");
    }


    @FXML
    public void initialize() {
        phase=1;
        phaseText.setText("Phase 1");

        turn.set(1);
        playerOnePieces.set(9);
        playerTwoPieces.set(9);
        playerOnePiecesLabel.textProperty().bind(playerOnePieces.asString());
        playerTwoPiecesLabel.textProperty().bind(playerTwoPieces.asString());
        gameState=new GameState();

    }



    public void slotClicked(MouseEvent mouseEvent){

        if (phase==1){
            Shape clicked=(Shape) mouseEvent.getTarget();
            if (gameState.isValidPlacement(clicked.getId())) {

                if (turn.get() % 2 == 1) {

                    clicked.setFill(playerOneColor);
                    playerOnePieces.set(playerOnePieces.get() - 1);
                    playerTurnLabel.setText(playerNameTwo + "'s turn");
                    gameState.placePieceOnBoard(clicked.getId(), '1');
                    log.info("{} has placed a piece on the {} place", playerNameOne, clicked.getId());

                } else {

                    clicked.setFill(playerTwoColor);
                    playerTwoPieces.set(playerTwoPieces.get() - 1);
                    playerTurnLabel.setText(playerNameOne + "'s turn");
                    log.info("{} has placed a piece on the {} place", playerNameTwo, clicked.getId());
                    gameState.placePieceOnBoard(clicked.getId(), '2');
                }
                turn.set(turn.get()+1);
            }


            if (playerOnePieces.get()+playerTwoPieces.get()==0){
                phase=2;
                phaseText.setText("Phase 2");
            }


        }





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

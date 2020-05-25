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
import mills.state.GameState;
import mills.state.Mill;

import javax.inject.Inject;

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

    @FXML
    private Label millFormedLabel;

    private String playerNameOne, playerNameTwo;

    private Paint playerOneColor=Color.BLACK;
    private Paint playerTwoColor=Color.DEEPSKYBLUE;
    private GameState gameState;
    private int playerTurn;
    private Mill mill;
    private boolean millFormed=false;

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
            char actingPlayer=' ';
            switch (turn.get()%2){
                case 0:
                    actingPlayer='2';
                    break;
                case 1:
                    actingPlayer='1';
                    break;
            }

            if (gameState.isValidPlacement(clicked.getId())) {

                if (actingPlayer=='1') {

                    clicked.setFill(playerOneColor);
                    playerOnePieces.set(playerOnePieces.get() - 1);
                    gameState.placePieceOnBoard(clicked.getId(), '1');
                    log.info("{} has placed a piece on the {} place", playerNameOne, clicked.getId());
                    if (gameState.isMill(clicked.getId(),'1',gameState.getBoard())){
                        millFormedLabel.setText(playerNameOne+" has formed a mill. Remove one of the other player's men.");
                        millFormedLabel.setVisible(true);
                        millFormed=true;
                    }else{
                        playerTurnLabel.setText(playerNameTwo + "'s turn");
                    }

                } else {

                    clicked.setFill(playerTwoColor);
                    playerTwoPieces.set(playerTwoPieces.get() - 1);
                    gameState.placePieceOnBoard(clicked.getId(), '2');
                    log.info("{} has placed a piece on the {} place", playerNameTwo, clicked.getId());
                    if (gameState.isMill(clicked.getId(),'2',gameState.getBoard())){
                        millFormedLabel.setText(playerNameTwo+" has formed a mill. Remove one of the other player's men.");
                        millFormedLabel.setVisible(true);
                        millFormed=true;
                    }else{
                        playerTurnLabel.setText(playerNameOne + "'s turn");
                    }
                }
                if (!millFormed) {
                    turn.set(turn.get() + 1);
                }
            }else if(millFormed){

                if (gameState.isValidRemoval(clicked.getId(),actingPlayer)){
                    gameState.removePieceFromBoard(clicked.getId());
                    clicked.setFill(Color.TRANSPARENT);
                    millFormed=false;
                    millFormedLabel.setVisible(false);
                    turn.set(turn.get() + 1);
                    if (playerOnePieces.get()+playerTwoPieces.get()==0){
                        phase=2;
                        phaseText.setText("Phase 2");
                    }
                }
            }


            if (playerOnePieces.get()+playerTwoPieces.get()==0 && !millFormed){
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

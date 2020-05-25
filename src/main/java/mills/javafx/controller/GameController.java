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

                updateBoardOnPlacement(clicked,actingPlayer);

            }else if(millFormed){

                if (gameState.isValidRemoval(clicked.getId(),actingPlayer)){
                    updateBoardOnRemoval(clicked,actingPlayer);
                }
            }

            if (playerOnePieces.get()+playerTwoPieces.get()==0 && !millFormed){
                phase=2;
                phaseText.setText("Phase 2");
            }


        }

    }

    public void updateBoardOnRemoval(Shape clicked,char actingPlayer){
        String playerNameNext="";
        switch (actingPlayer) {
            case '1':
                playerNameNext = playerNameTwo;
                break;
            case '2':
                playerNameNext = playerNameOne;
                break;
        }
        gameState.removePieceFromBoard(clicked.getId());
        clicked.setFill(Color.TRANSPARENT);
        millFormed=false;
        millFormedLabel.setVisible(false);
        turn.set(turn.get() + 1);
        playerTurnLabel.setText(playerNameNext+"'s turn");
        if (playerOnePieces.get()+playerTwoPieces.get()==0){
            phase=2;
            phaseText.setText("Phase 2");
        }
    }

    public void updateBoardOnPlacement(Shape clicked,char actingPlayer){
        Paint playerColor=null;
        String playerName="";
        String playerNameNext="";
        switch (actingPlayer){
            case '1':
                playerColor=playerOneColor;
                playerName=playerNameOne;
                playerNameNext=playerNameTwo;
                playerOnePieces.set(playerOnePieces.get() - 1);
                break;
            case '2':
                playerColor=playerTwoColor;
                playerName=playerNameTwo;
                playerNameNext=playerNameOne;
                playerTwoPieces.set(playerTwoPieces.get() - 1);
                break;
        }

        clicked.setFill(playerColor);

        gameState.placePieceOnBoard(clicked.getId(), actingPlayer);
        log.info("{} has placed a piece on the {} place", playerName, clicked.getId());
        if (gameState.isMill(clicked.getId(),actingPlayer,gameState.getBoard())){
            millFormedLabel.setText(playerName+" has formed a mill. Remove one of the other player's men.");
            log.info("{} has formed a mill",playerName);
            millFormedLabel.setVisible(true);
            millFormed=true;
        }else{
            playerTurnLabel.setText(playerNameNext + "'s turn");
        }
        if (!millFormed) {
            turn.set(turn.get() + 1);
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

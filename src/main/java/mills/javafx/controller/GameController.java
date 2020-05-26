package mills.javafx.controller;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import mills.state.GameState;
import mills.state.Mill;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

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
    private Label  phaseTwoStartedLabel;


    @FXML
    private Label millFormedLabel;

    private String playerNameOne, playerNameTwo;

    private Paint playerOneColor=Color.BLACK;
    private Paint playerTwoColor=Color.DEEPSKYBLUE;
    private GameState gameState;
    private boolean millFormed=false;
    private ArrayList<String> validMoves;
    private boolean hasClicked=false;
    private Shape previousShape;

    @FXML
    private Label playerNameOneLabel;

    @FXML
    private Label playerOnePiecesLabel;
    @FXML
    private Label playerTwoPiecesLabel;
    @FXML
    private Pane mainPane;

    @FXML
    private Label playerNameTwoLabel;
    @FXML
    private Label playerTurnLabel;





    private int phase;


    public void setPlayerNames(String playerName1, String playerName2) {
        this.playerNameTwo = playerName2;
        this.playerNameOne=playerName1;
        playerNameOneLabel.setText(playerName1);
        playerNameTwoLabel.setText(playerName2);
        playerNameTwoLabel.setTextFill(playerTwoColor);
        playerTurnLabel.setText(playerNameOne+"'s turn");

    }


    @FXML
    public void initialize() {
        phase=1;
        phaseText.setText("Phase 1");

        turn.set(1);
        playerOnePieces.set(4);
        playerTwoPieces.set(4);
        playerOnePiecesLabel.textProperty().bind(playerOnePieces.asString());
        playerTwoPiecesLabel.textProperty().bind(playerTwoPieces.asString());
        gameState=new GameState();


    }



    public void slotClicked(MouseEvent mouseEvent) {

        char actingPlayer = ' ';
        switch (turn.get() % 2) {
            case 0:
                actingPlayer = '2';
                break;
            case 1:
                actingPlayer = '1';
                break;
        }
        if (phase == 1) {
            Shape clicked = (Shape) mouseEvent.getTarget();


            if (gameState.isValidPlacement(clicked.getId()) && !millFormed) {

                updateBoardOnPlacement(clicked, actingPlayer);
                if (!millFormed) {
                    turn.set(turn.get() + 1);
                    updatePlayerTurnLabel(actingPlayer);
                }

            } else if (millFormed) {

                if (gameState.isValidRemoval(clicked.getId(), actingPlayer)) {
                    updateBoardOnRemoval(clicked, actingPlayer);
                    turn.set(turn.get() + 1);
                    updatePlayerTurnLabel(actingPlayer);
                }
            }

            if (playerOnePieces.get() + playerTwoPieces.get() == 0 && !millFormed) {
                phase = 2;
                phaseText.setText("Phase 2");
                phaseTwoStartedLabel.setText("Phase 2 has started. Click on one of your men you wish to move, then select one of the slots marked with a green circle to move your man there");
                phaseTwoStartedLabel.setVisible(true);
                validMoves=new ArrayList<>();
                playerOnePieces.set(gameState.countPlayerPieces('1'));
                playerTwoPieces.set(gameState.countPlayerPieces('2'));
            }


        }
        else if (phase == 2){
            Shape clicked = (Shape) mouseEvent.getTarget();

            if (actingPlayer==gameState.getPlayerOfPiece(clicked.getId()) &&!millFormed) {


                if (hasClicked && clicked!=previousShape) {
                    for (String moves : validMoves) {
                        String shapeId = "c" + moves;
                        Shape movable = (Shape) mainPane.lookup("#" + shapeId);
                        movable.setStroke(Color.BLACK);
                        movable.setStrokeWidth(1);
                    }
                }

                hasClicked = true;
                previousShape = clicked;
                validMoves = gameState.checkForValidMovement(clicked.getId());
                for (String moves : validMoves) {
                    String shapeId = "c" + moves;
                    Shape movable = (Shape) mainPane.lookup("#" + shapeId);
                    movable.setStroke(Color.GREEN);
                    movable.setStrokeWidth(3);
                }


            }else if (validMoves.contains(clicked.getId().substring(1,3)) && !millFormed && previousShape!=null){
                updateBoardOnRemoval(previousShape,actingPlayer);
                updateBoardOnPlacement(clicked,actingPlayer);
                if (!millFormed) {
                    turn.set(turn.get() + 1);
                    updatePlayerTurnLabel(actingPlayer);
                }
                hasClicked=false;
                previousShape=null;
                for (String moves : validMoves) {
                    String shapeId = "c" + moves;
                    Shape movable = (Shape) mainPane.lookup("#" + shapeId);
                    movable.setStroke(Color.BLACK);
                    movable.setStrokeWidth(1);
                }
                if (phaseTwoStartedLabel.isVisible()){
                    phaseTwoStartedLabel.setVisible(false);
                }
            }else if(millFormed) {
                if (gameState.isValidRemoval(clicked.getId(), actingPlayer)) {
                    updateBoardOnRemoval(clicked, actingPlayer);
                    turn.set(turn.get() + 1);
                    updatePlayerTurnLabel(actingPlayer);
                }
            }




        }



    }

    public void updatePlayerTurnLabel(char actingPlayer){
        String playerNameNext="";
        Paint playerColorNext=null;
        switch (actingPlayer) {
            case '1':
                playerNameNext = playerNameTwo;
                playerColorNext = playerTwoColor;
                break;
            case '2':
                playerNameNext = playerNameOne;
                playerColorNext = playerOneColor;
                break;
        }

        playerTurnLabel.setText(playerNameNext+"'s turn");
        playerTurnLabel.setTextFill(playerColorNext);
    }

    public void updateBoardOnRemoval(Shape clicked,char actingPlayer){

        gameState.removePieceFromBoard(clicked.getId());
        clicked.setFill(Color.TRANSPARENT);
        if (phase==2 && millFormed){
            if (actingPlayer == '1') {
                playerTwoPieces.set(playerTwoPieces.get() - 1);
            } else {
                playerOnePieces.set(playerOnePieces.get() - 1);
            }
        }
        millFormed=false;
        millFormedLabel.setVisible(false);
        if (playerOnePieces.get()+playerTwoPieces.get()==0){
            phase=2;
            phaseText.setText("Phase 2");
        }
    }

    public void updateBoardOnPlacement(Shape clicked,char actingPlayer){
        Paint playerColor=null;
        String playerName="";
        switch (actingPlayer){
            case '1':
                playerColor=playerOneColor;
                playerName=playerNameOne;
                if (phase==1) {
                    playerOnePieces.set(playerOnePieces.get() - 1);
                }
                break;
            case '2':
                playerColor=playerTwoColor;
                playerName=playerNameTwo;
                if (phase==1) {
                    playerTwoPieces.set(playerTwoPieces.get() - 1);
                }
                break;
        }

        clicked.setFill(playerColor);

        gameState.placePieceOnBoard(clicked.getId(), actingPlayer);
        if (phase==1) {
            log.info("{} has placed a piece on the {} place", playerName, clicked.getId());
        }else{
            log.info("{} has moved a piece from {} to the {} place", playerName,previousShape.getId(), clicked.getId());
        }
        if (gameState.isMill(clicked.getId(),actingPlayer,gameState.getBoard())){
            millFormedLabel.setText(playerName+" has formed a mill. Remove one of the other player's men.");
            log.info("{} has formed a mill",playerName);
            millFormedLabel.setVisible(true);
            millFormed=true;
        }



    }

}

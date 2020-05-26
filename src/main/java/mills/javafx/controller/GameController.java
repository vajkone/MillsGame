package mills.javafx.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import mills.results.GameResult;
import mills.results.GameResultDao;
import mills.state.GameState;
import org.apache.commons.lang3.time.DurationFormatUtils;

import javax.inject.Inject;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;


@Slf4j
public class GameController {

    @Inject
    private FXMLLoader fxmlLoader;
    @Inject
    private GameResultDao gameResultDao;

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
    private ArrayList<String> removablePieces;
    private boolean hasClicked=false;
    private Shape previousShape;
    private ArrayList<Character> flyingPlayers;
    private String winner;

    @FXML
    private Label playerNameOneLabel;

    private Timeline stopWatchTimeline;
    @FXML
    private Label stopWatchLabel;
    private Instant startTime;

    @FXML
    private Label playerOnePiecesLabel;
    @FXML
    private Label playerTwoPiecesLabel;
    @FXML
    private Pane mainPane;

    private boolean stopwatchStarted=false;

    @FXML
    private Label playerNameTwoLabel;
    @FXML
    private Label playerTurnLabel;
    @FXML
    private Button resultsButton;





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
        playerOnePieces.set(5);
        playerTwoPieces.set(5);
        playerOnePiecesLabel.textProperty().bind(playerOnePieces.asString());
        playerTwoPiecesLabel.textProperty().bind(playerTwoPieces.asString());
        gameState=new GameState();
        flyingPlayers=new ArrayList<>();
        removablePieces=new ArrayList<>();




    }



    public void slotClicked(MouseEvent mouseEvent) {

        if (!stopwatchStarted){
            startTime = Instant.now();
            createStopWatch();
            stopwatchStarted=true;
        }

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
        else if (phase >= 2 && phase<4){
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
                if (phase==3 && flyingPlayers.contains(actingPlayer)){
                    validMoves=gameState.getVacantPoints();
                }else {
                    validMoves = gameState.checkForValidMovement(clicked.getId());
                }
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

            checkForPhaseThree();

            checkForGameEnd();

        }
    }

    private void checkForGameEnd() {
        if (playerOnePieces.get()==2) {
            phase=4;
            phaseText.setText("Game ended");
            log.info("Game ended, {} has won the game.",playerNameTwo);
            winner=playerNameTwo;
            phaseTwoStartedLabel.setText(playerNameOne + " has only 2 pieces left, therefore " + playerNameTwo + " has won the game. Congratulations.");
            phaseTwoStartedLabel.setVisible(true);
            playerTurnLabel.setVisible(false);
            stopWatchTimeline.stop();
            gameResultDao.persist(createGameResult());
            resultsButton.setVisible(true);

        }else if(playerTwoPieces.get() == 2){
            phase=4;
            phaseText.setText("Game ended");
            log.info("Game ended, {} has won the game.",playerNameOne);
            winner=playerNameOne;
            phaseTwoStartedLabel.setText(playerNameTwo + " has only 2 pieces left, therefore " + playerNameOne + " has won the game. Congratulations.");
            phaseTwoStartedLabel.setVisible(true);
            playerTurnLabel.setVisible(false);
            stopWatchTimeline.stop();
            gameResultDao.persist(createGameResult());
            resultsButton.setVisible(true);
        }
    }

    private void checkForPhaseThree() {
        if (playerOnePieces.get()==3) {
            if (!flyingPlayers.contains('1')) {
                phase = 3;
                phaseText.setText("Phase 3");
                phaseTwoStartedLabel.setText(playerNameOne + " has only 3 pieces left. " + playerNameOne + ", you may now start flying your pieces.");
                phaseTwoStartedLabel.setVisible(true);
                flyingPlayers.add('1');
            }

        }

        if(playerTwoPieces.get() == 3){
            if (!flyingPlayers.contains('2')) {
                phase = 3;
                phaseText.setText("Phase 3");
                phaseTwoStartedLabel.setText(playerNameTwo + " has only 3 pieces left. " + playerNameTwo + ", you may now start flying your pieces.");
                phaseTwoStartedLabel.setVisible(true);
                flyingPlayers.add('2');
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
        if (phase>=2 && millFormed){
            if (actingPlayer == '1') {
                playerTwoPieces.set(playerTwoPieces.get() - 1);
            } else {
                playerOnePieces.set(playerOnePieces.get() - 1);
            }
            for (String pieces: removablePieces) {
                String shapeId = "c" + pieces;
                Shape movable = (Shape) mainPane.lookup("#" + shapeId);
                movable.setStroke(Color.BLACK);
                movable.setStrokeWidth(1);
            }
        }else if (phase==1){
            for (String pieces: removablePieces) {
                String shapeId = "c" + pieces;
                Shape movable = (Shape) mainPane.lookup("#" + shapeId);
                movable.setStroke(Color.BLACK);
                movable.setStrokeWidth(1);
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
            removablePieces=gameState.getRemovables(actingPlayer);
            for (String pieces: removablePieces) {
                String shapeId = "c" + pieces;
                Shape movable = (Shape) mainPane.lookup("#" + shapeId);
                movable.setStroke(Color.RED);
                movable.setStrokeWidth(3);
            }
        }



    }

    private void createStopWatch() {
        stopWatchTimeline = new Timeline(new KeyFrame(javafx.util.Duration.ZERO, e -> {
            long millisElapsed = startTime.until(Instant.now(), ChronoUnit.MILLIS);
            stopWatchLabel.setText(DurationFormatUtils.formatDuration(millisElapsed, "HH:mm:ss"));
        }), new KeyFrame(javafx.util.Duration.seconds(1)));
        stopWatchTimeline.setCycleCount(Animation.INDEFINITE);
        stopWatchTimeline.play();
    }

    public GameResult createGameResult() {
        GameResult gr = GameResult.builder()
                .player1(playerNameOne)
                .player2(playerNameTwo)
                .winner(winner)
                .moves(turn.get()-18)
                .duration(Duration.between(startTime, Instant.now()))

                .build();
        return gr;
    }

    public void goToResults(javafx.event.ActionEvent actionEvent) throws IOException {
        fxmlLoader.setLocation(getClass().getResource("/fxml/results.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}

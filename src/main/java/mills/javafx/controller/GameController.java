package mills.javafx.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
import mills.stats.PlayerStat;
import mills.stats.PlayerStatDao;
import org.apache.commons.lang3.time.DurationFormatUtils;
import javax.inject.Inject;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;


@Slf4j
public class GameController {

    @Inject
    private FXMLLoader fxmlLoader;
    @Inject
    private GameResultDao gameResultDao;
    @Inject
    private PlayerStatDao playerStatDao;

    private IntegerProperty turn = new SimpleIntegerProperty();
    private IntegerProperty playerOnePieces = new SimpleIntegerProperty();
    private IntegerProperty playerTwoPieces = new SimpleIntegerProperty();

    @FXML
    private Label phaseText;

    @FXML
    private Label tipsLabel;

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
    private String loser;
    private boolean forfeit =false;
    private boolean draw =false;


    @FXML
    private Label playerNameOneLabel;

    private int playerOneMoves;
    private int playerTwoMoves;

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
    @FXML
    private Button offerDrawButton;
    @FXML
    private Button giveUpButton;





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
        playerOnePieces.set(9);
        playerTwoPieces.set(9);
        playerOneMoves=0;
        playerTwoMoves=0;
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
            log.debug("Circle {} clicked",clicked.getId());

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
                tipsLabel.setText("Phase 2 has started. Click on one of your men you wish to move, then select one of the slots marked with a green circle to move your man there");
                tipsLabel.setVisible(true);
                validMoves=new ArrayList<>();
                playerOnePieces.set(gameState.countPlayerPieces('1'));
                playerTwoPieces.set(gameState.countPlayerPieces('2'));
            }


        }
        else if (phase >= 2 && phase<4){
            Shape clicked = (Shape) mouseEvent.getTarget();
            log.debug("Circle {} clicked",clicked.getId());

            if (actingPlayer==gameState.getPlayerOfPiece(clicked.getId()) &&!millFormed) {


                if (hasClicked && clicked!=previousShape) {
                    updateMovesHighlight(validMoves, Color.BLACK, 1);
                }

                hasClicked = true;
                previousShape = clicked;
                if (phase==3 && flyingPlayers.contains(actingPlayer)){
                    validMoves=gameState.getVacantPoints();
                }else {
                    validMoves = gameState.checkForValidMovement(clicked.getId());
                }
                updateMovesHighlight(validMoves, Color.GREEN, 3);


            }else if (validMoves.contains(clicked.getId().substring(1,3)) && !millFormed && previousShape!=null){
                updateBoardOnRemoval(previousShape,actingPlayer);
                updateBoardOnPlacement(clicked,actingPlayer);
                if (!millFormed) {
                    if (actingPlayer == '1') playerOneMoves++;
                    else playerTwoMoves++;
                    turn.set(turn.get() + 1);
                    updatePlayerTurnLabel(actingPlayer);
                }
                hasClicked=false;
                previousShape=null;
                updateMovesHighlight(validMoves, Color.BLACK, 1);
                if (tipsLabel.isVisible()){
                    tipsLabel.setVisible(false);
                }
            }else if(millFormed) {
                if (gameState.isValidRemoval(clicked.getId(), actingPlayer)) {
                    updateBoardOnRemoval(clicked, actingPlayer);
                    if (actingPlayer == '1') playerOneMoves++;
                    else playerTwoMoves++;
                    turn.set(turn.get() + 1);
                    updatePlayerTurnLabel(actingPlayer);
                }
            }

            checkForPhaseThree();

            checkForGameEnd();

            if (phase>1 && playerTwoPieces.get()==3 && playerOnePieces.get()==3){
                offerDrawButton.setVisible(true);
            }

        }
    }

    private void updateMovesHighlight(ArrayList<String> validMoves, Color black, int i) {
        for (String moves : validMoves) {
            String shapeId = "c" + moves;
            Shape movable = (Shape) mainPane.lookup("#" + shapeId);
            movable.setStroke(black);
            movable.setStrokeWidth(i);
        }
    }

    private void checkForGameEnd() {
        if (playerOnePieces.get()==2) {
            updateOnGameEnd('2');

        }else if(playerTwoPieces.get() == 2){
            updateOnGameEnd('1');
        }
    }

    private void updateOnGameEnd(char winningPlayer) {
        switch (winningPlayer){
            case '1':
                winner=playerNameOne;
                loser=playerNameTwo;
                break;
            case '2':
                winner=playerNameTwo;
                loser=playerNameOne;
                break;
            case ' ':
                winner="";
                loser="";
                break;

        }
        phase=4;
        phaseText.setText("Game ended");
        giveUpButton.setDisable(true);
        if (winner.isEmpty()){
            log.info("Game ended with a draw");
        }else{
            log.info("Game ended, {} has won the game.", winner);
        }

        if (!forfeit && !draw) {
            tipsLabel.setText(loser + " has only 2 pieces left, therefore " + winner + " has won the game. Congratulations.");
            tipsLabel.setVisible(true);
        }else if(forfeit){
            tipsLabel.setText(loser + " gave up. " + winner + " has won the game. Congratulations.");
            tipsLabel.setVisible(true);
        }
        playerTurnLabel.setVisible(false);
        stopWatchTimeline.stop();
        log.debug("Saving result to database...");
        gameResultDao.persist(createGameResult());
        playerStatDao.persist(createPlayerOneStat(playerNameOne));
        playerStatDao.persist(createPlayerTwoStat(playerNameTwo));
        resultsButton.setVisible(true);
    }

    private void checkForPhaseThree() {
        if (playerOnePieces.get()==3) {
            if (!flyingPlayers.contains('1')) {
                if (phase==2){
                    phase = 3;
                    log.info("Phase 3 started");
                }
                phaseText.setText("Phase 3");
                tipsLabel.setText(playerNameOne + " has only 3 pieces left. " + playerNameOne + ", you may now start flying your pieces.");
                tipsLabel.setVisible(true);
                flyingPlayers.add('1');
            }

        }

        if(playerTwoPieces.get() == 3){
            if (!flyingPlayers.contains('2')) {
                if (phase==2){
                    phase = 3;
                    log.info("Phase 3 started");
                }
                phaseText.setText("Phase 3");
                tipsLabel.setText(playerNameTwo + " has only 3 pieces left. " + playerNameTwo + ", you may now start flying your pieces.");
                tipsLabel.setVisible(true);
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
            updateRemoveHighlight(removablePieces, Color.BLACK, 1);
        }else if (phase==1){
            updateRemoveHighlight(removablePieces, Color.BLACK, 1);
        }
        millFormed=false;
        tipsLabel.setVisible(false);
        if (playerOnePieces.get()+playerTwoPieces.get()==0){
            phase=2;
            phaseText.setText("Phase 2");
            log.info("Phase 2 started");
        }
    }

    private void updateRemoveHighlight(ArrayList<String> removablePieces, Color black, int i) {
        for (String pieces : removablePieces) {
            String shapeId = "c" + pieces;
            Shape movable = (Shape) mainPane.lookup("#" + shapeId);
            movable.setStroke(black);
            movable.setStrokeWidth(i);
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
            tipsLabel.setText(playerName+" has formed a mill. You can now remove one of the opponent's pieces.");
            log.info("{} has formed a mill",playerName);
            tipsLabel.setVisible(true);
            millFormed=true;
            removablePieces=gameState.getRemovables(actingPlayer);
            updateRemoveHighlight(removablePieces, Color.RED, 3);
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
        return GameResult.builder()
                .player1(playerNameOne)
                .player2(playerNameTwo)
                .winner(winner)
                .moves(turn.get())
                .duration(Duration.between(startTime, Instant.now()))
                .build();
    }

    public PlayerStat createPlayerOneStat(String player){

        return PlayerStat.builder()
                .player(player)
                .win(player.equals(winner))
                .duration(Duration.between(startTime, Instant.now()))
                .moves(playerOneMoves)
                .draw(draw)
                .build();
    }
    public PlayerStat createPlayerTwoStat(String player){
        return PlayerStat.builder()
                .player(player)
                .win(player.equals(winner))
                .duration(Duration.between(startTime, Instant.now()))
                .moves(playerTwoMoves)
                .draw(draw)
                .build();
    }

    public void goToResults(javafx.event.ActionEvent actionEvent) throws IOException {
        String buttonText = ((Button) actionEvent.getSource()).getText();
        log.debug("{} is pressed", buttonText);
        log.info("Loading results scene...");
        fxmlLoader.setLocation(getClass().getResource("/fxml/results.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void handleGiveUp(ActionEvent actionEvent){
        String buttonText = ((Button) actionEvent.getSource()).getText();
        log.debug("{} button pressed", buttonText);
        String actingPlayer;
        char winner;
        if (turn.get()%2==1){
            actingPlayer=playerNameOne;
            winner='2';
        }else{
            actingPlayer=playerNameTwo;
            winner='1';
        }
        log.info("{} wants to give up the game",actingPlayer);
        log.debug("Showing alert dialog");
        Alert alert =new Alert(Alert.AlertType.NONE, actingPlayer+", are you sure you want to give up?", ButtonType.YES,ButtonType.CANCEL);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            log.info("{} has given up the game",actingPlayer);
            forfeit=true;
            updateOnGameEnd(winner);
        }
    }

    public void offerDraw(ActionEvent actionEvent){
        String buttonText = ((Button) actionEvent.getSource()).getText();
        log.debug("{} button pressed", buttonText);
        String actingPlayer;
        String otherPlayer;
        if (turn.get()%2==1){
            actingPlayer=playerNameOne;
            otherPlayer=playerNameTwo;

        }else{
            actingPlayer=playerNameTwo;
            otherPlayer=playerNameOne;

        }
        log.info("{} has offered a draw",actingPlayer);
        log.debug("Showing alert dialog");

        Alert alert =new Alert(Alert.AlertType.NONE, actingPlayer+" has offered a draw. "+otherPlayer+", do you accept?", ButtonType.YES,ButtonType.NO);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            draw=true;
            log.info("{} has accepted the draw",otherPlayer);
            updateOnGameEnd(' ');
        }else{
            log.info("{} didn't accept the draw",otherPlayer);
        }

    }
}

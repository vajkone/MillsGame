package mills.javafx.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import mills.stats.PlayerStat;
import mills.stats.PlayerStatDao;
import org.apache.commons.lang3.time.DurationFormatUtils;


import javax.inject.Inject;
import java.io.IOException;
import java.time.Duration;


@Slf4j
public class PlayerStatController {

    @Inject
    private FXMLLoader fxmlLoader;

    @Inject
    private mills.stats.PlayerStatDao PlayerStatDao;

    @FXML
    private Label playerNameLabel;

    @FXML
    private Label playerWins;

    @FXML
    private Label avgTimeLabel;

    @FXML
    private Label avgMovesLabel;

    @FXML
    private Label gamesPlayedLabel;

    @FXML
    private Label avgGameDurationLabel;
    @FXML
    private Label winPercentLabel;
    @FXML
    private Label drawsLabel;
    @FXML
    private Label errorLabel;

    @FXML
    private TableColumn<PlayerStat, String> winPercent;

    @FXML
    private TableColumn<PlayerStat, Duration> avgGameDur;

    @FXML
    private TableColumn<PlayerStat, Integer> avgMoves;

    @FXML
    private TableColumn<PlayerStat, Duration> avgTime;

    @FXML
    private TextField playerNameTf;

    @FXML
    private void initialize() {

        log.debug("Player stats...");

    }

    public void handleSearchPlayer(ActionEvent actionEvent){
        if (playerNameTf.getText().isEmpty()){

        }else{
            String playerNameSearched = playerNameTf.getText();
            int wins= (int) PlayerStatDao.getWins(playerNameSearched);
            if(PlayerStatDao.checkPlayerInDb(playerNameSearched).isEmpty()){
                errorLabel.setVisible(true);
            }else{
                errorLabel.setVisible(false);
                int gamesplayed=(int)PlayerStatDao.getGamesCount(playerNameSearched);
                gamesPlayedLabel.setText(String.valueOf(gamesplayed));
                playerWins.setText(String.valueOf(wins));
                winPercentLabel.setText(String.format("%.2f",(double) wins/gamesplayed)+"%");
                drawsLabel.setText(String.valueOf(PlayerStatDao.getDraws(playerNameSearched)));
                playerNameLabel.setText(playerNameSearched);
                avgMovesLabel.setText(String.format("%.2f",PlayerStatDao.getMoves(playerNameSearched)));
                avgGameDurationLabel.setText(DurationFormatUtils.formatDuration(PlayerStatDao.getAvgDuration(playerNameSearched),"mm:ss"));
            }





        }
    }

    public void goToLaunchScreen(ActionEvent actionEvent) throws IOException {
        String buttonText = ((Button) actionEvent.getSource()).getText();
        log.debug("{} is pressed", buttonText);
        log.info("Loading launch scene...");
        fxmlLoader.setLocation(getClass().getResource("/fxml/launch.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

}

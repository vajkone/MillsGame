package mills.javafx.controller;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;

@Slf4j
public class LaunchController {

    @Inject
    private FXMLLoader fxmlLoader;

    @FXML
    private TextField playerOneTF;

    @FXML
    private TextField playerTwoTF;
    @FXML
    private Button resultsPageButton;

    @FXML
    private Label errorLabel;

    public void playGame(ActionEvent actionEvent) throws IOException {
        if (playerOneTF.getText().isEmpty() || playerTwoTF.getText().isEmpty()) {
            errorLabel.setText("Please enter a name for both players!");
            playFadeTransition(errorLabel);

        } else if (playerOneTF.getText().equals(playerTwoTF.getText())) {
            errorLabel.setText("Please enter different names for each player!");
            playFadeTransition(errorLabel);

        } else {

            fxmlLoader.setLocation(getClass().getResource("/fxml/game.fxml"));
            Parent root = fxmlLoader.load();
            fxmlLoader.<GameController>getController().setPlayerNames(playerOneTF.getText(), playerTwoTF.getText());

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            log.info("Player 1's name is set to {}", playerOneTF.getText());
            log.info("Player 2's name is set to {}, loading game scene", playerTwoTF.getText());
        }
    }

    private void playFadeTransition(Label errorLabel) {
        errorLabel.setVisible(true);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(3), errorLabel);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setCycleCount(1);
        fadeTransition.play();
    }

    public void goToResults(ActionEvent actionEvent) throws IOException {

        fxmlLoader.setLocation(getClass().getResource("/fxml/results.fxml"));
        Parent root = fxmlLoader.load();

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setResizable(true);
        stage.show();

    }


}

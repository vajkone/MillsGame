package mills.javafx.controller;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    private Label errorLabel;

    public void playGame(ActionEvent actionEvent) throws IOException {
        if (playerOneTF.getText().isEmpty() || playerTwoTF.getText().isEmpty()) {
            errorLabel.setText("Please enter a name for both players!");
            errorLabel.setVisible(true);
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(3), errorLabel);
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.0);
            fadeTransition.setCycleCount(1);
            fadeTransition.play();

        } else {
            fxmlLoader.setLocation(getClass().getResource("/fxml/game.fxml"));
            Parent root = fxmlLoader.load();
            //fxmlLoader.<GameController>getController().setPlayerName(playerNameTextField.getText());
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            log.info("Player 1's name is set to {}", playerOneTF.getText());
            log.info("Player 2's name is set to {}, loading game scene", playerTwoTF.getText());
        }
    }


}

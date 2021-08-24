package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Massages_Window_Controller {
    @FXML
    private Label massegeField;
    @FXML
    private Button okButton;

    //a method to pass data
    void initData(String massage) {
        massegeField.setText(massage);
    }

    @FXML
    //an action for exit
    private void setOkButton() {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }
}

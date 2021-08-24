package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CreatContact_Controller {

    //to initialize Nodes
    @FXML
    private Pane mainPane;
    @FXML
    private VBox vBox;
    @FXML
    private Button cancel;
    @FXML
    private Button save;

    //Some useful data
    private Contact contact;
    private ArrayList<String> newContact = new ArrayList<String>();
    private File passedFile;

    //to initialize data
    void initData(Contact contact, File passedFile) {
        this.passedFile = passedFile;
        this.contact = contact.clon();
        editFields();
    }

    //to add labels and textfields to scene
    private void editFields() {

        for (int loop = 0; loop < contact.getPlaceHolders().size(); loop++) {
            HBox hBox = new HBox();
            Label label = new Label("Add the new " + contact.getPlaceHolders().get(loop));
            TextField textField = new TextField();
            textField.setOnAction(getTextField);
            hBox.getChildren().addAll(label, textField);
            vBox.getChildren().add(hBox);
        }
    }

    //a handler to get text from textFields
    private EventHandler<ActionEvent> getTextField =
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    newContact.add(((TextField) event.getSource()).getText());
                }
            };

    //a handler to save the new contact to the opened Contact File
    @FXML
    private void setSave() throws IOException {
        System.out.println(newContact);
        FileWriter writer = new FileWriter(passedFile, true);
        CsvFile.writeLine(writer, newContact);
        writer.flush();
        writer.close();
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

    //an action for exit
    @FXML
    private void setCancel(ActionEvent event) {
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }
}

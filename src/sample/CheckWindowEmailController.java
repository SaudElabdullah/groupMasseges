package sample;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class CheckWindowEmailController {

    //initializing the nodes
    @FXML
    private Pane mainPane;
    @FXML
    private TableView checkList;
    @FXML
    private HBox Tools;
    @FXML
    private Button cancel;
    @FXML
    private Button continueToSendEmailButton;

    //initializing some useful data
    private Contact contact;
    private String massege;
    private String userName;
    private String passWord;

    //to pass data
    void initData(Contact contact, String massege) {
        this.contact = contact.clon();
        this.massege = massege;
        setCheckList();
    }

    //a method to set the window
    public void setCheckList() {
        String[] placeHolders = new String[this.contact.getPlaceHolders().size()];
        for (int count = 0; count < this.contact.getPlaceHolders().size(); count++) {
            placeHolders[count] = this.contact.getPlaceHolders().get(count);
        }

        String[][] contactArray = new String[this.contact.getContact().size()]
                [(this.contact.getContact().get(0)).size()];
        for (int countFirstD = 0; countFirstD < this.contact.getContact().size(); countFirstD++) {
            for (int countSecondD = 0; countSecondD < (this.contact.getContact().get(countFirstD)).size(); countSecondD++) {
                contactArray[countFirstD][countSecondD] = (this.contact.getContact().get(countFirstD)).get(countSecondD);
            }
        }
        ObservableList<String[]> data = FXCollections.observableArrayList();
        data.addAll(Arrays.asList(contactArray));
        for (int loop = 0; loop < placeHolders.length; loop++) {
            TableColumn columnTitle = new TableColumn(placeHolders[loop]);
            final int columnNo = loop;
            columnTitle.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<String[], String>
                    , ObservableValue<String>>() {
                @Override
                public ObservableValue<String>
                call(TableColumn.CellDataFeatures<String[], String> param) {
                    return new SimpleStringProperty((param.getValue()[columnNo]));
                }
            });
            checkList.getColumns().add(columnTitle);
        }
        checkList.setItems(data);
    }

    //an action for exit
    @FXML
    private void setCancel(ActionEvent event) {
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

    //a method to creat massages
    private CreatMassage[] messageToBeSent() {
        CreatMassage[] messageArray = new CreatMassage[this.contact.getContact().size()];
        String[] separateingString = massege.split(" ");
        for (int loopOfContact = 0; loopOfContact < contact.getContact().size(); loopOfContact++) {
            String massegeContent = "";
            for (int loopOfString = 0; loopOfString < separateingString.length; loopOfString++) {
                if (separateingString[loopOfString].contains("[[")) {
                    for (int loopOfPlaceHolders = 0; loopOfPlaceHolders < contact.getPlaceHolders().size(); loopOfPlaceHolders++) {
                        if (separateingString[loopOfString].contains(contact.getPlaceHolders().get(loopOfPlaceHolders))) {
                            massegeContent = massegeContent + " " + contact.getContact().get(loopOfContact).get(loopOfPlaceHolders);
                        }
                    }
                } else {
                    massegeContent = massegeContent + " " + separateingString[loopOfString];
                }
            }
            messageArray[loopOfContact] = new CreatMassage(massegeContent);
        }
        return messageArray;
    }

    //a handler to send emails
    @FXML
    private void setContinueToSendEmailButton(ActionEvent event) throws IOException {
        String subject;
        CreatMassage[] messageToBeSent = messageToBeSent();
        loginIntoEmail();
        String[] toEmails = searchEmail();
        subject = "Mail-Merge";
        SendEmailOffice365 mailer = null;
        for (int times = 0; times < this.contact.getContact().size(); times++) {
            mailer = new SendEmailOffice365(userName, passWord, toEmails[times], subject, messageToBeSent[times].getMassage());
            mailer.sendEmail();

        }
//        try {
//            if (mailer.sendEmail()) {
//                openMassageWindow("The Email sent Successfully");
//            }
//        } catch (Exception e) {
//            openMassageWindow("The Email sent Failed");
//        }
//        mailer.sendEmail();
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

    //a method to find the emails
    private String[] searchEmail() {
        String[] emails = new String[this.contact.getContact().size()];
        int indextOfEmail = 0;
        for (int loopOfPlaceHolders = 0; loopOfPlaceHolders < this.contact.getPlaceHolders().size(); loopOfPlaceHolders++) {
            if (this.contact.getPlaceHolders().get(loopOfPlaceHolders).equalsIgnoreCase("[[EMAIL_ADDRESS]]")) {
                indextOfEmail = loopOfPlaceHolders;
            }
        }
        for (int loopOfEmails = 0; loopOfEmails < this.contact.getContact().size(); loopOfEmails++) {
            emails[loopOfEmails] = this.contact.getContact().get(loopOfEmails).get(indextOfEmail);
        }
        return emails;
    }

    //a method to prompt the user log in his email
    private void loginIntoEmail() {
        final LoginCredentials[] login = new LoginCredentials[1];
        Dialog<LoginCredentials> dialog = new Dialog<>();
        dialog.setTitle("Login Dialog");
        dialog.setHeaderText("Enter your portal username and password:");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> username.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new LoginCredentials(username.getText(), password.getText());
            }
            return null;
        });

        Optional<LoginCredentials> result = dialog.showAndWait();

        result.ifPresent(usernamePassword -> {
            login[0] = usernamePassword;
        });
        userName = username.getText();
        passWord = password.getText();
    }

    //a method to open alerts
    private void openMassageWindow(String massageToShow) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("masseges_Window.fxml"));
        Stage massageWindow = new Stage();
        massageWindow.setScene(new Scene(loader.load()));
        Massages_Window_Controller massage =
                loader.<Massages_Window_Controller>getController();
        massage.initData(massageToShow);
        massageWindow.show();
    }
}
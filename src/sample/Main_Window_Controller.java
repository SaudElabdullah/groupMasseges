package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main_Window_Controller {

    //initializing the nodes
    @FXML
    private BorderPane Root;
    @FXML
    private ToolBar toolBar;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu menuFile;
    @FXML
    private Menu menuOption;
    @FXML
    private MenuItem openTemplet;
    @FXML
    private MenuItem openContact;
    @FXML
    private MenuItem exit;
    @FXML
    private MenuItem saveTamplet;
    @FXML
    private MenuItem createContact;
    @FXML
    private TextArea templateArea;
    @FXML
    private HBox tools;
    @FXML
    private Button sendEmail;
    @FXML
    private Button createPDF;
    @FXML
    private MenuButton placeHolders;

    //initializing some useful data
    private Contact contact;
    private String placeHolderKind = "";
    private int pos = 0;
    private static File passedFile;

    //an action for exit
    @FXML
    private void setExit(ActionEvent event) {
        Platform.exit();
    }

    //an action for opening Template
    @FXML
    private void setOpenTemplate(ActionEvent event) throws IOException {
        final FileChooser fileChooser = new FileChooser();
        configureFileChooserTemplate(fileChooser);
        Stage stage = new Stage();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            fileToString(file);
            openMassageWindow("The Template opened Successfully");
        }
    }

    //passedFile method that configure file chooser for the Template
    private static void configureFileChooserTemplate(final FileChooser fileChooser) {
        fileChooser.setTitle("Choose passedFile template Text");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text", "*.txt")
        );
    }

    // passedFile method that get the text from the chosen file and set it in the textField
    private void fileToString(final File file) throws FileNotFoundException {
        Scanner outputFile = new Scanner(new FileInputStream(file));
        String outputForTextField = "";
        while (outputFile.hasNext()) {
            outputForTextField = outputForTextField + outputFile.nextLine() + System.lineSeparator();
        }
        templateArea.setText(outputForTextField);
    }

    //an action for opening the Contact
    @FXML
    private void setOpenContact(ActionEvent event) throws IOException {
        final FileChooser fileChooser = new FileChooser();
        configureFileChooserContact(fileChooser);
        Stage stage = new Stage();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            fileToArray(file);
            openMassageWindow("The Contact opened Successfully");
        }
        for (int loop = 0; loop < contact.getPlaceHolders().size(); loop++) {
            MenuItem menuItem = new MenuItem(contact.getPlaceHolders().get(loop));
            menuItem.setOnAction(setMenuButton);
            menuItem.setId(contact.getPlaceHolders().get(loop));
            placeHolders.getItems().add(menuItem);
        }
        passedFile = file;
    }

    //passedFile method that configure file chooser for the Contact
    private static void configureFileChooserContact(final FileChooser fileChooser) {
        fileChooser.setTitle("Choose passedFile Contact");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text", "*.txt")
                , new FileChooser.ExtensionFilter("CSV", "*.csv")
        );
    }

    //A method that creat passedFile Contact Object
    private void fileToArray(final File file) throws FileNotFoundException {

        Scanner outputFile = new Scanner(new FileInputStream(file));
        ArrayList<String> placeHoldersArray;
        ArrayList<ArrayList<String>> contactArray = new ArrayList<>();
        List<String> temporaryList;
        String[] elements;

        elements = outputFile.next().split(",");
        temporaryList = Arrays.asList(elements);
        placeHoldersArray = new ArrayList<>(temporaryList);

        while (outputFile.hasNext()) {
            elements = outputFile.next().split(",");
            temporaryList = Arrays.asList(elements);
            contactArray.add(new ArrayList<>(temporaryList));
        }
        contact = new Contact(contactArray, placeHoldersArray);
    }

    //passedFile handler for the place Holders
    private EventHandler<ActionEvent> setMenuButton =
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    String o = event.getSource().toString();
                    int startOfAWord = o.indexOf("[") + 4;
                    int EndOfAWord = o.indexOf("]") + 2;
                    placeHolderKind = o.substring(startOfAWord, EndOfAWord);
                }
            };

    //an action to save passedFile new Template
    @FXML
    private void setSaveTemplate(ActionEvent event) {
        String content = templateArea.getText();
        FileChooser fileChooser = new FileChooser();
        Stage stage = new Stage();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (.txt)", ".txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            saveTextToFile(content, file);
        }
    }

    //passedFile method to save the new Template to passedFile file
    private void saveTextToFile(String content, File file) {
        try {
            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.println(content);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //an action that get passedFile text from passedFile file and then get the text and set the Text field to it
    @FXML
    public void setTemplateArea(MouseEvent mouseEvent) {
        templateArea.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String formerText = templateArea.getText();
                pos = templateArea.getCaretPosition();
                templateArea.setText(formerText.substring(0, pos) +
                        placeHolderKind +
                        formerText.substring(pos));
                placeHolderKind = "";
            }
        });
    }

    //an action to open creat contact window
    @FXML
    private void setCreateContact(ActionEvent event) throws IOException {
        openAnotherWindow("CreatContact.fxml");
    }

    //an action to open creat PDF Window
    @FXML
    private void setCreatePDF(ActionEvent event) throws IOException {
        openAnotherWindow("Check_Window_PDF.fxml");
    }

    //an action to open Send Email window
    @FXML
    private void setSendEmail(ActionEvent event) throws IOException {
        openAnotherWindow("Check_Window_Email.fxml");
    }

    //an action to facilitate opening another window
    private void openAnotherWindow(String fxml) throws IOException {
        boolean open = true;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Stage newStage = new Stage();
        newStage.setScene(new Scene(loader.load()));
        try {
            if (fxml.equals("Check_Window_Email.fxml")) {
                CheckWindowEmailController contactTobeSend =
                        loader.<CheckWindowEmailController>getController();
                contactTobeSend.initData(contact, templateArea.getText());
            } else if (fxml.equals("Check_Window_PDF.fxml")) {
                CheckWindowPDFController contactTobePDF =
                        loader.<CheckWindowPDFController>getController();
                contactTobePDF.initData(contact, templateArea.getText());
            } else if (fxml.equals("CreatContact.fxml")) {
                CreatContact_Controller contactTobeAdded =
                        loader.<CreatContact_Controller>getController();
                contactTobeAdded.initData(contact, passedFile);
            }
        } catch (Exception e) {
            openMassageWindow("You either did not upload the template or the contact or both");
            open = false;
        }
        if (open) {
            newStage.show();
        }
    }

    //passedFile method to open alerts
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
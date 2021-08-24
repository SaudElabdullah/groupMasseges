package sample;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CheckWindowPDFController {

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
    private Button continueToCreatPDFButton;

    //initializing some useful data
    private Contact contact;
    private String massege;
    private final int uniqueName = 0;

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

    private boolean makePDF(CreatMassage[] messageToBeSent) throws IOException {
        try {
            OutputStream file = new FileOutputStream(new File("PDF-File" + uniqueName + ".pdf"));
            Document document = new Document();
            PdfWriter.getInstance(document, file);
            document.open();
            for (int loopOfAddingMassages = 0; loopOfAddingMassages < contact.getContact().size(); loopOfAddingMassages++) {
                document.add(new Paragraph(messageToBeSent[loopOfAddingMassages].getMassage()));
                document.newPage();
            }
            document.close();
            file.close();
            return true;
        } catch (Exception e) {
            openMassageWindow("The save as PDF Fialed");
            return false;
        }
    }

    //a method to save the PDF file in the choosen directory
    private void save() throws IOException {
        FileChooser fileChooser = new FileChooser();
        Stage window = new Stage();
        window.setTitle("Save PDF");
        fileChooser.setTitle("Save PDF File");
        FileChooser.ExtensionFilter pdfFilter = new FileChooser
                .ExtensionFilter("PDF files (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(pdfFilter);
        File directory = fileChooser.showSaveDialog(window);
        File PDFFile = new File("PDF-File" + uniqueName + ".pdf");
        if (directory != null) {
            try {
                Files.copy(PDFFile.toPath(), directory.toPath());
                openMassageWindow("The save as PDF Successfully");
            } catch (IOException ex) {
                openMassageWindow("The save as PDF Fialed");
            }
        }
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

    //an action for exit
    @FXML
    private void setCancel(ActionEvent event) {
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

    //a handler to crat the PDF File
    @FXML
    private void setContinueToCreatPDFButton(ActionEvent event) throws IOException {
        makePDF(messageToBeSent());
        save();
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
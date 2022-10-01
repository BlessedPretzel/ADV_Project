package se233.project.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import se233.project.Launcher;
import se233.project.Model.FileDirectories;
import se233.project.Model.FileExtension;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ViewController {
    private FileDirectories fileDirectories = new FileDirectories();
    private ArrayList<String> fileArrayList = new ArrayList<>();
    @FXML
    private Pane dropPane;
    @FXML
    private MenuBar menuBar;
    @FXML
    private ChoiceBox<FileExtension> extensionChoiceBox = new ChoiceBox<FileExtension>();
    @FXML
    private ListView<String> listView;
    @FXML
    private Label progressLabel;
    @FXML
    private Button browseButton, addButton, deleteButton, actionButton, clearButton;
    @FXML
    private TextField directoryTextField, fileNameTextField, passwordField;
    @FXML
    private ProgressBar progressBar;
    @FXML
    public void initialize() {

        extensionChoiceBox.setItems(FXCollections.observableList(Arrays.asList(FileExtension.ZIP,FileExtension.TARGZ)));
        extensionChoiceBox.setValue(FileExtension.ZIP);
        directoryTextField.setText("D:\\Code\\ADV_Project\\testfolder");

        extensionChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> {
            if (t1.equals(1)) {
                passwordField.setEditable(false);
                passwordField.setOpacity(0.5);
            } else {
                passwordField.setEditable(true);
                passwordField.setOpacity(1);
            }
        });

        dropPane.setOnDragOver(dragEvent -> {
            Dragboard dragboard = dragEvent.getDragboard();
            if (dragboard.hasFiles()) {
                dragEvent.acceptTransferModes(TransferMode.COPY);
            } else {
                dragEvent.consume();
            }
        });

        dropPane.setOnDragDropped(dragEvent -> {
            Dragboard dragboard = dragEvent.getDragboard();
            boolean success = false;
            if (dragboard.hasFiles()) {
                success = true;
                int total_files = dragboard.getFiles().size();
                for (int i = 0; i<total_files; i++) {
                    File file = dragboard.getFiles().get(i);
                    if (!fileArrayList.contains(file.getPath()))
                        fileArrayList.add(file.getPath());
                }
            }
            fileDirectories.setAndUpdate(fileArrayList);
            listView.setItems(fileDirectories.getObservableFileList());
        });

        addButton.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select files");
            try {
                fileArrayList.addAll(fileChooser.showOpenMultipleDialog(Launcher.stage.getOwner())
                        .stream()
                        .map(File::getAbsolutePath)
                        .toList());
                fileDirectories.setAndUpdate(fileArrayList);
            } catch (NullPointerException e) {
                System.out.println("Add Button: No directory chosen");
            }
            listView.setItems(fileDirectories.getObservableFileList());
        });

        deleteButton.setOnAction(actionEvent -> {
            fileArrayList.remove(listView.getSelectionModel().getSelectedIndex());
            fileDirectories.setAndUpdate(fileArrayList);
            listView.setItems(fileDirectories.getObservableFileList());
        });

        browseButton.setOnAction(actionEvent -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select a directory");
            directoryTextField.setText(directoryChooser.showDialog(Launcher.stage.getOwner()).getAbsolutePath());
        });

        clearButton.setOnAction(actionEvent -> {

        });

        actionButton.setOnAction(actionEvent -> {
            if (directoryTextField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Notice");
                alert.setHeaderText("Please enter the file directory");
                alert.showAndWait();
                return;
            }
            if (fileNameTextField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Notice");
                alert.setHeaderText("Please enter the file name");
                alert.showAndWait();
                return;
            }
            try {
                if (extensionChoiceBox.getValue().equals(FileExtension.ZIP)) {
                    CompressController.compressToZip(fileDirectories.getFileList(),
                            directoryTextField.getText() + "\\" + fileNameTextField.getText(),
                            passwordField.getText(),
                            progressLabel,
                            progressBar);
                }
                else {
                    CompressController.compressToTargz(fileDirectories.getFileList(),
                            directoryTextField.getText() + "\\" + fileNameTextField.getText()+".tar.gz");
                }
            } catch (IllegalStateException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Notice");
                alert.setHeaderText("No file");
                alert.showAndWait();
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}

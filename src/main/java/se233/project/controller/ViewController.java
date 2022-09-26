package se233.project.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import se233.project.Launcher;
import se233.project.Model.FileList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ViewController {
    private FileList fileList = new FileList();
    private ArrayList<File> fileArrayList = new ArrayList<>();
    @FXML
    private Pane dropPane;
    @FXML
    private MenuBar menuBar;
    @FXML
    private ListView<String> listView;
    @FXML
    private Button browseButton, addButton, deleteButton, actionButton;
    @FXML
    private TextField directoryTextField;
    @FXML
    private TextField fileNameTextField;
    @FXML
    private ProgressBar progressBar;
    @FXML
    public void initialize() {

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
                    if (!fileArrayList.contains(file))
                        fileArrayList.add(file);
                }
            }
            fileList.setAndUpdate(fileArrayList);
            listView.setItems(fileList.getObservableFileList());
        });

        addButton.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select files");
            fileArrayList.addAll(fileChooser.showOpenMultipleDialog(Launcher.stage.getOwner()));
            fileList.setAndUpdate(fileArrayList);
            listView.setItems(fileList.getObservableFileList());
        });

        deleteButton.setOnAction(actionEvent -> {
            fileArrayList.remove(listView.getSelectionModel().getSelectedIndex());
            fileList.setAndUpdate(fileArrayList);
            listView.setItems(fileList.getObservableFileList());
        });

        browseButton.setOnAction(actionEvent -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select a directory");
            directoryTextField.setText(directoryChooser.showDialog(Launcher.stage.getOwner()).getAbsolutePath());
        });

        actionButton.setOnAction(actionEvent -> {
            try {
                System.out.println(directoryTextField.getText()+fileNameTextField.getText());
                CompressController.compressToZip(fileList.getFileList(), directoryTextField.getText()+fileNameTextField.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}

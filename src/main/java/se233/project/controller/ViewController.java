package se233.project.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import se233.project.Launcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewController {
    private List<File> files = new ArrayList<>();
    private ObservableList<String> fileObservableList;
    @FXML
    private Pane dropPane;
    @FXML
    private MenuBar menuBar;
    @FXML
    private ListView listView;
    @FXML
    private Button browseButton, addButton, deleteButton;
    @FXML
    private TextField directoryTextBox;
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
                    if (!files.contains(file))
                        files.add(file);
                }
            }
            fileObservableList = FXCollections.observableList(files.stream().map(list -> list.getName()).toList());
            listView.setItems(fileObservableList);
        });

        addButton.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select files");
            files.addAll(fileChooser.showOpenMultipleDialog(Launcher.stage.getOwner()));
        });

        deleteButton.setOnAction(actionEvent -> {
            List<Integer> selectedItems = listView.getSelectionModel().getSelectedIndices();
            for (int i: selectedItems){
                files.remove(i);
            }
            fileObservableList = FXCollections.observableList(files.stream().map(list -> list.getName()).toList());
            listView.setItems(fileObservableList);
        });

        browseButton.setOnAction(actionEvent -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select a directory");
            directoryTextBox.setText(directoryChooser.showDialog(Launcher.stage.getOwner()).getAbsolutePath());

        });
    }

    public List<File> getFiles() {
        return files;
    }
}

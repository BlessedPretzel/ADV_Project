package se233.project.controller;

import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

import java.io.File;
import java.util.ArrayList;

public class ViewController {
    private ArrayList<File> files;
    @FXML
    private Pane dropPane;
    @FXML
    private MenuBar menuBar;
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
                    files.add(file);
                }
            }
        });

    }

    public ArrayList<File> getFiles() {
        return files;
    }
}

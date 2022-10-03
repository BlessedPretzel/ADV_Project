/*
    AUTHOR
    642115016 Danaikrit Jaiwong
    642115024 Thaiphat Sukhumpraisan
 */
package se233.project.controller;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.lingala.zip4j.ZipFile;
import se233.project.Launcher;
import se233.project.model.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ViewController {
    private FileDirectories fileDirectories = new FileDirectories();
    private ArrayList<String> fileArrayList = new ArrayList<>();
    private ExecutorService executorService;
    @FXML
    private Pane dropPane;
    @FXML
    private ChoiceBox<FileExtension> extensionChoiceBox = new ChoiceBox<FileExtension>();
    @FXML
    private ListView<String> listView;
    @FXML
    private Label progressLabel;
    @FXML
    private Button browseButton, addButton, deleteButton, archiveButton, clearButton, extractButton;
    @FXML
    private TextField directoryTextField, fileNameTextField, passwordField;
    @FXML
    private TabPane tabPane;
    @FXML
    public void initialize() throws NullPointerException {

        extensionChoiceBox.setItems(FXCollections.observableList(Arrays.asList(FileExtension.ZIP,FileExtension.TARGZ)));
        extensionChoiceBox.setValue(FileExtension.ZIP);
        directoryTextField.setText(System.getProperty("user.dir"));

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
            SelectionModel<Tab> tabSelectionModel = tabPane.getSelectionModel();
            tabSelectionModel.select(0);
            if (fileDirectories.isExtractable()) {
                tabSelectionModel.select(1);
            }
        });

        addButton.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select files");
            fileArrayList.addAll(fileChooser.showOpenMultipleDialog(Launcher.stage.getOwner())
                    .stream()
                    .map(File::getAbsolutePath)
                    .toList());
            fileDirectories.setAndUpdate(fileArrayList);
            listView.setItems(fileDirectories.getObservableFileList());
            SelectionModel<Tab> tabSelectionModel = tabPane.getSelectionModel();
            tabSelectionModel.select(0);
            if (fileDirectories.isExtractable()) {
                tabSelectionModel.select(1);
            }
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
            fileArrayList = new ArrayList<>();
            fileDirectories.setAndUpdate(fileArrayList);
            listView.setItems(fileDirectories.getObservableFileList());
        });

        archiveButton.setOnAction(actionEvent -> {
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
            executorService = Executors.newSingleThreadExecutor();
            Task<Void> processTask = new Task<>() {
                @Override
                public Void call() {
                    try {
                        if (extensionChoiceBox.getValue().equals(FileExtension.ZIP)) {
                            executorService.submit(new ZipCompressor(fileDirectories.getFileList(),
                                    directoryTextField.getText() + "\\" + fileNameTextField.getText() + ".zip",
                                    passwordField.getText()));
                        }
                        else {
                            executorService.submit(new TargzCompressor(fileDirectories.getFileList(),
                                    directoryTextField.getText() + "\\" + fileNameTextField.getText()+".tar.gz"));
                        }
                    } catch (IllegalStateException  e) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Notice");
                        alert.setHeaderText("No file");
                        alert.showAndWait();
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            Thread thread = new Thread(processTask);
            thread.setDaemon(true);
            thread.start();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Notice");
            alert.setHeaderText("Done!");
            alert.showAndWait();
        });

        extractButton.setOnAction(actionEvent -> {
            if (!fileDirectories.isExtractable()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Notice");
                alert.setHeaderText("Invalid file extension");
                alert.showAndWait();
                return;
            }
            // Get passwords for each zip if it is encrypted
            Map<String, String> map =
            fileDirectories.getFileList().stream().collect(Collectors.toMap(s-> s,s -> {
                if (s.contains(".zip")) {
                    try (ZipFile zipFile = new ZipFile(new File(s))) {
                        if (zipFile.isEncrypted()) {
                            TextInputDialog inputDialog = new TextInputDialog();
                            inputDialog.setTitle("Enter password");
                            inputDialog.setHeaderText("Enter password for " + zipFile.getFile().getName());
                            inputDialog.showAndWait();
                            return inputDialog.getResult();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return "";
            }));
            Task<Void> processTask = new Task<>() {
                @Override
                public Void call() {
                    executorService = Executors.newFixedThreadPool(fileDirectories.getFileList().size());
                    map.forEach((dir, pass) -> {
                        if (dir.contains(".zip")) {
                            executorService.submit(new ZipExtractor(dir, directoryTextField.getText(), pass));
                        } else {
                            executorService.submit(new TargzExtractor(dir, directoryTextField.getText()));
                        }
                    });
                    executorService.shutdown();
                    return null;
                }
            };
            Thread thread = new Thread(processTask);
            thread.setDaemon(true);
            thread.start();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Notice");
            alert.setHeaderText("Done!");
            alert.showAndWait();
        });
    }
}
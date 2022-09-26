package se233.project.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileList {
    private List<File> fileList = new ArrayList<>();
    private ObservableList<String> observableFileList;

    public FileList() {}

    /*
        Set fileList and update observableFileList to current value of fileList
        @param fileList - List of files
     */
    public void setAndUpdate(List<File> fileList) {
        setFileList(fileList);
        observableFileList = FXCollections.observableList(fileList.stream().map(File::getName).toList());
    }

    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
    }

    public List<File> getFileList() {
        return fileList;
    }

    public ObservableList<String> getObservableFileList() {
        return observableFileList;
    }
}

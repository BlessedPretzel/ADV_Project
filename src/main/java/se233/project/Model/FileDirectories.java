package se233.project.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class FileDirectories {
    private List<String> fileDirectories = new ArrayList<>();
    private ObservableList<String> observableFileList;

    public FileDirectories() {}

    /*
        Set fileDirectories and update observableFileList to current value of fileDirectories
        @param fileDirectories - List of files
     */
    public void setAndUpdate(List<String> fileList) {
        setFileList(fileList);
        observableFileList = FXCollections.observableList(fileList.stream().toList());
    }

    public void setFileList(List<String> fileList) {
        this.fileDirectories = fileList;
    }

    public List<String> getFileList() {
        return fileDirectories;
    }

    public ObservableList<String> getObservableFileList() {
        return observableFileList;
    }
}

/*
    AUTHOR
    642115016 Danaikrit Jaiwong
    642115024 Thaiphat Sukhumpraisan
 */
package se233.project.controller;

import javafx.concurrent.Task;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextInputDialog;
import net.lingala.zip4j.ZipFile;

import java.io.File;
import java.io.IOException;

public class ZipExtractor extends Task<Void> {
    private String sourceDir, extDir, password;

    public ZipExtractor(String sourceDir, String extDir, String password) {
        this.sourceDir = sourceDir;
        this.extDir = extDir;
        this.password = password;
    }

    @Override
    protected Void call() {
        File file = new File(sourceDir);
        try (ZipFile zipFile = new ZipFile(file)) {
            zipFile.setPassword(password.toCharArray());
            zipFile.extractAll(extDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
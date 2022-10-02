package se233.project.Model;

import javafx.concurrent.Task;
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
        try (ZipFile zipFile = new ZipFile(file, password.toCharArray())) {
            zipFile.extractAll(extDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
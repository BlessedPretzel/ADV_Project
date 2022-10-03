/*
    AUTHOR
    642115016 Danaikrit Jaiwong
    642115024 Thaiphat Sukhumpraisan
 */
package se233.project.controller;

import javafx.concurrent.Task;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.IOException;
import java.util.List;

public class ZipCompressor extends Task<Void> {
    private List<String> fileDirectories;
    private String directory, password;

    public ZipCompressor(List<String> fileDirectories, String directory, String password) {
        this.fileDirectories = fileDirectories;
        this.directory = directory;
        this.password = password;
    }
    @Override
    protected Void call() throws IOException {
        // throw IllegalStateException when there is no file
        if (fileDirectories.isEmpty()) {
            throw new IllegalStateException();
        }

        boolean encrypt = !password.isEmpty();
        ZipFile zipFile;
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setCompressionMethod(CompressionMethod.DEFLATE);
        zipParameters.setCompressionLevel(CompressionLevel.NORMAL);
        if (encrypt) {
            zipParameters.setEncryptFiles(true);
            zipParameters.setEncryptionMethod(EncryptionMethod.AES);
            zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
            zipFile = new ZipFile(directory, password.toCharArray());
        }
        else {
            zipFile = new ZipFile(directory);
        }
        zipFile.setRunInThread(true);
        for (String file: fileDirectories) {
            zipFile.addFile(file, zipParameters);
        }
        zipFile.close();
        return null;
    }
}

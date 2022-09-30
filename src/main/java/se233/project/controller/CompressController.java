package se233.project.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import net.lingala.zip4j.progress.ProgressMonitor;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CompressController {

    /*
        Create .zip file from array of file directories into the specified directory. Can be encrypted with password.
        This function uses zip4j.

        @param files - array of files
        @param directory - directory for the file
        @param password - password if needed
     */
    public static void compressToZip(List<String> fileDirectories, String directory, String password, Label label, ProgressBar progressBar) throws IOException, InterruptedException {
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
        ProgressMonitor progressMonitor = zipFile.getProgressMonitor();
        zipFile.setRunInThread(true);
        for (String file: fileDirectories) {
            zipFile.addFile(file, zipParameters);
            while (!progressMonitor.getState().equals(ProgressMonitor.State.READY)) {
                label.setText(progressMonitor.getPercentDone()+"%");
                progressBar.setProgress(progressMonitor.getPercentDone());
            }
        }
        zipFile.close();
    }
    public static void compressToTargz(List<String> fileDirectories, String directory, String password, Label label, ProgressBar progressBar) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(directory);
        TarArchiveOutputStream tarArchiveOutputStream = new TarArchiveOutputStream(fileOutputStream);

        for (String file : fileDirectories) {
            TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(Path.of(file));
            tarArchiveOutputStream.putArchiveEntry(tarArchiveEntry);
        }
        tarArchiveOutputStream.closeArchiveEntry();
        tarArchiveOutputStream.finish();
    }
    /*
        Create .zip file from array of files into the specified directory.

        @param files - array of files
        @param directory - directory for the file
     */
    /*public static void compressToZip(List<File> files, String directory) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(directory);
        ZipOutputStream zipOut = new ZipOutputStream(fileOut);
        for (File file : files) {
            FileInputStream fileInputStream = new FileInputStream(file);
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fileInputStream.read(bytes)) >= 0) {
                zipOut.write(bytes,0,length);
            }
            fileInputStream.close();
        }
        zipOut.close();
        fileOut.close();
    }*/
}
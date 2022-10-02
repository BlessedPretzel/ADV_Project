package se233.project.controller;

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
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

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
    public static void encryptZip(File file, String directory, String password) throws IOException {
        ZipFile zipFile;
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setCompressionMethod(CompressionMethod.DEFLATE);
        zipParameters.setCompressionLevel(CompressionLevel.NORMAL);
        zipParameters.setEncryptFiles(true);
        zipParameters.setEncryptionMethod(EncryptionMethod.AES);
        zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
        zipFile = new ZipFile(directory, password.toCharArray());
        zipFile.setRunInThread(true);
        zipFile.addFile(file, zipParameters);
        zipFile.close();
    }

    public static void compressToTargz(List<String> fileDirectories, String directory, String password) throws IOException {
        File newFile = new File(directory);
        FileOutputStream fileOutputStream = new FileOutputStream(newFile);
        GzipCompressorOutputStream gzipCompressorOutputStream = new GzipCompressorOutputStream(fileOutputStream);
        TarArchiveOutputStream tarArchiveOutputStream = new TarArchiveOutputStream(gzipCompressorOutputStream);

        for (Path file : fileDirectories.stream().map(Path::of).toList()) {
            TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(file.toFile(), file.getFileName().toString());
            tarArchiveOutputStream.putArchiveEntry(tarArchiveEntry);
            IOUtils.copy(file.toFile(), tarArchiveOutputStream);
            tarArchiveOutputStream.closeArchiveEntry();
        }
        tarArchiveOutputStream.finish();
        gzipCompressorOutputStream.finish();
        tarArchiveOutputStream.close();
        gzipCompressorOutputStream.close();
        fileOutputStream.close();
    }
}
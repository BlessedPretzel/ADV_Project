/*
    AUTHOR
    642115016 Danaikrit Jaiwong
    642115024 Thaiphat Sukhumpraisan
 */
package se233.project.controller;

import javafx.concurrent.Task;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class TargzCompressor extends Task<Void> {
    private List<String> fileDirectories;
    private String directory;

    public TargzCompressor(List<String> fileDirectories, String directory) {
        this.fileDirectories = fileDirectories;
        this.directory = directory;
    }

    @Override
    protected Void call() throws IOException {
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
        return null;
    }
}
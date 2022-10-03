package se233.project.model;

import javafx.concurrent.Task;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;

public class TargzExtractor extends Task<Void> {
    private String sourceDir, extDir;

    public TargzExtractor(String sourceDir, String extDir) {
        this.sourceDir = sourceDir;
        this.extDir = extDir;
    }

    @Override
    protected Void call() throws IOException {
        File file = new File(sourceDir);
        FileInputStream fileInputStream = new FileInputStream(file);
        GzipCompressorInputStream gzipCompressorInputStream = new GzipCompressorInputStream(fileInputStream);
        TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(gzipCompressorInputStream);
        TarArchiveEntry tarEntry;
        while ((tarEntry = tarArchiveInputStream.getNextTarEntry()) != null) {
            IOUtils.copy(tarArchiveInputStream, new FileOutputStream(new File(extDir, tarEntry.getName())));
        }
        tarArchiveInputStream.close();
        gzipCompressorInputStream.close();
        fileInputStream.close();
        return null;
    }
}
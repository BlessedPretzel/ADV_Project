package se233.project.controller;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CompressController {

    /*
        Create .zip file from array of files into the specified directory.

        @param files - array of files
        @param directory - directory for the file
     */
    public static void compressToZip(List<File> files, String directory) throws IOException {
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
    }
    /*
    public static void main(String[] args) throws IOException {
        File file = new File("C:\\Users\\rysei\\Downloads\\cat.jpg");
        ArrayList<File> files = new ArrayList<>();
        files.add(file);
        compressToZip(files, "D:\\Code\\ADV_Project\\testfolder\\cat.zip");
    }*/
}
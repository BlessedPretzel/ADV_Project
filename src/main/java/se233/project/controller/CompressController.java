package se233.project.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class CompressController {

    public static void compressToZip(ArrayList<File> files, String directory) throws FileNotFoundException {
        for (File file : files) {
            FileInputStream fileInputStream = new FileInputStream(file);
        }
    }
}
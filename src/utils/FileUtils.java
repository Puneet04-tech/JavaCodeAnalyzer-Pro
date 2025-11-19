package utils;

import java.io.*;
import java.util.*;

/**
 * File utilities using ONLY java.io
 * NO external libraries
 */
public class FileUtils {

    /**
     * Read all lines from file
     * Uses BufferedReader with try-with-resources (Java 7+)
     */
    public static List<String> readFileLines(File file) throws IOException {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        return lines;
    }

    /**
     * Write lines to file
     */
    public static void writeLines(String filePath, List<String> lines) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * Get all Java files recursively from directory
     */
    public static List<File> getAllJavaFiles(File directory) {
        List<File> javaFiles = new ArrayList<>();

        if (!directory.isDirectory()) {
            return javaFiles;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return javaFiles;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                // Recursion - core Java technique
                javaFiles.addAll(getAllJavaFiles(file));
            } else if (file.getName().endsWith(".java")) {
                javaFiles.add(file);
            }
        }

        return javaFiles;
    }

    /**
     * Get all files recursively from directory (no extension filter)
     */
    public static List<File> getAllFiles(File directory) {
        List<File> filesList = new ArrayList<>();

        if (!directory.isDirectory()) {
            return filesList;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return filesList;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                filesList.addAll(getAllFiles(file));
            } else {
                filesList.add(file);
            }
        }

        return filesList;
    }

    /**
     * Calculate file size in KB
     */
    public static double getFileSizeKB(File file) {
        return file.length() / 1024.0;
    }
}

package ie.ucdconnect.sep.gui;

import ie.ucdconnect.sep.Solution;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/** Class with all the methods needed to save projects, students or solution from a file with a specific extension.
 *  For now it only implements the method for saving a solution.
 *  .txt files are written like .csv files. */
public class FileSaver {

    public static void saveSolutionAsCSV(File file, Solution solution) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(file.toPath());
        String fileContent = solution.toCSV();
        writer.write(fileContent);
        writer.close();
    }

    public static void saveSolutionAsTXT(File file, Solution solution) throws IOException {
        saveSolutionAsCSV(file, solution);
    }
}

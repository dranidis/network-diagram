package com.se.netdiagram;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

public class AppTest {

    /**
     * Smoke test for the main method.
     */
    @Test
    public void testApp() {
        // run the main for all files in examples folder
        // scan the examples folder for all json file and
        // run the main for each file

        String[] args = new String[1];

        try {
            List<String> jsonFiles = Files.walk(Paths.get("examples"))
                    .filter(Files::isRegularFile)
                    .map(path -> path.toString())
                    .filter(name -> name.endsWith(".json"))
                    .collect(Collectors.toList());

            for (String jsonFile : jsonFiles) {
                System.out.println("Processing file: " + jsonFile);

                try {
                    args[0] = jsonFile;
                    App.main(args);
                } catch (Exception e) {
                    System.err.println("While processing file" + args[0] + " an error occurred.");
                    System.err.println(e.getMessage());
                    // e.printStackTrace();
                    fail("Test failed: An exception occurred while processing file: " + args[0] + ": "
                            + e.getMessage());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue("Smoke test passed.", true);
    }

    @Test
    public void testAppOnErrors() {

        String[] args = new String[1];

        try {
            List<String> jsonFiles = Files.walk(Paths.get("example_errors"))
                    .filter(Files::isRegularFile)
                    .map(path -> path.toString())
                    .filter(name -> name.endsWith(".json"))
                    .collect(Collectors.toList());

            for (String jsonFile : jsonFiles) {
                System.out.println("Processing file: " + jsonFile);

                try {
                    args[0] = jsonFile;
                    App.main(args);

                    fail("An exception should have occurred while processing file: " + args[0] + ".");
                } catch (Exception e) {
                    System.err.println("While processing file" + args[0] + " an error occurred.");
                    System.err.println(e.getMessage());
                    // e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue("Smoke test on errors passed.", true);
    }

}

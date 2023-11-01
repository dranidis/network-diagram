package com.se.netdiagram;

import static org.junit.Assert.assertTrue;

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

        args[0] = "examples/tasks_FF.json";
        App.main(args);

        args[0] = "examples/tasks_SF.json";
        App.main(args);

        args[0] = "examples/tasks_SS.json";
        App.main(args);

        args[0] = "examples/full_example.json";
        App.main(args);

        assertTrue("Smoke test passed.", true);
    }
}

package com.se.netdiagram.domain.model.networkdiagram;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.se.netdiagram.domain.model.exceptions.DuplicateTaskKeyException;
import com.se.netdiagram.domain.model.exceptions.KeyNotFoundException;

public class NetworkDiagramTest {
    @Test
    public void when_there_is_a_task_getCriticalPaths_is_not_empty() throws DuplicateTaskKeyException {
        NetworkDiagram networkDiagram = new NetworkDiagram();
        networkDiagram.addTask("A", 1);

        assertFalse(networkDiagram.getCriticalPaths().isEmpty());
    }

    @Test(expected = KeyNotFoundException.class)
    public void when_thePredecessorIsNotFound_then_throws_KeyNotFoundException()
            throws DuplicateTaskKeyException, KeyNotFoundException {
        NetworkDiagram networkDiagram = new NetworkDiagram();
        networkDiagram.addTask("A", 1);
        networkDiagram.addPredecessorToTask("A", "B", "FS", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void when_theDependencyTypeIsNotFound_then_itThrowsIllegalArgumentException()
            throws DuplicateTaskKeyException, KeyNotFoundException {
        NetworkDiagram networkDiagram = new NetworkDiagram();
        networkDiagram.addTask("A", 0);
        networkDiagram.addTask("B", 0);
        networkDiagram.addPredecessorToTask("A", "B", "XX", 0);
    }
}

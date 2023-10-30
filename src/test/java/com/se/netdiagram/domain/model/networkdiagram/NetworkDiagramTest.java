package com.se.netdiagram.domain.model.networkdiagram;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.se.netdiagram.domain.model.exceptions.DuplicateTaskKeyException;

public class NetworkDiagramTest {
    @Test
    public void when_there_is_a_task_getCriticalPaths_is_not_empty() throws DuplicateTaskKeyException {
        NetworkDiagram networkDiagram = new NetworkDiagram();
        networkDiagram.addTask("A", 1);

        assertFalse(networkDiagram.getCriticalPaths().isEmpty());
    }
}

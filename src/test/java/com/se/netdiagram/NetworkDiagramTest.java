package com.se.netdiagram;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

/**
 * Unit test for NetworkDiagram
 */
public class NetworkDiagramTest {
    @Test(expected = DuplicateTaskKeyException.class)
    public void read_Should_throw_DuplicateTaskKeyException_WhenDuplicateKeys()
            throws DuplicateTaskKeyException, KeyNotFoundException, CircularDependencyException {
        List<TaskJSON> tasklist = new ArrayList<>();

        tasklist.add(new TaskJSON("A"));
        tasklist.add(new TaskJSON("A"));

        NetworkDiagram nd = new NetworkDiagram();
        nd.readTasklist(tasklist);
    }

    @Test(expected = KeyNotFoundException.class)
    public void read_Should_throw_KeyNotFoundException_WhenKeyDoesNotExist()
            throws DuplicateTaskKeyException, KeyNotFoundException, CircularDependencyException {

        List<TaskJSON> taskList = new ArrayList<>();
        taskList.add(new TaskJSON("A", 5, Arrays.asList(new String[] {})));
        taskList.add(new TaskJSON("B", 3, Arrays.asList(new String[] {"C"})));
        NetworkDiagram nd = new NetworkDiagram();
        nd.readTasklist(taskList);
    }

    // @Test(expected = CircularDependencyException.class)
    // public void read_Should_throw_CircularDependencyException()
    //         throws DuplicateTaskKeyException, KeyNotFoundException, CircularDependencyException {

    //     List<TaskJSON> taskList = new ArrayList<>();
    //     taskList.add(new TaskJSON("A", 5, Arrays.asList(new String[] {})));
    //     taskList.add(new TaskJSON("B", 3, Arrays.asList(new String[] {"A"})));
    //     NetworkDiagram nd = new NetworkDiagram();
    //     nd.readTasklist(taskList);
    // }    
    @Test
    public void read_Should_Finish_WhenThereAreNoProblems() throws DuplicateTaskKeyException, KeyNotFoundException,
            CircularDependencyException {

        List<TaskJSON> taskList = new ArrayList<>();
        taskList.add(new TaskJSON("A", 5, Arrays.asList(new String[] {})));
        taskList.add(new TaskJSON("B", 3, Arrays.asList(new String[] {"A"})));

        NetworkDiagram nd = new NetworkDiagram();
        nd.readTasklist(taskList);

        assertNotNull(nd.getTask("A")); 
        assertNotNull(nd.getTask("B")); 
    }

    @Test
    public void earliestStart_Should_Be_Zero_When_ThereAreNoPredecessors()
            throws DuplicateTaskKeyException, KeyNotFoundException, CircularDependencyException {
        List<TaskJSON> taskList = new ArrayList<>();
        taskList.add(new TaskJSON("A", 5, Arrays.asList(new String[] {})));

        NetworkDiagram nd = new NetworkDiagram();
        nd.readTasklist(taskList);

        nd.process();

        assertEquals(0, nd.getTask("A").earliestStart.getAsLong());
        assertEquals(5, nd.getTask("A").earliestFinish.getAsLong());
    }

    @Test
    public void earliestStart_Should_Be_EarliestFinishOfPred_When_ThereisOnePredecessor()
            throws DuplicateTaskKeyException, KeyNotFoundException, CircularDependencyException {
        List<TaskJSON> taskList = new ArrayList<>();
        taskList.add(new TaskJSON("A", 5, Arrays.asList(new String[] {})));
        taskList.add(new TaskJSON("B", 3, Arrays.asList(new String[] {"A"})));

        NetworkDiagram nd = new NetworkDiagram();
        nd.readTasklist(taskList);

        nd.process();

        assertEquals(5, nd.getTask("B").earliestStart.getAsLong());
        assertEquals(8, nd.getTask("B").earliestFinish.getAsLong());
    }

    @Test
    public void earliestStart_Should_Be_MaxOfEarliestFinishOfPreds_When_ThereAreMorePredecessors()
            throws DuplicateTaskKeyException, KeyNotFoundException, CircularDependencyException {

        List<TaskJSON> taskList = new ArrayList<>();
        taskList.add(new TaskJSON("A", 5, Arrays.asList(new String[] {})));
        taskList.add(new TaskJSON("B", 3, Arrays.asList(new String[] {})));
        taskList.add(new TaskJSON("C", 2, Arrays.asList(new String[] { "A", "B" })));

        NetworkDiagram nd = new NetworkDiagram();
        nd.readTasklist(taskList);

        nd.process();

        assertEquals(5, nd.getTask("C").earliestStart.getAsLong());
        assertEquals(7, nd.getTask("C").earliestFinish.getAsLong());
    }

    @Test
    public void successors_Should_Corectly_Link_tasks() throws DuplicateTaskKeyException, KeyNotFoundException,
            CircularDependencyException {
        List<TaskJSON> taskList = new ArrayList<>();
        taskList.add(new TaskJSON("A", 5, Arrays.asList(new String[] {})));

        NetworkDiagram nd = new NetworkDiagram();
        nd.readTasklist(taskList);

        nd.process();

        assertEquals(0, nd.getTask("A").succ.size());
    }

    @Test
    public void successors_Should_Corectly_Link_tasksBackWards()
            throws DuplicateTaskKeyException, KeyNotFoundException, CircularDependencyException {
        List<TaskJSON> taskList = new ArrayList<>();
        taskList.add(new TaskJSON("A", 5, Arrays.asList(new String[] {})));
        taskList.add(new TaskJSON("B", 5, Arrays.asList(new String[] { "A" })));
        taskList.add(new TaskJSON("C", 5, Arrays.asList(new String[] { "A", "B" })));

        NetworkDiagram nd = new NetworkDiagram();
        nd.readTasklist(taskList);

        nd.process();

        assertTrue(nd.getTask("A").succ.stream().map(s -> s.id).collect(Collectors.toList())
                .containsAll(Arrays.asList(new String[] { "C", "B" })));

        assertTrue(nd.getTask("B").succ.stream().map(s -> s.id).collect(Collectors.toList())
                .containsAll(Arrays.asList(new String[] { "C" })));

        assertEquals(0, nd.getTask("C").succ.size());
    }

    @Test
    public void latestFinish_Should_Be_earliestFinish_When_ThereAreNoSuccessors()
            throws DuplicateTaskKeyException, KeyNotFoundException, CircularDependencyException {
        List<TaskJSON> taskList = new ArrayList<>();
        taskList.add(new TaskJSON("A", 5, Arrays.asList(new String[] {})));

        NetworkDiagram nd = new NetworkDiagram();
        nd.readTasklist(taskList);
        nd.process();

        assertEquals(5, nd.getTask("A").latestFinish.getAsLong());
        assertEquals(0, nd.getTask("A").latestStart.getAsLong());
        assertEquals(0, nd.getTask("A").slack.getAsLong());
    }

    @Test
    public void latestFinish_Should_Be_min_of_latestStart_When_ThereAreSuccessors()
            throws DuplicateTaskKeyException, KeyNotFoundException, CircularDependencyException {
        List<TaskJSON> taskList = new ArrayList<>();
        taskList.add(new TaskJSON("A", 5, Arrays.asList(new String[] {})));
        taskList.add(new TaskJSON("B", 3, Arrays.asList(new String[] { "A" })));
        taskList.add(new TaskJSON("C", 2, Arrays.asList(new String[] { "A" })));

        NetworkDiagram nd = new NetworkDiagram();
        nd.readTasklist(taskList);
        nd.process();

        assertEquals(8, nd.getTask("C").latestFinish.getAsLong());
        assertEquals(6, nd.getTask("C").latestStart.getAsLong());
        assertEquals(1, nd.getTask("C").slack.getAsLong());

        assertEquals(8, nd.getTask("B").latestFinish.getAsLong());
        assertEquals(5, nd.getTask("B").latestStart.getAsLong());
        assertEquals(0, nd.getTask("B").slack.getAsLong());

        assertEquals(5, nd.getTask("A").latestFinish.getAsLong());
        assertEquals(0, nd.getTask("A").latestStart.getAsLong());
        assertEquals(0, nd.getTask("A").slack.getAsLong());
    }

    @Test
    public void process_Should_WorkWithTransitiveDependencies() throws DuplicateTaskKeyException, KeyNotFoundException,
            CircularDependencyException {
        List<TaskJSON> taskList = new ArrayList<>();
        taskList.add(new TaskJSON("A", 2, Arrays.asList(new String[] {})));
        taskList.add(new TaskJSON("B", 3, Arrays.asList(new String[] { "A" })));
        taskList.add(new TaskJSON("C", 4, Arrays.asList(new String[] { "B", "A" })));

        NetworkDiagram nd = new NetworkDiagram();
        nd.readTasklist(taskList);
        nd.process();

        assertEquals(0, nd.getTask("A").earliestStart.getAsLong());
        assertEquals(2, nd.getTask("B").earliestStart.getAsLong());
        assertEquals(5, nd.getTask("C").earliestStart.getAsLong());

        assertEquals(2, nd.getTask("A").latestFinish.getAsLong());
        assertEquals(5, nd.getTask("B").latestFinish.getAsLong());
        assertEquals(9, nd.getTask("C").latestFinish.getAsLong());
    }

    @Test
    public void criticalPath_Should_Be_Task_When_OnlyOne() throws DuplicateTaskKeyException, KeyNotFoundException,
            CircularDependencyException {
        List<TaskJSON> taskList = new ArrayList<>();
        taskList.add(new TaskJSON("A", 2, Arrays.asList(new String[] {})));

        NetworkDiagram nd = new NetworkDiagram();
        nd.readTasklist(taskList);
        nd.process();

        List<List<Task>> paths = nd.getCriticalPaths();

        assertEquals(1, paths.size());
        assertEquals(1, paths.get(0).size());
        assertEquals("A", paths.get(0).get(0).id);
    }

    @Test
    public void criticalPath_Should_Be_OnlyTasksWithSlackZero() throws DuplicateTaskKeyException, KeyNotFoundException,
            CircularDependencyException {

        List<TaskJSON> taskList = new ArrayList<>();
        taskList.add(new TaskJSON("A", 2, Arrays.asList(new String[] {})));
        taskList.add(new TaskJSON("B", 3, Arrays.asList(new String[] { "A" })));
        taskList.add(new TaskJSON("C", 2, Arrays.asList(new String[] { "A" })));

        NetworkDiagram nd = new NetworkDiagram();
        nd.readTasklist(taskList);
        nd.process();

        List<List<Task>> paths = nd.getCriticalPaths();

        assertEquals(1, paths.size());
        assertEquals(2, paths.get(0).size());
        assertEquals("A", paths.get(0).get(0).id);
        assertEquals("B", paths.get(0).get(1).id);
    }

    @Test
    public void criticalPath_Should_ReturnMoreThanOneCPath() throws DuplicateTaskKeyException, KeyNotFoundException,
            CircularDependencyException {
        List<TaskJSON> taskList = new ArrayList<>();
        taskList.add(new TaskJSON("A", 0, Arrays.asList(new String[] {})));
        taskList.add(new TaskJSON("B", 0, Arrays.asList(new String[] { "A" })));
        taskList.add(new TaskJSON("C", 0, Arrays.asList(new String[] { "A" })));

        NetworkDiagram nd = new NetworkDiagram();
        nd.readTasklist(taskList);
        nd.process();

        List<List<Task>> paths = nd.getCriticalPaths();

        assertEquals(2, paths.size());
        assertEquals(2, paths.get(0).size());
        assertEquals(2, paths.get(1).size());

        assertEquals("A", paths.get(0).get(0).id);
        assertEquals("B", paths.get(0).get(1).id);

        assertEquals("A", paths.get(1).get(0).id);
        assertEquals("C", paths.get(1).get(1).id);
    }

    @Test
    public void criticalPath_Should_WorkWithTransitiveDependencies()
            throws DuplicateTaskKeyException, KeyNotFoundException, CircularDependencyException {
        List<TaskJSON> taskList = new ArrayList<>();
        taskList.add(new TaskJSON("A", 0, Arrays.asList(new String[] {})));
        taskList.add(new TaskJSON("B", 0, Arrays.asList(new String[] { "A" })));
        taskList.add(new TaskJSON("C", 0, Arrays.asList(new String[] { "A", "B" })));

        NetworkDiagram nd = new NetworkDiagram();
        nd.readTasklist(taskList);
        nd.process();

        List<List<Task>> paths = nd.getCriticalPaths();

        assertEquals(1, paths.size());
        assertEquals(3, paths.get(0).size());

        assertEquals("A", paths.get(0).get(0).id);
        assertEquals("B", paths.get(0).get(1).id);
        assertEquals("C", paths.get(0).get(2).id);
    }

    @Test
    public void criticalPath_Should_WorkWithTransitiveDependenciesMoreThanOnePath()
            throws DuplicateTaskKeyException, KeyNotFoundException, CircularDependencyException {
        List<TaskJSON> taskList = new ArrayList<>();
        taskList.add(new TaskJSON("A", 0, Arrays.asList(new String[] {})));
        taskList.add(new TaskJSON("B", 0, Arrays.asList(new String[] {})));
        taskList.add(new TaskJSON("C", 0, Arrays.asList(new String[] { "A", "B" })));
        taskList.add(new TaskJSON("D", 0, Arrays.asList(new String[] { "A", "B" })));
        taskList.add(new TaskJSON("E", 0, Arrays.asList(new String[] { "A", "B", "C", "D" })));
        taskList.add(new TaskJSON("F", 0, Arrays.asList(new String[] { "A", "B", "C", "D" })));

        NetworkDiagram nd = new NetworkDiagram();
        nd.readTasklist(taskList);
        nd.process();

        List<List<Task>> paths = nd.getCriticalPaths();

        assertEquals(8, paths.size());
        for (int i = 0; i < 8; i++) {
            assertEquals(3, paths.get(i).size());
            assertTrue(Arrays.asList(new String[] { "A", "B" }).contains(paths.get(i).get(0).id));
            assertTrue(Arrays.asList(new String[] { "C", "D" }).contains(paths.get(i).get(1).id));
            assertTrue(Arrays.asList(new String[] { "E", "F" }).contains(paths.get(i).get(2).id));
        }

    }
}

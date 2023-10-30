package com.se.netdiagram;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.se.netdiagram.application.JSONReaderService;
import com.se.netdiagram.application.TaskData;
import com.se.netdiagram.domain.model.NetworkDiagram;
import com.se.netdiagram.domain.model.Path;
import com.se.netdiagram.domain.model.exceptions.DuplicateTaskKeyException;
import com.se.netdiagram.domain.model.exceptions.KeyNotFoundException;

/**
 * Unit test for NetworkDiagram
 */
public class NetworkDiagramTest {

    @Test(expected = DuplicateTaskKeyException.class)
    public void read_Should_throw_DuplicateTaskKeyException_WhenDuplicateKeys()
            throws DuplicateTaskKeyException, KeyNotFoundException {
        List<TaskData> tasklist = new ArrayList<>();

        tasklist.add(new TaskData("A", 5, Arrays.asList(new String[] {})));
        tasklist.add(new TaskData("A", 5, Arrays.asList(new String[] {})));

        NetworkDiagram nd = new NetworkDiagram();
        JSONReaderService.processTaskList(nd, tasklist);
    }

    @Test(expected = KeyNotFoundException.class)
    public void read_Should_throw_KeyNotFoundException_WhenKeyDoesNotExist()
            throws DuplicateTaskKeyException, KeyNotFoundException {

        List<TaskData> taskList = new ArrayList<>();
        taskList.add(new TaskData("A", 5, Arrays.asList(new String[] {})));
        taskList.add(new TaskData("B", 3, Arrays.asList(new String[] { "C" })));
        NetworkDiagram nd = new NetworkDiagram();
        JSONReaderService.processTaskList(nd, taskList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void read_Should_throw_CircularDependencyException() throws DuplicateTaskKeyException, KeyNotFoundException {

        List<TaskData> taskList = new ArrayList<>();
        taskList.add(new TaskData("A", 5, Arrays.asList(new String[] { "B" })));
        taskList.add(new TaskData("B", 3, Arrays.asList(new String[] { "A" })));
        NetworkDiagram nd = new NetworkDiagram();
        JSONReaderService.processTaskList(nd, taskList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void read_Should_throw_CircularDependencyException_WhenThereIsATransitiveCircularDependency()
            throws DuplicateTaskKeyException, KeyNotFoundException {

        List<TaskData> taskList = new ArrayList<>();
        taskList.add(new TaskData("A", 5, Arrays.asList(new String[] { "C" })));
        taskList.add(new TaskData("B", 3, Arrays.asList(new String[] { "A" })));
        taskList.add(new TaskData("C", 3, Arrays.asList(new String[] { "B" })));
        NetworkDiagram nd = new NetworkDiagram();
        JSONReaderService.processTaskList(nd, taskList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void read_Should_throw_CircularDependencyException_WhenThereIsATransitiveCircularDependency1()
            throws DuplicateTaskKeyException, KeyNotFoundException {

        List<TaskData> taskList = new ArrayList<>();
        taskList.add(new TaskData("A", 5, Arrays.asList(new String[] {})));
        taskList.add(new TaskData("B", 3, Arrays.asList(new String[] { "A", "C" })));
        taskList.add(new TaskData("C", 3, Arrays.asList(new String[] { "B" })));
        NetworkDiagram nd = new NetworkDiagram();
        JSONReaderService.processTaskList(nd, taskList);
    }

    @Test
    public void read_Should_Finish_WhenThereAreNoProblems() throws DuplicateTaskKeyException, KeyNotFoundException {

        List<TaskData> taskList = new ArrayList<>();
        taskList.add(new TaskData("A", 5, Arrays.asList(new String[] {})));
        taskList.add(new TaskData("B", 3, Arrays.asList(new String[] { "A" })));

        NetworkDiagram nd = new NetworkDiagram();
        JSONReaderService.processTaskList(nd, taskList);

        assertNotNull(nd.getTask("A"));
        assertNotNull(nd.getTask("B"));
    }

    @Test
    public void earliestStart_Should_Be_Zero_When_ThereAreNoPredecessors()
            throws DuplicateTaskKeyException, KeyNotFoundException {
        List<TaskData> taskList = new ArrayList<>();
        taskList.add(new TaskData("A", 5, Arrays.asList(new String[] {})));

        NetworkDiagram nd = new NetworkDiagram();
        JSONReaderService.processTaskList(nd, taskList);

        nd.forwardAndBackWard();

        assertEquals(0, nd.getTask("A").earliestStart().getAsLong());
        assertEquals(5, nd.getTask("A").earliestFinish().getAsLong());
    }

    @Test
    public void earliestStart_Should_Be_EarliestFinishOfPred_When_ThereisOnePredecessor()
            throws DuplicateTaskKeyException, KeyNotFoundException {
        List<TaskData> taskList = new ArrayList<>();
        taskList.add(new TaskData("A", 5, Arrays.asList(new String[] {})));
        taskList.add(new TaskData("B", 3, Arrays.asList(new String[] { "A" })));

        NetworkDiagram nd = new NetworkDiagram();
        JSONReaderService.processTaskList(nd, taskList);

        nd.forwardAndBackWard();

        assertEquals(5, nd.getTask("B").earliestStart().getAsLong());
        assertEquals(8, nd.getTask("B").earliestFinish().getAsLong());
    }

    @Test
    public void earliestStart_Should_Be_MaxOfEarliestFinishOfPreds_When_ThereAreMorePredecessors()
            throws DuplicateTaskKeyException, KeyNotFoundException {

        List<TaskData> taskList = new ArrayList<>();
        taskList.add(new TaskData("A", 5, Arrays.asList(new String[] {})));
        taskList.add(new TaskData("B", 3, Arrays.asList(new String[] {})));
        taskList.add(new TaskData("C", 2, Arrays.asList(new String[] { "A", "B" })));

        NetworkDiagram nd = new NetworkDiagram();
        JSONReaderService.processTaskList(nd, taskList);

        nd.forwardAndBackWard();

        assertEquals(5, nd.getTask("C").earliestStart().getAsLong());
        assertEquals(7, nd.getTask("C").earliestFinish().getAsLong());
    }

    @Test
    public void successors_Should_Corectly_Link_tasks() throws DuplicateTaskKeyException, KeyNotFoundException {
        List<TaskData> taskList = new ArrayList<>();
        taskList.add(new TaskData("A", 5, Arrays.asList(new String[] {})));

        NetworkDiagram nd = new NetworkDiagram();
        JSONReaderService.processTaskList(nd, taskList);

        nd.forwardAndBackWard();

        assertEquals(0, nd.getTask("A").successors().size());
    }

    @Test
    public void successors_Should_Corectly_Link_tasksBackWards()
            throws DuplicateTaskKeyException, KeyNotFoundException {
        List<TaskData> taskList = new ArrayList<>();
        taskList.add(new TaskData("A", 5, Arrays.asList(new String[] {})));
        taskList.add(new TaskData("B", 5, Arrays.asList(new String[] { "A" })));
        taskList.add(new TaskData("C", 5, Arrays.asList(new String[] { "A", "B" })));

        NetworkDiagram nd = new NetworkDiagram();
        JSONReaderService.processTaskList(nd, taskList);

        nd.forwardAndBackWard();

        assertTrue(nd.getTask("A").successors().stream().map(s -> s.idAsString()).collect(Collectors.toList())
                .containsAll(Arrays.asList(new String[] { "C", "B" })));

        assertTrue(nd.getTask("B").successors().stream().map(s -> s.idAsString()).collect(Collectors.toList())
                .containsAll(Arrays.asList(new String[] { "C" })));

        assertEquals(0, nd.getTask("C").successors().size());
    }

    @Test
    public void latestFinish_Should_Be_earliestFinish_When_ThereAreNoSuccessors()
            throws DuplicateTaskKeyException, KeyNotFoundException {
        List<TaskData> taskList = new ArrayList<>();
        taskList.add(new TaskData("A", 5, Arrays.asList(new String[] {})));

        NetworkDiagram nd = new NetworkDiagram();
        JSONReaderService.processTaskList(nd, taskList);
        nd.forwardAndBackWard();

        assertEquals(5, nd.getTask("A").latestFinish().getAsLong());
        assertEquals(0, nd.getTask("A").latestStart().getAsLong());
        assertEquals(0, nd.getTask("A").slack().getAsLong());
    }

    @Test
    public void latestFinish_Should_Be_min_of_latestStart_When_ThereAreSuccessors()
            throws DuplicateTaskKeyException, KeyNotFoundException {
        List<TaskData> taskList = new ArrayList<>();
        taskList.add(new TaskData("A", 5, Arrays.asList(new String[] {})));
        taskList.add(new TaskData("B", 3, Arrays.asList(new String[] { "A" })));
        taskList.add(new TaskData("C", 2, Arrays.asList(new String[] { "A" })));

        NetworkDiagram nd = new NetworkDiagram();
        JSONReaderService.processTaskList(nd, taskList);
        nd.forwardAndBackWard();

        assertEquals(8, nd.getTask("C").latestFinish().getAsLong());
        assertEquals(6, nd.getTask("C").latestStart().getAsLong());
        assertEquals(1, nd.getTask("C").slack().getAsLong());

        assertEquals(8, nd.getTask("B").latestFinish().getAsLong());
        assertEquals(5, nd.getTask("B").latestStart().getAsLong());
        assertEquals(0, nd.getTask("B").slack().getAsLong());

        assertEquals(5, nd.getTask("A").latestFinish().getAsLong());
        assertEquals(0, nd.getTask("A").latestStart().getAsLong());
        assertEquals(0, nd.getTask("A").slack().getAsLong());
    }

    @Test
    public void process_Should_WorkWithTransitiveDependencies() throws DuplicateTaskKeyException, KeyNotFoundException {
        List<TaskData> taskList = new ArrayList<>();
        taskList.add(new TaskData("A", 2, Arrays.asList(new String[] {})));
        taskList.add(new TaskData("B", 3, Arrays.asList(new String[] { "A" })));
        taskList.add(new TaskData("C", 4, Arrays.asList(new String[] { "B", "A" })));

        NetworkDiagram nd = new NetworkDiagram();
        JSONReaderService.processTaskList(nd, taskList);
        nd.forwardAndBackWard();

        assertEquals(0, nd.getTask("A").earliestStart().getAsLong());
        assertEquals(2, nd.getTask("B").earliestStart().getAsLong());
        assertEquals(5, nd.getTask("C").earliestStart().getAsLong());

        assertEquals(2, nd.getTask("A").latestFinish().getAsLong());
        assertEquals(5, nd.getTask("B").latestFinish().getAsLong());
        assertEquals(9, nd.getTask("C").latestFinish().getAsLong());
    }

    @Test
    public void criticalPath_Should_Be_Task_When_OnlyOne() throws DuplicateTaskKeyException, KeyNotFoundException {
        List<TaskData> taskList = new ArrayList<>();
        taskList.add(new TaskData("A", 2, Arrays.asList(new String[] {})));

        NetworkDiagram nd = new NetworkDiagram();
        JSONReaderService.processTaskList(nd, taskList);
        nd.forwardAndBackWard();

        List<Path> paths = nd.getCriticalPaths();

        assertEquals(1, paths.size());
        assertEquals(1, paths.get(0).size());
        assertEquals("A", paths.get(0).get(0).idAsString());
    }

    @Test
    public void criticalPath_Should_Be_OnlyTasksWithSlackZero() throws DuplicateTaskKeyException, KeyNotFoundException {

        List<TaskData> taskList = new ArrayList<>();
        taskList.add(new TaskData("A", 2, Arrays.asList(new String[] {})));
        taskList.add(new TaskData("B", 3, Arrays.asList(new String[] { "A" })));
        taskList.add(new TaskData("C", 2, Arrays.asList(new String[] { "A" })));

        NetworkDiagram nd = new NetworkDiagram();
        JSONReaderService.processTaskList(nd, taskList);
        nd.forwardAndBackWard();

        List<Path> paths = nd.getCriticalPaths();

        assertEquals(1, paths.size());
        assertEquals(2, paths.get(0).size());
        assertEquals("A", paths.get(0).get(0).idAsString());
        assertEquals("B", paths.get(0).get(1).idAsString());
    }

    @Test
    public void criticalPath_Should_ReturnMoreThanOneCPath() throws DuplicateTaskKeyException, KeyNotFoundException {
        List<TaskData> taskList = new ArrayList<>();
        taskList.add(new TaskData("A", 0, Arrays.asList(new String[] {})));
        taskList.add(new TaskData("B", 0, Arrays.asList(new String[] { "A" })));
        taskList.add(new TaskData("C", 0, Arrays.asList(new String[] { "A" })));

        NetworkDiagram nd = new NetworkDiagram();
        JSONReaderService.processTaskList(nd, taskList);
        nd.forwardAndBackWard();

        List<Path> paths = nd.getCriticalPaths();

        assertEquals(2, paths.size());
        assertEquals(2, paths.get(0).size());
        assertEquals(2, paths.get(1).size());

        assertEquals("A", paths.get(0).get(0).idAsString());
        assertEquals("B", paths.get(0).get(1).idAsString());

        assertEquals("A", paths.get(1).get(0).idAsString());
        assertEquals("C", paths.get(1).get(1).idAsString());
    }

    @Test
    public void criticalPath_Should_WorkWithTransitiveDependencies()
            throws DuplicateTaskKeyException, KeyNotFoundException {
        List<TaskData> taskList = new ArrayList<>();
        taskList.add(new TaskData("A", 0, Arrays.asList(new String[] {})));
        taskList.add(new TaskData("B", 0, Arrays.asList(new String[] { "A" })));
        taskList.add(new TaskData("C", 0, Arrays.asList(new String[] { "A", "B" })));

        NetworkDiagram nd = new NetworkDiagram();
        JSONReaderService.processTaskList(nd, taskList);
        nd.forwardAndBackWard();

        List<Path> paths = nd.getCriticalPaths();

        assertEquals(1, paths.size());
        assertEquals(3, paths.get(0).size());

        assertEquals("A", paths.get(0).get(0).idAsString());
        assertEquals("B", paths.get(0).get(1).idAsString());
        assertEquals("C", paths.get(0).get(2).idAsString());
    }

    @Test
    public void criticalPath_Should_WorkWithTransitiveDependenciesMoreThanOnePath()
            throws DuplicateTaskKeyException, KeyNotFoundException {
        List<TaskData> taskList = new ArrayList<>();
        taskList.add(new TaskData("A", 0, Arrays.asList(new String[] {})));
        taskList.add(new TaskData("B", 0, Arrays.asList(new String[] {})));
        taskList.add(new TaskData("C", 0, Arrays.asList(new String[] { "A", "B" })));
        taskList.add(new TaskData("D", 0, Arrays.asList(new String[] { "A", "B" })));
        taskList.add(new TaskData("E", 0, Arrays.asList(new String[] { "A", "B", "C", "D" })));
        taskList.add(new TaskData("F", 0, Arrays.asList(new String[] { "A", "B", "C", "D" })));

        NetworkDiagram nd = new NetworkDiagram();
        JSONReaderService.processTaskList(nd, taskList);
        nd.forwardAndBackWard();

        List<Path> paths = nd.getCriticalPaths();

        assertEquals(8, paths.size());
        for (int i = 0; i < 8; i++) {
            assertEquals(3, paths.get(i).size());
            assertTrue(Arrays.asList(new String[] { "A", "B" }).contains(paths.get(i).get(0).idAsString()));
            assertTrue(Arrays.asList(new String[] { "C", "D" }).contains(paths.get(i).get(1).idAsString()));
            assertTrue(Arrays.asList(new String[] { "E", "F" }).contains(paths.get(i).get(2).idAsString()));
        }

    }
}

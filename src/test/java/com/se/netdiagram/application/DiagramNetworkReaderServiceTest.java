package com.se.netdiagram.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.se.netdiagram.domain.model.exceptions.DuplicateTaskKeyException;
import com.se.netdiagram.domain.model.exceptions.KeyNotFoundException;
import com.se.netdiagram.domain.model.networkdiagram.NetworkDiagram;
import com.se.netdiagram.domain.model.networkdiagram.Path;
import com.se.netdiagram.domain.model.utilities.Query;
import com.se.netdiagram.port.adapter.ConsoleNetworkDiagramPrinter;

import static com.se.netdiagram.application.TaskData.task;
import static com.se.netdiagram.application.TaskDataList.taskList;

public class DiagramNetworkReaderServiceTest {

    @Test(expected = DuplicateTaskKeyException.class)
    public void read_Should_throw_DuplicateTaskKeyException_WhenDuplicateKeys()
            throws DuplicateTaskKeyException, KeyNotFoundException {

        TaskDataList taskList = taskList()
                .add(task("A", 5))
                .add(task("A", 3));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);
    }

    @Test(expected = KeyNotFoundException.class)
    public void read_Should_throw_KeyNotFoundException_WhenKeyDoesNotExist()
            throws DuplicateTaskKeyException, KeyNotFoundException {

        TaskDataList taskList = taskList()
                .add(task("A", 5))
                .add(task("B", 3).withPred("C"));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void read_Should_throw_CircularDependencyException()
            throws DuplicateTaskKeyException, KeyNotFoundException {

        TaskDataList taskList = taskList()
                .add(task("A", 5).withPred("B"))
                .add(task("B", 3).withPred("A"));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void read_Should_throw_CircularDependencyException_WhenThereIsATransitiveCircularDependency()
            throws DuplicateTaskKeyException, KeyNotFoundException {

        TaskDataList taskList = taskList()
                .add(task("A", 5).withPred("C"))
                .add(task("B", 3).withPred("A"))
                .add(task("C", 3).withPred("B"));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void read_Should_throw_CircularDependencyException_WhenThereIsATransitiveCircularDependency1()
            throws DuplicateTaskKeyException, KeyNotFoundException {

        TaskDataList taskList = taskList()
                .add(task("A", 5))
                .add(task("B", 3).withPred("A").withPred("C"))
                .add(task("C", 3).withPred("B"));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);
    }

    @Test
    public void read_Should_Finish_WhenThereAreNoProblems() throws DuplicateTaskKeyException, KeyNotFoundException {

        TaskDataList taskList = taskList()
                .add(task("A", 5))
                .add(task("B", 3).withPred("A"));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);

        assertNotNull(nd.getTask("A"));
        assertNotNull(nd.getTask("B"));
    }

    @Test
    public void earliestStart_Should_Be_Zero_When_ThereAreNoPredecessors()
            throws DuplicateTaskKeyException, KeyNotFoundException {
        TaskDataList taskList = taskList()
                .add(task("A", 5));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);

        assertEquals(0, nd.getTask("A").earliestStart());
        assertEquals(5, nd.getTask("A").earliestFinish());
    }

    @Test
    public void earliestStart_Should_Be_EarliestFinishOfPred_When_ThereIsOnePredecessor()
            throws DuplicateTaskKeyException, KeyNotFoundException {
        TaskDataList taskList = taskList()
                .add(task("A", 5))
                .add(task("B", 3).withPred("A"));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);

        assertEquals(5, nd.getTask("B").earliestStart());
        assertEquals(8, nd.getTask("B").earliestFinish());
    }

    @Test
    public void earliestStart_Should_Be_MaxOfEarliestFinishOfPredecessors_When_ThereAreMorePredecessors()
            throws DuplicateTaskKeyException, KeyNotFoundException {

        TaskDataList taskList = taskList()
                .add(task("A", 5))
                .add(task("B", 3))
                .add(task("C", 2).withPred("A").withPred("B"));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);

        assertEquals(5, nd.getTask("C").earliestStart());
        assertEquals(7, nd.getTask("C").earliestFinish());
    }

    @Test
    public void earliestStart_Should_Be_Same_With_EarliestStart_of_SS_pred()
            throws DuplicateTaskKeyException, KeyNotFoundException {

        TaskDataList taskList = taskList()
                .add(task("A", 1))
                .add(task("B", 2).withPred("A"))
                .add(task("C", 3).withPred("B", "SS"));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);

        assertEquals(1, nd.getTask("B").earliestStart());
        assertEquals(1, nd.getTask("C").earliestStart());
    }

    @Test
    public void latestFinish_with_SS_pred() throws DuplicateTaskKeyException, KeyNotFoundException {

        TaskDataList taskList = taskList()
                .add(task("A", 1))
                .add(task("B", 2).withPred("A"))
                .add(task("C", 3).withPred("B", "SS"));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);

        assertEquals(3, nd.getTask("B").latestFinish());
        assertEquals(4, nd.getTask("C").latestFinish());
    }

    @Test
    public void latestFinish_with_SS_pred2() throws DuplicateTaskKeyException, KeyNotFoundException {

        TaskDataList taskList = taskList()
                .add(task("A", 1))
                .add(task("B", 3).withPred("A"))
                .add(task("C", 2).withPred("B", "SS"));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);

        assertEquals(4, nd.getTask("B").latestFinish());
        assertEquals(4, nd.getTask("C").latestFinish());
    }

    @Test
    public void earliestFinish_Should_Be_Same_earliestFinish_of_FF_pred()
            throws DuplicateTaskKeyException, KeyNotFoundException {

        TaskDataList taskList = taskList()
                .add(task("A", 1))
                .add(task("B", 3).withPred("A"))
                .add(task("C", 2).withPred("B", "FF"));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);

        assertEquals(4, nd.getTask("B").earliestFinish());
        assertEquals(4, nd.getTask("C").earliestFinish());
    }

    @Test
    public void latestStart_with_FF_pred() throws DuplicateTaskKeyException, KeyNotFoundException {

        TaskDataList taskList = taskList()
                .add(task("A", 1))
                .add(task("B", 1).withPred("A"))
                .add(task("C", 3).withPred("B", "FF"));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);

        assertEquals(1, nd.getTask("B").earliestStart());
        assertEquals(2, nd.getTask("B").latestStart());
        assertEquals(2, nd.getTask("B").earliestFinish());
        assertEquals(3, nd.getTask("B").latestFinish());

    }

    @Test
    public void FF_pred() throws DuplicateTaskKeyException, KeyNotFoundException {

        TaskDataList taskList = taskList()
                .add(task("B", 2))
                .add(task("C", 3).withPred("B", "FF"));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);

        assertEquals(0, nd.getTask("B").earliestStart());
        assertEquals(2, nd.getTask("B").earliestFinish());
        assertEquals(1, nd.getTask("B").latestStart());
        assertEquals(3, nd.getTask("B").latestFinish());

        assertEquals(0, nd.getTask("C").earliestStart());
        assertEquals(3, nd.getTask("C").earliestFinish());
        assertEquals(0, nd.getTask("C").latestStart());
        assertEquals(3, nd.getTask("C").latestFinish());
    }

    @Test
    public void FF_pred_2() throws DuplicateTaskKeyException, KeyNotFoundException {

        TaskDataList taskList = taskList()
                .add(task("B", 3))
                .add(task("C", 2).withPred("B", "FF"));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);

        assertEquals(0, nd.getTask("B").earliestStart());
        assertEquals(3, nd.getTask("B").earliestFinish());
        assertEquals(0, nd.getTask("B").latestStart());
        assertEquals(3, nd.getTask("B").latestFinish());

        assertEquals(1, nd.getTask("C").earliestStart());
        assertEquals(3, nd.getTask("C").earliestFinish());
        assertEquals(1, nd.getTask("C").latestStart());
        assertEquals(3, nd.getTask("C").latestFinish());
    }

    @Test
    public void SF_pred() throws DuplicateTaskKeyException, KeyNotFoundException {

        TaskDataList taskList = taskList()
                .add(task("A", 3))
                .add(task("B", 4).withPred("A"))
                .add(task("C", 2).withPred("B", "SF"));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);

        assertEquals(1, nd.getTask("C").earliestStart());
        assertEquals(3, nd.getTask("C").earliestFinish());
        assertEquals(5, nd.getTask("C").latestStart());
        assertEquals(7, nd.getTask("C").latestFinish());
    }

    @Test
    public void successors_Should_Correctly_Link_tasks() throws DuplicateTaskKeyException, KeyNotFoundException {
        TaskDataList taskList = taskList()
                .add(task("A", 5));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);

        assertEquals(0, nd.getTask("A").successors().size());
    }

    @Test
    public void successors_Should_Correctly_Link_tasksBackWards()
            throws DuplicateTaskKeyException, KeyNotFoundException {
        TaskDataList taskList = taskList()
                .add(task("A", 5))
                .add(task("B", 5).withPred("A"))
                .add(task("C", 5).withPred("A").withPred("B"));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);

        assertTrue(nd.getTask("A").successors().stream().map(s -> s.task().idAsString())
                .collect(Collectors.toList())
                .containsAll(Arrays.asList(new String[] { "C", "B" })));

        assertTrue(nd.getTask("B").successors().stream().map(s -> s.task().idAsString())
                .collect(Collectors.toList())
                .containsAll(Arrays.asList(new String[] { "C" })));

        assertEquals(0, nd.getTask("C").successors().size());
    }

    @Test
    public void latestFinish_Should_Be_earliestFinish_When_ThereAreNoSuccessors()
            throws DuplicateTaskKeyException, KeyNotFoundException {
        TaskDataList taskList = taskList()
                .add(task("A", 5));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);

        assertEquals(5, nd.getTask("A").latestFinish());
        assertEquals(0, nd.getTask("A").latestStart());
        assertEquals(0, nd.getTask("A").slack());
    }

    @Test
    public void latestFinish_Should_Be_min_of_latestStart_When_ThereAreSuccessors()
            throws DuplicateTaskKeyException, KeyNotFoundException {
        TaskDataList taskList = taskList()
                .add(task("A", 5))
                .add(task("B", 3).withPred("A"))
                .add(task("C", 2).withPred("A"));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);

        assertEquals(8, nd.getTask("C").latestFinish());
        assertEquals(6, nd.getTask("C").latestStart());
        assertEquals(1, nd.getTask("C").slack());

        assertEquals(8, nd.getTask("B").latestFinish());
        assertEquals(5, nd.getTask("B").latestStart());
        assertEquals(0, nd.getTask("B").slack());

        assertEquals(5, nd.getTask("A").latestFinish());
        assertEquals(0, nd.getTask("A").latestStart());
        assertEquals(0, nd.getTask("A").slack());
    }

    // https://www.youtube.com/watch?v=G5sz_ucbb9A
    @Test
    public void FS_with_lag() throws DuplicateTaskKeyException, KeyNotFoundException {

        TaskDataList taskList = taskList()
                .add(task("A", 3))
                .add(task("B", 4).withPred("A", "FS", 0))
                .add(task("C", 3).withPred("B", "FS", 2));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);

        assertEquals(3, nd.getTask("B").earliestStart());
        assertEquals(7, nd.getTask("B").earliestFinish());
        assertEquals(9, nd.getTask("C").earliestStart());
        assertEquals(12, nd.getTask("C").earliestFinish());

        assertEquals(12, nd.getTask("C").latestFinish());
        assertEquals(9, nd.getTask("C").latestStart());
        assertEquals(7, nd.getTask("B").latestFinish());
        assertEquals(3, nd.getTask("B").latestStart());
    }

    // https://www.youtube.com/watch?v=lMWi96SjZ1Y
    @Test
    public void SS_with_lag() throws DuplicateTaskKeyException, KeyNotFoundException {
        TaskDataList taskList = taskList()
                .add(task("A", 2))
                .add(task("B", 3).withPred("A", "SS"))
                .add(task("C", 4).withPred("B"))
                .add(task("D", 3).withPred("C", "SS", 2));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);

        assertEquals(7, nd.getTask("C").latestFinish());
        assertEquals(3, nd.getTask("C").latestStart());
        ;
    }

    // https://www.youtube.com/watch?v=AGYdLUaZvDk
    @Test
    public void FF_with_lag() throws DuplicateTaskKeyException, KeyNotFoundException {
        TaskDataList taskList = taskList()
                .add(task("A", 3))
                .add(task("B", 2).withPred("A", "FF"))
                .add(task("C", 3).withPred("B", "FF", 2))
                .add(task("D", 3).withPred("C"));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);

        assertEquals(3, nd.getTask("B").latestFinish());
        assertEquals(1, nd.getTask("B").latestStart());
        ;
    }

    // https://www.youtube.com/watch?v=Ex_wD26Ixbc
    @Test
    public void SF_with_lag() throws DuplicateTaskKeyException, KeyNotFoundException {
        TaskDataList taskList = taskList()
                .add(task("A", 3))
                .add(task("B", 3).withPred("A", "SF", 5))
                .add(task("C", 4).withPred("B"));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);

        assertEquals(2, nd.getTask("B").earliestStart());
        assertEquals(5, nd.getTask("B").earliestFinish());
        assertEquals(5, nd.getTask("B").latestFinish());
        assertEquals(2, nd.getTask("B").latestStart());

        assertEquals(3, nd.getTask("A").latestFinish());
        assertEquals(0, nd.getTask("A").latestStart());

    }

    @Test
    public void process_Should_WorkWithTransitiveDependencies()
            throws DuplicateTaskKeyException, KeyNotFoundException {
        TaskDataList taskList = taskList()
                .add(task("A", 2))
                .add(task("B", 3).withPred("A"))
                .add(task("C", 4).withPred("B").withPred("A"));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);

        assertEquals(0, nd.getTask("A").earliestStart());
        assertEquals(2, nd.getTask("B").earliestStart());
        assertEquals(5, nd.getTask("C").earliestStart());

        assertEquals(2, nd.getTask("A").latestFinish());
        assertEquals(5, nd.getTask("B").latestFinish());
        assertEquals(9, nd.getTask("C").latestFinish());
    }

    @Test
    public void criticalPath_Should_Be_Task_When_OnlyOne() throws DuplicateTaskKeyException, KeyNotFoundException {
        TaskDataList taskList = taskList()
                .add(task("A", 2));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);

        List<Path> paths = nd.getCriticalPaths();

        assertEquals(1, paths.size());
        assertEquals(1, paths.get(0).size());
        assertEquals("A", paths.get(0).taskAt(0).idAsString());
    }

    @Test
    public void criticalPath_Should_Be_OnlyTasksWithSlackZero()
            throws DuplicateTaskKeyException, KeyNotFoundException {

        TaskDataList taskList = taskList()
                .add(task("A", 2))
                .add(task("B", 3).withPred("A"))
                .add(task("C", 2).withPred("A"));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);

        List<Path> paths = nd.getCriticalPaths();

        assertEquals(1, paths.size());
        assertEquals(2, paths.get(0).size());
        assertEquals("A", paths.get(0).taskAt(0).idAsString());
        assertEquals("B", paths.get(0).taskAt(1).idAsString());
    }

    @Test
    public void criticalPath_Should_ReturnMoreThanOneCPath()
            throws DuplicateTaskKeyException, KeyNotFoundException {
        TaskDataList taskList = taskList()
                .add(task("A"))
                .add(task("B").withPred("A"))
                .add(task("C").withPred("A"));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);

        List<Path> paths = nd.getCriticalPaths();

        assertEquals(2, paths.size());
        assertEquals(2, paths.get(0).size());
        assertEquals(2, paths.get(1).size());

        assertEquals("A", paths.get(0).taskAt(0).idAsString());
        assertEquals("B", paths.get(0).taskAt(1).idAsString());

        assertEquals("A", paths.get(1).taskAt(0).idAsString());
        assertEquals("C", paths.get(1).taskAt(1).idAsString());
    }

    @Test
    public void criticalPath_Should_WorkWithTransitiveDependencies()
            throws DuplicateTaskKeyException, KeyNotFoundException {
        TaskDataList taskList = taskList()
                .add(task("A"))
                .add(task("B").withPred("A"))
                .add(task("C").withPred("A").withPred("B"));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);

        List<Path> paths = nd.getCriticalPaths();

        assertEquals(1, paths.size());
        assertEquals(3, paths.get(0).size());

        assertEquals("A", paths.get(0).taskAt(0).idAsString());
        assertEquals("B", paths.get(0).taskAt(1).idAsString());
        assertEquals("C", paths.get(0).taskAt(2).idAsString());
    }

    @Test
    public void criticalPath_Should_WorkWithTransitiveDependenciesMoreThanOnePath()
            throws DuplicateTaskKeyException, KeyNotFoundException {
        TaskDataList taskList = taskList()
                .add(task("A"))
                .add(task("B"))
                .add(task("C").withPred("A").withPred("B"))
                .add(task("D").withPred("A").withPred("B"))
                .add(task("E")
                        .withPred("A", "FS")
                        .withPred("B", "FS")
                        .withPred("C", "FS")
                        .withPred("D", "FS"))
                .add(task("F")
                        .withPred("A", "FS")
                        .withPred("B", "FS")
                        .withPred("C", "FS")
                        .withPred("D", "FS"));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, taskList);

        List<Path> paths = nd.getCriticalPaths();

        assertEquals(8, paths.size());
        for (int i = 0; i < 8; i++) {
            assertEquals(3, paths.get(i).size());
            assertTrue(Arrays.asList(new String[] { "A", "B" })
                    .contains(paths.get(i).taskAt(0).idAsString()));
            assertTrue(Arrays.asList(new String[] { "C", "D" })
                    .contains(paths.get(i).taskAt(1).idAsString()));
            assertTrue(Arrays.asList(new String[] { "E", "F" })
                    .contains(paths.get(i).taskAt(2).idAsString()));
        }

    }

    @Test
    public void test111() throws DuplicateTaskKeyException, KeyNotFoundException {
        TaskDataList t = taskList()
                .add(task("A"))
                .add(task("B").withPred("A"))
                .add(task("C").withPred("B").withPred("A"))
                .add(task("D").withPred("A"))
                .add(task("E").withPred("D").withPred("A"));

        NetworkDiagram nd = new NetworkDiagram();
        DiagramNetworkReaderService.processTaskList(nd, t);

        List<Path> paths = nd.getCriticalPaths();
        // PrinterService.printTasksAndCriticalPaths(nd, new ConsoleNetworkDiagramPrinter(1));

        List<List<String>> pathsAsListsOfStrings = Query.map(paths,
                p -> Query.map(
                        p.tasks(),
                        tsk -> tsk.idAsString()));

        assertEquals(4, paths.size());
        assertThat(pathsAsListsOfStrings,
                containsInAnyOrder(
                        Arrays.asList("A", "B", "C"),
                        Arrays.asList("A", "D", "E"),
                        Arrays.asList("A", "C"),
                        Arrays.asList("A", "E")));

    }
}

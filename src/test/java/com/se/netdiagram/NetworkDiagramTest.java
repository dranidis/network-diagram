package com.se.netdiagram;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalLong;

import org.junit.Test;

/**
 * Unit test for NetworkDiagram
 */
public class NetworkDiagramTest 
{
    @Test(expected = DuplicateTaskKeyException.class)
    public void read_Should_throw_DuplicateTaskKeyException_WhenDuplicateKeys()
            throws DuplicateTaskKeyException, KeyNotFoundException
    {
        List<Task> tasklist = new ArrayList<>();

        Task task1 = new Task();
        task1.id = "A";
        tasklist.add(task1);

        Task task2 = new Task();
        task2.id = "A";
        tasklist.add(task2);

        NetworkDiagram nd = new NetworkDiagram();
        nd.readTasklist(tasklist);
    }

    @Test(expected = KeyNotFoundException.class)
    public void read_Should_throw_KeyNotFoundException_WhenKeyDoesNotExist()
            throws DuplicateTaskKeyException, KeyNotFoundException
    {
        List<Task> tasklist = new ArrayList<>();

        Task task1 = new Task();
        task1.id = "A";
        task1.pred = Arrays.asList(new String[]{});
        tasklist.add(task1);

        Task task2 = new Task();
        task2.id = "B";
        task2.pred = Arrays.asList(new String[]{"C"});
        tasklist.add(task2);

        NetworkDiagram nd = new NetworkDiagram();
        nd.readTasklist(tasklist);
    }

    @Test
    public void read_Should_Finish_WhenThereAreNoProblems()
            throws DuplicateTaskKeyException, KeyNotFoundException
    {
        List<Task> tasklist = new ArrayList<>();

        Task task1 = new Task();
        task1.id = "A";
        task1.pred = Arrays.asList(new String[]{});
        tasklist.add(task1);

        Task task2 = new Task();
        task2.id = "B";
        task2.pred = Arrays.asList(new String[]{"A"});
        tasklist.add(task2);

        NetworkDiagram nd = new NetworkDiagram();
        nd.readTasklist(tasklist);

        assertTrue(nd.getTasks().containsKey("A"));
        assertTrue(nd.getTasks().containsKey("B"));
    }

    @Test
    public void earliestStart_Should_Be_Zero_When_ThereAreNoPredecessors() {
        Task task1 = new Task();
        task1.id = "A"; 
        task1.pred = Arrays.asList(new String[]{});
        task1.duration = 5;

        NetworkDiagram nd = new NetworkDiagram();
        nd.getTasks().put(task1.id, task1);

        long projectEnd = nd.forward();
        
        assertEquals(0, task1.earliestStart.getAsLong());       
        assertEquals(5, task1.earliestFinish.getAsLong());       
        assertEquals(5, projectEnd);       
    }

    @Test
    public void earliestStart_Should_Be_EarliestFinishOfPred_When_ThereisOnePredecessor() {
        Task task1 = new Task();
        task1.id = "A"; 
        task1.pred = Arrays.asList(new String[]{});
        task1.duration = 5;

        Task task2 = new Task();
        task2.id = "B"; 
        task2.pred = Arrays.asList(new String[]{"A"});
        task2.duration = 3;        

        NetworkDiagram nd = new NetworkDiagram();
        nd.getTasks().put(task1.id, task1);
        nd.getTasks().put(task2.id, task2);

        long projectEnd = nd.forward();
        
        assertEquals(5, task2.earliestStart.getAsLong());       
        assertEquals(8, task2.earliestFinish.getAsLong());       
        assertEquals(8, projectEnd);       
    }

    @Test
    public void earliestStart_Should_Be_MaxOfEarliestFinishOfPreds_When_ThereAreMorePredecessors() {
        Task task1 = new Task();
        task1.id = "A"; 
        task1.pred = Arrays.asList(new String[]{});
        task1.duration = 5;

        Task task2 = new Task();
        task2.id = "B"; 
        task2.pred = Arrays.asList(new String[]{});
        task2.duration = 3;   
        
        Task task3 = new Task();
        task3.id = "C"; 
        task3.pred = Arrays.asList(new String[]{"A", "B"});
        task3.duration = 2;           

        NetworkDiagram nd = new NetworkDiagram();
        nd.getTasks().put(task1.id, task1);
        nd.getTasks().put(task2.id, task2);
        nd.getTasks().put(task3.id, task3);

        long projectEnd = nd.forward();
        
        assertEquals(5, task3.earliestStart.getAsLong());       
        assertEquals(7, task3.earliestFinish.getAsLong());       
        assertEquals(7, projectEnd);       
    }

    @Test
    public void successors_Should_Corectly_Link_tasks() {
        Task task1 = new Task();
        task1.id = "A"; 
        task1.pred = Arrays.asList(new String[]{});

        NetworkDiagram nd = new NetworkDiagram();
        nd.getTasks().put(task1.id, task1);

        nd.successors();
        
        assertEquals(0, task1.succ.size());       
    }

    @Test
    public void successors_Should_Corectly_Link_tasksBackWards() {
        Task task1 = new Task();
        task1.id = "A"; 
        task1.pred = Arrays.asList(new String[]{});

        Task task2 = new Task();
        task2.id = "B"; 
        task2.pred = Arrays.asList(new String[]{"A"});

        Task task3 = new Task();
        task3.id = "C"; 
        task3.pred = Arrays.asList(new String[]{"A", "B"});

        NetworkDiagram nd = new NetworkDiagram();
        nd.getTasks().put(task1.id, task1);
        nd.getTasks().put(task2.id, task2);
        nd.getTasks().put(task3.id, task3);

        nd.successors();
        
        assertEquals(2, task1.succ.size());       
        assertTrue(task1.succ.contains("B"));       
        assertTrue(task1.succ.contains("C"));       
        assertEquals(1, task2.succ.size());       
        assertTrue(task2.succ.contains("C"));       
        assertEquals(0, task3.succ.size());       
    }

    @Test
    public void latestFinish_Should_Be_earliestFinish_When_ThereAreNoSuccessors() {
        Task task1 = new Task();
        task1.id = "A"; 
        task1.succ = Arrays.asList(new String[]{});
        task1.duration = 5;
        task1.earliestFinish = OptionalLong.of(5);

        NetworkDiagram nd = new NetworkDiagram();
        nd.getTasks().put(task1.id, task1);

        nd.backward(5);
        
        assertEquals(5, task1.latestFinish.getAsLong());       
        assertEquals(0, task1.latestStart.getAsLong());       
        assertEquals(0, task1.slack.getAsLong());       
    }

    @Test
    public void latestFinish_Should_Be_min_of_latestStart_When_ThereAreSuccessors() {
        Task task1 = new Task();
        task1.id = "A"; 
        task1.succ = Arrays.asList(new String[]{"B", "C"});
        task1.duration = 5;
        task1.earliestFinish = OptionalLong.of(5);

        Task task2 = new Task();
        task2.id = "B"; 
        task2.succ = Arrays.asList(new String[]{});
        task2.duration = 3;
        task2.earliestFinish = OptionalLong.of(8);

        Task task3 = new Task();
        task3.id = "C"; 
        task3.succ = Arrays.asList(new String[]{});
        task3.duration = 2;        
        task3.earliestFinish = OptionalLong.of(7);

        NetworkDiagram nd = new NetworkDiagram();
        nd.getTasks().put(task1.id, task1);
        nd.getTasks().put(task2.id, task2);
        nd.getTasks().put(task3.id, task3);

        nd.backward(8);
        
        assertEquals(8, task3.latestFinish.getAsLong());       
        assertEquals(6, task3.latestStart.getAsLong());       
        assertEquals(1, task3.slack.getAsLong());       

        assertEquals(8, task2.latestFinish.getAsLong());       
        assertEquals(5, task2.latestStart.getAsLong());       
        assertEquals(0, task2.slack.getAsLong());       

        assertEquals(5, task1.latestFinish.getAsLong());       
        assertEquals(0, task1.latestStart.getAsLong());       
        assertEquals(0, task1.slack.getAsLong());       
    }

    @Test
    public void process_Should_WorkWithTransitiveDependencies() {
        Task task1 = new Task();
        task1.id = "A"; 
        task1.pred = Arrays.asList(new String[]{});
        task1.duration = 2;

        Task task2 = new Task();
        task2.id = "B"; 
        task2.pred = Arrays.asList(new String[]{"A"});
        task2.duration = 3;

        Task task3 = new Task();
        task3.id = "C"; 
        task3.pred = Arrays.asList(new String[]{"B", "A"});
        task3.duration = 4;

        NetworkDiagram nd = new NetworkDiagram();
        nd.getTasks().put(task1.id, task1);
        nd.getTasks().put(task2.id, task2);
        nd.getTasks().put(task3.id, task3);

        nd.process();

        assertEquals(0, task1.earliestStart.getAsLong());
        assertEquals(2, task2.earliestStart.getAsLong());
        assertEquals(5, task3.earliestStart.getAsLong());

        assertEquals(2, task1.latestFinish.getAsLong());
        assertEquals(5, task2.latestFinish.getAsLong());
        assertEquals(9, task3.latestFinish.getAsLong());
    }

    @Test
    public void criticalPath_Should_Be_Task_When_OnlyOne() {
        Task task1 = new Task();
        task1.id = "A"; 
        task1.pred = Arrays.asList(new String[]{});
        task1.slack = OptionalLong.of(0);

        NetworkDiagram nd = new NetworkDiagram();
        nd.getTasks().put(task1.id, task1);

        List<List<Task>> paths = nd.getCriticalPaths();
        
        assertEquals(1, paths.size());       
        assertEquals(1, paths.get(0).size());       
        assertEquals("A", paths.get(0).get(0).id);       
    }

    @Test
    public void criticalPath_Should_Be_OnlyTasksWithSlackZero() {
        Task task1 = new Task();
        task1.id = "A"; 
        task1.pred = Arrays.asList(new String[]{});
        task1.slack = OptionalLong.of(0);

        Task task2 = new Task();
        task2.id = "B"; 
        task2.pred = Arrays.asList(new String[]{"A"});
        task2.slack = OptionalLong.of(0);


        Task task3 = new Task();
        task3.id = "C"; 
        task3.pred = Arrays.asList(new String[]{"A"});
        task3.slack = OptionalLong.of(1);

        NetworkDiagram nd = new NetworkDiagram();
        nd.getTasks().put(task1.id, task1);
        nd.getTasks().put(task2.id, task2);
        nd.getTasks().put(task3.id, task3);

        List<List<Task>> paths = nd.getCriticalPaths();
        
        assertEquals(1, paths.size());       
        assertEquals(2, paths.get(0).size());       
        assertEquals("A", paths.get(0).get(0).id);       
        assertEquals("B", paths.get(0).get(1).id);       
    }

    @Test
    public void criticalPath_Should_ReturnMoreThanOneCPath() {
        Task task1 = new Task();
        task1.id = "A"; 
        task1.pred = Arrays.asList(new String[]{});
        task1.slack = OptionalLong.of(0);

        Task task2 = new Task();
        task2.id = "B"; 
        task2.pred = Arrays.asList(new String[]{"A"});
        task2.slack = OptionalLong.of(0);


        Task task3 = new Task();
        task3.id = "C"; 
        task3.pred = Arrays.asList(new String[]{"A"});
        task3.slack = OptionalLong.of(0);

        NetworkDiagram nd = new NetworkDiagram();
        nd.getTasks().put(task1.id, task1);
        nd.getTasks().put(task2.id, task2);
        nd.getTasks().put(task3.id, task3);

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
    public void criticalPath_Should_WorkWithTransitiveDependencies() {
        Task task1 = new Task();
        task1.id = "A"; 
        task1.pred = Arrays.asList(new String[]{});
        task1.slack = OptionalLong.of(0);

        Task task2 = new Task();
        task2.id = "B"; 
        task2.pred = Arrays.asList(new String[]{"A"});
        task2.slack = OptionalLong.of(0);


        Task task3 = new Task();
        task3.id = "C"; 
        task3.pred = Arrays.asList(new String[]{"A", "B"});
        task3.slack = OptionalLong.of(0);

        NetworkDiagram nd = new NetworkDiagram();
        nd.getTasks().put(task1.id, task1);
        nd.getTasks().put(task2.id, task2);
        nd.getTasks().put(task3.id, task3);

        List<List<Task>> paths = nd.getCriticalPaths();
        
        assertEquals(1, paths.size());       
        assertEquals(3, paths.get(0).size());       

        assertEquals("A", paths.get(0).get(0).id);       
        assertEquals("B", paths.get(0).get(1).id);   
        assertEquals("C", paths.get(0).get(2).id);         
    }
    
    @Test
    public void criticalPath_Should_WorkWithTransitiveDependenciesMoreThanOnePath() {
        Task task1 = new Task();
        task1.id = "A"; 
        task1.pred = Arrays.asList(new String[]{});
        task1.slack = OptionalLong.of(0);

        Task task2 = new Task();
        task2.id = "B"; 
        task2.pred = Arrays.asList(new String[]{});
        task2.slack = OptionalLong.of(0);

        Task task3 = new Task();
        task3.id = "C"; 
        task3.pred = Arrays.asList(new String[]{"A", "B"});
        task3.slack = OptionalLong.of(0);

        Task task4 = new Task();
        task4.id = "D"; 
        task4.pred = Arrays.asList(new String[]{"A", "B"});
        task4.slack = OptionalLong.of(0);

        Task task5 = new Task();
        task5.id = "E"; 
        task5.pred = Arrays.asList(new String[]{"A", "B", "C", "D"});
        task5.slack = OptionalLong.of(0);

        Task task6 = new Task();
        task6.id = "F"; 
        task6.pred = Arrays.asList(new String[]{"A", "B", "C", "D"});
        task6.slack = OptionalLong.of(0);

        NetworkDiagram nd = new NetworkDiagram();
        nd.getTasks().put(task1.id, task1);
        nd.getTasks().put(task2.id, task2);
        nd.getTasks().put(task3.id, task3);
        nd.getTasks().put(task4.id, task4);
        nd.getTasks().put(task5.id, task5);
        nd.getTasks().put(task6.id, task6);

        List<List<Task>> paths = nd.getCriticalPaths();

        assertEquals(8, paths.size());
        for(int i = 0; i < 8; i++) {      
            assertEquals(3, paths.get(i).size());  
            assertTrue(Arrays.asList(new String[]{"A", "B"}).contains(paths.get(i).get(0).id));
            assertTrue(Arrays.asList(new String[]{"C", "D"}).contains(paths.get(i).get(1).id));
            assertTrue(Arrays.asList(new String[]{"E", "F"}).contains(paths.get(i).get(2).id));
        }     
    
    }    
}

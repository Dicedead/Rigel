package ch.epfl.rigel.parallelism;

import ch.epfl.rigel.math.graphs.GraphNode;
import ch.epfl.rigel.math.graphs.Path;
import ch.epfl.rigel.math.graphs.Tree;
import ch.epfl.rigel.math.sets.abstraction.AbstractMathSet;
import ch.epfl.rigel.math.sets.implement.MathSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.concurrent.Task;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Multithreaded environment manager
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class ThreadManager {

    private final static int CPU_CORES = Runtime.getRuntime().availableProcessors();
    private final ExecutorService threadPool;
    private final ObservableSet<List<Task<?>>> tasksQueue;
    private final Tree<Task<?>> taskTree;

    private int difficulty(List<Task<?>> tasks)
    {
        return 0;
    }
    private void addFromTree(final Task<?> level)
    {
        taskTree.getElement(g -> g
                .getValue()
                .equals(level))
                .flatMap(taskTree::branchAtPoint)
                .ifPresent(l -> tasksQueue.add(l.toList().stream().map(GraphNode::getValue).collect(Collectors.toList())));
    }

    /**
     * Main constructor
     * @param taskTree
     */
    public ThreadManager(Tree<Task<?>> taskTree)
    {
        this.taskTree       = taskTree;
        threadPool          = Executors.newCachedThreadPool();
        tasksQueue          = FXCollections.synchronizedObservableSet(FXCollections.observableSet(new ArrayList<>()));

        tasksQueue.addListener((SetChangeListener<List<Task<?>>> )c -> {
            if (c.wasAdded())
                threadPool.submit()
        });

    }


    /**
     *
     * @return the number of available CPU cores counting soft ones like intel's hyperthreading
     */
    public static int getCpuCores() {
        return CPU_CORES;
    }

}

package ch.epfl.rigel.parallelism;


import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;

/**
 * Thread creator
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class RigelThreadFactory implements ThreadFactory {

    private final String name;
    private final int priority;
    private final ThreadGroup group;
    private int count;
    private final List<String> stats;

    /**
     * Main RigelThreadFactory constructor
     */
    public RigelThreadFactory(String name, int priority, ThreadGroup group, List<String> stats)
    {
        this.name = name;
        this.priority = priority;
        this.group = group;
        this.stats = stats;
        count = 0;
    }

    @Override
    public Thread newThread(Runnable runnable) {

        Thread t = new Thread(group, name + ++count);
        t.setPriority(priority);
        stats.add(String.format("Le thread %s a été créé à %s \n", t.getName() + count, new Date()));

        return t;
    }

    public String getStats()
    {
        return String.join("\n", stats);
    }

}
package ch.epfl.rigel.parallelism;

import ch.epfl.rigel.logging.RigelLogger;

import java.util.concurrent.ThreadFactory;


public final class RigelThreadFactory implements ThreadFactory
{
    private final String name;
    private final int priority;
    private final ThreadGroup group;
    private int count;

    public RigelThreadFactory (final String name, final int priority, final ThreadGroup group)
    {
        this.name = name;
        this.priority = priority;
        this.group = group;
        count = 0;
    }

    @Override
    public Thread newThread(Runnable runnable) {

        final Thread t = new Thread(group, name + ++count);
        t.setPriority(priority);

        RigelLogger.getBackendLogger().info("Thread " + name + ++count + " has been created");

        return t;
    }
}
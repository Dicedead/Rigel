package ch.epfl.rigel.parallelism;

import java.util.concurrent.ThreadFactory;


public final class RigelThreadFactory implements ThreadFactory
{
    final String name;
    final int priority;
    final ThreadGroup group;
    int count;

    RigelThreadFactory (final String name, final int priority, final ThreadGroup group)
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

        System.out.println("====== Thread " + name + ++count + " has been created ======");
        return t;
    }
}
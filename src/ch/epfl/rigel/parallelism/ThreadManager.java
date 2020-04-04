package ch.epfl.rigel.parallelism;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadFactory;

public final class ThreadManager {

    private final ExecutorService gui;
    private final ExecutorService astronomy;

    private static final class RigelFactory implements ThreadFactory
    {
        final String name;
        final int priority;
        final ThreadGroup group;
        int count;

        RigelFactory(final String n, final int prio, final ThreadGroup group)
        {
            name = n;
            priority = prio;
            this.group = group;
            count = 0;
        }

        @Override
        public Thread newThread(Runnable runnable) {

            final Thread t = new Thread(group, name + ++count);
            t.setPriority(priority);
            return t;
        }
    }

    public ThreadManager()
    {
        ThreadGroup backend = new ThreadGroup("BACKEND");
        ThreadGroup frontend = new ThreadGroup("FRONTEND");


         astronomy = Executors.newCachedThreadPool(new RigelFactory("astronomy", 1, backend));
         gui = Executors.newCachedThreadPool(new RigelFactory("gui", 1, frontend));


    }


}

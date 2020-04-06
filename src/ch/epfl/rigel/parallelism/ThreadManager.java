package ch.epfl.rigel.parallelism;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public final class ThreadManager {

    private final static int cores = Runtime.getRuntime().availableProcessors();
    private ExecutorService io;
    private ExecutorService astronomy;
    private ExecutorService gui;

    public ThreadManager()
    {
        ThreadGroup backend = new ThreadGroup("BACKEND");
        ThreadGroup frontend = new ThreadGroup("FRONTEND");
        ThreadGroup IO = new ThreadGroup(backend, "BACKEND");

        io = Executors.newCachedThreadPool(new RigelThreadFactory("io", 3, IO));
        astronomy = Executors.newCachedThreadPool(new RigelThreadFactory("astronomy", 1, backend));
        gui = Executors.newCachedThreadPool(new RigelThreadFactory("gui", 2, frontend));

    }


    public ExecutorService getIo() {
        return io;
    }

    public ExecutorService getAstronomy() {
        return astronomy;
    }

    public ExecutorService getGui() {
        return gui;
    }
}

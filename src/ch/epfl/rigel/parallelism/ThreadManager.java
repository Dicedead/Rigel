package ch.epfl.rigel.parallelism;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public final class ThreadManager {

    private final static int cores = Runtime.getRuntime().availableProcessors();
    private static ExecutorService io;
    private static ExecutorService astronomy;
    private static ExecutorService gui;
    private static ExecutorService logger;

    public static void initThreads()
    {
        final ThreadGroup backend = new ThreadGroup("BACKEND");
        final ThreadGroup frontend = new ThreadGroup("FRONTEND");
        final ThreadGroup IO = new ThreadGroup(backend, "BACKEND");

        io = Executors.newCachedThreadPool(new RigelThreadFactory("io", 3, IO));
        astronomy = Executors.newCachedThreadPool(new RigelThreadFactory("astronomy", 1, backend));
        gui = Executors.newCachedThreadPool(new RigelThreadFactory("gui", 2, frontend));
        logger = Executors.newSingleThreadExecutor(new RigelThreadFactory("logger", 2, frontend));

    }
    private ThreadManager()
    { throw new UnsupportedOperationException();}


    public static ExecutorService getIo() {
        return io;
    }

    public static ExecutorService getAstronomy() {
        return astronomy;
    }

    public static ExecutorService getGui() {
        return gui;
    }

    public static ExecutorService getLogger() {
        return logger;
    }

}

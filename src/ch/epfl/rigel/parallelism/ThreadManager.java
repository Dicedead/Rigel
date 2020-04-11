package ch.epfl.rigel.parallelism;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Multithreaded environment manager
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
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

        io = Executors.newCachedThreadPool();
        astronomy = Executors.newCachedThreadPool();
        gui = Executors.newCachedThreadPool();
        logger = Executors.newSingleThreadExecutor();

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

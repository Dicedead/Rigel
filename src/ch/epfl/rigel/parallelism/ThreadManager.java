package ch.epfl.rigel.parallelism;

import javafx.concurrent.Task;
import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static java.util.concurrent.ForkJoinPool.defaultForkJoinWorkerThreadFactory;

/**
 * Multithreaded environment manager
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class ThreadManager {

    public enum serviceType {
        CACHED, SINGLE, FIXED
    }

    private final static int CPU_CORES = Runtime.getRuntime().availableProcessors();

    private final Map<String, ExecutorService> executorServiceMap = new HashMap<>();
    private final Map<String, ForkJoinPool> forkJoinPoolMap = new HashMap<>();
    private final ThreadFactory factory;
    private Deque< Runnable> tasks;

    public void addService(String name, serviceType type, int nthreads)
    {
        switch (type){
            case FIXED:
                executorServiceMap.put(name, Executors.newFixedThreadPool(nthreads, factory));
                break;
            case CACHED:
                executorServiceMap.put(name, Executors.newCachedThreadPool(factory));
                break;
            case SINGLE:
                executorServiceMap.put(name, Executors.newSingleThreadExecutor(factory));
                break;
        }
    }

    public void addForkJoinPool(String name, String threadGroup, int thread, boolean async)
    {
        forkJoinPoolMap.put(name, new ForkJoinPool(thread, defaultForkJoinWorkerThreadFactory, new ThreadGroup(threadGroup), async));
    }

    public void addForkJoinPool(String name)
    {
        forkJoinPoolMap.put(name, new ForkJoinPool());
    }

    void addTask(Runnable r)
    {
        executorServiceMap.get("background").submit(r);
    }

    void addTask(Runnable r, String name)
    {
        executorServiceMap.get(name).submit(r);
    }


    public ThreadManager(List<String> threadGroupNames, ThreadFactory factory)
    {
        this.factory = factory;

        forkJoinPoolMap.put("default", new ForkJoinPool(1));
        executorServiceMap.put("default", Executors.newSingleThreadExecutor(factory));
        executorServiceMap.put("background", Executors.newWorkStealingPool());
    }

    public void shutdown(boolean force)
    {
        if (!force) {
            forkJoinPoolMap.values().forEach(ForkJoinPool::shutdown);
            executorServiceMap.values().forEach(ExecutorService::shutdown);

        } else {

            forkJoinPoolMap.values().forEach(ForkJoinPool::shutdownNow);
            executorServiceMap.values().forEach(ExecutorService::shutdownNow);
        }
    }

    public static int getCpuCores() {
        return CPU_CORES;
    }

    public ForkJoinPool getFJ(String name)
    {
        return forkJoinPoolMap.get(name);
    }

    public ExecutorService getES(String name)
    {
        return executorServiceMap.get(name);
    }

}

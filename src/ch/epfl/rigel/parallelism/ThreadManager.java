package ch.epfl.rigel.parallelism;

import ch.epfl.rigel.math.graphs.Tree;

import java.util.*;
import java.util.concurrent.*;

import static java.util.concurrent.ForkJoinPool.defaultForkJoinWorkerThreadFactory;

/**
 * Multithreaded environment manager
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class ThreadManager {
    /**
     * A small enum type for handling different types of pool of threads
     */
    public enum serviceType {
        CACHED, SINGLE, FIXED
    }
    private final static int CPU_CORES = Runtime.getRuntime().availableProcessors();

    private final Map<String, ExecutorService> executorServiceMap = new HashMap<>();
    private final Map<String, ForkJoinPool> forkJoinPoolMap = new HashMap<>();
    private final ThreadFactory factory;
    private Tree<RigelTask> taskTree;


    public void assign(RigelTask.Difficulty forkjoinLimit)
    {
        //taskTree.getLeaves().stream().forEach();
    }

    /**
     * Method allowing to ad a new executors service, useful to separate different usages
     * @param name the name to get to the executor
     * @param type the type of pool desired
     * @param threads the number of threads to assign to it if it is a fixed pool
     */
    public void addService(String name, serviceType type, int threads)
    {
        switch (type){
            case FIXED:
                executorServiceMap.put(name, Executors.newFixedThreadPool(threads, factory));
                break;
            case CACHED:
                executorServiceMap.put(name, Executors.newCachedThreadPool(factory));
                break;
            case SINGLE:
                executorServiceMap.put(name, Executors.newSingleThreadExecutor(factory));
                break;
        }
    }

    /**
     * Method allowing to ad a new ForkJoinPool, useful to separate different light usages
     * @param name the name to get to the executor
     * @param threadGroup the type of pool desired
     * @param thread the number of threads to assign to it if it is a fixed pool
     * @param async //TODO: WTF is this one ?
     */
    public void addForkJoinPool(String name, String threadGroup, int thread, boolean async)
    {
        forkJoinPoolMap.put(name, new ForkJoinPool(thread, defaultForkJoinWorkerThreadFactory, new ThreadGroup(threadGroup), async));
    }
    /**
     * Method allowing to ad a new ForkJoinPool, useful to separate different light usages
     * @param name the name to get to the executor
     * */
    public void addForkJoinPool(String name)
    {
        forkJoinPoolMap.put(name, new ForkJoinPool());
    }

    /**
     * Add an extra task not specified in the task tree
     * @param r the task to be added
     */
    void addTask(Runnable r)
    {
        executorServiceMap.get("background").submit(r);
    }


    /**
     * Add an extra task not specified in the task tree to the executor specified
     * @param r the task to be added
     * @param name the executor to use for this task
     */
    void addTask(Runnable r, String name)
    {
        executorServiceMap.get(name).submit(r);
    }

    /**
     * Main constructor
     * @param factory the  factory used in the thread manager
     */
    public ThreadManager(ThreadFactory factory)
    {
        this.factory = factory;
        forkJoinPoolMap.put("default", new ForkJoinPool(1));
        executorServiceMap.put("default", Executors.newSingleThreadExecutor(factory));
        executorServiceMap.put("background", Executors.newWorkStealingPool());
    }

    /**
     * A shutdown is sent to every executor, if forced it will cancel any computation
     * @param force wheter it should wait for completitionis of the threads or not
     */
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

    /**
     *
     * @return the number of available CPU cores counting soft ones like intel's hyperthreading
     */
    public static int getCpuCores() {
        return CPU_CORES;
    }

    /**
     *
     * @param name the name of a constructed forkJoinPool
     * @return the forkjoin pool specified
     */
    public ForkJoinPool getFJ(String name)
    {
        return forkJoinPoolMap.get(name);
    }
    /**
     *
     * @param name the name of a constructed executorService
     * @return the executorService specified
     */
    public ExecutorService getES(String name)
    {
        return executorServiceMap.get(name);
    }

}

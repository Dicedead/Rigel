package ch.epfl.rigel.parallelism;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * Multithreaded environment manager
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class ThreadManager {

    private final List<ThreadGroup> threadGroups = new ArrayList<>();;
    private final static int cores = Runtime.getRuntime().availableProcessors();
    private Map<String, ExecutorService> executorServiceMap;
    private Map<String, ForkJoinPool> forkJoinPoolMap;

    public ThreadManager(List<String> threadGroupNames)
    {
        IntStream.of(0, threadGroupNames.size()).forEach(
                i-> threadGroups.add(new ThreadGroup(threadGroupNames.get(i))));

        forkJoinPoolMap.put("default", new ForkJoinPool(cores));
        executorServiceMap.put("default", Executors.newCachedThreadPool());
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


}

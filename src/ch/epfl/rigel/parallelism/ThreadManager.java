package ch.epfl.rigel.parallelism;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

public final class ThreadManager {



    public ThreadManager()
    {
        ThreadGroup backend = new ThreadGroup("BACKEND");
        ThreadGroup frontend = new ThreadGroup("FRONTEND");
        ThreadGroup IO = new ThreadGroup(backend, "BACKEND");
    }


}

package ch.epfl.rigel.parallelism;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Wrapper for a task in this project
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public interface RigelFuture<V> extends Future<V> {

    /**
     * A scale of difficulty for a task, prompting more ressources from the system
     */
    enum Difficulty
    {
        EASY, NORMAL, HARD
    }

    /**
     *
     * @return getter for difficulty of the task, not synchronised as it is final
     */
    Difficulty getDifficulty();

    /**
     *
     * @return if the task is completed, true
     * */
    boolean isCompleted();

}

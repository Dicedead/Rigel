package ch.epfl.rigel.parallelism;

/**
 * Wrapper for a task in this project
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class RigelTask implements Runnable {

    /**
     * A scale of difficulty for a task, prompting more ressources from the system
     */
    public enum Difficulty
    {
        EASY, NORMAL, HARD
    }
    private final Runnable task;
    private final Difficulty difficulty;
    private boolean isCompleted;

    /**
     * Main constructor
     * @param vRunnableFuture the task to execute
     * @param d its difficulty
     */
    public RigelTask(final Runnable vRunnableFuture, final Difficulty d) {
        task = vRunnableFuture;
        difficulty = d;
    }

    /**
     *
     * @return getter for difficulty of the task, not synchronised as it is final
     */
    public Difficulty getDifficulty()
    {
        return difficulty;
    }

    /**
     *
     * @return if the task is completed, true
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * modifies the status of completion in a synchronised way in case some other thread tries to do so
     */
    private synchronized void setDone()
    {
        isCompleted = true;
    }

    /**
     * Wrapper that allows to directly run the task
     */
    @Override
    public void run() {
        task.run();
        setDone();
    }
}

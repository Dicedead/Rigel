package ch.epfl.rigel.parallelism;

public class RigelTask implements Runnable {


    public String getExecutor() {
        return executor;
    }

    public enum Difficulty
    {
        EASY, NORMAL, HARD
    }
    private final Runnable task;
    private final Difficulty difficulty;
    private boolean isCompleted;
    private final String executor;
    public RigelTask(final Runnable vRunnableFuture, final Difficulty d, final String exec) {
        task = vRunnableFuture;
        difficulty = d;
        executor = exec;
    }

    public Difficulty getDifficulty()
    {
        return difficulty;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    private synchronized void setDone()
    {
        isCompleted = true;
    }

    @Override
    public void run() {
        task.run();
        setDone();
    }
}

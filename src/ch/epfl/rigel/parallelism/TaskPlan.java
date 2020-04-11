package ch.epfl.rigel.parallelism;

public class TaskPlan {
    final private Tree<Runnable> plan;

    public TaskPlan(Tree<Runnable> plan) {
        this.plan = plan;
    }
}

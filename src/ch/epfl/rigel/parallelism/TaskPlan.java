package ch.epfl.rigel.parallelism;

import ch.epfl.rigel.math.graphs.Tree;

public final class TaskPlan {
    final private Tree<Runnable> plan;

    public TaskPlan(Tree<Runnable> plan) {
        this.plan = plan;
    }
}

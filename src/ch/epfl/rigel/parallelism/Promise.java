package ch.epfl.rigel.parallelism;

import javafx.beans.property.BooleanProperty;
import javafx.concurrent.Task;

/**
 * Wrapper for a task in this project
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public abstract class Promise<V> extends Task<V> {
    private BooleanProperty canLaunch;
}

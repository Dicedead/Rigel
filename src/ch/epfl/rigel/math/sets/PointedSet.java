package ch.epfl.rigel.math.sets;

import javafx.util.Pair;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.DoubleStream;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public class PointedSet<T> extends MathSet<T> {

    final private T special;

    public PointedSet(Collection<T> t, T special) {
        super(t);
        this.special = special;
    }

    public PointedSet(MathSet<T> t, T special) {
        super(t.getData());
        this.special = special;
    }

    public PointedSet(PointedSet<T> t) {
        super(t.getData());
        this.special = t.special;
    }
    @Override
    public T getElement()
    {return special;}

}

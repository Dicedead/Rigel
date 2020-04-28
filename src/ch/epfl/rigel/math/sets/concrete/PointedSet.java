package ch.epfl.rigel.math.sets.concrete;

import ch.epfl.rigel.math.sets.abtract.AbstractMathSet;

import java.util.Collection;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public abstract class PointedSet<T> extends MathSet<T> {

    final private T special;

    /**
     * A set possessing a special element
     *
     * @param t       the underlying data
     * @param special the element to remember
     */
    public PointedSet(Collection<T> t, T special) {
        super(t);
        this.special = special;
    }

    /**
     * A set possessing a special element
     *
     * @param t       the underlying data
     * @param special the element to remember
     */
    public PointedSet(AbstractMathSet<T> t, T special) {
        super(t.getData());
        this.special = special;
    }

    /**
     * Copy constructor
     *
     * @param t the special element
     */
    public PointedSet(PointedSet<T> t) {
        super(t.getData());
        this.special = t.special;
    }

    /**
     * @return (T) the pointed element in the set
     */
    @Override
    public T getElementOrThrow() {
        return special;
    }

}

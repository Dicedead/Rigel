package ch.epfl.rigel.math.sets.abstraction;

import ch.epfl.rigel.math.sets.implement.MathSet;

import java.util.Collection;

/**
 * Abstraction of a set with a special element
 *
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
        super(t.getRawData());
        this.special = special;
    }

    /**
     * Copy constructor
     *
     * @param t the special element
     */
    public PointedSet(PointedSet<T> t) {
        super(t.getRawData());
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

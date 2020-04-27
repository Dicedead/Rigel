package ch.epfl.rigel.math.sets;

import javafx.util.Pair;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * An ordered set with 2 possible values of 2 different types
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class OptionalPair<U, V> extends Pair<Optional<U>, Optional<V>> {

    public enum Position {LEFT, RIGHT}

    /**
     * Creates a new pair containing an element containing data and a dummy element
     *
     * @param u An element of type u, if null the other will be the true value
     * @param v An element of type v, if null the other will be the true value
     */
    public OptionalPair(U u, V v) {
        super(Objects.isNull(u) ? Optional.empty() : Optional.of(u), Objects.isNull(u) ? Optional.of(v) : Optional.empty());
    }

    public static <U, V>  OptionalPair<U, V> of(final Pair<U, V> c)
    {
        if(c.getKey() != null)
            return new OptionalPair<>(c.getKey(), null );

        else if (c.getValue() != null)
            return new OptionalPair<>(null, c.getValue() );

        else throw new IllegalArgumentException();
    }

    public <A, B> OptionalPair<A, B> flatMap(final BiFunction<U, V, OptionalPair<A,B>> function)
    {
        return getTruePos() == Position.LEFT ? function.apply(getKey().get(), null) : function.apply(null, getValue().get());
    }

    /**
     * @return the position of the true holder of the value
     */
    public Position getTruePos()
    {
        return getValue().isEmpty() ? Position.LEFT : Position.RIGHT;
    }

}

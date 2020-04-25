package ch.epfl.rigel.math.sets;

import ch.epfl.rigel.math.sets.abtract.SetFunction;
import javafx.util.Pair;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Maybe<U, V> extends Pair<Optional<U>, Optional<V>> {

    public enum Type {LEFT, RIGHT}
    /**
     * Creates a new pair containing an element containing data and a dummy element
     *
     * @param u An element of type u, if null the other will be the true value
     * @param v An element of type v, if null the other will be the true value
     */
    public Maybe(U u, V v) {
        super(Objects.isNull(u) ? Optional.empty() : Optional.of(u), Objects.isNull(u) ? Optional.of(v) : Optional.empty());
    }

    public static <U, V>  Maybe<U, V> of(final Pair<U, V> c)
    {
        if(c.getKey() != null)
            return new Maybe<>(c.getKey(), null );

        else if (c.getValue() != null)
            return new Maybe<>(null, c.getValue() );

        else throw new IllegalArgumentException();
    }

    public <A, B> Maybe<A, B> flatMap(final BiFunction<U, V, Maybe<A,B>> function)
    {
        return getTrueType() == Type.LEFT ? function.apply(getKey().get(), null) : function.apply(null, getValue().get());
    }

    /**
     *
     * @return the position of the true holder of the value
     */
    public Type getTrueType()
    {
        return getValue().isEmpty() ? Type.LEFT : Type.RIGHT;
    }

}

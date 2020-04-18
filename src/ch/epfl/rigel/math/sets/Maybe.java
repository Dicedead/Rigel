package ch.epfl.rigel.math.sets;

import ch.epfl.rigel.Preconditions;
import javafx.util.Pair;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Maybe<U, V> extends Pair<Optional<U>, Optional<V>> {
    public static enum Type {LEFT, RIGHT}
    /**
     * Creates a new pair
     *
     * @param u   The key for this pair
     * @param v The value to use for this pair
     */
    public Maybe(U u, V v) {
        super(Objects.isNull(u) ? Optional.empty() : Optional.of(u), Objects.isNull(u) ? Optional.of(v) : Optional.empty());
    }

    public Type getTrueType()
    {
        return getValue().isEmpty() ? Type.LEFT : Type.RIGHT;
    }

}

package ch.epfl.rigel.math.sets;

import javafx.util.Pair;

import java.util.Objects;
import java.util.Optional;

public final class Maybe<U, V> extends Pair<Optional<U>, Optional<V>> {

    public enum Type {LEFT, RIGHT}
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

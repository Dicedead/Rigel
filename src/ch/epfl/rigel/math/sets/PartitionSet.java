package ch.epfl.rigel.math.sets;

import ch.epfl.rigel.Preconditions;
import javafx.util.Pair;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public class PartitionSet<T> extends MathSet<T> {

    private final IndexedSet<MathSet<T>, T> components;

    public PartitionSet(Collection<MathSet<T>> data) {
        super(unionOf(data).getData());
        components = new IndexedSet<>(data, new SetFunction<>(elem -> data.stream().filter(subset -> subset.contains(elem))
                .findFirst().orElseThrow()));
    }

    public PartitionSet(IndexedSet<MathSet<T>, T> t) {
        super(unionOf(t.getData()));
        components = t;
    }

    public PartitionSet(final MathSet<T> data, BiFunction<T, T, Boolean> areInRelation) {
        this(data.stream().map(elem1 -> data.suchThat(elem2 -> areInRelation.apply(elem1, elem2))).collect(Collectors.toSet()));
    }

    public PartitionSet(final MathSet<T> t) {
        this(Collections.singletonList(t));
    }

    public MathSet<T> component(T t) {
        return components.at(t);
    }

    public MathSet<MathSet<T>> components() {
        return components;
    }

    public T representant (MathSet<T> component)
    {
        Preconditions.checkArgument(components.in(component));
        return component.getElement();
    }

    public MathSet<T> representants()
    {
        return components.image(this::representant);
    }

    @Override
    public <U> PartitionSet<U> image(Function<T, U> f) {
        return new PartitionSet<>(components.stream().map(C -> C.image(f)).collect(Collectors.toSet()));
    }

    public void forEachComponent(Consumer<? super MathSet<T>> action) {
        components.getData().forEach(action);
    }

    public int numberOfComponents(){return components.cardinality();}

    public Stream<MathSet<T>> streamComponents()
    {
        return components.stream();
    }

}

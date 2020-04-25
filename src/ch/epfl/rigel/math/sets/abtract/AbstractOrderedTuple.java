package ch.epfl.rigel.math.sets.abtract;

import ch.epfl.rigel.math.sets.properties.Relation;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface AbstractOrderedTuple<T> extends AbstractIndexedSet<T, Integer>, AbstractOrderedSet<T>, AbstractMathSet<T>{
    default List<T> toList() {
        return IntStream.range(0, getData().size()).mapToObj(this::at).collect(Collectors.toList());
    }

    default T next (T t)
    {

        return t.equals(tail()) ? t : at(indexOf(t) + 1);
    }

    default T prev (T t)
    {
        return t.equals(head()) ? t : at(toList().indexOf(t) - 1);
    }


    default T tail(){return  at(cardinality() -1);}
    default T head(){return at(0);}
    default int indexOf(T t)
    {
        return toList().indexOf(t);
    }

    @Override
    default Relation.Order<T> getComparator()
    {
        return (t, u) -> indexOf(t) < indexOf(u) ? Relation.COMP.LESS : indexOf(t) == indexOf(u) ? Relation.COMP.EQUAL : Relation.COMP.GREATER;
    }

    @Override
    default Stream<T> stream() {
        return toList().stream();
    }

    @Override
    default @NotNull Iterator<T> iterator() {
        return toList().iterator();
    }

    @Override
    default void forEach(Consumer<? super T> action) {
        toList().forEach(action);
    }

    @Override
    default Spliterator<T> spliterator() {
        return toList().spliterator();
    }
}

package ch.epfl.rigel.math.sets.abstraction;

import ch.epfl.rigel.math.sets.properties.Relation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public interface AbstractOrderedTuple<T> extends AbstractIndexedSet<T, Integer>, AbstractOrderedSet<T>, AbstractMathSet<T> {

    default List<T> toList() {
        return createList(this);
    }

    static <X> List<X> createList(AbstractOrderedTuple<X> orderedTuple) {
        return IntStream.range(0, orderedTuple.getRawData().size())
                .mapToObj(orderedTuple::at)
                .collect(Collectors.toList());
    }

    default T next(T t) {
        return t.equals(tail()) ? t : at(indexOf(t) + 1);
    }

    default T prev(T t) {
        return t.equals(head()) ? t : at(toList().indexOf(t) - 1);
    }


    default T tail() {
        return at(cardinality() - 1);
    }

    default T head() {
        return at(0);
    }

    default int indexOf(T t) {
        return toList().indexOf(t);
    }

    @Override
    default Relation.Order<T> getComparator() {
        return (t, u) -> indexOf(t) < indexOf(u) ? Relation.COMP.LESS : indexOf(t) == indexOf(u) ? Relation.COMP.EQUAL : Relation.COMP.GREATER;
    }
}

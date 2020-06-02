package ch.epfl.rigel.math.sets.abstraction;

import ch.epfl.rigel.math.sets.properties.Relation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Abstraction of a set ordered with integers
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public interface AbstractOrderedTuple<T> extends AbstractIndexedSet<T, Integer>, AbstractOrderedSet<T>, AbstractMathSet<T> {

    /**
     * Convert this tuple to a list
     *
     * @return (List<T>) list form of this set
     */
    default List<T> toList() {
        return createList(this);
    }

    /**
     * Convert given tuple into a list
     *
     * @param orderedTuple (AbstractOrderedTuple<X>) given tuple
     * @param <X> type
     * @return (List<X>) list form of given tuple
     */
    static <X> List<X> createList(AbstractOrderedTuple<X> orderedTuple) {
        return IntStream.range(0, orderedTuple.getRawData().size())
                .mapToObj(orderedTuple::at)
                .collect(Collectors.toList());
    }

    /**
     * @param t (T) element
     * @return (T) the element in the tuple after given element, given element itself if it is at the end of the tuple
     */
    default T next(T t) {
        return t.equals(tail()) ? t : at(indexOf(t) + 1);
    }

    /**
     * @param t (T) element
     * @return (T) the element in the tuple before given element, given element itself if it is at the beginning of the tuple
     */
    default T prev(T t) {
        return t.equals(head()) ? t : at(toList().indexOf(t) - 1);
    }

    /**
     * @return (T) last element in the tuple
     */
    default T tail() {
        return at(cardinality() - 1);
    }

    /**
     * @return (T) first element in the tuple
     */
    default T head() {
        return at(0);
    }

    /**
     * @param t (T) element
     * @return (int) index of given element t in this set
     */
    default int indexOf(T t) {
        return toList().indexOf(t);
    }

    /**
     * @return (Relation.Order<T>) index comparator of elements in this set
     */
    @Override
    default Relation.Order<T> getComparator() {
        return (t, u) -> indexOf(t) < indexOf(u) ? Relation.Ordering.LESS : indexOf(t) == indexOf(u) ?
                Relation.Ordering.EQUAL : Relation.Ordering.GREATER;
    }
}

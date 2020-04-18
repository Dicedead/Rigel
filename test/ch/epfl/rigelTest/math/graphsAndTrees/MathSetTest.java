package ch.epfl.rigelTest.math.graphsAndTrees;

import ch.epfl.rigel.math.sets.MathSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MathSetTest {

    private static MathSet<Integer> set1 = new MathSet<>(4,5,6);
    private static MathSet<Integer> set2 = new MathSet<Integer>(2,3,4);

    @Test
    void toSetTest() {
        assertEquals(2, new MathSet<>(1,2).stream().collect(MathSet.toSet()).cardinality());
    }

    @Test
    void unionTest() {
        assertEquals(Set.of(2,3,4,5,6), set1.union(set2).getData());
    }

    @Test
    void intersectionTest() {
        assertEquals(Set.of(4), set1.intersection(set2).getData());
    }
}

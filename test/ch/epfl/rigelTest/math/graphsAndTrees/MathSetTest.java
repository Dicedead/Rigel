package ch.epfl.rigelTest.math.graphsAndTrees;

import ch.epfl.rigel.math.sets.MathSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MathSetTest {

    @Test
    void toSetTest() {
        assertEquals(2, new MathSet<>(1,2).stream().collect(MathSet.toSet()).cardinality());
    }
}

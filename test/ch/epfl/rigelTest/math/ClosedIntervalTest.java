package ch.epfl.rigelTest.math;

import ch.epfl.rigel.math.ClosedInterval;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClosedIntervalTest {

    private static ClosedInterval closedInterval;

    @BeforeAll
    private static void setUp()
    {
        closedInterval = ClosedInterval.of(-5, 9);
    }
    @Test
    void clip() {
        assertEquals(5, closedInterval.clip(5));
        assertEquals(9, closedInterval.clip(520));
        assertEquals(-5, closedInterval.clip(-600));


    }

    @Test
    void testToString() {
        assertEquals("[ -5.00, 9.00 ]", closedInterval.toString());
    }

    @Test
    void contains() {
        assertTrue(closedInterval.contains(4));
        assertTrue(closedInterval.contains(9));
        assertFalse(closedInterval.contains(765));


    }
}
package ch.epfl.rigelTest.math;

import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;
import ch.epfl.test.Chronometer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClosedIntervalTest{

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

    @Test
    void test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        System.out.println(Chronometer.prettyPrint(Chronometer.battle(List.of(ClosedInterval.class.getMethod("of", double.class, double.class)), List.of(new Object[][]{{3., 5.}}), null, 50)));
    }
}
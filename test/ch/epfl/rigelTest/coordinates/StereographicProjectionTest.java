package ch.epfl.rigelTest.coordinates;

import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StereographicProjectionTest {

    private final static double EPSILON = 1e-7;
    private final static StereographicProjection stereographicProjection =
            new StereographicProjection (HorizontalCoordinates.ofDeg(23, 45));
    private final static StereographicProjection stereographicProjection45 =
            new StereographicProjection (HorizontalCoordinates.ofDeg(45, 45));
    @Test
    void apply() {
        assertEquals(-0.1316524976, stereographicProjection45.apply(HorizontalCoordinates.ofDeg(45,30)).y(),EPSILON);
        assertEquals(Angle.ofDeg(29.05537+180), stereographicProjection45.inverseApply(CartesianCoordinates.of(10,0)).az());
        //assertEquals(stereographicProjection.apply(stereographicProjection.inverseApply(CartesianCoordinates.of(10, 15))).y(), 15);
    }

    @Test
    void circleCenterForParallel() {

    }

    @Test
    void circleRadiusForParallel() {
    }

    @Test
    void applyToAngle() {
        assertEquals(4.363330053e-3,stereographicProjection.applyToAngle(Angle.ofDeg(1/2.0)),EPSILON);
    }


    @Test
    void testToString() {

        assertTrue(stereographicProjection.toString().contains("StereographicProjection"));
        assertTrue(stereographicProjection.toString().contains(((String.valueOf(Angle.ofDeg(23))))));
        assertTrue(stereographicProjection.toString().contains((String.valueOf(Angle.ofDeg(45)))));

    }
}
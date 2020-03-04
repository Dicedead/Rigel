package ch.epfl.rigelTest.coordinates;

import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StereographicProjectionTest {
    StereographicProjection stereographicProjection = new StereographicProjection (HorizontalCoordinates.ofDeg(23, 45));
    @Test
    void apply() {

    }

    @Test
    void circleCenterForParallel() {

    }

    @Test
    void circleRadiusForParallel() {
    }

    @Test
    void applyToAngle() {
    }

    @Test
    void inverseApply() {
    }

    @Test
    void testToString() {

        assertTrue(stereographicProjection.toString().contains("StereographicProjection"));
        assertTrue(stereographicProjection.toString().contains(((String.valueOf(Angle.ofDeg(23))))));
        assertTrue(stereographicProjection.toString().contains((String.valueOf(Angle.ofDeg(45)))));

    }
}
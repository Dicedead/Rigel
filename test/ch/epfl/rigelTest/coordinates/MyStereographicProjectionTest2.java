package ch.epfl.rigelTest.coordinates;

import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MyStereographicProjectionTest2 {
    private final double delta = 1e-12;
    @Test
    void applyWorks(){
        HorizontalCoordinates h1 = HorizontalCoordinates.of(Math.PI/4, Math.PI/6);
        HorizontalCoordinates center1 = HorizontalCoordinates.of(0,0);
        StereographicProjection e = new StereographicProjection(center1);
        double p = Math.sqrt(6);
        CartesianCoordinates a1 = CartesianCoordinates.of(p/(4+p), 2/(4+p));
        CartesianCoordinates c1 = e.apply(h1);
        assertEquals(a1.x(), c1.x(), delta);
        assertEquals(a1.y(), c1.y(), delta);

        HorizontalCoordinates h2 = HorizontalCoordinates.of(Math.PI/2, Math.PI/2);
        HorizontalCoordinates center2 = HorizontalCoordinates.of(Math.PI/4, Math.PI/4);
        StereographicProjection e2 = new StereographicProjection(center2);
        double p2 = Math.sqrt(2);
        CartesianCoordinates a2 = CartesianCoordinates.of(0, p2/(2+p2));
        CartesianCoordinates c2 = e2.apply(h2);
        assertEquals(a2.x(), c2.x(), delta);
        assertEquals(a2.y(), c2.y(), delta);

        double y = (new StereographicProjection(HorizontalCoordinates.ofDeg(45,45))).apply(HorizontalCoordinates.ofDeg(45,30)).y();
        assertEquals( -0.13165249758739583, y);
    }

    @Test
    void circleCenterForParallelWorks(){
        HorizontalCoordinates h1 = HorizontalCoordinates.of(Math.PI/4, Math.PI/6);
        HorizontalCoordinates center1 = HorizontalCoordinates.of(0,0);
        StereographicProjection s = new StereographicProjection(center1);
        CartesianCoordinates a1 = s.circleCenterForParallel(h1);
        assertEquals(0, a1.x(), delta);
        assertEquals(2, a1.y(), delta);

        double y = (new StereographicProjection(HorizontalCoordinates.ofDeg(45,45))).circleCenterForParallel(HorizontalCoordinates.ofDeg(0,27)).y();
        assertEquals(0.6089987400733187,y);
    }

    @Test
    void circleRadiusForParallelWorks(){
        HorizontalCoordinates h2 = HorizontalCoordinates.of(Math.PI/2, Math.PI/2);
        HorizontalCoordinates center2 = HorizontalCoordinates.of(Math.PI/4, Math.PI/4);
        StereographicProjection e2 = new StereographicProjection(center2);
        double rho1 = e2.circleRadiusForParallel(h2);
        assertEquals(0, rho1, delta);

        double c = (new StereographicProjection(HorizontalCoordinates.ofDeg(45,45))).circleRadiusForParallel(HorizontalCoordinates.ofDeg(0,27));
        assertEquals(0.767383180397855,c);
    }

    @Test
    void applyToAngle(){
        double a = (new StereographicProjection (HorizontalCoordinates.ofDeg(23, 45))).applyToAngle(Angle.ofDeg(1/2.0));
        assertEquals(0.00436333005262522,a);
    }

    @Test
    void inverseApplyWorks(){
        HorizontalCoordinates h1 = HorizontalCoordinates.of(Math.PI/4, Math.PI/6);
        HorizontalCoordinates center1 = HorizontalCoordinates.of(0,0);
        StereographicProjection e = new StereographicProjection(center1);
        double p = Math.sqrt(6);
        CartesianCoordinates a1 = CartesianCoordinates.of(p/(4+p), 2/(4+p));
        CartesianCoordinates c1 = e.apply(h1);
        HorizontalCoordinates d1 = e.inverseApply(c1);
        assertEquals(h1.az(), d1.az(), delta);
        assertEquals(h1.alt(), d1.alt(), delta);


        HorizontalCoordinates h2 = HorizontalCoordinates.of(Math.PI/2, (Math.PI/2)-0.1);
        HorizontalCoordinates center2 = HorizontalCoordinates.of(Math.PI/4, Math.PI/4);
        StereographicProjection e2 = new StereographicProjection(center2);
        CartesianCoordinates c2 = e2.apply(h2);
        assertEquals(h2.az(), e2.inverseApply(c2).az(), delta);
        assertEquals(h2.alt(), e2.inverseApply(c2).alt(), delta);

        double az1 = (new StereographicProjection(HorizontalCoordinates.ofDeg(45,45))).inverseApply(CartesianCoordinates.of(10,0)).az();
        double az2 = (new StereographicProjection(HorizontalCoordinates.ofDeg(45,20)).inverseApply(CartesianCoordinates.of(0,25)).az());
        double alt1 = (new StereographicProjection(HorizontalCoordinates.ofDeg(45,20)).inverseApply(CartesianCoordinates.of(0,25)).alt());
        assertEquals(3.648704634091643, az1, delta);
        assertEquals( 3.9269908169872414, az2, delta);
        assertEquals(-0.2691084761522857, alt1, delta);
    }

    @Test
    void equalsThrowsUOE() {
        assertThrows(UnsupportedOperationException.class, () -> {
            CartesianCoordinates coordonnees= CartesianCoordinates.of(9,0);
            coordonnees.equals(coordonnees);
        });
    }

    @Test
    void hashCodeThrowsUOE() {
        assertThrows(UnsupportedOperationException.class, () -> {
            CartesianCoordinates coordinates = CartesianCoordinates.of(0, 75);
            coordinates.hashCode();
        });
    }
}

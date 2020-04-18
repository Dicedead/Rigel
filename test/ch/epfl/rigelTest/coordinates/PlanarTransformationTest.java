package ch.epfl.rigelTest.coordinates;

import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.PlanarTransformation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class PlanarTransformationTest {

    @Test
    void inverseOf() {
        PlanarTransformation lul = PlanarTransformation.of(3,5,4.14,Math.PI, 350,250);
        PlanarTransformation lulInv = lul.invert();
        CartesianCoordinates input = CartesianCoordinates.of(149, Math.exp(14/149d));
        assertEquals(149, lulInv.apply(lul.apply(input)).x(), 1e-8);
        assertEquals(Math.exp(14/149d), lulInv.apply(lul.apply(input)).y(), 1e-8);
    }

    @Test
    void determinant() {
        assertEquals(0, PlanarTransformation.of(2,4,5,10,5925,-25).getDeterminant());
        assertThrows(IllegalArgumentException.class, () -> PlanarTransformation.of(2,4,5,10,5925,-25).invert());
    }

    @Test
    void applyDistance() {
        PlanarTransformation lul = PlanarTransformation.ofDilatAndTrans(500, 8, 13);
        assertEquals(1000, lul.applyDistance(2));
    }
}
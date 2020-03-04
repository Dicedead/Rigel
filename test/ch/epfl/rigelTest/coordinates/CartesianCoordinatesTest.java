package ch.epfl.rigelTest.coordinates;

import ch.epfl.rigel.coordinates.CartesianCoordinates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartesianCoordinatesTest {

    private static final CartesianCoordinates cartes = CartesianCoordinates.of(10.59258,5.614905);

    @Test
    void of() {
        assertEquals(10.59258,cartes.x());
        assertEquals(5.614905,cartes.y());
    }

    @Test
    void testToString() {
        assertEquals("CartesianCoordinates : (10.59258 ; 5.614905)",cartes.toString());
    }

    @Test
    void testEqualsAndHashcode() {
        assertThrows(UnsupportedOperationException.class, () -> {
            cartes.equals(cartes);
        });
        assertThrows(UnsupportedOperationException.class, cartes::hashCode);
    }

}
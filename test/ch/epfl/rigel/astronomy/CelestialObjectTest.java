package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CelestialObjectTest {

    private final static Planet validPlanet = new Planet("lul", EquatorialCoordinates.of(0,0),4f,5f);
    private final static Planet validPlanet2 = new Planet("lul", EquatorialCoordinates.of(0,0),4.52f,5.56f);


    @Test
    void constructorThrows() {
        //Negative angular size
        assertThrows(IllegalArgumentException.class, () -> {
            new Planet("lul", EquatorialCoordinates.of(0,0),-4f,5f);
        });

        //Null String
        String lol = null;
        assertThrows(NullPointerException.class, () -> {
            new Planet(lol, EquatorialCoordinates.of(0,0),4f,5f);
        });

        //Null Equatorial pos
        EquatorialCoordinates equatorialCoord = null;
        assertThrows(NullPointerException.class, () -> {
            new Planet("lul", equatorialCoord,4f,5f);
        });
    }

    @Test
    void getterTests(){
        assertEquals(EquatorialCoordinates.of(0,0).ra(), validPlanet2.equatorialPos().ra());
        assertEquals(EquatorialCoordinates.of(0,0).dec(),validPlanet.equatorialPos().dec());
        assertEquals("lul",validPlanet.name());
        assertEquals("lul",validPlanet2.info());
        assertEquals(5, validPlanet.magnitude());
        assertEquals(4, validPlanet.angularSize());

        //Double values:
        assertEquals(4.52,validPlanet2.angularSize());
        assertEquals(5.56,validPlanet2.magnitude());
    }

}
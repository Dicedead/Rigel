package ch.epfl.rigelTest.astronomy;

import ch.epfl.rigel.astronomy.Planet;
import ch.epfl.rigel.astronomy.PlanetModel;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static ch.epfl.rigel.astronomy.Epoch.J2010;
import static org.junit.jupiter.api.Assertions.*;

class MyPlanetModelTest {

    private final static double EPSILON = 1e-2;

    @Test
    void atOuterPlanet() {
        ZonedDateTime D = ZonedDateTime.of(2003,11,22,0,0,0,0, ZoneOffset.UTC);
        double days22Nov = J2010.daysUntil(D);

        assertEquals(Angle.ofDMS(6,21,25), PlanetModel.JUPITER.at(days22Nov,
                new EclipticToEquatorialConversion(D)).equatorialPos().dec(),EPSILON);
        assertEquals(Angle.ofHr(11+11/60.0+14/3600.0), PlanetModel.JUPITER.at(days22Nov,
                new EclipticToEquatorialConversion(D)).equatorialPos().ra(),EPSILON);

        Planet jupTest = PlanetModel.JUPITER.at(days22Nov,
                new EclipticToEquatorialConversion(D));
        assertEquals(Angle.ofArcsec(35.1),jupTest.angularSize(),EPSILON);
        assertEquals(-2,jupTest.magnitude(),EPSILON*10);
    }

    @Test
    void atInnerPlanet() {
        ZonedDateTime D = ZonedDateTime.of(2003,11,22,0,0,0,0, ZoneOffset.UTC);
        double days22Nov = J2010.daysUntil(D);

        assertEquals(Angle.ofDMS(-1 *24,30,9),PlanetModel.MERCURY.at(days22Nov,
                new EclipticToEquatorialConversion(D)).equatorialPos().dec(),EPSILON);
        assertEquals(Angle.ofHr(16+49/60.0 +12/3600.0),PlanetModel.MERCURY.at(days22Nov,
                new EclipticToEquatorialConversion(D)).equatorialPos().ra(),EPSILON);
    }
}
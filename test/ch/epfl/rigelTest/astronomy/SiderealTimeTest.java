package ch.epfl.rigelTest.astronomy;

import ch.epfl.rigel.astronomy.SiderealTime;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SiderealTimeTest {

    private final static double EPSILON = 1e-3;

    @Test
    void greenwich() {
        ZonedDateTime date = ZonedDateTime.of(1980,4,22,14,36,51,27
                , ZoneOffset.UTC);
        assertEquals(Angle.normalizePositive(Angle.ofHr(4+40/60.0+5.23/3600.0)), SiderealTime.greenwich(date),EPSILON);

        assertEquals( 1.9883078130455532,SiderealTime.greenwich(ZonedDateTime.of(2001,9,11,8,14,0,0, ZoneId.of("UTC"))),EPSILON);

    }

    @Test
    void local() {
        GeographicCoordinates geoCoords = GeographicCoordinates.ofDeg(30,45);
        ZonedDateTime date = ZonedDateTime.of(1980,4,22,14,36,51,27
                , ZoneId.of("GMT"));
        assertEquals(Math.PI/6 + Angle.normalizePositive(Angle.ofHr(4+40/60.0+5.23/3600.0)),
                SiderealTime.local(date,geoCoords),EPSILON);
        assertEquals(geoCoords.lon() + SiderealTime.greenwich(date),
                SiderealTime.local(date,geoCoords));
    }
}
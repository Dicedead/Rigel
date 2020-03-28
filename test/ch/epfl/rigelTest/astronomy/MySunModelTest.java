package ch.epfl.rigelTest.astronomy;

import ch.epfl.rigel.astronomy.Epoch;
import ch.epfl.rigel.astronomy.Sun;
import ch.epfl.rigel.astronomy.SunModel;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigelTest.math.UsefulMathTestingMethods;
import ch.epfl.rigelTest.math.UsefulMathTestingMethods.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static ch.epfl.rigel.astronomy.Epoch.J2010;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MySunModelTest {
    private final static double delta = 1e-14;
    private final static double delta2 = 1e-4;
    private final static double delta3 = 1e-5;

    @Test
    void at() {

        ZonedDateTime time = ZonedDateTime.of(
                LocalDate.of(2003,7,27),
                LocalTime.of(0,0),
                ZoneOffset.UTC
        );
        assertEquals(201.159131,Angle.toDeg(Angle.normalizePositive(SunModel.SUN.at(
                J2010.daysUntil(time), new EclipticToEquatorialConversion(time)
        ).meanAnomaly())),1e-3); //Normalised!
        assertEquals(UsefulMathTestingMethods.hoursFromHMS(8,23,34), SunModel.SUN.at(
                J2010.daysUntil(time), new EclipticToEquatorialConversion(time)
        ).equatorialPos().raHr(), delta2);
        assertEquals(Angle.ofDMS(0, 31, 30), SunModel.SUN.at(
                J2010.daysUntil(time), new EclipticToEquatorialConversion(time)
        ).angularSize(),delta3);
        EquatorialCoordinates eq1 = SunModel.SUN.at(-2349, new EclipticToEquatorialConversion(ZonedDateTime.of(LocalDate.of(2003, Month.JULY,
                27), LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC))).equatorialPos();
        assertEquals(8.392682808297808, eq1.raHr(), delta);
        assertEquals(19.35288373097352, eq1.decDeg());
        assertEquals(5.9325494700300885,SunModel.SUN.at(27 + 31, new EclipticToEquatorialConversion(ZonedDateTime.of(LocalDate.of(2010,  Month.FEBRUARY, 27),LocalTime.of(0,0), ZoneOffset.UTC))).equatorialPos().ra());

        ZonedDateTime zdt = ZonedDateTime.of(LocalDate.of(1988, Month.JULY, 27), LocalTime.of(0, 0), ZoneOffset.UTC);
        assertEquals(0.3353207024580374,SunModel.SUN.at(Epoch.J2010.daysUntil(zdt), new EclipticToEquatorialConversion(zdt)).equatorialPos().dec());

        ZonedDateTime zone1988 = ZonedDateTime.of(
                LocalDate.of(1988,Month.JULY,27),
                LocalTime.of(0,0),ZoneOffset.UTC
        );
        assertEquals(Angle.ofDMS(0,31,30),SunModel.SUN.at(J2010.daysUntil(zone1988),new EclipticToEquatorialConversion(zone1988)).angularSize(), Angle.ofDMS(0,0,0.5));

    }
}
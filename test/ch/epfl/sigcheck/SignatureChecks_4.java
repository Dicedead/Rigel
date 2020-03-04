package ch.epfl.sigcheck;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.*;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.function.Function;

final class SignatureChecks_4 {

    @Test
    void checkCartesianCoordinates() {
        double d = 0;
        CartesianCoordinates c = CartesianCoordinates.of(d, d);
        d = c.x();
        d = c.y();
    }

    @Test
    void checkStereographicProjection() {
        HorizontalCoordinates h = null;
        StereographicProjection s;
        CartesianCoordinates c;
        double d;
        s = new StereographicProjection(h);
        c = s.circleCenterForParallel(h);
        d = s.circleRadiusForParallel(h);
        d = s.applyToAngle(d);
        c = s.apply(h);
        h = s.inverseApply(c);
    }

    @Test
    void checkCelestialObject() {
        CelestialObject c = null;
        String s;
        double d;
        EquatorialCoordinates e;
        s = c.name();
        d = c.angularSize();
        d = c.magnitude();
        e = c.equatorialPos();
        s = c.info();
    }

    @Test
    void checkSun() {
        CelestialObject c;
        Sun s;
        EclipticCoordinates e = null;
        EquatorialCoordinates q = null;
        float f = 0f;
        double d;
        s = new Sun(e, q, f, f);
        e = s.eclipticPos();
        d = s.meanAnomaly();
        c = s;
    }

    @Test
    void checkMoon() {
        CelestialObject c;
        Moon m;
        EquatorialCoordinates e = null;
        float f = 0f;
        m = new Moon(e, f, f, f);
        c = m;
    }

    @Test
    void checkPlanet() {
        CelestialObject c;
        Planet m;
        EquatorialCoordinates e = null;
        String s = null;
        float f = 0f;
        m = new Planet(s, e, f, f);
        c = m;
    }
}
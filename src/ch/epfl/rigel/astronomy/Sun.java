package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.util.Objects;

/**
 * Sun modeled as an implementation of a CelestialObject
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Sun extends CelestialObject {

    private final static String NAME = "Soleil";
    private final static float magnitude = 26.7f;
    private final EclipticCoordinates eclipticPos;
    private final float meanAnomaly;

    Sun(EclipticCoordinates eclipticPos, EquatorialCoordinates equatorialPos, float angularSize, float meanAnomaly) {
        super(NAME, equatorialPos, angularSize, magnitude);

        this.eclipticPos = Objects.requireNonNull(eclipticPos);
        this.meanAnomaly = meanAnomaly;
    }

    public EclipticCoordinates eclipticPos(){ return eclipticPos; }

    public double meanAnomaly(){ return meanAnomaly; }
}

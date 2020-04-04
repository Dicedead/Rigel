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
    private final static float MAGNITUDE = -26.7f;
    private final EclipticCoordinates eclipticPos;
    private final float meanAnomaly;

    /**
     * Constructor for the Sun at a given time
     *
     * @param eclipticPos   (EclipticCoordinates) object's ecliptic coordinates
     * @param equatorialPos (EquatorialCoordinates) object's equatorial coordinates
     * @param angularSize   (float) object's angular size
     * @param meanAnomaly   (float) object's mean anomaly
     * @throws IllegalArgumentException if angularSize < 0
     * @throws NullPointerException     if name, eclipticPos or equatorialPos are null
     */
    public Sun(EclipticCoordinates eclipticPos, EquatorialCoordinates equatorialPos, float angularSize, float meanAnomaly) {
        super(NAME, equatorialPos, angularSize, MAGNITUDE);

        this.eclipticPos = Objects.requireNonNull(eclipticPos);
        this.meanAnomaly = meanAnomaly;
    }

    /**
     * @return (EclipticCoordinates) Sun's ecliptic coordinates
     */
    public EclipticCoordinates eclipticPos() {
        return eclipticPos;
    }

    /**
     * @return (double) Sun's current mean anomaly
     */
    public double meanAnomaly() {
        return meanAnomaly;
    }
}

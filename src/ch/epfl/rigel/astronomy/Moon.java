package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.ClosedInterval;

import java.util.Locale;

/**
 * Moon modeled as an implementation of a CelestialObject
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Moon extends CelestialObject {

    private final static String NAME = "Lune";
    private final float phase;
    private final static ClosedInterval INTERVAL_01 = ClosedInterval.of(0,1);

    public Moon(EquatorialCoordinates equatorialPos, float angularSize, float magnitude, float phase) {
        super(NAME, equatorialPos, angularSize, magnitude);
        Preconditions.checkInInterval(INTERVAL_01, phase);

        this.phase = phase;
    }

    @Override
    public String info() {
        return String.format(Locale.ROOT,"Lune (%.1f%%)",phase*100);
    }

}

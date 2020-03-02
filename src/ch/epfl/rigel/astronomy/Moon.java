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

    /**
     * Moon constructor
     *
     * @param equatorialPos (EquatorialCoordinates) object's coordinates
     * @param angularSize (float) object's angular size
     * @param magnitude (float) object's apparent magnitude
     * @param phase (float) object's phase in interval [0,1]
     * @throws IllegalArgumentException if angularSize < 0 or phase not in [0,1]
     * @throws NullPointerException if name or equatorialPos are null
     */
    public Moon(EquatorialCoordinates equatorialPos, float angularSize, float magnitude, float phase) {
        super(NAME, equatorialPos, angularSize, magnitude);
        Preconditions.checkInInterval(INTERVAL_01, phase);
        /*Note that it is not beneficial to use the syntax:
               this.phase = Preconditions.checkInInterval(INTERVAL_01, phase);
          as the latter returns a double and not a float.*/
        this.phase = phase;
    }

    
    /**
     * @return Moon's name and its phase percentage
     */
    @Override
    public String info() {
        return String.format(Locale.ROOT,"Lune (%.1f%%)",phase*100);
    }

}

package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.ClosedInterval;

/**
 * Representing a Star object
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Star extends CelestialObject {

    private final int hipparcosId;
    private final int colorTemperature;

    private final static ClosedInterval COLOR_INTERVAL = ClosedInterval.of(-0.5,5.5);

    /**
     * @param hipparcosId (int) Star's identification integer
     * @param name (String) Star's name
     * @param equatorialPos (EquatorialPos) Star's position
     * @param magnitude (float)
     * @param colorIndex (float)
     * @throws IllegalArgumentException if hipparcosId < 0 or colorIndex not in [-0.5;5.5]
     */
    public Star(int hipparcosId, String name, EquatorialCoordinates equatorialPos, float magnitude, float colorIndex) {
        super(name, equatorialPos, 0, magnitude);

        Preconditions.checkArgument(hipparcosId >= 0);
        Preconditions.checkInInterval(COLOR_INTERVAL, colorIndex);

        this.hipparcosId = hipparcosId;
        this.colorTemperature = (int)Math.floor(4600*(1/(0.92*colorIndex + 1.7) + 1/(0.92*colorIndex + 0.62)));
    }

    /***
     * @return (int) Star's identification number
     */
    public int hipparcosId() {
        return this.hipparcosId;
    }

    /**
     * @return (int) Star's temperature in Kelvin (computed thanks to its color)
     */
    public int colorTemperature() {
        return this.colorTemperature;
    }
}

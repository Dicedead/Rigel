package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

/**
 * Representing a Star object
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Star extends CelestialObject{
    public Star(int hipparcosId, String name, EquatorialCoordinates equatorialPos, float magnitude, float colorIndex)
    {
        super(name, equatorialPos, magnitude, 0);
    }
    public int hipparcosId(){
        return 0;
    };
    public int colorTemperature(){
        return 0;
    };
}

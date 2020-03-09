package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

public final class Star extends CelestialObject{
    public Star(int hipparcosId, String name, EquatorialCoordinates equatorialPos, float magnitude, float colorIndex)
    {
        super(name, equatorialPos, );
    }
    public int hipparcosId(){
        return 0;
    };
    public int colorTemperature(){
        return 0;
    };
}

package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

/**
 * Planet modeled as an implementation of a CelestialObject
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Planet extends CelestialObject {

    /**
     * Planet constructor
     *
     * @param name          (String) object's identification
     * @param equatorialPos (EquatorialCoordinates) object's coordinates
     * @param angularSize   (float) object's angular size
     * @param magnitude     (float) object's apparent magnitude
     * @throws IllegalArgumentException if angularSize < 0
     * @throws NullPointerException     if name or equatorialPos are null
     */
    public Planet(String name, EquatorialCoordinates equatorialPos, float angularSize, float magnitude) {
        super(name, equatorialPos, angularSize, magnitude);
    }
}

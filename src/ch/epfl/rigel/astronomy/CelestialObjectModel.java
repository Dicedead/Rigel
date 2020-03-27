package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;

/**
 * Abstraction of a mathematical model for computing properties of celestial objects at given point and time
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public interface CelestialObjectModel<O> {
    public abstract O at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion);
}
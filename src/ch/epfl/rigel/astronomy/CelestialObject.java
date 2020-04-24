package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.util.Objects;

/**
 * Abstract representation of a celestial object
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public abstract class CelestialObject {

    private final String name;
    private final EquatorialCoordinates equatorialPos;
    private final float angularSize;
    private final float magnitude;

    /**
     * Celestial Object constructor
     *
     * @param name          (String) object's identification
     * @param equatorialPos (EquatorialCoordinates) object's coordinates
     * @param angularSize   (float) object's angular size
     * @param magnitude     (float) object's apparent magnitude
     * @throws IllegalArgumentException if angularSize < 0
     * @throws NullPointerException     if name or equatorialPos are null
     */
    public CelestialObject(String name, EquatorialCoordinates equatorialPos, float angularSize, float magnitude) {
        Preconditions.checkArgument(angularSize >= 0);
        this.name = Objects.requireNonNull(name);
        this.equatorialPos = Objects.requireNonNull(equatorialPos);
        this.angularSize = angularSize;
        this.magnitude = magnitude;
    }

    /**
     * @return (String) Object's name
     */
    public String name() {
        return name;
    }

    /**
     * @return (double) Object's angular size
     */
    public double angularSize() {
        return angularSize;
    }

    /**
     * @return (double) Object's apparent magnitude
     */
    public double magnitude() {
        return magnitude;
    }

    /**
     * @return (EquatorialCoordinates) Object's equatorial coordinates
     */
    public EquatorialCoordinates equatorialPos() {
        return equatorialPos;
    }

    /**
     * @return (String) Information on the object (by default: its name)
     */
    public String info() {
        return name();
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return info();
    }
}

package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.Asterism;
import ch.epfl.rigel.astronomy.Moon;
import ch.epfl.rigel.astronomy.Planet;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.astronomy.Sun;

/**
 * Simple enum used to mask / draw objects, also suggests an order for drawing them (use EnumSet)
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public enum DrawableObjects {
    ORBIT(null, "Orbites"),
    ASTERISMS(Asterism.class, "Astérismes"),
    GRID(null, "Grille"),
    STARS(Star.class, "Etoiles"),
    PLANETS(Planet.class, "Planètes"),
    SUN(Sun.class, "Soleil "),
    MOON(Moon.class, "Lune"),
    HORIZON(null, "Horizon");

    private final Class<?> reppedClass;

    private final String name;

    DrawableObjects(Class<?> representedClass, String newName) {
        reppedClass = representedClass;
        name = newName;
    }

    public Class<?> getCorrespondingClass() {
        return reppedClass;
    }

    public String getName() { return name; }

    /**
     * @param s (String)
     * @return (DrawableObjects) the drawable such that its name equals given string
     * @throws IllegalArgumentException if given string is not a drawable's name
     */
    public static DrawableObjects getDrawableFromString(String s) {
        for(DrawableObjects drawable : DrawableObjects.values()) {
            if (drawable.name.equals(s)) {
                return drawable;
            }
        }
        throw new IllegalArgumentException("Fatal error (DrawableObjects): Given string is not a DrawableObject's name.");
    }
}

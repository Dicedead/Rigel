package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.Asterism;
import ch.epfl.rigel.astronomy.Moon;
import ch.epfl.rigel.astronomy.Planet;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.astronomy.Sun;

/**
 * Simple enum used to mask / draw objects, also controls the order of drawing these objects in SkyCanvasPainter
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public enum DrawableObjects {
    ASTERISMS(Asterism.class),
    GRID(null),
    STARS(Star.class),
    PLANETS(Planet.class),
    SUN(Sun.class),
    MOON(Moon.class),
    HORIZON(null);

    private final Class<?> reppedClass;

    DrawableObjects(Class<?> representedClass) {
        reppedClass = representedClass;
    }

    public Class<?> getCorrespondingClass() {
        return reppedClass;
    }
}

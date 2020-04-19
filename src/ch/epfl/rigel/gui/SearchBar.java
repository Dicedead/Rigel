package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.Moon;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.Planet;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.astronomy.Sun;
import ch.epfl.rigel.math.graphs.Tree;

import java.util.List;

/**
 * A tool to search through celestial objects
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class SearchBar {

    private final ObservedSky sky;

    public SearchBar(final ObservedSky sky) {
        this.sky = sky;
    }

    public enum Filters {
        SOLAR_SYSTEM(List.of(Moon.class, Sun.class, Planet.class)),
        STARS(List.of(Star.class)),
        NONE(List.of(Moon.class, Sun.class, Planet.class, Star.class));

        private final List<Class<? extends CelestialObject>> classList;

        private Filters(List<Class<? extends CelestialObject>> classList) {
            this.classList = classList;
        }


    }

    public enum SearchBy {
        NAME, HIPPARCOS, MAGNITUDE, COORDS, NONE
    }

    private Tree<CelestialObject> huffmanCode() {
        return null;
    }

    /*
    Jpensais à un bonus possible implémentable avec un Tree:
Un moteur de recheche parmi les objets célestes qui emploie leur name par ex, seulement, t'as pas besoin de mettre
le nom complet de l'objet céleste pour le trouver (sinon bien sûr une map suffirait):
À chaque caractère ajouté, ça affiche une nouvelle liste de recommandations, qui devient du coup de + en + petite
On peut ensuite cliquer sur une des propositions pour recentrer la projection sur l'objet choisi

Bien sûr on peut faire des filtres du genre stars only, planets only, chercher par hipparcos, etc

L'intérêt des Trees: Huffman + avec les subtrees ce sera facile de get leur data pour les recommandations
     */
}

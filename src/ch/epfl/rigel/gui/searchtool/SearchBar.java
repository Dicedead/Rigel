package ch.epfl.rigel.gui.searchtool;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.Moon;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.Planet;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.astronomy.Sun;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A tool to search through celestial objects
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class SearchBar {

    private ObservedSky cachedSky;
    private final Map<String, Set<CelestialObject>> initialsMap;
    private final Map<String, Set<Star>> hipparcosMap;
    private boolean initOngoing;

    private Set<CelestialObject> ongoingSearchSet;
    private String ongoingString;

    /**
     * SearchBar constructor
     *
     * @param sky (ObservedSky) current ObservedSky
     */
    public SearchBar(final ObservedSky sky){

        this.initOngoing = true;
        this.cachedSky = sky;
        this.initialsMap = IntStream.rangeClosed('A', 'Z').boxed()
                .collect(Collectors.toMap(
                        String::valueOf, HashSet::new, (u,v) -> u, HashMap::new
                ));
        this.hipparcosMap = IntStream.range(0, 10).boxed()
                .collect(Collectors.toMap(
                        String::valueOf, HashSet::new, (u,v) -> u, HashMap::new
                ));
        updateWithSky(sky);
        this.initOngoing = false;
    }

    /**
     * Called before a search, skipped if sky's contents have not changed since last search, otherwise,
     * refreshes the sets to search in
     *
     * @param sky (ObservedSky) potentially new sky
     */
    private void updateWithSky(final ObservedSky sky) {
        if (sky.celestialObjMap().keySet().equals(cachedSky.celestialObjMap().keySet()) && !initOngoing) {
            cachedSky = sky;
            return;
        }
        initialsMap.values().forEach(Set::clear);
        hipparcosMap.values().forEach(Set::clear);
        sky.celestialObjMap().keySet().forEach(
                celestObj -> {
                    final String initial = String.valueOf(celestObj.name().charAt(0));
                    if (!initialsMap.containsKey(initial)) {
                        initialsMap.put(initial, new HashSet<>());
                    }
                    initialsMap.get(initial).add(celestObj);
                    
                    if (celestObj instanceof Star) {
                        final String firstDigit = String.valueOf(String.valueOf(((Star) celestObj).hipparcosId()).charAt(0));

                        if (!hipparcosMap.containsKey(firstDigit)) {
                            hipparcosMap.put(firstDigit, new HashSet<>());
                        }
                        hipparcosMap.get(firstDigit).add((Star) celestObj);
                    }
                }
        );
        cachedSky = sky;
    }

    /**
     * Main method returning the set of possible celestial objects corresponding to given search parameters
     *
     * @param inputString (String)
     * @param inputFilter (Filters) classes of CelestialObject to search in
     * @param type (SearchBy) search either by hipparcos or name
     * @param sky (ObservedSky) observed sky at moment of search
     * @return (Set<CelestialObject>) said set
     */
    public Set<CelestialObject> search(final String inputString, final Filters inputFilter, final SearchBy type,
                                       final ObservedSky sky) {

        updateWithSky(sky);
        final Filters filter = (type == SearchBy.HIPPARCOS) ? Filters.STARS : inputFilter;

        if (inputString.length() == 1) {
            return (!initialsMap.containsKey(inputString))? Set.of() : search(inputString, Set.copyOf(
                    (filter == Filters.STARS) ? hipparcosMap.get(inputString) : initialsMap.get(inputString)),
                    filter, type);
        }
        return Collections.unmodifiableSet(
                search(inputString, (ongoingString.length() < inputString.length()) ? ongoingSearchSet :
                Set.copyOf((filter == Filters.STARS) ? hipparcosMap.get(inputString.substring(0,2)) :
                        initialsMap.get(inputString.substring(0,2))), filter, type));
    }

    /**
     * Gives the set of next possible string inputs according to following parameters:
     *
     * @param inputString (String) string entry
     * @param type (SearchBy) search either by hipparcos or name
     * @return (Set<String>) said set
     */
    public Set<String> suggestions(final String inputString, final SearchBy type) {
        return ongoingSearchSet.stream()
                .filter(celestObj -> type.stringFunction.apply(celestObj).length() > inputString.length())
                .map(celestObj -> inputString + type.stringFunction.apply(celestObj).charAt(inputString.length()))
                .collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * To be called once a search is done.
     */
    public void endSearch() {
        ongoingString = "";
        ongoingSearchSet.clear();
    }

    private Set<CelestialObject> search(final String inputString, final Set<? extends CelestialObject> searchSet,
                                        final Filters filter, final SearchBy type) {
        ongoingString = inputString;
        ongoingSearchSet = searchSet.stream()
                .filter(celestObj -> filter.classList.contains(celestObj.getClass())
                        && type.stringFunction.apply(celestObj).startsWith(inputString))
                .collect(Collectors.toCollection(HashSet::new));
        return ongoingSearchSet;
    }

    /**
     * Defines types of celestial objects to search
     */
    public enum Filters {

        SOLAR_SYSTEM(List.of(Moon.class, Sun.class, Planet.class)),
        STARS(List.of(Star.class)),
        ALL(List.of(Moon.class, Sun.class, Planet.class, Star.class));

        private final List<Class<? extends CelestialObject>> classList;

        Filters(List<Class<? extends CelestialObject>> classList) {
            this.classList = classList;
        }

        private List<Class<? extends CelestialObject>> classList() {
            return classList;
        }
    }

    /**
     * Defines the search criterion
     */
    public enum SearchBy {
        NAME(CelestialObject::name),
        HIPPARCOS(celestObj -> (!(celestObj instanceof Star)) ?
                "" : String.valueOf(((Star) celestObj).hipparcosId()));

        private final Function<CelestialObject, String> stringFunction;

        SearchBy(final Function<CelestialObject, String> stringGetter) {
            this.stringFunction = stringGetter;
        }
    }
}

package ch.epfl.rigel.gui.bonus;

import ch.epfl.rigel.Preconditions;
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

    private ObservedSky savedSky;
    private final Map<String, Set<CelestialObject>> initialsMap;
    private final Map<String, Set<Star>> hipparcosMap;
    private boolean initOngoing;

    private Set<CelestialObject> ongoingSearchSet;
    private String ongoingString;

    public SearchBar(final ObservedSky sky){

        this.initOngoing = true;
        this.savedSky = sky;
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

    public void updateWithSky(final ObservedSky sky) {
        if (sky.celestialObjMap().keySet().equals(savedSky.celestialObjMap().keySet()) && !initOngoing) {
            savedSky = sky;
            return;
        }
        initialsMap.values().forEach(Set::clear);
        hipparcosMap.values().forEach(Set::clear);
        sky.celestialObjMap().keySet().forEach(
                celestObj -> {
                    final String initial = String.valueOf(celestObj.name().charAt(0));
                    if (initialsMap.containsKey(initial)) {
                        initialsMap.get(initial).add(celestObj);
                    } else {
                        initialsMap.put(initial, new HashSet<>());
                    }
                    
                    if (celestObj instanceof Star) {
                       // hipparcosMap.get(String.valueOf(((Star) celestObj).hipparcosId())).add((Star) celestObj);
                    }
                }
        );
        savedSky = sky;
    }

    public Set<CelestialObject> search(final String inputString, final Filters filter, final SearchBy type) {
        Preconditions.checkArgument(type != SearchBy.HIPPARCOS || filter == Filters.STARS);
        if (inputString.length() == 1) {
            return (!initialsMap.containsKey(inputString))? Set.of() : search(inputString, Set.copyOf(
                    (filter == Filters.STARS) ? hipparcosMap.get(inputString) : initialsMap.get(inputString)),
                    filter, type);
        }
        return search(inputString, (ongoingString.length() < inputString.length()) ? ongoingSearchSet :
                Set.copyOf((filter == Filters.STARS) ? hipparcosMap.get(inputString) :
                        initialsMap.get(inputString.substring(0,2))), filter, type);
    }

    public Set<String> suggestions(final String inputString, final SearchBy type) {
        return ongoingSearchSet.stream()
                .filter(celestObj -> type.stringFunction.apply(celestObj).length() > inputString.length())
                .map(celestObj -> inputString + type.stringFunction.apply(celestObj).charAt(inputString.length()))
                .collect(Collectors.toCollection(HashSet::new));
    }

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
        return Collections.unmodifiableSet(ongoingSearchSet);
    }

    public enum Filters {

        SOLAR_SYSTEM(List.of(Moon.class, Sun.class, Planet.class)),
        STARS(List.of(Star.class)),
        ALL(List.of(Moon.class, Sun.class, Planet.class, Star.class));

        private final List<Class<? extends CelestialObject>> classList;

        Filters(List<Class<? extends CelestialObject>> classList) {
            this.classList = classList;
        }
        public List<Class<? extends CelestialObject>> classList() {
            return classList;
        }
    }


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

package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;

import java.util.*;

public final class StarCatalogue {

    private final List<Star> stars;
    private final Map<Asterism, List<Integer>> map;

    StarCatalogue(List<Star> stars, List<Asterism> asterisms)
    {

        this.stars = stars;
        map = new HashMap<Asterism, List<Integer>>();
    }

    public List<Star> stars()
    {
        return stars;
    }

    public Set<Asterism> asterisms()
    {
        return map.keySet();
    }

    public List<Integer> asterismIndices(Asterism asterism)
    {
        Preconditions.checkArgument(map.keySet().contains(asterism));
        return map.get(asterism);
    }
}

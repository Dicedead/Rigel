package ch.epfl.rigelTest.astronomy;

import ch.epfl.rigel.astronomy.Asterism;
import ch.epfl.rigel.astronomy.Star;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MyAsterismTest {

    @Test
    void stars() {
        Asterism a = new Asterism(new ArrayList<Star>(Collections.singleton(MyStarTest.Rigel)));
        assertEquals( new ArrayList<Star>(Collections.singleton(MyStarTest.Rigel)), a.stars());
    }

    @Test
    void construcThrowsOnEmptyList() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Asterism(List.of());
        });
    }
}
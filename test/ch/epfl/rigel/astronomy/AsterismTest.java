package ch.epfl.rigel.astronomy;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AsterismTest {

    @Test
    void stars() {
        Asterism a = new Asterism(new ArrayList<Star>(Collections.singleton(StarTest.Rigel)));
        assertEquals( new ArrayList<Star>(Collections.singleton(StarTest.Rigel)), a.stars());
    }
}
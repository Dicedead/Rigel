package ch.epfl.rigelTest.gui;

import ch.epfl.rigel.gui.BlackBodyColor;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BlackBodyColorTest {

    @Test
    void colorForTemperatureWorks() {
        assertEquals(Color.rgb(0xc7,0xd8, 0xff), BlackBodyColor.colorForTemperature(10634));
        assertEquals(Color.rgb(0xb3,0xcc,0xff), BlackBodyColor.colorForTemperature(14849.149));
        assertEquals(Color.web("#9bbcff"), BlackBodyColor.colorForTemperature(39988.149));
        assertEquals(Color.web("#ff3800"), BlackBodyColor.colorForTemperature(1001.000149));
    }
}

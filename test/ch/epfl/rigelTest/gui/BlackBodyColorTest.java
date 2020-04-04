package ch.epfl.rigelTest.gui;

import ch.epfl.rigel.gui.BlackBodyColor;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BlackBodyColorTest {
    @Test
    void colorForTemperatureWorks() {
        long time0 = System.nanoTime();
        assertEquals(Color.rgb(0xb3,0xcc,0xff), BlackBodyColor.colorForTemperature(14849.149));
        System.out.println(System.nanoTime() - time0);

        long time1 = System.nanoTime();
        assertEquals(Color.web("#ffcc99"), BlackBodyColor.colorForTemperature(3798.1409));
        System.out.println(System.nanoTime() - time1);

        assertEquals(Color.web("#9bbcff"), BlackBodyColor.colorForTemperature(39988.149));
        assertEquals(Color.web("#ff3800"), BlackBodyColor.colorForTemperature(1001.000149));
        assertThrows(IllegalArgumentException.class, ()-> {
            BlackBodyColor.colorForTemperature(40_000.00000000001d);
        });
        assertThrows(IllegalArgumentException.class, ()-> {
            BlackBodyColor.colorForTemperature(1000d - 0.00000000001d);
        });
    }
}

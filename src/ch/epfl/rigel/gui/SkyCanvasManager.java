package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.StarCatalogue;
import javafx.beans.property.ObjectProperty;

/**
 * Draws the sky on canvas continuously
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class SkyCanvasManager {

    private final StarCatalogue catalogue;
    private final DateTimeBean dtBean;
    private final ViewingParametersBean viewBean;
    private final ObserverLocationBean obsLocBean;

    public SkyCanvasManager(final StarCatalogue catalogue, final DateTimeBean dtBean,
           final ObserverLocationBean obsLocBean, final ViewingParametersBean viewBean) {
        this.catalogue = catalogue;
        this.dtBean = dtBean;
        this.viewBean = viewBean;
        this.obsLocBean = obsLocBean;
    }
}

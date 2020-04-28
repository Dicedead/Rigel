package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Observable visualization parameters class
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class ViewingParametersBean {

    private final DoubleProperty fieldOfViewDeg = new SimpleDoubleProperty();
    private final ObjectProperty<HorizontalCoordinates> center = new SimpleObjectProperty<>();

    /**
     * Setter for observable: field of view property
     *
     * @param newFieldOfViewDeg (double) fov to be set to in degrees
     */
    public void setFieldOfViewDeg(final double newFieldOfViewDeg) {
        fieldOfViewDeg.set(newFieldOfViewDeg);
    }

    /**
     * Setter for observable: center property
     *
     * @param newCenter (HorizontalCoordinates) coordinates to be set to
     */
    public void setCenter(final HorizontalCoordinates newCenter) {
        center.set(newCenter);
    }

    /**
     * @return (double) value of observable: field of view
     */
    public double getFieldOfViewDeg() {
        return fieldOfViewDeg.get();
    }

    /**
     * @return (HorizontalCoordinates) value of observable: center
     */
    public HorizontalCoordinates getCenter() {
        return center.get();
    }

    /**
     * @return (DoubleProperty) observable: field of view property
     */
    public DoubleProperty fieldOfViewDegProperty() {
        return fieldOfViewDeg;
    }

    /**
     * @return (ObjectProperty<HorizontalCoordinates>) observable: center of projection
     */
    public ObjectProperty<HorizontalCoordinates> centerProperty() {
        return center;
    }
}

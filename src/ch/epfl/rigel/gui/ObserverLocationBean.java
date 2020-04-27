package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Subject geographic location's bean
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class ObserverLocationBean {

    private final DoubleProperty lonDeg = new SimpleDoubleProperty();
    private final DoubleProperty latDeg = new SimpleDoubleProperty();
    private final ObjectBinding<GeographicCoordinates> coords = Bindings.createObjectBinding(
            () -> GeographicCoordinates.ofDeg(lonDeg.get(), latDeg.get()),
            lonDeg, latDeg);

    /**
     * Setter for observable: longitude in degrees
     *
     * @param newLonDeg (double) longitude to be set to
     */
    public void setLonDeg(double newLonDeg) {
        lonDeg.set(newLonDeg);
    }

    /**
     * Setter for observable: latitude in degrees
     *
     * @param newLatDeg (double) latitude to be set to
     */
    public void setLatDeg(double newLatDeg) {
        latDeg.set(newLatDeg);
    }

    /**
     * Composite setter for longitude and latitude observables (in turn modifying the GeographicCoordinates coords
     * bind)
     *
     * @param geoCoords (GeographicCoordinates) coordinates to be set to
     */
    public void setCoordinates(final GeographicCoordinates geoCoords) {
        lonDeg.set(geoCoords.lon());
        latDeg.set(geoCoords.lat());
    }

    /**
     * @return (double) value of observable: longitude in degrees
     */
    public double getLonDeg() {
        return lonDeg.get();
    }

    /**
     * @return (DoubleProperty) observable: longitude in degrees
     */
    public DoubleProperty lonDegProperty() {
        return lonDeg;
    }

    /**
     * @return (double) value of observable: latitude in degrees
     */
    public double getLatDeg() {
        return latDeg.get();
    }

    /**
     * @return (DoubleProperty) observable: latitude in degrees
     */
    public DoubleProperty latDegProperty() {
        return latDeg;
    }

    /**
     * @return (double) value of observable: geographical coordinates
     */
    public GeographicCoordinates getCoords() {
        return coords.get();
    }

    /**
     * @return (ObjectBinding<GeographicCoordinates>) GeographicCoordinates binding
     */
    public ObjectBinding<GeographicCoordinates> coordsProperty() {
        return coords;
    }
}

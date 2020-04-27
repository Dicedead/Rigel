package ch.epfl.rigel.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * JavaFX bean representing a ZonedDateTime with 3 properties: date, time and timezone
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class DateTimeBean {

    private final ObjectProperty<LocalDate> dateProperty = new SimpleObjectProperty<>(null);
    private final ObjectProperty<LocalTime> timeProperty = new SimpleObjectProperty<>(null);
    private final ObjectProperty<ZoneId> zoneProperty = new SimpleObjectProperty<>(null);
    private ZonedDateTime currentZDT;

    /**
     * @return (ObjectProperty<LocalDate>) observable: date property
     */
    public ObjectProperty<LocalDate> dateProperty() {
        return dateProperty;
    }

    /**
     * @return (LocalDate) value of observable: date
     */
    public LocalDate getDate() {
        return dateProperty.get();
    }

    /**
     * Setter for observable: date property
     *
     * @param date (LocalDate) date to be set to
     */
    public void setDate(final LocalDate date) {
        dateProperty.set(date);
    }

    /**
     * @return (ObjectProperty<LocalTime>) observable: time property
     */
    public ObjectProperty<LocalTime> timeProperty() {
        return timeProperty;
    }

    /**
     * @return (LocalTime) value of observable: time
     */
    public LocalTime getTime() {
        return timeProperty.get();
    }

    /**
     * Setter for observable: time property
     *
     * @param time (LocalTime) time to be set to
     */
    public void setTime(final LocalTime time) {
        timeProperty.set(time);
    }

    /**
     * @return (ObjectProperty<ZoneId>) observable: timezone property
     */
    public ObjectProperty<ZoneId> zoneProperty() {
        return zoneProperty;
    }

    /**
     * @return (ZoneId) value of observable: timezone
     */
    public ZoneId getZone() {
        return zoneProperty.get();
    }

    /**
     * Setter for observable: timezone property
     *
     * @param zone (ZonedId) zone to be set to
     */
    public void setZone(final ZoneId zone) {
        zoneProperty.set(zone);
    }

    /**
     * @return (ZonedDateTime) current ZonedDateTime, with current date, time and timezone values
     */
    public ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.of(dateProperty.get(), timeProperty.get(), zoneProperty.get());
    }

    /**
     * 3 in 1 setter for date, time and timezone properties
     *
     * @param zonedDateTime (ZonedDateTime) ZonedDateTime to which all 3 properties will be set to
     */
    public void setZonedDateTime(final ZonedDateTime zonedDateTime) {
        dateProperty.set(zonedDateTime.toLocalDate());
        timeProperty.set(zonedDateTime.toLocalTime());
        zoneProperty.set(zonedDateTime.getZone());
    }
}
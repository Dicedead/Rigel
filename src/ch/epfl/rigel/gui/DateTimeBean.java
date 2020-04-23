package ch.epfl.rigel.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class DateTimeBean {


    private LocalDate date  = null;
    private LocalTime time  = null;
    private ZoneId zone     = null;

    public ObjectProperty<LocalDate> dateProperty()
    {
        return  new SimpleObjectProperty<>(date);
    }
    public LocalDate getDate() {
        return date;
    }

    public void setDate(final LocalDate date) {
        this.date = date;
    }

    public ObjectProperty<LocalTime> timeProperty()
    {
        return  new SimpleObjectProperty<>(time);
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(final LocalTime time) {
        this.time = time;
    }

    public ObjectProperty<ZoneId> zoneProperty()
    {
        return  new SimpleObjectProperty<>(zone);
    }

    public ZoneId getZone() {
        return zone;
    }

    public void setZone(final ZoneId zone) {
        this.zone = zone;
    }

    public ZonedDateTime getZonedDateTime()
    {
        return ZonedDateTime.of(date, time, zone);
    }

    public void setZonedDateTime(final ZonedDateTime zonedDateTime)
    {
        setDate(zonedDateTime.toLocalDate());
        setTime(zonedDateTime.toLocalTime());
        setZone(zonedDateTime.getZone());
    }

}

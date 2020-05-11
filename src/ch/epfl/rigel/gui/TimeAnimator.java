package ch.epfl.rigel.gui;

import javafx.animation.AnimationTimer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.ZonedDateTime;

/**
 * Periodic time modifier
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class TimeAnimator extends AnimationTimer {

    private final DateTimeBean dateTimeBean;
    private final ObjectProperty<TimeAccelerator> accelerator;
    private final SimpleBooleanProperty running;
    private ZonedDateTime startTime;

    private boolean hasJustBeenStarted = false;
    private long initialRealTime;

    /**
     * Creates a new TimeAnimator modifying parameter dtBean
     *
     * @param dtBean (DateTimeBean) mutable ZonedDateTime instance
     */
    public TimeAnimator(DateTimeBean dtBean) {
        dateTimeBean = dtBean;
        accelerator = new SimpleObjectProperty<>(null);
        running = new SimpleBooleanProperty(false);
    }

    /**
     * Advances time until 'now', given in nanoseconds
     *
     * @see AnimationTimer#handle(long)
     * @param now (long) nanoseconds
     */
    @Override
    public void handle(long now) {
        if (hasJustBeenStarted) {
            initialRealTime = now;
            hasJustBeenStarted = false;
        } else {
            dateTimeBean.setZonedDateTime(accelerator.get().adjust(startTime, now - initialRealTime));
        }
    }

    /**
     * @see AnimationTimer#start()
     */
    @Override
    public void start() {
        hasJustBeenStarted = true;
        startTime = dateTimeBean.getZonedDateTime();
        super.start();
        running.set(true);
    }

    /**
     * @see AnimationTimer#stop()
     */
    @Override
    public void stop() {
        dateTimeBean.setZone(startTime.getZone()); //accounts for possible daylight saving time modification
        super.stop();
        running.set(false);
    }

    /**
     * Sets input TimeAccelerator a onto modified DateTimeBean
     *
     * @param a (TimeAccelerator)
     */
    public void setAccelerator(TimeAccelerator a) {
        accelerator.set(a);
    }

    /**
     * @return (ReadOnlyBooleanProperty) subject / observable boolean property: TimeAnimator running
     */
    public ReadOnlyBooleanProperty runningProperty() {
        return running;
    }

    /**
     * @see TimeAnimator#runningProperty()
     * @return (boolean) boolean value of running property
     */
    public boolean isRunning() {
        return running.get();
    }
}

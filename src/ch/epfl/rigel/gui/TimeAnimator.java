package ch.epfl.rigel.gui;

import javafx.animation.AnimationTimer;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public final class TimeAnimator extends AnimationTimer {
    private final DateTimeBean dateTimeBean;

    public void setAccelerator(TimeAccelerator a)
    {
        accelerator.setValue(a);
    }

    private Property<TimeAccelerator> accelerator;
    private SimpleBooleanProperty running;
    private long last;
    public TimeAnimator(DateTimeBean d) {
        dateTimeBean = d;
        last = 0;
        accelerator = null;
    }

    /**
     * This method needs to be overridden by extending classes. It is going to
     * be called in every frame while the {@code AnimationTimer} is active.
     *
     * @param now The timestamp of the current frame given in nanoseconds. This
     *            value will be the same for all {@code AnimationTimers} called
     *            during one frame.
     */
    @Override
    public void handle(long now) {

        dateTimeBean.setZonedDateTime(accelerator.getValue().adjust(dateTimeBean.getZonedDateTime(), now - last));
        last = now;
    }

    /**
     * Starts the {@code AnimationTimer}. Once it is started, the
     * {@link #handle(long)} method of this {@code AnimationTimer} will be
     * called in every frame.
     * <p>
     * The {@code AnimationTimer} can be stopped by calling {@link #stop()}.
     */
    @Override
    public void start() {
        super.start();
        running.set(true);
    }

    /**
     * Stops the {@code AnimationTimer}. It can be activated again by calling
     * {@link #start()}.
     */
    @Override
    public void stop() {
        super.stop();
        running.set(false);
    }

    public ReadOnlyBooleanProperty isRunning() {
        return running;
    }
}

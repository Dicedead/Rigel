package ch.epfl.rigel.logging;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.*;

/**
 * Fancy logging class, helpful for debugging
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public class RigelLogger extends LogManager{

    public enum runType {
        DEBUG, RELEASE
    }

    @Override
    public boolean addLogger(Logger logger) {

        final boolean b = super.addLogger(logger);

        if ((getProperty("BuildType").equals(runType.DEBUG.name()))) {
            logger.setLevel(Level.ALL);
        } else {
            logger.setLevel(Level.WARNING);
        }

        final SimpleDateFormat format = new SimpleDateFormat("M-d_HHmmss");

        try {
            final FileHandler handler = new FileHandler("logs/" + logger.getName() + logger.getParent().getName()
                    + format.format(Calendar.getInstance().getTime()) + ".log");

            handler.setFormatter(new SimpleFormatter());
            logger.addHandler(handler);

        } catch (IOException e) {

            final StreamHandler sh = new StreamHandler(System.out, new SimpleFormatter());
            logger.addHandler(sh);

        }


        return b;


    }
}

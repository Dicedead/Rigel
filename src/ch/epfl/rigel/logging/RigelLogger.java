package ch.epfl.rigel.logging;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class RigelLogger {

    private static final Logger guiLogger = Logger.getLogger("GUI Logger");
    private static final Logger backendLogger = Logger.getLogger("Back Logger");
    private static final Logger astronomyLogger = Logger.getLogger("Astronomy Logger");
    private static final Logger mathLogger = Logger.getLogger("Math Logger");
    private static final Logger fileLogger = Logger.getLogger("File Logger");


    private static final List<Logger> loggerList = List.of(guiLogger, backendLogger, astronomyLogger, mathLogger, fileLogger);
    private RigelLogger() {throw new UnsupportedOperationException();}

    public enum runType {
        DEBUG, REALEASE;
        runType() {}
    }

    public static void init(File save, runType type)
    {
        astronomyLogger.setParent(backendLogger);
        mathLogger.setParent(backendLogger);
        fileLogger.setParent(backendLogger);

        backendLogger.setUseParentHandlers(false);
        backendLogger.setUseParentHandlers(true);

        SimpleDateFormat format = new SimpleDateFormat("M-d_HHmmss");
        try {

            FileHandler fileHandler = new FileHandler(save.getAbsolutePath()
                    + format.format(Calendar.getInstance().getTime()) + ".log");

            fileHandler.setFormatter(new SimpleFormatter());


            backendLogger.addHandler(fileHandler);
            guiLogger.addHandler(fileHandler);

        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (type) {
            case DEBUG:
                loggerList.forEach(l -> l.setLevel(Level.ALL));
                break;
            case REALEASE:
                loggerList.forEach(l -> l.setLevel(Level.WARNING));
                break;
        }

    }


    public static Logger getGuiLogger()
    {
        return guiLogger;
    }

    public static Logger getAstronomyLogger()
    {
        return astronomyLogger;
    }

    public static Logger getBackendLogger()
    {
        return backendLogger;
    }

    public static Logger getMathLogger()
    {
        return mathLogger;
    }

    public static Logger getFileLogger()
    {
        return fileLogger;
    }


}

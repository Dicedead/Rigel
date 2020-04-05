package ch.epfl.rigel.logging;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.*;


public class RigelLogger {

    private static final Logger guiLogger = Logger.getLogger("GUI Logger");
    private static final Logger backendLogger = Logger.getLogger("Back Logger");
    private static final Logger astronomyLogger = Logger.getLogger("Astronomy Logger");
    private static final Logger mathLogger = Logger.getLogger("Math Logger");
    private static final Logger fileLogger = Logger.getLogger("File Logger");


    private static final List<Logger> loggerList = List.of(guiLogger, backendLogger, astronomyLogger, mathLogger, fileLogger);
    private RigelLogger() {throw new UnsupportedOperationException();}

    public enum runType {
        DEBUG, RELEASE
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

            FileHandler backendHandler = new FileHandler(save.getAbsolutePath() + "_backend_"
                    + format.format(Calendar.getInstance().getTime()) + ".log");

            FileHandler guiHandler = new FileHandler(save.getAbsolutePath() + "_gui_"
                    + format.format(Calendar.getInstance().getTime()) + ".log");

            backendHandler.setFormatter(new SimpleFormatter());
            guiHandler.setFormatter(new SimpleFormatter());

            backendLogger.addHandler(backendHandler);
            guiLogger.addHandler(guiHandler);

            backendLogger.config("Logging files has been found");

        } catch (Exception e) {

            backendLogger.config("Logging files not found relying on standard output");
            StreamHandler sh = new StreamHandler(System.out, new SimpleFormatter());

            backendLogger.addHandler(sh);
            guiLogger.addHandler(sh);

        }

        switch (type) {
            case DEBUG:
                loggerList.forEach(l -> l.setLevel(Level.ALL));
                break;
            case RELEASE:
                loggerList.forEach(l -> l.setLevel(Level.WARNING));
                break;
        }

        backendLogger.exiting("RigelLogger", "init");

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

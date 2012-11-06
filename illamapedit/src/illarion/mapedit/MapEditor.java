/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Mapeditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Mapeditor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit;

import illarion.common.bug.CrashReporter;
import illarion.common.config.ConfigSystem;
import illarion.common.util.*;
import illarion.mapedit.crash.DefaultCrashHandler;
import illarion.mapedit.crash.exceptions.UnhandlableException;
import illarion.mapedit.gui.GuiController;
import illarion.mapedit.gui.MainFrame;
import illarion.mapedit.gui.SplashScreen;
import illarion.mapedit.resource.ResourceManager;
import illarion.mapedit.resource.loaders.*;
import illarion.mapedit.util.JavaLogToLog4J;
import org.apache.log4j.*;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Handler;

/**
 * Main MapEditor class. This class starts the map editor and handles all
 * configuration files and central settings.
 *
 * @author Martin Karing
 * @since 0.99
 */
public final class MapEditor {
    /**
     * The string that represents the name of this application.
     */
    @SuppressWarnings("nls")
    public static final String APPLICATION = "Illarion Mapeditor";

    /**
     * The version number of the map editor.
     */
    @SuppressWarnings("nls")
    public static final String VERSION = "2.0 alpha";

    /**
     * The instance of the map editor.
     */
    private static MapEditor instance;

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * The configuration of the map editor that is used to get the proper
     * locations of the maps.
     */
    private final ConfigSystem config;

    /**
     * Constructor of the map editor that loads up all required data.
     */
    @SuppressWarnings("nls")
    public MapEditor() {

        final String userDir = checkFolder();

        config = new ConfigSystem(userDir + File.separator + "MapEdit.xcfgz");
        config.setDefault("mapLastOpenDir", new File(System.getProperty("user.home")));

        final Crypto crypt = new Crypto();
        crypt.loadPublicKey();
        TableLoader.setCrypto(crypt);

    }

    /**
     * Crash the editor with a message.
     *
     * @param message the message the editor is supposed to crash with
     */
    public static void crashEditor(final String message) {
        LOGGER.fatal(message);
        System.exit(-1);
    }

    /**
     * Stop the map editor correctly.
     */
    public static void exit() {
        MainFrame.getInstance().exit();
        StoppableStorage.getInstance().shutdown();
        CrashReporter.getInstance().waitForReport();
        saveConfiguration();
    }

    public static String getVersion() {
        return VERSION;
    }

    /**
     * Get the configuration file of the map editor.
     *
     * @return the configuration of the map editor
     */
    public static ConfigSystem getConfig() {
        if (instance == null) {
            instance = new MapEditor();
        }
        return instance.config;
    }

    /**
     * Main function to call to start the map editor.
     *
     * @param args the argument of the system call
     */
    public static void main(final String[] args) {
        initLogging();
        initConfig();
        initExceptionHandler();

        initEventBus();
        SplashScreen.getInstance().setVisible(true);
        JRibbonFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        loadResources();
        final GuiController controller = new GuiController(getConfig());
        controller.initialize();
        instance = new MapEditor();

        Scheduler.getInstance().start();

        controller.start();
    }

    private static void initConfig() {
        final ConfigSystem c = getConfig();
        c.setDefault("errorReport", 0);

    }

    private static void loadResources() {
        final ResourceManager resourceManager = ResourceManager.getInstance();
        resourceManager.addResources(
                ImageLoader.getInstance(),
                TextureLoaderAwt.getInstance(),
                TileLoader.getInstance(),
                ItemLoader.getInstance(),
                OverlayLoader.getInstance()
        );
        while (resourceManager.hasNextToLoad()) {
            try {
                LOGGER.debug("Loading " + resourceManager.getNextDescription());
                SplashScreen.getInstance().setMessage("Loading " + resourceManager.getNextDescription());
                resourceManager.loadNext();
            } catch (IOException e) {
                LOGGER.warn(resourceManager.getPrevDescription() + " failed!");
//                Crash the editor
                throw new UnhandlableException("Can't load " + resourceManager.getPrevDescription(), e);
            }
        }
    }

    private static void initExceptionHandler() {
        CrashReporter.getInstance().setConfig(getConfig());
        CrashReporter.getInstance().setDisplay(CrashReporter.DISPLAY_SWING);
        CrashReporter.getInstance().setMessageSource(Lang.getInstance());
        Thread.setDefaultUncaughtExceptionHandler(DefaultCrashHandler.getInstance());
        Thread.currentThread().setUncaughtExceptionHandler(DefaultCrashHandler.getInstance());
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setUncaughtExceptionHandler(DefaultCrashHandler.getInstance());
            }
        });
    }

    private static void initEventBus() {
        /*try {
            EventServiceLocator.setEventService(EventServiceLocator.SERVICE_NAME_SWING_EVENT_SERVICE, new SwingEventService());
        } catch (EventServiceExistsException e) {
            LOGGER.warn("Can't setup the event bus correctly.", e);
        }*/
    }

    /**
     * Save the current state of the configuration to the filesystem if needed.
     */
    public static void saveConfiguration() {
        //TODO: Fix not saved
        getConfig().save();
    }

    /**
     * This function determines the user data directory and requests the folder
     * to store the client data in case it is needed. It also performs checks to
     * see if the folder is valid.
     *
     * @return a string with the path to the folder or null in case no folder is
     *         set
     */
    @SuppressWarnings("nls")
    private static String checkFolder() {
        if (!DirectoryManager.getInstance().hasUserDirectory()) {
            SplashScreen.getInstance().setVisible(false);
            JOptionPane.showMessageDialog(null,
                    "Installation ist fehlerhaft. Bitte neu ausführen.\n\n"
                            + "Installation is corrupted, please run it again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        return DirectoryManager.getInstance().getUserDirectory()
                .getAbsolutePath();
    }

    /**
     * Prepare the proper output of the log files.
     */
    @SuppressWarnings("nls")
    private static void initLogging() {
        final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(java.util.logging.Logger
                .GLOBAL_LOGGER_NAME);
        //Remove Console handler
        final Handler[] handlers = logger.getHandlers();
        for (final Handler handler : handlers) {
            logger.removeHandler(handler);
        }
        logger.addHandler(new JavaLogToLog4J());
        LOGGER.setLevel(Level.ALL);
        final Layout consoleLayout = new PatternLayout("%-5p - (%c) - [%t]: %m%n");
        LOGGER.addAppender(new ConsoleAppender(consoleLayout));
        final Layout fileLayout = new PatternLayout("%-5p - %d{ISO8601} - [%t]: %m%n");
        try {
            final RollingFileAppender fileAppender = new RollingFileAppender(fileLayout, "mapedit.log");
            fileAppender.setMaxBackupIndex(9);
            fileAppender.setMaxFileSize("50KB");
            LOGGER.addAppender(fileAppender);
        } catch (IOException e) {
            LOGGER.warn("Can't write log to file");
        }
    }
}

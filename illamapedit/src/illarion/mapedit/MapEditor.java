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
import illarion.common.config.Config;
import illarion.common.config.ConfigSystem;
import illarion.common.util.*;
import illarion.mapedit.crash.DefaultCrashHandler;
import illarion.mapedit.gui.MainFrame;
import illarion.mapedit.resource.ResourceManager;
import illarion.mapedit.resource.loaders.ItemLoader;
import illarion.mapedit.resource.loaders.TextureLoaderAwt;
import illarion.mapedit.resource.loaders.TileLoader;
import org.apache.log4j.*;
import org.bushe.swing.event.EventServiceExistsException;
import org.bushe.swing.event.EventServiceLocator;
import org.bushe.swing.event.SwingEventService;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

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
        initLogging();
        final String userDir = checkFolder();

        LOGGER.debug("UserDir: " + userDir);
        config = new ConfigSystem(userDir + File.separator + "MapEdit.xcfgz");
        LOGGER.debug("Config: " + userDir + File.separator + "MapEdit.xcfgz");
        config.setDefault("globalHist", false);
        config.setDefault("historyLength", 100);
        config.setDefault("mapLastOpenDir", new File(System.getProperty("user.home")));

        Thread.setDefaultUncaughtExceptionHandler(DefaultCrashHandler.getInstance());

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
        saveConfiguration();
        StoppableStorage.getInstance().shutdown();
        CrashReporter.getInstance().waitForReport();
    }

    public static String getVersion() {
        return VERSION;
    }

    /**
     * Get the configuration file of the map editor.
     *
     * @return the configuration of the map editor
     */
    public static Config getConfig() {
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
        initEventBus();
        instance = new MapEditor();


        final ResourceManager res = ResourceManager.getInstance();
        res.addResources(
                TextureLoaderAwt.getInstance(),
                TileLoader.getInstance(),
                ItemLoader.getInstance()
        );
        while (res.hasNextToLoad()) {
            try {
                LOGGER.debug("Loading " + res.getNextDescription());
                res.loadNext();
            } catch (IOException e) {
                LOGGER.warn(res.getPrevDescription() + " failed!");
            }
        }


        CrashReporter.getInstance().setConfig(getConfig());
        CrashReporter.getInstance().setDisplay(CrashReporter.DISPLAY_SWING);
        CrashReporter.getInstance().setMessageSource(Lang.getInstance());

        Scheduler.getInstance().start();

        startGui();
    }

    private static void initEventBus() {
        try {
            EventServiceLocator.setEventService(EventServiceLocator.SERVICE_NAME_SWING_EVENT_SERVICE, new SwingEventService());
        } catch (EventServiceExistsException e) {
            LOGGER.warn("Can't setup the event bus correctly.", e);
        }
    }

    /**
     * This method starts up the gui.
     */
    private static void startGui() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SubstanceLookAndFeel.setSkin("org.pushingpixels.substance.api.skin.OfficeSilver2007Skin");
                MainFrame.getInstance().setVisible(true);

            }
        });

    }

    /**
     * Save the current state of the configuration to the filesystem if needed.
     */
    public static void saveConfiguration() {
        instance.config.save();
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
        LOGGER.setLevel(Level.ALL);
        final Layout consoleLayout = new PatternLayout("%-5p - (%c) - [%t]: %m%n");
        LOGGER.addAppender(new ConsoleAppender(consoleLayout));
        final Layout fileLayout = new PatternLayout("%-5p - %d{ISO8601} - [%t]: %m%n");
        try {
            LOGGER.addAppender(new RollingFileAppender(fileLayout, "mapedit.log"));
        } catch (IOException e) {
            LOGGER.warn("Can't write log to file");
        }
    }
}

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
import illarion.common.util.*;
import illarion.mapedit.crash.DefaultCrashHandler;
import illarion.mapedit.crash.exceptions.UnhandlableException;
import illarion.mapedit.gui.MapEditorConfig;
import illarion.mapedit.gui.GuiController;
import illarion.mapedit.gui.MainFrame;
import illarion.mapedit.gui.SplashScreen;
import illarion.mapedit.resource.ResourceManager;
import illarion.mapedit.resource.loaders.*;
import org.apache.log4j.*;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;

import javax.swing.*;
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
     * The identifier of the application.
     */
    @SuppressWarnings("nls")
    public static final AppIdent APPLICATION = new AppIdent("Illarion Mapeditor");

    /**
     * The instance of the map editor.
     */
    private static MapEditor instance;

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * Constructor of the map editor that loads up all required data.
     */
    @SuppressWarnings("nls")
    public MapEditor() {
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
        MapEditorConfig.getInstance().save();
    }

    /**
     * Main function to call to start the map editor.
     *
     * @param args the argument of the system call
     */
    public static void main(final String[] args) {
        initLogging();
        MapEditorConfig.getInstance().init();
        initExceptionHandler();

        SplashScreen.getInstance().setVisible(true);

        JRibbonFrame.setDefaultLookAndFeelDecorated(MapEditorConfig.getInstance().isUseWindowDecoration());
        JDialog.setDefaultLookAndFeelDecorated(MapEditorConfig.getInstance().isUseWindowDecoration());
        instance = new MapEditor();

        loadResources();
        final GuiController controller = new GuiController();
        controller.initialize();

        Scheduler.getInstance().start();

        controller.start();
    }

    private static void loadResources() {
        final ResourceManager resourceManager = ResourceManager.getInstance();
        resourceManager.addResources(
                ImageLoader.getInstance(),
                TextureLoaderAwt.getInstance(),
                TileLoader.getInstance(),
                ItemLoader.getInstance(),
                SongLoader.getInstance(),
                ItemGroupLoader.getInstance(),
                OverlayLoader.getInstance(),
                DocuLoader.getInstance()
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
        CrashReporter.getInstance().setConfig(MapEditorConfig.getInstance().getInternalCfg());
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
            final RollingFileAppender fileAppender = new RollingFileAppender(fileLayout, "mapedit.log");
            fileAppender.setMaxBackupIndex(9);
            fileAppender.setMaxFileSize("50KB");
            LOGGER.addAppender(fileAppender);
        } catch (IOException e) {
            LOGGER.warn("Can't write log to file");
        }
        System.out.println("Startup done.");
        LOGGER.info(APPLICATION + " started.");
        JavaLogToLog4J.setup();
        StdOutToLog4J.setup();
    }
}

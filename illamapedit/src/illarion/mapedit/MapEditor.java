/*
 * This file is part of the Illarion Mapeditor.
 * 
 * Copyright © 2011 - Illarion e.V.
 * 
 * The Illarion Mapeditor is free software: you can redistribute i and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Mapeditor is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Mapeditor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import illarion.mapedit.crash.DefaultCrashHandler;
import illarion.mapedit.database.MapDatabaseManager;
import illarion.mapedit.graphics.MapDisplay;
import illarion.mapedit.gui.swing.MainFrame;
import illarion.mapedit.gui.awt.SaveChangedDialog;
import illarion.mapedit.gui.awt.SplashScreen;
import illarion.mapedit.input.InputHandler;
import illarion.mapedit.map.MapStorage;

import illarion.common.bug.CrashReporter;
import illarion.common.config.Config;
import illarion.common.config.ConfigSystem;
import illarion.common.util.Crypto;
import illarion.common.util.DirectoryManager;
import illarion.common.util.Scheduler;
import illarion.common.util.StoppableStorage;
import illarion.common.util.TableLoader;

import illarion.graphics.Graphics;

import illarion.input.Engines;
import illarion.input.InputManager;

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
     * The default look and feel of the map editor.
     */
    @SuppressWarnings("nls")
    public static final String defaultLookAndFeel =
        "org.pushingpixels.substance.api.skin.OfficeSilver2007Skin";

    /**
     * The version number of the map editor.
     */
    @SuppressWarnings("nls")
    public static final String VERSION = "1.01";

    /**
     * The instance of the map editor.
     */
    private static MapEditor instance;

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(MapEditor.class);

    /**
     * The configuration of the map editor that is used to get the proper
     * locations of the maps.
     */
    private final ConfigSystem cfg;

    /**
     * The map display that is used to show the tiles on the map.
     */
    private MapDisplay display;

    /**
     * The GUI mainframe.
     */
    private MainFrame guiMain;

    /**
     * Constructor of the map editor that loads up all required data.
     */
    @SuppressWarnings("nls")
    public MapEditor() {
        final String folder = checkFolder();
        cfg = new ConfigSystem(folder + File.separator + "MapEdit.xcfgz");
        cfg.setDefault(MapDatabaseManager.CFG_KEY_MAP_DIR,
            new File(System.getProperty("user.home")));
        cfg.setDefault("hideTiles", false);
        cfg.setDefault("hideItems", false);
        cfg.setDefault("hideGrid", false);
        cfg.setDefault("displayMap", 0);
        cfg.setDefault("globalHist", false);
        cfg.setDefault("historyLength", 100);
        cfg.setDefault("decorateWindows", true);
        cfg.setDefault("skin",
            "org.pushingpixels.substance.api.skin.OfficeSilver2007Skin");
        
        cfg.set(MapDatabaseManager.CFG_KEY_MAP_DIR, "C:/Users/Martin Karing/Entwicklung/maps/trunk/Testserver");

        CrashReporter.getInstance().setConfig(cfg);
        CrashReporter.getInstance().setDisplay(CrashReporter.DISPLAY_AWT);
        CrashReporter.getInstance().setMessageSource(Lang.getInstance());

        Thread.setDefaultUncaughtExceptionHandler(DefaultCrashHandler
            .getInstance());

        final Crypto crypt = new Crypto();
        crypt.loadPublicKey();
        TableLoader.setCrypto(crypt);

        initLogging(folder);
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
        final String[] changedMaps = MapStorage.getInstance().getChangedMaps();
        if (changedMaps != null) {
            final SaveChangedDialog diag = new SaveChangedDialog();
            diag.setUnsavedMaps(changedMaps);
            diag.setVisible(true);

            if (!diag.isExit()) {
                return;
            }
        }
        instance.cfg.save();
        StoppableStorage.getInstance().shutdown();
        Graphics.getInstance().getRenderDisplay().stopRendering();
        Graphics.getInstance().getRenderDisplay().shutdown();
        instance.guiMain.setVisible(false);
        instance.guiMain.dispose();

        CrashReporter.getInstance().waitForReport();
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
        return instance.cfg;
    }

    /**
     * Get the display handler of the map editor.
     * 
     * @return the map display of the editor
     */
    public static MapDisplay getDisplay() {
        if (instance == null) {
            instance = new MapEditor();
        }
        return instance.display;
    }

    /**
     * Get the mainframe of the GUI.
     * 
     * @return the gui mainframe
     */
    public static MainFrame getMainFrame() {
        if (instance == null) {
            instance = new MapEditor();
        }
        return instance.guiMain;
    }

    /**
     * Main function to call to start the map editor.
     * 
     * @param args the argument of the system call
     */
    public static void main(final String[] args) {
        SplashScreen.getInstance().setVisible(true);

        instance = new MapEditor();
        CrashReporter.getInstance().setConfig(MapEditor.getConfig());
        CrashReporter.getInstance().setMessageSource(Lang.getInstance());

        MapDatabaseManager.getInstance().configChanged(getConfig(),
            MapDatabaseManager.CFG_KEY_MAP_DIR);

        Graphics.getInstance().setEngine(illarion.graphics.Engines.jogl);
        Graphics.getInstance().getRenderDisplay()
            .setDisplayOptions("jogl.newt", Boolean.TRUE.toString()); //$NON-NLS-1$
        InputManager.getInstance().setEngine(Engines.newt);
        Graphics.getInstance().setQuality(Graphics.QUALITY_NORMAL);
        Scheduler.getInstance().start();
        instance.display = new MapDisplay();

        instance.guiMain = MainFrame.createMainFrame();

        instance.display.startRendering();

        InputHandler.getInstance().start();
    }

    /**
     * Save the current state of the configuration to the filesystem if needed.
     */
    public static void saveConfiguration() {
        instance.cfg.save();
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
     * 
     * @param folder the folder the log file is written to
     */
    @SuppressWarnings("nls")
    private void initLogging(final String folder) {
        final Properties tempProps = new Properties();
        try {
            tempProps.load(MapEditor.class.getClassLoader()
                .getResourceAsStream("logging.properties"));
            tempProps.put("log4j.appender.IllaLogfileAppender.file", folder
                + File.separator + "mapedit.log");
            tempProps.put("log4j.reset", "true");
            new PropertyConfigurator().doConfigure(tempProps,
                LOGGER.getLoggerRepository());
        } catch (final IOException ex) {
            System.err.println("Error setting up logging environment");
        }
    }
}

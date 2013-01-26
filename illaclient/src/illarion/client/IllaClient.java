/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client;

import de.lessvoid.nifty.slick2d.loaders.SlickAddLoaderLocation;
import de.lessvoid.nifty.slick2d.loaders.SlickRenderFontLoaders;
import de.lessvoid.nifty.slick2d.loaders.SlickRenderImageLoaders;
import illarion.client.crash.DefaultCrashHandler;
import illarion.client.graphics.FontLoader;
import illarion.client.graphics.TextureLoader;
import illarion.client.net.client.LogoutCmd;
import illarion.client.resources.SongFactory;
import illarion.client.resources.SoundFactory;
import illarion.client.resources.loaders.SongLoader;
import illarion.client.resources.loaders.SoundLoader;
import illarion.client.util.ChatLog;
import illarion.client.util.Lang;
import illarion.client.world.MapDimensions;
import illarion.client.world.Player;
import illarion.client.world.World;
import illarion.common.bug.CrashReporter;
import illarion.common.config.Config;
import illarion.common.config.ConfigChangedEvent;
import illarion.common.config.ConfigSystem;
import illarion.common.graphics.GraphicResolution;
import illarion.common.util.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.bushe.swing.event.*;
import org.lwjgl.Sys;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.LogSystem;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.Timer;

/**
 * Main Class of the Illarion Client, this loads up the whole game and runs the main loop of the Illarion Client.
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class IllaClient implements EventTopicSubscriber<ConfigChangedEvent> {
    /**
     * The name of this application.
     */
    public static final String APPLICATION = "Illarion Client"; //$NON-NLS-1$

    /**
     * The version information of this client. This version is shows at multiple positions within the client.
     */
    public static final String VERSION = "2.0.9.1"; //$NON-NLS-1$

    /**
     * The default server the client connects too. The client will always connect to this server.
     */
    @Nonnull
    public static final Servers DEFAULT_SERVER;

    static {
        if ("testserver".equals(System.getProperty("illarion.server"))) {
            DEFAULT_SERVER = Servers.testserver;
        } else {
            DEFAULT_SERVER = Servers.realserver;
        }
    }

    /**
     * The error and debug logger of the client.
     */
    static final Logger LOGGER = Logger.getLogger(IllaClient.class);

    /**
     * Stores if there currently is a exit requested to avoid that the question area is opened multiple times.
     */
    private static boolean exitRequested;

    /**
     * The singleton instance of this class.
     */
    private static final IllaClient INSTANCE = new IllaClient();

    /**
     * Storage of the properties that are used by the logger settings. This is needed to set up the correct paths to
     * the
     * files.
     */
    @Nonnull
    private static final Properties tempProps = new Properties();

    /**
     * The configuration of the client settings.
     */
    private ConfigSystem cfg;

    /**
     * Stores the debug level of the client.
     */
    private int debugLevel = 0;

    /**
     * The class loader of the this class. It is used to get the resource streams that contain the resource data of the
     * client.
     */
    private final ClassLoader rscLoader = IllaClient.class.getClassLoader();

    /**
     * Stores the server the client shall connect to.
     */
    private Servers usedServer = DEFAULT_SERVER;

    /**
     * This is the reference to the Illarion Game instance.
     */
    private Game game;

    /**
     * The container that is used to display the game.
     */
    private AppGameContainer gameContainer;

    /**
     * The default empty constructor used to create the singleton instance of this class.
     */
    private IllaClient() {

    }

    public GameState getGameState(int id) {
        return game.getState(id);
    }

    private void init() {
        try {
            EventServiceLocator.setEventService(EventServiceLocator.SERVICE_NAME_EVENT_BUS, new ThreadSafeEventService());
        } catch (EventServiceExistsException e1) {
            LOGGER.error("Failed preparing the EventBus. Settings the Service handler happened too late");
        }

        prepareConfig();
        assert cfg != null;
        try {
            initLogfiles();
        } catch (IOException e) {
            System.err.println("Failed to setup logging system!");
            e.printStackTrace(System.err);
        }

        Lang.getInstance().recheckLocale(cfg.getString(Lang.LOCALE_CFG));
        CrashReporter.getInstance().setConfig(getCfg());

        Renderer.setRenderer(Renderer.IMMEDIATE_RENDERER);

        SlickRenderImageLoaders.getInstance().addLoader(TextureLoader.getInstance(), SlickAddLoaderLocation.first);
        SlickRenderFontLoaders.getInstance().addLoader(FontLoader.getInstance(), SlickAddLoaderLocation.first);

        // Preload sound and music
        try {
            new SongLoader().setTarget(SongFactory.getInstance()).call();
            new SoundLoader().setTarget(SoundFactory.getInstance()).call();
        } catch (Exception e) {
            LOGGER.error("Failed to load sounds and music!");
        }

        game = new Game();

        GraphicResolution res = null;
        final String resolutionString = cfg.getString(CFG_RESOLUTION);
        if (resolutionString != null) {
            try {
                res = new GraphicResolution(resolutionString);
            } catch (@Nonnull final IllegalArgumentException ex) {
                LOGGER.error("Failed to initialize screen resolution. Falling back.");
            }
        }
        if (res == null) {
            res = new GraphicResolution(800, 600, 32, 60);
        }

        try {
            gameContainer = new AppGameContainer(game, res.getWidth(), res.getHeight(),
                    cfg.getBoolean(CFG_FULLSCREEN));
            MapDimensions.getInstance().reportScreenSize(gameContainer.getWidth(), gameContainer.getHeight());
        } catch (SlickException e) {
            LOGGER.error("Fatal error creating game screen!!!", e);
            System.exit(-1);
        }

        gameContainer.setAlwaysRender(true);
        gameContainer.setUpdateOnlyWhenVisible(false);
        gameContainer.setResizable(false);
        gameContainer.setTargetFrameRate(res.getRefreshRate());
        gameContainer.setForceExit(false);
        if (DEFAULT_SERVER == Servers.realserver) {
            gameContainer.setShowFPS(false);
        }

        EventBus.subscribe(CFG_FULLSCREEN, this);
        EventBus.subscribe(CFG_RESOLUTION, this);

        try {
            gameContainer.setIcons(new String[]{"illarion_client16.png", "illarion_client32.png",
                    "illarion_client64.png", "illarion_client256.png"});
            gameContainer.start();
            LOGGER.info("Client shutdown initiated.");
        } catch (@Nonnull final Exception e) {
            LOGGER.fatal("Exception while launching game.", e);
            Sys.alert("Error", "The client caused a error while starting up: " + e.getMessage());
        } finally {
            quitGame();
            World.cleanEnvironment();
            cfg.save();
        }

        LOGGER.info("Cleanup done.");
        startFinalKiller();
    }

    /**
     * Get the container that is used to display the game inside.
     *
     * @return the are used to display the game inside
     */
    public GameContainer getContainer() {
        return gameContainer;
    }

    /**
     * Show a question frame if the client shall really quit and exit the client in case the user selects yes.
     */
    public static void ensureExit() {
        if (exitRequested) {
            return;
        }
        exitRequested = true;

        INSTANCE.game.enterState(Game.STATE_ENDING);
    }

    public static void exitGameContainer() {
        INSTANCE.gameContainer.exit();
    }

    /**
     * Show an error message and leave the client.
     *
     * @param message the error message that shall be displayed.
     */
    @SuppressWarnings("nls")
    public static void errorExit(final String message) {
        World.cleanEnvironment();

        LOGGER.info("Client terminated on user request.");

        LOGGER.fatal(Lang.getMsg(message));
        LOGGER.fatal("Terminating client!");

        INSTANCE.cfg.save();
        LogManager.shutdown();

        new Thread(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null, Lang.getMsg(message), "Error", JOptionPane.ERROR_MESSAGE);
                startFinalKiller();
            }
        }).start();
    }

    /**
     * Quit the client and restart the connection with the login screen right away.
     *
     * @param message the message that shall be displayed in the login screen
     */
    public static void fallbackToLogin(final String message) {
        LOGGER.warn(message);
        ensureExit();
        //INSTANCE.game.enterState(Game.STATE_LOGIN);
        //World.cleanEnvironment();
    }

    /**
     * Get the configuration handler of the basic client settings.
     *
     * @return the configuration handler
     */
    public static Config getCfg() {
        return INSTANCE.cfg;
    }

    /**
     * Get the full path to a file. This includes the default path that was set up and the name of the file this
     * function gets.
     *
     * @param name the name of the file that shall be append to the folder
     * @return the full path to a file
     */
    public static String getFile(final String name) {
        return new File(DirectoryManager.getInstance().getUserDirectory(), name).getAbsolutePath();
    }

    /**
     * Get the singleton instance of this client main object.
     *
     * @return the singleton instance of this class
     */
    @Nonnull
    public static IllaClient getInstance() {
        return INSTANCE;
    }

    /**
     * Load a resource as stream via the basic class loader.
     *
     * @param path the path to the object that shall be loaded
     * @return the data stream of the object
     */
    public static InputStream getResource(final String path) {
        return INSTANCE.rscLoader.getResourceAsStream(path);
    }

    /**
     * Get a text that identifies the version of this client.
     *
     * @return the version text of this client
     */
    @Nonnull
    public static String getVersionText() {
        return "Illarion Client " + VERSION; //$NON-NLS-1$
    }

    /**
     * Check if a debug flag is set or not.
     *
     * @param flag the debug flag that shall be checked
     * @return true in case the flag is enabled, false if not
     */
    public static boolean isDebug(@Nonnull final Debug flag) {
        return (INSTANCE.debugLevel & (1 << flag.ordinal())) > 0;
    }

    public static void initChatLog() {
        ChatLog.getInstance().init(tempProps);
    }

    /**
     * Main function starts the client and sets up all data.
     *
     * @param args the arguments handed over to the client
     */
    @SuppressWarnings("nls")
    public static void main(final String[] args) {
        final String folder = checkFolder();

        // Setup the crash reporter so the client is able to crash properly.
        CrashReporter.getInstance().setMessageSource(Lang.getInstance());
        CrashReporter.getInstance().setDisplay(CrashReporter.DISPLAY_SWING);

        // in case the server is now known, update the files if needed and
        // launch the client.
        if (folder == null) {
            return;
        }

        INSTANCE.init();
    }

    /**
     * This method is the final one to be called before the client is killed for sure. It gives the rest of the client
     * 10 seconds before it forcefully shuts down everything. This is used to ensure that the client quits when it has
     * to, but in case it does so faster, it won't be killed like that.
     */
    @SuppressWarnings("nls")
    public static void startFinalKiller() {
        final Timer finalKiller = new Timer("Final Death", true);
        finalKiller.schedule(new TimerTask() {
            @Override
            public void run() {
                CrashReporter.getInstance().waitForReport();
                System.err.println("Killed by final death");
                System.exit(-1);
            }
        }, 10000);
    }

    /**
     * This function changes the state of the exit requested parameter.
     *
     * @param newValue the new value for the exitRequested parameter
     */
    static void setExitRequested(final boolean newValue) {
        exitRequested = newValue;
    }

    /**
     * Get if there is currently a exit request pending. Means of the really exit dialog is opened or not.
     *
     * @return true in case the exit dialog is currently displayed
     */
    private static boolean getExitRequested() {
        return exitRequested;
    }

    /**
     * This function determines the user data directory and requests the folder to store the client data in case it is
     * needed. It also performs checks to see if the folder is valid.
     *
     * @return a string with the path to the folder or null in case no folder is set
     */
    @SuppressWarnings("nls")
    private static String checkFolder() {
        if (!DirectoryManager.getInstance().hasUserDirectory()) {
            JOptionPane.showMessageDialog(null, "Installation ist fehlerhaft. Bitte neu ausführen.\n\n" +
                    "Installation is corrupted, please run it again.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        final File userDirectory = DirectoryManager.getInstance().getUserDirectory();
        assert userDirectory != null;
        return userDirectory.getAbsolutePath();
    }

    /**
     * Get the server that was selected as connection target.
     *
     * @return the selected server
     */
    public Servers getUsedServer() {
        return usedServer;
    }

    /**
     * End the game by user request and send the logout command to the server.
     */
    public void quitGame() {
        World.getNet().sendCommand(new LogoutCmd());
    }

    /**
     * Set the server that shall be used to login at.
     *
     * @param server the server that is used to connect with
     */
    public void setUsedServer(final Servers server) {
        usedServer = server;
    }

    /**
     * Basic initialization of the log files and the debug settings.
     */
    @SuppressWarnings("nls")
    private static void initLogfiles() throws IOException {
        tempProps.load(getResource("logging.properties"));
        tempProps.put("log4j.appender.IllaLogfileAppender.file", getFile("error.log"));
        tempProps.put("log4j.appender.ChatAppender.file", getFile("illarion.log"));
        tempProps.put("log4j.reset", "true");
        new PropertyConfigurator().doConfigure(tempProps, LOGGER.getLoggerRepository());

        Thread.setDefaultUncaughtExceptionHandler(DefaultCrashHandler.getInstance());

        System.out.println("Startup done.");
        LOGGER.info(getVersionText() + " started.");
        LOGGER.info("VM: " + System.getProperty("java.version"));
        LOGGER.info("OS: " + System.getProperty("os.name") + ' ' + System.getProperty("os.version") + ' ' + System
                .getProperty("os.arch"));

        //java.util.logging.Logger.getAnonymousLogger().getParent().setLevel(Level.SEVERE);
        //java.util.logging.Logger.getLogger("de.lessvoid.nifty").setLevel(Level.SEVERE);
        java.util.logging.Logger.getLogger("javolution").setLevel(Level.SEVERE);
        JavaLogToLog4J.setup();
        StdOutToLog4J.setup();
        Log.setLogSystem(new LogSystem() {
            private final Logger log = Logger.getLogger(IllaClient.class);

            @Override
            public void error(final String s, final Throwable throwable) {
                log.error(s, throwable);
            }

            @Override
            public void error(final Throwable throwable) {
                log.error("", throwable);
            }

            @Override
            public void error(final String s) {
                log.error(s);
            }

            @Override
            public void warn(final String s) {
                log.warn(s);
            }

            @Override
            public void warn(final String s, final Throwable throwable) {
                log.warn(s, throwable);
            }

            @Override
            public void info(final String s) {
                log.info(s);
            }

            @Override
            public void debug(final String s) {
                log.debug(s);
            }
        });
    }

    public static final String CFG_FULLSCREEN = "fullscreen";
    public static final String CFG_RESOLUTION = "resolution";

    /**
     * Prepare the configuration system and the decryption system.
     */
    @SuppressWarnings("nls")
    private void prepareConfig() {
        cfg = new ConfigSystem(getFile("Illarion.xcfgz"));
        cfg.setDefault("debugLevel", 1);
        cfg.setDefault("showIDs", false);
        cfg.setDefault("soundOn", true);
        cfg.setDefault("soundVolume", Player.MAX_CLIENT_VOL);
        cfg.setDefault("musicOn", true);
        cfg.setDefault("musicVolume", Player.MAX_CLIENT_VOL * 0.75f);
        cfg.setDefault(ChatLog.CFG_TEXTLOG, true);
        cfg.setDefault(CFG_FULLSCREEN, false);
        cfg.setDefault(CFG_RESOLUTION, new GraphicResolution(800, 600, 32, 60).toString());
        cfg.setDefault("savePassword", false);
        cfg.setDefault(CrashReporter.CFG_KEY, CrashReporter.MODE_ASK);
        cfg.setDefault(Lang.LOCALE_CFG, Lang.LOCALE_CFG_ENGLISH);
        cfg.setDefault("inventoryPosX", "100px");
        cfg.setDefault("inventoryPosY", "10px");
        cfg.setDefault("bookDisplayPosX", "150px");
        cfg.setDefault("bookDisplayPosY", "15px");
        cfg.setDefault("skillWindowPosX", "200px");
        cfg.setDefault("skillWindowPosY", "20px");

        final Toolkit awtDefaultToolkit = Toolkit.getDefaultToolkit();
        cfg.setDefault("doubleClickInterval", (Integer) awtDefaultToolkit.getDesktopProperty("awt" +
                ".multiClickInterval"));

        final Crypto crypt = new Crypto();
        crypt.loadPublicKey();
        TableLoader.setCrypto(crypt);
    }

    @Override
    public void onEvent(final String topic, final ConfigChangedEvent data) {
        if (CFG_FULLSCREEN.equals(topic)) {
            try {
                gameContainer.setFullscreen(cfg.getBoolean(CFG_FULLSCREEN));
            } catch (SlickException e) {
                LOGGER.error("Failed to apply fullscreen mode. New requested mode: " +
                        Boolean.toString(cfg.getBoolean(CFG_FULLSCREEN)));
            }
        } else if (CFG_RESOLUTION.equals(topic)) {
            final String resolutionString = cfg.getString(CFG_RESOLUTION);
            if (resolutionString == null) {
                LOGGER.error("Failed reading new resolution.");
                return;
            }
            try {
                final GraphicResolution res = new GraphicResolution(resolutionString);
                final boolean fullScreen = cfg.getBoolean(CFG_FULLSCREEN);
                gameContainer.setDisplayMode(res.getWidth(), res.getHeight(), fullScreen);
                gameContainer.setTargetFrameRate(res.getRefreshRate());
                MapDimensions.getInstance().reportScreenSize(gameContainer.getWidth(), gameContainer.getHeight());
            } catch (@Nonnull final SlickException e) {
                LOGGER.error("Failed to apply graphic mode: " + resolutionString);
            } catch (@Nonnull final IllegalArgumentException ex) {
                LOGGER.error("Failed to apply graphic mode: " + resolutionString);
            }
        }
    }
}

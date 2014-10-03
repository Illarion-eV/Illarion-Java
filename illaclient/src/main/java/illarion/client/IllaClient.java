/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import illarion.client.crash.DefaultCrashHandler;
import illarion.client.net.client.LogoutCmd;
import illarion.client.resources.SongFactory;
import illarion.client.resources.SoundFactory;
import illarion.client.resources.loaders.SongLoader;
import illarion.client.resources.loaders.SoundLoader;
import illarion.client.util.ChatLog;
import illarion.client.util.GlobalExecutorService;
import illarion.client.util.Lang;
import illarion.client.world.Player;
import illarion.client.world.World;
import illarion.client.world.events.ConnectionLostEvent;
import illarion.common.bug.CrashReporter;
import illarion.common.config.Config;
import illarion.common.config.ConfigChangedEvent;
import illarion.common.config.ConfigSystem;
import illarion.common.util.AppIdent;
import illarion.common.util.Crypto;
import illarion.common.util.DirectoryManager;
import illarion.common.util.TableLoader;
import org.bushe.swing.event.*;
import org.illarion.engine.Backend;
import org.illarion.engine.DesktopGameContainer;
import org.illarion.engine.EngineException;
import org.illarion.engine.EngineManager;
import org.illarion.engine.graphic.GraphicResolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main Class of the Illarion Client, this loads up the whole game and runs the main loop of the Illarion Client.
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class IllaClient implements EventTopicSubscriber<ConfigChangedEvent> {
    /**
     * The identification of this application.
     */
    public static final AppIdent APPLICATION = new AppIdent("Illarion Client"); //$NON-NLS-1$

    /**
     * The default server the client connects too. The client will always connect to this server.
     */
    @Nonnull
    public static final Servers DEFAULT_SERVER;

    static {
        String server = System.getProperty("illarion.server", "realserver");
        switch (server) {
            case "testserver":
                DEFAULT_SERVER = Servers.testserver;
                break;
            case "devserver":
                DEFAULT_SERVER = Servers.devserver;
                break;
            default:
                DEFAULT_SERVER = Servers.realserver;
                break;
        }
    }

    /**
     * The error and debug logger of the client.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(IllaClient.class);

    /**
     * Stores if there currently is a exit requested to avoid that the question area is opened multiple times.
     */
    private static boolean exitRequested;

    /**
     * The singleton instance of this class.
     */
    private static final IllaClient INSTANCE = new IllaClient();

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
    private DesktopGameContainer gameContainer;

    /**
     * The default empty constructor used to create the singleton instance of this class.
     */
    private IllaClient() {

    }

    private void init() {
        try {
            EventServiceLocator.setEventService(EventServiceLocator.SERVICE_NAME_EVENT_BUS, null);
            EventServiceLocator.setEventService(EventServiceLocator.SERVICE_NAME_SWING_EVENT_SERVICE, null);
            EventServiceLocator
                    .setEventService(EventServiceLocator.SERVICE_NAME_EVENT_BUS, new ThreadSafeEventService());
            EventServiceLocator.setEventService(EventServiceLocator.SERVICE_NAME_SWING_EVENT_SERVICE,
                                                EventServiceLocator.getEventBusService());
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

        // Report errors of the released version only
        if (DEFAULT_SERVER != Servers.realserver) {
            CrashReporter.getInstance().setMode(CrashReporter.MODE_NEVER);
        }

        // Preload sound and music
        try {
            new SongLoader().setTarget(SongFactory.getInstance()).call();
            new SoundLoader().setTarget(SoundFactory.getInstance()).call();
        } catch (Exception e) {
            LOGGER.error("Failed to load sounds and music!");
        }

        game = new Game();

        GraphicResolution res = null;
        String resolutionString = cfg.getString(CFG_RESOLUTION);
        if (resolutionString != null) {
            try {
                res = new GraphicResolution(resolutionString);
            } catch (@Nonnull IllegalArgumentException ex) {
                LOGGER.error("Failed to initialize screen resolution. Falling back.");
            }
        }
        if (res == null) {
            res = new GraphicResolution(800, 600, 32, 60);
        }

        try {
            int requestedWidth;
            int requestedHeight;
            boolean fullScreenMode;

            if (cfg.getBoolean(CFG_FULLSCREEN)) {
                fullScreenMode = true;
                requestedHeight = res.getHeight();
                requestedWidth = res.getWidth();
            } else {
                fullScreenMode = false;
                int windowedWidth = cfg.getInteger("windowWidth");
                int windowedHeight = cfg.getInteger("windowHeight");
                requestedHeight = (windowedHeight < 0) ? res.getHeight() : windowedHeight;
                requestedWidth = (windowedWidth < 0) ? res.getWidth() : windowedWidth;
            }

            gameContainer = EngineManager
                    .createDesktopGame(Backend.libGDX, game, requestedWidth, requestedHeight, fullScreenMode);
        } catch (@Nonnull EngineException e) {
            LOGGER.error("Fatal error creating game screen!!!", e);
            System.exit(-1);
        }

        gameContainer.setTitle(APPLICATION.getApplicationIdentifier());
        gameContainer.setIcons(new String[]{"illarion_client16.png", "illarion_client32.png", "illarion_client64.png",
                                            "illarion_client256.png"});

        EventBus.subscribe(CFG_FULLSCREEN, this);
        EventBus.subscribe(CFG_RESOLUTION, this);

        try {
            gameContainer.setResizeable(true);
            gameContainer.startGame();
        } catch (@Nonnull Exception e) {
            LOGGER.error("Exception while launching game.", e);
            exitGameContainer();
        }
    }

    /**
     * Get the container that is used to display the game inside.
     *
     * @return the are used to display the game inside
     */
    public DesktopGameContainer getContainer() {
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
        INSTANCE.gameContainer.exitGame();

        LOGGER.info("Client shutdown initiated.");

        getInstance().quitGame();
        World.cleanEnvironment();
        getCfg().save();
        GlobalExecutorService.shutdown();

        LOGGER.info("Cleanup done.");
        startFinalKiller();
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

        LOGGER.error(Lang.getMsg(message));
        LOGGER.error("Terminating client!");

        INSTANCE.cfg.save();

        new Thread(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null, Lang.getMsg(message), "Error", JOptionPane.ERROR_MESSAGE);
                startFinalKiller();
            }
        }).start();
    }

    /**
     * Publishing a ConnectionLostEvent.
     *
     * @param message the message that shall be displayed
     */
    public static void sendDisconnectEvent(String message) {
        LOGGER.warn(message);
        EventBus.publish(new ConnectionLostEvent(message));
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
    @Nonnull
    public static Path getFile(@Nonnull String name) {
        Path userDir = DirectoryManager.getInstance().getDirectory(DirectoryManager.Directory.User);
        return userDir.resolve(name);
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
    public static InputStream getResource(String path) {
        return INSTANCE.rscLoader.getResourceAsStream(path);
    }

    /**
     * Check if a debug flag is set or not.
     *
     * @param flag the debug flag that shall be checked
     * @return true in case the flag is enabled, false if not
     */
    public static boolean isDebug(@Nonnull Debug flag) {
        return (INSTANCE.debugLevel & (1 << flag.ordinal())) > 0;
    }

    /**
     * Main function starts the client and sets up all data.
     *
     * @param args the arguments handed over to the client
     */
    @SuppressWarnings("nls")
    public static void main(String[] args) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        // Setup the crash reporter so the client is able to crash properly.
        CrashReporter.getInstance().setMessageSource(Lang.getInstance());

        // in case the server is now known, update the files if needed and
        // launch the client.

        INSTANCE.init();
    }

    /**
     * This method is the final one to be called before the client is killed for sure. It gives the rest of the client
     * 10 seconds before it forcefully shuts down everything. This is used to ensure that the client quits when it has
     * to, but in case it does so faster, it won't be killed like that.
     */
    @SuppressWarnings("nls")
    public static void startFinalKiller() {
        Timer finalKiller = new Timer("Final Death", true);
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
    static void setExitRequested(boolean newValue) {
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
        try {
            World.getNet().sendCommand(new LogoutCmd());
        } catch (@Nonnull IllegalStateException ex) {
            // the NET was not launched up yet. This does not really matter.
        }
    }

    /**
     * Set the server that shall be used to login at.
     *
     * @param server the server that is used to connect with
     */
    public void setUsedServer(Servers server) {
        usedServer = server;
    }

    /**
     * Basic initialization of the log files and the debug settings.
     */
    @SuppressWarnings("nls")
    private static void initLogfiles() throws IOException {
        Path userDir = DirectoryManager.getInstance().getDirectory(DirectoryManager.Directory.User);
        System.setProperty("log_dir", userDir.toAbsolutePath().toString());

        //Reload:
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        ContextInitializer ci = new ContextInitializer(lc);
        lc.reset();
        try {
            ci.autoConfig();
        } catch (JoranException e) {
            e.printStackTrace();
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);

        Thread.setDefaultUncaughtExceptionHandler(DefaultCrashHandler.getInstance());

        System.out.println("Startup done.");
        LOGGER.info("{} started.", APPLICATION.getApplicationIdentifier());
        LOGGER.info("VM: {}", System.getProperty("java.version"));
        LOGGER.info("OS: {} {} {}", System.getProperty("os.name"), System.getProperty("os.version"),
                    System.getProperty("os.arch"));
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
        cfg.setDefault("windowWidth", -1);
        cfg.setDefault("windowHeight", -1);
        cfg.setDefault("savePassword", false);
        cfg.setDefault("showFps", false);
        cfg.setDefault("showPing", false);
        cfg.setDefault(CrashReporter.CFG_KEY, CrashReporter.MODE_ASK);
        cfg.setDefault(Lang.LOCALE_CFG, Lang.LOCALE_CFG_ENGLISH);
        cfg.setDefault("inventoryPosX", "100px");
        cfg.setDefault("inventoryPosY", "10px");
        cfg.setDefault("bookDisplayPosX", "150px");
        cfg.setDefault("bookDisplayPosY", "15px");
        cfg.setDefault("skillWindowPosX", "200px");
        cfg.setDefault("skillWindowPosY", "20px");
        cfg.setDefault("backpackDisplayPosX", "100px");
        cfg.setDefault("backpackDisplayPosY", "10px");
        cfg.setDefault("depotDisplayPosX", "150px");
        cfg.setDefault("depotDisplayPosY", "15px");
        cfg.setDefault("bagDisplayPosX", "200px");
        cfg.setDefault("bagDisplayPosY", "20px");
        cfg.setDefault("questWindowPosX", "100px");
        cfg.setDefault("questWindowPosY", "100px");
        cfg.setDefault("questShowFinished", false);
        cfg.setDefault("server", Login.DEVSERVER);
        cfg.setDefault("serverAddress", Servers.customserver.getServerHost());
        cfg.setDefault("serverPort", Servers.customserver.getServerPort());
        cfg.setDefault("clientVersion", Servers.customserver.getClientVersion());
        cfg.setDefault("serverAccountLogin", true);
        cfg.setDefault("wasdWalk", true);
        cfg.setDefault("disableChatAfterSending", true);
        cfg.setDefault("showQuestsOnGameMap", true);
        cfg.setDefault("showQuestsOnMiniMap", true);
        /* Showing the avatar tag on a permanent base.
         * 0 -> none are shown
         * 1 -> other players only
         * 2 -> other players and monsters
         */
        cfg.setDefault("showAvatarTagPermanently", 0);
        cfg.set("limitPathFindingToMouseDirection", true);
        cfg.set("followMousePathFinding", true);

        @Nonnull Toolkit awtDefaultToolkit = Toolkit.getDefaultToolkit();
        @Nullable Object doubleClick = awtDefaultToolkit.getDesktopProperty("awt.multiClickInterval");
        if (doubleClick instanceof Number) {
            cfg.set("doubleClickInterval", ((Number) doubleClick).intValue());
        } else {
            cfg.set("doubleClickInterval", 500);
        }

        Crypto crypt = new Crypto();
        crypt.loadPublicKey();
        TableLoader.setCrypto(crypt);
    }

    @Override
    public void onEvent(String topic, ConfigChangedEvent data) {
        if (CFG_RESOLUTION.equals(topic) || CFG_FULLSCREEN.equals(topic)) {
            String resolutionString = cfg.getString(CFG_RESOLUTION);
            if (resolutionString == null) {
                LOGGER.error("Failed reading new resolution.");
                return;
            }
            try {
                GraphicResolution res = new GraphicResolution(resolutionString);
                boolean fullScreen = cfg.getBoolean(CFG_FULLSCREEN);
                if (fullScreen) {
                    gameContainer.setFullScreenResolution(res);
                    gameContainer.setFullScreen(true);
                } else {
                    gameContainer.setWindowSize(res.getWidth(), res.getHeight());
                    gameContainer.setFullScreen(false);
                }
                if (!fullScreen) {
                    cfg.set("windowHeight", res.getHeight());
                    cfg.set("windowWidth", res.getWidth());
                }
            } catch (@Nonnull EngineException e) {
                LOGGER.error("Failed to apply graphic mode: " + resolutionString);
            } catch (@Nonnull IllegalArgumentException ex) {
                LOGGER.error("Failed to apply graphic mode: " + resolutionString);
            }
        }
    }
}

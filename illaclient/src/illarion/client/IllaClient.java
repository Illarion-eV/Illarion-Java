/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import illarion.client.crash.DefaultCrashHandler;
import illarion.client.graphics.LoadingScreen;
import illarion.client.guiNG.GUI;
import illarion.client.guiNG.Journal;
import illarion.client.net.CommandFactory;
import illarion.client.net.CommandList;
import illarion.client.net.client.SimpleCmd;
import illarion.client.util.ChatLog;
import illarion.client.util.Lang;
import illarion.client.util.SessionManager;
import illarion.client.world.Game;

import illarion.common.bug.CrashReporter;
import illarion.common.config.Config;
import illarion.common.config.ConfigSystem;
import illarion.common.debug.DefaultDeadlockListener;
import illarion.common.debug.ThreadDeadlockDetector;
import illarion.common.util.Crypto;
import illarion.common.util.DirectoryManager;
import illarion.common.util.Scheduler;
import illarion.common.util.StoppableStorage;
import illarion.common.util.TableLoader;

import illarion.graphics.GraphicResolution;
import illarion.graphics.Graphics;

import illarion.input.InputManager;

/**
 * Main Class of the Illarion Client, this loads up the whole game and runs the
 * main loop of the Illarion Client.
 * 
 * @author Nop
 * @author Martin Karing
 * @since 0.95
 */
public final class IllaClient {
    /**
     * The name of this application.
     */
    public static final String APPLICATION = "Illarion Client"; //$NON-NLS-1$

    /**
     * The version information of this client. This version is shows at multiple
     * positions within the client.
     */
    public static final String VERSION = "1.22 δ"; //$NON-NLS-1$

    /**
     * The default server the client connects too. The client will always
     * connect to this server if {@link #MULTI_CLIENT} is set to false.
     */
    static final Servers DEFAULT_SERVER = Servers.testserver;

    /**
     * The error and debug logger of the client.
     */
    static final Logger LOGGER = Logger.getLogger(IllaClient.class);

    /**
     * Allow to connect to multiple servers. In case this is set to true, the
     * server is able to connect to all servers listed. In case its false the
     * client connects only to the default server.
     */
    static final boolean MULTI_CLIENT = true;

    /**
     * The constant to disable the LWJGL component.
     */
    private static final int COMPONENT_JOGL = (1 << 1);

    /**
     * The constant to disable the LWJGL component.
     */
    private static final int COMPONENT_LWJGL = (1 << 0);

    /**
     * The error message that should be displayed in the login window at the
     * next reconnect.
     */
    @SuppressWarnings("nls")
    private static String errorMessage = "";

    /**
     * The variable is set true in case the error exit handler has taken over.
     * The main loop must not perform any actions in case this happens and it
     * also must not quit.
     */
    private static boolean errorQuitting = false;

    /**
     * Stores if there currently is a exit requested to avoid that the question
     * area is opened multiple times.
     */
    private static boolean exitRequested = false;

    /**
     * The buffer size in byte that is available to install files to the client
     * directory. This buffer is used to store and save the files that need to
     * be copied.
     */
    private static final int FILE_BUFFER = 1000;

    /**
     * The singleton instance of this class.
     */
    private static final IllaClient INSTANCE = new IllaClient();

    /**
     * True in case its needed to reconnect the client instead of shutting it
     * down.
     */
    private static boolean requestedReconnect = false;

    /**
     * Sleeping time in ms between each render of the splash screen while the
     * login window is displayed.
     */
    private static final int SLEEP_RENDER = 150;

    /**
     * The amount of times the splash screen is rendered after the login dialog
     * is displayed and before the texture loading starts.
     */
    private static final int SLEEP_TEXTURE = 7;

    /**
     * The sleep time of the main thread in ms after the logout command was send
     * to give the other threads some time to handle their things and shut down
     * as well.
     */
    private static final int SLEEPTIME_LOGOUT = 200;

    /**
     * Storage of the properties that are used by the logger settings. This is
     * needed to set up the correct paths to the files.
     */
    private static Properties tempProps = new Properties();

    /**
     * The configuration of the client settings.
     */
    private ConfigSystem cfg;

    /**
     * Stores the debug level of the client.
     */
    private int debugLevel = 0;

    /**
     * Components that got disabled during the integrity check.
     */
    private int disableComponents = 0;

    /**
     * Stores the information if the login is done or not.
     */
    private boolean loginDone = false;

    /**
     * Stores the information what the result of the login was (cancel or
     * login).
     */
    private volatile boolean loginResult = false;

    /**
     * The class loader of the this class. It is used to get the resource
     * streams that contain the resource data of the client.
     */
    private final ClassLoader rscLoader = IllaClient.class.getClassLoader();

    /**
     * Stores the server the client shall connect to.
     */
    private Servers usedServer = DEFAULT_SERVER;

    /**
     * The default empty constructor used to create the singleton instance of
     * this class.
     */
    private IllaClient() {
        // do nothing
    }

    /**
     * Show a question frame if the client shall really quit and exit the client
     * in case the user selects yes.
     */
    public static void ensureExit() {
        if (exitRequested) {
            return;
        }
        exitRequested = true;

        GUI.getInstance().showReallyExitDialog(new Runnable() {
            @Override
            public void run() {
                IllaClient.getInstance().quitGame();
            }
        }, new Runnable() {
            @Override
            public void run() {
                setExitRequested(false);
            }
        });
    }

    /**
     * Show an error message and leave the client.
     * 
     * @param message the error message that shall be displayed.
     */
    @SuppressWarnings("nls")
    public static void errorExit(final String message) {
        requestedReconnect = false;
        errorQuitting = true;

        INSTANCE.cfg.save();
        Game.getInstance().setRunning(false);
        StoppableStorage.getInstance().shutdown();

        // try to save names and maps
        if (Game.getPeople() != null) {
            Game.getPeople().saveNames();
        }
        if (Game.getMap() != null) {
            Game.getMap().getMinimap().saveMap();
        }

        // try sending a logout to the server
        if (Game.getNet() != null) {
            final SimpleCmd cmd =
                (SimpleCmd) CommandFactory.getInstance().getCommand(
                    CommandList.CMD_LOGOFF);
            Game.getNet().sendCommand(cmd);

            // wait a little so the command gets send
            try {
                Thread.sleep(SLEEPTIME_LOGOUT);
            } catch (final InterruptedException e) {
                LOGGER.warn("Interrupted waiting time", e);
            }
        }

        LOGGER.info("Client terminated on user request.");

        LOGGER.fatal(Lang.getMsg(message));
        LOGGER.fatal("Terminating client!");

        INSTANCE.cfg.save();
        LogManager.shutdown();

        new Thread(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null, Lang.getMsg(message),
                    "Error", JOptionPane.ERROR_MESSAGE);
                startFinalKiller();
            }
        }).start();
    }

    /**
     * Quit the client and restart the connection with the login screen right
     * away.
     * 
     * @param message the message that shall be displayed in the login screen
     */
    public static void fallbackToLogin(final String message) {
        if (Game.getInstance().isRunning()) {
            errorMessage = message;
            requestedReconnect = true;
            INSTANCE.quitGame();
        }
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
     * Get the full path to a file. This includes the default path that was set
     * up and the name of the file this function gets.
     * 
     * @param name the name of the file that shall be append to the folder
     * @return the full path to a file
     */
    public static String getFile(final String name) {
        return new File(DirectoryManager.getInstance().getUserDirectory(),
            name).getAbsolutePath();
    }

    /**
     * Get the singleton instance of this client main object.
     * 
     * @return the singleton instance of this class
     */
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
    public static String getVersionText() {
        return "Illarion Client " + VERSION; //$NON-NLS-1$
    }

    /**
     * Check if a debug flag is set or not.
     * 
     * @param flag the debug flag that shall be checked
     * @return true in case the flag is enabled, false if not
     */
    public static boolean isDebug(final Debug flag) {
        return (INSTANCE.debugLevel & (1 << flag.ordinal())) > 0;
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

        try {
            tempProps.load(getResource("logging.properties"));
        } catch (final IOException e1) {
            LOGGER.error("Error settings up logging system.", e1);
        }

        // in case the server is now known, update the files if needed and
        // launch the client.
        if (folder != null) {
            INSTANCE.prepareConfig();
            INSTANCE.initLogfiles();

            if (!INSTANCE.integrityCheck()) {
                System.exit(-1);
            }

            // Prepare the Engines
            if ((INSTANCE.disableComponents & COMPONENT_JOGL) == COMPONENT_JOGL) {
                if ((INSTANCE.disableComponents & COMPONENT_LWJGL) == COMPONENT_LWJGL) {
                    LOGGER.fatal("No OpenGL avaiable.");
                    System.exit(-1);
                } else {
                    Graphics.getInstance().setEngine(
                        illarion.graphics.Engines.lwjgl);
                    InputManager.getInstance().setEngine(
                        illarion.input.Engines.lwjgl);
                    getCfg().set("engine", 0);
                }
            } else {
                if ((INSTANCE.disableComponents & COMPONENT_LWJGL) == COMPONENT_LWJGL) {
                    Graphics.getInstance().setEngine(
                        illarion.graphics.Engines.jogl);
                    Graphics
                        .getInstance()
                        .getRenderDisplay()
                        .setDisplayOptions("jogl.newt",
                            Boolean.FALSE.toString());
                    InputManager.getInstance().setEngine(
                        illarion.input.Engines.java);
                    getCfg().set("engine", 1);
                } else {
                    if (getCfg().getInteger("engine") == 0) {
                        Graphics.getInstance().setEngine(
                            illarion.graphics.Engines.lwjgl);
                        InputManager.getInstance().setEngine(
                            illarion.input.Engines.lwjgl);
                    } else {
                        Graphics.getInstance().setEngine(
                            illarion.graphics.Engines.jogl);
                        Graphics
                            .getInstance()
                            .getRenderDisplay()
                            .setDisplayOptions("jogl.newt",
                                Boolean.FALSE.toString());
                        InputManager.getInstance().setEngine(
                            illarion.input.Engines.java);
                    }
                }
            }

            CrashReporter.getInstance().setConfig(getCfg());

            INSTANCE.setupGraphicQuality();

            // show the main window
            System.out.write(0xFF);
            ClientWindow.getInstance().init();
            INSTANCE.initInput();

            // update read-me file if required
            installFile("readme.txt");

            // update manual files if required
            installFile("manual_de.pdf");
            installFile("manual_en.pdf");

            boolean loadingDone = false;
            boolean debugThread = false;

            do {
                requestedReconnect = false;

                Scheduler.getInstance().start();
                // create the login dialog in a separated thread so we can
                // update
                // the display with the main thread
                displayLoginScreen(args);

                // slowdown time to we get a few display renders before the slow
                // graphic loading part starts
                int slowdown = SLEEP_TEXTURE;
                while (!INSTANCE.loginDone) {
                    try {
                        Thread.sleep(SLEEP_RENDER);
                    } catch (final InterruptedException e) {
                        e.notify();
                    }

                    if (!loadingDone) {
                        if (slowdown == 0) {
                            SessionManager.getInstance().addMember(
                                Game.getInstance());

                            LOGGER.info("Loading ressources and factories "
                                + "done");

                            loadingDone = true;
                        } else if (slowdown > 0) {
                            --slowdown;
                        }
                    }
                }
                INSTANCE.cfg.save();
                if (isDebug(Debug.deadlock) && !debugThread) {
                    new ThreadDeadlockDetector()
                        .addListener(new DefaultDeadlockListener());
                    debugThread = true;
                }

                if (!INSTANCE.loginResult) {
                    INSTANCE.exitLWJGL();
                    LOGGER.info("Client terminated on user request "
                        + "before login.");
                    LogManager.shutdown();
                    StoppableStorage.getInstance().shutdown();
                    startFinalKiller();
                    return;
                }

                // establish connection
                SessionManager.getInstance().startSession();

                if (!Game.getInstance().isRunning()) {
                    continue;
                }

                // init the talking log system
                ChatLog.getInstance().init(tempProps);

                // wait until the network starts working
                while (Game.getInstance().isRunning()
                    && !Game.getNet().receivedAnything()) {

                    try {
                        Thread.sleep(30);
                    } catch (final InterruptedException e) {
                        LOGGER.debug("Waiting for the server got interupted"
                            + " unexpected");
                    }
                }

                ClientWindow.getInstance().focus();

                LoadingScreen.getInstance().setLoadingDone();
                ClientWindow.getInstance().getRenderDisplay().hideCursor();
                ClientWindow.getInstance().getRenderDisplay().startRendering();

                /*
                 * Main game loop, as long as the game is running this function
                 * is not left
                 */
                Game.getInstance().gameLoop();

                // Shut the game down correctly
                SessionManager.getInstance().endSession();
                ClientWindow.getInstance().getRenderDisplay().showCursor();
                INSTANCE.cfg.save();
            } while (requestedReconnect && !errorQuitting);

            if (!errorQuitting) {
                SessionManager.getInstance().shutdownSession();

                INSTANCE.exitLWJGL();

                LOGGER.info("Client terminated on user request.");
                LogManager.shutdown();
            } else {
                INSTANCE.exitLWJGL();
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (final InterruptedException e) {
                        // nothing
                    }
                }
            }
        }

        CrashReporter.getInstance().waitForReport();

        startFinalKiller();
    }

    /**
     * This method is the final one to be called before the client is killed for
     * sure. It gives the rest of the client 10 seconds before it forcefully
     * shuts down everything. This is used to ensure that the client quits when
     * it has to, but in case it does so faster, it won't be killed like that.
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
     * Get if there is currently a exit request pending. Means of the really
     * exit dialog is opened or not.
     * 
     * @return true in case the exit dialog is currently displayed
     */
    protected static boolean getExitRequested() {
        return exitRequested;
    }

    /**
     * Check if the login that was performed is okay or not.
     * 
     * @return true in case the login part is done and the user set up all
     *         needed data to perform the login.
     */
    protected static boolean getLoginOkay() {
        return INSTANCE.loginResult;
    }

    /**
     * Update the values for the result of the update. This sets the login state
     * to done and the result to the value of the parameter.
     * 
     * @param result true in case the login is fine, false in case something
     *            went wrong
     */
    protected static void updateLoginResult(final boolean result) {
        INSTANCE.loginDone = true;
        INSTANCE.loginResult = result;
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
     * Show the login screen.
     * 
     * @param args the arguments of the login screen that allow to log the
     *            client in right away without showing the login selection
     */
    @SuppressWarnings("nls")
    private static void displayLoginScreen(final String[] args) {
        INSTANCE.loginDone = false;
        final Thread loginScreenThread = new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean result =
                    IllaClient.getInstance().prepareLogin(args);
                updateLoginResult(result);
                if (!result) {
                    Game.getInstance().cancelInit();
                }
            }
        }, "Login Screen");
        loginScreenThread.setPriority(Thread.MAX_PRIORITY);
        loginScreenThread.start();
    }

    /**
     * Install a file from the jar files to the client directory.
     * 
     * @param fileName the name of the file that shall be stored
     * @return true in case the file was stored, false if not.
     */
    private static boolean installFile(final String fileName) {
        return installFile(fileName, new File(getFile(fileName)));
    }

    /**
     * Install a file from a jar in the client directory. Existing files are
     * only updates if needed.
     * 
     * @param fileName the filename of the source file
     * @param outputFile the full path to the target file
     * @return true in case the file got stored or if there was no update needed
     */
    @SuppressWarnings("nls")
    private static boolean installFile(final String fileName,
        final File outputFile) {
        final InputStream input = getResource(fileName);
        if (input == null) {
            return false;
        }
        FileOutputStream out = null;
        try {
            if (input.available() != outputFile.length()) {
                out = new FileOutputStream(outputFile);

                final byte[] buffer = new byte[FILE_BUFFER];
                int read;
                while ((read = input.read(buffer)) >= 0) {
                    out.write(buffer, 0, read);
                }
            }
            input.close();
        } catch (final FileNotFoundException e) {
            LOGGER.error(fileName + " not found", e);
        } catch (final IOException e) {
            LOGGER.error(fileName + " not accessible", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                out = null;
            } catch (final IOException e) {
                LOGGER.error(fileName + " not accessible", e);
            }
        }
        return true;
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
        // send logoff command
        if (Game.getNet() == null) {
            return;
        }
        final SimpleCmd cmd =
            (SimpleCmd) CommandFactory.getInstance().getCommand(
                CommandList.CMD_LOGOFF);
        Game.getNet().sendCommand(cmd);

        Game.getInstance().setRunning(false);
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
     * Prepare the login of the client. Means parsing the arguments in case
     * there are any or show the login dialog in order to fetch the needed
     * informations.
     * 
     * @param args the arguments that were handed over to the client call
     * @return true in case all data is fine and the client is ready to login,
     *         false in case something when wrong
     */
    @SuppressWarnings("nls")
    boolean prepareLogin(final String[] args) {
        final LoginDialog login =
            new LoginDialog(Game.getInstance(), ClientWindow.getInstance()
                .getFrame());

        if (!errorMessage.equals("")) {
            login.setErrorMessage(errorMessage);
        }

        // evaluate parameters
        // check parameters for login info
        boolean quickstart = false;
        int forceDebug = -1;
        int loginIndex = 0;

        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-login") && ((i + 2) < args.length)) {
                quickstart = true;
                loginIndex = i + 1;
            } else if (args[i].equals("-server:game") && MULTI_CLIENT) {
                login.setServer(1);
            } else if (args[i].equals("-server:test") && MULTI_CLIENT) {
                login.setServer(0);
            } else if (args[i].equals("-debug")) {
                forceDebug = Integer.parseInt(args[i + 1]);
            }
        }

        // repeat until user cancels login dialog
        // bypass login dialog upon first start with parameters
        if (quickstart || login.display()) {
            // detect the used server
            final int selectedServer = login.getServer();
            for (final Servers server : Servers.values()) {
                if (selectedServer == server.ordinal()) {
                    usedServer = server;
                }
            }

            // set login info from parameters
            if (quickstart) {
                Game.getInstance().setLogin(args[loginIndex],
                    args[loginIndex + 1]);
                // use parameters only once
                quickstart = false;
            }

            if (forceDebug > -1) {
                debugLevel = forceDebug;
            } else {
                debugLevel = cfg.getInteger("debugLevel");
            }

            // ready of loading the client.
            return true;
        }

        // something failed, lets stop it right here.
        return false;
    }

    /**
     * Check of a component is listed as loaded.
     * 
     * @param comp the component to check
     * @return <code>true</code> in case the component is load
     */
    private boolean componentLoaded(final String comp) {
        return Boolean.toString(true).equalsIgnoreCase(
            System.getProperty("illarion.components.avaiable." + comp));
    }

    /**
     * Shut down the LWJGL driven parts of the client. So the mouse, the
     * keyboard as well as the display.
     */
    private void exitLWJGL() {
        // InputManager.getInstance().getKeyboardManager().shutdown();
        // InputManager.getInstance().getMouseManager().shutdown();
        ClientWindow.getInstance().destruct();
    }

    /**
     * Prepare the input handler for keyboard and mouse.
     */
    private void initInput() {
        InputManager.getInstance().getKeyboardManager().startManager();
        InputManager.getInstance().getMouseManager().startManager();
    }

    /**
     * Basic initialization of the log files and the debug settings.
     */
    @SuppressWarnings("nls")
    private void initLogfiles() {
        tempProps.put("log4j.appender.IllaLogfileAppender.file",
            getFile("error.log"));
        tempProps.put("log4j.appender.ChatAppender.file",
            getFile("illarion.log"));
        tempProps.put("log4j.reset", "true");
        new PropertyConfigurator().doConfigure(tempProps,
            LOGGER.getLoggerRepository());

        Thread.setDefaultUncaughtExceptionHandler(DefaultCrashHandler
            .getInstance());

        LOGGER.info(getVersionText() + " started.");
        LOGGER.info("VM: " + System.getProperty("java.version"));
        LOGGER.info("OS: " + System.getProperty("os.name") + " "
            + System.getProperty("os.version") + " "
            + System.getProperty("os.arch"));
    }

    /**
     * This function checks if all components the client requires are load and
     * ready to be used.
     */
    @SuppressWarnings("nls")
    private boolean integrityCheck() {
        boolean result = true;
        if (componentLoaded("javolution")) {
            LOGGER
                .debug("Checking Javolution............................[OK]");
        } else {
            result = false;
            LOGGER
                .error("Checking Javolution........................[FAILED]");
        }

        if (componentLoaded("jorbis")) {
            LOGGER
                .debug("Checking JOrbis................................[OK]");
        } else {
            result = false;
            LOGGER
                .error("Checking JOrbis............................[FAILED]");
        }

        if (componentLoaded("jogg")) {
            LOGGER
                .debug("Checking JOgg..................................[OK]");
        } else {
            result = false;
            LOGGER
                .error("Checking JOgg..............................[FAILED]");
        }

        if (componentLoaded("tritonus")) {
            LOGGER
                .debug("Checking Tritonus..............................[OK]");
        } else {
            result = false;
            LOGGER
                .error("Checking Tritonus..........................[FAILED]");
        }

        if (componentLoaded("vorbisspi")) {
            LOGGER
                .debug("Checking VorbisSPI.............................[OK]");
        } else {
            result = false;
            LOGGER
                .error("Checking VorbisSPI.........................[FAILED]");
        }

        if (componentLoaded("trove")) {
            LOGGER
                .debug("Checking GNU Trove.............................[OK]");
        } else {
            result = false;
            LOGGER
                .error("Checking GNU Trove.........................[FAILED]");
        }

        if (componentLoaded("gluegen")) {
            LOGGER
                .debug("Checking Gluegen...............................[OK]");
        } else {
            disableComponents |= COMPONENT_JOGL;
            LOGGER
                .warn("Checking Gluegen............................[FAILED]");
        }

        if (componentLoaded("jogl")) {
            LOGGER
                .debug("Checking JOGL..................................[OK]");
        } else {
            disableComponents |= COMPONENT_JOGL;
            LOGGER
                .warn("Checking JOGL...............................[FAILED]");
        }

        if (componentLoaded("nativewindow")) {
            LOGGER
                .debug("Checking Nativewindow..........................[OK]");
        } else {
            disableComponents |= COMPONENT_JOGL;
            LOGGER
                .warn("Checking Nativewindow.......................[FAILED]");
        }

        if (componentLoaded("newt")) {
            LOGGER
                .debug("Checking Newt..................................[OK]");
        } else {
            disableComponents |= COMPONENT_JOGL;
            LOGGER.warn("Checking Newt..............................[FAILED]");
        }

        if (componentLoaded("lwjgl")) {
            LOGGER
                .debug("Checking LWJGL.................................[OK]");
        } else {
            disableComponents |= COMPONENT_LWJGL;
            LOGGER
                .warn("Checking LWJGL..............................[FAILED]");
        }

        return result;
    }

    /**
     * Prepare the configuration system and the decryption system.
     */
    @SuppressWarnings("nls")
    private void prepareConfig() {
        cfg = new ConfigSystem(getFile("Illarion.xcfgz"));
        cfg.setDefault("debugLevel", 1);
        cfg.setDefault("lowMemory", LoginDialog.SETTINGS_GRAPHIC_NORMAL);
        cfg.setDefault("showNameMode", illarion.client.world.People.NAME_SHORT);
        cfg.setDefault("showIDs", false);
        cfg.setDefault("soundOn", true);
        cfg.setDefault("soundVolume",
            (int) illarion.client.world.Player.MAX_CLIENT_VOL);
        cfg.setDefault("musicOn", true);
        cfg.setDefault("musicVolume",
            (int) (illarion.client.world.Player.MAX_CLIENT_VOL * 0.75f));
        cfg.setDefault(illarion.client.util.ChatLog.CFG_TEXTLOG, true);
        cfg.setDefault("fadingTime", 600);
        cfg.setDefault(ClientWindow.CFG_FULLSCREEN, false);
        cfg.setDefault(ClientWindow.CFG_RESOLUTION, new GraphicResolution(800,
            600, 32, 60).toString());
        cfg.setDefault("savePassword", false);
        cfg.setDefault(CrashReporter.CFG_KEY, CrashReporter.MODE_ASK);
        cfg.setDefault("engine", 1);
        cfg.setDefault(Journal.CFG_JOURNAL_LENGTH, 100);
        cfg.setDefault(Journal.CFG_JOURNAL_FONT,
            Journal.CFG_JOURNAL_FONT_LARGE);

        final String locale = cfg.getString(Lang.LOCALE_CFG);
        if (locale == null) {
            final String jnlpLocale =
                System.getProperty("illarion.client.locale");
            if ((jnlpLocale == null)
                || jnlpLocale.equals(Lang.LOCALE_CFG_ENGLISH)) {
                cfg.set(Lang.LOCALE_CFG, Lang.LOCALE_CFG_ENGLISH);
            } else {
                cfg.set(Lang.LOCALE_CFG, Lang.LOCALE_CFG_GERMAN);
            }
        }

        final Crypto crypt = new Crypto();
        crypt.loadPublicKey();
        TableLoader.setCrypto(crypt);
        Lang.getInstance().setConfig(cfg);
    }

    /**
     * Setup the graphic port regarding the configuration for the requested
     * graphic quality.
     */
    @SuppressWarnings("nls")
    private void setupGraphicQuality() {
        // setup the graphic environment
        final int lowMemory = INSTANCE.cfg.getInteger("lowMemory");
        switch (lowMemory) {
            case LoginDialog.SETTINGS_GRAPHIC_MAX: // maximal quality
                Graphics.getInstance().setQuality(Graphics.QUALITY_MAX);
                break;
            case LoginDialog.SETTINGS_GRAPHIC_HIGH: // high quality
                Graphics.getInstance().setQuality(Graphics.QUALITY_HIGH);
                break;
            case LoginDialog.SETTINGS_GRAPHIC_NORMAL: // normal quality
                Graphics.getInstance().setQuality(Graphics.QUALITY_NORMAL);
                break;
            case LoginDialog.SETTINGS_GRAPHIC_LOW: // low quality
                Graphics.getInstance().setQuality(Graphics.QUALITY_LOW);
                break;
            case LoginDialog.SETTINGS_GRAPHIC_MIN: // minimal quality
                Graphics.getInstance().setQuality(Graphics.QUALITY_MIN);
                break;
            default:
                Graphics.getInstance().setQuality(Graphics.QUALITY_HIGH);
        }
    }
}

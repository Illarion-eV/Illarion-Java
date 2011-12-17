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
import org.bushe.swing.event.EventServiceExistsException;
import org.bushe.swing.event.EventServiceLocator;
import org.bushe.swing.event.ThreadSafeEventService;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.renderer.Renderer;

import de.lessvoid.nifty.slick2d.loaders.SlickAddLoaderLocation;
import de.lessvoid.nifty.slick2d.loaders.SlickRenderFontLoaders;
import de.lessvoid.nifty.slick2d.loaders.SlickRenderImageLoaders;

import illarion.client.crash.DefaultCrashHandler;
import illarion.client.net.CommandFactory;
import illarion.client.net.CommandList;
import illarion.client.net.client.SimpleCmd;
import illarion.client.resources.SongFactory;
import illarion.client.resources.SoundFactory;
import illarion.client.resources.loaders.SongLoader;
import illarion.client.resources.loaders.SoundLoader;
import illarion.client.util.ChatLog;
import illarion.client.util.Lang;
import illarion.client.world.MapDimensions;
import illarion.client.world.World;

import illarion.common.bug.CrashReporter;
import illarion.common.config.Config;
import illarion.common.config.ConfigSystem;
import illarion.common.graphics.FontLoader;
import illarion.common.graphics.GraphicResolution;
import illarion.common.graphics.TextureLoader;
import illarion.common.util.Crypto;
import illarion.common.util.DirectoryManager;
import illarion.common.util.Scheduler;
import illarion.common.util.StoppableStorage;
import illarion.common.util.TableLoader;


/**
 * Main Class of the Illarion Client, this loads up the whole game and runs the
 * main loop of the Illarion Client.
 * 
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
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
     * The class loader of the this class. It is used to get the resource
     * streams that contain the resource data of the client.
     */
    private final ClassLoader rscLoader = IllaClient.class.getClassLoader();

    /**
     * Stores the server the client shall connect to.
     */
    private Servers usedServer = DEFAULT_SERVER;
    
    /**
     * This is the reference to the Illarion Game instance.
     */
    private illarion.client.Game game;
    
    /**
     * The container that is used to display the game.
     */
    private AppGameContainer gameContainer;

    /**
     * The default empty constructor used to create the singleton instance of
     * this class.
     */
    private IllaClient() {

    }
    
    private void init() {
        prepareConfig();
        initLogfiles();
        
        CrashReporter.getInstance().setConfig(getCfg());
        
        try {
            EventServiceLocator.setEventService(EventServiceLocator.EVENT_BUS_CLASS, new ThreadSafeEventService());
        } catch (EventServiceExistsException e1) {
            LOGGER.error("Failed preparing the EventBus. Settings the Service handler happened too late");
        }

        Renderer.setRenderer(Renderer.VERTEX_ARRAY_RENDERER);
        
        SlickRenderImageLoaders.getInstance().addLoader(TextureLoader.getInstance(), SlickAddLoaderLocation.first);
        SlickRenderFontLoaders.getInstance().addLoader(FontLoader.getInstance(), SlickAddLoaderLocation.first);
        
        // Preload sound and music
        new SongLoader().setTarget(SongFactory.getInstance()).load();
        new SoundLoader().setTarget(SoundFactory.getInstance()).load();
        
        game = new illarion.client.Game();
        
        final GraphicResolution res = new GraphicResolution(cfg.getString(CFG_RESOLUTION));
        
        try {
            gameContainer = new AppGameContainer(game, res.getWidth(), res.getHeight(), cfg.getBoolean(CFG_FULLSCREEN));
            MapDimensions.getInstance().reportScreenSize(gameContainer.getWidth(), gameContainer.getHeight());
        } catch (SlickException e) {
            System.err.println("Fatal error creating game screen!!!");
            System.exit(-1);
        }
        
        gameContainer.setAlwaysRender(true);
        //gameContainer.setTargetFrameRate(60);
        
        try {
            gameContainer.start();
        } catch (SlickException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
     * Show a question frame if the client shall really quit and exit the client
     * in case the user selects yes.
     */
    public static void ensureExit() {
        if (exitRequested) {
            return;
        }
        exitRequested = true;
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
        INSTANCE.game.enterState(Game.STATE_LOGIN);
        World.cleanEnvironment();
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

        try {
            tempProps.load(getResource("logging.properties"));
        } catch (final IOException e1) {
            LOGGER.error("Error settings up logging system.", e1);
        }

        // in case the server is now known, update the files if needed and
        // launch the client.
        if (folder == null) {
            return;
        }
        
        INSTANCE.init();

        // update read-me file if required
        installFile("readme.txt");

        // update manual files if required
        installFile("manual_de.pdf");
        installFile("manual_en.pdf");
       
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
        if (World.getNet() == null) {
            return;
        }
        final SimpleCmd cmd =
            (SimpleCmd) CommandFactory.getInstance().getCommand(
                CommandList.CMD_LOGOFF);
        World.getNet().sendCommand(cmd);
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
        
        
        java.util.logging.Logger.getAnonymousLogger().getParent().setLevel(java.util.logging.Level.SEVERE);
        java.util.logging.Logger.getLogger("de.lessvoid.nifty.*").setLevel(java.util.logging.Level.SEVERE);
        java.util.logging.Logger.getLogger("javolution").setLevel(java.util.logging.Level.SEVERE);
    }

    private static final String CFG_FULLSCREEN = "fullscreen";
    private static final String CFG_RESOLUTION = "resolution";

    /**
     * Prepare the configuration system and the decryption system.
     */
    @SuppressWarnings("nls")
    private void prepareConfig() {
        cfg = new ConfigSystem(getFile("Illarion.xcfgz"));
        cfg.setDefault("debugLevel", 1);
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
        cfg.setDefault(CFG_FULLSCREEN, false);
        cfg.setDefault(CFG_RESOLUTION, new GraphicResolution(800,
            600, 32, 60).toString());
        cfg.setDefault("savePassword", false);
        cfg.setDefault(CrashReporter.CFG_KEY, CrashReporter.MODE_ASK);
        cfg.setDefault("engine", 1);

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
}

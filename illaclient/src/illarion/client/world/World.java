package illarion.client.world;

import java.util.logging.Logger;

import illarion.client.graphics.AnimationManager;
import illarion.client.graphics.Avatar;
import illarion.client.graphics.MapDisplayManager;
import illarion.client.net.NetComm;
import illarion.client.util.ChatHandler;
import illarion.common.graphics.LightTracer;
import illarion.common.util.StoppableStorage;

/**
 * This class is used to unify the access to the different components of the
 * game and to ensure a proper initialization and cleaning of those components
 * if needed.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class World {
    /**
     * The singleton instance of this class.
     */
    private static final World INSTANCE = new World();
    
    /**
     * The logger of this class.
     */
    private final Logger log;

    /**
     * Private constructor to ensure the sole instance is the singleton instance.
     */
    private World() {
        log = Logger.getLogger(World.class.getName());
    }
    
    /**
     * The animation manager that takes care for updating and synchronizing
     * the animations in the game.
     */
    private AnimationManager aniManager;
    
    /**
     * The class that handles the chat in and output.
     */
    private ChatHandler chatHandler;

    /**
     * The instance of the map of the game.
     */
    private GameMap map;
    
    /**
     * The map display manager that takes care for rendering the map.
     */
    private MapDisplayManager mapDisplay;
    
    /**
     * The instance of the light tracer of the game.
     */
    private LightTracer lights;
    
    /**
     * The instance of the player who plays currently.
     */
    private Player player;
    
    /**
     * The instance of the people storage that is used in this session.
     */
    private People people;
    
    /**
     * The network communication interface that is used in this game session.
     */
    private NetComm net;
    
    /**
     * The weather class that is used to maintain the current weather and
     * display it.
     */
    private Weather weather;
    
    /**
     * The music box that takes care for playing the proper music.
     */
    private MusicBox musicBox;

    /**
     * Get the map of the game.
     * 
     * @return the map of the game
     */
    public static GameMap getMap() {
        INSTANCE.checkGameMap();
        return INSTANCE.map;
    }
    
    public static MapDisplayManager getMapDisplay() {
        INSTANCE.checkMapDisplay();
        return INSTANCE.mapDisplay;
    }

    public static LightTracer getLights() {
        INSTANCE.checkLights();
        return INSTANCE.lights;
    }

    public static Player getPlayer() {
        INSTANCE.checkPlayer();
        return INSTANCE.player;
    }
    
    public static Avatar getAvatar() {
        return getPlayer().getCharacter().getAvatar();
    }

    public static People getPeople() {
        INSTANCE.checkPeople();
        return INSTANCE.people;
    }

    public static NetComm getNet() {
        INSTANCE.checkNet();
        return INSTANCE.net;
    }

    public static AnimationManager getAnimationManager() {
        INSTANCE.checkAniManager();
        return INSTANCE.aniManager;
    }

    public static Weather getWeather() {
        INSTANCE.checkWeather();
        return INSTANCE.weather;
    }
    
    public static ChatHandler getChatHandler() {
        INSTANCE.checkChatHandler();
        return INSTANCE.chatHandler;
    }
    
    public static MusicBox getMusicBox() {
        INSTANCE.checkMusicBox();
        return INSTANCE.musicBox;
    }

    public static void cleanEnvironment() {
        INSTANCE.log.entering(World.class.getName(),
            "cleanEnvironment()");

        synchronized (INSTANCE) {
            StoppableStorage.getInstance().shutdown();
            
            if (INSTANCE.net != null) {
                INSTANCE.net.disconnect();
            }
            
            INSTANCE.chatHandler = null;
            INSTANCE.aniManager = null;
            INSTANCE.map = null;
            INSTANCE.musicBox = null;
            INSTANCE.lights = null;
            INSTANCE.player = null;
            INSTANCE.people = null;
            INSTANCE.net = null;
            INSTANCE.weather = null;
            
        }

        INSTANCE.log.exiting(World.class.getName(),
            "cleanEnvironment()");
    }
    
    public static void initMissing() {
        getAnimationManager();
        getChatHandler();
        getMap();
        getMusicBox();
        getLights();
        getPlayer();
        getPeople();
        getNet();
        getWeather();
    }

    private void checkAniManager() {
        if (aniManager == null) {
            synchronized (this) {
                aniManager = new AnimationManager();
            }
        }
    }

    private void checkGameMap() {
        if (map == null) {
            synchronized (this) {
                map = new GameMap();
            }
        }
    }
    
    private void checkMapDisplay() {
        if (mapDisplay == null) {
            synchronized (this) {
                mapDisplay = new MapDisplayManager();
            }
        }
    }

    private void checkLights() {
        if (lights == null) {
            synchronized (this) {
                lights = new LightTracer(getMap());
                lights.start();
            }
        }
    }

    private void checkPlayer() {
        if (player == null) {
            synchronized (this) {
                player = new Player();
            }
        }
    }

    private void checkPeople() {
        if (people == null) {
            synchronized (this) {
                people = new People();
            }
        }
    }

    private void checkNet() {
        if (net == null) {
            synchronized (this) {
                net = new NetComm();
            }
        }
    }

    private void checkWeather() {
        if (weather == null) {
            synchronized (this) {
                weather = new Weather();
            }
        }
    }

    private void checkChatHandler() {
        if (chatHandler == null) {
            synchronized (this) {
                chatHandler = new ChatHandler();
            }
        }
    }

    private void checkMusicBox() {
        if (musicBox == null) {
            synchronized (this) {
                musicBox = new MusicBox();
            }
        }
    }
}

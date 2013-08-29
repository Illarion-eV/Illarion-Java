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
package illarion.client.world;

import illarion.client.graphics.AnimationManager;
import illarion.client.graphics.MapDisplayManager;
import illarion.client.gui.GameGui;
import illarion.client.gui.controller.GameScreenController;
import illarion.client.net.NetComm;
import illarion.client.util.ChatHandler;
import illarion.client.util.UpdateTaskManager;
import illarion.client.world.interactive.InteractionManager;
import illarion.common.util.StoppableStorage;
import org.illarion.engine.Engine;
import org.illarion.engine.EngineException;
import org.illarion.engine.graphic.LightTracer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * This class is used to unify the access to the different components of the game and to ensure a proper
 * initialization and cleaning of those components if needed.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ThreadSafe
public final class World {
    /**
     * The singleton instance of this class.
     */
    @Nonnull
    private static final World INSTANCE = new World();

    /**
     * Shutdown every class that is currently maintained by the world.
     */
    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    public static void cleanEnvironment() {
        synchronized (INSTANCE) {
            StoppableStorage.getInstance().shutdown();

            if (INSTANCE.net != null) {
                //noinspection ConstantConditions
                INSTANCE.net.disconnect();
            }
            if (INSTANCE.player != null) {
                //noinspection ConstantConditions
                INSTANCE.player.shutdown();
            }
            if (INSTANCE.map != null) {
                //noinspection ConstantConditions
                INSTANCE.map.getMiniMap().saveAllMaps();
            }

            INSTANCE.chatHandler = null;
            INSTANCE.aniManager = null;
            INSTANCE.interactionManager = null;
            INSTANCE.map = null;
            INSTANCE.musicBox = null;
            INSTANCE.lights = null;

            INSTANCE.player = null;
            INSTANCE.people = null;
            INSTANCE.net = null;
            INSTANCE.weather = null;
            INSTANCE.clock = null;
            INSTANCE.updateManager = null;

        }
    }

    @Nonnull
    public static AnimationManager getAnimationManager() {
        final AnimationManager instance = INSTANCE.aniManager;
        if (instance == null) {
            throw new IllegalStateException("World is not yet initialized");
        }
        return instance;
    }

    @Nonnull
    public static Clock getClock() {
        final Clock instance = INSTANCE.clock;
        if (instance == null) {
            throw new IllegalStateException("World is not yet initialized");
        }
        return instance;
    }

    @Nonnull
    public static ChatHandler getChatHandler() {
        final ChatHandler instance = INSTANCE.chatHandler;
        if (instance == null) {
            throw new IllegalStateException("World is not yet initialized");
        }
        return instance;
    }

    @Nonnull
    public static LightTracer getLights() {
        final LightTracer instance = INSTANCE.lights;
        if (instance == null) {
            throw new IllegalStateException("World is not yet initialized");
        }
        return instance;
    }

    /**
     * Get the map of the game.
     *
     * @return the map of the game
     */
    @Nonnull
    public static GameMap getMap() {
        final GameMap instance = INSTANCE.map;
        if (instance == null) {
            throw new IllegalStateException("World is not yet initialized");
        }
        return instance;
    }

    @Nonnull
    public static MapDisplayManager getMapDisplay() {
        final MapDisplayManager instance = INSTANCE.mapDisplay;
        if (instance == null) {
            throw new IllegalStateException("World is not yet initialized");
        }
        return instance;
    }

    @Nonnull
    public static MusicBox getMusicBox() {
        final MusicBox instance = INSTANCE.musicBox;
        if (instance == null) {
            throw new IllegalStateException("World is not yet initialized");
        }
        return instance;
    }

    @Nonnull
    public static NetComm getNet() {
        final NetComm instance = INSTANCE.net;
        if (instance == null) {
            throw new IllegalStateException("World is not yet initialized");
        }
        return instance;
    }

    @Nonnull
    public static People getPeople() {
        final People instance = INSTANCE.people;
        if (instance == null) {
            throw new IllegalStateException("World is not yet initialized");
        }
        return instance;
    }

    @Nonnull
    public static Player getPlayer() {
        final Player instance = INSTANCE.player;
        if (instance == null) {
            throw new IllegalStateException("World is not yet initialized");
        }
        return instance;
    }

    @Nonnull
    public static Weather getWeather() {
        final Weather instance = INSTANCE.weather;
        if (instance == null) {
            throw new IllegalStateException("World is not yet initialized");
        }
        return instance;
    }

    @Nonnull
    public static InteractionManager getInteractionManager() {
        final InteractionManager instance = INSTANCE.interactionManager;
        if (instance == null) {
            throw new IllegalStateException("World is not yet initialized");
        }
        return instance;
    }

    @Nonnull
    public static UpdateTaskManager getUpdateTaskManager() {
        final UpdateTaskManager instance = INSTANCE.updateManager;
        if (instance == null) {
            throw new IllegalStateException("World is not yet initialized");
        }
        return instance;
    }

    @Nonnull
    public static GameGui getGameGui() {
        final GameGui instance = INSTANCE.gameGui;
        if (instance == null) {
            throw new IllegalStateException("World is not yet initialized");
        }
        return instance;
    }

    /**
     * The animation manager that takes care for updating and synchronizing the animations in the game.
     */
    @Nullable
    private AnimationManager aniManager;

    /**
     * The manager that takes care for the interaction between map and GUI.
     */
    @Nullable
    private InteractionManager interactionManager;

    /**
     * The class that handles the Chat in and output.
     */
    @Nullable
    private ChatHandler chatHandler;

    /**
     * The instance of the clock used to keep track of the time in Illarion.
     */
    @Nullable
    private Clock clock;

    /**
     * The instance of the light tracer of the game.
     */
    @Nullable
    private LightTracer lights;

    /**
     * The instance of the map of the game.
     */
    @Nullable
    private GameMap map;

    /**
     * The map display manager that takes care for rendering the map.
     */
    private MapDisplayManager mapDisplay;

    /**
     * The music box that takes care for playing the proper music.
     */
    @Nullable
    private MusicBox musicBox;

    /**
     * The network communication interface that is used in this game session.
     */
    @Nullable
    private NetComm net;

    /**
     * The instance of the people storage that is used in this session.
     */
    @Nullable
    private People people;

    /**
     * The instance of the player who plays currently.
     */
    @Nullable
    private Player player;

    /**
     * The weather class that is used to maintain the current weather and
     * display it.
     */
    @Nullable
    private Weather weather;

    /**
     * This update manager takes care for executing tasks in sync with the main loop.
     */
    @Nullable
    private UpdateTaskManager updateManager;

    /**
     * The reference to the GUI of the game.
     */
    @Nullable
    private GameGui gameGui;

    /**
     * Private constructor to ensure the sole instance is the singleton
     * instance.
     */
    private World() {
    }

    /**
     * This variable is set {@code true} once the world is initialized.
     */
    private boolean init;

    /**
     * Prepare all components of the world. This needs to be called before the world is used.
     *
     * @param engine the engine that is used to display the game
     */
    public static synchronized void initWorldComponents(@Nonnull final Engine engine) throws EngineException {
        if (INSTANCE.init) {
            return;
        }
        INSTANCE.init = true;
        INSTANCE.updateManager = new UpdateTaskManager();
        INSTANCE.aniManager = new AnimationManager();
        INSTANCE.chatHandler = new ChatHandler();
        INSTANCE.clock = new Clock();
        INSTANCE.map = new GameMap(engine);
        //noinspection ConstantConditions
        INSTANCE.lights = new LightTracer(INSTANCE.map);
        INSTANCE.mapDisplay = new MapDisplayManager(engine);
        INSTANCE.musicBox = new MusicBox(engine);
        INSTANCE.net = new NetComm();
        INSTANCE.people = new People();
        INSTANCE.player = new Player(engine);
        INSTANCE.weather = new Weather();
        INSTANCE.interactionManager = new InteractionManager();

        INSTANCE.lights.start();
    }

    /**
     * Init the GUI of the game.
     *
     * @param engine the game engine
     */
    public static synchronized void initGui(final Engine engine) {
        INSTANCE.gameGui = new GameScreenController(engine.getInput());
    }
}

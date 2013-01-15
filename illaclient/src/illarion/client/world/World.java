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
import illarion.client.net.NetComm;
import illarion.client.util.ChatHandler;
import illarion.client.world.interactive.InteractionManager;
import illarion.common.graphics.LightTracer;
import illarion.common.util.StoppableStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
                INSTANCE.map.getMinimap().saveMap();
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

            if (INSTANCE.executorService != null) {
                //noinspection ConstantConditions
                INSTANCE.executorService.shutdownNow();
            }
            INSTANCE.executorService = null;

        }
    }

    @Nonnull
    public static AnimationManager getAnimationManager() {
        INSTANCE.checkAniManager();
        //noinspection ConstantConditions
        return INSTANCE.aniManager;
    }

    @Nonnull
    public static Clock getClock() {
        INSTANCE.checkClock();
        //noinspection ConstantConditions
        return INSTANCE.clock;
    }

    @Nonnull
    public static ChatHandler getChatHandler() {
        INSTANCE.checkChatHandler();
        //noinspection ConstantConditions
        return INSTANCE.chatHandler;
    }

    @Nonnull
    public static LightTracer getLights() {
        INSTANCE.checkLights();
        //noinspection ConstantConditions
        return INSTANCE.lights;
    }

    /**
     * Get the map of the game.
     *
     * @return the map of the game
     */
    @Nonnull
    public static GameMap getMap() {
        INSTANCE.checkGameMap();
        //noinspection ConstantConditions
        return INSTANCE.map;
    }

    @Nonnull
    public static MapDisplayManager getMapDisplay() {
        INSTANCE.checkMapDisplay();
        return INSTANCE.mapDisplay;
    }

    @Nonnull
    public static MusicBox getMusicBox() {
        INSTANCE.checkMusicBox();
        //noinspection ConstantConditions
        return INSTANCE.musicBox;
    }

    @Nonnull
    public static NetComm getNet() {
        INSTANCE.checkNet();
        //noinspection ConstantConditions
        return INSTANCE.net;
    }

    @Nonnull
    public static People getPeople() {
        INSTANCE.checkPeople();
        //noinspection ConstantConditions
        return INSTANCE.people;
    }

    @Nonnull
    public static Player getPlayer() {
        INSTANCE.checkPlayer();
        //noinspection ConstantConditions
        return INSTANCE.player;
    }

    @Nonnull
    public static Weather getWeather() {
        INSTANCE.checkWeather();
        //noinspection ConstantConditions
        return INSTANCE.weather;
    }

    @Nonnull
    public static InteractionManager getInteractionManager() {
        INSTANCE.checkInteractionManager();
        //noinspection ConstantConditions
        return INSTANCE.interactionManager;
    }

    @Nonnull
    public static ExecutorService getExecutorService() {
        INSTANCE.checkExecutorService();
        //noinspection ConstantConditions
        return INSTANCE.executorService;
    }

    /**
     * Initialize all components of the world.
     */
    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    public static void initMissing() {
        getAnimationManager();
        getChatHandler();
        getMap();
        getMusicBox();
        getLights();
        getPlayer();
        getPeople();
        getNet();
        getClock();
        getWeather();
        getInteractionManager();
        getExecutorService();
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
     * The executor service that is used for the execution of all concurrent tasks.
     */
    @Nullable
    private ExecutorService executorService;

    /**
     * Private constructor to ensure the sole instance is the singleton
     * instance.
     */
    private World() {
    }

    private void checkAniManager() {
        if (aniManager == null) {
            synchronized (this) {
                aniManager = new AnimationManager();
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

    private void checkClock() {
        if (clock == null) {
            synchronized (this) {
                clock = new Clock();
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

    private void checkLights() {
        if (lights == null) {
            synchronized (this) {
                lights = new LightTracer(getMap());
                lights.start();
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

    private void checkMusicBox() {
        if (musicBox == null) {
            synchronized (this) {
                musicBox = new MusicBox();
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

    private void checkPeople() {
        if (people == null) {
            synchronized (this) {
                people = new People();
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

    private void checkWeather() {
        if (weather == null) {
            synchronized (this) {
                weather = new Weather();
            }
        }
    }

    private void checkInteractionManager() {
        if (interactionManager == null) {
            synchronized (this) {
                interactionManager = new InteractionManager();
            }
        }
    }

    private void checkExecutorService() {
        if (executorService == null) {
            synchronized (this) {
                executorService = Executors.newCachedThreadPool();
            }
        }
    }
}

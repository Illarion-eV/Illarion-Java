/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
package illarion.client.world;

import illarion.client.graphics.AnimationManager;
import illarion.client.graphics.MapDisplayManager;
import illarion.client.gui.GameGui;
import illarion.client.gui.controller.GameScreenController;
import illarion.client.net.NetComm;
import illarion.client.util.ChatHandler;
import illarion.client.util.UpdateTaskManager;
import illarion.client.world.interactive.InteractionManager;
import org.illarion.engine.Engine;
import org.illarion.engine.EngineException;
import org.illarion.engine.graphic.LightTracer;
import org.jetbrains.annotations.Contract;

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

    public static void shutdownWorld() {
        if (INSTANCE.net != null) {
            INSTANCE.net.disconnect();
        }
        if (INSTANCE.player != null) {
            INSTANCE.player.shutdown();
        }
        if (INSTANCE.lights != null) {
            INSTANCE.lights.saveShutdown();
        }
        if (INSTANCE.map != null) {
            INSTANCE.map.getMiniMap().saveAllMaps();
            INSTANCE.map.saveShutdown();
        }
        if (INSTANCE.weather != null) {
            INSTANCE.weather.shutdown();
        }
        if (INSTANCE.musicBox != null) {
            INSTANCE.musicBox.saveShutdown();
        }
    }

    /**
     * Shutdown every class that is currently maintained by the world.
     */
    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    public static void cleanEnvironment() {
        shutdownWorld();

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

        INSTANCE.init = false;
    }

    @Nonnull
    public static AnimationManager getAnimationManager() {
        AnimationManager instance = INSTANCE.aniManager;
        if (instance == null) {
            throw new IllegalStateException("World is not yet initialized");
        }
        return instance;
    }

    @Nonnull
    public static Clock getClock() {
        Clock instance = INSTANCE.clock;
        if (instance == null) {
            throw new IllegalStateException("World is not yet initialized");
        }
        return instance;
    }

    @Nonnull
    public static ChatHandler getChatHandler() {
        ChatHandler instance = INSTANCE.chatHandler;
        if (instance == null) {
            throw new IllegalStateException("World is not yet initialized");
        }
        return instance;
    }

    @Nonnull
    public static LightTracer getLights() {
        LightTracer instance = INSTANCE.lights;
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
        GameMap instance = INSTANCE.map;
        if (instance == null) {
            throw new IllegalStateException("World is not yet initialized");
        }
        return instance;
    }

    @Nonnull
    public static MapDisplayManager getMapDisplay() {
        MapDisplayManager instance = INSTANCE.mapDisplay;
        if (instance == null) {
            throw new IllegalStateException("World is not yet initialized");
        }
        return instance;
    }

    @Nonnull
    public static MusicBox getMusicBox() {
        MusicBox instance = INSTANCE.musicBox;
        if (instance == null) {
            throw new IllegalStateException("World is not yet initialized");
        }
        return instance;
    }

    @Nonnull
    public static NetComm getNet() {
        NetComm instance = INSTANCE.net;
        if (instance == null) {
            throw new IllegalStateException("World is not yet initialized");
        }
        return instance;
    }

    @Nonnull
    public static People getPeople() {
        People instance = INSTANCE.people;
        if (instance == null) {
            throw new IllegalStateException("World is not yet initialized");
        }
        return instance;
    }

    @Nonnull
    public static Player getPlayer() {
        Player instance = INSTANCE.player;
        if (instance == null) {
            throw new IllegalStateException("World is not yet initialized");
        }
        return instance;
    }

    @Nonnull
    public static Weather getWeather() {
        Weather instance = INSTANCE.weather;
        if (instance == null) {
            throw new IllegalStateException("World is not yet initialized");
        }
        return instance;
    }

    @Nonnull
    public static InteractionManager getInteractionManager() {
        InteractionManager instance = INSTANCE.interactionManager;
        if (instance == null) {
            throw new IllegalStateException("World is not yet initialized");
        }
        return instance;
    }

    @Nonnull
    public static UpdateTaskManager getUpdateTaskManager() {
        UpdateTaskManager instance = INSTANCE.updateManager;
        if (instance == null) {
            throw new IllegalStateException("World is not yet initialized");
        }
        return instance;
    }

    @Nonnull
    public static GameGui getGameGui() {
        GameGui instance = INSTANCE.gameGui;
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
     * @param characterName the name of the character the world is being initialized for
     */
    public static void initWorldComponents(@Nonnull Engine engine, @Nonnull String characterName) throws EngineException {
        if (INSTANCE.init) {
            return;
        }
        INSTANCE.init = true;
        INSTANCE.updateManager = new UpdateTaskManager();
        INSTANCE.aniManager = new AnimationManager();
        INSTANCE.chatHandler = new ChatHandler();
        INSTANCE.clock = new Clock();
        INSTANCE.map = new GameMap(engine);
        INSTANCE.lights = new LightTracer(INSTANCE.map);
        INSTANCE.mapDisplay = new MapDisplayManager(engine);
        INSTANCE.musicBox = new MusicBox(engine);
        INSTANCE.net = new NetComm();
        INSTANCE.people = new People();
        INSTANCE.player = new Player(engine, characterName);
        INSTANCE.weather = new Weather();
        INSTANCE.interactionManager = new InteractionManager();
    }

    /**
     * Init the GUI of the game.
     *
     * @param engine the game engine
     */
    public static void initGui(@Nonnull Engine engine) {
        INSTANCE.gameGui = new GameScreenController(engine.getInput());
    }

    @Contract(pure = true)
    public static boolean isInitDone() {
        return INSTANCE.init;
    }
}

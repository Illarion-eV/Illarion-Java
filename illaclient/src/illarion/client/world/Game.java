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
package illarion.client.world;

import illarion.client.ClientWindow;
import illarion.client.IllaClient;
import illarion.client.crash.LightTracerCrashHandler;
import illarion.client.graphics.AnimationManager;
import illarion.client.graphics.Avatar;
import illarion.client.graphics.AvatarClothLoader;
import illarion.client.graphics.AvatarFactory;
import illarion.client.graphics.EffectFactory;
import illarion.client.graphics.ItemFactory;
import illarion.client.graphics.LoadingScreen;
import illarion.client.graphics.MapDisplayManager;
import illarion.client.graphics.MarkerFactory;
import illarion.client.graphics.OverlayFactory;
import illarion.client.graphics.RuneFactory;
import illarion.client.graphics.TileFactory;
import illarion.client.graphics.particle.ParticleSystem;
import illarion.client.net.CommandFactory;
import illarion.client.net.CommandList;
import illarion.client.net.NetComm;
import illarion.client.net.client.LoginCmd;
import illarion.client.net.client.MapDimensionCmd;
import illarion.client.sound.SongFactory;
import illarion.client.sound.SoundFactory;
import illarion.client.util.ChatHandler;
import illarion.client.util.SessionManager;
import illarion.client.util.SessionMember;

import illarion.common.bug.CrashData;
import illarion.common.bug.CrashReporter;
import illarion.common.util.DebugTimer;
import illarion.common.util.NoResourceException;
import illarion.common.util.StoppableStorage;

import illarion.graphics.Graphics;
import illarion.graphics.RenderTask;
import illarion.graphics.common.LightTracer;
import illarion.graphics.common.SpriteBuffer;
import illarion.graphics.common.TextureLoader;

/**
 * Main Game control class for the main loop. Created: 20.08.2005 16:01:50
 */
public final class Game implements SessionMember {

    /**
     * Singleton instance of the game class.
     */
    private static final Game INSTANCE = new Game();

    /**
     * Pointer to the light tracer the game uses.
     */
    private LightTracer lights;

    /**
     * This values has to be true in case the game shall load up. If its set to
     * false the init will cancel right away.
     */
    private boolean loadData = true;

    /**
     * Flag if the loading of the game resources is done or not.
     */
    private boolean loaded;

    /**
     * Login Name that was used to establish a connection.
     */
    private String login;

    /**
     * Pointer to the current game map.
     */
    private GameMap map;

    /**
     * Pointer to the used map manager.
     */
    private MapDisplayManager mapDisplay;

    /**
     * The music box that is used to handle the sound playback.
     */
    private MusicBox musicBox;

    /**
     * Pointer to the used network communication interface.
     */
    private NetComm net;

    /**
     * Pointer to the used particle system.
     */
    private ParticleSystem partSystem;

    /**
     * Plain text password that is used to establish a connection.
     */
    private String password;

    /**
     * Pointer to the handler of the currently known characters.
     */
    private People people;

    /**
     * Pointer the the current player and all its data.
     */
    private Player player;

    /**
     * Running indicator for the game loop. True the the game is currently
     * running, false if not.
     */
    private boolean running;

    /**
     * Pointer to the current weather handler.
     */
    private Weather weather;

    /**
     * The client window that is used as the render target. This variable is
     * only used to avoid the constant usage of
     * {@link illarion.client.ClientWindow#getInstance()}.
     */
    private ClientWindow windowHandler;

    /**
     * Default constructor.
     */
    private Game() {
        // singleton constructor does not need to do anything since its not
        // clear when its executed
    }

    /**
     * Get the Avatar of the current player character.
     * 
     * @return the avatar of the player char
     */
    public static Avatar getAvatar() {
        return Game.getInstance().player.getCharacter().getAvatar();
    }

    /**
     * Get the reference to the map display manager the client uses.
     * 
     * @return the map display manager that is used by the game
     */
    public static MapDisplayManager getDisplay() {
        return Game.getInstance().mapDisplay;
    }

    /**
     * Get the singleton instance of the game.
     * 
     * @return a instance of the game
     */
    public static Game getInstance() {
        return INSTANCE;
    }

    /**
     * Get the light tracer that is used by the game.
     * 
     * @return the light tracer the game uses currently
     */
    public static LightTracer getLights() {
        return Game.getInstance().lights;
    }

    /**
     * Get the reference to the map the client currently shows.
     * 
     * @return the map the client knows
     */
    public static GameMap getMap() {
        return Game.getInstance().map;
    }

    /**
     * Get the music box that is currently the active one in this game.
     * 
     * @return the music box
     */
    public static MusicBox getMusicBox() {
        return Game.getInstance().musicBox;
    }

    /**
     * Get the Network interface of the game.
     * 
     * @return the network interface
     */
    public static NetComm getNet() {
        return Game.getInstance().net;
    }

    /**
     * Get the particle system of the game.
     * 
     * @return the instance of the particle system that is used by the main game
     *         loop
     */
    public static ParticleSystem getParticleSystem() {
        return Game.getInstance().partSystem;
    }

    /**
     * Get the reference to the peoples the client knows.
     * 
     * @return the people manager the game uses
     */
    public static People getPeople() {
        return Game.getInstance().people;
    }

    /**
     * Get the reference to the player.
     * 
     * @return the player that plays the game
     */
    public static Player getPlayer() {
        return Game.getInstance().player;
    }

    /**
     * Get the reference to the weather handler of the game.
     * 
     * @return the weather handler the game uses
     */
    public static Weather getWeather() {
        return Game.getInstance().weather;
    }

    /**
     * After this function is called the init of the game is canceled as soon as
     * possible.
     */
    public void cancelInit() {
        loadData = false;
    }

    /**
     * Establish a connection to the server and send the login informations.
     */
    @SuppressWarnings("nls")
    public void connect() {
        // connect to server
        running = true;

        if (net.connect()) {
            // login
            final LoginCmd cmd =
                (LoginCmd) CommandFactory.getInstance().getCommand(
                    CommandList.CMD_LOGIN);
            cmd.setVersion(IllaClient.getInstance().getUsedServer()
                .getClientVersion());
            cmd.setLogin(login, password);
            net.sendCommand(cmd);

            final MapDimensionCmd cmd2 =
                (MapDimensionCmd) CommandFactory.getInstance().getCommand(
                    CommandList.CMD_MAPDIMENSION);
            cmd2.setMapDimensions(map.getWidthStripes(),
                map.getHeightStripes());
            net.sendCommand(cmd2);
        } else {
            IllaClient.fallbackToLogin("Cannot connect to server!");
            running = false;
            return;
        }
    }

    @Override
    public void endSession() {
        if (net != null) {
            net.disconnect();
            net = null;
        }

        if (player != null) {
            player.shutdown();
            player = null;
        }

        if (lights != null) {
            lights.saveShutdown();
            lights = null;
        }

        if (musicBox != null) {
            musicBox.shutdownMusicBox();
            musicBox = null;
        }

        partSystem = null;
        mapDisplay = null;
        ChatHandler.getInstance().saveShutdown();
    }

    /**
     * The main loop of the game. Contains all actions performed in cycle.
     */
    public void gameLoop() {

        // Particle System Update
        Graphics.getInstance().getRenderManager().addTask(new RenderTask() {
            @Override
            public boolean render(final int delta) {
                if (Game.getInstance().isRunning()) {
                    getParticle().update(delta);
                    return true;
                }
                return false;
            }
        });

        // GUI Update
        Graphics.getInstance().getRenderManager().addTask(new RenderTask() {
            @Override
            public boolean render(final int delta) {
                if (Game.getInstance().isRunning()) {
                    illarion.client.guiNG.GUI.getInstance().draw(delta);
                    return true;
                }
                return false;
            }
        });

        // Animation Update
        Graphics.getInstance().getRenderManager().addTask(new RenderTask() {
            @Override
            public boolean render(final int delta) {
                if (Game.getInstance().isRunning()) {
                    AnimationManager.getInstance().animate(delta);
                    return true;
                }
                return false;
            }
        });

        while (running) {
            try {
                Thread.sleep(1000);
            } catch (final InterruptedException ex) {
                // no message needed
            }

            // update the fps display
            windowHandler.updateFPS();
        }
    }

    /**
     * Get the login name (the character name) that is used to establish a
     * connection.
     * 
     * @return the character name that is used for the login
     */
    public String getLogin() {
        return login;
    }

    /**
     * Initialize the entire game. This method adds all required members to the
     * session manager in order to maintain the sessions consistent.
     */
    @Override
    public void initSession() {
        DebugTimer.start();

        windowHandler = ClientWindow.getInstance();

        // preload textures
        LoadingScreen.getInstance().setCurrentlyLoading(
            LoadingScreen.LOADING_GRAPHICS);

        Graphics.getInstance().getRenderManager().addTask(new RenderTask() {
            @Override
            public boolean render(final int delta) {
                boolean result = false;
                try {
                    result = loadData();
                } catch (final NoResourceException e) {
                    CrashReporter.getInstance().reportCrash(
                        new CrashData(IllaClient.APPLICATION, IllaClient.VERSION,
                            "crash.loadres", Thread.currentThread(), e)); //$NON-NLS-1$
                    IllaClient.errorExit("crash.loadres"); //$NON-NLS-1$
                }

                ClientWindow.getInstance().getRenderDisplay().getRenderArea()
                    .repaint();
                return result;
            }

            @SuppressWarnings("synthetic-access")
            private boolean loadData() {
                if (!loadData) {
                    return false;
                }

                if (!TextureLoader.getInstance().preloadAtlasTextures()) {
                    return true;
                }

                if (!loadData) {
                    return false;
                }

                weather = new Weather();
                SessionManager.getInstance().addMember(weather);

                GameFactory.getInstance().init();
                map = new GameMap();
                SessionManager.getInstance().addMember(map);

                LoadingScreen.getInstance().setLoadingDone(
                    LoadingScreen.LOADING_GRAPHICS);

                return false;
            }
        });

        if (!loadData) {
            return;
        }

        LoadingScreen.getInstance().setCurrentlyLoading(
            LoadingScreen.LOADING_ENVIRONMENT);

        SessionManager.getInstance().addMember(AnimationManager.getInstance());

        LoadingScreen.getInstance().setLoadingDone(
            LoadingScreen.LOADING_ENVIRONMENT);

        do {
            try {
                Thread.sleep(50);
            } catch (final InterruptedException ex) {
                // no message needed
            }
        } while (!LoadingScreen.getInstance().isLoadingDone(
            LoadingScreen.LOADING_GRAPHICS)
            && loadData);

        if (!loadData) {
            return;
        }

        LoadingScreen.getInstance().setCurrentlyLoading(
            LoadingScreen.LOADING_TILES);
        TileFactory.getInstance().init();
        LoadingScreen.getInstance()
            .setLoadingDone(LoadingScreen.LOADING_TILES);

        if (!loadData) {
            return;
        }

        LoadingScreen.getInstance().setCurrentlyLoading(
            LoadingScreen.LOADING_OVERLAYS);
        OverlayFactory.getInstance().init();
        LoadingScreen.getInstance().setLoadingDone(
            LoadingScreen.LOADING_OVERLAYS);
        if (!loadData) {
            return;
        }

        LoadingScreen.getInstance().setCurrentlyLoading(
            LoadingScreen.LOADING_ITEMS);
        ItemFactory.getInstance().init();
        LoadingScreen.getInstance()
            .setLoadingDone(LoadingScreen.LOADING_ITEMS);
        if (!loadData) {
            return;
        }

        LoadingScreen.getInstance().setCurrentlyLoading(
            LoadingScreen.LOADING_AVATARS);
        AvatarFactory.getInstance().init();
        LoadingScreen.getInstance().setLoadingDone(
            LoadingScreen.LOADING_AVATARS);
        if (!loadData) {
            return;
        }

        LoadingScreen.getInstance().setCurrentlyLoading(
            LoadingScreen.LOADING_CLOTHES);
        new AvatarClothLoader().init();
        LoadingScreen.getInstance().setLoadingDone(
            LoadingScreen.LOADING_CLOTHES);
        if (!loadData) {
            return;
        }

        LoadingScreen.getInstance().setCurrentlyLoading(
            LoadingScreen.LOADING_EFFECTS);
        EffectFactory.getInstance().init();
        LoadingScreen.getInstance().setLoadingDone(
            LoadingScreen.LOADING_EFFECTS);
        if (!loadData) {
            return;
        }

        LoadingScreen.getInstance().setCurrentlyLoading(
            LoadingScreen.LOADING_MENUS);
        MarkerFactory.getInstance();
        LoadingScreen.getInstance()
            .setLoadingDone(LoadingScreen.LOADING_MENUS);
        if (!loadData) {
            return;
        }

        LoadingScreen.getInstance().setCurrentlyLoading(
            LoadingScreen.LOADING_RUNES);
        RuneFactory.getInstance().init();
        LoadingScreen.getInstance()
            .setLoadingDone(LoadingScreen.LOADING_RUNES);
        if (!loadData) {
            return;
        }

        LoadingScreen.getInstance().setCurrentlyLoading(
            LoadingScreen.LOADING_SOUNDS);
        SongFactory.getInstance().init();
        SoundFactory.getInstance().init();

        if (!loadData) {
            return;
        }
        LoadingScreen.getInstance().setLoadingDone(
            LoadingScreen.LOADING_SOUNDS);

        LoadingScreen.getInstance().setCurrentlyLoading(
            LoadingScreen.LOADING_FINAL);
        if (!loadData) {
            return;
        }
        people = new People();
        SessionManager.getInstance().addMember(people);
        SessionManager.getInstance().addMember(
            illarion.client.guiNG.GUI.getInstance());
        SessionManager.getInstance().addMember(ChatHandler.getInstance());
        LoadingScreen.getInstance()
            .setLoadingDone(LoadingScreen.LOADING_FINAL);

        do {
            try {
                Thread.sleep(50);
            } catch (final InterruptedException ex) {
                // no message needed.
            }
        } while (!LoadingScreen.getInstance().isLoadingDone(
            LoadingScreen.LOADING_RUNES));

        LoadingScreen.getInstance().setCurrentlyLoading(
            LoadingScreen.LOADING_OPTIMIZE);
        // cleanup the system
        SpriteBuffer.getInstance().cleanup();
        TextureLoader.getInstance().cleanup();
        System.gc();
        LoadingScreen.getInstance().setLoadingDone(
            LoadingScreen.LOADING_OPTIMIZE);

        LoadingScreen.getInstance().setCurrentlyLoading(
            LoadingScreen.READY_TO_GO);

        ClientWindow.getInstance().getRenderDisplay().stopRendering();
        ClientWindow.getInstance().getRenderDisplay().getRenderArea()
            .repaint();
        DebugTimer.mark("Loading the client done in"); //$NON-NLS-1$
    }

    /**
     * Check if the client is fully loaded and ready to play.
     * 
     * @return true if the client is loaded up fully, false if not
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Check if the game handler is currently running.
     * 
     * @return true in case the game is running correctly
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Restart the light tracer.
     */
    public void restartLight() {
        StoppableStorage.getInstance().remove(lights);
        lights = new LightTracer(map);
        StoppableStorage.getInstance().add(lights);
        lights.setUncaughtExceptionHandler(LightTracerCrashHandler
            .getInstance());
        lights.start();
    }

    /**
     * Set the current loaded status.
     * 
     * @param newLoaded the new value for the loaded status
     */
    public void setLoaded(final boolean newLoaded) {
        loaded = newLoaded;
    }

    /**
     * Set the login informations that are used to establish a connection the
     * the Illarion server.
     * 
     * @param name The character name that is used for the login
     * @param pw The plain text password that is used for the login
     */
    public void setLogin(final String name, final String pw) {
        login = name;
        password = pw;
    }

    /**
     * Set if the mainloop shall be running or quit after the next run.
     * 
     * @param newRunning set the new running state, if this is set to false the
     *            main loop will exit at the next run
     */
    public void setRunning(final boolean newRunning) {
        running = newRunning;
    }

    @Override
    public void shutdownSession() {
        // nothing to do at the shutdown
    }

    @Override
    public void startSession() {
        lights = new LightTracer(map);
        lights.setUncaughtExceptionHandler(LightTracerCrashHandler
            .getInstance());
        lights.start();

        partSystem = new ParticleSystem();
        mapDisplay = new MapDisplayManager();

        musicBox = new MusicBox();

        net = new NetComm();
        player = new Player(login);
        connect();

        if (!running) {
            SessionManager.getInstance().cancelStart();
            return;
        }

    }

    protected ParticleSystem getParticle() {
        return partSystem;
    }
}

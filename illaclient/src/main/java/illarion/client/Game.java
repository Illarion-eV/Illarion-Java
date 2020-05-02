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
package illarion.client;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.spi.time.impl.AccurateTimeProvider;
import illarion.client.graphics.FontLoader;
import illarion.client.input.InputReceiver;
import illarion.client.states.*;
import illarion.client.util.ConnectionPerformanceClock;
import illarion.client.util.Lang;
import illarion.client.world.World;
import illarion.common.config.ConfigChangedEvent;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventTopicSubscriber;
import org.illarion.engine.GameContainer;
import org.illarion.engine.GameListener;
import org.illarion.engine.assets.TextureManager;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.Font;
import org.illarion.engine.input.ForwardingListener;
import org.illarion.engine.input.ForwardingTarget;
import org.illarion.engine.nifty.IgeInputSystem;
import org.illarion.engine.nifty.IgeRenderDevice;
import org.illarion.engine.nifty.IgeSoundDevice;
import org.illarion.engine.sound.Sounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Properties;

/**
 * This is the game Illarion. This class takes care for actually building up Illarion. It will maintain the different
 * states of the game and allow switching them.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class Game implements GameListener {
    /**
     * The ID of no state. In case this "state" is chosen the game will not display anything.
     */
    public static final int STATE_NONE = -1;
    /**
     * The ID of the login state. This is one of the constants to use in order to switch the current state of the game.
     */
    public static final int STATE_LOGIN = 0;

    /**
     * The ID of the loading state. This state can be used in order to display the current loading progress.
     */
    public static final int STATE_LOADING = 1;

    /**
     * The ID of the playing state. This can be used in order to display the current game.
     */
    public static final int STATE_PLAYING = 2;

    /**
     * The ID of the ending state. This displays the last screen before the shutdown.
     */
    public static final int STATE_ENDING = 3;

    /**
     * The ID of the ending state. This displays the last screen before the shutdown.
     */
    public static final int STATE_LOGOUT = 4;

    /**
     * The ID of the disconnected state. This state is displayed in case the client lost the connection to the server.
     * This may happen due to the server shutting down the connection or by the connection being interrupted.
     */
    public static final int STATE_DISCONNECT = 5;

    @Nullable
    private Nifty nifty;

    @Nonnull
    private final GameState[] gameStates;
    private int activeListener = STATE_NONE;
    private int targetListener = STATE_NONE;
    private boolean showFPS;
    private boolean showPing;

    /**
     * Create the game with the fitting title, showing the name of the application and its version.
     */
    public Game() {
        gameStates = new GameState[6];
        AnnotationProcessor.process(this);
        showFPS = IllaClient.getCfg().getBoolean("showFps");
        showPing = IllaClient.getCfg().getBoolean("showPing");
    }

    /**
     * Sets the next game state to the given state, if between -1 and 5
     * Game will advance to the given state upon the next call of update()
     * @param stateId   the state to enter. Using a class constant is recommended for readability.
     */
    public void enterState(int stateId) {
        if ((stateId >= -1) && (stateId < gameStates.length)) {
            targetListener = stateId;
        } else {
            throw new IllegalArgumentException("Illegal stateId: " + stateId);
        }
    }

    @Nullable
    private GameState getCurrentState() {
        if ((activeListener >= 0) && (activeListener < gameStates.length)) {
            return gameStates[activeListener];
        }
        return null;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);

    /**
     * Initializes fields and prepares the Game for launch
     * Enters the login state when finished
     * @param container the game container
     */
    @Override
    public void create(@Nonnull GameContainer container) {
        TextureManager texManager = container.getEngine().getAssets().getTextureManager();
        texManager.addTextureDirectory("gui");
        texManager.addTextureDirectory("chars");
        texManager.addTextureDirectory("items");
        texManager.addTextureDirectory("tiles");
        texManager.addTextureDirectory("effects");

        try {
            FontLoader.getInstance().prepareAllFonts(container.getEngine().getAssets());
        } catch (@Nonnull IOException e) {
            LOGGER.error("Error while loading fonts!", e);
        }

        InputReceiver inputReceiver = new InputReceiver(container.getEngine().getInput());
        // Prepare the game's Nifty and its properties
        nifty = new Nifty(new IgeRenderDevice(container, "gui/"), new IgeSoundDevice(container.getEngine()),
                          new IgeInputSystem(container.getEngine().getInput(), inputReceiver),
                          new AccurateTimeProvider());

        Properties niftyProperties = nifty.getGlobalProperties();
        if (niftyProperties == null) {
            niftyProperties = new Properties();
            nifty.setGlobalProperties(niftyProperties);
        }
        niftyProperties.setProperty("MULTI_CLICK_TIME",
                                    Integer.toString(IllaClient.getCfg().getInteger("doubleClickInterval")));
        nifty.setLocale(Lang.getInstance().getLocale());
        container.getEngine().getInput().addForwardingListener(new ForwardingListener() {
            @Override
            public void forwardingEnabledFor(@Nonnull ForwardingTarget target) {
                // nothing
            }

            @Override
            public void forwardingDisabledFor(@Nonnull ForwardingTarget target) {
                if ((target == ForwardingTarget.Mouse) || (target == ForwardingTarget.All)) {
                    nifty.resetMouseInputEvents();
                }
            }
        });

        gameStates[STATE_LOGIN] = new LoginState();
        gameStates[STATE_LOADING] = new LoadingState();
        gameStates[STATE_PLAYING] = new PlayingState(inputReceiver);
        gameStates[STATE_ENDING] = new EndState();
        gameStates[STATE_LOGOUT] = new LogoutState();
        gameStates[STATE_DISCONNECT] = new DisconnectedState();
        // Prepare the sounds and music for use, set volume based on the current configuration settings
        Sounds sounds = container.getEngine().getSounds();
        if (IllaClient.getCfg().getBoolean("musicOn")) {
            sounds.setMusicVolume(IllaClient.getCfg().getFloat("musicVolume") / 100.f);
        } else {
            sounds.setMusicVolume(0.f);
        }
        if (IllaClient.getCfg().getBoolean("soundOn")) {
            sounds.setSoundVolume(IllaClient.getCfg().getFloat("soundVolume") / 100.f);
        } else {
            sounds.setSoundVolume(0.f);
        }

        for (@Nonnull GameState listener : gameStates) {
            listener.create(this, container, nifty);
        }

        enterState(STATE_LOGIN);
    }

    /**
     * Returns the state associated with the given index
     * @param index the ID of the state to use, between 0 and {@code gameStates.length - 1}
     * @return  the state associated with the given index, if a valid index
     */
    @Nonnull
    public GameState getState(int index) {
        if ((index < 0) || (index >= gameStates.length)) {
            throw new IndexOutOfBoundsException(String.format("Index is expected between 0 and %d, got: %d",
                    gameStates.length - 1, index));
        }
        return gameStates[index];
    }

    @Nonnull
    public <T extends GameState> T getState(@Nonnull Class<T> clazz, int index) {
        GameState state = getState(index);
        if (clazz.isAssignableFrom(state.getClass())) {
            //noinspection unchecked
            return (T) state;
        }
        throw new IllegalArgumentException(
                String.format("Requested state contains the class %s but the expected class was: %s",
                        state.getClass().getName(), clazz.getName()));
    }

    @Nonnull
    public <T extends GameState> T getState(@Nonnull Class<T> clazz) {
        for (int i = 0; i < gameStates.length; i++) {
            GameState state = getState(i);
            if (clazz.isAssignableFrom(state.getClass())) {
                //noinspection unchecked
                return (T) state;
            }
        }
        throw new IllegalArgumentException(
                String.format("Failed to locate any state with the requested class: %s",
                        clazz.getName()));
    }

    /**
     * Sets this instance's nifty to null, disposes of each GameState
     */
    @Override
    public void dispose() {
        nifty = null;

        for (@Nonnull GameState listener : gameStates) {
            listener.dispose();
        }
    }

    /**
     * Changes the container's dimensions
     * Sets the Client's config's window height and width to the new dimensions
     *
     * @param container the game container
     * @param width the new width
     * @param height the new height
     */
    @Override
    public void resize(@Nonnull GameContainer container, int width, int height) {
        IllaClient.getCfg().set("windowHeight", height);
        IllaClient.getCfg().set("windowWidth", width);

        if (nifty != null) {
            nifty.resolutionChanged();
        }

        GameState activeListener = getCurrentState();
        if (activeListener != null) {
            activeListener.resize(container, width, height);
        }
    }

    /**
     * During the call of this function the application is supposed to perform the update of the game logic.
     *
     * If the Game is not in the state given by the last call of enterState(), enters that state
     * Updates the Nifty gui for the (now current) state
     *
     * @param container the game container
     * @param delta the time since the last update call
     */
    @Override
    public void update(@Nonnull GameContainer container, int delta) {
        assert nifty != null;
        if (targetListener != activeListener) {
            GameState activeState = getCurrentState();
            if (activeState != null) {
                activeState.leaveState(container);
            }
            activeListener = targetListener;
            GameState newState = getCurrentState();
            if (newState != null) {
                newState.enterState(container, nifty);
            }
        }

        nifty.update();
        container.getEngine().getSounds().poll(delta);

        GameState activeListener = getCurrentState();
        if (activeListener != null) {
            activeListener.update(container, delta);
        }
    }

    /**
     * Perform all rendering operations
     * If more diagnostic data should be shown, add to this method
     * @param container the game container
     */
    @Override
    public void render(@Nonnull GameContainer container) {
        assert nifty != null;

        GameState activeListener = getCurrentState();
        if (activeListener != null) {
            activeListener.render(container);
        }
        nifty.render(false);

        if (showFPS || showPing) {
            Font fpsFont = container.getEngine().getAssets().getFontManager().getFont(FontLoader.CONSOLE_FONT);
            if (fpsFont != null) {
                // Only show render data unless on devserver, testerver, or a custom server
                boolean showRenderDiagnostic = IllaClient.DEFAULT_SERVER != Servers.Illarionserver;
                int renderLine = 10;
                if (showFPS) {
                    container.getEngine().getGraphics()
                            .drawText(fpsFont, "FPS: " + container.getFPS(), Color.WHITE, 10, renderLine);
                    renderLine += fpsFont.getLineHeight();

                    if (showRenderDiagnostic) {
                        for (CharSequence line : container.getDiagnosticLines()) {
                            container.getEngine().getGraphics().drawText(fpsFont, line, Color.WHITE, 10, renderLine);
                            renderLine += fpsFont.getLineHeight();
                        }
                    }
                }

                if (showRenderDiagnostic && World.isInitDone()) {
                    String tileLine = "Tile count: " + World.getMap().getTileCount();
                    container.getEngine().getGraphics().drawText(fpsFont, tileLine, Color.WHITE, 10, renderLine);
                    renderLine += fpsFont.getLineHeight();

                    String sceneLine = "Scene objects: " + World.getMapDisplay().getGameScene().getElementCount();
                    container.getEngine().getGraphics().drawText(fpsFont, sceneLine, Color.WHITE, 10, renderLine);
                    renderLine += fpsFont.getLineHeight();
                }

                if (showPing) {
                    long serverPing = ConnectionPerformanceClock.getServerPing();
                    long netCommPing = ConnectionPerformanceClock.getNetCommPing();
                    if (serverPing > -1) {
                        container.getEngine().getGraphics().drawText(fpsFont, "Ping: " + serverPing + '+' +
                                Math.max(0, netCommPing - serverPing) + " ms", Color.WHITE, 10, renderLine);
                        renderLine += fpsFont.getLineHeight();
                    }
                }
                // If more diagnostics are wanted, add them here
            }
        }
    }



    @EventTopicSubscriber(topic = "showFps")
    public void onFpsConfigChanged(@Nonnull String topic, @Nonnull ConfigChangedEvent event) {
        showFPS = event.getConfig().getBoolean(event.getKey());
    }

    @EventTopicSubscriber(topic = "showPing")
    public void onPingConfigChanged(@Nonnull String topic, @Nonnull ConfigChangedEvent event) {
        showPing = event.getConfig().getBoolean(event.getKey());
    }

    /**
     * This function is called in case the game receives a request to be closed.
     *
     * @return {@code true} in case the game is supposed to shutdown, else the closing request is rejected
     */
    @Override
    public boolean isClosingGame() {
        GameState activeListener = getCurrentState();
        if (activeListener != null) {
            return activeListener.isClosingGame();
        }
        return true; // According to the interface, default reply is false. Consider rewriting docs there or this method.
    }
}

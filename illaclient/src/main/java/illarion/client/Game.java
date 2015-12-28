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

import com.google.common.collect.ImmutableMap;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This is the game Illarion. This class takes care for actually building up Illarion. It will maintain the different
 * states of the game and allow switching them.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class Game implements GameListener {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(Game.class);

    @Nullable
    private Nifty nifty;

    @Nullable
    private Map<Class<? extends GameState>, GameState> gameStates;

    @Nullable
    private GameState currentState;

    @Nullable
    private GameState targetState;

    private boolean showFPS;
    private boolean showPing;

    /**
     * Create the game with the fitting title, showing the name of the application and its version.
     */
    public Game() {
        gameStates = new HashMap<>();
        showFPS = IllaClient.getCfg().getBoolean("showFps");
        showPing = IllaClient.getCfg().getBoolean("showPing");

        AnnotationProcessor.process(this);
    }

    /**
     * Change the state of the game. This will not take effect right away, but once update is called the next time.
     *
     * @param targetStateClass The class of the state that is supposed to be entered
     * @throws IllegalStateException in case the initialization of the states is not done yet
     * @throws IllegalArgumentException in case the class is not registered as valid state
     */
    public void enterState(@Nonnull Class<? extends GameState> targetStateClass) {
        if (gameStates == null) {
            throw new IllegalStateException("The states are not initialized yet.");
        }
        if (!gameStates.containsKey(targetStateClass)) {
            throw new IllegalArgumentException("Illegal state: " + targetStateClass.getName());
        }

        targetState = gameStates.get(targetStateClass);
    }

    @Nullable
    GameState getCurrentState() {
        return currentState;
    }

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
            log.error("Error while loading fonts!", e);
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

        /* Populate the game states. */
        gameStates = ImmutableMap.<Class<? extends GameState>, GameState>builder()
                .put(AccountSystemState.class, new AccountSystemState())
                .put(LoadingState.class, new LoadingState())
                .put(PlayingState.class, new PlayingState(inputReceiver))
                .put(EndState.class, new EndState())
                .put(LogoutState.class, new LogoutState())
                .build();

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

        /* Loading general style and control files. */
        nifty.loadStyleFile("nifty-illarion-style.xml");
        nifty.loadControlFile("nifty-default-controls.xml");
        nifty.loadControlFile("illarion-gamecontrols.xml");

        for (@Nonnull GameState listener : gameStates.values()) {
            listener.create(this, container, nifty);
        }

        enterState(LoadingState.class);
    }

    @Nonnull
    public <T extends GameState> T getState(@Nonnull Class<T> clazz) {
        if (gameStates == null) {
            throw new IllegalStateException("The game states are not initialized yet.");
        }

        if (gameStates.containsKey(clazz)) {
            //noinspection unchecked
            return (T) gameStates.get(clazz);
        }
        throw new IllegalArgumentException(
                String.format("Failed to locate any state with the requested class: %s", clazz.getName()));
    }

    /**
     * Sets this instance's nifty to null, disposes of each GameState
     */
    @Override
    public void dispose() {
        nifty = null;

        if (gameStates != null) {
            gameStates.values().forEach(GameState::dispose);
            gameStates = null;
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
        if (targetState != currentState) {
            GameState activeState = currentState;
            if (activeState != null) {
                activeState.leaveState(container);
            }
            currentState = targetState;
            GameState newState = targetState;
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
                int renderLine = 10;
                if (showFPS) {
                    container.getEngine().getGraphics()
                            .drawText(fpsFont, "FPS: " + container.getFPS(), Color.WHITE, 10, renderLine);
                    renderLine += fpsFont.getLineHeight();

                    if (IllaClient.IS_DEVELOP) {
                        for (CharSequence line : container.getDiagnosticLines()) {
                            container.getEngine().getGraphics().drawText(fpsFont, line, Color.WHITE, 10, renderLine);
                            renderLine += fpsFont.getLineHeight();
                        }
                    }
                }

                if (IllaClient.IS_DEVELOP && World.isInitDone()) {
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
        return activeListener == null || activeListener.isClosingGame();
    }
}

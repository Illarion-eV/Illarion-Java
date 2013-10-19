/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with the Illarion Client. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package illarion.client;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.spi.time.impl.AccurateTimeProvider;
import illarion.client.graphics.FontLoader;
import illarion.client.input.InputReceiver;
import illarion.client.states.*;
import illarion.client.util.ConnectionPerformanceClock;
import illarion.client.util.Lang;
import illarion.common.config.ConfigChangedEvent;
import org.apache.log4j.Logger;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

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

    @Nullable
    private Nifty nifty;

    private final GameState[] gameStates;
    private int activeListener = STATE_NONE;
    private int targetListener = STATE_NONE;

    /**
     * Create the game with the fitting title, showing the name of the application and its version.
     */
    public Game() {
        gameStates = new GameState[4];
        AnnotationProcessor.process(this);
        showFPS = IllaClient.getCfg().getBoolean("showFps");
        showPing = IllaClient.getCfg().getBoolean("showPing");
    }

    public void enterState(final int stateId) {
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

    private static final Logger LOGGER = Logger.getLogger(Game.class);

    @Override
    public void create(@Nonnull final GameContainer container) {
        final TextureManager texManager = container.getEngine().getAssets().getTextureManager();
        texManager.addTextureDirectory("gui");
        texManager.addTextureDirectory("chars");
        texManager.addTextureDirectory("items");
        texManager.addTextureDirectory("tiles");
        texManager.addTextureDirectory("effects");

        try {
            FontLoader.getInstance().prepareAllFonts(container.getEngine().getAssets());
        } catch (@Nonnull final IOException e) {
            LOGGER.error("Error while loading fonts!", e);
        }

        final InputReceiver inputReceiver = new InputReceiver(container.getEngine().getInput());
        nifty = new Nifty(new IgeRenderDevice(container, "gui/"), new IgeSoundDevice(container.getEngine()),
                new IgeInputSystem(container.getEngine().getInput(), inputReceiver), new AccurateTimeProvider());
        nifty.setLocale(Lang.getInstance().getLocale());
        container.getEngine().getInput().addForwardingListener(new ForwardingListener() {
            @Override
            public void forwardingEnabledFor(@Nonnull final ForwardingTarget target) {
                // nothing
            }

            @Override
            public void forwardingDisabledFor(@Nonnull final ForwardingTarget target) {
                if ((target == ForwardingTarget.Mouse) || (target == ForwardingTarget.All)) {
                    nifty.resetMouseInputEvents();
                }
            }
        });

        gameStates[STATE_LOGIN] = new LoginState();
        gameStates[STATE_LOADING] = new LoadingState();
        gameStates[STATE_PLAYING] = new PlayingState(inputReceiver);
        gameStates[STATE_ENDING] = new EndState();

        final Sounds sounds = container.getEngine().getSounds();
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

        for (@Nonnull final GameState listener : gameStates) {
            listener.create(this, container, nifty);
        }

        enterState(STATE_LOGIN);
    }

    @Override
    public void dispose() {
        nifty = null;

        for (@Nonnull final GameState listener : gameStates) {
            listener.dispose();
        }
    }

    @Override
    public void resize(@Nonnull final GameContainer container, final int width, final int height) {
        IllaClient.getCfg().set("windowHeight", height);
        IllaClient.getCfg().set("windowWidth", width);

        if (nifty != null) {
            nifty.resolutionChanged();
        }

        final GameState activeListener = getCurrentState();
        if (activeListener != null) {
            activeListener.resize(container, width, height);
        }
    }

    @Override
    public void update(@Nonnull final GameContainer container, final int delta) {
        assert nifty != null;
        if (targetListener != activeListener) {
            final GameState activeState = getCurrentState();
            if (activeState != null) {
                activeState.leaveState(container);
            }
            activeListener = targetListener;
            final GameState newState = getCurrentState();
            if (newState != null) {
                newState.enterState(container, nifty);
            }
        }

        nifty.update();
        container.getEngine().getSounds().poll(delta);

        final GameState activeListener = getCurrentState();
        if (activeListener != null) {
            activeListener.update(container, delta);
        }
    }

    @Override
    public void render(@Nonnull final GameContainer container) {
        assert nifty != null;

        final GameState activeListener = getCurrentState();
        if (activeListener != null) {
            activeListener.render(container);
        }
        nifty.render(false);

        if (showFPS || showPing) {
            final Font fpsFont = container.getEngine().getAssets().getFontManager().getFont(FontLoader.CONSOLE_FONT);
            int renderLine = 10;
            if (fpsFont != null) {
                if (showFPS) {
                    container.getEngine().getGraphics().drawText(fpsFont, "FPS: " + container.getFPS(), Color.WHITE, 10,
                            renderLine);
                    renderLine += fpsFont.getLineHeight();

                    if (SHOW_RENDER_DIAGNOSTIC) {
                        for (final CharSequence line : container.getDiagnosticLines()) {
                            container.getEngine().getGraphics().drawText(fpsFont, line, Color.WHITE, 10, renderLine);
                            renderLine += fpsFont.getLineHeight();
                        }
                    }
                }

                if (showPing) {
                    final long serverPing = ConnectionPerformanceClock.getServerPing();
                    final long netCommPing = ConnectionPerformanceClock.getNetCommPing();
                    if (serverPing > -1) {
                        container.getEngine().getGraphics().drawText(fpsFont,
                                "Ping: " + serverPing + '+' + Math.max(0, netCommPing - serverPing) + " ms",
                                Color.WHITE, 10, renderLine);
                        renderLine += fpsFont.getLineHeight();
                    }
                }
            }
        }
    }

    private static final boolean SHOW_RENDER_DIAGNOSTIC = IllaClient.DEFAULT_SERVER != Servers.realserver;

    private boolean showFPS;
    private boolean showPing;

    @EventTopicSubscriber(topic = "showFps")
    public void onFpsConfigChanged(@Nonnull final String topic, @Nonnull final ConfigChangedEvent event) {
        showFPS = event.getConfig().getBoolean(event.getKey());
    }

    @EventTopicSubscriber(topic = "showPing")
    public void onPingConfigChanged(@Nonnull final String topic, @Nonnull final ConfigChangedEvent event) {
        showPing = event.getConfig().getBoolean(event.getKey());
    }

    @Override
    public boolean isClosingGame() {
        final GameState activeListener = getCurrentState();
        if (activeListener != null) {
            return activeListener.isClosingGame();
        }
        return true;
    }
}

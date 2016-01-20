/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
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
package illarion.client.gui.controller;

import com.google.common.io.Resources;
import com.google.common.util.concurrent.*;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.Game;
import illarion.client.IllaClient;
import illarion.client.net.NetComm;
import illarion.client.net.client.LoginCmd;
import illarion.client.states.PlayingState;
import illarion.client.util.account.AccountSystemEndpoint;
import illarion.client.world.MapDimensions;
import illarion.client.world.World;
import org.illarion.engine.EngineException;
import org.illarion.engine.GameContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executors;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class EnteringScreenController implements ScreenController {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(EnteringScreenController.class);
    @Nonnull
    private final GameContainer container;
    @Nonnull
    private final Game game;

    @Nullable
    private String serverId;
    @Nullable
    private String characterName;
    @Nullable
    private String password;
    @Nullable
    private AccountSystemEndpoint endpoint;

    public EnteringScreenController(@Nonnull Game game, @Nonnull GameContainer container) {
        this.game = game;
        this.container = container;
    }

    @Nonnull
    private static Properties getServerConfig() throws IOException {
        URL serverConfig = Resources.getResource(EnteringScreenController.class, "/server-config.properties");
        Properties result = new Properties();
        try (InputStream stream = serverConfig.openStream()) {
            result.load(stream);
        }
        return result;
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
    }

    @Override
    public void onStartScreen() {
        String serverId = Objects.requireNonNull(this.serverId);
        String characterName = Objects.requireNonNull(this.characterName);
        String password = Objects.requireNonNull(this.password);
        AccountSystemEndpoint endpoint = Objects.requireNonNull(this.endpoint);
        this.serverId = null;
        this.characterName = null;
        this.password = null;
        this.endpoint = null;

        try {
            World.initWorldComponents(container.getEngine(), characterName);
        } catch (@Nonnull EngineException | RuntimeException e) {
            IllaClient.errorExit("Initialization of the world components failed.");
            return;
        }

        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

        ListenableFuture<Void> worldInit = service.submit(() -> {
            String serverHost;
            int serverPort;
            if (endpoint.isUseConfigParameters()) {
                serverHost = IllaClient.getCfg().getString("customServer.domain");
                serverPort = IllaClient.getCfg().getInteger("customServer.port");
            } else {
                try {
                    Properties config = getServerConfig();
                    serverHost = config.getProperty("official.domain");
                    if (serverHost == null) {
                        serverHost = config.getProperty("official." + serverId + ".domain");
                    }
                    serverPort = Integer.parseInt(config.getProperty("official." + serverId + ".port"));
                } catch (IOException e) {
                    throw new CriticalLoadingFailureException("Failed to fetch server configuration file.", e);
                }
            }

            if (serverHost == null) {
                throw new CriticalLoadingFailureException("Failed to detect server data.");
            }

            NetComm net = World.getNet();
            if (net.connect(serverHost, serverPort)) {
                game.enterState(PlayingState.class);
                net.sendCommand(new LoginCmd(characterName, password, NetComm.PROTOCOL_VERSION));
                MapDimensions.getInstance().reportScreenSize(container.getWidth(), container.getHeight(), true);
            } else {
                throw new ServerNotFoundException();
            }

            return null;
        });

        Futures.addCallback(worldInit, new FutureCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void result) {
                service.shutdown();
            }

            @Override
            public void onFailure(@Nonnull Throwable t) {
                if (t instanceof ServerNotFoundException) {
                    log.error("Failed to locate the server.");
                    IllaClient.returnToLogin("Failed to locate the server.");
                } else {
                    IllaClient.errorExit(t.getMessage());
                }
            }
        });
    }

    @Override
    public void onEndScreen() {

    }

    public void setLoginInformation(@Nonnull AccountSystemEndpoint endpoint,
                                    @Nonnull String serverId,
                                    @Nonnull String characterName,
                                    @Nonnull String password) {
        this.endpoint = endpoint;
        this.serverId = serverId;
        this.characterName = characterName;
        this.password = password;
    }

    private static final class ServerNotFoundException extends Exception {
        public ServerNotFoundException() {
            super("Failed to connect to the server.");
        }
    }

    private static final class CriticalLoadingFailureException extends Exception {
        public CriticalLoadingFailureException(@Nonnull String message) {
            super(message);
        }

        public CriticalLoadingFailureException(@Nonnull String message, @Nullable Throwable cause) {
            super(message, cause);
        }
    }
}

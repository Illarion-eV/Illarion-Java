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
package illarion.client.states;

import de.lessvoid.nifty.Nifty;
import illarion.client.Game;
import illarion.client.gui.controller.LoadScreenController;
import illarion.client.loading.Loading;
import illarion.client.util.AudioPlayer;
import illarion.common.data.SkillLoader;
import org.illarion.engine.GameContainer;

import javax.annotation.Nonnull;

/**
 * This game state is active while the game loads. It takes care for showing the loading screen and to trigger the
 * actual loading.
 *
 * This loads all the textures, along with the definition tables and the skill table.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class LoadingState implements GameState {
    /**
     * The screen controller that handles the display of the loading progress.
     */
    private LoadScreenController controller;

    /**
     * The manager of the loading tasks.
     */
    @Nonnull
    private final Loading loadingManager = new Loading();

    @Override
    public void create(@Nonnull Game game, @Nonnull GameContainer container, @Nonnull Nifty nifty) {
        controller = new LoadScreenController(game);
        nifty.registerScreenController(controller);

        Util.loadXML(nifty, "illarion/client/gui/xml/loading.xml");
    }

    @Override
    public void dispose() {
    }

    @Override
    public void resize(@Nonnull GameContainer container, int width, int height) {
    }

    @Override
    public void update(@Nonnull GameContainer container, int delta) {
        SkillLoader.load();
        AudioPlayer.getInstance().initAudioPlayer(container.getEngine().getSounds());
    }

    @Override
    public void render(@Nonnull GameContainer container) {
        loadingManager.load();
        controller.setProgress(loadingManager.getProgress());

        if (loadingManager.isLoadingDone()) {
            controller.loadingDone();
        }
    }

    @Override
    public boolean isClosingGame() {
        return true;
    }

    @Override
    public void enterState(@Nonnull GameContainer container, @Nonnull Nifty nifty) {
        loadingManager.enlistMissingComponents(container.getEngine());
        nifty.gotoScreen("loading");
    }

    @Override
    public void leaveState(@Nonnull GameContainer container) {
    }
}

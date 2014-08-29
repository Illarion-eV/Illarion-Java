/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package illarion.client.graphics;

import illarion.client.world.World;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Graphics;
import org.illarion.engine.graphic.SceneEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ItemStack implements DisplayItem {
    private static final Logger log = LoggerFactory.getLogger(ItemStack.class);
    private boolean shown;

    public ItemStack() {
        shown = true;
    }

    /**
     * Hide the entity from the screen by removing it from the display list.
     */
    @Override
    public void hide() {
        if (shown) {
            World.getMapDisplay().getGameScene().removeElement(this);
            shown = false;
        }
    }

    @Override
    public void show() {
        if (shown) {
            log.error("Added item stack {} twice.", this);
        } else {
            World.getMapDisplay().getGameScene().addElement(this);
            shown = true;
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void render(@Nonnull Graphics graphics) {

    }

    @Override
    public void update(@Nonnull GameContainer container, int delta) {

    }

    @Override
    public boolean isEventProcessed(
            @Nonnull GameContainer container, int delta, @Nonnull SceneEvent event) {
        return false;
    }
}

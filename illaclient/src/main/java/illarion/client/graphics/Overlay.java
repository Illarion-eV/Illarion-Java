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
package illarion.client.graphics;

import illarion.client.resources.OverlayFactory;
import illarion.client.resources.Resource;
import illarion.client.resources.data.OverlayTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.illarion.engine.graphic.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class represents one overlay over a tile on the screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@SuppressWarnings("ClassNamingConvention")
public class Overlay extends AbstractEntity<OverlayTemplate> implements Resource {
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(Overlay.class);

    /**
     * The parent tile of this overlay.
     */
    @Nonnull
    private final Tile parentTile;

    /**
     * The default constructor for this class.
     *
     * @param template the template of the overlay this instance will refer to
     * @param shape    the shape of the overlay, this value will be set as frame
     */
    public Overlay(@Nonnull final OverlayTemplate template, final int shape, @Nonnull final Tile parentTile) {
        super(template);
        setFrame(shape);
        this.parentTile = parentTile;
    }

    /**
     * Create a new instance of a overlay.
     *
     * @param id    the ID of the overlay tile
     * @param shape the shape of the overlay ({@code 1} is the first shape
     * @return the newly created overlay or {@code null} in case the creation of the overlay failed
     */
    @Nullable
    public static Overlay create(final int id, final int shape, @Nonnull final Tile parentTile) {
        try {
            final OverlayTemplate template = OverlayFactory.getInstance().getTemplate(id);
            return new Overlay(template, shape - 1, parentTile);
        } catch (@Nonnull final IndexOutOfBoundsException ex) {
            LOGGER.error("Failed to create overlay with ID " + id, ex);
        } catch (@Nonnull IllegalStateException ex) {
            LOGGER.error("Template was not found for overlay " + id, ex);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Overlay always inherit the shown state of the tile they are signed to
     */
    @Override
    public boolean isShown() {
        return parentTile.isShown();
    }

    @Override
    public void show() {
        LOGGER.warn("SHOW was called for a overlay. That should not happen!");
    }

    @Override
    public void hide() {
    }

    @Override
    public Color getParentLight() {
        return parentTile.getLocalLight();
    }
}

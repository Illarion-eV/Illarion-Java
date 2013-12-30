/*
 * This file is part of the Illarion Game Engine.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Game Engine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Game Engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Game Engine.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.engine.backend.slick;

import illarion.common.types.Location;
import illarion.common.types.Rectangle;
import org.apache.log4j.Logger;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This is the slick implementation of the world map.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SlickWorldMap implements WorldMap, WorldMapDataProviderCallback {
    /**
     * The height and width in pixels that is updated at once.
     */
    private static final int MAX_UPDATE_SIZE = 64;

    /**
     * The area in pixels that is allowed to be updated during one render step.
     */
    private static final int MAX_UPDATE_AREA = MAX_UPDATE_SIZE * MAX_UPDATE_SIZE;

    /**
     * The logger of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(SlickWorldMap.class);

    /**
     * The provider that supplies the class with the required data.
     */
    @Nonnull
    private final WorldMapDataProvider provider;

    /**
     * The world map texture that stores the entire world map graphics.
     */
    @Nonnull
    private final SlickTexture worldMapTexture;

    /**
     * Get the Slick2D image the texture is rendered onto.
     */
    private final Image worldMapImage;

    /**
     * This is set true in case clearing the map was requested.
     */
    private boolean clearMap;

    /**
     * The origin location of the world map.
     */
    @Nonnull
    private final Location mapOrigin;

    /**
     * The last location of the player that was reported.
     */
    @Nonnull
    private final Location playerLocation;

    /**
     * The off screen graphics instance used to update the texture of the world map.
     */
    @Nullable
    private Graphics offScreenGraphics;

    /**
     * This color instance is used for the drawing operations.
     */
    @Nonnull
    private final Color tempDrawingColor;

    /**
     * The tiles that were marked as dirty and did not receive a update yet.
     */
    @Nonnull
    private final Queue<Rectangle> dirtyTiles;

    /**
     * Create a new instance of the Slick2D implementation of the world map.
     *
     * @param provider the provider that supplies the map data
     * @throws SlickEngineException in case creating the world map fails
     */
    SlickWorldMap(@Nonnull final WorldMapDataProvider provider) throws SlickEngineException {
        this.provider = provider;
        try {
            worldMapImage = Image.createOffscreenImage(WORLD_MAP_WIDTH, WORLD_MAP_HEIGHT);
            worldMapTexture = new SlickTexture(worldMapImage);
        } catch (@Nonnull final SlickException e) {
            throw new SlickEngineException(e);
        }
        mapOrigin = new Location();
        playerLocation = new Location();
        tempDrawingColor = new Color(Color.black);
        dirtyTiles = new LinkedList<>();
    }

    /**
     * Get the origin location of the map.
     *
     * @return the maps origin location
     */
    @Override
    @Nonnull
    public Location getMapOrigin() {
        return mapOrigin;
    }

    @Nonnull
    @Override
    public Texture getWorldMap() {
        return worldMapTexture;
    }

    @Override
    public void setTile(@Nonnull final Location loc, final int tileId, final int overlayId, final boolean blocked) {
        if (offScreenGraphics == null) {
            throw new IllegalStateException("Callback called while no callback was requested");
        }
        if (loc.getScZ() != mapOrigin.getScZ()) {
            return;
        }

        final int texPosX = loc.getScX() - mapOrigin.getScX();
        final int texPosY = loc.getScY() - mapOrigin.getScY();

        if ((texPosX < 0) || (texPosX >= WORLD_MAP_WIDTH) || (texPosY < 0) || (texPosY >= WORLD_MAP_HEIGHT)) {
            return;
        }

        if (tileId != NO_TILE) {
            SlickGraphics.transferColor(MapColor.getColor(tileId), tempDrawingColor);
            if (overlayId != NO_TILE) {
                final org.illarion.engine.graphic.Color mapColor = MapColor.getColor(tileId);
                tempDrawingColor.r += mapColor.getRedf();
                tempDrawingColor.g += mapColor.getGreenf();
                tempDrawingColor.b += mapColor.getBluef();
                tempDrawingColor.scale(0.5f);
            }
            if (blocked) {
                tempDrawingColor.scale(0.7f);
            }
            tempDrawingColor.a = 1.f;

            offScreenGraphics.setColor(tempDrawingColor);
            offScreenGraphics.fillRect(texPosX, texPosY, 1, 1);
        }
    }

    @Override
    public void setTileChanged(@Nonnull final Location location) {
        dirtyTiles.offer(new Rectangle(location.getScX(), location.getScY(), 1, 1));
    }

    @Override
    public void setMapChanged() {
        clear();
        for (int x = 0; x < WORLD_MAP_WIDTH; x += MAX_UPDATE_SIZE) {
            for (int y = 0; y < WORLD_MAP_HEIGHT; y += MAX_UPDATE_SIZE) {
                dirtyTiles.offer(new Rectangle(x + mapOrigin.getScX(), y + mapOrigin.getScY(), MAX_UPDATE_SIZE,
                                               MAX_UPDATE_SIZE));
            }
        }
    }

    @Override
    public void setPlayerLocation(@Nonnull final Location location) {
        playerLocation.set(location);
    }

    @Nonnull
    @Override
    public Location getPlayerLocation() {
        return playerLocation;
    }

    @Override
    public void setMapOrigin(@Nonnull final Location location) {
        mapOrigin.set(location);
        setMapChanged();
    }

    @Override
    public void clear() {
        dirtyTiles.clear();
        clearMap = true;
    }

    @Override
    public void render(@Nonnull final GameContainer container) {
        try {
            if (clearMap) {
                clearMap = false;
                if (offScreenGraphics == null) {
                    offScreenGraphics = worldMapImage.getGraphics();
                }
                offScreenGraphics.setBackground(Color.black);
                offScreenGraphics.clear();
            }

            if (!dirtyTiles.isEmpty()) {
                if (offScreenGraphics == null) {
                    offScreenGraphics = worldMapImage.getGraphics();
                }
                Rectangle dirtyArea = dirtyTiles.poll();
                int updatedArea = 0;
                int updateCount = 0;
                final Location tempLocation = new Location();
                while (dirtyArea != null) {
                    updatedArea += dirtyArea.getArea();
                    updateCount++;
                    for (int x = dirtyArea.getLeft(); x < dirtyArea.getRight(); x++) {
                        for (int y = dirtyArea.getBottom(); y < dirtyArea.getTop(); y++) {
                            tempLocation.setSC(x, y, mapOrigin.getScZ());
                            provider.requestTile(tempLocation, this);
                        }
                    }
                    if (updatedArea >= MAX_UPDATE_AREA) {
                        break;
                    }
                    dirtyArea = dirtyTiles.poll();
                }

                LOGGER.info("Updated " + updateCount + " areas with a total of " + updatedArea + " tiles");
            }

            if (offScreenGraphics != null) {
                offScreenGraphics.flush();
                offScreenGraphics = null;
            }
        } catch (@Nonnull final SlickException e) {
            // some strange problem
        }
    }
}

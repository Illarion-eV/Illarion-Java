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
     * This is the location of the last tile that was requested from the provider.
     */
    @Nonnull
    private final Location lastRequestedLocation;

    /**
     * This color instance is used for the drawing operations.
     */
    @Nonnull
    private final Color tempDrawingColor;

    /**
     * This parameter is set {@code true} in case a update of the entire map was requested.
     */
    private boolean fullMapUpdate;

    /**
     * The tiles that were marked as dirty and did not receive a update yet.
     */
    @Nonnull
    private final Queue<Location> dirtyTiles;

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
        lastRequestedLocation = new Location();
        tempDrawingColor = new Color(Color.black);
        dirtyTiles = new LinkedList<Location>();
    }

    /**
     * Get the origin location of the map.
     *
     * @return the maps origin location
     */
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
    public void setTile(final int tileId, final int overlayId, final boolean blocked) {
        if (offScreenGraphics == null) {
            throw new IllegalStateException("Callback called while no callback was requested");
        }
        if (lastRequestedLocation.getScZ() != mapOrigin.getScZ()) {
            return;
        }

        final int texPosX = lastRequestedLocation.getScX() - mapOrigin.getScX();
        final int texPosY = lastRequestedLocation.getScY() - mapOrigin.getScY();

        if ((texPosX < 0) || (texPosX >= WORLD_MAP_WIDTH) || (texPosY < 0) || (texPosY >= WORLD_MAP_HEIGHT)) {
            return;
        }

        if (tileId == NO_TILE) {
            SlickGraphics.transferColor(MapColor.getColor(NO_TILE), tempDrawingColor);
        } else {
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
        }
        tempDrawingColor.a = 1.f;

        offScreenGraphics.setColor(tempDrawingColor);
        offScreenGraphics.fillRect(texPosX, texPosY, 1, 1);
    }

    @Override
    public void setTileChanged(@Nonnull final Location location) {
        dirtyTiles.offer(new Location(location));
    }

    @Override
    public void setMapChanged() {
        clear();
        fullMapUpdate = true;
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
                offScreenGraphics.setColor(Color.black);
                offScreenGraphics.fillRect(0, 0, WORLD_MAP_WIDTH, WORLD_MAP_HEIGHT);
            }

            if (fullMapUpdate) {
                fullMapUpdate = false;
                if (offScreenGraphics == null) {
                    offScreenGraphics = worldMapImage.getGraphics();
                }
                for (int x = 0; x < WORLD_MAP_WIDTH; x++) {
                    for (int y = 0; y < WORLD_MAP_HEIGHT; y++) {
                        lastRequestedLocation.set(mapOrigin);
                        lastRequestedLocation.addSC(x, y, 0);
                        provider.requestTile(lastRequestedLocation, this);
                    }
                }
            }

            if (!dirtyTiles.isEmpty()) {
                Location dirtyLocation = dirtyTiles.poll();
                while (dirtyLocation != null) {
                    lastRequestedLocation.set(dirtyLocation);
                    provider.requestTile(lastRequestedLocation, this);
                    dirtyLocation = dirtyTiles.poll();
                }
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

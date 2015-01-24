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
package org.illarion.engine.backend.gdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import illarion.common.types.Location;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.MapColor;
import org.illarion.engine.graphic.WorldMap;
import org.illarion.engine.graphic.WorldMapDataProvider;
import org.illarion.engine.graphic.WorldMapDataProviderCallback;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxWorldMap implements WorldMap, WorldMapDataProviderCallback {
    /**
     * The origin location of the map.
     */
    @Nonnull
    private final Location mapOrigin;

    /**
     * The location of the player.
     */
    @Nonnull
    private final Location playerLocation;

    /**
     * The texture that shows the world map.
     */
    @Nonnull
    private final GdxTexture worldMapTexture;

    /**
     * The pixel data of the world map.
     */
    @Nonnull
    @GuardedBy("worldMapPixels")
    private final Pixmap worldMapPixels;

    /**
     * The data provider that supplies the data of the world map.
     */
    @Nonnull
    private final WorldMapDataProvider provider;

    /**
     * The color instance that is used temporary to calculate color values.
     */
    @Nonnull
    private final Color tempDrawingColor;

    /**
     * This flag is set {@code true} in case the map requires to be rendered again.
     */
    private boolean mapDirty;

    GdxWorldMap(@Nonnull WorldMapDataProvider provider) {
        this.provider = provider;
        mapOrigin = new Location();
        playerLocation = new Location();

        worldMapPixels = new Pixmap(WORLD_MAP_WIDTH, WORLD_MAP_HEIGHT, Pixmap.Format.RGB888);
        worldMapTexture = new GdxTexture(new TextureRegion(new Texture(worldMapPixels)));
        tempDrawingColor = new Color();
    }

    @Nonnull
    @Override
    public Location getMapOrigin() {
        return mapOrigin;
    }

    @Nonnull
    @Override
    public Location getPlayerLocation() {
        return playerLocation;
    }

    @Nonnull
    @Override
    public org.illarion.engine.graphic.Texture getWorldMap() {
        return worldMapTexture;
    }

    @Override
    public void setTile(@Nonnull Location loc, int tileId, int overlayId, boolean blocked) {
        if (loc.getScZ() != mapOrigin.getScZ()) {
            return;
        }

        int texPosX = loc.getScX() - mapOrigin.getScX();
        int texPosY = loc.getScY() - mapOrigin.getScY();

        if ((texPosX < 0) || (texPosX >= WORLD_MAP_WIDTH) || (texPosY < 0) || (texPosY >= WORLD_MAP_HEIGHT)) {
            return;
        }

        if (tileId != NO_TILE) {
            GdxGraphics.transferColor(MapColor.getColor(tileId), tempDrawingColor);
            if (overlayId != NO_TILE) {
                org.illarion.engine.graphic.Color mapColor = MapColor.getColor(tileId);
                tempDrawingColor.r += mapColor.getRedf();
                tempDrawingColor.g += mapColor.getGreenf();
                tempDrawingColor.b += mapColor.getBluef();
                tempDrawingColor.mul(0.5f);
            }
            if (blocked) {
                tempDrawingColor.mul(0.7f);
            }
            tempDrawingColor.a = 1.f;

            synchronized (worldMapPixels) {
                worldMapPixels.setColor(tempDrawingColor);
                worldMapPixels.drawPixel(texPosX, texPosY);
                mapDirty = true;
            }
        }
    }

    private boolean currentlyFetchingTiles;
    private boolean cancelFetchingTiles;

    @Override
    public void setTileChanged(@Nonnull Location location) {
        if (cancelFetchingTiles) {
            return;
        }
        if (location.getScZ() != mapOrigin.getScZ()) {
            return;
        }
        currentlyFetchingTiles = true;
        provider.requestTile(location, this);
        currentlyFetchingTiles = false;
        synchronized (this) {
            notifyAll();
        }
    }

    @Override
    public void setMapChanged() {
        currentlyFetchingTiles = true;
        Location tempLocation = new Location();
        for (int x = 0; x < WorldMap.WORLD_MAP_WIDTH; x++) {
            for (int y = 0; y < WorldMap.WORLD_MAP_HEIGHT; y++) {
                if (cancelFetchingTiles) {
                    break;
                }
                tempLocation.setSC(x + mapOrigin.getScX(), y + mapOrigin.getScY(), mapOrigin.getScZ());
                provider.requestTile(tempLocation, this);
            }
        }
        currentlyFetchingTiles = false;
        synchronized (this) {
            notifyAll();
        }
    }

    @Override
    public void setPlayerLocation(@Nonnull Location location) {
        playerLocation.set(location);
    }

    @Override
    public void setMapOrigin(@Nonnull Location location) {
        mapOrigin.set(location);
        clear();
    }

    @Override
    public void clear() {
        cancelFetchingTiles = true;
        if (currentlyFetchingTiles) {
            synchronized (this) {
                while (currentlyFetchingTiles) {
                    try {
                        wait();
                    } catch (@Nonnull InterruptedException ignored) {
                    }
                }
            }
        }
        cancelFetchingTiles = false;
        synchronized (worldMapPixels) {
            worldMapPixels.setColor(Color.BLACK);
            worldMapPixels.fill();
            mapDirty = true;
        }
    }

    @Override
    public void render(@Nonnull GameContainer container) {
        if (mapDirty) {
            synchronized (worldMapPixels) {
                worldMapTexture.getTextureRegion().getTexture().draw(worldMapPixels, 0, 0);
                mapDirty = false;
            }
        }
    }

    @Override
    public void dispose() {
        worldMapTexture.getTextureRegion().getTexture().dispose();
    }
}

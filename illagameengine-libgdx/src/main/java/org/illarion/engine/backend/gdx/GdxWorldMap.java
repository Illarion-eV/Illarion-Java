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
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import illarion.common.types.ServerCoordinate;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.MapColor;
import org.illarion.engine.graphic.WorldMap;
import org.illarion.engine.graphic.WorldMapDataProvider;
import org.illarion.engine.graphic.WorldMapDataProviderCallback;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxWorldMap implements WorldMap, WorldMapDataProviderCallback {
    /**
     * The origin location of the map.
     */
    @Nullable
    private ServerCoordinate mapOrigin;

    /**
     * The location of the player.
     */
    @Nullable
    private ServerCoordinate playerLocation;

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
    @GuardedBy("worldMapPixels")
    private boolean mapDirty;

    /**
     * Marks the native buffers as released, also for background refreshes.
     */
    private volatile boolean disposed;

    GdxWorldMap(@Nonnull WorldMapDataProvider provider) {
        this(provider, new Pixmap(WORLD_MAP_WIDTH, WORLD_MAP_HEIGHT, Format.RGB888));
    }

    /**
     * Create a map taking ownership of the supplied pixel buffer.
     *
     * @param provider the source of map tiles
     * @param pixels the pixel buffer retained until the map is disposed
     */
    GdxWorldMap(@Nonnull WorldMapDataProvider provider, @Nonnull Pixmap pixels) {
        this.provider = provider;
        worldMapPixels = pixels;
        try {
            worldMapTexture = new GdxTexture(new TextureRegion(new Texture(worldMapPixels)));
        } catch (RuntimeException | Error failure) {
            worldMapPixels.dispose();
            throw failure;
        }

        tempDrawingColor = new Color();
    }

    @Nullable
    @Override
    public ServerCoordinate getMapOrigin() {
        return mapOrigin;
    }

    @Nullable
    @Override
    public ServerCoordinate getPlayerLocation() {
        return playerLocation;
    }

    @Nonnull
    @Override
    public org.illarion.engine.graphic.Texture getWorldMap() {
        return worldMapTexture;
    }

    @Override
    public void setTile(@Nonnull ServerCoordinate loc, int tileId, int overlayId, boolean blocked) {
        synchronized (worldMapPixels) {
            if (disposed) {
                return;
            }

            if (mapOrigin == null) {
                throw new IllegalStateException("World map is not ready yet. The origin is not set.");
            }

            if (loc.getZ() != mapOrigin.getZ()) {
                return;
            }

            int texPosX = loc.getX() - mapOrigin.getX();
            int texPosY = loc.getY() - mapOrigin.getY();

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

                worldMapPixels.setColor(tempDrawingColor);
                worldMapPixels.drawPixel(texPosX, texPosY);
                mapDirty = true;
            }
        }
    }

    private boolean currentlyFetchingTiles;
    private boolean cancelFetchingTiles;

    @Override
    public void setTileChanged(@Nonnull ServerCoordinate location) {
        if (disposed) {
            return;
        }

        if (mapOrigin == null) {
            throw new IllegalStateException("World map is not ready yet. The origin is not set.");
        }
        if (cancelFetchingTiles) {
            return;
        }
        if (location.getZ() != mapOrigin.getZ()) {
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
        if (disposed) {
            return;
        }

        if (mapOrigin == null) {
            throw new IllegalStateException("World map is not ready yet. The origin is not set.");
        }
        currentlyFetchingTiles = true;
        for (int x = 0; x < WorldMap.WORLD_MAP_WIDTH; x++) {
            for (int y = 0; y < WorldMap.WORLD_MAP_HEIGHT; y++) {
                if (cancelFetchingTiles || disposed) {
                    break;
                }
                provider.requestTile(new ServerCoordinate(mapOrigin, x, y, 0), this);
            }
        }
        currentlyFetchingTiles = false;
        synchronized (this) {
            notifyAll();
        }
    }

    @Override
    public void setPlayerLocation(@SuppressWarnings("NullableProblems") @Nonnull ServerCoordinate location) {
        playerLocation = location;
    }

    @Override
    public void setMapOrigin(@Nonnull ServerCoordinate location) {
        mapOrigin = location;
        clear();
    }

    @Override
    public void clear() {
        if (disposed) {
            return;
        }

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
            if (disposed) {
                return;
            }

            worldMapPixels.setColor(Color.BLACK);
            worldMapPixels.fill();
            mapDirty = true;
        }
    }

    @Override
    public void render(@Nonnull GameContainer container) {
        synchronized (worldMapPixels) {
            if (!disposed && mapDirty) {
                worldMapTexture.getTextureRegion().getTexture().draw(worldMapPixels, 0, 0);
                mapDirty = false;
            }
        }
    }

    @Override
    public void dispose() {
        synchronized (worldMapPixels) {
            if (disposed) {
                return;
            }

            disposed = true;
            // Delayed provider callbacks must not write to the released native buffer.
            try {
                worldMapTexture.getTextureRegion().getTexture().dispose();
            } finally {
                worldMapPixels.dispose();
            }
        }
    }
}

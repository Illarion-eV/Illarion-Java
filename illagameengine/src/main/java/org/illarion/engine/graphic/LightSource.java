/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Common Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Common Library.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.engine.graphic;

import illarion.common.types.Location;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles a light source and contains its rays, the location and the
 * color of the light.
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class LightSource {
    /**
     * This cache array stores the light sources that were created already at a
     * given size and are currently not in use, so they can be reused later.
     */
    @SuppressWarnings("unchecked")
    private static final List<LightSource>[] CACHE = new List[LightTracer.MAX_RADIUS];

    /**
     * The brightness of the light. This acts like a general modifier on the
     * brightness of the light that reduces anyway with increasing distance from
     * the light source.
     */
    private double bright;

    /**
     * The color of the light itself.
     */
    @Nonnull
    private final Color color;

    /**
     * The dirty flag, this is set to true in case there are further
     * calculations needed and to false in case all calculations are done.
     */
    private boolean dirty;

    /**
     * The intensity array stores the calculated light intensity values. These
     * result from the pre-calculated light rays along with the situation on the
     * map such as objects that block out the light.
     */
    @Nonnull
    private final double[][] intensity;

    /**
     * Invert flag. If this is set to true it results in a reduce of the light
     * share on a tile instead of a increase.
     */
    private boolean invert;

    /**
     * Store if this light is currently in the cache.
     */
    private boolean lightCached;

    /**
     * The location of the light source on the map.
     */
    @Nonnull
    private Location location;

    /**
     * The reference map that is used to get the data how the light spreads on
     * the map.
     */
    private LightingMap mapSource;

    /**
     * The light rays that spread from the light source.
     */
    private final LightRays rays;

    /**
     * The length of the light rays that are send out by this light source.
     */
    private int size;

    /**
     * A location instance for temporary purposes. This is for calculations or
     * to get some data from other classes.
     */
    private final Location tempLocation = new Location();

    /**
     * Constructor for a new light source at a given location with some encoded
     * settings.
     *
     * @param location the location of the light source on the server map
     * @param encoding the encoding of the light, this contains the color, the
     * brightness, the size and the inversion flag
     */
    private LightSource(@Nonnull final Location location, final int encoding) {
        final int newSize = (encoding / 10000) % 10;
        rays = LightTracer.getRays(newSize);
        intensity = new double[(newSize * 2) + 1][(newSize * 2) + 1];
        color = new Color(Color.WHITE);

        init(location, encoding);
    }

    /**
     * Retrieve a light from the cache or create a new one.
     *
     * @param loc the location of the light source on the map
     * @param encoding the encoding of the light source that has to be decoded
     * in order to receive the parameters of the light
     * @return the prepared instance of the light source
     */
    @Nonnull
    @SuppressWarnings("nls")
    public static LightSource createLight(
            @Nonnull final Location loc, final int encoding) {
        final int size = ((encoding / 10000) % 10) - 1;
        if (size < 0) {
            throw new IllegalArgumentException("empty light source");
        }
        if (size >= LightTracer.MAX_RADIUS) {
            throw new IllegalArgumentException("light size too large");
        }

        if (CACHE[size] == null) {
            return new LightSource(loc, encoding);
        }

        LightSource light = null;
        synchronized (CACHE[size]) {
            if (!CACHE[size].isEmpty()) {
                light = CACHE[size].remove(CACHE[size].size() - 1);
            }
        }

        if (light == null) {
            return new LightSource(loc, encoding);
        }

        light.lightCached = false;
        light.init(loc, encoding);
        light.resetShadows();
        return light;
    }

    /**
     * Release a light source and put it in the cache for later reuse.
     *
     * @param light the light that shall be put into the cache.
     */
    public static void releaseLight(@Nonnull final LightSource light) {
        final int size = light.size - 1;

        if (CACHE[size] == null) {
            CACHE[size] = new ArrayList<>();
        }
        synchronized (CACHE[size]) {
            if (!CACHE[size].contains(light)) {
                CACHE[size].add(light);
            }
        }
    }

    /**
     * Apply shadow map to rendering target. So all calculated intensity values
     * are added to the map by this function.
     */
    public void apply() {
        if (lightCached) {
            return;
        }
        final int xOff = location.getScX() - size;
        final int yOff = location.getScY() - size;

        tempLocation.setSC(xOff, yOff, location.getScZ());
        final int xLimit = intensity.length + xOff;
        final int yLimit = intensity.length + yOff;
        while (tempLocation.getScX() < xLimit) {
            tempLocation.setSC(tempLocation.getScX(), yOff, tempLocation.getScZ());
            while (tempLocation.getScY() < yLimit) {
                final double locIntensity = intensity[tempLocation.getScX() - xOff][tempLocation.getScY() - yOff];
                if (locIntensity == 0) {
                    tempLocation.addSC(0, 1, 0);
                    continue;
                }

                final double factor = locIntensity * bright;

                final Color tempColor = new Color(color);
                tempColor.multiply((float) factor);
                if (invert) {
                    tempColor.multiply(-1.f);
                }

                // set the light on the map
                mapSource.setLight(tempLocation, tempColor);

                tempLocation.addSC(0, 1, 0);
            }
            tempLocation.addSC(1, 0, 0);
        }
    }

    /**
     * Recalculate the shadow map of the light source in case its needed.
     *
     * @return true in case anything was done
     */
    public boolean calculateShadows() {
        if (!dirty) {
            return false;
        }

        // reset array
        resetShadows();

        rays.apply(this);

        dirty = false;

        return true;
    }

    /**
     * Get the location of this light source.
     *
     * @return the location of the light source
     */
    @Nonnull
    public Location getLocation() {
        return location;
    }

    /**
     * Get the length of the light rays of this light source.
     *
     * @return the length of the light rays of this light source
     */
    public int getSize() {
        return size;
    }

    /**
     * Initializes the light source. So set the location to the correct one and
     * update the color of the light source by decoding the encoded data.
     *
     * @param newLoc the new location of this light source
     * @param encoding the encoded data that defines the light source
     */
    @SuppressWarnings("nls")
    private void init(@Nonnull final Location newLoc, final int encoding) {
        location = newLoc;
        int remEnc = encoding;

        final float blue = (remEnc % 10) / 9.f;
        remEnc /= 10;
        final float green = (remEnc % 10) / 9.f;
        remEnc /= 10;
        final float red = (remEnc % 10) / 9.f;
        remEnc /= 10;
        color.setRedf(red);
        color.setGreenf(green);
        color.setBluef(blue);

        bright = (remEnc % 10) / 9.f;
        remEnc /= 10;
        size = remEnc % 10;
        remEnc /= 10;
        invert = (remEnc == 1);

        dirty = true;
    }

    /**
     * Check if this light source is dirty and needs further calculations or
     * not.
     *
     * @return true in case the light source needs calculations
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * Notify the light source about a relevant change of data. Light source
     * will become dirty if change was within it's area of influence.
     *
     * @param changeLoc the location the change occurred on.
     */
    @SuppressWarnings("nls")
    public void notifyChange(@Nonnull final Location changeLoc) {
        assert !lightCached;
        if (location.getScZ() != changeLoc.getScZ()) {
            return;
        }

        if ((Math.abs(changeLoc.getScX() - location.getScX()) <= size) &&
                (Math.abs(changeLoc.getScY() - location.getScY()) <= size)) {
            dirty = true;
        }
    }

    /**
     * Refresh the calculated shadow and light data. This forces the light
     * source to recalculate all values.
     */
    public void refresh() {
        dirty = true;
    }

    /**
     * Reset all recalculated values. This should be done before the light
     * source object is put into the cache for later usage.
     */
    private void resetShadows() {
        for (int i = 0; i < intensity.length; ++i) {
            for (int j = 0; j < intensity.length; ++j) {
                intensity[j][i] = 0;
            }
        }
    }

    /**
     * Set light intensity in shadow map and return opacity value.
     *
     * @param x the X offset of the location that's intensity shall be set to the
     * location of the light source
     * @param y the Y offset of the location that's intensity shall be set to the
     * location of the light source
     * @param newInt the intensity that shall for this location now
     * @return the obscurity of the location that's light intensity was just set
     */
    public int setIntensity(final int x, final int y, final double newInt) {
        assert !lightCached;
        tempLocation.setSC(location.getScX() + x, location.getScY() + y, location.getScZ());

        if (((x == 0) && (y == 0)) || mapSource.acceptsLight(tempLocation, x, y)) {
            intensity[x + size][y + size] = newInt;
        }
        return mapSource.blocksView(tempLocation);
    }

    /**
     * Set a new map source to the light source. This needs to be the map the
     * light is on. All data to calculate the shadows is taken from this map,
     * also all results of the calculations are send to this map.
     *
     * @param newMapSource the map that contains the light source
     */
    public void setMapSource(final LightingMap newMapSource) {
        assert !lightCached;
        if ((mapSource == null) || (mapSource != newMapSource)) {
            mapSource = newMapSource;
            dirty = true;
        }
    }
}

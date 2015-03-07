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
package org.illarion.engine.graphic;

import illarion.common.types.Location;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class handles a light source and contains its rays, the location and the color of the light.
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class LightSource {
    private final int encodedValue;
    /**
     * The flag of this light source was already deleted.
     */
    private boolean disposed;

    /**
     * The brightness of the light. This acts like a general modifier on the rightness of the light that reduces
     * anyway with increasing distance from the light source.
     */
    private final double bright;

    /**
     * The color of the light itself.
     */
    @Nonnull
    private final Color color;

    /**
     * The dirty flag, this is set to true in case there are further calculations needed and to false in case all
     * calculations are done.
     */
    private boolean dirty;

    /**
     * The intensity array stores the calculated light intensity values. These result from the pre-calculated light
     * rays along with the situation on the map such as objects that block out the light.
     */
    @Nonnull
    private final double[][] intensity;

    /**
     * Invert flag. If this is set to true it results in a reduce of the light share on a tile instead of a increase.
     */
    private final boolean invert;

    /**
     * The location of the light source on the map.
     */
    @Nonnull
    private final Location location;

    /**
     * The reference map that is used to get the data how the light spreads on the map.
     */
    private LightingMap mapSource;

    /**
     * The light rays that spread from the light source.
     */
    private final LightRays rays;

    /**
     * The length of the light rays that are send out by this light source.
     */
    private final int size;

    /**
     * A location instance for temporary purposes. This is for calculations or
     * to get some data from other classes.
     */
    @Nonnull
    private final Location tempLocation = new Location();

    @Nonnull
    private final Lock calculationLock;

    /**
     * Constructor for a new light source at a given location with some encoded
     * settings.
     *
     * @param location the location of the light source on the server map
     * @param encoding the encoding of the light, this contains the color, the
     * brightness, the size and the inversion flag
     */
    public LightSource(@Nonnull Location location, int encoding) {
        encodedValue = encoding;
        int newSize = (encoding / 10000) % 10;
        rays = LightRays.getRays(newSize);
        intensity = new double[(newSize * 2) + 1][(newSize * 2) + 1];
        color = new Color(Color.WHITE);

        this.location = location;

        float blue = (encoding % 10) / 9.f;
        float green = ((encoding / 10) % 10) / 9.f;
        float red = ((encoding / 100) % 10) / 9.f;

        color.setRedf(red);
        color.setGreenf(green);
        color.setBluef(blue);

        bright = ((encoding / 1000) % 10) / 9.f;
        size = (encoding / 10000) % 10;
        invert = (encoding / 100000) == 1;

        dirty = true;

        calculationLock = new ReentrantLock();
    }

    /**
     * Apply shadow map to rendering target. So all calculated intensity values
     * are added to the map by this function.
     */
    public void apply() {
        if (mapSource == null) {
            throw new IllegalStateException("The light source is not properly bound to a map yet.");
        }
        int xOff = location.getScX() - size;
        int yOff = location.getScY() - size;

        tempLocation.setSC(xOff, yOff, location.getScZ());
        int xLimit = intensity.length + xOff;
        int yLimit = intensity.length + yOff;
        while (tempLocation.getScX() < xLimit) {
            tempLocation.setSC(tempLocation.getScX(), yOff, tempLocation.getScZ());
            while (tempLocation.getScY() < yLimit) {
                double locIntensity = intensity[tempLocation.getScX() - xOff][tempLocation.getScY() - yOff];
                if (locIntensity == 0) {
                    tempLocation.addSC(0, 1, 0);
                    continue;
                }

                double factor = locIntensity * bright;

                Color tempColor = new Color(color);
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
        dirty = false;

        // reset array
        resetShadows();
        rays.apply(this);

        return true;
    }

    /**
     * Get the location of this light source.
     *
     * @return the location of the light source
     */
    @Contract(pure = true)
    @Nonnull
    public Location getLocation() {
        return location;
    }

    /**
     * Get the length of the light rays of this light source.
     *
     * @return the length of the light rays of this light source
     */
    @Contract(pure = true)
    public int getSize() {
        return size;
    }

    /**
     * Check if this light source is dirty and needs further calculations or not.
     *
     * @return true in case the light source needs calculations
     */
    @Contract(pure = true)
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
    public void notifyChange(@Nonnull Location changeLoc) {
        if (location.getScZ() != changeLoc.getScZ()) {
            return;
        }

        if ((Math.abs(changeLoc.getScX() - location.getScX()) <= size) &&
                (Math.abs(changeLoc.getScY() - location.getScY()) <= size)) {
            refresh();
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
        for (double[] anIntensity : intensity) {
            Arrays.fill(anIntensity, 0);
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
    public int setIntensity(int x, int y, double newInt) {
        if (mapSource == null) {
            throw new IllegalStateException("The light source is not properly bound to a map yet.");
        }
        if (Math.abs(x) > size) {
            throw new IllegalArgumentException("The X offset for the light is out of bounds: " + x);
        }
        if (Math.abs(y) > size) {
            throw new IllegalArgumentException("The Y offset for the light is out of bounds: " + y);
        }

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
    void setMapSource(@Nonnull LightingMap newMapSource) {
        if ((mapSource == null) || !Objects.equals(mapSource, newMapSource)) {
            mapSource = newMapSource;
            dirty = true;
        }
    }

    void dispose() {
        disposed = true;
    }

    @Contract(pure = true)
    boolean isDisposed() {
        return disposed;
    }

    @Contract(pure = true)
    public int getEncodedValue() {
        return encodedValue;
    }

    @Override
    @Contract(value = "null->false", pure = true)
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof LightSource) && equals((LightSource) obj);
    }

    @Contract(value = "null->false", pure = true)
    public boolean equals(@Nullable LightSource light) {
        return (light != null) && (light.getEncodedValue() == getEncodedValue()) && light.location.equals(location);
    }

    @Override
    @Contract(pure = true)
    public int hashCode() {
        return (int) (((23L + getEncodedValue()) * 31L) + location.hashCode());
    }

    @Override
    @Nonnull
    @Contract(pure = true)
    public String toString() {
        return "LightSource (" + location + ", " + color + ",  brightness: " + bright + ", size: " + size +
                ", dirty: " + dirty + ')';
    }

    @Nonnull
    @Contract(pure = true)
    Lock getCalculationLock() {
        return calculationLock;
    }
}

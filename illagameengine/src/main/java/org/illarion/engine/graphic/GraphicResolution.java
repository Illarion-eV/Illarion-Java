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

import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class is used to supply and compare possible display resolutions.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class GraphicResolution {
    /**
     * The bits per point of this resolution.
     */
    private final int bpp;

    /**
     * The screen height of this resolution.
     */
    private final int height;

    /**
     * The refresh rate of this resolution.
     */
    private final int refreshRate;

    /**
     * The screen width of that resolution.
     */
    private final int width;

    /**
     * Default Constructor for a graphic resolution definition.
     * Sets width = (the local screen width) * 0.9
     * height = (the local screen height) * 0.9
     * refreshRate = local refreshRate;
     */
    public GraphicResolution(){
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = environment.getDefaultScreenDevice();
        DisplayMode dm = gd.getDisplayMode();
        height = (int) (dm.getHeight() * 0.9);
        width = (int) (dm.getWidth() * 0.9);
        refreshRate = dm.getRefreshRate();
        bpp = dm.getBitDepth();
    }
    /**
     * Constructor for a graphic resolution definition.
     *
     * @param newWidth the width of this resolution in pixel
     * @param newHeight the height of this resolution in pixel
     * @param newBpp the bits per point of this resolution
     * @param refresh the refresh rate of this resolution in Hz
     */
    public GraphicResolution(int newWidth, int newHeight, int newBpp, int refresh) {
        height = newHeight;
        width = newWidth;
        bpp = newBpp;
        refreshRate = refresh;
    }

    /**
     * Create a new graphic resolution object from a definition string.
     *
     * @param definition the text that is parsed to get the values for the resolution
     * @throws IllegalArgumentException in case the string can't be parsed
     */
    public GraphicResolution(@Nonnull CharSequence definition) {
        Pattern pattern = Pattern.compile("(\\d+) x (\\d+) x (\\d+) @ (\\d+)Hz");
        Matcher matcher = pattern.matcher(definition);

        if (matcher.matches()) {
            width = Integer.parseInt(matcher.group(1));
            height = Integer.parseInt(matcher.group(2));
            bpp = Integer.parseInt(matcher.group(3));
            refreshRate = Integer.parseInt(matcher.group(4));
            return;
        }

        pattern = Pattern.compile("(\\d+) x (\\d+) @ (\\d+)Hz");
        matcher = pattern.matcher(definition);
        if (matcher.matches()) {
            width = Integer.parseInt(matcher.group(1));
            height = Integer.parseInt(matcher.group(2));
            bpp = -1;
            refreshRate = Integer.parseInt(matcher.group(3));
            return;
        }

        pattern = Pattern.compile("(\\d+) x (\\d+) x (\\d+)");
        matcher = pattern.matcher(definition);
        if (matcher.matches()) {
            width = Integer.parseInt(matcher.group(1));
            height = Integer.parseInt(matcher.group(2));
            bpp = Integer.parseInt(matcher.group(3));
            refreshRate = -1;
            return;
        }

        pattern = Pattern.compile("(\\d+) x (\\d+)");
        matcher = pattern.matcher(definition);
        if (matcher.matches()) {
            width = Integer.parseInt(matcher.group(1));
            height = Integer.parseInt(matcher.group(2));
            bpp = -1;
            refreshRate = -1;
            return;
        }

        throw new IllegalArgumentException("Can't parse string.");
    }

    /**
     * Compare the resolution with another resolution object.
     *
     * @param compRes the object this one is compared to
     * @return {@code true} in case all values of the resolutions are equal
     */
    @Contract(value = "null->false", pure = true)
    public boolean equals(@Nullable GraphicResolution compRes) {
        return (compRes != null) && equals(compRes.width, compRes.height, compRes.bpp, compRes.refreshRate);
    }

    /**
     * Compare the resolution to a set of resolution values.
     *
     * @param compWidth the width to compare with
     * @param compHeight the height to compare with
     * @param compBpp the bits per point to compare with
     * @param compRefresh the fresh rate to compare with
     * @return {@code true} in case all values equal with this instance
     */
    @Contract(pure = true)
    public boolean equals(int compWidth, int compHeight, int compBpp, int compRefresh) {
        return (height == compHeight) && (width == compWidth) && (bpp == compBpp) && (refreshRate == compRefresh);
    }

    /**
     * Compare this resolution to another object.
     *
     * @param obj the object this resolution is to be compared with
     * @return {@code true} in case both objects are equal
     */
    @Override
    @Contract(value = "null->false", pure = true)
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof GraphicResolution) && equals((GraphicResolution) obj);
    }

    /**
     * Compare the resolution with another string. The string is expected to be
     * created with another instance of this class.
     *
     * @param compString the string to compare with
     * @return {@code true} if this object and the string represent the
     * same resolution
     */
    @Contract(value = "null->false", pure = true)
    public boolean equals(@Nullable String compString) {
        return (compString != null) && toString().equals(compString);
    }

    /**
     * Get the bits per point of this resolution.
     *
     * @return the bits per point of this resolution or -1 in case it was not
     * possible to determine the bits per point.
     */
    @Contract(pure = true)
    public int getBPP() {
        return bpp;
    }

    /**
     * Get the height of this resolution in pixel.
     *
     * @return the height of this resolution
     */
    @Contract(pure = true)
    public int getHeight() {
        return height;
    }

    /**
     * Get the refresh rate of this resolution in Hz.
     *
     * @return the refresh rate in Hz
     */
    @Contract(pure = true)
    public int getRefreshRate() {
        return refreshRate;
    }

    /**
     * Get the width of this resolution in pixel.
     *
     * @return the width of this resolution
     */
    @Contract(pure = true)
    public int getWidth() {
        return width;
    }

    /**
     * Generate a hash code for this graphic resolution object.
     *
     * @return the hash code of this object
     */
    @Override
    @Contract(pure = true)
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * Get a human readable string that describes that resolution.
     *
     * @return human readable string to describe that resolution
     */
    @Nonnull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(width);
        builder.append(' ').append('x').append(' ');
        builder.append(height);
        if (bpp > -1) {
            builder.append(' ').append('x').append(' ');
            builder.append(bpp);
        }
        if (refreshRate > -1) {
            builder.append(' ').append('@').append(' ');
            builder.append(refreshRate);
            builder.append('H').append('z');
        }
        return builder.toString();
    }
}

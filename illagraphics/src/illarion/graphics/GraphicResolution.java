/*
 * This file is part of the Illarion Graphics Engine.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Graphics Engine is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Graphics Engine is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Graphics Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.graphics;

/**
 * This class is used to supply and compare possible display resolutions.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class GraphicResolution {
    /**
     * The bits per point of this resolution.
     */
    private final int bpp;

    /**
     * The string builder used to construct the strings used to make the
     * resolution human readable.
     */
    private final StringBuilder builder;

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
     * Constructor for a graphic resolution definition.
     * 
     * @param newWidth the width of this resolution in pixel
     * @param newHeight the height of this resolution in pixel
     * @param newBpp the bits per point of this resolution
     * @param refresh the refresh rate of this resolution in Hz
     */
    public GraphicResolution(final int newWidth, final int newHeight,
        final int newBpp, final int refresh) {
        height = newHeight;
        width = newWidth;
        bpp = newBpp;
        refreshRate = refresh;
        builder = new StringBuilder();
    }

    /**
     * Compare the resolution with another resolution object.
     * 
     * @param compRes the object this one is compared to
     * @return <code>true</code> in case all values of the resolutions are equal
     */
    public boolean equals(final GraphicResolution compRes) {
        return ((height == compRes.height) && (width == compRes.width)
            && (bpp == compRes.bpp) && (refreshRate == compRes.refreshRate));
    }

    /**
     * Compare the resolution to a set of resolution values.
     * 
     * @param compWidth the width to compare with
     * @param compHeight the height to compare with
     * @param compBpp the bits per point to compare with
     * @param compRefresh the fresh rate to compare with
     * @return <code>true</code> in case all values equal with this instance
     */
    public boolean equals(final int compWidth, final int compHeight,
        final int compBpp, final int compRefresh) {
        return ((height == compHeight) && (width == compWidth)
            && (bpp == compBpp) && (refreshRate == compRefresh));
    }

    /**
     * Compare this resolution to another object. This will result in a return
     * of false, in case the object is not castable to a String or a
     * GraphicResolution object.
     * 
     * @param compObj the object this resolution is to be compared with
     * @return <code>true</code> in case both objects are equal
     */
    @Override
    public boolean equals(final Object compObj) {
        if (compObj instanceof String) {
            return equals((String) compObj);
        }
        if (compObj instanceof GraphicResolution) {
            return equals((GraphicResolution) compObj);
        }
        return false;
    }

    /**
     * Compare the resolution with another string. The string is expected to be
     * created with another instance of this class.
     * 
     * @param compString the string to compare with
     * @return <code>true</code> if this object and the string represent the
     *         same resolution
     */
    public boolean equals(final String compString) {
        return toString().equals(compString);
    }

    /**
     * Get the bits per point of this resolution.
     * 
     * @return the bits per point of this resolution or -1 in case it was not
     *         possible to determine the bits per point.
     */
    public int getBPP() {
        return bpp;
    }

    /**
     * Get the height of this resolution in pixel.
     * 
     * @return the height of this resolution
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get the refresh reate of this resolution in Hz.
     * 
     * @return the refresh rate in Hz
     */
    public int getRefreshRate() {
        return refreshRate;
    }

    /**
     * Get the width of this resolution in pixel.
     * 
     * @return the width of this resolution
     */
    public int getWidth() {
        return width;
    }

    /**
     * Generate a hash code for this graphic resolution object.
     * 
     * @return the hashcode of this object
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Get a human readable string that describes that resolution.
     * 
     * @return human readable string to describe that resolution
     */
    @Override
    public String toString() {
        builder.setLength(0);
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

/*
 * This file is part of the Illarion Build Utility.
 * 
 * The Illarion Build Utility is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Build Utility is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Build Utility. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.build.imagepacker;

import java.util.ArrayList;

final class Space implements TextureElement {
    private static final ArrayList<Space> BUFFER = new ArrayList<Space>();

    private int height;

    private int width;
    private int x;
    private int y;

    private Space(final int x, final int y, final int height, final int width) {
        setDim(x, y, height, width);
    }

    public static Space getSpace(final int x, final int y, final int height,
        final int width) {
        if (BUFFER.size() > 0) {
            final Space retSpace = BUFFER.remove(BUFFER.size() - 1);
            retSpace.setDim(x, y, height, width);
            return retSpace;
        }

        return new Space(x, y, height, width);
    }

    public boolean fitsInside(final Sprite s) {
        return ((s.getHeight() <= height) && (s.getWidth() <= width));
    }

    @Override
    public int getHeight() {
        return height;
    }

    public long getSize() {
        return height * width;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    public void recycle() {
        BUFFER.add(this);
    }

    public int toInt() {
        return width * height;
    }

    private void setDim(final int x, final int y, final int height,
        final int width) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }
}

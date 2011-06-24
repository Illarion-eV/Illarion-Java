/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.guiNG.elements;

import illarion.client.ClientWindow;

/**
 * The desktop widget is a plain background layer that allows drawing a
 * wallpaper on the background of the screen.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public class Desktop extends Widget {
    /**
     * Current serialization UID of the desktop class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The wallpaper that is drawn at the background of the desktop.
     */
    private DesktopWallpaper wallpaper;

    /**
     * Draw the desktop, this causes that the wallpaper is drawn first and all
     * children after.
     * 
     * @param delta the time since the render function was called last time
     */
    @Override
    public void draw(final int delta) {
        if (!isVisible()) {
            return;
        }
        ClientWindow.getInstance().getRenderDisplay()
            .setAreaLimit(getAbsX(), getAbsY(), getWidth(), getHeight());
        if (wallpaper != null) {
            wallpaper.draw(delta, getWidth(), getHeight());
        }
        super.draw(delta);
        ClientWindow.getInstance().getRenderDisplay().unsetAreaLimit();
    }

    /**
     * Set a new wallpaper that shall be drawn in the background of the desktop.
     * 
     * @param newWallpaper the new desktop wallpaper
     */
    public void setWallpaper(final DesktopWallpaper newWallpaper) {
        wallpaper = newWallpaper;
    }
}

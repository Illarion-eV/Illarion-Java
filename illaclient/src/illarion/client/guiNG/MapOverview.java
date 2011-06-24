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
package illarion.client.guiNG;

import illarion.client.ClientWindow;
import illarion.client.guiNG.elements.ImageZoomable;
import illarion.client.guiNG.elements.SolidColor;
import illarion.client.guiNG.elements.Widget;
import illarion.client.guiNG.init.SolidColorInit;
import illarion.client.guiNG.init.WorldmapInit;

public class MapOverview extends Widget {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private int height;
    private int width;

    public MapOverview() {

        height = (ClientWindow.getInstance().getScreenWidth() / 2);
        width = height;

        setWidth(width);
        setHeight(height);
        setVisible(true);

        final SolidColor backgroundColor = new SolidColor();
        backgroundColor.setHeight(1024);
        backgroundColor.setWidth(1024);
        backgroundColor.setInitScript(SolidColorInit.getInstance().setColor(0,
            0, 0, 150));
        addChild(backgroundColor);

        final ImageZoomable minimapImage = new ImageZoomable();
        minimapImage.setRelPos(0, 0);
        minimapImage.setWidth(1024);
        minimapImage.setHeight(1024);
        minimapImage.setInitScript(WorldmapInit.getInstance());
        addChild(minimapImage);

        final PlayerPositionOnMap overviewColor = new PlayerPositionOnMap();
        overviewColor.setHeight(3);
        overviewColor.setWidth(3);
        overviewColor.setInitScript(SolidColorInit.getInstance().setColor(
            illarion.graphics.SpriteColor.COLOR_MAX, 0, 0,
            illarion.graphics.SpriteColor.COLOR_MAX));
        minimapImage.addChild(overviewColor);

    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setHeight(final int height) {
        this.height = height;
    }

    @Override
    public void setWidth(final int width) {
        this.width = width;
    }
}

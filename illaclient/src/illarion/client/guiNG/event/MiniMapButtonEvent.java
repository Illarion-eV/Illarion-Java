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
package illarion.client.guiNG.event;

import illarion.client.guiNG.GUI;
import illarion.client.guiNG.elements.ImageZoomable;
import illarion.client.guiNG.elements.Widget;

/**
 * This event is used to control the buttons that are used with the Minimap.
 * 
 * @author Natal Venetz
 * @since 1.22
 * @version 1.22
 */
public class MiniMapButtonEvent implements WidgetEvent {

    /**
     * The constant used to set this button as button to show the overview map.
     */
    public static final int TYPE_OVERVIEW = 2;

    /**
     * The constant used to set this button as button to zoom in.
     */
    public static final int TYPE_ZOOMIN = 1;

    /**
     * The constant used to set this button as button to zoom out.
     */
    public static final int TYPE_ZOOMOUT = 0;

    /**
     * The serialization UID of this event script.
     */
    private static final long serialVersionUID = 1L;

    ImageZoomable imageZoomable;

    /**
     * The type of the BookButton.
     */
    private int buttonType;

    private MiniMapButtonEvent() {
        // private constructor to avoid instances created uncontrolled.
    }

    /**
     * Get a new instance of this event script. This either creates a new
     * instance of this class or returns always the same, depending on what is
     * needed for this script.
     * 
     * @return the instance of this event script that is to be used from now on
     */
    public static MiniMapButtonEvent getInstance() {
        return new MiniMapButtonEvent();
    }

    @Override
    public void handleEvent(final Widget source) {
        if (buttonType == TYPE_ZOOMIN) {
            imageZoomable.zoomIN();
        } else if (buttonType == TYPE_ZOOMOUT) {
            imageZoomable.zoomOUT();
        } else if (buttonType == TYPE_OVERVIEW) {

            GUI.getInstance().showMapOverview();
            System.out.println("Test Overview");
        }

    }

    /**
     * Sets the type of the zoom button.
     * 
     * @param newButtonType The new type of this button
     * @see #TYPE_ZOOMIN
     * @see #TYPE_ZOOMOUT
     */
    public void setButtonType(final int newButtonType) {
        buttonType = newButtonType;
    }

    public void setImageZoomable(final ImageZoomable imageZoomable) {

        this.imageZoomable = imageZoomable;
    }

}

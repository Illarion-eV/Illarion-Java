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
package illarion.client.gui;

import illarion.client.world.World;

import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.DroppableDroppedEvent;
import de.lessvoid.nifty.elements.Element;

/**
 * This class is used to monitor all dropping operations on the droppable area
 * over the game map and notify the interaction manager about a drop in case one
 * happens.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class MapDroppableSubscriber {
    /**
     * Called in case something is dropped on the game map.
     */
    @NiftyEventSubscriber(id="mapDropTarget")
    public void dropOnMap(final String topic, final DroppableDroppedEvent data) {
        final Element droppedElement = data.getDraggable().getElement();
        final int dropSpotX = droppedElement.getX() + (droppedElement.getWidth() / 2);
        final int dropSpotY = droppedElement.getY() + (droppedElement.getHeight() / 2);
        
        droppedElement.setVisible(false);
        
        World.getInteractionManager().dropAtMap(dropSpotX, dropSpotY);
    }

}

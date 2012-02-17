/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.gui;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.DroppableDroppedEvent;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.input.NiftyMouseInputEvent;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.slick2d.input.ForwardingInputSystem;
import de.lessvoid.nifty.tools.SizeValue;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;

import illarion.client.graphics.Item;
import illarion.client.input.DoubleClickOnMapEvent;
import illarion.client.input.DragOnMapEvent;
import illarion.client.input.InputReceiver;
import illarion.client.world.World;
import illarion.client.world.interactive.InteractiveMapTile;

/**
 * This class is used to monitor all double click operations on the game map and notify the
 * interaction manager about a double click in case one happens.
 *
 * @author Vilarion &lt;vilarion@illarion.org&gt;
 */
public final class GameMapDoubleClickHandler
        implements EventTopicSubscriber<DoubleClickOnMapEvent> {

    /**
     * The Nifty-GUI instance that is handling the GUI display currently.
     */
    private Nifty activeNifty;

    /**
     * The screen that takes care for the display currently.
     */
    private Screen activeScreen;

    /**
     * This mouse event instance is used to initiate the double click event.
     */
    private final NiftyMouseInputEvent mouseEvent;

    /**
     * The panel that is located on top of the game map. So this is the lowest located panel and the intended parent
     * for
     * the original location of the double clicked object.
     */
    private Element gamePanel;

    /**
     * The element that is double clicked.
     */
    private Element doubleClickedGraphic;

    /**
     * The element that displays the image that is double clicked.
     */
    private Element doubleClickedImage;

    /**
     * Default constructor that takes care of initializing the variables required for this class to work.
     */
    public GameMapDoubleClickHandler() {
        mouseEvent = new NiftyMouseInputEvent();
    }

    public void bind(final Nifty nifty, final Screen screen) {
        activeNifty = nifty;
        activeScreen = screen;
        gamePanel = screen.findElementByName("gamePanel");
        EventBus.subscribe(InputReceiver.EB_TOPIC, this);
    }

    public boolean handleDoubleClickOnMap(final int x, final int y, final ForwardingInputSystem forwardingControl) {
        final InteractiveMapTile tile = World.getMap().getInteractive().getInteractiveTileOnScreenLoc(x, y);

        if (tile == null) {
            return false;
        }

        if (!tile.isInUseRange()) {
            return false;
        }

        if (activeScreen != null && activeNifty != null) {
            forwardingControl.releaseExclusiveMouse();

            mouseEvent.initialize(x, y, 0, true, false, false);
            mouseEvent.setButton0InitialDown(true);
            activeScreen.mouseEvent(mouseEvent);
        }

        tile.use();

        return true;
    }

    private boolean moveToMouse(final int targetX, final int targetY, final ForwardingInputSystem forwardingControl) {
        World.getPlayer().getMovementHandler().walkTowards(targetX, targetY);
        forwardingControl.requestExclusiveMouse();

        return true;
    }

    /**
     * Handle a input event that was published.
     */
    @Override
    public void onEvent(final String topic, final DoubleClickOnMapEvent data) {
        if (topic.equals(InputReceiver.EB_TOPIC)) {
            if (handleDoubleClickOnMap(data.getX(), data.getY(), data.getForwardingControl())) {
                return;
            }

            // moveToMouse(data.getX(), data.getY(), data.getForwardingControl());
        }
    }
}

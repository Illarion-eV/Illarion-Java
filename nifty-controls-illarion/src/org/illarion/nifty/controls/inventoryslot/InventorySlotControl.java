/*
 * This file is part of the Illarion Nifty-GUI controls.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Nifty-GUI controls is free software: you can redistribute and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Nifty-GUI controls is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Nifty-GUI controls. If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.nifty.controls.inventoryslot;

import java.util.Properties;

import org.bushe.swing.event.EventTopicSubscriber;
import org.illarion.nifty.controls.InventorySlot;

import de.lessvoid.nifty.EndNotify;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.AbstractController;
import de.lessvoid.nifty.controls.DraggableDragCanceledEvent;
import de.lessvoid.nifty.controls.DraggableDragStartedEvent;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.dragndrop.DraggableControl;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.xml.xpp3.Attributes;

/**
 * The control class of the inventory slot.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @deprecated Don't refer to this class in any application. Rather use the
 *             general interface of the inventory slot:
 *             {@link org.illarion.nifty.controls.InventorySlot}
 */
@Deprecated
public class InventorySlotControl extends AbstractController implements
    InventorySlot {
    /**
     * The image that is dragged around.
     */
    private Element draggedImage;

    /**
     * The actual draggable control.
     */
    private Element draggable;

    /**
     * The element that shows the image of the object in the inventory slot.
     */
    private Element backgroundImage;

    /**
     * The label for the image in the background.
     */
    private Element backgroundImageLabel;

    /**
     * The element items can get dropped into.
     */
    private Element droppable;

    /**
     * The event subscriber that is used to monitor the start dragging events.
     */
    private EventTopicSubscriber<DraggableDragStartedEvent> dragStartEvent;

    /**
     * The event subscriber that is used to monitor the stop dragging events.
     */
    private EventTopicSubscriber<DraggableDragCanceledEvent> dragCanceledEvent;

    /**
     * {@inheritDoc}
     */
    @Override
    public void bind(Nifty nifty, Screen screen, Element element,
        Properties parameter, Attributes controlDefinitionAttributes) {
        super.bind(element);

        droppable = element.findElementByName("#droppable");
        draggable = droppable.findElementByName("#draggable");
        draggedImage = draggable.findElementByName("#draggableImage");
        backgroundImage = element.findElementByName("#backgroundImage");
        backgroundImageLabel =
            element.findElementByName("#backgroundImageLabel");

        dragStartEvent =
            new EventTopicSubscriber<DraggableDragStartedEvent>() {
                @Override
                public void onEvent(String topic,
                    DraggableDragStartedEvent data) {
                    setVisibleOfDraggedImage(true);
                }
            };

        dragCanceledEvent =
            new EventTopicSubscriber<DraggableDragCanceledEvent>() {
                @Override
                public void onEvent(String topic,
                    DraggableDragCanceledEvent data) {
                    setVisibleOfDraggedImage(false);
                }
            };

        nifty.subscribe(screen, draggable.getId(),
            DraggableDragStartedEvent.class, dragStartEvent);
        nifty.subscribe(screen, draggable.getId(),
            DraggableDragCanceledEvent.class, dragCanceledEvent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setImage(final NiftyImage image) {
        draggedImage.getRenderer(ImageRenderer.class).setImage(image);
        backgroundImage.getRenderer(ImageRenderer.class).setImage(image);
        if (image != null) {
            
            int width = image.getWidth();
            int height = image.getHeight();
            if (width > getWidth()) {
                height = height * (width / getWidth());
                width = getWidth();
            }
            if (height > getHeight()) {
                width = width * (height / getHeight());
                height = getHeight();
            }
            
            final SizeValue widthSize = new SizeValue(Integer.toString(width).concat("px"));
            final SizeValue heightSize = new SizeValue(Integer.toString(height).concat("px"));
            
            draggable.setConstraintHeight(heightSize);
            draggable.setConstraintWidth(widthSize);
            backgroundImage.setVisible(true);
            backgroundImage.setConstraintHeight(heightSize);
            backgroundImage.setConstraintWidth(widthSize);
            draggedImage.setVisible(false);
            draggable.getControl(DraggableControl.class).setEnabled(true);
            getElement().layoutElements();
        } else {
            backgroundImage.setVisible(false);
            backgroundImageLabel.setVisible(false);
            draggedImage.setVisible(false);
            draggable.getControl(DraggableControl.class).setEnabled(false);
        }
    }

    /**
     * Set the visibility value of the dragged image.
     * 
     * @param value the visibility value of the image
     */
    protected void setVisibleOfDraggedImage(final boolean value) {
        draggedImage.setVisible(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showLabel() {
        backgroundImageLabel.show();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hideLabel() {
        backgroundImageLabel.hide();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLabelText(final String text) {
        backgroundImageLabel.getNiftyControl(Label.class).setText(text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void retrieveDraggable() {
        draggable.markForMove(droppable, new EndNotify() {
            @Override
            public void perform() {
                draggedImage.setVisible(false);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStartScreen() {
        retrieveDraggable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean inputEvent(final NiftyInputEvent inputEvent) {
        return false;
    }

}

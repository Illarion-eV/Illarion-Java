/*
 * This file is part of the Illarion Nifty-GUI Controls.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Nifty-GUI Controls is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Nifty-GUI Controls is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Nifty-GUI Controls.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.nifty.controls.inventoryslot;

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
import org.apache.log4j.Logger;
import org.bushe.swing.event.EventTopicSubscriber;
import org.illarion.nifty.controls.InventorySlot;

import javax.annotation.Nonnull;
import java.util.Properties;

/**
 * The control class of the inventory slot.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @deprecated Don't refer to this class in any application. Rather use the
 *             general interface of the inventory slot:
 *             {@link InventorySlot}
 */
@Deprecated
public class InventorySlotControl extends AbstractController implements InventorySlot {
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
     * The image that is always displayed in the background.
     */
    private Element staticBackgroundImage;

    /**
     * The element items can get dropped into.
     */
    private Element droppable;

    /**
     * The merchant overlay icon.
     */
    private Element merchantOverlay;

    /**
     * This value stores if the label should be visible or not. This is needed to restore the visibility upon request.
     */
    private boolean labelVisible;

    /**
     * The event subscriber that is used to monitor the start dragging events.
     */
    private EventTopicSubscriber<DraggableDragStartedEvent> dragStartEvent;

    /**
     * The event subscriber that is used to monitor the stop dragging events.
     */
    private EventTopicSubscriber<DraggableDragCanceledEvent> dragCanceledEvent;

    private Screen screen;
    private Nifty nifty;

    /**
     * The logger that displays all logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(InventorySlotControl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void bind(@Nonnull final Nifty nifty, final Screen screen, @Nonnull final Element element, final Properties parameter,
                     @Nonnull final Attributes controlDefinitionAttributes) {
        bind(element);

        this.screen = screen;
        this.nifty = nifty;

        droppable = element.findElementByName("#droppable");
        draggable = droppable.findElementByName("#draggable");
        draggedImage = draggable.findElementByName("#draggableImage");
        backgroundImage = element.findElementByName("#backgroundImage");
        backgroundImageLabel = element.findElementByName("#backgroundImageLabel");
        staticBackgroundImage = element.findElementByName("#staticBackgroundImage");
        merchantOverlay = element.findElementByName("#merchantOverlay");

        dragStartEvent = new EventTopicSubscriber<DraggableDragStartedEvent>() {
            @Override
            public void onEvent(final String topic, final DraggableDragStartedEvent data) {
                setVisibleOfDraggedImage(true);
            }
        };

        dragCanceledEvent = new EventTopicSubscriber<DraggableDragCanceledEvent>() {
            @Override
            public void onEvent(final String topic, final DraggableDragCanceledEvent data) {
                setVisibleOfDraggedImage(false);
            }
        };

        final String background = controlDefinitionAttributes.get("background");

        if (background != null) {
            setBackgroundImage(nifty.createImage(background, false));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setImage(@Nonnull final NiftyImage image) {
        draggedImage.getRenderer(ImageRenderer.class).setImage(image);
        backgroundImage.getRenderer(ImageRenderer.class).setImage(image);

        if (image != null) {
            float width = image.getWidth();
            float height = image.getHeight();
            if (width > getWidth()) {
                height *= (float) getWidth() / width;
                width = getWidth();
            }
            if (height > getHeight()) {
                width *= (float) getHeight() / height;
                height = getHeight();
            }

            final SizeValue widthSize = SizeValue.px((int) width);
            final SizeValue heightSize = SizeValue.px((int) height);

            draggable.setConstraintHeight(SizeValue.px(getHeight()));
            draggable.setConstraintWidth(SizeValue.px(getWidth()));
            draggedImage.setConstraintHeight(heightSize);
            draggedImage.setConstraintWidth(widthSize);
            backgroundImage.setVisible(true);
            backgroundImage.setConstraintHeight(heightSize);
            backgroundImage.setConstraintWidth(widthSize);
            draggedImage.setVisible(false);
            draggable.enable();
            getElement().layoutElements();
        } else {
            backgroundImage.setVisible(false);
            backgroundImageLabel.setVisible(false);
            draggedImage.setVisible(false);
            draggable.getControl(DraggableControl.class).setEnabled(false);
            getElement().layoutElements();
        }
    }

    @Override
    public void setBackgroundImage(@Nonnull final NiftyImage image) {
        if (image == null) {
            staticBackgroundImage.getRenderer(ImageRenderer.class).setImage(null);
            staticBackgroundImage.setVisible(false);
        } else {
            staticBackgroundImage.getRenderer(ImageRenderer.class).setImage(image);
            staticBackgroundImage.setVisible(true);
        }
    }

    /**
     * Set the visibility value of the dragged image.
     *
     * @param value the visibility value of the image
     */
    protected void setVisibleOfDraggedImage(final boolean value) {
        if (draggedImage.isVisible() != value) {
            draggedImage.setVisible(value);
            draggedImage.getParent().layoutElements();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showLabel() {
        if (!backgroundImageLabel.isVisible()) {
            backgroundImageLabel.show();
        }
        labelVisible = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hideLabel() {
        if (backgroundImageLabel.isVisible()) {
            backgroundImageLabel.hide();
        }
        labelVisible = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLabelText(@Nonnull final String text) {
        backgroundImageLabel.getNiftyControl(Label.class).setText(text);
        if (backgroundImageLabel.isVisible()) {
            backgroundImageLabel.getParent().layoutElements();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void retrieveDraggable() {
        if (draggable.getParent() == droppable) {
            draggable.hide();
            return;
        }

        draggable.markForMove(droppable, new EndNotify() {
            @Override
            public void perform() {
                draggedImage.hide();
            }
        });
    }

    @Override
    public void restoreVisibility() {
        if (labelVisible) {
            showLabel();
        } else {
            hideLabel();
        }
    }

    @Override
    public void hideMerchantOverlay() {
        merchantOverlay.hideWithoutEffect();
    }

    @Override
    public void showMerchantOverlay(@Nonnull final InventorySlot.MerchantBuyLevel level) {
        switch (level) {
            case Copper:
                merchantOverlay.getRenderer(ImageRenderer.class).setImage(
                        nifty.createImage("data/gui/coin_1_c.png", false));
                break;
            case Silver:
                merchantOverlay.getRenderer(ImageRenderer.class).setImage(
                        nifty.createImage("data/gui/coin_1_s.png", false));
                break;
            case Gold:
                merchantOverlay.getRenderer(ImageRenderer.class).setImage(
                        nifty.createImage("data/gui/coin_1_g.png", false));
                break;
        }
        merchantOverlay.showWithoutEffects();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStartScreen() {
        nifty.subscribe(screen, draggable.getId(), DraggableDragStartedEvent.class, dragStartEvent);
        nifty.subscribe(screen, draggable.getId(), DraggableDragCanceledEvent.class, dragCanceledEvent);

        retrieveDraggable();
        restoreVisibility();
        hideMerchantOverlay();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean inputEvent(final NiftyInputEvent inputEvent) {
        return true;
    }
}

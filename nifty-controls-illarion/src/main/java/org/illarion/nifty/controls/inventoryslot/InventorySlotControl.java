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
import de.lessvoid.nifty.controls.*;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.SizeValue;
import org.bushe.swing.event.EventTopicSubscriber;
import org.illarion.nifty.controls.InventorySlot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The control class of the inventory slot.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @deprecated Don't refer to this class in any application. Rather use the
 * general interface of the inventory slot:
 * {@link InventorySlot}
 */
@Deprecated
public class InventorySlotControl extends AbstractController implements InventorySlot {
    /**
     * The image that is dragged around.
     */
    @Nullable
    private Element draggedImage;

    /**
     * The actual draggable control.
     */
    @Nullable
    private Element draggable;

    /**
     * The element that shows the image of the object in the inventory slot.
     */
    @Nullable
    private Element backgroundImage;

    /**
     * The label for the image in the background.
     */
    @Nullable
    private Element backgroundImageLabel;

    /**
     * The image that is always displayed in the background.
     */
    @Nullable
    private Element staticBackgroundImage;

    /**
     * The element items can get dropped into.
     */
    @Nullable
    private Element droppable;

    /**
     * The merchant overlay icon.
     */
    @Nullable
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
    @SuppressWarnings("deprecation")
    private static final Logger LOGGER = LoggerFactory.getLogger(InventorySlotControl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void bind(
            @Nonnull final Nifty nifty,
            @Nonnull final Screen screen,
            @Nonnull final Element element,
            @Nonnull final Parameters parameter) {
        bind(element);

        this.screen = screen;
        this.nifty = nifty;

        droppable = element.findElementById("#droppable");
        draggable = droppable.findElementById("#draggable");
        draggedImage = draggable.findElementById("#draggableImage");
        backgroundImage = element.findElementById("#backgroundImage");
        backgroundImageLabel = element.findElementById("#backgroundImageLabel");
        staticBackgroundImage = element.findElementById("#staticBackgroundImage");
        merchantOverlay = element.findElementById("#merchantOverlay");

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

        final String background = parameter.get("background");

        if (background != null) {
            setBackgroundImage(nifty.createImage(background, false));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setImage(@Nullable final NiftyImage image) {
        final NiftyImage oldImage = draggedImage.getRenderer(ImageRenderer.class).getImage();

        if (oldImage == image) {
            return;
        }

        draggedImage.getRenderer(ImageRenderer.class).setImage(image);
        backgroundImage.getRenderer(ImageRenderer.class).setImage(image);

        if (oldImage != null) {
            oldImage.dispose();
        }

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
            draggable.setVisible(true);
            draggedImage.setVisible(screen.isActivePopup(draggable));
            draggable.enable();
        } else {
            backgroundImage.setVisible(false);
            backgroundImageLabel.setVisible(false);
            draggedImage.setVisible(false);
            draggable.setVisible(false);
            draggable.getNiftyControl(Draggable.class).disable(true);
            hideLabel();
            hideMerchantOverlay();
        }
    }

    @Override
    public void setBackgroundImage(@Nullable final NiftyImage image) {
        final NiftyImage oldImage = staticBackgroundImage.getRenderer(ImageRenderer.class).getImage();
        if (oldImage != null) {
            oldImage.dispose();
        }

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
            if (value) {
                draggedImage.show(new EndNotify() {
                    @Override
                    public void perform() {
                        draggable.getParent().layoutElements();
                    }
                });
            } else {
                draggedImage.hide();
            }
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
            draggedImage.hide();
            return;
        }

        if (screen.isActivePopup(draggable)) {
            LOGGER.error("Trying to retrieve a draggable that is currently dragged!");
            return;
        }

        draggable.markForMove(droppable, new EndNotify() {
            @Override
            public void perform() {
                draggable.getNiftyControl(Draggable.class).setDroppable(droppable.getNiftyControl(Droppable.class));
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
        if (merchantOverlay.getRenderer(ImageRenderer.class).getImage() == null) {
            merchantOverlay.hideWithoutEffect();
        } else {
            merchantOverlay.showWithoutEffects();
        }
    }

    @Override
    public void hideMerchantOverlay() {
        merchantOverlay.getRenderer(ImageRenderer.class).setImage(null);
        merchantOverlay.hideWithoutEffect();
    }

    @Override
    public void showMerchantOverlay(@Nonnull final InventorySlot.MerchantBuyLevel level) {
        switch (level) {
            case Copper:
                merchantOverlay.getRenderer(ImageRenderer.class).setImage(nifty.createImage("gui/coin_1_c.png", false));
                break;
            case Silver:
                merchantOverlay.getRenderer(ImageRenderer.class).setImage(nifty.createImage("gui/coin_1_s.png", false));
                break;
            case Gold:
                merchantOverlay.getRenderer(ImageRenderer.class).setImage(nifty.createImage("gui/coin_1_g.png", false));
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
    public boolean inputEvent(@Nonnull final NiftyInputEvent inputEvent) {
        return true;
    }
}

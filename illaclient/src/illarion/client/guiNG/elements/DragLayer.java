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

import java.awt.Shape;

import illarion.client.guiNG.GUI;

import illarion.input.MouseEvent;

/**
 * A drag layer is a invisible widget that fetches dragging events and is able
 * to move some other widgets based on the drag event.
 * <p>
 * It is possible that this element drags its parent or any other element by
 * choice.
 * </p>
 * <p>
 * The shape that is listened for the drag events is either the shape of
 * DragLayer widget, the shape of its children or a individual
 * {@link java.awt.Shape}.
 * </p>
 * 
 * @author Martin Karing
 * @since 1.22
 */
public class DragLayer extends Widget {
    /**
     * The shape source constant for using the shape of the children of the
     * widget to listen for dragging events.
     */
    public static final int SHAPE_CHILDREN = 1;

    /**
     * The shape source constant for using the shape of this widget to listen
     * for dragging events.
     */
    public static final int SHAPE_WIDGET = 0;

    /**
     * The serialization UID for this widget.
     */
    private static final long serialVersionUID = 2L;

    /**
     * The shape source constant for using a {@link java.awt.Shape} to listen
     * for dragging events.
     */
    private static final int SHAPE_AWT = 2;

    /**
     * The {@link java.awt.Shape} that is used to listen to drag events. This is
     * only used in case {@link #shapeSource} is set to {@link #SHAPE_AWT}.
     */
    private Shape awtShape;

    /**
     * In case this variable is set to true it brings the dragged parent into
     * the front of the screen when a dragging event is started.
     */
    private boolean bringToFront = false;

    /**
     * The flag that disables his dragging layer.
     */
    private boolean disabled = false;

    /**
     * The flag if currently a dragging event is going on or not.
     */
    private transient boolean dragging = false;

    /**
     * The target of the dragging event.
     */
    private Widget dragTarget;

    /**
     * The last x coordinate fetched by this layer. This is used to calculate
     * the offset of the new location so the dragging is done correctly.
     */
    private transient int lastX = 0;

    /**
     * The last y coordinate fetched by this layer. This is used to calculate
     * the offset of the new location so the dragging is done correctly.
     */
    private transient int lastY = 0;

    /**
     * The shape source that is used to listen to the dragging events. All
     * dragging events outside this shape are ignored.
     */
    private int shapeSource = SHAPE_WIDGET;

    /**
     * Disable the dragging of this layer. This causes that the layer does not
     * fetch any dragging events anymore.
     */
    public void disableDragging() {
        disabled = true;
    }

    /**
     * Check if the dragging of this layer is enabled.
     * 
     * @return <code>true</code> in case this layer fetches and handles dragging
     *         events
     */
    public boolean draggingEnabled() {
        return !disabled;
    }

    /**
     * Enabled the dragging of this layer. This causes that the layer listens to
     * dragging events and forwards the movement to the dragging target.
     */
    public void enableDragging() {
        disabled = false;
    }

    /**
     * Handle a mouse event. This will check if a dragging event is reported and
     * in case its on the shape the dragging will be started.
     * 
     * @param event the mouse event that needs to be handled
     */
    @SuppressWarnings("nls")
    @Override
    public void handleMouseEvent(final MouseEvent event) {
        if (!isVisible()) {
            return;
        }

        if (!disabled && bringToFront && dragTarget.hasParent()
            && (event.getEvent() == MouseEvent.EVENT_KEY_CLICK)
            && (event.getKey() == java.awt.event.MouseEvent.BUTTON1)) {
            if (!dragTarget.getParent().isFrontChild(dragTarget)) {
                final Widget dragTargetParent = dragTarget.getParent();
                dragTargetParent.removeChild(dragTarget);
                dragTargetParent.addChild(dragTarget);
            }
        }

        if (disabled
            || (!dragging && (event.getEvent() != MouseEvent.EVENT_DRAG_START))) {
            GUI.getInstance().requestExclusiveMouse(null);
            dragging = false;
            super.handleMouseEvent(event);
            return;
        }

        if (dragging
            && ((event.getEvent() != MouseEvent.EVENT_DRAG_END) && (event
                .getEvent() != MouseEvent.EVENT_LOCATION))) {
            dragging = false;
            GUI.getInstance().requestExclusiveMouse(null);
            super.handleMouseEvent(event);
            return;
        }

        if (dragging) {
            final int offsetX = event.getPosX() - lastX;
            final int offsetY = event.getPosY() - lastY;
            lastX = event.getPosX();
            lastY = event.getPosY();
            dragTarget.setRelPos(dragTarget.getRelX() + offsetX,
                dragTarget.getRelY() + offsetY);

            if (event.getEvent() == MouseEvent.EVENT_DRAG_END) {
                dragging = false;
                GUI.getInstance().requestExclusiveMouse(null);
            }
            return;
        }

        if (event.getKey() != java.awt.event.MouseEvent.BUTTON1) {
            GUI.getInstance().requestExclusiveMouse(null);
            super.handleMouseEvent(event);
            return;
        }

        boolean startDrag = false;
        if (shapeSource == SHAPE_WIDGET) {
            startDrag = true;
        } else if (shapeSource == SHAPE_CHILDREN) {
            final int count = getChildrenCount();
            for (int i = 0; i < count; i++) {
                if (getChildByIndex(i).isInside(event.getPosX(),
                    event.getPosY())) {
                    startDrag = true;
                    break;
                }
            }
        } else if (shapeSource == SHAPE_AWT) {
            final int relMouseX = event.getPosX() - getAbsX();
            final int relMouseY = event.getPosY() - getAbsY();
            if (awtShape.contains(relMouseX, relMouseY)) {
                startDrag = true;
            }
        } else {
            throw new IllegalStateException("Invalid shape source.");
        }

        if (!startDrag) {
            GUI.getInstance().requestExclusiveMouse(null);
            super.handleMouseEvent(event);
            return;
        }

        dragging = true;
        lastX = event.getPosX();
        lastY = event.getPosY();
        GUI.getInstance().requestExclusiveMouse(this);

        if (bringToFront && dragTarget.hasParent()) {
            if (!dragTarget.getParent().isFrontChild(dragTarget)) {
                final Widget dragTargetParent = dragTarget.getParent();
                dragTargetParent.removeChild(dragTarget);
                dragTargetParent.addChild(dragTarget);
            }
        }
    }

    /**
     * Set a AWT shape that that is checked to determine if the dragging event
     * shall be received by the drag layer or not. This will automatically
     * activate the usage of this shape.
     * 
     * @param newShape the AWT shape that is used to determine the activate area
     *            of this widget, all locations are relative to the DragLayer
     *            widget location
     */
    public void setAWTShape(final Shape newShape) {
        shapeSource = SHAPE_AWT;
        awtShape = newShape;
    }

    /**
     * Set if the drag layer shall bring its parent to the front upon dragging
     * or not.
     * 
     * @param newBringToFront true in case this drag layer shall bring its
     *            parent to the front
     */
    public void setBringToFront(final boolean newBringToFront) {
        bringToFront = newBringToFront;
    }

    /**
     * Set the dragging target of this drag layer. The dragging target will be
     * moved around in case a dragging event is received.
     * 
     * @param target the dragging target or <code>null</code> what means the
     *            parent of this layer
     */
    public void setDragTarget(final Widget target) {
        if (target == null) {
            dragTarget = getParent();
        } else {
            dragTarget = target;
        }
    }

    /**
     * Set the source of the shape that is supposed to be used to determine the
     * active area of this widget.
     * 
     * @param newSource the source shape that shall be used from now on
     * @see #SHAPE_WIDGET
     * @see #SHAPE_CHILDREN
     */
    public void setShapeSource(final int newSource) {
        awtShape = null;
        shapeSource = newSource;
    }
}

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
import illarion.client.graphics.AnimationUtility;
import illarion.client.guiNG.GUI;

import illarion.common.util.FastMath;

import illarion.graphics.RenderDisplay;

import illarion.input.MouseEvent;

/**
 * This widget is a area that extends the actual visible size of a widget and
 * allows the rest of the area to be scrolled.
 * <p>
 * The current state of the component:
 * <ul>
 * <li>Basic scrolling is working</li>
 * <li>Throwing feature is <em>not</em> working</li>
 * <li>Scrollbars are <em>not</em> implemented yet</li>
 * </ul>
 * </p>
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
// TODO: Throwing feature
// TODO: Scrollbars
public class ScrollArea extends Widget {
    /**
     * This constant is supposed to be used to be set as display value for the
     * scrollbars. In this case the scrollbar will be always displayed.
     */
    public static final int SCROLLBAR_ALWAYS = 1;

    /**
     * This constant is supposed to be used to be set as display value for the
     * scrollbars. In this case the scrollbar will be displayed when needed, so
     * only in case the viewport is smaller then the actual size.
     */
    public static final int SCROLLBAR_AS_NEEDED = 2;

    /**
     * This constant is supposed to be used to be set as display value for the
     * scrollbars. In this case the scrollbar will be never displayed.
     */
    public static final int SCROLLBAR_NEVER = 0;

    /**
     * The serialization UID of this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The X offset that is the target. This value will be approached slowly.
     * This way smooth scrolling operations are realized.
     */
    private int approachOffsetX;

    /**
     * The Y offset that is the target. This value will be approached slowly.
     * This way smooth scrolling operations are realized.
     */
    private int approachOffsetY;

    /**
     * This variable stores if there is currently a dragging event going on.
     */
    private boolean currentlyDragging = false;

    /**
     * Enable moving the scroll area around by dragging the pane.
     */
    private boolean enableDrag;

    /**
     * Enable that the moving speed slows down slowly when dragging the area
     * around and releasing the mouse while in movement.
     */
    private boolean enableSmoothSlowdown;

    /**
     * This variable stores the last X location that was fetched during a
     * dragging event.
     */
    private int lastDragX = 0;

    /**
     * This variable stores the last Y location that was fetched during a
     * dragging event.
     */
    private int lastDragY = 0;

    /**
     * This variable stores the last movement speed along the X coordinate value
     * of the last dragging operation. This is used to calculate the smooth
     * slowdown.
     */
    private int lastSpeedX = 0;

    /**
     * This variable stores the last movement speed along the Y coordinate value
     * of the last dragging operation. This is used to calculate the smooth
     * slowdown.
     */
    private int lastSpeedY = 0;

    /**
     * The virtual height of the component. This is the actual size of the
     * component where the child components are placed on.
     */
    private int virtualHeight;

    /**
     * The X offset of the virtual area relative to the actual area.
     */
    private int virtualOffsetX;

    /**
     * The Y offset of the virtual area relative to the actual area.
     */
    private int virtualOffsetY;

    /**
     * The virtual width of the component. This is the actual size of the
     * component where the child components are placed on.
     */
    private int virtualWidth;

    /**
     * This function is called when drawing the GUI. The components need to
     * overwrite this function and draw everything <b>before</b> calling the
     * super function.
     * 
     * @param delta the time since the render function was called last time
     */
    @Override
    public void draw(final int delta) {
        if (!isVisible()) {
            return;
        }

        if ((approachOffsetX != virtualOffsetX)
            || (approachOffsetY != virtualOffsetY)) {
            applyOffset(AnimationUtility.approach(virtualOffsetX,
                approachOffsetX, Integer.MIN_VALUE, Integer.MAX_VALUE, delta),
                AnimationUtility.approach(virtualOffsetY, approachOffsetY,
                    Integer.MIN_VALUE, Integer.MAX_VALUE, delta));
        }

        final RenderDisplay display =
            ClientWindow.getInstance().getRenderDisplay();
        if (hasChildren()) {
            display.setAreaLimit(super.getAbsX(), super.getAbsY(),
                super.getWidth(), super.getHeight());

            final int childrenCount = getChildrenCount();
            for (int i = 0; i < childrenCount; ++i) {
                getChildByIndex(i).draw(delta);
            }

            display.unsetAreaLimit();
        }
    }

    /**
     * Overwritten absolute position function that shifts the reported absolute
     * location by the offset.
     * 
     * @return the absolute x position corrected by the offset
     */
    @Override
    public int getAbsX() {
        return super.getAbsX() + virtualOffsetX;
    }

    /**
     * Overwritten absolute position function that shifts the reported absolute
     * location by the offset.
     * 
     * @return the absolute y position corrected by the offset
     */
    @Override
    public int getAbsY() {
        return super.getAbsY() + virtualOffsetY;
    }

    /**
     * The height of this component. This function actually returns the size of
     * the area where all child elements can be placed on. So this is actually
     * the virtual size of the component and not the size of the actual
     * viewport.
     * 
     * @return the height of the virtual area of this widget
     */
    @Override
    public int getHeight() {
        return virtualHeight;
    }

    /**
     * Get the current offset along the X axis of the viewport.
     * 
     * @return the current X offset of the viewport
     */
    public int getScrollOffsetX() {
        return virtualOffsetX;
    }

    /**
     * Get the current offset along the Y axis of the viewport.
     * 
     * @return the current Y offset of the viewport
     */
    public int getScrollOffsetY() {
        return virtualOffsetY;
    }

    /**
     * Get the height of the area that is actually visible.
     * 
     * @return the height of the visible viewport
     */
    public int getViewportHeight() {
        return super.getHeight();
    }

    /**
     * Get the width of the area that is actually visible.
     * 
     * @return the width of the visible viewport
     */
    public int getViewportWidth() {
        return super.getWidth();
    }

    /**
     * Get the currently set virtual height.
     * 
     * @return the virtual height
     */
    public int getVirtualHeight() {
        return virtualHeight;
    }

    /**
     * Get the currently set virtual width.
     * 
     * @return the virtual width
     */
    public int getVirtualWidth() {
        return virtualWidth;
    }

    /**
     * The width of this component. This function actually returns the size of
     * the area where all child elements can be placed on. So this is actually
     * the virtual size of the component and not the size of the actual
     * viewport.
     * 
     * @return the width of the virtual area of this widget
     */
    @Override
    public int getWidth() {
        return virtualWidth;
    }

    /**
     * Handle a mouse event. This fetches the dragging events in case the
     * dragging feature is enabled and forwards all unused events to the parent.
     * 
     * @param event the mouse event that needs to be handled
     */
    @Override
    public void handleMouseEvent(final MouseEvent event) {
        if (enableDrag) {
            final int eventID = event.getEvent();
            if ((eventID == MouseEvent.EVENT_DRAG_START)
                && (event.getKey() == MouseEvent.BUTTON1)) {
                currentlyDragging = true;
                GUI.getInstance().requestExclusiveMouse(this);
                lastDragX = event.getPosX();
                lastDragY = event.getPosY();
                lastSpeedX = 0;
                lastSpeedY = 0;
                return;
            }

            if (!currentlyDragging) {
                return;
            }

            if ((eventID == MouseEvent.EVENT_LOCATION)
                || (eventID == MouseEvent.EVENT_DRAG_END)) {
                final int newPosX = event.getPosX();
                final int newPosY = event.getPosY();
                lastSpeedX = newPosX - lastDragX;
                lastSpeedY = newPosY - lastDragY;
                lastDragX = newPosX;
                lastDragY = newPosY;

                setScrollOffset(virtualOffsetX + lastSpeedX, virtualOffsetY
                    + lastSpeedY);

                if (eventID == MouseEvent.EVENT_DRAG_END) {
                    currentlyDragging = false;
                    GUI.getInstance().requestExclusiveMouse(null);

                    if (enableSmoothSlowdown) {
                        setScrollOffset(virtualOffsetX + lastSpeedX,
                            virtualOffsetY + lastSpeedY, true);
                    }
                }
                return;
            }
        }

        super.handleMouseEvent(event);
    }

    /**
     * Recalculate the layout variables of this widget and all its children.
     * This needs to be done before using any absolute location. But the needed
     * getter function for this should do it automatically.
     */
    @Override
    public final void refreshLayout() {
        if (!isLayoutDirty()) {
            return;
        }

        if (hasParent()) {
            setAbsPos(getParent().getAbsX() + getRelX(), getParent().getAbsY()
                + getRelY(), true);
        } else {
            setAbsPos(getRelX(), getRelY(), true);
        }
        cleanedLayout();

        if (hasChildren()) {
            final int childrenCount = getChildrenCount();
            for (int i = 0; i < childrenCount; ++i) {
                getChildByIndex(i).refreshLayout();
            }
        }
    }

    /**
     * Enable or disable the dragging feature. This feature allows to drag the
     * scroll area around by grabbing the area with the mouse and move it
     * around.
     * 
     * @param enable <code>true</code> to enable the dragging feature
     */
    public void setDrag(final boolean enable) {
        enableDrag = enable;
    }

    /**
     * Set the display mode of the horizontal scrollbar.
     * 
     * @param newVis the new visibility value of the scrollbar, valid values are
     *            only {@link #SCROLLBAR_ALWAYS}, {@link #SCROLLBAR_AS_NEEDED},
     *            {@link #SCROLLBAR_NEVER}
     */
    @SuppressWarnings("nls")
    public void setHorizontalScrollbarVisibility(final int newVis) {
        if ((newVis != SCROLLBAR_NEVER) && (newVis != SCROLLBAR_ALWAYS)
            && (newVis != SCROLLBAR_AS_NEEDED)) {
            throw new IllegalArgumentException(
                "Illegal value for horizontal scrollbar visibility");
        }
    }

    /**
     * Change the current scroll offset. This causes that the scroll area moves
     * to a new location. This function automatically corrects the offset
     * values. The change of the offset is not animated smoothly when using this
     * function.
     * 
     * @param x the new X offset
     * @param y the new Y offset
     */
    public void setScrollOffset(final int x, final int y) {
        setScrollOffset(x, y, false);
    }

    /**
     * Change the current scroll offset. This causes that the scroll area moves
     * to a new location. This function automatically corrects the offset
     * values.
     * 
     * @param x the new X offset
     * @param y the new Y offset
     * @param smooth <code>true</code> to animate the change of the offset
     *            smoothly, else its just set to the new value
     */
    public void setScrollOffset(final int x, final int y, final boolean smooth) {
        int fixedX;
        int fixedY;
        final int realWidth = getViewportWidth();
        final int realHeight = getViewportHeight();
        if (virtualWidth > realWidth) {
            fixedX = FastMath.clamp(x, realWidth - virtualWidth, 0);
        } else {
            fixedX = 0;
        }

        if (virtualHeight > realHeight) {
            fixedY = FastMath.clamp(y, realHeight - virtualHeight, 0);
        } else {
            fixedY = 0;
        }

        if (smooth) {
            approachOffsetX = fixedX;
            approachOffsetY = fixedY;
        } else {
            applyOffset(fixedX, fixedY);
            approachOffsetX = fixedX;
            approachOffsetY = fixedY;
        }
    }

    /**
     * Enable or disable the smooth slow down feature. This only applies in case
     * the dragging feature is enabled as well ({@link #setDrag(boolean)}). In
     * case this is enabled the movement will slow down slowly when the area is
     * dragged and the mouse is released during movement.
     * 
     * @param enable <code>true</code> to enable this slow down feature
     */
    public void setSmoothSlowdown(final boolean enable) {
        enableSmoothSlowdown = enable;
    }

    /**
     * Set the display mode of the vertical scrollbar.
     * 
     * @param newVis the new visibility value of the scrollbar, valid values are
     *            only {@link #SCROLLBAR_ALWAYS}, {@link #SCROLLBAR_AS_NEEDED},
     *            {@link #SCROLLBAR_NEVER}
     */
    @SuppressWarnings("nls")
    public void setVerticalScrollbarVisibility(final int newVis) {
        if ((newVis != SCROLLBAR_NEVER) && (newVis != SCROLLBAR_ALWAYS)
            && (newVis != SCROLLBAR_AS_NEEDED)) {
            throw new IllegalArgumentException(
                "Illegal value for vertical scrollbar visibility");
        }
    }

    /**
     * This function sets the height of the viewport of this widget. After all
     * its just a alias for the {@link #setHeight(int)} function.
     * 
     * @param height the new height of the widget viewport
     */
    public void setViewportHeight(final int height) {
        super.setHeight(height);
    }

    /**
     * Set the size of the viewport of this widget. After all its just a alias
     * for the {@link #setSize(int, int)} function.
     * 
     * @param width the new width of the widget viewport
     * @param height the new height of the widget viewport
     */
    public void setViewportSize(final int width, final int height) {
        super.setSize(width, height);
    }

    /**
     * This function sets the width of the viewport of this widget. After all
     * its just a alias for the {@link #setWidth(int)} function.
     * 
     * @param width the new width of the widget viewport
     */
    public void setViewportWidth(final int width) {
        super.setWidth(width);
    }

    /**
     * Set the virtual height of this scroll area. In case this height is larger
     * then the viewport height the area can be scrolled vertical.
     * 
     * @param height the new height value for the virtual area
     */
    public void setVirtualHeight(final int height) {
        virtualHeight = height;
    }

    /**
     * Set the virtual size of this scroll area. Means the area that is larger
     * then the actual widget and scrolled around then. Its possible that the
     * scrollbars, in case they are displayed, are subtracted from this area.
     * 
     * @param width the virtual width of this scroll area
     * @param height the virtual height of this scroll area
     */
    public void setVirtualSize(final int width, final int height) {
        virtualWidth = width;
        virtualHeight = height;
    }

    /**
     * Set the virtual width of this scroll area. In case this width is larger
     * then the viewport width the area can be scrolled horizontal.
     * 
     * @param width the new width value for the virtual area
     */
    public void setVirtualWidth(final int width) {
        virtualWidth = width;
    }

    /**
     * This function applies a offset to the scroll area. Applying the offset
     * changes the current relative location of the scroll area and the offset
     * of the viewport.
     * 
     * @param x the new x coordinate of the offset
     * @param y the new y coordinate of the offset
     */
    private void applyOffset(final int x, final int y) {
        virtualOffsetX = x;
        virtualOffsetY = y;
        layoutInvalid();
    }
}

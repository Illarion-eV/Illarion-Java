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

import java.io.Serializable;
import java.util.ConcurrentModificationException;
import java.util.List;

import javolution.util.FastTable;

import illarion.client.ClientWindow;
import illarion.client.guiNG.init.WidgetInit;
import illarion.client.guiNG.messages.Message;
import illarion.client.util.Lang;

import illarion.common.util.FastMath;

import illarion.input.KeyboardEvent;
import illarion.input.MouseEvent;

/**
 * The widget is the absolutely basic object in the environment of the GUI. Its
 * the parent of just everything that is located anywhere on the GUI. A widget
 * itself is not in all cases something visible. Its just a object with a parent
 * and some children.
 * <p>
 * The root widget, so the widget without any parent, would be the desktop of
 * the client screen where the map should be rendered upon.
 * </p>
 * Since the widgets are able to be stored by serialization it need to be
 * ensured that the stored variables and references are transient or absolutely
 * needed for the serialization. Any useless serialized variables increase the
 * file size of the GUI for nothing.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public class Widget implements Serializable {
    /**
     * Current serialization UID of the widget class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * This constant holds the format constant that is used to build the return
     * on any call of the {@link #toString()} function.
     */
    private static final String TO_STRING_FORMAT =
        "Widget(x=%1$s; y=%2$s; w=%3$s; h=%4$s)"; //$NON-NLS-1$

    /**
     * This variable stores the x coordinate of the absolute location of the
     * widget on the screen. It needs to be recalculated in case one of the
     * parent widgets change in any way.
     */
    private transient int absPosX = 0;

    /**
     * This variable stores the y coordinate of the absolute location of the
     * widget on the screen. It needs to be recalculated in case one of the
     * parent widgets change in any way.
     */
    private transient int absPosY = 0;

    /**
     * This array list stores all children of the widget once one is added.
     */
    private List<Widget> children = null;

    /**
     * This variable stores if the layout of the widget is up to date or if it
     * needs some recalculation.
     */
    private transient boolean dirtyLayout = true;

    /**
     * The height of the widget.
     */
    private int height = 0;

    /**
     * The object that takes care for loading the widget correctly after its
     * loaded from the serialized output.
     */
    private WidgetInit initScript;

    /**
     * The parent widget. In case this one is <code>null</code> this widget is
     * the root widget.
     */
    private Widget parent = null;

    /**
     * The x location of the widget. The origin this position is relative to is
     * always the origin of its parent.
     */
    private int posX = 0;

    /**
     * The y location of the widget. The origin this position is relative to is
     * always the origin of its parent.
     */
    private int posY = 0;

    /**
     * The visible flag. In case this widget is visible and is able to fetch
     * mouse clicks it is <code>true</code>.
     */
    private boolean visible = true;

    /**
     * The width of the widget.
     */
    private int width = 0;

    /**
     * Add a child to the widget at the end of the children list. This means
     * that the added children will be on top of all other children of this
     * widget and will fetch the focus first.
     * 
     * @param newChild the child that shall be added to the widget as top child
     */
    public void addChild(final Widget newChild) {
        insertChild(newChild, getChildrenCount());
    }

    /**
     * Cleanup the widget. This function is called by the GUI right before the
     * GUI is saved to a file. It gives the widget a chance to clean itself up
     * before its saved. Also widgets that are not supposed to be saved can be
     * cleaned with this.
     */
    public void cleanup() {
        if (children != null) {
            int childrenCount = children.size();
            int i = 0;
            while (i < childrenCount) {
                final Widget checkedChildren = children.get(i);
                checkedChildren.cleanup();
                if (!checkedChildren.hasParent()
                    || !checkedChildren.getParent().equals(this)) {
                    --childrenCount;
                } else {
                    ++i;
                }
            }
        }
    }

    /**
     * This function is called when drawing the GUI. The components need to
     * overwrite this function and draw everything <b>before</b> calling the
     * super function.
     * 
     * @param delta the time since the render function was called last time
     */
    public void draw(final int delta) {
        if (!isVisible()) {
            return;
        }
        if (hasChildren()) {
            ClientWindow.getInstance().getRenderDisplay()
                .setAreaLimit(getAbsX(), getAbsY(), getWidth(), getHeight());
            final int childrenCount = children.size();
            for (int i = 0; i < childrenCount; ++i) {
                children.get(i).draw(delta);
            }
            ClientWindow.getInstance().getRenderDisplay().unsetAreaLimit();
        }
    }

    /**
     * Get the y coordinate of the bottom border of this widget, relative to
     * 0,0.
     * 
     * @return the y coordinate of the bottom border, the result of
     *         {@link #getAbsY()} + {@link #getHeight()}
     * @see #getAbsY()
     * @see #getHeight()
     */
    public int getAbsBottom() {
        return getAbsY() + getHeight();
    }

    /**
     * Get the x coordinate of the right border of this widget, relative to 0,0.
     * 
     * @return the x coordinate of the right border, the result of
     *         {@link #getAbsX()} + {@link #getWidth()}
     * @see #getAbsX()
     * @see #getWidth()
     */
    public int getAbsRight() {
        return getAbsX() + getWidth();
    }

    /**
     * Get the absolute x coordinate of this widget. This is always relative to
     * the 0, 0.
     * <p>
     * <b>Important:</b> Use this function carefully, in case the layout is not
     * up to date it will trigger a recalculation instantly.
     * </p>
     * 
     * @return the absolute x coordinate of the widget
     */
    public int getAbsX() {
        refreshLayout();
        return absPosX;
    }

    /**
     * Get the absolute y coordinate of this widget. This is always relative to
     * the 0, 0.
     * <p>
     * <b>Important:</b> Use this function carefully, in case the layout is not
     * up to date it will trigger a recalculation instantly.
     * </p>
     * 
     * @return the absolute y coordinate of the widget
     */
    public int getAbsY() {
        refreshLayout();
        return absPosY;
    }

    /**
     * Get a child by its index.
     * 
     * @param index the index of the child
     * @return the child stored at this index
     */
    public final Widget getChildByIndex(final int index) {
        if (children != null) {
            return children.get(index);
        }

        return null;
    }

    /**
     * Get the index of a child in the children list of this widget.
     * 
     * @param child the child that shall be list in the children list
     * @return the index of the child or -1 in case it was not found
     */
    public final int getChildIndex(final Widget child) {
        if (children != null) {
            // indexOf uses equals, so its not usable in this case.
            final int childrenCount = children.size();
            for (int i = 0; i < childrenCount; ++i) {
                if (children.get(i) == child) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Get the amount of children of the widget.
     * 
     * @return the children count of this widget
     */
    public int getChildrenCount() {
        if (children == null) {
            return 0;
        }
        return children.size();
    }

    /**
     * Get the height of the widget.
     * 
     * @return the height of the widget
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get the parent widget of this widget.
     * 
     * @return the parent widget of this widget or <code>null</code> in case
     *         this one is the root widget or a widget that was not assigned
     *         yet.
     */
    public Widget getParent() {
        return parent;
    }

    /**
     * Get the x coordinate of the right border of this widget, relative to the
     * coordinates of its parent widget.
     * 
     * @return the x coordinate of the right border, the result of
     *         {@link #getRelX()} + {@link #getWidth()}
     * @see #getRelX()
     * @see #getWidth()
     */
    public int getRelRight() {
        return getRelX() + getWidth();
    }

    /**
     * Get the y coordinate of the top border of this widget, relative to the
     * coordinates of its parent widget.
     * 
     * @return the y coordinate of the top border, the result of
     *         {@link #getRelY()} + {@link #getHeight()}
     * @see #getRelY()
     * @see #getHeight()
     */
    public int getRelTop() {
        return getRelY() + getHeight();
    }

    /**
     * Get the relative x coordinate of this widget. Its always relative to the
     * origin of the parent widget.
     * 
     * @return the relative x coordinate of the widget
     */
    public int getRelX() {
        return posX;
    }

    /**
     * Get the relative y coordinate of this widget. Its always relative to the
     * origin of the parent widget.
     * 
     * @return the relative y coordinate of the widget
     */
    public int getRelY() {
        return posY;
    }

    /**
     * Get the widget at one location that is deepest in the tree and at the
     * highest layer. This function ensures that there is no other widget
     * overlaying the returned one.
     * <p>
     * Ensure that the widget is really within the location you hand over here.
     * Else its possible that a invalid result is returned.
     * </p>
     * 
     * @param checkX the x coordinate of the absolute location that shall be
     *            checked
     * @param checkY the y coordinate of the absolute location that shall be
     *            checked
     * @return the widget that was found at this location
     * @see #isInside(int, int)
     */
    public Widget getWidgetAt(final int checkX, final int checkY) {
        final Widget child = getChildAt(checkX, checkY);
        if (child != null) {
            return child.getWidgetAt(checkX, checkY);
        }
        return this;
    }

    /**
     * Get the width of the widget.
     * 
     * @return the width of the widget
     */
    public int getWidth() {
        return width;
    }

    /**
     * Handle a keyboard event. By default the event is just forwarded to all
     * children of this widget until the first children returns
     * <code>true</code> to indicate that the event is handled.
     * 
     * @param event the keyboard event to process
     * @return <code>true</code> in case the keyboard event is handled and and
     *         should not be forwarded to any other children
     */
    public boolean handleKeyboardEvent(final KeyboardEvent event) {
        if (children == null) {
            return false;
        }
        final int childrenCount = children.size();
        for (int i = 0; i < childrenCount; ++i) {
            if (children.get(i).handleKeyboardEvent(event)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handle a internal GUI message. The default implementation only forwards
     * the message to all its children.
     * 
     * @param msg the message to handle
     */
    public void handleMessage(final Message msg) {
        if (children == null) {
            return;
        }
        final int childrenCount = children.size();
        for (int i = 0; i < childrenCount; ++i) {
            children.get(i).handleMessage(msg);
        }
    }

    /**
     * Handle a mouse event. By default a widget is transparent to all mouse
     * events, so it forwards the events just to its parent. Overwrite this
     * function to change this behavior.
     * 
     * @param event the mouse event that needs to be handled
     */
    public void handleMouseEvent(final MouseEvent event) {
        if (parent != null) {
            parent.handleMouseEvent(event);
        }
    }

    /**
     * Check if this widget has any children.
     * 
     * @return <code>true</code> in case this widget has one or more children
     */
    public final boolean hasChildren() {
        return ((children != null) && !children.isEmpty());
    }

    /**
     * Check if this widget has any parent. In case it has no parent is either
     * not added to a widget tree or its a root widget.
     * 
     * @return <code>true</code> in case it has a parent
     */
    public final boolean hasParent() {
        return (parent != null);
    }

    /**
     * Walk over all the children of this widget and trigger the initialization.
     */
    public void initWidget() {
        dirtyLayout = true;
        if (initScript != null) {
            initScript.initWidget(this);
        }
        if (hasChildren()) {
            final int childrenCount = children.size();
            for (int i = 0; i < childrenCount; ++i) {
                children.get(i).initWidget();
            }
        }
    }

    /**
     * Add a child to the widget at a specified location of the list. The
     * location in the list determines the "layer" of the widget. The widget at
     * the last location in the list is the widget on the top.
     * 
     * @param newChild the widget that shall be added to the list
     * @param index the index within the list where the widget shall be added
     */
    @SuppressWarnings("nls")
    public void insertChild(final Widget newChild, final int index) {
        if (newChild == null) {
            throw new IllegalArgumentException("insert NULL child");
        }
        if (newChild == this) {
            throw new IllegalArgumentException("Can't add yourself");
        }
        if (newChild.hasParent()) {
            throw new IllegalArgumentException("Child has already a tree");
        }
        if (children == null) {
            children = FastTable.newInstance();
        }
        if ((index < 0) || (index > children.size())) {
            throw new IndexOutOfBoundsException();
        }
        newChild.parent = this;
        children.add(index, newChild);
        newChild.dirtyLayout = true;
    }

    /**
     * Check if a widget is the child of this widget that is displayed in the
     * front.
     * 
     * @param testWidget the widget that shall be checked
     * @return <code>true</code> in case this widget is at the highest level
     */
    public final boolean isFrontChild(final Widget testWidget) {
        if (children != null) {
            final int childrenCount = children.size();
            return testWidget.equals(children.get(childrenCount - 1));
        }
        return false;
    }

    /**
     * Check if any absolute location is within the area of this widget.
     * 
     * @param checkX the x coordinate of the absolute location that shall be
     *            checked
     * @param checkY the y coordinate of the absolute location that shall be
     *            checked
     * @return <code>true</code> in case the checked position is within the area
     *         of the widget
     */
    public boolean isInside(final int checkX, final int checkY) {
        refreshLayout();
        return (checkX >= absPosX) && (checkY >= absPosY)
            && (checkX < (absPosX + width)) && (checkY < (absPosY + height));
    }

    /**
     * Check is the widget is visible.
     * 
     * @return <code>true</code> in case the widget is visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Set the layout invalid.
     */
    public void layoutInvalid() {
        invalidateLayout();
    }

    /**
     * Recalculate the layout variables of this widget and all its children.
     * This needs to be done before using any absolute location. But the needed
     * getter function for this should do it automatically.
     */
    public void refreshLayout() {
        if (!isLayoutDirty()) {
            return;
        }

        if (hasParent()) {
            width = Math.min(getParent().getWidth(), width);
            height = Math.min(getParent().getHeight(), height);

            posX = FastMath.clamp(posX, 0, getParent().getWidth() - width);
            posY = FastMath.clamp(posY, 0, getParent().getHeight() - height);

            absPosX = parent.getAbsX() + posX;
            absPosY = parent.getAbsY() + posY;
        } else {
            absPosX = posX;
            absPosY = posY;
        }
        cleanedLayout();

        if (hasChildren()) {
            final int childrenCount = children.size();
            for (int i = 0; i < childrenCount; ++i) {
                children.get(i).refreshLayout();
            }
        }
    }

    /**
     * Remove all children of this widget.
     */
    public void removeAllChildren() {
        if (hasChildren()) {
            final int childrenCount = children.size();
            for (int i = 0; i < childrenCount; ++i) {
                children.get(i).parent = null;
            }
            children.clear();
            if (children instanceof FastTable) {
                FastTable.recycle((FastTable<Widget>) children);
                children = null;
            }
        }
    }

    /**
     * Remove a widget from the child list by the index of the child in the list
     * of children.
     * 
     * @param index the index location of the child within the list of children
     * @return the widget object that was removed
     * @throws IndexOutOfBoundsException in case the index is beyond or below
     *             the limits of the children list, or there is not children
     *             list at all
     */
    public Widget removeChild(final int index)
        throws IndexOutOfBoundsException {
        if (hasChildren()) {
            final Widget child = children.remove(index);
            if (children.isEmpty() && (children instanceof FastTable)) {
                FastTable.recycle((FastTable<Widget>) children);
                children = null;
            }
            child.parent = null;
            return child;
        }
        throw new IndexOutOfBoundsException();
    }

    /**
     * Remove a widget object from the children of this widget.
     * 
     * @param child the widget that shall be removed
     * @return <code>true</code> in case the child was found and removed within
     *         the list of children of this widget
     */
    @SuppressWarnings("nls")
    public boolean removeChild(final Widget child) {
        final int idx = getChildIndex(child);
        if (idx >= 0) {
            if (removeChild(idx) != child) {
                throw new ConcurrentModificationException(
                    "Tree changed during removal.");
            }
            return true;
        }
        return false;
    }

    /**
     * Set the absolute position of this widget. This is always relative to 0.
     * The position will be stored as relative location to the current state of
     * the widget below.
     * <p>
     * In case the relative coordinates change, it will invalidate the layout
     * what will require a recalculation.
     * </p>
     * 
     * @param newX the new absolute x coordinate
     * @param newY the new absolute y coordinate
     */
    public void setAbsPos(final int newX, final int newY) {
        if (hasParent()) {
            setRelPos(newX - parent.getAbsX(), newY - parent.getAbsY());
        } else {
            setRelPos(newX, newY);
        }
    }

    /**
     * Set the absolute x coordinate of this widget. This is always relative to
     * 0. The position will be stored as relative location to the current state
     * of the widget below.
     * <p>
     * In case the relative coordinates change, it will invalidate the layout
     * what will require a recalculation.
     * </p>
     * 
     * @param newX the new absolute x coordinate
     */
    public void setAbsX(final int newX) {
        if (hasParent()) {
            setRelX(newX - parent.getAbsX());
        } else {
            setRelX(newX);
        }
    }

    /**
     * Set the absolute y coordinate of this widget. This is always relative to
     * 0. The position will be stored as relative location to the current state
     * of the widget below.
     * <p>
     * In case the relative coordinates change, it will invalidate the layout
     * what will require a recalculation.
     * </p>
     * 
     * @param newY the new absolute y coordinate
     */
    public void setAbsY(final int newY) {
        if (hasParent()) {
            setRelY(newY - parent.getAbsY());
        } else {
            setRelY(newY);
        }
    }

    /**
     * Set the height of the widget to a new value.
     * 
     * @param newHeight the new height of the widget
     */
    public void setHeight(final int newHeight) {
        height = newHeight;
    }

    /**
     * Set a script that is triggered upon initialization. Note that this is
     * called right away when added. So you don't have to ensure its proper
     * working yourself.
     * 
     * @param newInitScript the new initialization script
     */
    public void setInitScript(final WidgetInit newInitScript) {
        initScript = newInitScript;
        if (newInitScript != null) {
            newInitScript.initWidget(this);
        }
    }

    /**
     * Set the size of a widget to its maximum according to the parent widget.
     * In case there is no parent to this widget the values remain unchanged.
     */
    public void setMaxiumSize() {
        if (hasParent()) {
            width = parent.getWidth() - posX;
            height = parent.getHeight() - posY;
        }
    }

    /**
     * Set the relative position to the parent widget. This will invalidate the
     * layout and cause a recalculation before its rendered next time.
     * <p>
     * The layout is not invalidated in case the new position equals the old
     * one.
     * </p>
     * 
     * @param newX the new relative x coordinate.
     * @param newY the new relative y coordinate.
     */
    public void setRelPos(final int newX, final int newY) {
        if ((newX != posX) || (newY != posY)) {
            posX = newX;
            posY = newY;
            layoutInvalid();
        }
    }

    /**
     * Set the x coordinate of the relative position to the parent widget. This
     * will invalidate the layout and cause a recalculation before its rendered
     * next time.
     * <p>
     * The layout is not invalidated in case the new x coordinate equals the old
     * one.
     * </p>
     * 
     * @param newX the new relative x coordinate.
     */
    public void setRelX(final int newX) {
        setRelPos(newX, posY);
    }

    /**
     * Set the y coordinate of the relative position to the parent widget. This
     * will invalidate the layout and cause a recalculation before its rendered
     * next time.
     * <p>
     * The layout is not invalidated in case the new y coordinate equals the old
     * one.
     * </p>
     * 
     * @param newY the new relative y coordinate.
     */
    public void setRelY(final int newY) {
        setRelPos(posX, newY);
    }

    /**
     * Set the size of this widget.
     * 
     * @param newWidth the new width of this widget
     * @param newHeight the new height of this widget
     */
    public void setSize(final int newWidth, final int newHeight) {
        height = newHeight;
        width = newWidth;
    }

    /**
     * Set the new visible value of this widget.
     * 
     * @param newVisible <code>true</code> in case the widget shall be visible
     */
    public void setVisible(final boolean newVisible) {
        visible = newVisible;
    }

    /**
     * Set the width of the widget to a new value.
     * 
     * @param newWidth the new width of the widget
     */
    public void setWidth(final int newWidth) {
        width = newWidth;
    }

    /**
     * Create a human readable string representation of this widget.
     */
    @Override
    public String toString() {
        return String.format(Lang.getInstance().getLocale(), TO_STRING_FORMAT,
            Integer.toString(getAbsX()), Integer.toString(getAbsY()),
            Integer.toString(getWidth()), Integer.toString(getHeight()));
    }

    /**
     * Report that the layout got cleaned up for the widget.
     */
    protected final void cleanedLayout() {
        dirtyLayout = false;
    }

    /**
     * Get the child that is at one location at the highest layer.
     * 
     * @param checkX the x coordinate of the absolute location that shall be
     *            checked
     * @param checkY the y coordinate of the absolute location that shall be
     *            checked
     * @return the widget that was found at this location or <code>null</code>
     */
    protected Widget getChildAt(final int checkX, final int checkY) {
        if (!isVisible()) {
            return null;
        }
        if (children != null) {
            for (int i = children.size() - 1; i >= 0; --i) {
                final Widget child = children.get(i);
                if (child.isVisible() && child.isInside(checkX, checkY)) {
                    return child;
                }
            }
        }
        return null;
    }

    /**
     * Check if the layout is currently dirty.
     * 
     * @return <code>true</code> in case the layout is currently dirty
     */
    protected final boolean isLayoutDirty() {
        return dirtyLayout;
    }

    /**
     * Set the absolute position of this widget. This is always relative to 0.
     * The position will be stored as relative location to the current state of
     * the widget below in case the unchecked flag is set to false. Else the
     * value is just written to the absolute values.
     * 
     * @param newX the new absolute x coordinate
     * @param newY the new absolute y coordinate
     */
    protected void setAbsPos(final int newX, final int newY,
        final boolean unchecked) {
        if (unchecked) {
            absPosX = newX;
            absPosY = newY;
        } else {
            setAbsPos(newX, newY);
        }
    }

    /**
     * Walk over all children and ensure that they all know that the layout is
     * now invalid.
     */
    private void invalidateLayout() {
        dirtyLayout = true;
        if (children != null) {
            final int childrenCount = children.size();
            for (int i = 0; i < childrenCount; ++i) {
                children.get(i).invalidateLayout();
            }
        }
    }
}

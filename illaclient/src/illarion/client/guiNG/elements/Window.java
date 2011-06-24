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

import illarion.client.guiNG.event.WidgetEvent;
import illarion.client.guiNG.messages.WindowMessage;

import illarion.input.MouseEvent;

/**
 * The window is pretty much exactly the same as a normal widget with the only
 * exception that it knows its content pane and is able to set some settings how
 * it shall act on close and such things.
 * <p>
 * This widget is not supposed to be used directly. There is a small builder
 * class that constructs a window at the needed size. A real window consists of
 * more then one widget.
 * </p>
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public class Window extends Widget {
    /**
     * This class stores the widget event that is executed in case the close
     * button is clicked. This causes the window to close and if set the close
     * handler to be executed.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    private static final class CloseButtonHandler implements WidgetEvent {
        /**
         * The serialization UID of this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * The window that is closed by this handler.
         */
        private final Window parent;

        /**
         * Constructor of the close button handler. This one takes the instance
         * of the window that is closed by this button.
         * 
         * @param parentWindow the window that is closed by this button
         */
        public CloseButtonHandler(final Window parentWindow) {
            parent = parentWindow;
        }

        /**
         * Execute this event and close the window by doing so.
         */
        @Override
        public void handleEvent(final Widget source) {
            final WidgetEvent closingHandler = parent.getCloseHandler();
            if (closingHandler != null) {
                closingHandler.handleEvent(parent);
            }
            parent.setVisible(false);
        }
    }

    /**
     * Current serialization UID of the window class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The widget that shows the bottom border of the window.
     */
    private final Widget bottomBorder;

    /**
     * The button that is used to close the window.
     */
    private final Button closeButton;

    /**
     * The widget event that is executed in case the window is closed.
     */
    private WidgetEvent closeHandler = null;

    /**
     * The content pane widget of the window.
     */
    private final Widget contentPane;

    /**
     * The widget that shows the left border of the window.
     */
    private final Widget leftBorder;

    /**
     * The widget that shows the border between title bar and content pane.
     */
    private final Widget middleBorder;

    /**
     * This variable stores if the windows is persistent or not. In case it is,
     * the window will be saved along with the GUI. In case its not, it will be
     * removed and discarded before the GUI is saved.
     */
    private boolean persistent;

    /**
     * The widget that shows the right border of the window.
     */
    private final Widget rightBorder;

    /**
     * The widget that shows the background of the title bar.
     */
    private final Widget titleBackground;

    /**
     * The widget that is the title bar.
     */
    private final Widget titleBar;

    /**
     * The widget that shows the text inside the title bar.
     */
    private final Text titleText;

    /**
     * The widget that shows the top border of the window.
     */
    private final Widget topBorder;

    /**
     * Constructor of the window. This one takes all the widget needed to
     * display the window. The widget must not be in a tree already, the window
     * will put them together properly.
     * 
     * @param newLeftBorder the left border
     * @param newRightBorder the right border
     * @param newTopBorder the top border
     * @param newBottomBorder the bottom border
     * @param newMiddleBorder the border between title bar and content pane
     * @param newTitleBar the title bar
     * @param newTitleBackground the background of the title bar
     * @param newTitleText the text written in the title bar
     * @param newContentPane the content pane
     * @param newCloseButtonImage the image of the close button
     */
    public Window(final Widget newLeftBorder, final Widget newRightBorder,
        final Widget newTopBorder, final Widget newBottomBorder,
        final Widget newMiddleBorder, final Widget newTitleBar,
        final Widget newTitleBackground, final Text newTitleText,
        final Widget newContentPane, final Widget newCloseButtonImage) {
        super();

        leftBorder = newLeftBorder;
        rightBorder = newRightBorder;
        topBorder = newTopBorder;
        bottomBorder = newBottomBorder;
        middleBorder = newMiddleBorder;
        titleBar = newTitleBar;
        titleBackground = newTitleBackground;
        titleText = newTitleText;
        contentPane = newContentPane;

        closeButton = new Button();
        closeButton.setWidth(newCloseButtonImage.getWidth());
        closeButton.setHeight(newCloseButtonImage.getHeight());
        closeButton.setClickHandler(new CloseButtonHandler(this));

        addChild(leftBorder);
        addChild(rightBorder);
        addChild(bottomBorder);
        addChild(titleBar);
        titleBar.addChild(topBorder);
        titleBar.addChild(middleBorder);
        titleBar.addChild(titleBackground);
        titleBackground.addChild(titleText);
        titleBackground.addChild(closeButton);
        closeButton.addChild(newCloseButtonImage);
        addChild(contentPane);

        arrangeItems();
    }

    /**
     * Clean up the window. In case this window is not persistent it is removed
     * from the GUI along with all its children and will be not loaded again
     * when the GUI is loaded at the next client start.
     */
    @Override
    public void cleanup() {
        if (!persistent) {
            getParent().removeChild(this);
        } else {
            super.cleanup();
        }
    }

    /**
     * Get the content pane of his window.
     * 
     * @return the content pane of the window
     */
    public Widget getContentPane() {
        return contentPane;
    }

    /**
     * Get the widget that displays the title of the window
     * 
     * @return the widget that displays the title of the window
     */
    public Text getTitleText() {
        return titleText;
    }

    /**
     * Handle a mouse event. A window is not click transparent. So it does not
     * forward the clicks to objects below.
     * 
     * @param event the mouse event that needs to be handled
     */
    @Override
    public void handleMouseEvent(final MouseEvent event) {
        return;
    }

    /**
     * Check if this window is a persistent window.
     * 
     * @return <code>true</code> in case this is a persistent window
     */
    public boolean isPersistent() {
        return persistent;
    }

    /**
     * Set the handler that is executed in case the window is closed using the
     * close button in the title bar.
     * 
     * @param event the widget event
     */
    public void setCloseHandler(final WidgetEvent event) {
        closeHandler = event;
    }

    @Override
    public void setHeight(final int newHeight) {
        super.setHeight(newHeight);
        arrangeItems();
    }

    /**
     * Set if his is a persistent window or not.
     * 
     * @param newPersistent <code>true</code> to set this a persistent window
     */
    public void setPersistent(final boolean newPersistent) {
        persistent = newPersistent;
    }

    @Override
    public void setVisible(final boolean visible) {
        if (!isVisible() && visible) {
            handleMessage(WindowMessage
                .getInstance(WindowMessage.WINDOW_OPENED));
        } else if (isVisible() && !visible) {
            handleMessage(WindowMessage
                .getInstance(WindowMessage.WINDOW_CLOSED));
        }
        super.setVisible(visible);
    }

    @Override
    public void setWidth(final int newWidth) {
        super.setWidth(newWidth);
        arrangeItems();
    }

    /**
     * Method to fetch the currently set close handler of window.
     * 
     * @return the close handler
     */
    WidgetEvent getCloseHandler() {
        return closeHandler;
    }

    /**
     * Update the positions of all child objects that are needed for the design
     * of the window.
     */
    private void arrangeItems() {
        if ((getHeight() == 0) || (getWidth() == 0)) {
            return;
        }
        leftBorder.setHeight(getHeight());

        rightBorder.setRelX(getWidth() - rightBorder.getWidth());
        rightBorder.setHeight(getHeight());

        topBorder.setRelY(titleBar.getHeight() - topBorder.getHeight());
        topBorder.setWidth(getWidth());

        bottomBorder.setWidth(getWidth());

        middleBorder.setWidth(getWidth());

        titleBar.setRelY(getHeight() - titleBar.getHeight());
        titleBar.setWidth(getWidth());

        titleBackground.setWidth(titleBar.getWidth() - leftBorder.getWidth()
            - rightBorder.getWidth());

        closeButton.setRelX(titleBackground.getWidth()
            - closeButton.getWidth());
        closeButton.setRelY(0);

        titleText.setWidth(titleBackground.getWidth());

        contentPane.setHeight(getHeight() - titleBar.getHeight()
            - bottomBorder.getHeight());
        contentPane.setWidth(getWidth() - leftBorder.getWidth()
            - rightBorder.getWidth());
    }
}

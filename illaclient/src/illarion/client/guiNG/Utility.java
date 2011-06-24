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
package illarion.client.guiNG;

import illarion.client.graphics.MarkerFactory;
import illarion.client.guiNG.elements.DragLayer;
import illarion.client.guiNG.elements.Image;
import illarion.client.guiNG.elements.ImageRepeated;
import illarion.client.guiNG.elements.Text;
import illarion.client.guiNG.elements.Widget;
import illarion.client.guiNG.elements.Window;
import illarion.client.guiNG.event.LockButtonEvent;
import illarion.client.guiNG.init.ImageInit;
import illarion.client.guiNG.init.WidgetInit;
import illarion.client.guiNG.init.WindowTitleTextInit;

import illarion.graphics.RenderableFont;
import illarion.graphics.common.FontLoader;

/**
 * This is a utility class for the GUI that creates default constructs in a
 * proper way.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class Utility {
    /**
     * The border width of a window.
     */
    private static final int WINDOW_BORDER_WIDTH = 3;

    /**
     * The height of the title bar of a window.
     */
    private static final int WINDOW_TITLE_HEIGHT =
        (WINDOW_BORDER_WIDTH * 2) + 15;

    /**
     * Private constructor to avoid the creation of any instances.
     */
    private Utility() {
        // nothing to do
    }

    /**
     * Build up the button for locking and unlocking the drag layer of a widget.
     * 
     * @param target the drag layer that shall be effected by the widget
     * @return the root node of the lock that needs to be attached and placed in
     *         the GUI tree
     */
    public static Widget buildLock(final DragLayer target) {
        target.enableDragging();

        final LockButton lockButton = new LockButton();
        lockButton.setWidth(23);
        lockButton.setHeight(15);

        final Image lockedImage = new Image();
        lockedImage.setRelPos(0, 0);
        lockedImage.setInitScript(ImageInit.getInstance().setImageID(
            MarkerFactory.DRAGGING_LOCK));
        lockedImage.setSizeToImage();
        lockedImage.setVisible(false);

        final Image unlockedImage = new Image();
        unlockedImage.setRelPos(0, 0);
        unlockedImage.setInitScript(ImageInit.getInstance().setImageID(
            MarkerFactory.DRAGGING_UNLOCK));
        unlockedImage.setSizeToImage();

        lockButton.addChild(lockedImage);
        lockButton.addChild(unlockedImage);

        final LockButtonEvent event = LockButtonEvent.getInstance();
        event.setTargetWidget(target);
        event.setEffectWidgets(lockedImage, unlockedImage);
        lockButton.setClickHandler(event);

        return lockButton;
    }

    /**
     * Build up a window in the default way and return the root node of the
     * window.
     * 
     * @param width the width of the content pane of the window, the window
     *            itself will be slightly larger
     * @param height the height of the content pane of the window, the window
     *            itself will be slightly larger
     * @return the root node of the created window, this can be attached to the
     *         GUI to display the Window
     */
    public static Window buildWindow(final int width, final int height) {
        final WidgetInit horzBorderInit =
            ImageInit.getInstance().setImageID(MarkerFactory.WINDOW_BORDER_H);
        final WidgetInit vertBorderInit =
            ImageInit.getInstance().setImageID(MarkerFactory.WINDOW_BORDER_V);
        final WidgetInit greenBackInit =
            ImageInit.getInstance().setImageID(
                MarkerFactory.WINDOW_BACKGROUND_GREEN);
        final WidgetInit beigeBackInit =
            ImageInit.getInstance().setImageID(
                MarkerFactory.WINDOW_BACKGROUND_BEIGE);

        final ImageRepeated bottomBorder = new ImageRepeated();
        bottomBorder.setHeight(WINDOW_BORDER_WIDTH);
        bottomBorder.setRelPos(0, 0);
        bottomBorder.setInitScript(horzBorderInit);

        final ImageRepeated leftBorder = new ImageRepeated();
        leftBorder.setWidth(WINDOW_BORDER_WIDTH);
        leftBorder.setRelPos(0, 0);
        leftBorder.setInitScript(vertBorderInit);

        final ImageRepeated rightBorder = new ImageRepeated();
        rightBorder.setWidth(WINDOW_BORDER_WIDTH);
        rightBorder.setRelY(0);
        rightBorder.setInitScript(vertBorderInit);

        final Image closeButton = new Image();
        closeButton.setRelPos(0, 0);
        closeButton.setInitScript(ImageInit.getInstance().setImageID(
            MarkerFactory.WINDOW_CLOSE_BUTTON));
        closeButton.setSizeToImage();

        final DragLayer titleBar = new DragLayer();
        titleBar.setHeight(WINDOW_TITLE_HEIGHT);
        titleBar.setRelX(0);
        titleBar.setShapeSource(DragLayer.SHAPE_WIDGET);

        final ImageRepeated topBorder = new ImageRepeated();
        topBorder.setHeight(WINDOW_BORDER_WIDTH);
        topBorder.setRelX(0);
        topBorder.setInitScript(horzBorderInit);

        final ImageRepeated middleBorder = new ImageRepeated();
        middleBorder.setHeight(WINDOW_BORDER_WIDTH);
        middleBorder.setRelPos(0, 0);
        middleBorder.setInitScript(horzBorderInit);

        final ImageRepeated titleBackground = new ImageRepeated();
        titleBackground.setHeight(WINDOW_TITLE_HEIGHT
            - (2 * WINDOW_BORDER_WIDTH));
        titleBackground.setRelPos(WINDOW_BORDER_WIDTH, WINDOW_BORDER_WIDTH);
        titleBackground.setInitScript(greenBackInit);

        final RenderableFont titleFont =
            FontLoader.getInstance().getFont(FontLoader.SMALL_FONT);
        final int fontHeight = titleFont.getSourceFont().getAscent();

        final Text titleText = new Text();
        titleText.setHeight(fontHeight);
        titleText.setRelPos(0, (titleBar.getHeight() - fontHeight) / 2);
        titleText.setAlign(Text.ALIGN_CENTER);
        titleText.setInitScript(WindowTitleTextInit.getInstance());

        final ImageRepeated mainBackground = new ImageRepeated();
        mainBackground.setRelPos(WINDOW_BORDER_WIDTH, WINDOW_BORDER_WIDTH);
        mainBackground.setInitScript(beigeBackInit);

        final Window rootNode =
            new Window(leftBorder, rightBorder, topBorder, bottomBorder,
                middleBorder, titleBar, titleBackground, titleText,
                mainBackground, closeButton);
        titleBar.setDragTarget(rootNode);
        titleBar.setBringToFront(true);
        rootNode.setHeight(height);
        rootNode.setWidth(width);

        return rootNode;
    }

    /**
     * Center the x coordinate of a widget. That means that the widget is
     * aligned centered related to its parent.
     * 
     * @param target the widget that shall be centered
     */
    public static void centerWidgetX(final Widget target) {
        target
            .setRelX((target.getParent().getWidth() - target.getWidth()) >> 1);
    }

    /**
     * Center the y coordinate of a widget. That means that the widget is
     * aligned centered related to its parent.
     * 
     * @param target the widget that shall be centered
     */
    public static void centerWidgetY(final Widget target) {
        target
            .setRelY((target.getParent().getHeight() - target.getHeight()) >> 1);
    }

    /**
     * Align the widget at the left side of its parent widget.
     * 
     * @param target the widget to align
     */
    public static void rightWidgetX(final Widget target) {
        target.setRelX((target.getParent().getWidth() - target.getWidth()));
    }

    /**
     * Align the widget on the top of the area of its parent widget.
     * 
     * @param target the widget to align
     */
    public static void topWidgetY(final Widget target) {
        target.setRelY((target.getParent().getHeight() - target.getHeight()));
    }
}

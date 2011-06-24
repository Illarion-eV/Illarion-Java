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

import illarion.client.graphics.AnimationUtility;
import illarion.client.graphics.Colors;
import illarion.client.graphics.MarkerFactory;
import illarion.client.guiNG.elements.Button;
import illarion.client.guiNG.elements.ImageRepeated;
import illarion.client.guiNG.elements.SolidColor;
import illarion.client.guiNG.elements.Text;
import illarion.client.guiNG.elements.Widget;
import illarion.client.guiNG.elements.Window;
import illarion.client.guiNG.event.WidgetEvent;
import illarion.client.guiNG.init.ImageInit;
import illarion.client.util.Lang;

import illarion.graphics.Graphics;
import illarion.graphics.RenderableFont;
import illarion.graphics.SpriteColor;
import illarion.graphics.common.Font;
import illarion.graphics.common.FontLoader;

/**
 * This widget is used to display the proper "really exit" dialog in case the
 * player requests to quit the game.
 * 
 * @author Martin Karing
 * @version 1.22
 * @since 1.22
 */
public final class ReallyExit extends SolidColor {
    /**
     * This class stores the event that is called in case the "No" button is
     * clicked or in case the window displaying the message is closed. This
     * causes the "cancel" task to be executed. In the most cases this should
     * hide the closing window again.
     * 
     * @author Martin Karing
     * @version 1.22
     * @since 1.22
     */
    private static final class NoButtonEvent implements WidgetEvent {
        /**
         * The serialization unique ID.
         */
        private static final long serialVersionUID = 1L;

        /**
         * The task that is executed by this widget event.
         */
        private final Runnable noTask;

        /**
         * The "Really Exit" widget that called this event. This reference is
         * used to hide the window again.
         */
        private final Widget parent;

        /**
         * The constructor for this "No" button widget event. This constructor
         * takes the task to be executed when this widget event is executed and
         * the widget that needs to be hidden once the task got executed.
         * 
         * @param task the task to be executed when this widget event is
         *            executed
         * @param rootHandler the widget that is hidden once the execution of
         *            the task is done
         */
        public NoButtonEvent(final Runnable task, final Widget rootHandler) {
            noTask = task;
            parent = rootHandler;
        }

        @Override
        public void handleEvent(final Widget source) {
            if (noTask != null) {
                noTask.run();
            }

            GUI.getInstance().requestExclusiveMouse(null);
            parent.getParent().removeChild(parent);
        }
    }

    /**
     * This class stores the event that is called in case the "Yes" button is
     * clicked. This causes the "close" task to be executed. In the most cases
     * this should terminate the client.
     * 
     * @author Martin Karing
     * @version 1.22
     * @since 1.22
     */
    private static final class YesButtonEvent implements WidgetEvent {
        /**
         * The serialization unique ID.
         */
        private static final long serialVersionUID = 1L;

        /**
         * The task that is executed by this widget event.
         */
        private final Runnable yesTask;

        /**
         * The constructor for this event. This constructor takes the task that
         * is wrapped by this widget event.
         * 
         * @param task the task to be executed by this event
         */
        public YesButtonEvent(final Runnable task) {
            yesTask = task;
        }

        @Override
        public void handleEvent(final Widget source) {
            if (yesTask != null) {
                yesTask.run();
            }
        }
    }

    /**
     * The serialization UID of this dialog.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The alpha value that is the target of a approach.
     */
    private static final int TARGET_ALPHA =
        (int) (0.75f * SpriteColor.COLOR_MAX);

    /**
     * The background color that is used to fade out the entire screen while
     * showing the dialog.
     */
    private transient final SpriteColor backgroundColor;

    /**
     * The task that is executed in case the "No" button is clicked.
     */
    private Runnable cancelingTask = null;

    /**
     * The task that is executed in case the "Yes" button is clicked.
     */
    private Runnable closingTask = null;

    /**
     * Initialize the background color that shall be displayed once this dialog
     * is shown.
     */
    public ReallyExit() {
        backgroundColor = Graphics.getInstance().getSpriteColor();
        backgroundColor.set(SpriteColor.COLOR_MIN);
        backgroundColor.setAlpha(SpriteColor.COLOR_MIN);
        setColor(backgroundColor);
        super.setVisible(false);
    }

    @Override
    public void cleanup() {
        if (hasParent()) {
            getParent().removeChild(this);
        }
    }

    @Override
    public void draw(final int delta) {
        if (backgroundColor.getAlphai() != TARGET_ALPHA) {
            backgroundColor.setAlpha(AnimationUtility.approach(
                backgroundColor.getAlphai(), TARGET_ALPHA,
                SpriteColor.COLOR_MIN, SpriteColor.COLOR_MAX, delta));
        }
        super.draw(delta);
    }

    /**
     * Set the task that is executed in case the closing operation is canceled.
     * So this task is executed in case the "No" button was clicked.
     * 
     * @param cancelTask the task
     */
    public void setCancelTask(final Runnable cancelTask) {
        cancelingTask = cancelTask;
    }

    /**
     * Set the task that is executed in case closing the client is requested. So
     * this task is executed in case the "Yes" button was clicked.
     * 
     * @param closeTask the task
     */
    public void setCloseTask(final Runnable closeTask) {
        closingTask = closeTask;
    }

    @Override
    @SuppressWarnings("nls")
    public void setVisible(final boolean visible) {
        if (visible && !hasParent()) {
            throw new IllegalStateException(
                "Before making this widget visible, it needs to be added to its parent.");
        }

        if (!isVisible() && visible) {
            backgroundColor.setAlpha(SpriteColor.COLOR_MIN);
            buildDialog();
        }

        super.setVisible(visible);
    }

    /**
     * This method constructors the entire widget that displays the dialog.
     */
    @SuppressWarnings("nls")
    private void buildDialog() {
        final Window window = Utility.buildWindow(400, 100);
        window.setPersistent(false);
        window.getTitleText().setText(Lang.getMsg("exit.reallyExit.head"));
        window.setCloseHandler(new NoButtonEvent(cancelingTask, this));

        final Text text = new Text();
        text.setHeight(30);
        text.setWidth(window.getWidth() - 30);
        text.setAlign(Text.ALIGN_CENTER);
        text.setRelPos(15, 57);
        text.setFont(FontLoader.getInstance().getFont(FontLoader.TEXT_FONT));
        text.setColor(Colors.black.getColor());
        text.setText(Lang.getMsg("exit.reallyExit.message"));

        window.getContentPane().addChild(text);

        final Button yesButton = new Button();
        yesButton.setAllowDoubleClick(false);
        yesButton.setClickHandler(new YesButtonEvent(closingTask));
        yesButton.setWidth(60);
        yesButton.setHeight(30);
        yesButton.setRelPos((window.getWidth() >> 2)
            - (yesButton.getWidth() >> 1), 10);

        final RenderableFont buttonFont =
            FontLoader.getInstance().getFont(FontLoader.TEXT_FONT);
        final SpriteColor buttonFontColor =
            Graphics.getInstance().getSpriteColor();
        buttonFontColor.set(221, 198, 135);

        final Text yesText = new Text();
        final String yesString = Lang.getMsg("button.yes");
        yesText.setFont(buttonFont);
        yesText.setText(yesString);
        yesText.setColor(buttonFontColor);
        yesText.setAlign(Text.ALIGN_CENTER);
        yesText.setWidth(50);
        yesText.setHeight(((Font) buttonFont.getSourceFont()).getStringBounds(
            yesString, 0, yesString.length()).height);
        yesText.setRelPos(5, 0);

        final ImageRepeated yesBackground = new ImageRepeated();
        yesBackground.setInitScript(ImageInit.getInstance().setImageID(
            MarkerFactory.WINDOW_BACKGROUND_GREEN));
        yesBackground.setWidth(60);
        yesBackground.setHeight(30);
        yesBackground.setRelPos(0, 0);

        yesButton.addChild(yesBackground);
        yesBackground.addChild(yesText);
        Utility.centerWidgetY(yesText);

        window.getContentPane().addChild(yesButton);

        final Button noButton = new Button();
        noButton.setAllowDoubleClick(false);
        noButton.setClickHandler(new NoButtonEvent(cancelingTask, this));
        noButton.setWidth(60);
        noButton.setHeight(30);
        noButton.setRelPos(
            ((window.getWidth() >> 2) + (window.getWidth() >> 1))
                - (noButton.getWidth() >> 1), 10);

        final Text noText = new Text();
        final String noString = Lang.getMsg("button.no");
        noText.setFont(buttonFont);
        noText.setText(noString);
        noText.setColor(buttonFontColor);
        noText.setAlign(Text.ALIGN_CENTER);
        noText.setWidth(50);
        noText.setHeight(((Font) buttonFont.getSourceFont()).getStringBounds(
            noString, 0, noString.length()).height);
        noText.setRelPos(5, 0);

        final ImageRepeated noBackground = new ImageRepeated();
        noBackground.setInitScript(ImageInit.getInstance().setImageID(
            MarkerFactory.WINDOW_BACKGROUND_GREEN));
        noBackground.setWidth(60);
        noBackground.setHeight(30);
        noBackground.setRelPos(0, 0);

        noButton.addChild(noBackground);
        noBackground.addChild(noText);
        Utility.centerWidgetY(noText);

        window.getContentPane().addChild(noButton);

        setWidth(getParent().getWidth());
        setHeight(getParent().getHeight());
        setRelPos(0, 0);

        addChild(window);
        Utility.centerWidgetX(window);
        Utility.centerWidgetY(window);
    }
}

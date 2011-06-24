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

import java.awt.Rectangle;

import javolution.util.FastList;

import illarion.client.graphics.AnimationUtility;
import illarion.client.graphics.Avatar;
import illarion.client.graphics.MapDisplayManager;
import illarion.client.guiNG.elements.TextArea;
import illarion.client.guiNG.elements.Widget;
import illarion.client.guiNG.messages.Message;
import illarion.client.util.ChatHandler.SpeechMode;
import illarion.client.util.Lang;
import illarion.client.world.Char;
import illarion.client.world.Game;

import illarion.common.util.Location;
import illarion.common.util.Vector;

import illarion.graphics.Drawer;
import illarion.graphics.Graphics;
import illarion.graphics.SpriteColor;
import illarion.graphics.common.FontLoader;

import illarion.input.KeyboardEvent;
import illarion.input.MouseEvent;

/**
 * This chat layer is a transparent layer on top of the map layer that displays
 * the messages the characters speak. All messages are displayed temporary for a
 * time span based on the length of the text.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class ChatLayer extends Widget {
    /**
     * The ChatText Widgets are the texts that are actually displayed on the on
     * the ChatLayer. They display the text and hide themselves after some time.
     * 
     * @author Martin Karing
     * @since 1.22
     */
    private static final class ChatText extends Widget {
        /**
         * The color used to draw the background of the text.
         */
        private static final SpriteColor backgroundColor;

        /**
         * The color used to draw the frame of the text background.
         */
        private static final SpriteColor frameColor;

        /**
         * The serialization UID of this Widget
         */
        private static final long serialVersionUID = 1L;

        /**
         * The border around the text additional to the rounded border.
         */
        private static final int TEXT_BORDER = 4;

        static {
            backgroundColor = Graphics.getInstance().getSpriteColor();
            backgroundColor.set(SpriteColor.COLOR_MIN);
            backgroundColor.setAlpha(0.6f);

            frameColor = Graphics.getInstance().getSpriteColor();
            frameColor.set(SpriteColor.COLOR_MIN);
            frameColor.setAlpha(0.95f);
        }

        /**
         * The current alpha value of the color.
         */
        private int alpha;

        /**
         * The character who said the text that was reported
         */
        private Char character;

        /**
         * This flag stores if the text displayed is dirty and requires a
         * update.
         */
        private boolean dirty;

        /**
         * The key for the language file for the generic "someone" name in the
         * chat.
         */
        @SuppressWarnings("nls")
        private final String KEY_SOMEONE = "chat.someone";

        /**
         * The position of the text on the map.
         */
        private final Location loc;

        /**
         * The mode of the speech text.
         */
        private SpeechMode mode;

        /**
         * The time in milliseconds remaining to display the text.
         */
        private int remainingTime;

        /**
         * The text that is displayed.
         */
        private final TextArea text;

        /**
         * The color that is used to draw the text currently.
         */
        private transient SpriteColor textColor;

        /**
         * The constructor for this Chat text widget. This prepares all
         * variables needed for this class to work properly.
         */
        public ChatText() {
            super();
            initWidget();
            text = new TextArea();
            loc = new Location();
            setVisible(false);

            addChild(text);
            text.setRelPos(Drawer.ROUNDED_BORDER_WIDTH + 1,
                Drawer.ROUNDED_BORDER_WIDTH + 1);
            text.setMaximalWidth(300);
            text.setFont(FontLoader.getInstance()
                .getFont(FontLoader.TEXT_FONT));
            text.setVisible(true);
            text.setColor(textColor);
        }

        @Override
        public void draw(final int delta) {
            remainingTime -= delta;
            if (remainingTime > 0) {
                alpha =
                    AnimationUtility.translateAlpha(alpha,
                        SpriteColor.COLOR_MAX, 20, delta);
            } else {
                alpha =
                    AnimationUtility.translateAlpha(alpha,
                        SpriteColor.COLOR_MIN, 20, delta);

                if (alpha == 0) {
                    getParent().removeChild(this);
                    return;
                }
            }

            backgroundColor.setAlpha((int) (alpha * 0.6f));
            frameColor.setAlpha((int) (alpha * 0.95f));
            textColor.setAlpha(alpha);

            final Drawer drawer = Graphics.getInstance().getDrawer();
            drawer.drawRoundedRectangle(Drawer.ROUNDED_BORDER_WIDTH
                + getAbsX(), Drawer.ROUNDED_BORDER_WIDTH + getAbsY(),
                getAbsRight() - Drawer.ROUNDED_BORDER_WIDTH, getAbsBottom()
                    - Drawer.ROUNDED_BORDER_WIDTH, backgroundColor);

            drawer.drawRoundedRectangleFrame(Drawer.ROUNDED_BORDER_WIDTH
                + getAbsX(), Drawer.ROUNDED_BORDER_WIDTH + getAbsY(),
                getAbsRight() - Drawer.ROUNDED_BORDER_WIDTH, getAbsBottom()
                    - Drawer.ROUNDED_BORDER_WIDTH, 1.5f, frameColor);

            // drawer.drawRectangleFrame(getAbsX(), getAbsY(), getAbsRight(),
            // getAbsBottom(), 1.f, Colors.green.getColor());
            // drawer.drawRectangleFrame(text.getAbsX(), text.getAbsY(),
            // text.getAbsRight(), text.getAbsBottom(), 1.f,
            // Colors.red.getColor());

            super.draw(delta);
        }

        @Override
        public void initWidget() {
            textColor = Graphics.getInstance().getSpriteColor();
            super.initWidget();
        }

        /**
         * Update the layout of this text and place is properly.
         */
        @Override
        public void refreshLayout() {
            if (dirty) {
                dirty = false;

                textColor.set(mode.getColor().getColor());

                final Rectangle bounds = text.getBounds();
                setHeight(bounds.height + (Drawer.ROUNDED_BORDER_WIDTH << 1)
                    + TEXT_BORDER);
                setWidth(bounds.width + (Drawer.ROUNDED_BORDER_WIDTH << 1)
                    + TEXT_BORDER);

                text.setRelPos(Drawer.ROUNDED_BORDER_WIDTH
                    + (TEXT_BORDER >> 1), Drawer.ROUNDED_BORDER_WIDTH
                    + (TEXT_BORDER >> 1));
                text.setHeight(bounds.height);
                text.setWidth(bounds.width);

                int displayX;
                int displayY;
                if ((character != null) && (character.getAvatar() != null)) {
                    final Avatar ava = character.getAvatar();
                    displayX = ava.getDisplayX();
                    displayY = ava.getDisplayY();
                    displayX -= ava.getWidth() >> 1;
                    displayY += ava.getHeight();
                } else {
                    displayX = loc.getDcX();
                    displayY = loc.getDcY();
                }

                final int originX =
                    Game.getDisplay()
                        .getWorldX(MapDisplayManager.MAP_CENTER_X);
                final int originY =
                    Game.getDisplay()
                        .getWorldY(MapDisplayManager.MAP_CENTER_Y);

                final Vector vect = Vector.getInstance();
                vect.setOrigin(MapDisplayManager.MAP_CENTER_X,
                    MapDisplayManager.MAP_CENTER_Y);
                vect.setTarget((displayX - originX)
                    + MapDisplayManager.MAP_CENTER_X, (displayY - originY)
                    + MapDisplayManager.MAP_CENTER_Y);

                int finalPosX = vect.getTargetX();
                int finalPosY = vect.getTargetY();

                if ((finalPosX - (getWidth() >> 1)) < 0) {
                    finalPosX = getWidth() >> 1;
                    finalPosY = vect.getYOnVector(finalPosX);
                } else if ((finalPosX + (getWidth() >> 1)) > getParent()
                    .getWidth()) {
                    finalPosX = getParent().getWidth() - (getWidth() >> 1);
                    finalPosY = vect.getYOnVector(finalPosX);
                }

                if (finalPosY < 0) {
                    finalPosY = 0;
                    finalPosX = vect.getXOnVector(finalPosY);
                } else if ((finalPosY + getHeight()) > getParent().getHeight()) {
                    finalPosY = getParent().getHeight() - getHeight();
                    finalPosX = vect.getXOnVector(finalPosY);
                }

                setRelPos(finalPosX - (getWidth() >> 1), finalPosY);
            }
            super.refreshLayout();
        }

        /**
         * Display the chattext on the screen. This function does not work
         * properly in case the line is currently not displayed.
         * 
         * @param messageText the text that shall be displayed
         * @param chara the character who said the text
         * @param location the position where it shall be displayed
         * @param speechMode the speech mode of this text
         */
        @SuppressWarnings("nls")
        public void showText(final String messageText, final Char chara,
            final Location location, final SpeechMode speechMode) {
            if (isVisible()) {
                throw new IllegalStateException(
                    "Can't alter chattext that is already displayed.");
            }

            if (speechMode == null) {
                throw new IllegalArgumentException(
                    "SpeechMode must not be NULL");
            }

            String usedText;
            if (speechMode == SpeechMode.emote) {
                String name = null;
                if (chara != null) {
                    name = chara.getName();
                }

                if (name == null) {
                    name = Lang.getMsg(KEY_SOMEONE);
                }

                usedText = name + messageText;
            } else if (speechMode == SpeechMode.ooc) {
                usedText = "(( " + messageText + " ))";
            } else {
                usedText = messageText;
            }

            character = chara;
            text.setText(usedText);
            mode = speechMode;
            loc.set(location);
            dirty = true;
            layoutInvalid();
            setVisible(true);
            alpha = 0;
            remainingTime = 1500 + ((3000 * usedText.length()) / 80);
        }
    }

    /**
     * The serialization UID of this chat layer.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The buffer of Chat text objects that can be displayed on this chat layer.
     * This list is marked as transient because it are restored during the
     * widget initialization.
     */
    private transient FastList<ChatText> buffer;

    /**
     * The list of ChatTexts that should be removed from the drawing list once
     * the drawing is done. This list is marked as transient because it are
     * restored during the widget initialization.
     */
    private transient FastList<ChatText> removeList;

    /**
     * The constructor for this Chat layer widget. This prepares all variables
     * needed for this class to work properly.
     */
    public ChatLayer() {
        super();
        buffer = new FastList<ChatText>();
        removeList = new FastList<ChatText>();
    }

    /**
     * Clean this chat layer by removing all older created entries.
     */
    @Override
    public void cleanup() {
        super.removeAllChildren();
        buffer.clear();
        removeList.clear();
    }

    @Override
    public void draw(final int delta) {
        super.draw(delta);

        if (!removeList.isEmpty()) {
            synchronized (buffer) {
                ChatText removeChild;
                while (!removeList.isEmpty()) {
                    removeChild = removeList.removeFirst();
                    super.removeChild(removeChild);
                    buffer.addLast(removeChild);
                }
            }
        }
    }

    /**
     * Overwritten function to get the child widget. This layer does not expose
     * its children to the other parts of the GUI.
     */
    @Override
    public Widget getWidgetAt(final int x, final int y) {
        return null;
    }

    /**
     * Ensure that the keyboard event are not forwarded here anymore and rather
     * returned to the rest of the GUI.
     */
    @Override
    public boolean handleKeyboardEvent(final KeyboardEvent event) {
        return false;
    }

    /**
     * Do not forward any messages inside the GUI.
     */
    @Override
    public void handleMessage(final Message msg) {
        // nothing to do
    }

    /**
     * Do not forward the mouse events to the texts displayed on this layer.
     */
    @Override
    public void handleMouseEvent(final MouseEvent event) {
        // nothing to do
    }

    /**
     * Prepare the chat layer for proper usage and put the transient variables
     * back into place.
     */
    @Override
    public void initWidget() {
        buffer = new FastList<ChatText>();
        removeList = new FastList<ChatText>();
        super.initWidget();
    }

    /**
     * Overwritten insert child function to ensure no child is added to this
     * layer at any time.
     */
    @Override
    @SuppressWarnings("nls")
    public void insertChild(final Widget newChild, final int position) {
        throw new IllegalArgumentException(
            "This widget does not take any children.");
    }

    @Override
    public boolean isInside(final int x, final int y) {
        return false;
    }

    @Override
    public void refreshLayout() {
        setHeight(getParent().getHeight());
        setWidth(getParent().getWidth());
        setRelPos(0, 0);
        super.refreshLayout();
    }

    @Override
    @SuppressWarnings("nls")
    public void removeAllChildren() {
        throw new IllegalStateException(
            "Removing the children of this widget is not allowed.");
    }

    @Override
    @SuppressWarnings("nls")
    public boolean removeChild(final Widget child) {
        if (child instanceof ChatText) {
            child.setVisible(false);
            removeList.addLast((ChatText) child);
            return true;
        }
        throw new IllegalStateException(
            "Removing any child of this widget is not allowed");
    }

    /**
     * Show a line of text on the screen.
     * 
     * @param text the text to show
     * @param chara the char that said the text
     * @param posX the x coordinate of the position where the text is shown
     * @param posY the y coordinate of the position where the text is shown
     * @param mode the speech mode of the said text
     */
    public void showText(final String text, final Char chara, final int posX,
        final int posY, final SpeechMode mode) {
        final Location tempLoc = Location.getInstance();
        tempLoc.setSC(posX, posY, 0);
        showText(text, chara, tempLoc, mode);
        tempLoc.recycle();
    }

    /**
     * Show a line of text on the screen.
     * 
     * @param text the text to show
     * @param chara the char that said the text
     * @param loc the position where the text is shown
     * @param mode the speech mode of the said text
     */
    public void showText(final String text, final Char chara,
        final Location loc, final SpeechMode mode) {
        ChatText displayText = null;
        synchronized (buffer) {
            if (!buffer.isEmpty()) {
                displayText = buffer.removeFirst();
            }
        }

        if (displayText == null) {
            displayText = new ChatText();
        }

        displayText.showText(text, chara, loc, mode);
        super.insertChild(displayText, super.getChildrenCount());
    }
}

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
package illarion.client.graphics;

import java.util.List;

import javolution.util.FastList;
import gnu.trove.list.array.TIntArrayList;

import illarion.client.util.Lang;

import illarion.graphics.Graphics;
import illarion.graphics.RenderTask;
import illarion.graphics.RenderableFont;
import illarion.graphics.SpriteColor;
import illarion.graphics.TextLine;
import illarion.graphics.common.FontLoader;

/**
 * This class displays and updates the loading screen that is displayed in the
 * background of the login window.
 * 
 * @author Martin Karing
 * @since 1.22
 */
public final class LoadingScreen implements RenderTask {
    /**
     * Constant for the display that the avatars are loading.
     */
    public static final int LOADING_AVATARS = 7;

    /**
     * Constant for the display that the clothes are loading.
     */
    public static final int LOADING_CLOTHES = 8;

    /**
     * Constant for the display that the effects are loading.
     */
    public static final int LOADING_EFFECTS = 9;

    /**
     * Constant for the display that the environment is loading.
     */
    public static final int LOADING_ENVIRONMENT = 2;

    /**
     * Constant for the display that the final loading steps are in progress.
     */
    public static final int LOADING_FINAL = 13;

    /**
     * Constant for the display that graphics are loading.
     */
    public static final int LOADING_GRAPHICS = 1;

    /**
     * Constant for the display that GUI is loading.
     */
    public static final int LOADING_GUI = 3;

    /**
     * Constant for the display that the items are loading.
     */
    public static final int LOADING_ITEMS = 6;

    /**
     * Constant for the display that the menus are loading.
     */
    public static final int LOADING_MENUS = 10;

    /**
     * Constant for the display that the network interface is starting
     */
    public static final int LOADING_NET = 14;

    /**
     * Constant for the display that the client is optimizing the data.
     */
    public static final int LOADING_OPTIMIZE = 15;

    /**
     * Constant for the display that the overlays are loading.
     */
    public static final int LOADING_OVERLAYS = 5;

    /**
     * Constant for the display that the runes are loading.
     */
    public static final int LOADING_RUNES = 11;

    /**
     * Constant for the display that the sounds are loading.
     */
    public static final int LOADING_SOUNDS = 12;

    /**
     * Constant for the display that the tiles are loading.
     */
    public static final int LOADING_TILES = 4;

    /**
     * Display a line that shows that the client is ready to go.
     */
    public static final int READY_TO_GO = 255;

    /**
     * The singleton instance of this class.
     */
    private static final LoadingScreen INSTANCE = new LoadingScreen();

    /**
     * The heading for the text entries in the localized file.
     */
    @SuppressWarnings("nls")
    private static final String LOADING_STRING = "splash.loading.";

    /**
     * The color that is used to drawn the text.
     */
    private SpriteColor color;

    /**
     * The list of IDs that are currently loading.
     */
    private TIntArrayList currList;

    /**
     * The dirty flag. In case this is set to true, all lines will be updated at
     * the next render.
     */
    private boolean dirty;

    /**
     * The list of IDs that are already load.
     */
    private TIntArrayList doneList;

    /**
     * The font that is used to display the things that are currently loading.
     */
    private RenderableFont font;

    /**
     * The font used to display the things that are already load.
     */
    private RenderableFont fontDone;

    /**
     * A flag if the loading is done or not. In case this is set to
     * <code>true</code> the loading screen will be fully black.
     */
    private boolean loadingDone;

    /**
     * The text lines that are prepared to be used.
     */
    private List<TextLine> textLines;

    /**
     * Private constructor to ensure that only the singleton instance exists and
     * to setup the variables and lists for the loading screen.
     */
    private LoadingScreen() {
        loadingDone = false;
        doneList = new TIntArrayList(13);
        currList = new TIntArrayList(4);
        color = Graphics.getInstance().getSpriteColor();
        color.set(1.f);
        color.setAlpha(1.f);
        textLines = new FastList<TextLine>(13);
        dirty = false;
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance
     */
    public static LoadingScreen getInstance() {
        return INSTANCE;
    }

    /**
     * Check if one part is already loaded.
     * 
     * @param part the id of the part to be checked
     * @return <code>true</code> in case loading the checked part is done
     */
    public boolean isLoadingDone(final int part) {
        if (loadingDone) {
            return true;
        }
        return doneList.contains(part);
    }

    /**
     * Render the loading screen correctly to the screen.
     * 
     * @param delta the time since the last render action
     * @return <code>true</code> in case the screen needs to be rendered again,
     *         false if not
     */
    @Override
    @SuppressWarnings("nls")
    public boolean render(final int delta) {
        if (loadingDone) {
            doneList = null;
            currList = null;
            font = null;
            fontDone = null;
            color = null;
            textLines = null;
            return false;
        }

        if (dirty) {
            if (font == null) {
                font = FontLoader.getInstance().getFont(FontLoader.MENU_FONT);
                fontDone =
                    FontLoader.getInstance().getFont(FontLoader.SMALL_FONT);
            }
            final int currLoadingLength = currList.size();
            int currentTextIndex = -1;
            int cursorY = 20;
            for (int i = currLoadingLength - 1; i > -1; i--) {
                currentTextIndex++;
                final TextLine line = getTextLine(currentTextIndex);
                line.setFont(font);
                line.setText(Lang.getMsg(LOADING_STRING
                    + Integer.toString(currList.get(i))));
                line.setLocation(110, cursorY);
                line.setColor(color);
                line.setCursorVisible(false);
                cursorY += 30;
                line.layout();
            }

            final int doneLoadingLength = doneList.size();
            for (int i = doneLoadingLength - 1; i > -1; i--) {
                currentTextIndex++;
                final TextLine line = getTextLine(currentTextIndex);
                line.setFont(fontDone);
                line.setText(Lang.getMsg(LOADING_STRING
                    + Integer.toString(doneList.get(i)))
                    + Lang.getMsg(LOADING_STRING + "done"));
                line.setLocation(110, cursorY);
                line.setColor(color);
                line.setCursorVisible(false);
                cursorY += 15;
                line.layout();
            }
            dirty = false;
        }

        final int lines = textLines.size();
        for (int i = 0; i < lines; i++) {
            textLines.get(i).render();
        }

        return true;
    }

    /**
     * Set the ID of the client part that is currently loading.
     * 
     * @param part the ID of the client part loading
     */
    public void setCurrentlyLoading(final int part) {
        if (loadingDone) {
            return;
        }
        currList.add(part);
        dirty = true;
    }

    /**
     * Set that the loading is done entirely. That will switch the display black
     * and don't show anything anymore.
     */
    public void setLoadingDone() {
        loadingDone = true;
    }

    /**
     * Set the ID of a client part that is now done loading.
     * 
     * @param part the ID of the part now done
     */
    public void setLoadingDone(final int part) {
        if (loadingDone) {
            return;
        }
        currList.remove(part);
        doneList.add(part);
        dirty = true;
    }

    /**
     * Get the text line of a specified index. In case this index is already set
     * the text line from the list is returned. Else a new line is created.
     * 
     * @param index the index value of the requested line
     * @return the text line returned
     */
    private TextLine getTextLine(final int index) {
        if (index >= textLines.size()) {
            final TextLine line = Graphics.getInstance().getTextLine();
            textLines.add(line);
            return line;
        }
        return textLines.get(index);
    }
}

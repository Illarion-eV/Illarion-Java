/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.graphics;

import de.lessvoid.nifty.slick2d.render.SlickRenderUtils;
import de.lessvoid.nifty.slick2d.render.font.SlickLoadFontException;
import de.lessvoid.nifty.slick2d.render.font.SlickRenderFont;
import illarion.common.util.Rectangle;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * This is the characterName tag that is in special displayed above avatars.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class AvatarTextTag implements Drawable {
    /**
     * The color of the background pane that is displayed behind the characterName.
     */
    private static final Color BACK_COLOR = new Color(0.f, 0.f, 0.f, 0.3f);

    /**
     * This color is used as temporary color during the rendering process.
     */
    private static final de.lessvoid.nifty.tools.Color NIFTY_COLOR =
            new de.lessvoid.nifty.tools.Color(0.f, 0.f, 0.f, 0.f);

    /**
     * The space in pixels between the lines.
     */
    private static final int LINE_SPACE = 0;

    /**
     * The color implementation that is used to render the characterName.
     */
    private Color charNameColor;

    /**
     * The color of the health state of the character.
     */
    private Color healthStateColor;

    /**
     * The height of the avatar that is applied as offset.
     */
    private int avatarHeight;

    /**
     * The x coordinate where the avatar is supposed to be displayed.
     */
    private int displayX;

    /**
     * The y coordinate where the avatar is supposed to be displayed.
     */
    private int displayY;

    /**
     * The text displayed to show the health state of the character.
     */
    private String healthState;

    /**
     * The name of the character that is displayed in this text.
     */
    private String charName;

    /**
     * This flag is set {@code true} in case the tag got changed.
     */
    private boolean dirty;

    /**
     * This flag is set {@code true} in case the dimensions got changed.
     */
    private boolean dimensionsDirty;

    /**
     * The width of this tag. This value is generated once the characterName is set.
     */
    private int width;

    /**
     * The height of this tag. This value is generated once the characterName is set.
     */
    private int height;

    /**
     * Get the height of the characterName tag.
     *
     * @return the height of the characterName tag
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get the width of the characterName tag.
     *
     * @return the width of the characterName tag
     */
    public int getWidth() {
        return width;
    }

    /**
     * Set the location on the screen where the tag is supposed to be displayed.
     *
     * @param x the x coordinate of the location on the screen
     * @param y the y coordinate of the location on the screen
     */
    public void setDisplayLocation(final int x, final int y) {
        if ((displayX == x) && (displayY == y)) {
            return;
        }

        displayX = x;
        displayY = y;
        dirty = true;
    }

    /**
     * Set the color that is used to render the text that shows the character name.
     *
     * @param newColor the color that is used to render the characterName tag.
     */
    public void setCharNameColor(final Color newColor) {
        if ((newColor == null) || newColor.equals(charNameColor)) {
            return;
        }

        charNameColor = newColor;
        dirty = true;
    }

    /**
     * Set the color that is used to render the text that shows the health state of the character.
     *
     * @param newColor the color that is used to render the characterName tag.
     */
    public void setHealthStateColor(final Color newColor) {
        if ((newColor == null) || newColor.equals(healthStateColor)) {
            return;
        }

        healthStateColor = newColor;
        dirty = true;
    }

    /**
     * Set the offset of this characterName tag.
     *
     * @param avaHeight set the height of the avatar this tag is displayed upon
     */
    public void setAvatarHeight(final int avaHeight) {
        if (avatarHeight == avaHeight) {
            return;
        }

        avatarHeight = avaHeight;
        dirty = true;
    }

    /**
     * Set a new character that is shown from now on in the avatar tag.
     *
     * @param newText the new name of the character that is displayed from now on
     */
    public void setCharacterName(final String newText) {
        if (newText.equals(charName)) {
            return;
        }

        charName = newText;
        dimensionsDirty = true;
    }

    /**
     * Set the text that is supposed to be displayed as health state indicator.
     *
     * @param newText the new health state text
     */
    public void setHealthState(final String newText) {
        if ((newText == null) && (healthState == null)) {
            return;
        }
        if ((newText != null) && newText.equals(healthState)) {
            return;
        }

        healthState = newText;
        dimensionsDirty = true;
    }

    private void calculateTextLocations() {
        if (!dimensionsDirty) {
            return;
        }

        final SlickRenderFont font;
        try {
            font = FontLoader.getInstance().getFont(FontLoader.Fonts.small);
        } catch (SlickLoadFontException e) {
            throw new RuntimeException(e);
        }

        final int nameWidth;
        final int nameHeight;
        if (charName == null) {
            nameWidth = 0;
            nameHeight = 0;
        } else {
            nameWidth = font.getWidth(charName);
            nameHeight = font.getHeight();
        }

        final int healthWidth;
        final int healthHeight;
        if (healthState == null) {
            healthWidth = 0;
            healthHeight = 0;
        } else {
            healthWidth = font.getWidth(healthState);
            healthHeight = font.getHeight();
        }

        width = Math.max(healthWidth, nameWidth);
        height = nameHeight + healthHeight;
        if ((nameHeight > 0) && (healthHeight > 0)) {
            height += LINE_SPACE;
        }

        charNameOffsetX = (width - nameWidth) / 2;
        charNameOffsetY = 0;

        healthStateOffsetX = (width - healthWidth) / 2;
        healthStateOffsetY = nameHeight + LINE_SPACE;

        dirty = true;
    }

    private int charNameOffsetX;
    private int charNameOffsetY;
    private int healthStateOffsetX;
    private int healthStateOffsetY;

    @Override
    public boolean draw(final Graphics g) {
        if ((charName == null) && (healthState == null)) {
            return true;
        }

        if (!Camera.getInstance().requiresUpdate(displayRect)) {
            return true;
        }

        final Rectangle parentDirtyArea = Camera.getInstance().getDirtyArea(displayRect);
        if (parentDirtyArea != null) {
            g.setWorldClip(parentDirtyArea.getX(), parentDirtyArea.getY(),
                    parentDirtyArea.getWidth(), parentDirtyArea.getHeight());
        }

        g.setColor(BACK_COLOR);
        g.fillRect(displayRect.getX(), displayRect.getY(), displayRect.getWidth(), displayRect.getHeight());

        final SlickRenderFont font;
        try {
            font = FontLoader.getInstance().getFont(FontLoader.Fonts.small);
        } catch (SlickLoadFontException e) {
            throw new RuntimeException(e);
        }

        if (charName != null) {
            SlickRenderUtils.convertColorSlickNifty(charNameColor, NIFTY_COLOR);
            font.renderText(g, charName, getRenderOriginX() + charNameOffsetX, getRenderOriginY() + charNameOffsetY,
                    NIFTY_COLOR, 1.f, 1.f);
        }

        if (healthState != null) {
            SlickRenderUtils.convertColorSlickNifty(healthStateColor, NIFTY_COLOR);
            font.renderText(g, healthState, getRenderOriginX() + healthStateOffsetX,
                    getRenderOriginY() + healthStateOffsetY, NIFTY_COLOR, 1.f, 1.f);
        }


        if (parentDirtyArea != null) {
            g.clearWorldClip();
        }

        Camera.getInstance().markAreaRendered(displayRect);

        return true;
    }

    private int getRenderOriginX() {
        return displayX - (getWidth() / 2);
    }

    private int getRenderOriginY() {
        return displayY - avatarHeight - getHeight() - 5;
    }

    private final Rectangle displayRect = new Rectangle();
    private final Rectangle oldDisplayRect = new Rectangle();

    @Override
    public Rectangle getLastDisplayRect() {
        return oldDisplayRect;
    }

    @Override
    public void update(final int delta) {
        calculateTextLocations();
        if (dirty) {
            dirty = false;
            oldDisplayRect.set(displayRect);
            displayRect.set(getRenderOriginX() - 1, getRenderOriginY() - 1, width + 2, height + 2);

            Camera.getInstance().markAreaDirty(oldDisplayRect);
            Camera.getInstance().markAreaDirty(displayRect);
        }
    }
}

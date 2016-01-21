/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client.graphics;

import illarion.common.types.DisplayCoordinate;
import illarion.common.types.Rectangle;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.Font;
import org.illarion.engine.graphic.Graphics;
import org.illarion.engine.graphic.ImmutableColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the characterName tag that is in special displayed above avatars.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class AvatarTextTag {
    /**
     * The color of the background pane that is displayed behind the characterName.
     */
    @Nonnull
    private static final Color BACK_COLOR = new Color(0.f, 0.f, 0.f, 0.58f);

    /**
     * The space in pixels between the lines.
     */
    private static final int LINE_SPACE = 0;
    /**
     * The font used to draw the text tag.
     */
    @Nonnull
    private final Font font;
    @Nonnull
    private final Rectangle displayRect = new Rectangle();
    /**
     * The color implementation that is used to render the characterName.
     */
    @Nullable
    private Color charNameColor;
    /**
     * The color of the health state of the character.
     */
    @Nullable
    private Color healthStateColor;
    /**
     * The height of the avatar that is applied as offset.
     */
    private int avatarHeight;
    /**
     * The coordinates where the tag is supposed to be displayed.
     */
    @Nullable
    private DisplayCoordinate displayCoordinate;
    /**
     * The text displayed to show the health state of the character.
     */
    @Nullable
    private String healthState;
    /**
     * The name of the character that is displayed in this text.
     */
    private String charName;
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
    private int charNameOffsetX;
    private int charNameOffsetY;
    private int healthStateOffsetX;
    private int healthStateOffsetY;

    /**
     * Default constructor.
     */
    public AvatarTextTag() {
        font = FontLoader.getInstance().getFont(FontLoader.SMALL_FONT);
    }

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
     * @param coordinate the coordinate of the location on the screen
     */
    public void setDisplayLocation(@Nonnull DisplayCoordinate coordinate) {
        displayCoordinate = coordinate;
    }

    /**
     * Set the color that is used to render the text that shows the character name.
     *
     * @param newColor the color that is used to render the characterName tag.
     */
    public void setCharNameColor(@Nonnull Color newColor) {
        if (newColor instanceof ImmutableColor) {
            charNameColor = newColor;
        } else if ((charNameColor == null) || charNameColor.equals(newColor)) {
            charNameColor = new ImmutableColor(newColor);
        }
    }

    /**
     * Set the color that is used to render the text that shows the health state of the character.
     *
     * @param newColor the color that is used to render the characterName tag.
     */
    public void setHealthStateColor(@Nonnull Color newColor) {
        if (newColor instanceof ImmutableColor) {
            healthStateColor = newColor;
        } else if ((healthStateColor == null) || healthStateColor.equals(newColor)) {
            healthStateColor = new ImmutableColor(newColor);
        }
    }

    /**
     * Set the offset of this characterName tag.
     *
     * @param avaHeight set the height of the avatar this tag is displayed upon
     */
    public void setAvatarHeight(int avaHeight) {
        if (avatarHeight == avaHeight) {
            return;
        }

        avatarHeight = avaHeight;
    }

    /**
     * Set a new character that is shown from now on in the avatar tag.
     *
     * @param newText the new name of the character that is displayed from now on
     */
    public void setCharacterName(@Nonnull String newText) {
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
    public void setHealthState(@Nullable String newText) {
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
        if (!dimensionsDirty || (displayCoordinate == null)) {
            return;
        }

        int nameWidth;
        int nameHeight;
        if (charName == null) {
            nameWidth = 0;
            nameHeight = 0;
        } else {
            nameWidth = font.getWidth(charName);
            nameHeight = font.getLineHeight();
        }

        int healthWidth;
        int healthHeight;
        if (healthState == null) {
            healthWidth = 0;
            healthHeight = 0;
        } else {
            healthWidth = font.getWidth(healthState);
            healthHeight = font.getLineHeight();
        }

        width = Math.max(healthWidth, nameWidth);
        height = nameHeight + healthHeight;
        if ((nameHeight > 0) && (healthHeight > 0)) {
            height += LINE_SPACE;
        }

        charNameOffsetX = (width - nameWidth) / 2;
        charNameOffsetY = 0;

        healthStateOffsetX = (width - healthWidth) / 2;
        healthStateOffsetY = nameHeight;

        displayRect.set(displayCoordinate.getX() - (getWidth() / 2),
                        displayCoordinate.getY() - avatarHeight - getHeight() - 5, width, height);
    }

    public boolean render(@Nonnull Graphics g) {
        if ((charName == null) && (healthState == null)) {
            return true;
        }

        if (!Camera.getInstance().requiresUpdate(displayRect)) {
            return true;
        }

        g.drawRectangle(displayRect, BACK_COLOR);

        if ((charName != null) && (charNameColor != null)) {
            g.drawText(font, charName, charNameColor, displayRect.getX() + charNameOffsetX,
                       displayRect.getY() + charNameOffsetY);
        }

        if ((healthState != null) && (healthStateColor != null)) {
            g.drawText(font, healthState, healthStateColor, displayRect.getX() + healthStateOffsetX,
                       displayRect.getY() + healthStateOffsetY);
        }

        return true;
    }

    @Nonnull
    public Rectangle getDisplayRect() {
        return displayRect;
    }

    public void update(@Nonnull GameContainer container, int delta) {
        calculateTextLocations();
    }
}

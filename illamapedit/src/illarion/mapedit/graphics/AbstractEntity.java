/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute i and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Mapeditor is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Mapeditor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.graphics;

import java.awt.Image;
import java.awt.image.BufferedImage;

import gnu.trove.map.hash.TIntObjectHashMap;

import illarion.mapedit.MapEditor;

import illarion.common.util.FastMath;
import illarion.common.util.Location;
import illarion.common.util.Rectangle;
import illarion.common.util.RecycleObject;

import illarion.graphics.Graphics;
import illarion.graphics.Sprite;
import illarion.graphics.SpriteColor;
import illarion.graphics.Texture;
import illarion.graphics.common.SpriteBuffer;

public abstract class AbstractEntity implements DisplayItem, RecycleObject {
    /**
     * The color that is used in case a tile or a item blocks the tile.
     */
    protected static final SpriteColor BLOCKED_COLOR;

    /**
     * A instance of sprite color used to calculate light changes.
     */
    protected static final SpriteColor CALC_COLOR = Graphics.getInstance()
        .getSpriteColor();

    /**
     * The color that is added to the current color in case the tile is
     * selected.
     */
    protected static final SpriteColor SELECTED_COLOR;

    /**
     * The storage of the image maps loaded as textures.
     */
    private static TIntObjectHashMap<BufferedImage> TEX_IMAGES =
        new TIntObjectHashMap<BufferedImage>();

    static {
        BLOCKED_COLOR = Graphics.getInstance().getSpriteColor();
        BLOCKED_COLOR.set(1.f, 0.f, 0.f);
        BLOCKED_COLOR.setAlpha(1.f);

        SELECTED_COLOR = Graphics.getInstance().getSpriteColor();
        SELECTED_COLOR.set(0.f, 1.f, 0.f);
        SELECTED_COLOR.setAlpha(1.f);
    }

    /**
     * Displayed frame.
     */
    private int currentFrame;

    /**
     * The current x location of this item on the screen relative to the origin
     * of the game map.
     */
    private int displayX = 0;

    /**
     * The current y location of this item on the screen relative to the origin
     * of the game map.
     */
    private int displayY = 0;

    /**
     * The ID of the entity. This is the ID the entity is stored in the recycle
     * factory with.
     */
    private int entityID;

    /**
     * The icon to display the the non openGL part of the GUI.
     */
    private final Image icon;

    /**
     * The z order of the item, so the layer of the item that determines the
     * position of the object in the display list.
     */
    private int layerZ;

    /**
     * The light that effects this entity directly. That could be the
     * {@link #DEFAULT_LIGHT} that ensures that the object is displayed with its
     * real colors or the ambient light of the weather that ensures that the
     * object is colored for the display on the map.
     */
    private SpriteColor localLight;

    /**
     * The scaling value that is applied to this entity.
     */
    private float scale;

    private Selectable selectParent;

    /**
     * A flag if this object is currently shown on the screen.
     */
    private boolean shown = false;

    /**
     * The sprite that is the actual graphical representation of the entity.
     */
    private final Sprite sprite;

    /**
     * The frame displayed by default.
     */
    private final int stillFrame;

    /**
     * This flag stores if the scaling value is to be used.
     */
    private boolean useScale;

    /**
     * Copy constructor to dublicate the object. This creates a copy of a entity
     * that is able to render in exactly the same way.
     * 
     * @param org the original entity that shall be copied
     */
    protected AbstractEntity(final AbstractEntity org) {
        // use same sprite as other entity
        sprite = org.sprite;
        entityID = org.entityID;
        icon = org.icon;
        currentFrame = org.currentFrame;
        stillFrame = org.stillFrame;
        reset();
    }

    /**
     * Create a new entity that is display able on the map.
     * 
     * @param id the ID of the entity
     * @param path the path to the graphic of the entity
     * @param name the texture name of the entity
     * @param frames the amount of frames for this sprite
     * @param still the amount of still frames for this sprite
     * @param offX the x offset of this entity
     * @param offY the y offset of this entity
     * @param horz the horizontal align of this entity
     * @param vert the vertical align of this entity
     */
    protected AbstractEntity(final int id, final String path,
        final String name, final int frames, final int still, final int offX,
        final int offY, final Sprite.HAlign horz, final Sprite.VAlign vert) {

        sprite =
            SpriteBuffer.getInstance().getSprite(path, name, frames, offX,
                offY, horz, vert, true, false);
        entityID = id;
        stillFrame = still;

        final Texture tex = sprite.getTexture(stillFrame);

        BufferedImage parentImage;
        if (TEX_IMAGES.contains(tex.getTextureID())) {
            parentImage = TEX_IMAGES.get(tex.getTextureID());
        } else {
            parentImage = tex.getParent().getTextureImage();
            TEX_IMAGES.put(tex.getTextureID(), parentImage);
        }

        icon =
            parentImage.getSubimage(tex.getImageX(), tex.getImageY(),
                tex.getImageWidth(), tex.getImageHeight());

        reset();
    }

    /**
     * Clean up the data from the entity that is not needed any longer after
     * loading it up.
     */
    public static void cleanup() {
        TEX_IMAGES.clear();
        TEX_IMAGES = null;
    }

    /**
     * Activate this entity. The entity may be requested with a new ID due some
     * mappings of the recycle factory. So set the new ID of the entity on this
     * entity instance.
     * <p>
     * In case this must not be done, overwrite this method.
     * </p>
     */
    @Override
    public void activate(final int requestID) {
        entityID = requestID;
    }

    /**
     * Create a clone from the this instance.
     */
    @Override
    public abstract AbstractEntity clone();

    /**
     * Draw the object on the screen.
     * 
     * @return true in case the render operation was performed correctly
     */
    @Override
    public boolean draw() {
        if (isSelected()) {
            CALC_COLOR.set(SELECTED_COLOR);
        } else if (MapEditor.getDisplay().getSettingsSpecial() == MapDisplay.SpecialDisplay.light) {
            CALC_COLOR.set(localLight);
        } else if ((MapEditor.getDisplay().getSettingsSpecial() == MapDisplay.SpecialDisplay.blocked)
            && isBlocked()) {
            CALC_COLOR.set(BLOCKED_COLOR);
        } else {
            CALC_COLOR.set(sprite.getDefaultLight());
        }
        if (!isSelectable()) {
            CALC_COLOR.multiply(0.5f);
        }
        return draw(CALC_COLOR);
    }

    /**
     * Get the rectangle that descripes the size of this entity of the screen.
     * 
     * @return the border frame of this entity
     */
    public Rectangle getBorderRectangle() {
        final Rectangle retRect = Rectangle.getInstance();

        retRect.set(displayX + sprite.getScaledOffsetX(scale), displayY
            + sprite.getScaledOffsetY(scale),
            (int) (sprite.getWidth() * scale),
            (int) (sprite.getHeight() * scale));
        return retRect;
    }

    /**
     * Get the amount of frames stored in this entity.
     * 
     * @return the amount of frames his entity has
     */
    public int getFrames() {
        return sprite.getFrames();
    }

    /**
     * Get the icon of this entity for the display outside of the openGL screen.
     * 
     * @return the icon of this entity
     */
    public Image getIcon() {
        return icon;
    }

    /**
     * Get the ID of the entity.
     * 
     * @return the ID of the entity
     */
    @Override
    public final int getId() {
        return entityID;
    }

    public abstract String getName();

    /**
     * Get the scaling value that is applied to the entity.
     * 
     * @return the scaling value applied to the entity
     */
    public float getScale() {
        return scale;
    }

    /**
     * Get the Z Order of this entity that marks the position in the display
     * list and selects this way, how other images overlay this entity.
     * 
     * @return the layer of this entity
     */
    @Override
    public final int getZOrder() {
        return layerZ;
    }

    /**
     * Hide the entity in the render environment.
     */
    @Override
    public void hide() {
        if (shown) {
            shown = false;
            MapEditor.getDisplay().remove(this);
            updateGraphic();
        }
    }

    public boolean isBlocked() {
        if (selectParent == null) {
            return false;
        }
        return selectParent.isBlocked();
    }

    public boolean isSelectable() {
        if (selectParent == null) {
            return true;
        }
        return selectParent.isSelectable();
    }

    public boolean isSelected() {
        if (selectParent == null) {
            return false;
        }
        return selectParent.isSelected();
    }

    /**
     * Cleanup the entity before placing it back to the recycle factory.
     */
    @Override
    public void reset() {
        currentFrame = stillFrame;
        setScale(1.f);
        localLight = sprite.getDefaultLight();
        selectParent = null;
        hide();
    }

    /**
     * Set the frame that is displayed of this entity.
     * 
     * @param newFrame the displayed frame
     */
    public void setFrame(final int newFrame) {
        currentFrame = newFrame;
    }

    /**
     * Set the light this entity is colored in.
     * 
     * @param newLight the new instance of light this entity is colored with
     * @throws IllegalArgumentException in case the newLight argument is
     *             <code>null</code>
     */
    public void setLight(final SpriteColor newLight) {
        if (newLight == null) {
            localLight = sprite.getDefaultLight();
            return;
        }
        localLight = newLight;
    }

    /**
     * Set the scaling that shall be applied to this entity.
     * 
     * @param newScale the new scaling value applied to this entity
     */
    public void setScale(final float newScale) {
        scale = newScale;
        useScale = (FastMath.abs(1.f - scale) > FastMath.FLT_EPSILON);
    }

    /**
     * Set the position of the entity on the display. The display origin is at
     * the origin of the game map.
     * 
     * @param dispX the x coordinate of the location of the display
     * @param dispY the y coordinate of the location of the display
     * @param zLayer the z layer of the coordinate
     * @param typeLayer the global layer of this type of entity.
     */
    public void setScreenPos(final int dispX, final int dispY,
        final int zLayer, final int typeLayer) {
        displayX = dispX;
        displayY = dispY;

        if (shown) {
            final int newLayerZ = zLayer - typeLayer;
            if (newLayerZ != layerZ) {
                layerZ = newLayerZ;
            }
        } else {
            layerZ = zLayer - typeLayer;
        }
    }

    /**
     * Set the position of the entity on the display. The display origin is at
     * the origin of the game map.
     * 
     * @param loc the location of the entity on the map
     * @param typeLayer the global layer of this type of entity.
     */
    public final void setScreenPos(final Location loc, final int typeLayer) {
        setScreenPos(loc.getDcX(), loc.getDcY(), loc.getDcZ(), typeLayer);
    }

    public void setSelectable(final Selectable newSelectable) {
        selectParent = newSelectable;
    }

    /**
     * Show the entity in the render environment.
     */
    @Override
    public void show() {
        if (!shown) {
            shown = true;
            MapEditor.getDisplay().add(this);
            updateGraphic();
        }
    }

    public void updateGraphic() {
        int x = displayX + sprite.getScaledOffsetX(scale);
        x += MapEditor.getDisplay().getOffsetX();
        x *= MapEditor.getDisplay().getZoom();

        int y = displayY + sprite.getScaledOffsetY(scale);
        y += MapEditor.getDisplay().getOffsetY();
        y *= MapEditor.getDisplay().getZoom();
        y =
            Graphics.getInstance().getRenderDisplay().getRenderArea()
                .getHeight()
                - y;

        int w = (int) (sprite.getWidth() * scale);
        w *= MapEditor.getDisplay().getZoom();

        int h = (int) (sprite.getHeight() * scale);
        h *= MapEditor.getDisplay().getZoom();

        Graphics.getInstance().getRenderDisplay().getRenderArea()
            .repaint(x, y, w, h);
    }

    /**
     * Draw the object on the screen.
     * 
     * @param overwriteColor the color used to render the image
     * @return true in case the render operation was performed correctly
     */
    protected boolean draw(final SpriteColor overwriteColor) {
        if (useScale) {
            sprite.draw(displayX, displayY, overwriteColor, currentFrame,
                scale);
        } else {
            sprite.draw(displayX, displayY, overwriteColor, currentFrame);
        }

        return true;
    }

    protected int getDiplsayY() {
        return displayY;
    }

    protected int getDisplayX() {
        return displayX;
    }
}

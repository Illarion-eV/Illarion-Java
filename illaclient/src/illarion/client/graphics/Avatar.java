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

import java.awt.Rectangle;

import illarion.client.util.Lang;
import illarion.client.world.Game;

import illarion.graphics.Graphics;
import illarion.graphics.Sprite;
import illarion.graphics.SpriteColor;

/**
 * Class for the avatar of a characters. The avatar is the visual representation
 * of a character on a map. All characters, including monsters and NPCs have a
 * avatar.
 * 
 * @author Nop
 * @author Martin Karing
 * @since 0.95
 * @version 1.22
 */
public final class Avatar extends AbstractEntity {
    /**
     * The resource path to the avatar graphics. All graphics need to be located
     * at this path within the JAR-resource files.
     */
    private static final String CHAR_PATH = "data/chars/"; //$NON-NLS-1$

    /**
     * The minimal alpha value of a avatar that is needed to show the name tag
     * above the avatar graphic.
     */
    private static final int HIDE_NAME_ALPHA =
        (int) (0.5f * SpriteColor.COLOR_MAX);

    /**
     * The minimal illumination that is still needed to show the name of a
     * character above the avatar. If the illumination is lower then this value,
     * the name is hidden.
     */
    private static final int HIDE_NAME_LUM =
        (int) (0.25f * SpriteColor.COLOR_MAX);

    /**
     * The frame animation that handles the animation of this avatar.
     */
    private transient final FrameAnimation ani;

    /**
     * In case the light shall be animated this value is set to true. In special
     * cases its not good if the light is animated, such as the switch of levels
     * and the sudden appearance of characters on the map. In such cases
     */
    private boolean animateLight;

    /**
     * The clothes this avatar can wear.
     */
    private transient final AvatarClothManager clothes;

    /**
     * The render system for the clothes of this avatar.
     */
    private transient final AvatarClothRenderer clothRender;

    /**
     * The information data of the avatar. This offers the possibility to send a
     * set of informations about the description of the characters avatar in
     * both languages and the visibility modifier informations to other classes.
     */
    private transient final AvatarInfo info;

    /**
     * The text tag is the small text box shown above the avatar that contains
     * the name of the avatar.
     */
    private TextTag name;

    /**
     * The x offset value for the image of this avatar. This values are needed
     * to calculate the relative offsets for the clothes.
     */
    private final int offsetX;

    /**
     * The y offset value for the image of this avatar. This values are needed
     * to calculate the relative offsets for the clothes.
     */
    private final int offsetY;

    /**
     * The character that created this avatar.
     */
    private Animated parentChar;

    /**
     * Stores if the name shall be rendered or not. It is checked at every
     * update if this flag is valid or not.
     */
    private boolean renderName = false;

    /**
     * The target light of this avatar. In case the light is set to be animated
     * the color this avatar is rendered with will approach this target light.
     */
    private transient SpriteColor targetLight;

    /**
     * This color is used to temporary store the local light of the avatar up
     * until the first change of this color. This is needed to ensure that the
     * correct light instance is used all times.
     */
    private transient final SpriteColor tempColor = Graphics.getInstance()
        .getSpriteColor();

    /**
     * Create animated avatar for a character.
     * 
     * @param avatarID the id of the avatar, the id needs to by unique per
     *            race/sex/direction combination
     * @param resName the name of the avatar, needs to fit to the name of the
     *            resource files with the images for this avatar
     * @param frames the count of frames for the animation of the character
     * @param still the still frame, so the frame that is shown in case the
     *            character does not move
     * @param offX the offset in x direction in pixels, so the amount of pixels
     *            the graphic is moved from its origin
     * @param offY the offset in y direction in pixels, so the amount of pixels
     *            the graphic is moved from its origin
     * @param shadowOffset the shadow offset so the amount of pixels the width
     *            of the image is lowered so the image does not fade out in case
     *            someone steps into its shadow
     * @param avatarInfo the avatar information data, such as the name of the
     *            avatar in German and English and the visibility modifier of
     *            the avatar
     * @param mirror show the avatar graphic horizontal mirrored
     * @param color the color that local light is by default modified with in
     *            order to get the proper render color of the Avatar
     * @param dir the direction the avatar is looking at
     */
    @SuppressWarnings("nls")
    protected Avatar(final int avatarID, final String resName,
        final int frames, final int still, final int offX, final int offY,
        final int shadowOffset, final AvatarInfo avatarInfo,
        final boolean mirror, final SpriteColor color, final int dir) {
        super(avatarID, CHAR_PATH, resName, frames, still, offX, offY,
            shadowOffset, Sprite.HAlign.center, Sprite.VAlign.bottom, true,
            mirror, color);

        if (avatarInfo == null) {
            throw new IllegalArgumentException(
                "Avatar informations may not be NULL");
        }

        targetLight = DEFAULT_LIGHT;
        animateLight = false;
        info = avatarInfo;
        clothes = new AvatarClothManager();
        clothRender = new AvatarClothRenderer(dir, frames);
        clothRender.setLight(getLight());
        clothRender.setFrame(0);
        offsetX = offX;
        offsetY = offY;
        if (frames > 1) {
            ani = new FrameAnimation(this);
            ani.setup(frames, still, 1, 0);
        } else {
            ani = null;
        }
        reset();
    }

    /**
     * Copy constructor. Create a copy of the current instance of the avatar
     * into a new avatar object.
     * 
     * @param org the avatar object that shall be copied
     */
    private Avatar(final Avatar org) {
        super(org);
        info = org.info;
        clothes = org.clothes;
        clothRender = new AvatarClothRenderer(org.clothRender);
        clothRender.setLight(getLight());
        clothRender.setFrame(0);
        offsetX = org.offsetX;
        offsetY = org.offsetY;

        if (org.ani == null) {
            ani = null;
        } else {
            ani = new FrameAnimation(this, org.ani);
        }
        reset();
    }

    /**
     * Create a avatar from the avatar factory. This either creates a new
     * instance of the avatar class or it takes a existing instance from the
     * list of currently unused instances.
     * 
     * @param avatarID the ID of the character that identifies the name and the
     *            sex and the direction of the avatar that is needed
     * @return a instance of the needed avatar type
     */
    public static Avatar create(final int avatarID) {
        return AvatarFactory.getInstance().getCommand(avatarID);
    }

    /**
     * Activate the avatar instance for usage. This needs to be done right after
     * the avatar instance got created.
     * 
     * @param newID doesn't do anything since the ID of this object is related
     *            to other things that can't be change
     */
    @Override
    public void activate(final int newID) {
        // nothing needs to be done
    }

    /**
     * Start a animation for this avatar.
     * 
     * @param speed the speed of the animation, the larger this value, the
     *            longer the animation takes to finish
     * @param parent the parent character that triggered the animation and needs
     *            to be notified when its finished
     * @param loop true in case the animation shall never stop and rather run
     *            forever
     */
    public void animate(final int speed, final Animated parent,
        final boolean loop) {
        if (ani == null) {
            return;
        }

        parentChar = parent;

        ani.updateSpeed(speed);
        if (loop) {
            ani.updateMode(FrameAnimation.LOOPED);
        } else {
            ani.updateMode(FrameAnimation.CYCLIC);
        }
        ani.restart();
    }

    /**
     * This function is triggered in case a animation that is not looped
     * finished.
     * 
     * @param finished set true in case the animation is really done
     */
    @Override
    public void animationFinished(final boolean finished) {
        if (parentChar != null) {
            parentChar.animationFinished(true);
        }
    }

    /**
     * Change the color of one paperdolling object.
     * 
     * @param slot the slot of the object that shall get a different color
     * @param color the new color that shall be used to color the graphic itself
     */
    public void changeClothColor(final int slot, final SpriteColor color) {
        clothRender.changeBaseColor(slot, color);
    }

    /**
     * Create a duplicate of this avatar instance. The returned object is a
     * exact copy of the current avatar instance.
     * 
     * @return the copy of the current avatar instance
     */
    @Override
    public Avatar clone() {
        return new Avatar(this);
    }

    /**
     * Check if a cloth item is defined in a specified group.
     * 
     * @param group the group where the item shall be searched in
     * @param itemID the item id that shall be checked
     * @return <code>true</code> in case the item is defined and renderable
     */
    public boolean clothItemExist(final int group, final int itemID) {
        return clothes.clothExists(group, itemID);
    }

    /**
     * Draw the avatar to the game screen. Calling this function causes the
     * light value to approach the target light in case the light values are
     * different. It also draws the name above the avatar in case it needs to be
     * shown.
     * 
     * @return true at all times
     */
    @Override
    public boolean draw() {
        // draw the avatar, naked!! :O
        super.draw();

        // draw the clothes
        clothRender.render();

        if (renderName && (name != null)) {
            name.draw(getDisplayX(), getDisplayY());
        }

        return true;
    }

    /**
     * Check if the avatar is able to show a special animation.
     * 
     * @param animationID the ID of the animation that shall be checked
     * @return true in case the animation is available
     */
    public boolean getAnimationAvaiable(final int animationID) {
        return info.animationAvaiable(animationID);
    }

    /**
     * Get the manager that holds all clothes this avatar can wear.
     * 
     * @return the cloth manager with all clothes the avatar can wear
     */
    public AvatarClothManager getClothes() {
        return clothes;
    }

    /**
     * Get the description of the avatar. This description can be used for this
     * name display above the avatar image. The returned string is already the
     * localized version.
     * 
     * @return the description text of the avatar, in German or English
     *         regarding the localising settings the client runs with
     */
    public String getDescription() {
        if (Lang.getInstance().isGerman()) {
            return info.getGerman();
        }
        return info.getEnglish();
    }

    /**
     * Get the x offset of this avatar.
     * 
     * @return the x offset of his avatar
     */
    public int getOffsetX() {
        return offsetX;
    }

    /**
     * Get the y offset of this avatar.
     * 
     * @return the y offset of his avatar
     */
    public int getOffsetY() {
        return offsetY;
    }

    /**
     * Get the bounding rectangle of the image of this avatar. This includes the
     * height, width and the location of the image on the screen. The location
     * that is set within the rectangle is the location of the origin of the
     * avatar so the rectangle does not mark the borders of the avatar for sure.
     * 
     * @return the rectangle of the the avatar image
     * @deprecated better use {@link #getRectangle(Rectangle)} to avoid the
     *             creation of too many instances of the rectangle object. This
     *             function creates a new instance of rectangle right away.
     */
    @Deprecated
    public Rectangle getRectangle() {
        final Rectangle retRect = new Rectangle();
        getRectangle(retRect);
        return retRect;
    }

    /**
     * Get the bounding rectangle of the image of this avatar. This includes the
     * height, width and the location of the image on the screen. The location
     * that is set within the rectangle is the location of the origin of the
     * avatar so the rectangle does not mark the borders of the avatar for sure.
     * 
     * @param targetRectangle the rectangle object that is the target of the
     *            bounding rectangle. The bounding data is set to this
     *            rectangle.
     */
    public void getRectangle(final Rectangle targetRectangle) {
        targetRectangle.x = getDisplayX() - (getWidth() >> 1);
        targetRectangle.y = getDisplayY();
        targetRectangle.width = getWidth();
        targetRectangle.height = getHeight();
    }

    /**
     * Get the visibility modifier of the avatar.
     * 
     * @return the visibility modifier of the avatar, the value is handled as
     *         percent value
     */
    public int getVisibility() {
        return info.getVisibility();
    }

    /**
     * Check if the light is currently animated. Means the light is currently
     * changing towards a target light color.
     * 
     * @return true in case the light is currently animated
     */
    public boolean hasAnimatedLight() {
        return animateLight;
    }

    /**
     * Recycle the avatar object. This causes that the animations of this avatar
     * are stopped and the name tag gets recycled as well. The object is cleaned
     * up for later reuse. After calling this function, do not use this avatar
     * instance anymore.
     */
    @Override
    public void recycle() {
        hide();
        parentChar = null;
        stopAnimation();
        if (name != null) {
            name.recycle();
            name = null;
        }
        AvatarFactory.getInstance().recycle(this);
    }

    /**
     * Remove a item from the list of items that are shown as clothes.
     * 
     * @param group the group that shall be cleaned
     */
    public void removeClothItem(final int group) {
        clothRender.setCloth(group, null);
    }

    /**
     * Clean up the avatar instance. That needs to be done before the avatar is
     * removed from the time and put back into the factory for later reuse.
     * <p>
     * This function is called automatically in case the avatar object is
     * recycled.
     * </p>
     */
    @Override
    public void reset() {
        super.reset();
        clothRender.clear();
        clothRender.setLight(getLight());
        animateLight = false;
    }

    /**
     * Set a item as a clothing item to a specified body location. In case its
     * defined the cloth renderer will try to show the cloth on the avatar.
     * 
     * @param group the group of the item, so the location of the item, where it
     *            shall be displayed
     * @param itemID the ID of the item that shall be displayed
     */
    public void setClothItem(final int group, final int itemID) {
        clothRender.setCloth(group, clothes.getCloth(group, itemID));
    }

    /**
     * Set the current frame of the avatar. This forwards the frame to the
     * Entity super function but sends it also to the cloth render.
     * 
     * @param frame the index of the frame that shall be rendered next
     */
    @Override
    public void setFrame(final int frame) {
        super.setFrame(frame);
        clothRender.setFrame(frame);
    }

    /**
     * Set the light this avatar is colored with. Setting the light with this
     * function will disable the smooth change of the light and sets the light
     * color right away.
     * 
     * @param light the light the avatar is enlighten with
     */
    @Override
    public void setLight(final SpriteColor light) {
        super.setLight(light);
        clothRender.setLight(light);
        animateLight = false;
    }

    /**
     * Set the light this avatar is colored with. Setting the light with this
     * function will enable the smooth change of the light and so the light
     * color of the avatar will slowly approach the color of the light set with
     * this function.
     * 
     * @param light the target light color for this avatar
     */
    public void setLightTarget(final SpriteColor light) {
        targetLight = light;
        final SpriteColor currLight = getLight();
        tempColor.set(currLight);
        setLight(tempColor);
        clothRender.setLight(tempColor);
        animateLight = true;
    }

    /**
     * Set the name that is displayed in the tag above the avatar graphic.
     * 
     * @param charName the name that is displayed above the character graphic
     */
    public void setName(final String charName) {
        if (charName.length() == 0) {
            if (name != null) {
                name.recycle();
                name = null;
            }
            return;
        }
        if (name == null) {
            name = TextTag.create();
        }
        name.setText(charName);
        name.setColor(Colors.yellow);
        name.setOffset(-name.getWidth() / 2, getHeight());
    }

    /**
     * Set the color of the text that is shown above the avatar that is shown.
     * 
     * @param color the color that is used for the font of the the text that is
     *            shown above the character and shows the name of the character
     * @see illarion.client.graphics.Colors
     */
    public void setNameColor(final Colors color) {
        if (name == null) {
            return;
        }

        name.setColor(color);
    }

    /**
     * Set the scaling value of the avatar. Possible values are between 0.5f and
     * 1.2f. The size is applied to the height and the width of the avatar
     * images since scaling height and width independent from each other would
     * look crap.
     * 
     * @param newScale the new scale value of the avatar image. Values between
     *            0.5f and 1.2f are valid
     */
    @Override
    public void setScale(final float newScale) {
        super.setScale(newScale);
        clothRender.setScale(newScale);
    }

    /**
     * Set the location on the screen.
     * 
     * @param posX the x coordinate of the location on the screen
     * @param posY the y coordinate of the location on the screen
     * @param layerZ the z coordinate, so the layer on the screen
     * @param groupLayer the global layer value of the graphic type
     */
    @Override
    public void setScreenPos(final int posX, final int posY, final int layerZ,
        final int groupLayer) {
        super.setScreenPos(posX, posY, layerZ, groupLayer);
        clothRender.setScreenLocation(posX, posY, layerZ, groupLayer);
    }

    /**
     * Update the values of this avatar entity. The light values, the alpha
     * values as well as the name display is checked using this function.
     * 
     * @param delta the time since the last update in milliseoncs
     */
    @Override
    public void update(final int delta) {
        updateAlpha(delta);

        clothRender.setAlpha(getAlpha());

        final SpriteColor locLight = getLight();
        if (animateLight && (locLight != null)
            && locLight.approach(targetLight)) {
            targetLight = locLight;
            animateLight = false;
        }

        if ((getAlpha() > HIDE_NAME_ALPHA) && (Game.getPeople() != null)
            && (Game.getPeople().getShowMapNames() > 0) && (name != null)
            && (locLight != null)
            && (locLight.getLuminationi() > HIDE_NAME_LUM)) {
            if (!renderName) {
                name.addToCamera(getDisplayX(), getDisplayY());
            }
            renderName = true;
        } else if (name != null) {
            if (renderName) {
                name.addToCamera(getDisplayX(), getDisplayY());
            }
            renderName = false;
        }
    }

    /**
     * Cause the current animation of the avatar to stop instantly. The parent
     * character is removed and not notified.
     */
    private void stopAnimation() {
        parentChar = null;
        if (ani != null) {
            ani.stop();
        }
    }
}

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

import illarion.client.input.ClickOnMapEvent;
import illarion.client.resources.CharacterFactory;
import illarion.client.resources.GuiImageFactory;
import illarion.client.resources.Resource;
import illarion.client.util.Lang;
import illarion.client.world.Char;
import illarion.client.world.CombatHandler;
import illarion.common.annotation.NonNull;
import illarion.common.annotation.Nullable;
import org.apache.log4j.Logger;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;


/**
 * Class for the avatar of a characters. The avatar is the visual representation
 * of a character on a map. All characters, including monsters and NPCs have a
 * avatar.
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class Avatar extends AbstractEntity implements Resource {
    /**
     * The resource path to the avatar graphics. All graphics need to be located
     * at this path within the JAR-resource files.
     */
    private static final String CHAR_PATH = "data/chars/"; //$NON-NLS-1$

    /**
     * The minimal alpha value of a avatar that is needed to show the name tag
     * above the avatar graphic.
     */
    private static final float HIDE_NAME_ALPHA = 0.5f;

    /**
     * The minimal illumination that is still needed to show the name of a
     * character above the avatar. If the illumination is lower then this value,
     * the name is hidden.
     */
    private static final float HIDE_NAME_LUM = 0.25f;

    /**
     * The frame animation that handles the animation of this avatar.
     */
    private final transient FrameAnimation ani;

    /**
     * In case the light shall be animated this value is set to true. In special
     * cases its not good if the light is animated, such as the switch of levels
     * and the sudden appearance of characters on the map. In such cases
     */
    private boolean animateLight;

    /**
     * The clothes this avatar can wear.
     */
    private final transient AvatarClothManager clothes;

    /**
     * The render system for the clothes of this avatar.
     */
    private final transient AvatarClothRenderer clothRender;

    /**
     * The mark that is displayed in case the character is the target of a attack.
     */
    private final AvatarMarker attackMark;

    /**
     * The information data of the avatar. This offers the possibility to send a
     * set of information about the description of the characters avatar in
     * both languages and the visibility modifier information to other classes.
     */
    private final transient AvatarInfo info;

    /**
     * The text tag is the small text box shown above the avatar that contains
     * the name of the avatar.
     */
    private final AvatarTextTag tag;

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
    private Char parentChar;

    public boolean isAttackMarkerVisible() {
        return attackMarkerVisible;
    }

    public void setAttackMarkerVisible(final boolean attackMarkerVisible) {
        this.attackMarkerVisible = attackMarkerVisible;
    }

    /**
     * This variable changes to true in case the attack marker is supposed to be displayed.
     */
    private boolean attackMarkerVisible;

    /**
     * Stores if the name shall be rendered or not. It is checked at every
     * update if this flag is valid or not.
     */
    private boolean renderName = false;

    /**
     * The target light of this avatar. In case the light is set to be animated
     * the color this avatar is rendered with will approach this target light.
     */
    private transient Color targetLight;

    /**
     * Create animated avatar for a character.
     *
     * @param avatarID     the id of the avatar, the id needs to by unique per
     *                     race/sex/direction combination
     * @param resName      the name of the avatar, needs to fit to the name of the
     *                     resource files with the images for this avatar
     * @param frames       the count of frames for the animation of the character
     * @param still        the still frame, so the frame that is shown in case the
     *                     character does not move
     * @param offX         the offset in x direction in pixels, so the amount of pixels
     *                     the graphic is moved from its origin
     * @param offY         the offset in y direction in pixels, so the amount of pixels
     *                     the graphic is moved from its origin
     * @param shadowOffset the shadow offset so the amount of pixels the width
     *                     of the image is lowered so the image does not fade out in case
     *                     someone steps into its shadow
     * @param avatarInfo   the avatar information data, such as the name of the
     *                     avatar in German and English and the visibility modifier of
     *                     the avatar
     * @param mirror       show the avatar graphic horizontal mirrored
     * @param color        the color that local light is by default modified with in
     *                     order to get the proper render color of the Avatar
     * @param dir          the direction the avatar is looking at
     */
    @SuppressWarnings("nls")
    public Avatar(final int avatarID, final String resName,
                  final int frames, final int still, final int offX, final int offY,
                  final int shadowOffset, final AvatarInfo avatarInfo,
                  final boolean mirror, final Color color, final int dir) {
        super(avatarID, CHAR_PATH, resName, frames, still, offX, offY, shadowOffset, Sprite.HAlign.center,
                Sprite.VAlign.bottom, true, mirror, color);

        if (avatarInfo == null) {
            throw new IllegalArgumentException("Avatar informations may not be NULL");
        }

        final Sprite attackMarkSprite = GuiImageFactory.getInstance().getObject("attackMarker");
        attackMarkSprite.setAlign(Sprite.HAlign.center, Sprite.VAlign.middle);
        attackMark = new AvatarMarker(5, attackMarkSprite, 0, Color.white);

        targetLight = DEFAULT_LIGHT;
        animateLight = false;
        info = avatarInfo;
        clothes = new AvatarClothManager();
        clothRender = new AvatarClothRenderer(dir, frames);
        clothRender.setLight(getLight());
        clothRender.setFrame(0);
        tag = new AvatarTextTag();
        tag.setAvatarHeight(getHeight());
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
        attackMark = new AvatarMarker(org.attackMark);
        clothes = org.clothes;
        clothRender = new AvatarClothRenderer(org.clothRender);
        clothRender.setLight(getLight());
        clothRender.setFrame(0);
        tag = new AvatarTextTag();
        tag.setAvatarHeight(getHeight());
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
     *                 sex and the direction of the avatar that is needed
     * @return a instance of the needed avatar type
     */
    @Nullable
    public static Avatar create(final int avatarID, final Char parent) {
        try {
            final Avatar avatar = CharacterFactory.getInstance().getCommand(avatarID);
            avatar.parentChar = parent;
            return avatar;
        } catch (final IndexOutOfBoundsException ex) {
            // ignored
        }
        return null;
    }

    /**
     * Activate the avatar instance for usage. This needs to be done right after
     * the avatar instance got created.
     *
     * @param newID doesn't do anything since the ID of this object is related
     *              to other things that can't be change
     */
    @Override
    public void activate(final int newID) {
        // nothing needs to be done
    }

    /**
     * Start a animation for this avatar.
     *
     * @param speed the speed of the animation, the larger this value, the
     *              longer the animation takes to finish
     * @param loop  true in case the animation shall never stop and rather run
     *              forever
     */
    public void animate(final int speed, final boolean loop) {
        if (ani == null) {
            return;
        }

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
     * @param slot  the slot of the object that shall get a different color
     * @param color the new color that shall be used to color the graphic itself
     */
    public void changeClothColor(final int slot, final Color color) {
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
     * @param group  the group where the item shall be searched in
     * @param itemID the item id that shall be checked
     * @return <code>true</code> in case the item is defined and renderable
     */
    public boolean clothItemExist(final int group, final int itemID) {
        return clothes.doesClothExists(group, itemID);
    }

    @Override
    public boolean processEvent(@NonNull final GameContainer container, final int delta, @NonNull final MapInteractionEvent event) {
        if (event instanceof ClickOnMapEvent) {
            return processEvent(container, delta, (ClickOnMapEvent) event);
        }
        return super.processEvent(container, delta, event);
    }

    /**
     * This function handles click events on the avatars.
     *
     * @param c     the game container
     * @param delta the time since the last update
     * @param event the event that actually happened
     * @return {@code true} in case the event was handled
     */
    private boolean processEvent(final GameContainer c, final int delta, final ClickOnMapEvent event) {
        if (!isMouseInInteractionRect(event.getX(), event.getY())) {
            return false;
        }

        if (event.getKey() == 1) {
            CombatHandler.getInstance().toggleAttackOnCharacter(parentChar);
            return true;
        }
        return false;
    }

    private static final Logger LOGGER = Logger.getLogger(Avatar.class);

    /**
     * Draw the avatar to the game screen. Calling this function causes the
     * light value to approach the target light in case the light values are
     * different. It also draws the name above the avatar in case it needs to be
     * shown.
     *
     * @return true at all times
     */
    @Override
    public boolean draw(@NonNull final Graphics g) {
        if (isAttackMarkerVisible()) {
            attackMark.draw(g);
        }

        // draw the avatar, naked!! :O
        super.draw(g);

        // draw the clothes
        clothRender.draw(g);

        if (renderName && (tag != null)) {
            tag.draw(g);
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
        attackMarkerVisible = false;
        stopAnimation();
        CharacterFactory.getInstance().recycle(this);
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
        attackMarkerVisible = false;
    }

    /**
     * Set a item as a clothing item to a specified body location. In case its
     * defined the cloth renderer will try to show the cloth on the avatar.
     *
     * @param group  the group of the item, so the location of the item, where it
     *               shall be displayed
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
    public void setLight(@NonNull final Color light) {
        final Color localLight = new Color(light);
        super.setLight(localLight);
        clothRender.setLight(localLight);
        attackMark.setLight(localLight);
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
    public void setLightTarget(final Color light) {
        targetLight = light;
        clothRender.setLight(light);
        attackMark.setLight(light);
        animateLight = true;
    }

    /**
     * Set the name that is displayed in the tag above the avatar graphic.
     *
     * @param charName the name that is displayed above the character graphic
     */
    public void setName(final String charName) {
        if (charName.isEmpty()) {
            tag.setCharacterName("unknown");
        } else {
            tag.setCharacterName(charName);
        }
        tag.setCharNameColor(Color.yellow);
    }

    /**
     * Set the color of the text that is shown above the avatar that is shown.
     *
     * @param color the color that is used for the font of the the text that is
     *              shown above the character and shows the name of the character
     */
    public void setNameColor(final Color color) {
        tag.setCharNameColor(color);
    }

    public void hideHealthPoints() {
        tag.setHealthState(null);
    }

    private static final Color COLOR_UNHARMED = new Color(0, 255, 0);
    private static final Color COLOR_SLIGHTLY_HARMED = new Color(127, 255, 0);
    private static final Color COLOR_HARMED = new Color(255, 255, 0);
    private static final Color COLOR_BADLY_HARMED = new Color(255, 127, 0);
    private static final Color COLOR_NEAR_DEATH = new Color(255, 0, 0);
    private static final Color COLOR_DEAD = new Color(173, 173, 173);

    public void setHealthPoints(int value) {
        if (value == 10000) {
            tag.setHealthState(Lang.getMsg("char.health.unharmed"));
            tag.setHealthStateColor(COLOR_UNHARMED);
        } else if (value > 8000) {
            tag.setHealthState(Lang.getMsg("char.health.slightlyHarmed"));
            tag.setHealthStateColor(COLOR_SLIGHTLY_HARMED);
        } else if (value > 5000) {
            tag.setHealthState(Lang.getMsg("char.health.harmed"));
            tag.setHealthStateColor(COLOR_HARMED);
        } else if (value > 2000) {
            tag.setHealthState(Lang.getMsg("char.health.badlyHarmed"));
            tag.setHealthStateColor(COLOR_BADLY_HARMED);
        } else if (value > 0) {
            tag.setHealthState(Lang.getMsg("char.health.nearDead"));
            tag.setHealthStateColor(COLOR_NEAR_DEATH);
        } else {
            tag.setHealthState(Lang.getMsg("char.health.dead"));
            tag.setHealthStateColor(COLOR_DEAD);
        }
    }

    /**
     * Set the scaling value of the avatar. Possible values are between 0.5f and
     * 1.2f. The size is applied to the height and the width of the avatar
     * images since scaling height and width independent from each other would
     * look crap.
     *
     * @param newScale the new scale value of the avatar image. Values between
     *                 0.5f and 1.2f are valid
     */
    @Override
    public void setScale(final float newScale) {
        super.setScale(newScale);
        clothRender.setScale(newScale);
    }

    /**
     * Set the location on the screen.
     *
     * @param posX       the x coordinate of the location on the screen
     * @param posY       the y coordinate of the location on the screen
     * @param layerZ     the z coordinate, so the layer on the screen
     * @param groupLayer the global layer value of the graphic type
     */
    @Override
    public void setScreenPos(final int posX, final int posY, final int layerZ,
                             final int groupLayer) {
        super.setScreenPos(posX, posY, layerZ, groupLayer);
        clothRender.setScreenLocation(posX, posY, layerZ, groupLayer);
        attackMark.setScreenPos(posX, posY, layerZ, groupLayer);
    }

    /**
     * Update the values of this avatar entity. The light values, the alpha
     * values as well as the name display is checked using this function.
     *
     * @param container
     * @param delta     the time since the last update in milliseconds
     */
    @Override
    public void update(@NonNull final GameContainer container, final int delta) {
        super.update(container, delta);

        clothRender.setAlpha(getAlpha());
        clothRender.update(container, delta);

        final Color locLight = getLight();
        if (animateLight && (locLight != null) && !AnimationUtility.approach(locLight, targetLight, delta)) {
            targetLight = locLight;
            animateLight = false;
        }

        final Input input = container.getInput();

        renderName = isMouseInInteractionRect(input) || (input.isKeyDown(Input.KEY_RALT) && (getAlpha() >
                HIDE_NAME_ALPHA));

        if ((tag != null) && renderName) {
            tag.setDisplayLocation(getDisplayX(), getDisplayY());
            tag.update(container, delta);
        }

        if ((renderName != oldRenderName) && (tag != null)) {
            Camera.getInstance().markAreaDirty(tag.getLastDisplayRect());
            oldRenderName = renderName;
        }

        if (isAttackMarkerVisible()) {
            attackMark.setAlpha(getAlpha());
            attackMark.update(container, delta);
        } else if (oldAttackMarkVisible) {
            Camera.getInstance().markAreaDirty(attackMark.getLastDisplayRect());
        }

        oldAttackMarkVisible = isAttackMarkerVisible();
    }

    private boolean oldAttackMarkVisible;
    private boolean oldRenderName;

    /**
     * Cause the current animation of the avatar to stop instantly. The parent
     * character is removed and not notified.
     */
    private void stopAnimation() {
        if (ani != null) {
            ani.stop();
        }
    }
}

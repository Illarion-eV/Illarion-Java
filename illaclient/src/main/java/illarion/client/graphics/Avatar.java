/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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

import illarion.client.input.AbstractMouseLocationEvent;
import illarion.client.input.ClickOnMapEvent;
import illarion.client.input.CurrentMouseLocationEvent;
import illarion.client.input.DoubleClickOnMapEvent;
import illarion.client.resources.CharacterFactory;
import illarion.client.resources.MiscImageFactory;
import illarion.client.resources.Resource;
import illarion.client.resources.data.AvatarTemplate;
import illarion.client.util.Lang;
import illarion.client.world.Char;
import illarion.client.world.World;
import illarion.client.world.interactive.InteractiveChar;
import illarion.common.graphics.Layers;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.Graphics;
import org.illarion.engine.graphic.ImmutableColor;
import org.illarion.engine.graphic.SceneEvent;
import org.illarion.engine.input.Button;
import org.illarion.engine.input.Input;
import org.illarion.engine.input.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Class for the avatar of a characters. The avatar is the visual representation of a character on a map. All
 * characters, including monsters and NPCs have a avatar.
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@SuppressWarnings("ClassNamingConvention")
public final class Avatar extends AbstractEntity<AvatarTemplate> implements Resource {
    /**
     * The minimal alpha value of a avatar that is needed to show the name tag above the avatar graphic.
     */
    private static final int HIDE_NAME_ALPHA = 127;

    /**
     * The frame animation that handles the animation of this avatar.
     */
    @Nullable
    private final FrameAnimation animation;

    /**
     * In case the light shall be animated this value is set to true. In special
     * cases its not good if the light is animated, such as the switch of levels
     * and the sudden appearance of characters on the map. In such cases
     */
    private boolean animateLight;

    /**
     * The render system for the clothes of this avatar.
     */
    @Nonnull
    private final transient AvatarClothRenderer clothRender;

    /**
     * The mark that is displayed in case the character is the target of a attack.
     */
    @Nonnull
    private final AvatarMarker attackMark;

    @Nonnull
    private final AvatarMarker attackAvailableMark;

    /**
     * The text tag is the small text box shown above the avatar that contains
     * the name of the avatar.
     */
    @Nonnull
    private final AvatarTextTag avatarTextTag;

    /**
     * The character that created this avatar.
     */
    @Nonnull
    private final Char parentChar;

    public boolean isAttackMarkerVisible() {
        return attackMarkerVisible;
    }

    public void setAttackMarkerVisible(boolean attackMarkerVisible) {
        this.attackMarkerVisible = attackMarkerVisible;
    }

    /**
     * This variable changes to true in case the attack marker is supposed to be displayed.
     */
    private boolean attackMarkerVisible;

    private boolean showAttackAvailable;

    /**
     * Stores if the name shall be rendered or not. It is checked at every
     * update if this flag is valid or not.
     */
    private boolean renderName;

    /**
     * The target light of this avatar. In case the light is set to be animated
     * the color this avatar is rendered with will approach this target light.
     */
    @Nonnull
    private Color targetLight;

    private int showHighlight;

    private Avatar(@Nonnull AvatarTemplate template, @Nonnull Char parentChar) {
        super(template);
        attackMark = new AvatarMarker(MiscImageFactory.ATTACK_MARKER, this);
        attackAvailableMark = new AvatarMarker(MiscImageFactory.ATTACK_MARKER, this);

        clothRender = new AvatarClothRenderer(template.getDirection(), template.getFrames());
        clothRender.setLight(getLight());
        clothRender.setFrame(0);

        targetLight = DEFAULT_LIGHT;
        animateLight = false;

        avatarTextTag = new AvatarTextTag();
        avatarTextTag.setAvatarHeight(template.getSprite().getHeight());

        if (template.getFrames() > 1) {
            animation = new FrameAnimation(this);
            animation.setup(template.getFrames(), template.getStillFrame(), 1, 0);
        } else {
            animation = null;
        }
        this.parentChar = parentChar;
    }

    /**
     * Create a avatar from the avatar factory. This either creates a new instance of the avatar class or it takes a
     * existing instance from the list of currently unused instances.
     *
     * @param avatarID the ID of the character that identifies the name and the sex and the direction of the avatar
     * that is needed
     * @return a instance of the needed avatar type
     */
    @Nullable
    public static Avatar create(int avatarID, @Nonnull Char parent) {
        try {
            AvatarTemplate template = CharacterFactory.getInstance().getTemplate(avatarID);
            return new Avatar(template, parent);
        } catch (@Nonnull IndexOutOfBoundsException ex) {
            // ignored
        }
        return null;
    }

    /**
     * Start a animation for this avatar.
     *
     * @param speed the speed of the animation, the larger this value, the
     * longer the animation takes to finish
     * @param loop true in case the animation shall never stop and rather run
     * forever
     */
    public void animate(int speed, boolean loop) {
        if (isMarkedAsRemoved()) {
            LOGGER.warn("Animating a removed avatar is illegal.");
            return;
        }
        if (animation == null) {
            return;
        }

        animation.updateSpeed(speed);
        if (loop) {
            animation.updateMode(FrameAnimation.LOOPED | FrameAnimation.CYCLIC);
        } else {
            animation.updateMode(FrameAnimation.CYCLIC);
        }
        animation.restart();
    }

    /**
     * Stop the execution of the current animation.
     */
    public void stopAnimation() {
        if (animation != null) {
            animation.stop();
        }
    }

    /**
     * This function is triggered in case a animation that is not looped finished.
     *
     * @param finished set true in case the animation is really done
     */
    @Override
    public void animationFinished(boolean finished) {
        parentChar.resetAnimation();
    }

    /**
     * Change the color of one paperdolling object.
     *
     * @param slot the slot of the object that shall get a different color
     * @param color the new color that shall be used to color the graphic itself
     */
    public void changeClothColor(int slot, Color color) {
        clothRender.changeBaseColor(slot, color);
    }

    @Override
    public boolean isEventProcessed(
            @Nonnull GameContainer container, int delta, @Nonnull SceneEvent event) {
        if (event instanceof ClickOnMapEvent) {
            return isEventProcessed(container, delta, (ClickOnMapEvent) event);
        }
        if (parentChar.isNPC()) {
            if (event instanceof CurrentMouseLocationEvent) {
                CurrentMouseLocationEvent moveEvent = (CurrentMouseLocationEvent) event;
                if (!isMouseInInteractionRect(moveEvent.getX(), moveEvent.getY())) {
                    return false;
                }

                showHighlight = 1;
                InteractiveChar interactiveChar = parentChar.getInteractive();
                if (interactiveChar != null && interactiveChar.isInUseRange()) {
                    showHighlight = 2;
                }
                return true;
            }

            if (event instanceof DoubleClickOnMapEvent) {
                return isEventProcessed(container, delta, (DoubleClickOnMapEvent) event);
            }
        }

        return super.isEventProcessed(container, delta, event);
    }

    /**
     * This function handles click events on the avatars.
     *
     * @param container the game container
     * @param delta the time since the last update
     * @param event the event that actually happened
     * @return {@code true} in case the event was handled
     */
    @SuppressWarnings({"BooleanMethodNameMustStartWithQuestion", "UnusedParameters"})
    private boolean isEventProcessed(
            GameContainer container, int delta, @Nonnull ClickOnMapEvent event) {
        if (!isMouseInInteractiveOrOnTag(event)) {
            return false;
        }

        if (event.getKey() == Button.Right) {
            World.getPlayer().getCombatHandler().toggleAttackOnCharacter(parentChar);
            return true;
        }

        if (!isMouseInInteractionRect(event.getX(), event.getY())) {
            return false;
        }

        if (event.getKey() == Button.Left) {
            World.getPlayer().getMovementHandler().walkTo(parentChar.getLocation(), parentChar.getInteractive());
            return false;
        }

        return false;
    }

    /**
     * This function handles double click events on the avatars.
     *
     * @param container the game container
     * @param delta the time since the last update
     * @param event the event that actually happened
     * @return {@code true} in case the event was handled
     */
    @SuppressWarnings({"BooleanMethodNameMustStartWithQuestion", "UnusedParameters"})
    private boolean isEventProcessed(
            GameContainer container, int delta, @Nonnull DoubleClickOnMapEvent event) {
        if (event.getKey() != Button.Left) {
            return false;
        }

        if (!isMouseInInteractiveOrOnTag(event)) {
            return false;
        }

        if (!isMouseInInteractionRect(event.getX(), event.getY())) {
            return false;
        }

        InteractiveChar interactiveChar = parentChar.getInteractive();

        if (interactiveChar == null) {
            return false;
        }

        if (interactiveChar.isInUseRange()) {
            interactiveChar.use();
        } else {
            World.getPlayer().getMovementHandler().walkToAndUse(parentChar.getLocation(), interactiveChar);
        }

        return true;
    }

    /**
     * Check if a mouse event points at the interactive area of a avatar or on its tag.
     *
     * @param event the mouse event
     * @return {@code true} in case the mouse is on the interactive area of the avatar or on its tag
     */
    private boolean isMouseInInteractiveOrOnTag(@Nonnull AbstractMouseLocationEvent event) {
        int mouseXonDisplay = event.getX() + Camera.getInstance().getViewportOffsetX();
        int mouseYonDisplay = event.getY() + Camera.getInstance().getViewportOffsetY();
        if (renderName && avatarTextTag.getDisplayRect().isInside(mouseXonDisplay, mouseYonDisplay)) {
            return true;
        }

        return isMouseInInteractionRect(event.getX(), event.getY());
    }

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger LOGGER = LoggerFactory.getLogger(Avatar.class);

    @Override
    public int getHighlight() {
        return showHighlight;
    }

    /**
     * Draw the avatar to the game screen. Calling this function causes the light value to approach the target light
     * in case the light values are different. It also draws the name above the avatar in case it needs to be shown.
     */
    @Override
    public void render(@Nonnull Graphics g) {
        if (getAlpha() == 0) {
            return;
        }

        if (isAttackMarkerVisible()) {
            attackMark.render(g);
        }

        if (showAttackAvailable) {
            attackAvailableMark.render(g);
        }

        // draw the avatar, naked!! :O
        super.render(g);

        // draw the clothes
        clothRender.render(g);

        if (renderName) {
            avatarTextTag.render(g);
        }
        showHighlight = 0;
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

    @Override
    public void hide() {
        if (animation != null) {
            animation.stop();
        }
        super.hide();
    }

    /**
     * Remove a item from the list of items that are shown as clothes.
     *
     * @param group the group that shall be cleaned
     */
    public void removeClothItem(int group) {
        clothRender.setCloth(group, null);
    }

    /**
     * Set a item as a clothing item to a specified body location. In case its defined the cloth renderer will try to
     * show the cloth on the avatar.
     *
     * @param group the group of the item, so the location of the item, where it shall be displayed
     * @param itemID the ID of the item that shall be displayed
     */
    public void setClothItem(int group, int itemID) {
        clothRender.setCloth(group, getTemplate().getClothes().getCloth(group, itemID, this));
    }

    /**
     * Set the current frame of the avatar. This forwards the frame to the Entity super function but sends it also to
     * the cloth render.
     *
     * @param frame the index of the frame that shall be rendered next
     */
    @Override
    public void setFrame(int frame) {
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
    public void setLight(@Nonnull Color light) {
        super.setLight(light);
        clothRender.setLight(light);
        attackMark.setLight(light);
        attackAvailableMark.setLight(light);
        attackAvailableMark.setBaseColor(Color.BLACK);
        animateLight = false;
    }

    /**
     * Set the light this avatar is colored with. Setting the light with this function will enable the smooth change
     * of the light and so the light color of the avatar will slowly approach the color of the light set with
     * this function.
     *
     * @param light the target light color for this avatar
     */
    public void setLightTarget(@Nonnull Color light) {
        targetLight = light;
        clothRender.setLight(light);
        attackMark.setLight(light);
        attackAvailableMark.setLight(light);
        attackAvailableMark.setBaseColor(Color.BLACK);
        animateLight = true;
    }

    /**
     * Set the name that is displayed in the tag above the avatar graphic.
     *
     * @param charName the name that is displayed above the character graphic
     */
    public void setName(@Nonnull String charName) {
        if (charName.isEmpty()) {
            avatarTextTag.setCharacterName("unknown");
        } else {
            avatarTextTag.setCharacterName(charName);
        }
        avatarTextTag.setCharNameColor(Color.YELLOW);
    }

    /**
     * Set the color of the text that is shown above the avatar that is shown.
     *
     * @param color the color that is used for the font of the the text that is
     * shown above the character and shows the name of the character
     */
    public void setNameColor(Color color) {
        avatarTextTag.setCharNameColor(color);
    }

    private static final Color COLOR_UNHARMED = new ImmutableColor(0, 255, 0);
    private static final Color COLOR_SLIGHTLY_HARMED = new ImmutableColor(127, 255, 0);
    private static final Color COLOR_HARMED = new ImmutableColor(255, 255, 0);
    private static final Color COLOR_BADLY_HARMED = new ImmutableColor(255, 127, 0);
    private static final Color COLOR_NEAR_DEATH = new ImmutableColor(255, 0, 0);
    private static final Color COLOR_DEAD = new ImmutableColor(173, 173, 173);

    public void setHealthPoints(int value) {
        //noinspection IfStatementWithTooManyBranches
        if (value == 10000) {
            avatarTextTag.setHealthState(Lang.getMsg("char.health.unharmed"));
            avatarTextTag.setHealthStateColor(COLOR_UNHARMED);
        } else if (value > 8000) {
            avatarTextTag.setHealthState(Lang.getMsg("char.health.slightlyHarmed"));
            avatarTextTag.setHealthStateColor(COLOR_SLIGHTLY_HARMED);
        } else if (value > 5000) {
            avatarTextTag.setHealthState(Lang.getMsg("char.health.harmed"));
            avatarTextTag.setHealthStateColor(COLOR_HARMED);
        } else if (value > 2000) {
            avatarTextTag.setHealthState(Lang.getMsg("char.health.badlyHarmed"));
            avatarTextTag.setHealthStateColor(COLOR_BADLY_HARMED);
        } else if (value > 0) {
            avatarTextTag.setHealthState(Lang.getMsg("char.health.nearDead"));
            avatarTextTag.setHealthStateColor(COLOR_NEAR_DEATH);
        } else {
            avatarTextTag.setHealthState(Lang.getMsg("char.health.dead"));
            avatarTextTag.setHealthStateColor(COLOR_DEAD);
        }
    }

    @Override
    public void setScale(float newScale) {
        super.setScale(newScale);
        clothRender.setScale(newScale);
    }

    public void setScreenPos(int posX, int posY, int layerZ) {
        setScreenPos(posX, posY, layerZ, Layers.CHARS);
    }

    @Override
    public void setScreenPos(int posX, int posY, int layerZ, int groupLayer) {
        super.setScreenPos(posX, posY, layerZ, groupLayer);
        clothRender.setScreenLocation(posX, posY, layerZ, groupLayer);
        attackMark.setScreenPos(posX, posY, layerZ, groupLayer);
        attackAvailableMark.setScreenPos(posX, posY, layerZ, groupLayer);
    }

    @Override
    public void update(@Nonnull GameContainer container, int delta) {
        super.update(container, delta);

        clothRender.setAlpha(getAlpha());
        clothRender.update(container, delta);

        Color locLight = getLight();
        if (animateLight && !AnimationUtility.approach(locLight, targetLight, delta)) {
            targetLight = locLight;
            animateLight = false;
        }

        Input input = container.getEngine().getInput();

        renderName =
                (isMouseInInteractionRect(input) || input.isKeyDown(Key.RightAlt)) && (getAlpha() > HIDE_NAME_ALPHA);

        if (isMouseInInteractionRect(input) && World.getPlayer().getCombatHandler().canBeAttacked(parentChar)) {
            showAttackAvailable = true;
            attackAvailableMark.setAlpha(getAlpha());
            attackAvailableMark.update(container, delta);
        } else {
            showAttackAvailable = false;
        }

        if (renderName) {
            avatarTextTag.setDisplayLocation(getDisplayX(), getDisplayY());
            avatarTextTag.update(container, delta);
        }

        if (isAttackMarkerVisible()) {
            attackMark.setAlpha(getAlpha());
            attackMark.update(container, delta);
        }
    }
}

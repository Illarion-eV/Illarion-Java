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

import illarion.client.graphics.AvatarClothManager.AvatarClothGroup;
import illarion.client.resources.MiscImageFactory;
import illarion.client.resources.Resource;
import illarion.client.resources.data.AvatarTemplate;
import illarion.common.types.DisplayCoordinate;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.Graphics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AvatarEntity extends AbstractEntity<AvatarTemplate> implements Resource {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(AvatarEntity.class);
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
    /**
     * The frame animation that handles the animation of this avatar.
     */
    @Nullable
    private final FrameAnimation animation;
    @Nonnull
    private AvatarAttackMarkerState attackMarkerState;
    /**
     * In case the light shall be animated this value is set to true. In special cases its not good if the light is
     * animated, such as the switch of levels and the sudden appearance of characters on the map. In such cases
     */
    private boolean animateLight;

    private int showHighlight;

    /**
     * The target light of this avatar. In case the light is set to be animated
     * the color this avatar is rendered with will approach this target light.
     */
    @Nonnull
    private Color targetLight;

    private final boolean uiMode;

    public AvatarEntity(@Nonnull AvatarTemplate template, boolean uiMode) {
        super(template);

        this.uiMode = uiMode;

        targetLight = DEFAULT_LIGHT;

        attackMarkerState = AvatarAttackMarkerState.Hidden;

        attackMark = new AvatarMarker(MiscImageFactory.ATTACK_MARKER, this);

        clothRender = new AvatarClothRenderer(template.getDirection(), template.getFrames());
        clothRender.setLight(getLight());
        clothRender.setFrame(0);

        if (template.getFrames() > 1) {
            animation = new FrameAnimation(this);
            animation.setup(template.getFrames(), template.getStillFrame(), 150);
        } else {
            animation = null;
        }

        animateLight = false;
    }

    public void changeAnimationDuration(int newDuration) {
        if ((animation != null) && animation.isRunning()) {
            animation.setDuration(newDuration);
        }
    }

    @Override
    public int getHighlight() {
        return showHighlight;
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
        attackMark.setLight(light);
        attackMark.setBaseColor(Color.BLACK);
        animateLight = false;
    }

    @Override
    public void setScale(float newScale) {
        super.setScale(newScale);
        clothRender.setScale(newScale);
    }

    /**
     * Draw the avatar to the game screen. Calling this function causes the light value to approach the target light
     * in case the light values are different. It also draws the name above the avatar in case it needs to be shown.
     */
    @Override
    public void render(@Nonnull Graphics graphics) {
        if (performRendering()) {
            if (attackMarkerState != AvatarAttackMarkerState.Hidden) {
                attackMark.render(graphics);
            }

            // draw the avatar, naked!! :O
            super.render(graphics);

            // draw the clothes
            clothRender.render(graphics);
            showHighlight = 0;
        }
    }

    @Override
    public void update(@Nonnull GameContainer container, int delta) {
        super.update(container, delta);

        if (!isShown()) {
            return;
        }

        int usedAlpha = getAlpha();

        clothRender.setAlpha(usedAlpha);
        clothRender.update(container, delta);

        Color locLight = getLight();
        if (animateLight && !AnimationUtility.approach(locLight, targetLight, delta)) {
            targetLight = locLight;
            animateLight = false;
        }
        locLight.setAlpha(usedAlpha);

        switch (attackMarkerState) {
            case Hidden:
                break;
            case AttackPossible:
                attackMark.setBaseColor(Color.BLACK);
                attackMark.setAlpha(usedAlpha);
                attackMark.update(container, delta);
                break;
            case AttackStarting:
                attackMark.setBaseColor(Color.WHITE);
                attackMark.setAlpha(usedAlpha / 2);
                attackMark.update(container, delta);
                break;
            case Attacking:
                attackMark.setBaseColor(Color.WHITE);
                attackMark.setAlpha(usedAlpha);
                attackMark.update(container, delta);
        }
    }

    /**
     * Hide the avatar from the screen.
     */
    @Override
    public void hide() {
        super.hide();
        stopAnimation();
    }

    @Override
    public boolean isShown() {
        return uiMode || super.isShown();
    }

    @Override
    protected boolean performRendering() {
        return uiMode || super.performRendering();
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
        log.debug("{}: Now showing animation frame {}", this, frame);
    }

    /**
     * Change the screen position.
     *
     * @param coordinate the new location on the screen.
     */
    @Override
    public void setScreenPos(@Nonnull DisplayCoordinate coordinate) {
        super.setScreenPos(coordinate);
        clothRender.setScreenPos(coordinate);
        attackMark.setScreenPos(coordinate);
    }

    public void setHighlight(int value) {
        showHighlight = value;
    }

    /**
     * Set the new state of the attack marker.
     *
     * @param attackMarkerState the new state
     */
    public void setAttackMarkerState(@Nonnull AvatarAttackMarkerState attackMarkerState) {
        this.attackMarkerState = attackMarkerState;
    }

    /**
     * Change the color of one paper dolling object.
     *
     * @param group the group of the object that shall get a different color
     * @param color the new color that shall be used to color the graphic itself
     */
    public void changeClothColor(@Nonnull AvatarClothGroup group, Color color) {
        clothRender.changeBaseColor(group, color);
    }

    /**
     * Check if the light is currently animated. Means the light is currently changing towards a target light color.
     *
     * @return true in case the light is currently animated
     */
    public boolean hasAnimatedLight() {
        return animateLight;
    }

    /**
     * Remove a item from the list of items that are shown as clothes.
     *
     * @param group the group that shall be cleaned
     */
    public void removeClothItem(@Nonnull AvatarClothGroup group) {
        clothRender.setCloth(group, null);
    }

    /**
     * Set a item as a clothing item to a specified body location. In case its defined the cloth renderer will try to
     * show the cloth on the avatar.
     *
     * @param group the group of the item, so the location of the item, where it shall be displayed
     * @param itemID the ID of the item that shall be displayed
     */
    public void setClothItem(@Nonnull AvatarClothGroup group, int itemID) {
        clothRender.setCloth(group, getTemplate().getClothes().getCloth(group, itemID, this));
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
        animateLight = true;
    }

    /**
     * Start a animation for this avatar.
     *
     * @param duration the duration of the animation in milliseconds
     * @param loop true in case the animation shall never stop and rather run
     * forever
     */
    public void animate(int duration, boolean loop) {
        animate(duration, loop, false, 1.f);
    }

    /**
     * Start a animation for this avatar.
     *
     * @param duration the duration of the animation in milliseconds
     * @param loop true in case the animation shall never stop and rather run
     * forever
     */
    public void animate(int duration, boolean loop, boolean expandStorybook, float length) {
        if (isMarkedAsRemoved()) {
            log.warn("Animating a removed avatar is illegal.");
            return;
        }
        if (animation == null) {
            return;
        }

        if (expandStorybook) {
            animation.continueStoryboard(length);
        } else {
            animation.resetStoryboard();
        }
        animation.setDuration(duration);
        if (loop) {
            animation.updateMode(FrameAnimationMode.Looped);
        } else {
            animation.updateMode();
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
}

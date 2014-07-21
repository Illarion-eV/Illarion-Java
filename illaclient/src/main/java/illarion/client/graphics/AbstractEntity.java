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

import illarion.client.resources.data.AbstractEntityTemplate;
import illarion.client.world.World;
import illarion.common.types.Location;
import illarion.common.types.Rectangle;
import illarion.common.util.FastMath;
import org.illarion.engine.EngineException;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.*;
import org.illarion.engine.graphic.effects.HighlightEffect;
import org.illarion.engine.graphic.effects.TextureEffect;
import org.illarion.engine.input.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * The entity is a object that is shown in the game. It contains a sprite and possibly a frame animation. Also it
 * performs fade in and out effects.
 * <p>
 * It handles static objects on the screen as well as animated ones or objects with variations.
 * </p>
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@SuppressWarnings("nls")
@NotThreadSafe
public abstract class AbstractEntity<T extends AbstractEntityTemplate>
        implements DisplayItem, AlphaHandler, AnimatedFrame {
    public boolean isCurrentlyEffectedByFadingCorridor() {
        return currentlyEffectedByFadingCorridor;
    }

    /**
     * This class is used in case more then one alpha change listener is added. It forwards a alpha change message to
     * two other handlers. This way its possible to create a infinite amount of listeners on one entity.
     */
    private static final class AlphaChangeListenerMulticast implements AlphaChangeListener {
        /**
         * The first listener to get the event.
         */
        private final AlphaChangeListener listener1;

        /**
         * The second listener to get the event.
         */
        private final AlphaChangeListener listener2;

        /**
         * Constructor for a multicast object.
         *
         * @param l1 the first listener to get the message
         * @param l2 the second listener to get the message
         */
        private AlphaChangeListenerMulticast(AlphaChangeListener l1, AlphaChangeListener l2) {
            listener1 = l1;
            listener2 = l2;
        }

        /**
         * This method receives the alpha changed event and forwards its data to both added listeners.
         *
         * @param from the old alpha value
         * @param to the new alpha value
         */
        @Override
        public void alphaChanged(int from, int to) {
            listener1.alphaChanged(from, to);
            listener2.alphaChanged(from, to);
        }
    }

    /**
     * The default light that is used in the client.
     */
    @Nonnull
    protected static final Color DEFAULT_LIGHT = Color.WHITE;

    /**
     * The speed value for fading the alpha values by default.
     */
    private static final int FADING_SPEED = 25;

    /**
     * The color value of the alpha when the object is faded out fully.
     */
    private static final int FADE_OUT_ALPHA = (int) (0.4f * 255);

    /**
     * The alpha listener that is supposed to receive a message in case the alpha value of this entity changed.
     */
    @Nullable
    private AlphaChangeListener alphaListener;

    /**
     * The target of the alpha approaching. The current alpha value will move by default closer to the alpha target
     * value at every render run.
     */
    private int alphaTarget;

    /**
     * The base color is the color the image of the sprite is always colored with,
     * this color is applied no matter what the localLight is set to.
     */
    @Nullable
    private Color baseColor;

    /**
     * The frame that is currently shown by this entity.
     */
    private int currentFrame;

    /**
     * The current x location of this item on the screen relative to the origin of the game map.
     */
    private int displayX;

    /**
     * The current y location of this item on the screen relative to the origin of the game map.
     */
    private int displayY;

    /**
     * The z order of the item, so the layer of the item that determines the position of the object in the display
     * list.
     */
    private int layerZ;

    /**
     * The light that effects this entity directly. That could be the {@link #DEFAULT_LIGHT} that ensures that the
     * object is displayed with its real colors or the ambient light of the weather that ensures that the object is
     * colored for the display on the map.
     */
    @Nonnull
    private Color localLight;

    /**
     * The light value that is used to render this entity during the next render loop.
     */
    @Nonnull
    private final Color renderLight = new Color(Color.WHITE);

    /**
     * This color is the color that was used last time to render the entity. Its used to check if the color changed
     * and the entity needs to be rendered again.
     */
    @Nonnull
    private final Color lastRenderLight = new Color(Color.WHITE);

    /**
     * The color that is used to overwrite the real color of this entity.
     */
    @Nullable
    private Color overWriteBaseColor;

    /**
     * The scaling value that is applied to this entity.
     */
    private float scale = 1.f;

    /**
     * A flag if this object is currently shown on the screen.
     */
    private boolean shown;

    /**
     * The template of this instance.
     */
    @Nonnull
    private final T template;

    protected AbstractEntity(@Nonnull T template) {
        this.template = template;
        baseColor = template.getDefaultColor();
        if (baseColor == null) {
            alphaTarget = 255;
            localLight = new Color(Color.WHITE);
        } else {
            localLight = new Color(baseColor);
            alphaTarget = baseColor.getAlpha();
        }
    }

    @Nonnull
    public T getTemplate() {
        return template;
    }

    @Override
    public void addAlphaChangeListener(@Nonnull AlphaChangeListener listener) {
        if (removedEntity) {
            LOGGER.warn("Adding a alpha listener to a removed entity is not allowed.");
            return;
        }
        if (alphaListener == null) {
            alphaListener = listener;
            return;
        }

        alphaListener = new AlphaChangeListenerMulticast(alphaListener, listener);
    }

    /**
     * This function is triggered when a frame animation is done. Overwrite this function in order to archive some
     * special event handling after the animation. By default it does nothing.
     *
     * @param finished true in case the animation is really done
     */
    @Override
    public void animationFinished(boolean finished) {
        // nothing needs to be done by default
    }

    @Override
    public void animationStarted() {
        // nothing to do
    }

    /**
     * Set a new base color of the entity.
     *
     * @param newBaseColor the new base color of the entity, {@code null} to get the default color
     */
    public void changeBaseColor(@Nullable Color newBaseColor) {
        if (removedEntity) {
            LOGGER.warn("Changing the baseColor of a entity is not allowed after the entity was removed.");
            return;
        }
        if (newBaseColor == null) {
            overWriteBaseColor = null;
            return;
        }

        if (overWriteBaseColor == null) {
            overWriteBaseColor = new Color(newBaseColor);
        } else {
            overWriteBaseColor.setColor(newBaseColor);
        }
    }

    /**
     * Get the frame that is currently displayed.
     *
     * @return the currently displayed frame
     */
    public int getCurrentFrame() {
        return currentFrame;
    }

    /**
     * Get the highlighting level of the item
     *
     * @return the highlight level of the object
     */
    public int getHighlight() {
        return 0;
    }

    /**
     * Draw this entity to the screen. This also performs a few basic animations such as fading in and out,
     * based on the delta time that is supplied to this function.
     */
    @Override
    public void render(@Nonnull Graphics g) {
        if (performRendering()) {
            int renderLocX = displayX;
            int renderLocY = displayY;

            int highlight = getHighlight();
            if ((highlight > 0) && (highlightEffect != null)) {
                if (highlight == 1) {
                    highlightEffect.setHighlightColor(COLOR_HIGHLIGHT_WEAK);
                } else {
                    highlightEffect.setHighlightColor(COLOR_HIGHLIGHT_STRONG);
                }
                renderSprite(g, renderLocX, renderLocY, renderLight, highlightEffect);
            } else {
                renderSprite(g, renderLocX, renderLocY, renderLight);
            }
        }
    }

    protected boolean performRendering() {
        return (getAlpha() > 0) && Camera.getInstance().requiresUpdate(displayRect);
    }

    protected void renderSprite(
            @Nonnull Graphics g, int x, int y, @Nonnull Color light, @Nonnull TextureEffect... effects) {
        g.drawSprite(template.getSprite(), x, y, light, getCurrentFrame(), getScale(), 0.f, effects);
    }

    private static final Color COLOR_HIGHLIGHT_STRONG = new ImmutableColor(1.f, 1.f, 1.f, 0.25f);
    private static final Color COLOR_HIGHLIGHT_WEAK = new ImmutableColor(1.f, 1.f, 1.f, 0.05f);

    /**
     * Get the current alpha value.
     *
     * @return the alpha value
     */
    @Override
    public int getAlpha() {
        return getLight().getAlpha();
    }

    /**
     * Get the current x location of this object on the screen relative to the
     * origin of the game map. That value is set with the screen position.
     *
     * @return the x coordinate of the display location
     */
    public final int getDisplayX() {
        return displayX;
    }

    /**
     * Get the current y location of this object on the screen relative to the
     * origin of the game map. That value is set with the screen position.
     *
     * @return the y coordinate of the display location
     */
    public final int getDisplayY() {
        return displayY;
    }

    /**
     * Get the current light instance that is used by this entity.
     *
     * @return the light this entity uses at the rendering functions
     */
    @Nonnull
    public final Color getLight() {
        return localLight;
    }

    /**
     * Get the scaling value that is applied to the entity.
     *
     * @return the scaling value applied to the entity
     */
    public float getScale() {
        return scale;
    }

    /**
     * Get the current target of the alpha approaching.
     *
     * @return the current alpha target value
     */
    public int getTargetAlpha() {
        return alphaTarget;
    }

    /**
     * Get the Z Order of this entity that marks the position in the display
     * list and selects this way, how other images overlay this entity.
     *
     * @return the layer of this entity
     */
    @Override
    public final int getOrder() {
        return layerZ;
    }

    /**
     * Hide the entity from the screen by removing it from the display list.
     */
    @Override
    public void hide() {
        if (shown) {
            World.getMapDisplay().getGameScene().removeElement(this);
            shown = false;
        }
    }

    /**
     * Once this value is set {@code true} the entity can be assumed to be removed. It must not be added to the
     * display again once this was done.
     */
    private boolean removedEntity;

    /**
     * Calling this function marks the entity to be removed from the client for good. Once this function was called
     * its not allowed to do anything anymore with this entity.
     */
    public void markAsRemoved() {
        hide();
        removedEntity = true;
    }

    public boolean isMarkedAsRemoved() {
        return removedEntity;
    }

    /**
     * Check if the entity is visible.
     *
     * @return true in case the entity is visible
     */
    public final boolean isVisible() {
        return (getTargetAlpha() > 0) || (getLight().getAlpha() > 0);
    }

    /**
     * Set the current alpha value of the entity. This causes that the alpha value is changed right away without any
     * fading effect. To get a fading effect use {@link #setAlphaTarget(int)}.
     *
     * @param newAlpha the new alpha value of this entity
     */
    @Override
    public void setAlpha(int newAlpha) {
        if (removedEntity) {
            LOGGER.warn("Changing the alpha value of a removed entity is not allowed.");
            return;
        }
        int usedAlpha = FastMath.clamp(newAlpha, 0, Color.MAX_INT_VALUE);
        if (getLight().getAlpha() != usedAlpha) {
            int oldAlpha = getLight().getAlpha();
            getLight().setAlpha(usedAlpha);
            if (alphaListener != null) {
                alphaListener.alphaChanged(oldAlpha, getLight().getAlpha());
            }
        }
    }

    /**
     * Set the target of a alpha fading effect. At every rendering run of this entity the real alpha value of this
     * entity will move closer to the alpha target. To set the alpha value without a fading animation use
     * {@link #setAlpha(int)}.
     *
     * @param newAlphaTarget the target of the alpha fading
     */
    @Override
    public final void setAlphaTarget(int newAlphaTarget) {
        if (removedEntity) {
            LOGGER.warn("Changing the alpha animation target of a entity is not allowed.");
            return;
        }
        alphaTarget = FastMath.clamp(newAlphaTarget, 0, Color.MAX_INT_VALUE);
    }

    /**
     * Set the base color of this entity. This operation does not create a copy of this reference.
     *
     * @param newBaseColor the new base color of the entity
     */
    public void setBaseColor(@Nullable Color newBaseColor) {
        if (removedEntity) {
            LOGGER.warn("Changing the base color of a entity is not allowed once the entity was removed.");
            return;
        }
        baseColor = newBaseColor;
    }

    /**
     * Set the frame that is currently displayed at the render functions of this entity.
     *
     * @param frame the index of the frame that is displayed
     */
    @Override
    public void setFrame(int frame) {
        if (removedEntity) {
            LOGGER.warn("Changing the frame of a removed entity is not allowed.");
            return;
        }
        if (currentFrame != frame) {
            currentFrame = frame;
        }
    }

    /**
     * Set the current light of this entity. This sets the instance that is set as parameter directly as local light
     * color. So any changes applied to the instance that was transferred will effect the light of this entity.
     *
     * @param light the new light that shall be used by this entity
     */
    public void setLight(@Nonnull Color light) {
        if (removedEntity) {
            LOGGER.warn("Changing the light of a removed entity is not allowed.");
            return;
        }
        float oldAlpha = localLight.getAlphaf();
        localLight = new Color(light);
        localLight.setAlphaf(oldAlpha);
    }

    /**
     * Set the scaling that shall be applied to this entity.
     *
     * @param newScale the new scaling value applied to this entity
     */
    public void setScale(float newScale) {
        if (removedEntity) {
            LOGGER.warn("Changing the scale of a removed entity is not allowed.");
            return;
        }
        if (scale != newScale) {
            scale = newScale;
        }
    }

    /**
     * Set the position of the entity on the display. The display origin is at the origin of the game map.
     *
     * @param dispX the x coordinate of the location of the display
     * @param dispY the y coordinate of the location of the display
     * @param zLayer the z layer of the coordinate
     * @param typeLayer the global layer of this type of entity.
     */
    public void setScreenPos(int dispX, int dispY, int zLayer, int typeLayer) {
        if (removedEntity) {
            LOGGER.warn("Changing the screen position of a removed entity is not allowed.");
            return;
        }

        displayX = dispX;
        displayY = dispY;

        if (shown) {
            int newLayerZ = zLayer - typeLayer;
            if (newLayerZ != layerZ) {
                layerZ = newLayerZ;
                updateDisplayPosition();
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
    public final void setScreenPos(@Nonnull Location loc, int typeLayer) {
        setScreenPos(loc.getDcX(), loc.getDcY(), loc.getDcZ(), typeLayer);
    }

    @Override
    public boolean isEventProcessed(
            @Nonnull GameContainer container, int delta, @Nonnull SceneEvent event) {
        return false;
    }

    protected boolean isMouseInInteractionRect(int mouseX, int mouseY) {
        int mouseXonDisplay = mouseX + Camera.getInstance().getViewportOffsetX();
        int mouseYonDisplay = mouseY + Camera.getInstance().getViewportOffsetY();

        return getInteractionRect().isInside(mouseXonDisplay, mouseYonDisplay);
    }

    protected final boolean isMouseInInteractionRect(@Nonnull Input input) {
        return isMouseInInteractionRect(input.getMouseX(), input.getMouseY());
    }

    /**
     * The logging instance of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEntity.class);

    @Override
    public void show() {
        if (removedEntity) {
            LOGGER.warn("Adding a entity to the display list is not allowed after the entity was removed.");
            return;
        }
        if (shown) {
            LOGGER.error("Added entity {} twice.", this);
        } else {
            World.getMapDisplay().getGameScene().addElement(this);
            shown = true;
        }
    }

    /**
     * Update the position of this entity in the display list.
     */
    public void updateDisplayPosition() {
        if (removedEntity) {
            LOGGER.warn("Updating the display position is not allowed once the entity was removed.");
            return;
        }
        if (shown) {
            World.getMapDisplay().getGameScene().updateElementLocation(this);
        } else {
            LOGGER.error("Updated display location for hidden item.");
        }
    }

    /**
     * Check if this entity is currently displayed on the screen.
     *
     * @return {@code true} in case this entity is currently displayed on the screen
     */
    protected boolean isShown() {
        return shown;
    }

    @Override
    public void update(@Nonnull GameContainer container, int delta) {
        if (removedEntity) {
            shown = true;
            hide();
            return;
        }
        if (!isShown()) {
            LOGGER.warn("{} Entity that is not shown received update.", this);
            shown = true;
            hide();
            return;
        }
        Sprite sprite = template.getSprite();
        int offS = template.getShadowOffset();

        sprite.getDisplayArea(displayX, displayY, scale, 0.f, displayRect);

        int widthNoShadow = displayRect.getWidth() - (int) (offS * scale);

        if (fadingCorridorEffect) {
            currentlyEffectedByFadingCorridor = FadingCorridor.getInstance()
                    .isInCorridor(displayRect.getX(), displayRect.getY(), layerZ, widthNoShadow,
                                  displayRect.getHeight());

            if (currentlyEffectedByFadingCorridor) {
                setAlphaTarget(FADE_OUT_ALPHA);
            } else {
                setAlphaTarget(255);
            }
        }

        updateAlpha(delta);

        renderLight.setColor(getLocalLight());

        if (renderLight.getAlpha() == 0) {
            return;
        }

        if ((baseColor != null) || (overWriteBaseColor != null)) {
            if (overWriteBaseColor != null) {
                renderLight.multiply(overWriteBaseColor);
            } else {
                renderLight.multiply(baseColor);
            }
        }

        if (!renderLight.equals(lastRenderLight)) {
            lastRenderLight.setColor(renderLight);
        }

        if (getHighlight() > 0) {
            try {
                highlightEffect = container.getEngine().getAssets().getEffectManager().getHighlightEffect(true);
            } catch (EngineException e) {
                LOGGER.warn("Failed to fetch highlight effect.", e);
            }
        } else {
            highlightEffect = null;
        }
    }

    private boolean currentlyEffectedByFadingCorridor;

    @Nullable
    private HighlightEffect highlightEffect;

    @Nonnull
    private final Color tempLight = new Color(Color.WHITE);

    /**
     * Get the light local to this tile.
     *
     * @return the local light of this entity
     */
    @Nonnull
    public Color getLocalLight() {
        Color parentLight = getParentLight();
        if (parentLight == null) {
            return getLight();
        }
        if (parentLight.getAlpha() == 0) {
            return parentLight;
        }
        tempLight.setColor(parentLight);
        tempLight.multiply(getLight());
        return tempLight;
    }

    @Nonnull
    private final Rectangle displayRect = new Rectangle();
    @Nonnull
    private final Rectangle interactionRect = new Rectangle();

    /**
     * Get the current interactive area of the object.
     *
     * @return the interactive area of the object
     */
    @Nonnull
    public final Rectangle getInteractionRect() {
        int offS = template.getShadowOffset();
        if (offS == 0) {
            return displayRect;
        }

        interactionRect.set(displayRect);
        interactionRect.expand(0, 0, -offS, 0);
        return interactionRect;
    }

    /**
     * Get the current display rectangle.
     *
     * @return the current display rectangle
     */
    @Nonnull
    public final Rectangle getDisplayRect() {
        return displayRect;
    }

    private boolean fadingCorridorEffect;

    public void setFadingCorridorEffectEnabled(boolean value) {
        fadingCorridorEffect = value;
    }

    /**
     * This function checks if the entity is made transparent due the color its
     * drawn with.
     *
     * @return {@code true} in case the graphic is turned transparent due
     * its color
     */
    public boolean isTransparent() {
        return getAlpha() < 255;
    }

    /**
     * Update the alpha value. This function causes the alpha value to approach
     * the target alpha value and notifies in case its needed all listeners.
     *
     * @param delta the time in milliseconds since the last update
     */
    protected final void updateAlpha(int delta) {
        if (getAlpha() != getTargetAlpha()) {
            setAlpha(AnimationUtility.translate(getAlpha(), getTargetAlpha(), FADING_SPEED, 0, 255, delta));
        }
    }

    /**
     * Get the parent light of this entity. This is the light value that is supplied by some other object. The value of
     * this color is never altered by this class. The value can be {@code null} to assume the default light.
     *
     * @return the parent light
     */
    @Nullable
    protected Color getParentLight() {
        return null;
    }
}

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

import illarion.client.graphics.shader.HighlightShader;
import illarion.client.graphics.shader.Shader;
import illarion.client.graphics.shader.ShaderManager;
import illarion.client.world.World;
import illarion.common.annotation.NonNull;
import illarion.common.annotation.Nullable;
import illarion.common.types.Location;
import illarion.common.types.Rectangle;
import illarion.common.util.FastMath;
import illarion.common.util.RecycleObject;
import net.jcip.annotations.NotThreadSafe;
import org.apache.log4j.Logger;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

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
public abstract class AbstractEntity implements RecycleObject, DisplayItem, AlphaHandler, AnimatedFrame {
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
        private AlphaChangeListenerMulticast(final AlphaChangeListener l1, final AlphaChangeListener l2) {
            listener1 = l1;
            listener2 = l2;
        }

        /**
         * This method receives the alpha changed event and forwards its data to both added listeners.
         *
         * @param from the old alpha value
         * @param to   the new alpha value
         */
        @Override
        public void alphaChanged(final int from, final int to) {
            listener1.alphaChanged(from, to);
            listener2.alphaChanged(from, to);
        }
    }

    /**
     * The default light that is used in the client.
     */
    @NonNull
    protected static final Color DEFAULT_LIGHT = new Color(1.f, 1.f, 1.f, 1.f);

    /**
     * The speed value for fading the alpha values by default.
     */
    private static final int FADING_SPEED = 600;

    /**
     * The color value of the alpha when the object is faded out fully.
     */
    private static final int FADE_OUT_ALPHA = (int) (0.4f * 255);

    /**
     * The current alpha value of the sprite. This is used for fading the entity in and out.
     */
    private int alpha;

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
     * The ID of the entity. This is the ID the entity is stored in the recycle factory with.
     */
    private int entityID;

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
    @NonNull
    private Color localLight;

    /**
     * The light value that is used to render this entity during the next render loop.
     */
    @NonNull
    private Color renderLight = new Color(0);

    /**
     * This color is the color that was used last time to render the entity. Its used to check if the color changed
     * and the entity needs to be rendered again.
     */
    @NonNull
    private final Color lastRenderLight = new Color(0);

    /**
     * The shadow offset of the entity. This offset marks the area that does not apply to the fading corridor. This
     * is used to avoid that objects fade out when the player character walks into their shadow.
     */
    private final int offS;

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
     * The sprite that is the actual graphical representation of the entity.
     */
    @NonNull
    private final Sprite sprite;

    /**
     * The start and end frame of the animation of this entity.
     */
    private final int stillFrame;

    /**
     * This flag is used to determine if the scaling value is used at the rendering or not. Not using the scaling
     * value has a positive impact on the performance.
     */
    private boolean useScale;

    /**
     * Copy constructor to duplicate the object. This creates a copy of a entity that is able to render in exactly
     * the same way.
     *
     * @param org the original entity that shall be copied
     */
    protected AbstractEntity(@NonNull final AbstractEntity org) {
        // use same sprite as other entity
        sprite = org.sprite;

        stillFrame = org.stillFrame;
        entityID = org.entityID;
        offS = org.offS;
        baseColor = org.baseColor;
        fadingCorridorEffect = org.fadingCorridorEffect;
    }

    /**
     * Construct a entity based on a sprite image and a location.
     *
     * @param entityId     the ID of the entity
     * @param path         the path where the entity shall load the resources from
     * @param name         the base name of the image that shall be loaded in the sprite
     *                     of this entity
     * @param frames       the amount of frames of this entity
     * @param still        the first and the last frame of the frame animation
     * @param offX         the x offset of the entity sprite
     * @param offY         the y offset of the entity sprite
     * @param shadowOffset the shadow offset if the entity image, so the space
     *                     that does not apply to the fading corridor
     * @param horz         the horizontal alignment of the entity sprite, so the
     *                     horizontal position of the origin of the sprite
     * @param vert         the vertical alignment of the entity sprite, so the vertical
     *                     position of the origin of the sprite
     * @param smooth       true in case the entity graphic shall be smoothed, use this
     *                     in case the sprite needs to scale up and down
     * @deprecated Better use the function that offers the possibility to mirror the image and setting a base color
     */
    @Deprecated
    protected AbstractEntity(final int entityId, @NonNull final String path, @NonNull final String name,
                             final int frames, final int still, final int offX, final int offY,
                             final int shadowOffset, @NonNull final Sprite.HAlign horz,
                             @NonNull final Sprite.VAlign vert, final boolean smooth) {
        this(entityId, path, name, frames, still, offX, offY, shadowOffset,
                horz, vert, smooth, false, null);
    }

    /**
     * Construct a entity based on a sprite image and a location.
     *
     * @param entityId     the ID of the entity
     * @param path         the path where the entity shall load the resources from
     * @param name         the base name of the image that shall be loaded in the sprite of this entity
     * @param frames       the amount of frames of this entity
     * @param still        the first and the last frame of the frame animation
     * @param offX         the x offset of the entity sprite
     * @param offY         the y offset of the entity sprite
     * @param shadowOffset the shadow offset if the entity image, so the space that does not apply to the fading
     *                     corridor
     * @param horz         the horizontal alignment of the entity sprite, so the horizontal position of the origin of
     *                     the sprite
     * @param vert         the vertical alignment of the entity sprite, so the vertical position of the origin of the
     *                     sprite
     * @param smooth       true in case the entity graphic shall be smoothed, use this in case the sprite needs to
     *                     scale up and down
     * @param mirror       true in case the image of this entity has to be mirrored horizontal
     * @param baseCol      the base color of the image, the image will be always colored with this color,
     *                     set it to {@code null} in case there is not recoloring needed
     */
    protected AbstractEntity(final int entityId, @NonNull final String path, @NonNull final String name,
                             final int frames, final int still, final int offX, final int offY,
                             final int shadowOffset, @NonNull final Sprite.HAlign horz,
                             @NonNull final Sprite.VAlign vert, final boolean smooth, final boolean mirror,
                             @Nullable final Color baseCol) {

        sprite = SpriteBuffer.getInstance().getSprite(path, name, frames, offX, offY, horz, vert, smooth, mirror);
        stillFrame = still;
        currentFrame = still;
        if ((baseCol == null) || baseCol.equals(DEFAULT_LIGHT)) {
            baseColor = null;
        } else {
            baseColor = baseCol;
        }

        entityID = entityId;
        offS = shadowOffset;
    }

    /**
     * Construct a entity based on a sprite image and a location.
     *
     * @param entityId        the ID of the entity
     * @param displayedSprite the sprite that is displayed in this entity
     * @param still           the first and the last frame of the frame animation
     * @param shadowOffset    the shadow offset if the entity image, so the space that does not apply to the fading
     *                        corridor
     * @param baseCol         the base color of the image, the image will be always colored with this color,
     *                        set it to {@code null} in case there is not recoloring needed
     */
    protected AbstractEntity(final int entityId, @NonNull final Sprite displayedSprite, final int still,
                             final int shadowOffset, @Nullable final Color baseCol) {

        sprite = displayedSprite;
        stillFrame = still;
        currentFrame = still;
        if ((baseCol == null) || baseCol.equals(DEFAULT_LIGHT)) {
            baseColor = null;
        } else {
            baseColor = baseCol;
        }

        entityID = entityId;
        offS = shadowOffset;
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

    @Override
    public void addAlphaChangeListener(@NonNull final AlphaChangeListener listener) {
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
    public void animationFinished(final boolean finished) {
        // nothing needs to be done by default
    }

    /**
     * Set a new base color of the entity.
     *
     * @param newBaseColor the new base color of the entity, {@code null} to get the default color
     */
    public void changeBaseColor(@Nullable final Color newBaseColor) {
        if (newBaseColor == null) {
            overWriteBaseColor = null;
            return;
        }

        if (overWriteBaseColor == null) {
            overWriteBaseColor = new Color(newBaseColor);
        } else {
            copyLightValues(newBaseColor, overWriteBaseColor);
        }
    }

    /**
     * The clone operation creates a copy of the entity in case it is needed.
     */
    @Override
    public abstract AbstractEntity clone();

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
     *
     * @return true in case the rendering operation was done successfully
     */
    @Override
    public boolean draw(@NonNull final Graphics g) {
        final int renderLocX = displayX;
        final int renderLocY = displayY;

        if (!Camera.getInstance().requiresUpdate(displayRect)) {
            return true;
        }

        final Rectangle parentDirtyArea = Camera.getInstance().getDirtyArea(displayRect);
        if ((parentDirtyArea != null) && !parentDirtyArea.equals(Camera.getInstance().getViewport())) {
            g.setClip(parentDirtyArea.getX() - Camera.getInstance().getViewportOffsetX(),
                    (parentDirtyArea.getY() - Camera.getInstance().getViewportOffsetY()) +
                            World.getMapDisplay().getHeightOffset(),
                    parentDirtyArea.getWidth(), parentDirtyArea.getHeight());
        }


        HighlightShader shader = null;
        final int highlight = getHighlight();
        if (highlight > 0) {
            shader = ShaderManager.getShader(Shader.Highlight, HighlightShader.class);
            shader.bind();
            shader.setTexture(0);
            if (highlight == 1) {
                shader.setHighlightShare(0.05f);
            } else {
                shader.setHighlightShare(0.25f);
            }
        }

        if (useScale) {
            sprite.draw(g, renderLocX, renderLocY, renderLight, currentFrame, scale);
        } else {
            sprite.draw(g, renderLocX, renderLocY, renderLight, currentFrame);
        }

        if (shader != null) {
            shader.unbind();
        }

        if ((parentDirtyArea != null) && !parentDirtyArea.equals(Camera.getInstance().getViewport())) {
            g.clearClip();
        }

        Camera.getInstance().markAreaRendered(displayRect);

        return true;
    }

    /**
     * Get the current alpha value.
     *
     * @return the alpha value
     */
    @Override
    public int getAlpha() {
        return alpha;
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
     * Get the amount of frames this entity contains.
     *
     * @return the amount of frames
     */
    public final int getFrames() {
        return sprite.getFrames();
    }

    /**
     * Get the height of the entity graphic.
     *
     * @return the height of the entity image
     */
    public final int getHeight() {
        return sprite.getHeight();
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

    /**
     * Get the current light instance that is used by this entity.
     *
     * @return the light this entity uses at the rendering functions
     */
    @Nullable
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
     * Get the sprite of this entity.
     *
     * @return the sprite of the entity
     */
    @NonNull
    public final Sprite getSprite() {
        return sprite;
    }

    /**
     * Get the current target of the alpha approaching.
     *
     * @return the current alpha target value
     */
    public final int getTargetAlpha() {
        return alphaTarget;
    }

    /**
     * Get the width of the entity graphic.
     *
     * @return the width of the entity image
     */
    public final int getWidth() {
        return sprite.getWidth();
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
     * Hide the entity from the screen by removing it from the display list.
     */
    @Override
    public void hide() {
        if (shown) {
            World.getMapDisplay().remove(this);
            shown = false;
        }
    }

    /**
     * Check if the entity is visible.
     *
     * @return true in case the entity is visible
     */
    public final boolean isVisible() {
        return (alphaTarget > 0) || (alpha > 0);
    }

    /**
     * Clean up the entity. This sets all colors and animations.
     */
    @Override
    public void reset() {
        // start as opaque
        alpha = 255;
        alphaTarget = 255;
        currentFrame = stillFrame;
        setLight(sprite.getDefaultLight());
        overWriteBaseColor = null;
        alphaListener = null;
    }

    /**
     * Set the current alpha value of the entity. This causes that the alpha value is changed right away without any
     * fading effect. To get a fading effect use {@link #setAlphaTarget(int)}.
     *
     * @param newAlpha the new alpha value of this entity
     */
    @Override
    public final void setAlpha(final int newAlpha) {
        if (alpha != newAlpha) {
            final int oldAlpha = alpha;
            alpha = newAlpha;
            if (alphaListener != null) {
                alphaListener.alphaChanged(oldAlpha, alpha);
            }
            wentDirty = true;
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
    public final void setAlphaTarget(final int newAlphaTarget) {
        alphaTarget = newAlphaTarget;
    }

    /**
     * Set the base color of this entity. This operation does not create a copy of this reference.
     *
     * @param newBaseColor the new base color of the entity
     */
    public void setBaseColor(@Nullable final Color newBaseColor) {
        baseColor = newBaseColor;
    }

    /**
     * Set the frame that is currently displayed at the render functions of this entity.
     *
     * @param frame the index of the frame that is displayed
     */
    @Override
    public void setFrame(final int frame) {
        if (currentFrame != frame) {
            currentFrame = frame;
            wentDirty = true;
        }
    }

    /**
     * Set the current light of this entity. This sets the instance that is set as parameter directly as local light
     * color. So any changes applied to the instance that was transferred will effect the light of this entity.
     *
     * @param light the new light that shall be used by this entity
     */
    public void setLight(@NonNull final Color light) {
        localLight = light;
    }

    /**
     * Set the scaling that shall be applied to this entity.
     *
     * @param newScale the new scaling value applied to this entity
     */
    public void setScale(final float newScale) {
        if (scale != newScale) {
            scale = newScale;
            useScale = FastMath.abs(1.f - newScale) > FastMath.FLT_EPSILON;
            wentDirty = true;
        }
    }

    /**
     * Set the position of the entity on the display. The display origin is at the origin of the game map.
     *
     * @param dispX     the x coordinate of the location of the display
     * @param dispY     the y coordinate of the location of the display
     * @param zLayer    the z layer of the coordinate
     * @param typeLayer the global layer of this type of entity.
     */
    public void setScreenPos(final int dispX, final int dispY, final int zLayer, final int typeLayer) {
        if ((dispX != displayX) || (dispY != displayY)) {
            wentDirty = true;
        }

        displayX = dispX;
        displayY = dispY;

        if (shown) {
            final int newLayerZ = zLayer - typeLayer;
            if (newLayerZ != layerZ) {
                updateDisplayPosition();
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
     * @param loc       the location of the entity on the map
     * @param typeLayer the global layer of this type of entity.
     */
    public final void setScreenPos(final Location loc, final int typeLayer) {
        setScreenPos(loc.getDcX(), loc.getDcY(), loc.getDcZ(), typeLayer);
    }

    @Override
    public boolean processEvent(@NonNull final GameContainer container, final int delta,
                                @NonNull final MapInteractionEvent event) {
        return false;
    }

    protected boolean isMouseInInteractionRect(final int mouseX, final int mouseY) {
        final int mouseXonDisplay = mouseX + Camera.getInstance().getViewportOffsetX();
        final int mouseYonDisplay = mouseY + Camera.getInstance().getViewportOffsetY();

        return getInteractionRect().isInside(mouseXonDisplay, mouseYonDisplay);
    }

    protected boolean isMouseInInteractionRect(@NonNull final Input input) {
        return isMouseInInteractionRect(input.getMouseX(), input.getMouseY());
    }

    /**
     * The logging instance of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(AbstractEntity.class);

    @Override
    public void show() {
        if (shown) {
            LOGGER.error("Added entity twice.");
        } else {
            World.getMapDisplay().add(this);
            shown = true;
            wentDirty = true;
        }
    }

    /**
     * Update the position of this entity in the display list.
     */
    public void updateDisplayPosition() {
        if (shown) {
            World.getMapDisplay().readd(this);
            wentDirty = true;
        } else {
            LOGGER.error("Updated display location for hidden item.");
        }
    }

    @Override
    public void update(@NonNull final GameContainer container, final int delta) {
        int xOffset = sprite.getOffsetX() + sprite.getAlignOffsetX();
        int yOffset = sprite.getOffsetY() - sprite.getAlignOffsetY();

        int width = sprite.getWidth();
        int widthNoShadow = width - offS;
        int height = sprite.getHeight();

        if (useScale) {
            xOffset *= scale;
            yOffset *= scale;
            width *= scale;
            widthNoShadow *= scale;
            height *= scale;
        }

        final int scrX = displayX + xOffset;
        final int scrY = displayY - yOffset;

        displayRect.set(scrX, scrY, width, height);

        if (fadingCorridorEffect) {
            final boolean transparent =
                    FadingCorridor.getInstance().isInCorridor(scrX, scrY, layerZ,
                            widthNoShadow, height);

            if (transparent) {
                setAlphaTarget(FADE_OUT_ALPHA);
            } else {
                setAlphaTarget(255);
            }
        }

        updateAlpha(delta);

        localLight.a = getAlpha() / 255.f;

        if ((baseColor == null) && (overWriteBaseColor == null)) {
            copyLightValues(localLight, renderLight);
        } else {
            if (overWriteBaseColor != null) {
                renderLight = localLight.multiply(overWriteBaseColor);
            } else {
                renderLight = localLight.multiply(baseColor);
            }
        }

        if (!renderLight.equals(lastRenderLight)) {
            wentDirty = true;
            copyLightValues(renderLight, lastRenderLight);
        }

        setEntityAreaDirty();
    }

    private static void copyLightValues(@NonNull final Color source, @NonNull final Color target) {
        target.r = source.r;
        target.g = source.g;
        target.b = source.b;
        target.a = source.a;
    }

    @NonNull
    private final Rectangle displayRect = new Rectangle();
    @NonNull
    private final Rectangle interactionRect = new Rectangle();
    @NonNull
    private final Rectangle lastDisplayRect = new Rectangle();
    private boolean wentDirty;

    /**
     * Get the current interactive area of the object.
     *
     * @return the interactive area of the object
     */
    @NonNull
    public final Rectangle getInteractionRect() {
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
    @NonNull
    public final Rectangle getDisplayRect() {
        return displayRect;
    }

    /**
     * The display rectangle that was active when rendering the last frame.
     *
     * @return the last display rectangle
     */
    @Override
    @NonNull
    public Rectangle getLastDisplayRect() {
        return lastDisplayRect;
    }

    public final void setEntityAreaDirty() {
        if (wentDirty || !lastDisplayRect.equals(displayRect)) {
            wentDirty = false;
            if (!lastDisplayRect.isEmpty()) {
                Camera.getInstance().markAreaDirty(lastDisplayRect);
                if (displayRect.equals(lastDisplayRect)) {
                    return;
                }
            }

            lastDisplayRect.set(displayRect);
            Camera.getInstance().markAreaDirty(displayRect);
        }
    }

    private boolean fadingCorridorEffect;

    public void setFadingCorridorEffectEnabled(final boolean value) {
        fadingCorridorEffect = value;
    }

    /**
     * This function checks if the entity is made transparent due the color its
     * drawn with.
     *
     * @return <code>true</code> in case the graphic is turned transparent due
     *         its color
     */
    protected boolean isTransparent() {
        return alpha < 255;
    }

    /**
     * Update the alpha value. This function causes the alpha value to approach
     * the target alpha value and notifies in case its needed all listeners.
     *
     * @param delta the time in milliseconds since the last update
     */
    protected final void updateAlpha(final int delta) {
        if (alpha != alphaTarget) {
            setAlpha(AnimationUtility.translate(alpha, alphaTarget, FADING_SPEED, FADE_OUT_ALPHA, 255, delta));
        }
    }

    /**
     * This function returns if this entity is using the scale value. This value
     * can be used for optimization.
     *
     * @return <code>true</code> in case the scaling value is used
     */
    protected boolean usingScale() {
        return useScale;
    }
}

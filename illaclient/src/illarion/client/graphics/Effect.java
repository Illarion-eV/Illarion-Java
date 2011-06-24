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

import illarion.client.world.Game;

import illarion.common.graphics.Layers;
import illarion.common.util.Location;

import illarion.graphics.Sprite;
import illarion.graphics.common.LightSource;

/**
 * A effect is a frame based animation that shows at one tile on the game map.
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.95
 */
public final class Effect extends AbstractEntity {
    /**
     * The path to the folder that contains the images for the graphic effects.
     */
    @SuppressWarnings("nls")
    private static final String EFFECTS_PATH = "data/effects/";

    /**
     * The frame animation that handles the animation of this effect.
     */
    private transient final FrameAnimation ani;

    /**
     * The encoded light value of the light that is emitted by the effect.
     */
    private final int light;

    /**
     * The light source of that effect that emits the light of this effect.
     */
    private LightSource lightSrc;

    /**
     * The animation speed the effect is shown with. The larger this number the
     * longer the effect animation shows.
     */
    private final int speed;

    /**
     * Constructor that defines a new effect. This causes that the resources are
     * loaded from the file system in case its not done and everything is set up
     * to show this effect.
     * 
     * @param effectID the ID of the effect
     * @param name the base name of the images for this effect
     * @param frames the amount of frames of the animation of this effect
     * @param offX the x offset of the graphic of this effect
     * @param offY the y offset of the graphic of this effect
     * @param animSpeed the speed of the animation, the larger the number, the
     *            longer the animation takes
     * @param effectLight the encoded light value of the light that is emitted
     *            by this effect
     */
    protected Effect(final int effectID, final String name, final int frames,
        final int offX, final int offY, final int animSpeed,
        final int effectLight) {
        super(effectID, EFFECTS_PATH, name, frames, 0, offX, offY, 0,
            Sprite.HAlign.center, Sprite.VAlign.middle, false, false, null);
        speed = animSpeed;
        light = effectLight;
        if (frames > 1) {
            ani = new FrameAnimation(this);
            ani.setup(frames, 0, speed, 0);
        } else {
            ani = null;
        }
        reset();
    }

    /**
     * The copy constructor that created a exact copy of a original effect.
     * 
     * @param org the instance of this class that shall be copied
     */
    private Effect(final Effect org) {
        super(org);
        speed = org.speed;
        light = org.light;

        if (org.ani == null) {
            ani = null;
        } else {
            ani = new FrameAnimation(this, org.ani);
        }
        reset();
    }

    /**
     * Create a new effect. This requests a new effect from the EffectFactory.
     * So the effect returned by this function is either a newly created object
     * or a unused one from the factory.
     * 
     * @param effectID the id of the effect that is needed
     * @return the instance of Effect that shall be used
     */
    public static Effect create(final int effectID) {
        return EffectFactory.getInstance().getCommand(effectID);
    }

    /**
     * Activate the effect and prepare to use it.
     * 
     * @param effectID the effect ID this instance of effect was fetched with,
     *            due some ID mappings in the EffectFactory its possible that
     *            this is not the same ID as the ID the effect was created with
     */
    @Override
    public void activate(final int effectID) {
        // nothing to be done
    }

    /**
     * Function that is called after a animation finished playing. For a effect
     * the end of a animation means that the object can be recycled for later
     * reuse because it does not have to show anymore.
     * 
     * @param finished true in case the animation is done, false if it got
     *            canceled
     */
    @Override
    public void animationFinished(final boolean finished) {
        // remove effect after it has played once
        recycle();
    }

    /**
     * Create a duplicate of this effect in order to get a new instance of this
     * effect that are needed if more effects then one show at the same time on
     * the screen.
     * 
     * @return a new instance that is a exact copy of this instance
     */
    @Override
    public Effect clone() {
        return new Effect(this);
    }

    /**
     * Hide the effect from the screen. This hides the graphic of the effect
     * itself and stops the animation.
     */
    @Override
    public void hide() {
        super.hide();

        if (ani != null) {
            ani.stop();
        }
    }

    /**
     * Recycle this object, so put it back to the recycle factory so the object
     * can be reused in case another instance of this object with the same
     * specifications is needed again.
     */
    @Override
    public void recycle() {
        EffectFactory.getInstance().recycle(this);
    }

    /**
     * Clean up this instance of effect. This is done when the life time of the
     * effect runs out and the instance is but back to the recycle factory. So
     * this function is called by the recycle factory.
     * <p>
     * Effect activate references of this effect are removed by this, so the
     * graphic is removed from the display list and the light source is removed
     * from the LightTracer.
     * </p>
     */
    @Override
    public void reset() {
        super.reset();
        hide();

        if (lightSrc != null) {
            Game.getLights().remove(lightSrc);
            LightSource.releaseLight(lightSrc);
            lightSrc = null;
        }
    }

    /**
     * Show a effect on a specified location on the map. This creates the light
     * of the effect on the map and starts the animation of the effect.
     * 
     * @param loc the location on the game map the effect shall be shown on
     */
    public void show(final Location loc) {
        setScreenPos(loc, Layers.EFFECTS);
        if (ani != null) {
            ani.restart();
        }
        super.show();

        if (light > 0) {
            if (lightSrc != null) {
                Game.getLights().remove(lightSrc);
                LightSource.releaseLight(lightSrc);
            }
            lightSrc = LightSource.createLight(loc, light);
            Game.getLights().add(lightSrc);
            setLight(DEFAULT_LIGHT);
        }
    }
}

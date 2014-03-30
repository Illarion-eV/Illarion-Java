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

import illarion.client.resources.EffectFactory;
import illarion.client.resources.Resource;
import illarion.client.resources.data.EffectTemplate;
import illarion.client.world.World;
import illarion.common.graphics.Layers;
import illarion.common.types.Location;
import org.illarion.engine.graphic.LightSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A effect is a frame based animation that shows at one tile on the game map.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@SuppressWarnings("ClassNamingConvention")
public final class Effect extends AbstractEntity<EffectTemplate> implements Resource {
    /**
     * The frame animation that handles the animation of this effect.
     */
    @Nullable
    private final FrameAnimation animation;

    /**
     * The light source of that effect that emits the light of this effect.
     */
    @Nullable
    private LightSource lightSrc;

    /**
     * Default constructor for a graphics effect displayed on the map.
     *
     * @param template the template used to create a new item
     */
    public Effect(@Nonnull final EffectTemplate template) {
        super(template);

        if (template.getFrames() > 0) {
            animation = new FrameAnimation(this);
            animation.setup(template.getFrames(), 0, template.getAnimationSpeed(), 0);
        } else {
            animation = null;
        }
    }

    /**
     * Create a new effect. This requests a new effect from the EffectFactory. So the effect returned by this
     * function is either a newly created object or a unused one from the factory.
     *
     * @param effectID the id of the effect that is needed
     * @return the instance of Effect that shall be used
     */
    @Nonnull
    public static Effect create(final int effectID) {
        return new Effect(EffectFactory.getInstance().getTemplate(effectID));
    }

    /**
     * Function that is called after a animation finished playing. For a effect the end of a animation means that the
     * object can be recycled for later reuse because it does not have to show anymore.
     *
     * @param finished true in case the animation is done, false if it got canceled
     */
    @Override
    public void animationFinished(final boolean finished) {
        hide();
    }

    /**
     * Hide the effect from the screen. This hides the graphic of the effect itself and stops the animation.
     */
    @Override
    public void hide() {
        if (animation != null) {
            animation.stop();
        }
        if (lightSrc != null) {
            World.getLights().remove(lightSrc);
            LightSource.releaseLight(lightSrc);
            lightSrc = null;
        }
        super.hide();
    }

    /**
     * Show a effect on a specified location on the map. This creates the light of the effect on the map and starts
     * the animation of the effect.
     *
     * @param loc the location on the game map the effect shall be shown on
     */
    public void show(@Nonnull final Location loc) {
        setScreenPos(loc, Layers.EFFECTS);
        if (animation != null) {
            animation.restart();
        }
        show();

        if (getTemplate().getLight() > 0) {
            if (lightSrc != null) {
                World.getLights().remove(lightSrc);
                LightSource.releaseLight(lightSrc);
            }
            setLight(DEFAULT_LIGHT);
            lightSrc = LightSource.createLight(loc, getTemplate().getLight());
            World.getLights().add(lightSrc);
        }
    }
}

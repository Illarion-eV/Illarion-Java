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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * The main animation manager that handles and updates all animations that are registered. This class handles only the
 * updates to the animations. Rendering the results of the updates must be done at another position.
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class AnimationManager {

    /**
     * The animation to add.
     */
    @Nonnull
    private final Queue<AbstractAnimation<?>> addAnimations;

    /**
     * The list of animations that are registered to the manager. All animations in here need to be updated when the
     * animate function is called.
     */
    @Nonnull
    private final List<AbstractAnimation<?>> animations;

    /**
     * The private constructor of this class. This ensures that the only instance of this class is the singleton
     * instance.
     */
    public AnimationManager() {
        animations = new ArrayList<>();
        addAnimations = new LinkedList<>();
    }

    /**
     * Update all animations to the new state for the next rendering.
     *
     * @param delta the time since the last update of the animations. Its only needed to update the animations right
     * before a rendering run
     */
    public void animate(int delta) {
        if (!addAnimations.isEmpty()) {
            AbstractAnimation<?> ani;
            while ((ani = addAnimations.poll()) != null) {
                if (!animations.contains(ani)) {
                    animations.add(ani);
                }
            }
        }

        animations.removeIf(ani -> {
            // execute those that are running
            if (!ani.isRunning()) {
                ani.animationFinished(false);
            } else if (!ani.animate(delta)) {
                ani.animationFinished(true);
            } else {
                return false;
            }
            return true;
        });
    }

    /**
     * Add an animation to this animation manager. Every animation that is registered to the Animation Manager is
     * notified at every call of {@link #animate(int)}. Animations are deleted automatically in case they stopped
     * running.
     *
     * @param animation the animation that shall be to the animation manager
     */
    void register(@Nonnull AbstractAnimation<?> animation) {
        if (!addAnimations.contains(animation)) {
            addAnimations.add(animation);
        }
    }
}

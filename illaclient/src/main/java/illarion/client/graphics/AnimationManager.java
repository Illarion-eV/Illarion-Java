/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2013 - Illarion e.V.
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

import javolution.util.FastTable;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * The main animation manager that handles and updates all animations that are
 * registered. This class handles only the updates to the animations. Rendering
 * the results of the updates must be done at another position.
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class AnimationManager {

    /**
     * The animation to add.
     */
    @Nonnull
    private final List<AbstractAnimation> addAnimations;

    /**
     * The list of animations that are registered to the manager. All animations
     * in here need to be updated when the animate function is called.
     */
    @Nonnull
    private final List<AbstractAnimation> animations;

    /**
     * The private constructor of this class. This ensures that the only
     * instance of this class is the singleton instance.
     */
    public AnimationManager() {
        animations = new FastTable<AbstractAnimation>();
        addAnimations = new FastTable<AbstractAnimation>();
    }

    /**
     * Update all animations to the new state for the next rendering.
     *
     * @param delta the time since the last update of the animations. Its only
     *              needed to update the animations right before a rendering run
     */
    public void animate(final int delta) {
        while (!addAnimations.isEmpty()) {
            final AbstractAnimation ani = addAnimations.remove(0);
            if (!animations.contains(ani)) {
                animations.add(ani);
            }
        }

        int count = animations.size();
        for (int i = 0; i < count; ++i) {
            final AbstractAnimation ani = animations.get(i);
            // execute those that are running
            if (ani == null || !ani.isRunning() || !ani.animate(delta)) {
                animations.remove(i);
                if (ani != null) {
                    ani.animationFinished(true);
                }
                --count;
                --i;
            }
        }
    }

    /**
     * Add an animation to this animation manager. Every animation that is
     * registered to the Animation Manager is notified at every call of
     * {@link #animate(int)}. Animations are deleted automatically in case they
     * stopped running.
     *
     * @param animation the animation that shall be to the animation manager
     */
    protected void register(final AbstractAnimation animation) {
        if (!addAnimations.contains(animation)) {
            addAnimations.add(animation);
        }
    }
}

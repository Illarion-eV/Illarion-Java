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

import illarion.common.graphics.CharAnimations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Additional Info for an avatar. Every avatar for every avatar gets only one Avatar info object and has to report
 * all animations known for this character to the object.
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class AvatarInfo {
    /**
     * The buffer of constructs of this class that is generated during the creation process and stores all AvatarInfo
     * constructs that were generated. This is needed to ensure that there is only one instance for each avatar
     * appearance. This object is deleted after the characters are loaded completely.
     */
    @Nullable
    private static Map<Integer, AvatarInfo> buffer = new HashMap<>();

    /**
     * The list of animations known to this avatar. All animation index values marked with true need to be playable.
     */
    @Nonnull
    private final boolean[] animations;

    /**
     * The visibility modifier of the avatar. 100 means normal visibility, values greater then 100 improve the
     * visibility, values below 100 lower the visibility.
     */
    private final int visibility;

    /**
     * Default constructor of the avatar information.
     *
     * @param visibilityMod the visibility modifications for the avatar in percent. Values above 100 increase the
     * default visibility.
     */
    private AvatarInfo(final int visibilityMod) {
        visibility = visibilityMod;
        animations = new boolean[CharAnimations.DEFINED_ANIMATIONS];
    }

    /**
     * Clean this class up. This should be called after all avatars are loaded. It ensures that is becomes impossible
     * to modify the instances of this class and the unneeded buffer table is emptied and removed.
     */
    public static void cleanup() {
        if (buffer != null) {
            buffer.clear();
        }
        buffer = null;
    }

    /**
     * Get the avatar instance for a avatar appearance with some set information about the avatar. This function
     * creates a new instance of the AvatarInfo class with the parameters, or returns a already created one for this
     * appearance.
     *
     * @param appearance the appearance this avatar information are related to
     * @param visibilityMod the visibility modifications for the avatar in percent. Values above 100 increase the
     * default visibility.
     * @return the newly created instance of AvatarInfo or a already created one from the cache
     */
    public static AvatarInfo getInstance(final int appearance, final int visibilityMod) {
        if (buffer != null) {
            if (buffer.containsKey(appearance)) {
                return buffer.get(appearance);
            }
        } else {
            LOGGER.error("Requested new avatar information after setup was done. Efficiency degrading!");
        }

        final AvatarInfo newInfo = new AvatarInfo(visibilityMod);
        if (buffer != null) {
            buffer.put(appearance, newInfo);
        }
        return newInfo;
    }

    /**
     * The instance of the logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AvatarInfo.class);

    /**
     * Get the visibility modifications of the avatar. The modifications is set in percent. Use the return of this
     * function in general in the following way: {@code realVisibility = defaultVisibility * functionReturn / 100}
     *
     * @return 100 for the normal visibility, greater values for larger visibility and lower values for less visibility
     */
    public int getVisibility() {
        return visibility;
    }

    /**
     * Check if a animation is set for this avatar. In case the animation is available its possible to play that
     * animation.
     *
     * @param animationID the ID of the animation that shall be checked for availability
     * @return {@code true} in case the animation is available
     */
    public boolean isAnimationAvailable(final int animationID) {
        return animations[animationID];
    }

    /**
     * Report to this Avatar info that a particular animation is available. After reporting this it must be ensured
     * that this animation is available to be shown.
     *
     * @param animationID the ID of the animation that is available.
     */
    @SuppressWarnings("nls")
    public void reportAnimation(final int animationID) {
        if (buffer == null) {
            throw new IllegalStateException("Changing this construct is not allowed after the creation process.");
        }
        animations[animationID] = true;
    }
}

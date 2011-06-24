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

import java.util.HashMap;
import java.util.Map;

import illarion.common.graphics.CharAnimations;

/**
 * Additional Info for an avatar. Every avatar for every avatar gets only one
 * Avatar info object and has to report all animations known for this character
 * to the object.
 * 
 * @author Nop
 * @author Martin Karing
 * @since 0.95
 */
final class AvatarInfo {
    /**
     * The buffer of constructs of this class that is generated during the
     * creation process and stores all AvatarInfo constructs that were
     * generated. This is needed to ensure that there is only one instance for
     * each avatar appearance. This object is deleted after the characters are
     * loaded completely.
     */
    private static Map<Integer, AvatarInfo> buffer =
        new HashMap<Integer, AvatarInfo>();

    /**
     * The setup flag. While this flag is set to true, its possible to change
     * the animation informations of this class.
     */
    private static boolean setup = true;

    /**
     * The list of animations known to this avatar. All animation index values
     * marked with true need to be playable.
     */
    private final boolean[] animations;

    /**
     * The English name of this avatar.
     */
    private final String english;

    /**
     * The German name of this avatar.
     */
    private final String german;

    /**
     * The visibility modifier of the avatar. 100 means normal visibility,
     * values greater then 100 improve the visibility, values below 100 lower
     * the visibility.
     */
    private final int visibility;

    /**
     * Default constructor of the avatar informations.
     * 
     * @param visibilityMod the visibility modificator for the avatar in
     *            percent. Values above 100 increase the default visibility.
     * @param germanDesc the German description of the avatar. This is shown in
     *            the name tag of the avatar in case the name of the character
     *            is unknown
     * @param englishDesc the English description of the avatar. This is shown
     *            in the name tag of the avatar in case the name of the
     *            character is unknown
     */
    private AvatarInfo(final int visibilityMod, final String germanDesc,
        final String englishDesc) {
        english = englishDesc;
        german = germanDesc;
        visibility = visibilityMod;
        animations = new boolean[CharAnimations.DEFINED_ANIMATIONS];
    }

    /**
     * Clean this class up. This should be called after all avatars are loaded.
     * It ensures that is becomes impossible to modify the instances of this
     * class and the unneeded buffer table is emptied and removed.
     */
    protected static void cleanup() {
        buffer.clear();
        buffer = null;
        setup = false;
    }

    /**
     * Get the avatar instance for a avatar appearance with some set
     * informations about the avatar. This function creates a new instance of
     * the AvatarInfo class with the parameters, or returns a already created
     * one for this appearance.
     * 
     * @param appearance the appearance this avatar informations are related to
     * @param visibilityMod the visibility modificator for the avatar in
     *            percent. Values above 100 increase the default visibility.
     * @param germanDesc the German description of the avatar. This is shown in
     *            the name tag of the avatar in case the name of the character
     *            is unknown
     * @param englishDesc the English description of the avatar. This is shown
     *            in the name tag of the avatar in case the name of the
     *            character is unknown
     * @return the newly created instance of AvatarInfo or a already created one
     *         from the cache
     */
    protected static AvatarInfo get(final int appearance,
        final int visibilityMod, final String germanDesc,
        final String englishDesc) {

        final Integer key = Integer.valueOf(appearance);
        if (buffer.containsKey(key)) {
            return buffer.get(key);
        }

        final AvatarInfo newInfo =
            new AvatarInfo(visibilityMod, germanDesc, englishDesc);
        buffer.put(key, newInfo);
        return newInfo;
    }

    /**
     * The English description of the avatar that can be used in case the name
     * of the character is not known.
     * 
     * @return the English name of this avatar
     */
    public String getEnglish() {
        return english;
    }

    /**
     * The German description of the avatar that can be used in case the name of
     * the character is not known.
     * 
     * @return the German name of this avatar
     */
    public String getGerman() {
        return german;
    }

    /**
     * Get the visibility modificator of the avatar. The modificator is set in
     * percent. Use the return of this function in general in the following way:
     * <code>realVisibility = defaultVisibility * functionReturn / 100</code>
     * 
     * @return 100 for the normal visibility, greater values for larger
     *         visibility and lower values for less visibility
     */
    public int getVisibility() {
        return visibility;
    }

    /**
     * Check if a animation is set for this avatar. In case the animation is
     * available its possible to play that animation.
     * 
     * @param animationID the ID of the animation that shall be checked for
     *            availability
     * @return true in case the animation is available
     */
    protected boolean animationAvaiable(final int animationID) {
        return animations[animationID];
    }

    /**
     * Report to this Avatar info that a particular animation is available.
     * After reporting this it must be ensured that this animation is available
     * to be shown.
     * 
     * @param animationID the ID of the animation that is available.
     */
    @SuppressWarnings("nls")
    protected void reportAnimation(final int animationID) {
        if (!setup) {
            throw new IllegalStateException("Changing this construct is not "
                + "allowed after the creation process.");
        }
        animations[animationID] = true;
    }
}

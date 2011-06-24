/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Common Library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Common Library. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.graphics;

/**
 * This class contains the constants for all character animations that are
 * defined. Also it builds the texts for the configuration tool to refer to the
 * animations properly.
 * 
 * @author Martin Karing
 * @since 1.22
 */
@SuppressWarnings("nls")
public final class CharAnimations {
    /**
     * The array of the names of the animations.
     */
    public static final String[] ANIMATION_NAMES;

    /**
     * Constant for the one handed attack animation.
     */
    public static final int ATTACK_1HAND;

    /**
     * Constant for the two handed attack animation.
     */
    public static final int ATTACK_2HAND;

    /**
     * Constant for the blocking an attack animation.
     */
    public static final int ATTACK_BLOCK;

    /**
     * Constant for the attack with a bow animation.
     */
    public static final int ATTACK_BOW;

    /**
     * Constant for the attacking with a crossbow animation.
     */
    public static final int ATTACK_CROSSBOW;

    /**
     * Constant for the getting hit by a attack animation.
     */
    public static final int ATTACK_GET_HIT;

    /**
     * Constant for the casting a spell animation.
     */
    public static final int CAST_SPELL;

    /**
     * The amount of animations that were defined to be used. All others are
     * named as undefined.
     */
    public static final int DEFINED_ANIMATIONS;

    /**
     * Constant for the drinking something animation.
     */
    public static final int DRINK;

    /**
     * Constant for the lie one the ground animation.
     */
    public static final int LIE_DOWN;

    /**
     * Constant for the picking something up animation.
     */
    public static final int PICK_UP;

    /**
     * Constant for the running animation.
     */
    public static final int RUN;

    /**
     * Constant for the sitting animation.
     */
    public static final int SIT;

    /**
     * Constant for the standing animation.
     */
    public static final int STAND;

    /**
     * Constant for the standing around idle animation.
     */
    public static final int STAND_IDLE;

    /**
     * The total count of animations that are allowed to be defined. Changing
     * this value results in a change of the IDs of the avatars and require a
     * full rebuild of the avatar and cloth lists.
     */
    public static final int TOTAL_ANIMATIONS = 50;

    /**
     * Constant for the walking animation.
     */
    public static final int WALK;

    /**
     * Constant for the chopping animation.
     */
    public static final int WORK_CHOPPING;

    /**
     * Constant for the fishing animation.
     */
    public static final int WORK_FISH;

    /**
     * Constant for the mining animation.
     */
    public static final int WORK_MINING;

    /**
     * Constant for the sawing animation.
     */
    public static final int WORK_SAW;

    /**
     * Constant for the forging animation.
     */
    public static final int WORK_SMITH;

    static {
        int i = 0;
        STAND = i++;
        SIT = i++;
        LIE_DOWN = i++;
        WALK = i++;
        STAND_IDLE = i++;
        ATTACK_1HAND = i++;
        ATTACK_2HAND = i++;
        ATTACK_BOW = i++;
        ATTACK_CROSSBOW = i++;
        ATTACK_BLOCK = i++;
        ATTACK_GET_HIT = i++;
        CAST_SPELL = i++;
        DRINK = i++;
        PICK_UP = i++;
        WORK_SMITH = i++;
        WORK_MINING = i++;
        WORK_SAW = i++;
        WORK_CHOPPING = i++;
        WORK_FISH = i++;
        RUN = i++;

        DEFINED_ANIMATIONS = i;

        ANIMATION_NAMES = new String[TOTAL_ANIMATIONS];

        ANIMATION_NAMES[STAND] = "Stand";
        ANIMATION_NAMES[SIT] = "Sit";
        ANIMATION_NAMES[LIE_DOWN] = "Lie down";
        ANIMATION_NAMES[WALK] = "Walk";
        ANIMATION_NAMES[STAND_IDLE] = "Idle";
        ANIMATION_NAMES[ATTACK_1HAND] = "Attack 1 handed";
        ANIMATION_NAMES[ATTACK_2HAND] = "Attack 2 handed";
        ANIMATION_NAMES[ATTACK_BOW] = "Attack with bow";
        ANIMATION_NAMES[ATTACK_CROSSBOW] = "Attack with crossbow";
        ANIMATION_NAMES[ATTACK_BLOCK] = "Block Attack";
        ANIMATION_NAMES[ATTACK_GET_HIT] = "Get hit by attack";
        ANIMATION_NAMES[CAST_SPELL] = "Cast spell";
        ANIMATION_NAMES[DRINK] = "Drink";
        ANIMATION_NAMES[PICK_UP] = "Pick up";
        ANIMATION_NAMES[WORK_SMITH] = "Smithing";
        ANIMATION_NAMES[WORK_MINING] = "Mining";
        ANIMATION_NAMES[WORK_SAW] = "Sawing";
        ANIMATION_NAMES[WORK_CHOPPING] = "Chopping";
        ANIMATION_NAMES[WORK_FISH] = "fishing";
        ANIMATION_NAMES[RUN] = "run";

        for (int pos = i; pos < TOTAL_ANIMATIONS; ++pos) {
            ANIMATION_NAMES[pos] = "undefined " + pos;
        }
    }

    /**
     * Private constructor in order to avoid that anything ever creates a
     * instance of this class.
     */
    private CharAnimations() {
        // nothing to do, this is never called anyway
    }
}

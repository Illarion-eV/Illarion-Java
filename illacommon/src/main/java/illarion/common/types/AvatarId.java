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
package illarion.common.types;

import illarion.common.graphics.CharAnimations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class AvatarId {
    private static final Logger log = LoggerFactory.getLogger(AvatarId.class);
    private int raceId;
    private int typeId;
    @Nonnull
    private Direction direction;
    private int animationId;

    public AvatarId(int raceId, int typeId, @Nonnull Direction direction, int animationId) {
        this.raceId = raceId;
        this.typeId = typeId;
        this.direction = direction;
        this.animationId = animationId;
    }

    /**
     * Get the appearance for a race and a gender.
     * TODO: This function is plain and utter crap. It needs to go away. Far away. Soon.
     *
     * @param raceId the race ID
     * @param typeId the type id
     * @return the appearance ID
     */
    private static int getAppearanceId(int raceId, int typeId) {
        boolean male = typeId == 0;
        switch (raceId) {
            case 0: //human
                return male ? 1 : 16;
            case 1: //dwarf
                return male ? 12 : 17;
            case 2: //halfling
                return male ? 24 : 25;
            case 3: //elf
                return male ? 20 : 19;
            case 4: //orc
                return male ? 13 : 18;
            case 5: //lizardman
                return 7;
            case 7: // column of resurrection
                return 3;
            case 9: //forest troll
                return 21;
            case 10: //mummy
                return 2;
            case 11: //skeleton
                return 5;
            case 12: //floating eye
                return 6;
            case 18: //sheep
                return 9;
            case 19: //spider
                return 10;
            case 24: //pig
                return 23;
            case 27: //wasp
                return 28;
            case 30: //golem
                return 31;
            case 37: //cow
                return 40;
            case 39: //wolf
                return 42;
            case 51: //bear
                return 51;
            case 52: //raptor
                return 52;
            case 53: //zombie
                return 53;
            case 54: //hellhound
                return 54;
            case 55: //imp
                return 55;
            case 56: //iron golem
                return 56;
            case 57: //ratman
                return 57;
            case 58: //dog
                return 58;
            case 59: //beetle
                return 59;
            case 60: //fox
                return 60;
            case 61: //slime
                return 61;
            case 62: //chicken
                return 62;
            case 63: //bone dragon
                return 63;
            case 111: //rat
                return 111;
            case 112: //black dragon
                return 112;
            case 113: //rabbit
                return 113;
            case 114: //Akaltut
                return 114;
            case 115: //fairy
                return 115;
            case 116: //deer
                return 116;
            case 117: //Ettin
                return 117;
            default:
                log.warn("Unexpected race id {}. Using appearance with the same ID by chance.", raceId);
                return raceId;
        }
    }

    public int getRaceId() {
        return raceId;
    }

    public void setRaceId(int raceId) {
        this.raceId = raceId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    @Nonnull
    public Direction getDirection() {
        return direction;
    }

    public void setDirection(@Nonnull Direction direction) {
        this.direction = direction;
    }

    public int getAnimationId() {
        return animationId;
    }

    public void setAnimationId(int animationId) {
        this.animationId = animationId;
    }

    @SuppressWarnings("ConstantConditions")
    public int getAvatarId() {
        if (raceId == -1) {
            return -1;
        }
        return (((getAppearanceId(raceId, typeId) * Direction.values().length) +
                 direction.getServerId()) * CharAnimations.TOTAL_ANIMATIONS) + animationId;
    }
}

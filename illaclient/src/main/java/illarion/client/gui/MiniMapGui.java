/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
package illarion.client.gui;

import illarion.common.types.ServerCoordinate;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.ImmutableColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This interface is used to control the mini map.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface MiniMapGui {
    /**
     * This interface defines a pointer on the map that points towards a specified location.
     */
    interface Pointer {
        /**
         * Get target of this pointer.
         *
         * @param coordinate the location this pointer is supposed to point to
         */
        void setTarget(@Nonnull ServerCoordinate coordinate);

        /**
         * The color that is supposed to be applied to the pointers.
         */
        @Nonnull
        Color POINTER_COLOR = new ImmutableColor(255, 166, 102, 255);

        @Nonnull
        Color ACTIVEPOINTER_COLOR = new ImmutableColor(255, 0, 0, 255);

        /**
         * Set this quest marker as a current quest marker
         *
         * @param currentQuest
         */
        void setCurrentQuest(boolean currentQuest);
    }

    /**
     * Create a new pointer instance.
     *
     * @return the new pointer
     */
    @Nonnull
    Pointer createTargetPointer();

    /**
     * Create a pointer that points to the start of a quest.
     *
     * @return the quest start pointer
     */
    @Nonnull
    Pointer createStartPointer(boolean available);

    /**
     * Cleanup this pointer. Once this is called the instance is not usable anymore
     *
     * @param pointer the pointer to release
     */
    void releasePointer(@Nullable Pointer pointer);

    /**
     * Add the pointer to the mini map.
     *
     * @param pointer the pointer to be added
     */
    void addPointer(@Nonnull Pointer pointer);

    /**
     * Allows to toggle between showing and hiding minimap
     */
    void toggleMiniMap();
}

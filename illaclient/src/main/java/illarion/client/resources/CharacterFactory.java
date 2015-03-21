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
package illarion.client.resources;

import illarion.client.resources.data.AvatarTemplate;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;

/**
 * The avatar factory loads and stores all graphical representations of characters.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public final class CharacterFactory extends AbstractTemplateFactory<AvatarTemplate> {
    /**
     * The ID of the avatar that is loaded by default in case the requested avatar was not found.
     */
    private static final int DEFAULT_AVATAR_ID = 10450;

    /**
     * The singleton instance of this class.
     */
    @Nonnull
    private static final CharacterFactory INSTANCE = new CharacterFactory();

    /**
     * Get the singleton instance of this class.
     *
     * @return the singleton instance of the avatar factory
     */
    @Nonnull
    @Contract(pure = true)
    public static CharacterFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Constructor for the avatar factory. This sets up all storage tables that are needed to store the instances of
     * the avatars created by this function and it starts loading the avatar table.
     */
    private CharacterFactory() {
        super(DEFAULT_AVATAR_ID);
    }
}

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

import illarion.client.resources.Resource;
import illarion.client.resources.data.AvatarClothTemplate;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * A avatar cloth definition stores all data about a cloth that are needed to know. It also allows to render a cloth
 * part.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@NotThreadSafe
public final class AvatarCloth extends AbstractEntity<AvatarClothTemplate> implements Resource {
    /**
     * The avatar that is the parent to this cloth instance.
     */
    @Nonnull
    private final AbstractEntity<?> parent;

    /**
     * Standard constructor.
     *
     * @param template the template this new cloth instance is build from
     * @param parentAvatar the parent avatar this cloth belong to
     */
    public AvatarCloth(@Nonnull AvatarClothTemplate template, @Nonnull AbstractEntity<?> parentAvatar) {
        super(template);
        parent = parentAvatar;
    }

    @Override
    public void hide() {
        // do nothing
    }

    @Override
    public void show() {
        // do nothing
    }

    /**
     * {@inheritDoc}
     * <p>
     * Clothes inherit the shown state of their avatar. If the avatar is visible, so are they.
     */
    @Override
    protected boolean isShown() {
        return parent.isShown();
    }

    @Override
    @Nonnull
    public String toString() {
        return getTemplate() + " of " + parent;
    }
}

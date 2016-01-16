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

import illarion.client.resources.MiscImageFactory;
import illarion.client.resources.data.MiscImageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * This class is used to store the markers that are displayed below a avatar.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AvatarMarker extends AbstractEntity<MiscImageTemplate> {
    /**
     * The logging instance of this class.
     */
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(AvatarMarker.class);

    /**
     * The avatar that is the parent of this class.
     */
    @Nonnull
    private final AbstractEntity<?> parent;

    /**
     * The default constructor of this avatar marker.
     *
     * @param markerId the image ID of this marker
     * @param parentAvatar the parent avatar
     */
    public AvatarMarker(int markerId, @Nonnull AbstractEntity<?> parentAvatar) {
        super(MiscImageFactory.getInstance().getTemplate(markerId));
        parent = parentAvatar;
    }

    @Override
    public void setAlpha(int newAlpha) {
        super.setAlpha(newAlpha);
        setAlphaTarget(newAlpha);
    }

    @Override
    public void hide() {
        // nothing to do
    }

    @Override
    public void show() {
        log.warn("Show was called for a avatar marker. This shouldn't happen.");
    }

    @Override
    protected boolean isShown() {
        return parent.isShown();
    }
}

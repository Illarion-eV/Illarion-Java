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
package illarion.client.resources.data;

import illarion.client.graphics.AbstractEntity;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.Sprite;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * This template in general stores the data required to create any class that inherits {@link AbstractEntity}.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
@ThreadSafe
public class AbstractEntityTemplate implements ResourceTemplate {
    /**
     * The sprite that is used to render the entity using this template.
     */
    @Nonnull
    private final Sprite sprite;

    /**
     * The default color of the entity. Can be {@code null} to use the default color.
     */
    @Nullable
    private final Color defaultColor;

    /**
     * The identification number of the entity.
     */
    private final int id;

    /**
     * The shadow offset value of the identification number.
     */
    private final int shadowOffset;

    /**
     * The constructor of this class.
     *
     * @param id the identification number of the entity
     * @param sprite the sprite used to render the entity
     * @param defaultColor the default color of the entity
     * @param shadowOffset the offset of the shadow
     */
    protected AbstractEntityTemplate(
            int id, @Nonnull Sprite sprite, @Nullable Color defaultColor, int shadowOffset) {
        this.sprite = sprite;
        this.defaultColor = defaultColor;
        this.id = id;
        this.shadowOffset = shadowOffset;
    }

    @Nonnull
    public Sprite getSprite() {
        return sprite;
    }

    @Nullable
    public Color getDefaultColor() {
        if (defaultColor == null) {
            return null;
        }
        return new Color(defaultColor);
    }

    public int getId() {
        return id;
    }

    @Override
    public int getTemplateId() {
        return id;
    }

    public int getShadowOffset() {
        return shadowOffset;
    }
}

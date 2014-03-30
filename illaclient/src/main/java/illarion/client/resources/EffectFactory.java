/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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

import illarion.client.resources.data.EffectTemplate;

import javax.annotation.Nonnull;

/**
 * The effect factory that stores the effect templates.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public final class EffectFactory extends AbstractTemplateFactory<EffectTemplate> {
    /**
     * The ID of the effect that is shown in case the requested effect is not defined.
     */
    private static final int DEFAULT_EFFECT_ID = 12;

    /**
     * The singleton instance of the effect factory.
     */
    private static final EffectFactory INSTANCE = new EffectFactory();

    /**
     * The the singleton instance of this effect factory.
     *
     * @return the singleton instance for the effect factory
     */
    @Nonnull
    public static EffectFactory getInstance() {
        return INSTANCE;
    }

    /**
     * The constructor of the effect factory that ensures that only the
     * singleton instance is created.
     */
    private EffectFactory() {
        super(DEFAULT_EFFECT_ID);
    }
}

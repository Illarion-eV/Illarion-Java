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
package illarion.client.resources;

import illarion.client.graphics.Effect;
import illarion.common.util.RecycleFactory;

import javax.annotation.Nonnull;

/**
 * The effect factory creates and stores the effect objects and keeps them for
 * reuse.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public final class EffectFactory extends RecycleFactory<Effect> implements
        ResourceFactory<Effect> {
    /**
     * The ID of the effect that is shown in case the requested effect is not
     * defined.
     */
    private static final int DEFAULT_EFFECT = 12;

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
        super();
    }

    /**
     * The initialization function prepares this factory to load data.
     */
    @Override
    @SuppressWarnings("nls")
    public void init() {
    }

    /**
     * Optimize the factory once the loading finished.
     */
    @Override
    public void loadingFinished() {
        mapDefault(DEFAULT_EFFECT, 1);
        finish();
    }

    /**
     * Store a loaded resource in this factory.
     */
    @Override
    public void storeResource(final Effect resource) {
        register(resource);
    }
}

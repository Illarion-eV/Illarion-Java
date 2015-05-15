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
package illarion.client.util.translation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class TranslateSentenceTask implements Callable<String> {
    @Nonnull
    private final TranslationProvider provider;
    @Nonnull
    private final TranslationDirection direction;
    @Nonnull
    private final String original;

    public TranslateSentenceTask(@Nonnull TranslationProvider provider, @Nonnull TranslationDirection direction,
                                 @Nonnull String original) {
        this.provider = provider;
        this.direction = direction;
        this.original = original;
    }

    @Override
    @Nullable
    public String call() throws Exception {
        return provider.getTranslation(original, direction);
    }
}

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

/**
 * This interface is the common definition for the translation providers. It allows the common class to query the
 * specific translation provider for translations.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface TranslationProvider {
    /**
     * Get a translated version of a text.
     *
     * @param original  The original text that is supposed to be translated
     * @param direction The translation direction
     * @return The translated text
     */
    @Nullable
    String getTranslation(@Nonnull String original, @Nonnull TranslationDirection direction);

    /**
     * Check if the provider is working correctly.
     *
     * @return {@code true} in case the provider is properly working
     */
    boolean isProviderWorking();
}

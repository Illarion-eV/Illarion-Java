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
package illarion.client.util.translation.yandex;

import illarion.client.util.translation.TranslationDirection;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class YandexProviderTest {
    private YandexProvider provider;

    @BeforeClass
    public void createProvider() {
        provider = new YandexProvider();
    }

    @Test(enabled = false)
    public void testGermanToEnglish() {
        if (provider == null) {
            throw new SkipException("Provider was not correctly prepared.");
        }
        String translation = provider.getTranslation("Hallo!", TranslationDirection.GermanToEnglish);

        assertEquals(translation, "Hello!", "Translation service is not yielding the expected result.");
    }

    @Test(enabled = false)
    public void testEnglishToGerman() {
        if (provider == null) {
            throw new SkipException("Provider was not correctly prepared.");
        }
        String translation = provider.getTranslation("Hello!", TranslationDirection.EnglishToGerman);

        assertEquals(translation, "Hallo!", "Translation service is not yielding the expected result.");
    }
}

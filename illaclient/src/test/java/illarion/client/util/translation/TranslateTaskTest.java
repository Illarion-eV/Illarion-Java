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

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/** Exercise real sentence tasks with a deterministic provider, without bytecode instrumentation. */

public class TranslateTaskTest {
    @Test
    public void testSplit() throws Exception {
        verify("Hello. This is a multi sentence line! It should be split. Even if the last part is not terminated",

                new String[]{
                        "Hello.", "This is a multi sentence line!", "It should be split.",
                        "Even if the last part is not terminated"
                },

                new String[]{
                        "Hallo.", "Das ist eine Zeile mit mehreren Sätzen.", "Sie sollte geteilt werden.",
                        "Auch wenn der letzte Teil nicht abgeschlossen ist"
                },
                "Hallo. Das ist eine Zeile mit mehreren Sätzen. Sie sollte geteilt werden. " +
                        "Auch wenn der letzte Teil nicht abgeschlossen ist");
    }

    @Test
    public void testHeaderExclusion1() throws Exception {
        verifyHeader("You hear: ", false);
    }

    @Test
    public void testHeaderExclusion2() throws Exception {
        verifyHeader("Somebody says: ", false);
    }

    @Test
    public void testHeaderExclusionOoc() throws Exception {
        verifyHeader("Somebody says: ", true);
    }

    private void verifyHeader(String header, boolean ooc) throws Exception {
        String original = "ALL YOUR BASE ARE BELONG TO US.";
        String translated = "ALL DEINE STÜTZPUNKT SIND GEHÖREN UNS.";
        verify(header + (ooc ? "((" : "") + original + (ooc ? "))" : ""),
                new String[]{original}, new String[]{translated},
                header + (ooc ? "((" : "") + translated + (ooc ? "))" : ""));
    }

    private void verify(String input, String[] sentences, String[] translations, String expected) throws Exception {
        List<String> received = new ArrayList<>();
        List<String> callbacks = new ArrayList<>();

        TranslationProvider provider = new TranslationProvider() {
            @Override
            public String getTranslation(String original, TranslationDirection direction) {
                assertEquals(direction, TranslationDirection.EnglishToGerman);
                int index = received.size();
                received.add(original);
                assertTrue(index < sentences.length, "Unexpected extra sentence");
                assertEquals(original, sentences[index]);
                return translations[index];
            }

            @Override
            public boolean isProviderWorking() {
                return true;
            }
        };

        ExecutorService service = Executors.newSingleThreadExecutor();

        try {
            TranslateTask task = new TranslateTask(service, provider, TranslationDirection.EnglishToGerman,
                    input, callbacks::add);
            assertTrue(received.isEmpty(), "Construction must not perform translations");
            assertTrue(callbacks.isEmpty(), "Construction must not invoke callbacks");
            assertEquals(task.call(), expected);
            assertEquals(received, Arrays.asList(sentences));
            assertEquals(callbacks, Collections.singletonList(expected));
        } finally {
            service.shutdownNow();
            assertTrue(service.awaitTermination(5, TimeUnit.SECONDS));
        }
    }
}

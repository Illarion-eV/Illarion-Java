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
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class TranslateTask implements Callable<String> {
    @Nonnull
    private final ExecutorService executorService;
    @Nonnull
    private final TranslationProvider provider;
    @Nonnull
    private final TranslationDirection direction;
    @Nonnull
    private final String original;
    @Nonnull
    private final TranslatorCallback callback;

    TranslateTask(@Nonnull ExecutorService executorService, @Nonnull TranslationProvider provider,
                  @Nonnull TranslationDirection direction, @Nonnull String original,
                  @Nonnull TranslatorCallback callback) {
        this.executorService = executorService;
        this.provider = provider;
        this.direction = direction;
        this.original = original;
        this.callback = callback;
    }

    @Override
    @Nullable
    public String call() throws Exception {
        BreakIterator iterator = BreakIterator.getSentenceInstance();
        iterator.setText(original);

        Collection<Future<String>> translationTasks = new ArrayList<>();
        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            String line = original.substring(start, end).trim();
            translationTasks.add(executorService.submit(new TranslateSentenceTask(provider, direction, line)));
        }

        StringBuilder resultBuilder = new StringBuilder();
        for (Future<String> task : translationTasks) {
            String translated = task.get();
            if (translated == null) {
                callback.sendTranslation(null);
                return null;
            }
            resultBuilder.append(task.get());
            resultBuilder.append(' ');
        }

        if (resultBuilder.length() == 0) {
            callback.sendTranslation(null);
            return null;
        }
        resultBuilder.setLength(resultBuilder.length() - 1);
        String result = resultBuilder.toString();
        callback.sendTranslation(result);
        return result;
    }
}

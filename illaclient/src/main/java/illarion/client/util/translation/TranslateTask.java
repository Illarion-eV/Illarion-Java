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

import illarion.client.util.Lang;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String header = findHeader(original);
        String usedText = (header == null) ? original : original.substring(header.length());
        boolean foundOocMarkers = false;
        if (usedText.startsWith("((") && usedText.endsWith("))")) {
            foundOocMarkers = true;
            usedText = usedText.substring(2, usedText.length() - 2);
        }

        BreakIterator iterator = BreakIterator.getSentenceInstance(Lang.getInstance().getLocale());
        iterator.setText(usedText);

        Collection<Future<String>> translationTasks = new ArrayList<>();
        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            String line = usedText.substring(start, end).trim();
            translationTasks.add(executorService.submit(new TranslateSentenceTask(provider, direction, line)));
        }

        StringBuilder resultBuilder = new StringBuilder();
        if (header != null) {
            resultBuilder.append(header);
        }
        if (foundOocMarkers) {
            resultBuilder.append("((");
        }

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
        if (foundOocMarkers) {
            resultBuilder.append("))");
        }
        String result = resultBuilder.toString();
        callback.sendTranslation(result);
        return result;
    }

    @Nonnull
    private static final Pattern PATTERN_SAY = Pattern.compile("^(.+?)\\s" + Lang.getMsg("log.say") + ":\\s");
    @Nonnull
    private static final Pattern PATTERN_SHOUT = Pattern.compile("^(.+?)\\s" + Lang.getMsg("log.shout") + ":\\s");
    @Nonnull
    private static final Pattern PATTERN_WHISPER = Pattern.compile("^(.+?)\\s" + Lang.getMsg("log.whisper") + ":\\s");

    @Nullable
    private String findHeader(@Nonnull String input) {
        if (input.startsWith(Lang.getMsg("chat.distantShout") + ": ")) {
            return Lang.getMsg("chat.distantShout") + ": ";
        }
        if (input.startsWith(Lang.getMsg("chat.broadcast") + ": ")) {
            return Lang.getMsg("chat.broadcast") + ": ";
        }
        if (input.startsWith(Lang.getMsg("chat.textto") + ": ")) {
            return Lang.getMsg("chat.textto") + ": ";
        }
        if (input.startsWith(Lang.getMsg("chat.scriptInform") + ": ")) {
            return Lang.getMsg("chat.scriptInform") + ": ";
        }
        String sayHeader = findPattern(input, PATTERN_SAY);
        if (sayHeader != null) {
            return sayHeader;
        }
        String shoutHeader = findPattern(input, PATTERN_SHOUT);
        if (shoutHeader != null) {
            return shoutHeader;
        }
        String whisperHeader = findPattern(input, PATTERN_WHISPER);
        if (whisperHeader != null) {
            return whisperHeader;
        }
        return null;
    }

    @Nullable
    private String findPattern(@Nonnull String input, @Nonnull Pattern pattern) {
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}

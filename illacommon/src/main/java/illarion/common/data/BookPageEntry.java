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
package illarion.common.data;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

/**
 * This class represents one entry of a book page.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class BookPageEntry {

    public enum Align {
        Left,
        Center,
        Right
    }

    private static final Pattern REMOVE_LINE_BREAKS_PATTERN = Pattern.compile("\\s+");
    private static final Pattern COLLAPSE_SPACE_PATTERN = Pattern.compile("[ \t]+");
    private static final Pattern LINE_BREAK_PATTERN = Pattern.compile("\n\r?");
    /**
     * In case this flag this {@code true} this entry is a headline and no paragraph.
     */
    private final boolean headline;

    /**
     * The text that belongs to this entry.
     */
    @Nonnull
    private final String text;

    @Nonnull
    private final Align alignment;

    /**
     * Create a new entry for a book page.
     *
     * @param headline {@code true} in case this entry is a headline
     * @param text the text of this entry
     * @param keepLineBreaks keep the line breaks as is
     * @param alignment the alignment of the paragraph
     */
    public BookPageEntry(boolean headline, @Nonnull String text, boolean keepLineBreaks, @Nonnull Align alignment) {
        this.headline = headline;
        this.alignment = alignment;
        if (keepLineBreaks) {
            String cleanedText = COLLAPSE_SPACE_PATTERN.matcher(text.trim()).replaceAll(" ");
            String[] lines = LINE_BREAK_PATTERN.split(cleanedText);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < lines.length; i++) {
                if (i > 0) {
                    sb.append('\n');
                }
                sb.append(lines[i].trim());
            }
            this.text = sb.toString();
        } else {
            this.text = REMOVE_LINE_BREAKS_PATTERN.matcher(text.trim()).replaceAll(" ");
        }
    }

    /**
     * Check if this entry is a headline.
     *
     * @return {@code true} in case this line is a headline
     */
    public boolean isHeadline() {
        return headline;
    }

    /**
     * Get the text this entry contains.
     *
     * @return the text of this entry
     */
    @Nonnull
    public String getText() {
        return text;
    }

    @Nonnull
    public Align getAlignment() {
        return alignment;
    }
}

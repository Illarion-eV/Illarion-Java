/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Common Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Common Library.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.data;

/**
 * This class represents one entry of a book page.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class BookPageEntry {
    /**
     * In case this flag this {@code true} this entry is a headline and no paragraph.
     */
    private final boolean headline;

    /**
     * The text that belongs to this entry.
     */
    private final String text;

    /**
     * Create a new entry for a book page.
     *
     * @param headline {@code true} in case this entry is a headline
     * @param text     the text of this entry
     */
    public BookPageEntry(final boolean headline, final String text) {
        this.headline = headline;
        this.text = text.trim().replaceAll("\\s+", " ");
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
    public String getText() {
        return text;
    }
}

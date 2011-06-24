/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Common Library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Common Library. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.config.gui.entries.awt;

import java.awt.TextField;

import illarion.common.config.entries.ConfigEntry;
import illarion.common.config.entries.TextEntry;
import illarion.common.config.gui.entries.SaveableEntry;

/**
 * This is a special implementation for the text area that is initialized with a
 * configuration entry. Its sole purpose is the use along with the configuration
 * system.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class TextEntryAwt extends TextField implements SaveableEntry {
    /**
     * The serialization UID of this text field.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The text entry used to initialize this instance.
     */
    private final TextEntry entry;

    /**
     * Create a instance of this text entry and set the configuration entry that
     * is used to setup this class.
     * 
     * @param usedEntry the entry used to setup this class, the entry needs to
     *            pass the check with the static method
     */
    @SuppressWarnings("nls")
    public TextEntryAwt(final ConfigEntry usedEntry) {
        if (!isUsableEntry(usedEntry)) {
            throw new IllegalArgumentException("ConfigEntry type illegal.");
        }
        entry = (TextEntry) usedEntry;
        setColumns(25);
        setText(entry.getValue());
    }

    /**
     * Text a entry if it is usable with this class or not.
     * 
     * @param entry the entry to test
     * @return <code>true</code> in case this entry is usable with this class
     */
    public static boolean isUsableEntry(final ConfigEntry entry) {
        return (entry instanceof TextEntry);
    }

    /**
     * Save the value in this text entry to the configuration.
     */
    @Override
    public void save() {
        entry.setValue(getText());
    }

}

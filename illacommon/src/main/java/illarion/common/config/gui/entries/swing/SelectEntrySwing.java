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
package illarion.common.config.gui.entries.swing;

import illarion.common.config.entries.ConfigEntry;
import illarion.common.config.entries.SelectEntry;
import illarion.common.config.gui.entries.SavableEntry;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;

/**
 * This is a special implementation for the combo box that is initialized with a
 * configuration entry. Its sole purpose is the use along with the configuration
 * system.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class SelectEntrySwing extends JComboBox<String> implements SavableEntry {
    /**
     * The serialization UID of this text field.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The text entry used to initialize this instance.
     */
    @Nonnull
    private final SelectEntry entry;

    /**
     * Create a instance of this check entry and set the configuration entry
     * that is used to setup this class.
     *
     * @param usedEntry the entry used to setup this class, the entry needs to
     * pass the check with the static method
     */
    public SelectEntrySwing(@Nonnull ConfigEntry usedEntry) {
        if (!isUsableEntry(usedEntry)) {
            throw new IllegalArgumentException("ConfigEntry type illegal.");
        }
        entry = (SelectEntry) usedEntry;

        for (String item : entry.getLabels()) {
            addItem(item);
        }
        setSelectedIndex(entry.getIndex());
        setMinimumSize(new Dimension(300, 10));
    }

    /**
     * Text a entry if it is usable with this class or not.
     *
     * @param entry the entry to test
     * @return {@code true} in case this entry is usable with this class
     */
    @Contract(pure = true)
    public static boolean isUsableEntry(@Nonnull ConfigEntry entry) {
        return entry instanceof SelectEntry;
    }

    /**
     * Save the value in this text entry to the configuration.
     */
    @Override
    public void save() {
        entry.setValue(getSelectedIndex());
    }
}

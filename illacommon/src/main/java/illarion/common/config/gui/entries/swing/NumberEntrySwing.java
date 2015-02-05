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
import illarion.common.config.entries.NumberEntry;
import illarion.common.config.gui.entries.SavableEntry;
import illarion.common.util.FastMath;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

/**
 * This is a special implementation for the panel that is initialized with a
 * configuration entry. Its sole purpose is the use along with the configuration
 * system. In this case the panel is filled with all components needed to set a
 * number in the configuration properly.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class NumberEntrySwing extends JPanel implements SavableEntry {
    /**
     * This listener is used to monitor any actions done to the scroll bar and
     * update the display of the scroll bar according to this.
     *
     * @author Martin Karing &lt;nitram@illarion.org&gt;
     */
    private static final class NumberEntryScrollListener implements AdjustmentListener {
        /**
         * The instance that is updated by this listener.
         */
        private final NumberEntrySwing parentInstance;

        /**
         * Public constructor to allow the parent class to create instances.
         * Also this is needed to set the instance of the parent class that is
         * updated by this listener.
         *
         * @param parent the class that is updated by this listener
         */
        public NumberEntryScrollListener(NumberEntrySwing parent) {
            parentInstance = parent;
        }

        /**
         * This method is called in case the scroll bar changes its set value
         * due user interaction.
         */
        @Override
        public void adjustmentValueChanged(@Nonnull AdjustmentEvent e) {
            parentInstance.setCurrentValue(e.getValue());
        }
    }

    /**
     * The serialization UID of this text field.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The current value of this number entry.
     */
    private int currentValue;

    /**
     * The label the current value of the scroll bar is displayed in.
     */
    @Nonnull
    private final JLabel display;

    /**
     * The text entry used to initialize this instance.
     */
    @Nonnull
    private final NumberEntry entry;

    /**
     * Create a instance of this check entry and set the configuration entry
     * that is used to setup this class.
     *
     * @param usedEntry the entry used to setup this class, the entry needs to
     * pass the check with the static method
     */
    public NumberEntrySwing(ConfigEntry usedEntry) {
        super(new BorderLayout(10, 0));

        if (!isUsableEntry(usedEntry)) {
            throw new IllegalArgumentException("ConfigEntry type illegal.");
        }
        entry = (NumberEntry) usedEntry;

        currentValue = entry.getValue();

        display = new JLabel(Integer.toString(currentValue));
        Dimension d = display.getPreferredSize();
        display.setPreferredSize(new Dimension(d.width + 15, d.height));
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        add(display, BorderLayout.EAST);

        JScrollBar scroll = new JScrollBar();
        scroll.setOrientation(Adjustable.HORIZONTAL);
        scroll.setValues(currentValue, 1, entry.getRange().getMin(), entry.getRange().getMax() + 1);
        scroll.addAdjustmentListener(new NumberEntryScrollListener(this));
        add(scroll, BorderLayout.CENTER);

        setMinimumSize(new Dimension(300, 10));
    }

    /**
     * Text a entry if it is usable with this class or not.
     *
     * @param entry the entry to test
     * @return {@code true} in case this entry is usable with this class
     */
    public static boolean isUsableEntry(ConfigEntry entry) {
        return entry instanceof NumberEntry;
    }

    /**
     * Save the value in this text entry to the configuration.
     */
    @Override
    public void save() {
        entry.setValue(FastMath.clamp(currentValue, entry.getRange()));
    }

    /**
     * Set the current value of this entry. This is a internal function that is
     * required to update the state of this instance.
     *
     * @param val the value the current value is supposed to be set to
     */
    void setCurrentValue(int val) {
        currentValue = FastMath.clamp(val, entry.getRange());
        display.setText(Integer.toString(currentValue));
    }
}

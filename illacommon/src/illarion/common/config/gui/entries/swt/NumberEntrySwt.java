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
package illarion.common.config.gui.entries.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Slider;

import illarion.common.config.entries.ConfigEntry;
import illarion.common.config.entries.NumberEntry;
import illarion.common.util.FastMath;

/**
 * This is a special implementation for the panel that is initialized with a
 * configuration entry. Its sole purpose is the use along with the configuration
 * system. In this case the panel is filled with all components needed to set a
 * number in the configuration properly.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class NumberEntrySwt implements SaveableEntrySwt {
    /**
     * This listener is used to monitor any actions done to the scroll bar and
     * update the display of the scroll bar according to this.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    private static final class NumberEntryScrollListener implements Listener {
        /**
         * The instance that is updated by this listener.
         */
        private final NumberEntrySwt parentInstance;

        /**
         * The slider controlled by this listener.
         */
        private final Slider scrollBar;

        /**
         * Public constructor to allow the parent class to create instances.
         * Also this is needed to set the instance of the parent class that is
         * updated by this listener.
         * 
         * @param parent the class that is updated by this listener
         * @param slider the slider that is controlled by this listener
         */
        public NumberEntryScrollListener(final NumberEntrySwt parent,
            final Slider slider) {
            parentInstance = parent;
            scrollBar = slider;
        }

        /**
         * This method is called in case the scroll bar changes its set value
         * due user interaction.
         */
        @Override
        public void handleEvent(final Event event) {
            parentInstance.setCurrentValue(scrollBar.getSelection());
        }

    }

    /**
     * The current value of this number entry.
     */
    private int currentValue;

    /**
     * The label the current value of the scroll bar is displayed in.
     */
    private final Label display;

    /**
     * The text entry used to initialize this instance.
     */
    private final NumberEntry entry;

    /**
     * The panel the SWT components are added to.
     */
    private final Composite panel;

    /**
     * Create a instance of this check entry and set the configuration entry
     * that is used to setup this class.
     * 
     * @param usedEntry the entry used to setup this class, the entry needs to
     *            pass the check with the static method
     * @param parentWidget the widget this widget is added to
     */
    @SuppressWarnings("nls")
    public NumberEntrySwt(final ConfigEntry usedEntry,
        final Composite parentWidget) {
        if (!isUsableEntry(usedEntry)) {
            throw new IllegalArgumentException("ConfigEntry type illegal.");
        }

        panel = new Composite(parentWidget, SWT.NONE);
        entry = (NumberEntry) usedEntry;

        currentValue = entry.getValue();

        final GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        panel.setLayout(layout);

        final Slider scroll = new Slider(panel, SWT.HORIZONTAL);
        scroll
            .setValues(FastMath.clamp(currentValue, entry.getRange())
                - entry.getRange().getMin(), 0, entry.getRange()
                .getDifference() + 1, 1,
                entry.getRange().getDifference() / 100, entry.getRange()
                    .getDifference() / 100);
        scroll.addListener(SWT.Selection, new NumberEntryScrollListener(this,
            scroll));
        scroll.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
            false, 1, 1));

        display = new Label(panel, SWT.NONE);
        display.setText(Integer.toString(currentValue));
        final GridData displayData =
            new GridData(SWT.FILL, SWT.BEGINNING, false, false, 1, 1);
        displayData.horizontalIndent = 10;
        display.setLayoutData(displayData);
    }

    /**
     * Text a entry if it is usable with this class or not.
     * 
     * @param entry the entry to test
     * @return <code>true</code> in case this entry is usable with this class
     */
    public static boolean isUsableEntry(final ConfigEntry entry) {
        return (entry instanceof NumberEntry);
    }

    /**
     * Save the value in this text entry to the configuration.
     */
    @Override
    public void save() {
        entry.setValue(FastMath.clamp(currentValue, entry.getRange()));
    }

    /**
     * Set the layout data of this entry.
     */
    @Override
    public void setLayoutData(final Object data) {
        panel.setLayoutData(data);
    }

    /**
     * Set the current value of this entry. This is a internal function that is
     * required to update the state of this instance.
     * 
     * @param val the value the current value is supposed to be set to
     */
    void setCurrentValue(final int val) {
        currentValue =
            FastMath.clamp(val + entry.getRange().getMin(), entry.getRange());
        display.setText(Integer.toString(currentValue));
    }

}

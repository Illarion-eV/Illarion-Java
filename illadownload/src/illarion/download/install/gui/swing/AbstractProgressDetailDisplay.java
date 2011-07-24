/*
 * This file is part of the Illarion Download Manager.
 * 
 * Copyright © 2011 - Illarion e.V.
 * 
 * The Illarion Download Manager is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Download Manager is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Download Manager. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.download.install.gui.swing;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.BevelBorder;

/**
 * That is the abstract definition of the detailed display classes for the
 * progress visualization.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.00
 */
abstract class AbstractProgressDetailDisplay extends JPanel {
    /**
     * The dimension of the progress bars.
     */
    private static final Dimension PROGRESS_BAR_DIM = new Dimension(100, 30);

    /**
     * The serialization UID of this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The progress bar that displays the progress.
     */
    private final JProgressBar progress;

    /**
     * Create a new display and set it up to display a progress.
     * 
     * @param name the name of this progress item
     */
    public AbstractProgressDetailDisplay(final String name) {
        super(new FlowLayout(FlowLayout.LEFT, 10, 5));
        setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        progress = new JProgressBar();
        progress.setStringPainted(true);
        progress.setMinimumSize(PROGRESS_BAR_DIM);
        progress.setMaximumSize(PROGRESS_BAR_DIM);
        progress.setPreferredSize(PROGRESS_BAR_DIM);
        progress.setSize(PROGRESS_BAR_DIM);
        add(progress);
        progress.setFont(progress.getFont().deriveFont(11.f));

        add(new JLabel(name));

        setVisible(false);
    }

    /**
     * Set the lower and the upper limits of the progress bar.
     * 
     * @param min the lower limit of the progress bar
     * @param max the upper limit of the progress bar
     */
    public void setProgressLimits(final int min, final int max) {
        progress.setMinimum(min);
        progress.setMaximum(max);
        progress.repaint();
    }

    /**
     * Set the text that is displayed on the progress bar.
     * 
     * @param message the message that is displayed
     */
    public void setProgressMessage(final String message) {
        progress.setString(message);
        progress.repaint();
    }

    /**
     * Change the value of the progress bar.
     * 
     * @param value the value of the progress bar
     */
    public void setProgressValue(final int value) {
        progress.setValue(value);
        progress.repaint();
    }
}

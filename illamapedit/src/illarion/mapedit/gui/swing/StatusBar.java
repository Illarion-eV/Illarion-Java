/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute i and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Mapeditor is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Mapeditor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.gui.swing;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javolution.text.TextBuilder;

import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.JXStatusBar.Constraint.ResizeBehavior;
import org.jdesktop.swingx.plaf.basic.BasicStatusBarUI;

/**
 * This status bar is used for some simple controls over the editor and the
 * display of some status values.
 * 
 * @author Martin Karing
 * @since 1.02
 * @version 1.02
 */
public final class StatusBar extends JXStatusBar {
    /**
     * This listener is applied to the Zoom buttons. Its used to alter the value
     * of the zoom by a offset.
     * 
     * @author Martin Karing
     * @since 1.02
     * @version 1.02
     */
    private static final class ZoomButtonListener implements ActionListener {
        /**
         * The offset the zoom is changed by.
         */
        private final int changeBy;

        /**
         * This public constructor is needed to allow the parent class to create
         * a instance. Also its used to define the delta value the zoom is
         * changed by.
         * 
         * @param zoomChangeByValue the delta value the zoom is changed by
         */
        public ZoomButtonListener(final int zoomChangeByValue) {
            changeBy = zoomChangeByValue;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            if (e.getID() == ActionEvent.ACTION_PERFORMED) {
                ZoomManager.getInstance().changeZoom(changeBy);
            }
        }
    }

    /**
     * This is the button that displays the current zoom level and allows to set
     * the zoom to predefined values in a swift way.
     * 
     * @author Martin Karing
     * @since 1.02
     * @version 1.02
     */
    private static final class ZoomDisplayButton extends JButton implements
        ZoomManager.Listener {
        /**
         * This listener class is used for the entries of the popup menu that is
         * displayed in case the detail selection of the zoom button is
         * activated. It allows choosing a fixed zoom level.
         * 
         * @author Martin Karing
         * @since 1.02
         * @version 1.02
         */
        private static final class ZoomMenuItemUpdate implements
            ActionListener {
            /**
             * The target value that is passed to the zoom manger in case this
             * listener is triggered.
             */
            private final int targetValue;

            /**
             * This public constructor is needed to allow the parent class to
             * create a instance of this class. Also its used to define the
             * value that is passed to the ZoomManager once this listener is
             * activated.
             * 
             * @param newTargetValue the value that is reported to the listener
             * @throws IllegalArgumentException in case the parameter value is
             *             outside of the valid range
             */
            public ZoomMenuItemUpdate(final int newTargetValue) {
                if ((newTargetValue > ZoomManager.MAX_ZOOM)
                    || (newTargetValue < ZoomManager.MIN_ZOOM)) {
                    throw new IllegalArgumentException();
                }
                targetValue = newTargetValue;
            }

            @Override
            public void actionPerformed(final ActionEvent e) {
                ZoomManager.getInstance().requestNewZoom(targetValue);
            }

        }

        /**
         * The serialization UID of this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * The menu that is displayed in case the button is clicked.
         */
        private final JPopupMenu popup;

        /**
         * This public constructor is needed to allow the parent class to create
         * a proper instance and to setup the default values of this button.
         */
        public ZoomDisplayButton() {
            zoomChanged(0, 100);

            popup = new JPopupMenu();

            JMenuItem currItem = new JMenuItem("100%"); //$NON-NLS-1$
            currItem.addActionListener(new ZoomMenuItemUpdate(100));
            popup.add(currItem);

            currItem = new JMenuItem("75%"); //$NON-NLS-1$
            currItem.addActionListener(new ZoomMenuItemUpdate(75));
            popup.add(currItem);

            currItem = new JMenuItem("50%"); //$NON-NLS-1$
            currItem.addActionListener(new ZoomMenuItemUpdate(50));
            popup.add(currItem);

            currItem = new JMenuItem("25%"); //$NON-NLS-1$
            currItem.addActionListener(new ZoomMenuItemUpdate(25));
            popup.add(currItem);

            currItem = new JMenuItem("10%"); //$NON-NLS-1$
            currItem.addActionListener(new ZoomMenuItemUpdate(10));
            popup.add(currItem);

            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    ZoomDisplayButton.this.showPopupMenu();
                }
            });
        }

        @Override
        public void zoomChanged(final int oldState, final int newState) {
            final TextBuilder builder = TextBuilder.newInstance();
            builder.append(newState);
            builder.append('%');
            setText(builder.toString());
            TextBuilder.recycle(builder);
        }

        /**
         * This function shows the JPopupMenu that is handled by this button.
         */
        protected void showPopupMenu() {
            popup.show(this, 0, 0);
        }
    }

    /**
     * This special implementation of the slider takes update requests from the
     * zoom manger in order to display the actual current zoom.
     * 
     * @author Martin Karing
     * @since 1.02
     * @version 1.02
     */
    private static final class ZoomSlider extends JSlider implements
        ZoomManager.Listener {
        /**
         * The serialization UID of this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Public constructor that allows the parent class to create a instance
         * of this class and that prepares all the required values for this
         * slider. Also it will register the required listener that reports
         * changes to the zoom manager.
         */
        public ZoomSlider() {
            super(HORIZONTAL, ZoomManager.MIN_ZOOM, ZoomManager.MAX_ZOOM,
                ZoomManager.MAX_ZOOM);

            addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(final ChangeEvent e) {
                    if (e.getSource() instanceof JSlider) {
                        ZoomManager.getInstance().requestNewZoom(
                            ((JSlider) e.getSource()).getValue());
                    }
                }
            });
        }

        @Override
        public void zoomChanged(final int oldState, final int newState) {
            if (!getValueIsAdjusting()) {
                setValue(newState);
            }
        }
    }

    /**
     * The serialization UID of this status bar.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The height of the status bar in pixels.
     */
    private static final int STATUS_BAR_HEIGHT = 21;

    /**
     * Create a new instance of the status bar and fill it with data.
     */
    @SuppressWarnings("nls")
    public StatusBar() {
        super();
        putClientProperty(BasicStatusBarUI.AUTO_ADD_SEPARATOR, Boolean.FALSE);

        setResizeHandleEnabled(true);

        // final JProgressBar progress = new JProgressBar();

        final Dimension buttonSize =
            new Dimension(STATUS_BAR_HEIGHT, STATUS_BAR_HEIGHT);

        final JButton decreaseBtn =
            new JButton(Utils.getIconFromResource("decrease.png"));
        decreaseBtn.setVerticalTextPosition(SwingConstants.CENTER);
        decreaseBtn.setHorizontalTextPosition(SwingConstants.LEFT);
        decreaseBtn.setPreferredSize(buttonSize);
        decreaseBtn.setMinimumSize(buttonSize);
        decreaseBtn.setMaximumSize(buttonSize);
        decreaseBtn.setSize(buttonSize);
        decreaseBtn.setIconTextGap(0);
        decreaseBtn.addActionListener(new ZoomButtonListener(-10));

        final JButton increaseBtn =
            new JButton(Utils.getIconFromResource("increase.png"));
        increaseBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        increaseBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        increaseBtn.setPreferredSize(buttonSize);
        increaseBtn.setMinimumSize(buttonSize);
        increaseBtn.setMaximumSize(buttonSize);
        increaseBtn.setSize(buttonSize);
        increaseBtn.setIconTextGap(0);
        increaseBtn.addActionListener(new ZoomButtonListener(10));

        final Dimension zoom100BtnSize = new Dimension(50, STATUS_BAR_HEIGHT);
        final ZoomDisplayButton zoom100Btn = new ZoomDisplayButton();
        ZoomManager.getInstance().addListener(zoom100Btn);
        zoom100Btn.setPreferredSize(zoom100BtnSize);
        zoom100Btn.setMinimumSize(zoom100BtnSize);
        zoom100Btn.setMaximumSize(zoom100BtnSize);
        zoom100Btn.setSize(zoom100BtnSize);
        zoom100Btn.setIconTextGap(0);

        final Dimension sliderSize = new Dimension(150, STATUS_BAR_HEIGHT);
        final ZoomSlider zoomSlider = new ZoomSlider();
        ZoomManager.getInstance().addListener(zoomSlider);
        zoomSlider.setPreferredSize(sliderSize);
        zoomSlider.setMinimumSize(sliderSize);
        zoomSlider.setMaximumSize(sliderSize);
        zoomSlider.setSize(sliderSize);
        zoomSlider.setMajorTickSpacing(25);
        zoomSlider.setPaintTicks(true);

        add(new JLabel(), new Constraint(ResizeBehavior.FILL));
        // add(progress, new Constraint(150));
        add(new JSeparator(SwingConstants.VERTICAL), new Constraint(15));
        add(zoom100Btn, new Constraint(zoom100BtnSize.width));
        add(decreaseBtn, new Constraint(buttonSize.width + 1, new Insets(0, 5,
            0, 0)));
        add(zoomSlider, new Constraint(sliderSize.width));
        add(increaseBtn, new Constraint(buttonSize.width + 1));
    }
}

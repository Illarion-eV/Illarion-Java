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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXStatusBar;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;

import illarion.mapedit.Lang;
import illarion.mapedit.MapEditor;
import illarion.mapedit.crash.AWTCrashHandler;

import illarion.common.bug.CrashReporter;

import illarion.graphics.Graphics;

/**
 * This is the main frame of the SWING GUI of the map editor. It implements the
 * ribbon bar that is the main control unit in this frame.
 * 
 * @author Martin Karing
 * @since 1.01
 * @version 1.01
 */
public final class MainFrame extends JRibbonFrame {
    /**
     * The serialization UID of this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Initialize the display frame for the map editor.
     */
    private MainFrame() {
        super();
    }
    
    /**
     * This class is a supporter class that is used to handle the proper
     * initialization of the main frame inside the SWING event loop.
     * 
     * @author Martin Karing
     * @version 1.01
     * @since 1.01
     */
    private static final class InitalizationHelper implements Runnable {
        /**
         * The MainFrame instance that is created by this helper.
         */
        private MainFrame createdFrame;
        
        /**
         * This function is called inside the event loop of swing and will
         * trigger the creation of the MainFrame.
         */
        @Override
        public void run() {
            createdFrame = MainFrame.createMainFrame();
        }
        
        /**
         * The public constructor required to allow the parent class proper
         * access.
         */
        public InitalizationHelper() {
            // nothing to do
        }
        
        /**
         * Get the MainFrame instance created. The result value is not available
         * until the {@link #run()} function is called by the Swing Event loop.
         * 
         * @return the created MainFrame instance
         */
        public MainFrame getCreatedFrame() {
            return createdFrame;
        }
    }
    
    /**
     * Create a mainframe. This class itself will ensure that the MainFrame is
     * properly created by the Swing event loop.
     * 
     * @return the created main frame instance.
     */
    public static MainFrame createMainFrame() {
        if (SwingUtilities.isEventDispatchThread()) {
            initializeLookAndFeel();
            final MainFrame createdFrame = new MainFrame();
            createdFrame.initialize();
            return createdFrame;
        }
        
        final InitalizationHelper helper = new InitalizationHelper();
        try {
            SwingUtilities.invokeAndWait(helper);
        } catch (InterruptedException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        }
        return helper.getCreatedFrame();
    }

    /**
     * This function will prepare the look and feel settings of java for
     * properly displaying the map editor.
     */
    @SuppressWarnings("nls")
    private static void initializeLookAndFeel() {
        JFrame.setDefaultLookAndFeelDecorated(MapEditor.getConfig()
            .getBoolean("decorateWindows"));
        JDialog.setDefaultLookAndFeelDecorated(MapEditor.getConfig()
            .getBoolean("decorateWindows"));

        try {
            SubstanceLookAndFeel.setSkin(MapEditor.getConfig().getString(
                "skin"));
        } catch (final Exception e) {
            MapEditor.getConfig().set("skin",
                "org.pushingpixels.substance.api.skin.OfficeSilver2007Skin");
            SubstanceLookAndFeel
                .setSkin("org.pushingpixels.substance.api.skin.OfficeSilver2007Skin");
        }

        CrashReporter.getInstance().setDisplay(CrashReporter.DISPLAY_SWING);
        AWTCrashHandler.init();
    }

    /**
     * This method initializes this
     */
    @SuppressWarnings("nls")
    private void initialize() {        
        if (MapEditor.getConfig().getBoolean("decorateWindows")) {
            setUndecorated(true);
            getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
        }

        setSize(new Dimension(500, 300));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setTitle(MapEditor.APPLICATION + " " + MapEditor.VERSION);

        final RibbonTask startTask =
            new RibbonTask(Lang.getMsg(MainFrame.class, "StartTask"),
                new PlacementBand(), new ViewBand());

        getRibbon().addTask(startTask);

        final JPanel content = new JPanel(new BorderLayout());
        getContentPane().add(content);

        final JXStatusBar statusBar = new StatusBar();
        content.add(statusBar, BorderLayout.SOUTH);
        content.add(Graphics.getInstance().getRenderDisplay().getRenderArea(),
            BorderLayout.CENTER);
        content.setBackground(Color.black);
        
        final MapsListing mapsListing = new MapsListing();
        content.add(mapsListing, BorderLayout.WEST);

        invalidate();
        validate();
        pack();

        setExtendedState(Frame.MAXIMIZED_BOTH);

        setVisible(true);
    }
}

/*
 * This file is part of the Illarion Mapeditor.
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

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
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

import illarion.input.Engines;
import illarion.input.InputManager;

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
    public MainFrame() {
        super();
    }

    /**
     * This function is just used to start the Swing Main frame of the map
     * editor for testing purposes.
     * 
     * @param args launch arguments
     */
    @SuppressWarnings("nls")
    public static void main(final String[] args) {

        Graphics.getInstance().setEngine(illarion.graphics.Engines.jogl);
        Graphics.getInstance().getRenderDisplay()
            .setDisplayOptions("jogl.newt", Boolean.TRUE.toString());
        InputManager.getInstance().setEngine(Engines.newt);
        Graphics.getInstance().setQuality(Graphics.QUALITY_NORMAL);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame.setDefaultLookAndFeelDecorated(MapEditor.getConfig()
                    .getBoolean("decorateWindows"));
                JDialog.setDefaultLookAndFeelDecorated(MapEditor.getConfig()
                    .getBoolean("decorateWindows"));

                CrashReporter.getInstance().setConfig(MapEditor.getConfig());
                CrashReporter.getInstance().setDisplay(
                    CrashReporter.DISPLAY_SWING);
                CrashReporter.getInstance().setMessageSource(
                    Lang.getInstance());
                AWTCrashHandler.init();

                try {
                    SubstanceLookAndFeel.setSkin(MapEditor.getConfig()
                        .getString("skin"));
                } catch (final Exception e) {
                    MapEditor
                        .getConfig()
                        .set("skin",
                            "org.pushingpixels.substance.api.skin.OfficeSilver2007Skin");
                    SubstanceLookAndFeel
                        .setSkin("org.pushingpixels.substance.api.skin.OfficeSilver2007Skin");
                }

                final MainFrame frame = new MainFrame();
                frame.initialize();
            }
        });
    }

    /**
     * This method initializes this
     */
    @SuppressWarnings("nls")
    public void initialize() {
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

        invalidate();
        validate();
        pack();

        setExtendedState(Frame.MAXIMIZED_BOTH);

        setVisible(true);

    }
}

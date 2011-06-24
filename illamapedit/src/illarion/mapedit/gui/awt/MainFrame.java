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
package illarion.mapedit.gui.awt;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import illarion.mapedit.MapEditor;

import illarion.graphics.Graphics;

/**
 * The main frame of the map editor display. This one allows access to all parts
 * of the editor.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class MainFrame extends Frame {
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(MainFrame.class);

    /**
     * The serialization UID of the main frame.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The menu bar of the main frame.
     */
    private final MapMenuBar menubar;

    /**
     * The message line that is used to display informations on the map editor.
     */
    private final MessageLine msgLine;

    /**
     * The render are that is used to display and scroll the map.
     */
    private final GLScrollpane renderArea;

    /**
     * The tool bar at the right side of the map editor GUI.
     */
    private final RightToolbar rightTool;

    /**
     * The tool bar that is displayed in the main frame at the top.
     */
    private final Toolbar toolbar;

    /**
     * Constructor for the main frame that prepares the entire GUI to be
     * displayed.
     */
    @SuppressWarnings("nls")
    public MainFrame() {
        super("Illarion Mapeditor " + MapEditor.VERSION);

        // this.getRibbon().addTask(new RibbonTask("Task", new
        // PlacementBand()));

        final Panel content = new Panel(new BorderLayout());
        add(content);

        renderArea =
            new GLScrollpane(Graphics.getInstance().getRenderDisplay()
                .getRenderArea());
        content.add(renderArea, BorderLayout.CENTER);

        toolbar = new Toolbar();
        content.add(toolbar, BorderLayout.NORTH);

        rightTool = new RightToolbar();
        content.add(rightTool, BorderLayout.EAST);

        msgLine = new MessageLine();
        content.add(msgLine, BorderLayout.SOUTH);

        final Dimension screenSize =
            Toolkit.getDefaultToolkit().getScreenSize();
        screenSize.height -= 30;
        setPreferredSize(screenSize);
        setLocation(0, 0);

        menubar = new MapMenuBar();
        setMenuBar(menubar);

        pack();
        validate();

        setExtendedState(Frame.MAXIMIZED_BOTH);

        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent we) {
                MapEditor.exit();
            }
        });

        final ArrayList<Image> icons = new ArrayList<Image>();
        try {
            icons.add(ImageIO.read(MainFrame.class.getClassLoader()
                .getResourceAsStream("mapedit64.png")));
        } catch (final IOException ex) {
            LOGGER.warn("Can't load mapedit64.png as icon");
        }
        try {
            icons.add(ImageIO.read(MainFrame.class.getClassLoader()
                .getResourceAsStream("mapedit48.png")));
        } catch (final IOException ex) {
            LOGGER.warn("Can't load mapedit48.png as icon");
        }
        try {
            icons.add(ImageIO.read(MainFrame.class.getClassLoader()
                .getResourceAsStream("mapedit32.png")));
        } catch (final IOException ex) {
            LOGGER.warn("Can't load mapedit32.png as icon");
        }
        try {
            icons.add(ImageIO.read(MainFrame.class.getClassLoader()
                .getResourceAsStream("mapedit16.png")));
        } catch (final IOException ex) {
            LOGGER.warn("Can't load mapedit16.png as icon");
        }

        setIconImages(icons);
    }

    /**
     * Get the menu bar of the main frame.
     * 
     * @return the menu bar instance used for the display
     * @see illarion.mapedit.gui.awt.MapMenuBar
     */
    public MapMenuBar getMenubar() {
        return menubar;
    }

    /**
     * Get the message line that is used to display some informations for the
     * player.
     * 
     * @return the message line used for the informations in the GUI
     * @see illarion.mapedit.gui.awt.MessageLine
     */
    public MessageLine getMessageLine() {
        return msgLine;
    }

    /**
     * Get the scroll pane that handles the offset values of the actual map
     * display.
     * 
     * @return the scroll pane that handles the offset for the map display
     * @see illarion.mapedit.gui.awt.GLScrollpane
     */
    public GLScrollpane getRenderArea() {
        return renderArea;
    }

    /**
     * Get the tool bar for the map editor that is displayed at the right side
     * of the GUI.
     * 
     * @return the right menu of the map editor
     * @see illarion.mapedit.gui.awt.RightToolbar
     */
    public RightToolbar getRightToolbar() {
        return rightTool;
    }

    /**
     * Get the tool bar of the main frame that is displayed at the top to access
     * the components there.
     * 
     * @return the used tool bar instance
     * @see illarion.mapedit.gui.awt.Toolbar
     */
    public Toolbar getToolbar() {
        return toolbar;
    }
} // @jve:decl-index=0:visual-constraint="10,10"

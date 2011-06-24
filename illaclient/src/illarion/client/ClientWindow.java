/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Image;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import illarion.client.graphics.LoadingScreen;
import illarion.client.world.Game;

import illarion.common.config.Config;
import illarion.common.config.ConfigChangeListener;
import illarion.common.util.IllarionLookAndFeel;

import illarion.graphics.GraphicResolution;
import illarion.graphics.Graphics;
import illarion.graphics.RenderDisplay;
import illarion.graphics.RenderTask;

/**
 * Default handler for all things related to the client window. This function
 * also prepares the openGL settings and includes openGL to the client window.
 * 
 * @author Martin Karing
 * @version 1.22
 * @since 1.5
 */
public final class ClientWindow implements ConfigChangeListener {
    /**
     * The key used to store the fullscreen value. In case this value is set
     * true the fullscreen mode is enabled in case its possible.
     */
    public static final String CFG_FULLSCREEN = "fullScreen"; //$NON-NLS-1$

    /**
     * The key used to store the resolution of the client window in the
     * configuration.
     */
    public static final String CFG_RESOLUTION = "resolution"; //$NON-NLS-1$

    /**
     * The height of the client screen that is needed to show all contents of
     * the client.
     * 
     * @deprecated Screen size is not static anymore. Do not use this variable
     */
    @Deprecated
    public static final int SCREEN_HEIGHT = 768;

    /**
     * The width of the client screen that is needed to show all contents of the
     * client.
     * 
     * @deprecated Screen size is not static anymore. Do not use this variable
     */
    @Deprecated
    public static final int SCREEN_WIDTH = 1024;

    /**
     * The path and the filename of the icon file that shall be displayed in the
     * windows of the Illarion Client.
     */
    private static final String CLIENT_ICON = "data/gui/icon.png"; //$NON-NLS-1$

    /**
     * The singleton instance of this class.
     */
    private static final ClientWindow INSTANCE = new ClientWindow();

    /**
     * The instance of the logger that is used to write out the data.
     */
    private static final Logger LOGGER = Logger.getLogger(ClientWindow.class);

    /**
     * The icon image that is used in the windows of the Illarion client.
     */
    private Image clientIcon;

    /**
     * Pointer to the used render display handler that takes care for the canvas
     * that renders the screen.
     */
    private RenderDisplay display;

    /**
     * The JFrame that displays the client.
     */
    private Frame displayFrame;

    /**
     * The height of the screen the client is shown in.
     */
    private int screenHeight = 0;

    /**
     * The width of the screen the client is shown in.
     */
    private int screenWidth = 0;

    /**
     * Private constructor so nothing can create a instance of this object but
     * the singleton instance.
     */
    private ClientWindow() {
        super();
        IllaClient.getCfg().addListener(CFG_FULLSCREEN, this);
        IllaClient.getCfg().addListener(CFG_RESOLUTION, this);
    }

    /**
     * Get the singleton instance of this object.
     * 
     * @return the singleton instance of this object
     */
    public static ClientWindow getInstance() {
        return INSTANCE;
    }

    /**
     * Load a icon file from a path and prepare it for usage as window icon
     * file. Only JPEG, GIF and PNG is supported.
     * 
     * @param path the path to the file that shall be loaded
     * @return the ImageIcon that was loaded from the file.
     */
    @SuppressWarnings("nls")
    public static Image loadIcon(final String path) {
        try {
            final InputStream in = IllaClient.getResource(path);
            return ImageIO.read(in);
        } catch (final Exception e) {
            LOGGER.error("can't load image " + path, e);
            return null;
        }
    }

    /**
     * Set a scaling value to the client window. All following drawing
     * operations are scaled by this. Positions are corrected according to the
     * scaling automatically.
     * 
     * @param scale the scaling value &gt;1.0 to increase the size, &lt;1.0 to
     *            reduce the size
     */
    public void applyScaling(final float scale) {
        display.applyScaling(scale);
    }

    @Override
    public void configChanged(final Config cfg, final String key) {
        if (key.equals(CFG_RESOLUTION)) {
            final GraphicResolution[] resolutions =
                display.getPossibleResolutions();
            final String selected = cfg.getString(CFG_RESOLUTION);
            for (final GraphicResolution res : resolutions) {
                if (res.toString().equals(selected)) {
                    display.setDisplayMode(res);

                    screenWidth = display.getWidth();
                    screenHeight = display.getHeight();

                    if (displayFrame != null) {
                        displayFrame.pack();
                        displayFrame.setLocationRelativeTo(null);
                    }
                    return;
                }
            }

            // overwrite with default value in case invalid value was insert
            cfg.set(CFG_RESOLUTION,
                new GraphicResolution(800, 600, 32, 60).toString());

            return;
        }

        if (key.equals(CFG_FULLSCREEN)) {
            if (display.isFullscreen() != cfg.getBoolean(CFG_FULLSCREEN)) {
                display.toogleFullscreen();
            }
            return;
        }
    }

    /**
     * Get the frame of the display that is used as render target of the openGL
     * Display. Careful with this instance, it will cause massive problems if
     * that Frame is not displayable only a short time.
     * 
     * @return The display frame of the client
     */
    public Frame getFrame() {
        return displayFrame;
    }

    /**
     * Get the clients render display handler.
     * 
     * @return the render display
     */
    public RenderDisplay getRenderDisplay() {
        return display;
    }

    /**
     * Get the height of the screen the client is shown in.
     * 
     * @return the height of the client screen
     */
    public int getScreenHeight() {
        return screenHeight;
    }

    /**
     * Get the width of the screen the client is shown in.
     * 
     * @return the width of the client screen
     */
    public int getScreenWidth() {
        return screenWidth;
    }

    /**
     * Remove a previously added scaling value.
     */
    public void resetScaling() {
        display.resetScaling();
    }

    /**
     * Sets the default icon of the Illarion client a JFrame.
     * 
     * @param targetFrame the frame that shall get the icon
     */
    public void setIcon(final Frame targetFrame) {
        if (clientIcon == null) {
            final Image img = loadIcon(CLIENT_ICON);
            if (img != null) {
                clientIcon = img;
            }
        }
        if (clientIcon != null) {
            targetFrame.setIconImage(clientIcon);
        }
    }

    /**
     * Switch between full screen and windowed mode.
     */
    @SuppressWarnings("nls")
    public void toggleFullscreen() {
        IllaClient.getCfg().set("fullScreen", !display.isFullscreen());
    }

    /**
     * Update the current FPS display in the title of the client window.
     */
    @SuppressWarnings("nls")
    public void updateFPS() {
        if (!IllaClient.getExitRequested()) {
            displayFrame.setTitle(IllaClient.getVersionText()
                + "         FPS: "
                + Graphics.getInstance().getRenderManager().getRealFPS()
                + "         Particles: "
                + Game.getParticleSystem().getParticleCount()
                + "         Objects: " + display.getObjects());
        }
    }

    /**
     * Destroy the display and dispose the frame. After calling this function
     * all further rendering actions are impossible.
     */
    protected void destruct() {
        Graphics.getInstance().getRenderDisplay().stopRendering();
        Graphics.getInstance().getRenderDisplay().shutdown();
        displayFrame.dispose();
    }

    /**
     * Set the focus on the game screen.
     */
    protected void focus() {
        displayFrame.requestFocus();
        display.getRenderArea().requestFocusInWindow();
    }

    /**
     * Start the game display and load up the first basic settings in so its
     * prepared to render some OpenGL.
     */
    protected void init() {
        initWindow();
        initGraphics();

        Graphics.getInstance().getRenderManager()
            .addTask(LoadingScreen.getInstance());
    }

    /**
     * Initialize OpenGL and create main window, also prepare the sprite of the
     * splash screen in case the client is not set to low memory settings.
     */
    private void initGraphics() {
        configChanged(IllaClient.getCfg(), CFG_FULLSCREEN);
    }

    /**
     * Prepare the window that shall show the openGL content.
     */
    private void initWindow() {
        IllarionLookAndFeel.setupLookAndFeel();
        // create canvas that shall show the openGL content
        display = Graphics.getInstance().getRenderDisplay();

        configChanged(IllaClient.getCfg(), CFG_RESOLUTION);

        final Component displayParent = display.getRenderArea();
        displayParent.setBackground(Color.black);
        displayParent.setVisible(true);

        // set up the window settings
        displayFrame = new Frame();
        displayFrame.setLayout(new BorderLayout(0, 0));
        displayFrame.setTitle(IllaClient.getVersionText());
        displayFrame.setBackground(Color.black);

        setIcon(displayFrame);

        displayFrame.addWindowListener(new ClientWindowListener());
        displayFrame.setResizable(false);
        displayFrame.setFocusable(false);
        displayFrame.setFocusableWindowState(true);
        displayFrame.setFocusTraversalKeysEnabled(false);
        displayParent.setFocusable(true);
        displayParent.setFocusTraversalKeysEnabled(false);

        // add the canvas to the window and make the canvas the openGL render
        // target.
        displayFrame.add(displayParent, BorderLayout.CENTER);
        displayFrame.pack();

        displayFrame.setLocationRelativeTo(null);
        displayFrame.setVisible(true);

        displayParent.requestFocusInWindow();

        final RenderDisplay usedDisplay = display;
        Graphics.getInstance().getRenderManager().addTask(new RenderTask() {
            @Override
            public boolean render(final int delta) {
                usedDisplay.startRendering();
                return false;
            }
        });
    }
}

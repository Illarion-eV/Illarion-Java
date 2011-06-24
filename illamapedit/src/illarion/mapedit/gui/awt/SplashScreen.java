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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

/**
 * The splash screen is used to display that the map editor is still starting.
 * 
 * @author Martin Karing
 * @since 1.00
 */
public final class SplashScreen extends Frame {
    /**
     * The singleton instance of this class.
     */
    private static SplashScreen instance = new SplashScreen();

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(SplashScreen.class);

    /**
     * The serialization UID of this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The image rendered on the splash screen.
     */
    private final Image splashImage;

    /**
     * Private constructor to avoid any instances but the singleton instance.
     */
    @SuppressWarnings("nls")
    private SplashScreen() {
        super();
        setUndecorated(true);
        setAlwaysOnTop(true);

        final InputStream in =
            SplashScreen.class.getClassLoader().getResourceAsStream(
                "mapeditsplash.png");
        if (in == null) {
            splashImage = null;
            return;
        }

        Image image = null;
        try {
            image = ImageIO.read(in);
        } catch (final IOException ex) {
            LOGGER.debug("Can't load splash image");
        }
        splashImage = image;

        setPreferredSize(new Dimension(splashImage.getWidth(null),
            splashImage.getHeight(null)));

        final Dimension screenSize =
            Toolkit.getDefaultToolkit().getScreenSize();

        validate();
        setSize(getPreferredSize());

        setLocation((screenSize.width - splashImage.getWidth(null)) / 2,
            (screenSize.height - splashImage.getHeight(null)) / 2);

    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance
     */
    public static SplashScreen getInstance() {
        return instance;
    }

    /**
     * Free all ressources used by this splash screen.
     */
    @Override
    public void dispose() {
        super.dispose();
        instance = null;
    }

    /**
     * Draw the splash screen.
     * 
     * @param g the graphics reference used to draw this screen
     */
    @Override
    public void paint(final Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(splashImage, 0, 0, null);
    }
}

/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Mapeditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Mapeditor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.gui;

import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * @author Tim
 */
public class SplashScreen extends JWindow {
    private static final SplashScreen INSTANCE = new SplashScreen();
    private static final Logger LOGGER = Logger.getLogger(SplashScreen.class);
    private static final Color TRANSPARENT = new Color(0, 0, 0, 0);
    public static final int HEIGHT_ADJUSTMENT = 30;
    private static final int FONT_SIZE = 20;
    public static final Font FONT = new Font("Arial", Font.BOLD, FONT_SIZE);
    private final Image background;
    private Image img;
    private String message;

    private SplashScreen() {
        getContentPane().setBackground(TRANSPARENT);
        try {
            img = ImageIO.read(SplashScreen.class.getResource("/mapeditsplash.png"));
        } catch (IOException e) {
            LOGGER.warn("Can't read splash image", e);
        }
        setSize(img.getWidth(null), img.getHeight(null) + HEIGHT_ADJUSTMENT);
        final Dimension screenSize =
                Toolkit.getDefaultToolkit().getScreenSize();
        setLocationRelativeTo(null);
        background = makeScreenShot();
        message = "Starting..";
    }

    private Image makeScreenShot() {
        try {
            Robot r = new Robot();
            return r.createScreenCapture(new Rectangle(getX(), getY(), getWidth(), getHeight()));
        } catch (AWTException e) {
            return null;
        }

    }

    public static SplashScreen getInstance() {
        return INSTANCE;
    }

    @Override
    public void paint(final Graphics g) {
        if (background != null) {
            g.drawImage(background, 0, 0, null);
        }
        if (img != null) {
            g.drawImage(img, 0, 0, null);
        }

        if (message != null) {
            g.setColor(Color.RED);
            g.setFont(FONT);
            g.drawString(message, 5, (img.getHeight(null) + HEIGHT_ADJUSTMENT) - 10);
        }
    }

    public void setMessage(final String string) {
        message = string;
        repaint();
    }
}

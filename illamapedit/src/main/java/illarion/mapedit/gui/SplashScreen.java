/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package illarion.mapedit.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * @author Tim
 */
public class SplashScreen extends JWindow {
    private static final Logger LOGGER = LoggerFactory.getLogger(SplashScreen.class);
    private static final Color TRANSPARENT = new Color(0, 0, 0, 0);
    public static final int HEIGHT_ADJUSTMENT = 30;
    private static final int FONT_SIZE = 20;
    public static final Font FONT = new Font("Arial", Font.BOLD, FONT_SIZE);
    private static final SplashScreen INSTANCE = new SplashScreen();
    @Nullable
    private final Image background;
    @Nullable
    private transient Image img;
    private String message;

    private SplashScreen() {
        img = null;
        getContentPane().setBackground(TRANSPARENT);
        Image img = getImage();
        if (img != null) {
            setSize(img.getWidth(null), img.getHeight(null) + HEIGHT_ADJUSTMENT);
            setLocationRelativeTo(null);
        } else {
            LOGGER.warn("Can't read splash image!");
        }
        background = makeScreenShot();
        message = "Starting..";
    }

    @Nullable
    private Image getImage() {
        if (img == null) {
            try {
                img = ImageIO.read(SplashScreen.class.getResource("/mapeditsplash.png"));
            } catch (IOException ignored) {
            }
        }
        return img;
    }

    @Nullable
    private Image makeScreenShot() {
        try {
            Robot r = new Robot();
            return r.createScreenCapture(new Rectangle(getX(), getY(), getWidth(), getHeight()));
        } catch (AWTException e) {
            return null;
        }
    }

    @Nonnull
    public static SplashScreen getInstance() {
        return INSTANCE;
    }

    @Override
    public void paint(@Nonnull Graphics g) {
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

    public void setMessage(String string) {
        message = string;
        repaint();
    }
}

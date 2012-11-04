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

import illarion.mapedit.events.MessageStringEvent;
import org.apache.log4j.Logger;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * @author Tim
 */
public class SplashScreen extends JPanel {
    private static final SplashScreen INSTANCE = new SplashScreen();
    private static final Logger LOGGER = Logger.getLogger(SplashScreen.class);
    private static final Color TRANSPARENT = new Color(0, 0, 0, 0);
    public static final int HEIGHT_ADJUSTMENT = 30;
    private static final int FONT_SIZE = 20;
    private final Frame frame;
    private Image img;
    private String message;

    private SplashScreen() {
        frame = new Frame("Loading MapEditor");
        frame.setAutoRequestFocus(true);
        frame.setUndecorated(true);
        frame.setBackground(TRANSPARENT);
        setBackground(TRANSPARENT);
        frame.add(this);
        try {
            img = ImageIO.read(SplashScreen.class.getResource("/mapeditsplash.png"));
        } catch (IOException e) {
            LOGGER.warn("Can't read splash image", e);
        }
        frame.setSize(img.getWidth(null), img.getHeight(null) + HEIGHT_ADJUSTMENT);
        final Dimension screenSize =
                Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((screenSize.width - img.getWidth(null)) / 2,
                (screenSize.height - img.getHeight(null)) / 2);
        message = "Starting..";
    }

    public static SplashScreen getInstance() {
        return INSTANCE;
    }

    @Override
    public void setVisible(final boolean aFlag) {
        frame.setVisible(aFlag);
        if (aFlag) {
            AnnotationProcessor.process(this);
        } else {
            AnnotationProcessor.unprocess(this);
        }
    }

    @Override
    public void paintComponent(final Graphics g) {
        paintComponents(g);
        g.clearRect(0, 0, getWidth(), getHeight());
        if (img != null) {
            g.drawImage(img, 0, 0, null);
        }

        if (message != null) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, FONT_SIZE));
            g.drawString(message, 5, img.getHeight(null));
        }
    }

    @EventSubscriber(eventClass = MessageStringEvent.class)
    public void onMessageStringEvent(final MessageStringEvent e) {
        message = e.getMessage();
        repaint();
    }

}

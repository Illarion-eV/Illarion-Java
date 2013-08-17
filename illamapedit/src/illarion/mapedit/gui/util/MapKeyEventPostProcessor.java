/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2013 - Illarion e.V.
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
package illarion.mapedit.gui.util;

import illarion.mapedit.events.MapScrollEvent;
import org.bushe.swing.event.EventBus;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @author Fredrik K
 */
public class MapKeyEventPostProcessor implements KeyEventPostProcessor {

    private static class KeyEventRunnable implements Runnable {
        private final KeyEvent keyEvent;

        private KeyEventRunnable(final KeyEvent keyEvent) {
            this.keyEvent = keyEvent;
        }

        @Override
        public void run() {
            if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
                EventBus.publish(new MapScrollEvent(0, 5));
            }
            if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
                EventBus.publish(new MapScrollEvent(0, -5));
            }

            if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
                EventBus.publish(new MapScrollEvent(5, 0));

            }
            if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
                EventBus.publish(new MapScrollEvent(-5, 0));
            }
        }
    }

    @Override
    public boolean postProcessKeyEvent (final KeyEvent keyEvent) {
        EventQueue.invokeLater(new KeyEventRunnable(keyEvent));
        return false;
    }
}

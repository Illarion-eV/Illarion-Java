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
package illarion.mapedit.util;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @author Tim
 */
public enum MouseButton {
    NoButton,
    LeftButton,
    MiddleButton,
    RightButton,
    OtherButton;

    public static MouseButton getButton(final int btn) {
        switch (btn) {
            case 0:
                return NoButton;
            case 1:
                return LeftButton;
            case 2:
                return MiddleButton;
            case 3:
                return LeftButton;
            default:
                return OtherButton;
        }
    }

    public static int getButtonCount() {
        return MouseInfo.getNumberOfButtons();
    }

    public static MouseButton fromAwt(final int buttonMask) {
        if ((buttonMask & MouseEvent.BUTTON3_MASK) != 0) {
            return MouseButton.RightButton;
        } else if ((buttonMask & MouseEvent.BUTTON2_MASK) != 0) {
            return MouseButton.MiddleButton;
        } else if ((buttonMask & MouseEvent.BUTTON1_MASK) != 0) {
            return MouseButton.LeftButton;
        } else {
            return MouseButton.OtherButton;
        }
    }
}

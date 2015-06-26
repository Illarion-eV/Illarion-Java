/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
package illarion.mapedit.util;

import javax.annotation.Nonnull;
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

    @Nonnull
    public static MouseButton getButton(int btn) {
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

    @Nonnull
    public static MouseButton fromAwt(int buttonMask) {
        if ((buttonMask & MouseEvent.BUTTON3_MASK) != 0) {
            return RightButton;
        } else if ((buttonMask & MouseEvent.BUTTON2_MASK) != 0) {
            return MiddleButton;
        } else if ((buttonMask & MouseEvent.BUTTON1_MASK) != 0) {
            return LeftButton;
        } else {
            return OtherButton;
        }
    }
}

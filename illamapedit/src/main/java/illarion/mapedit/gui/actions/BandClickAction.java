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
package illarion.mapedit.gui.actions;

import org.pushingpixels.flamingo.api.common.AbstractCommandButton;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Fredrik K
 */
public class BandClickAction extends AbstractAction {
    private final AbstractCommandButton button;

    public BandClickAction(final AbstractCommandButton button) {
        this.button = button;
    }

    public void actionPerformed(ActionEvent e) {
        button.doActionClick();
    }
}

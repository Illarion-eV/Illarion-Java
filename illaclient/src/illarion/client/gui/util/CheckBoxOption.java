/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.gui.util;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.checkbox.builder.CheckboxBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;

/**
 * @author Stefano Bonicatti &lt;smjert@gmail.com&gt;
 */
public class CheckBoxOption implements Option<Boolean, CheckBox>{

    private CheckBox control;
    private String key;

    public CheckBoxOption(String name, Element parent, Nifty nifty, Screen screen)
    {
        key = name;

        CheckboxBuilder checkBoxBuilder = new CheckboxBuilder(name + "GUI");
        Element checkBoxElement = checkBoxBuilder.build(nifty, screen, parent);
        control = checkBoxElement.getNiftyControl(CheckBox.class);
    }
    
    @Override
    public Boolean getValue() {
        if(control == null)
            return false;
        return control.isChecked();
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public CheckBox getControl() {
        return control;
    }
}

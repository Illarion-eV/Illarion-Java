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
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.controls.dropdown.builder.DropDownBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;

/**
 *
 * @author Stefano Bonicatti &lt;smjert@gmail.com&gt;
 */
public class DropDownOption<T> implements Option<String, DropDown<DropDownItem<T>>>{
    
    private DropDown<DropDownItem<T>> control;
    private String key;
    
    public DropDownOption(String name, Element parent, Nifty nifty, Screen screen)
    {
        key = name;

        DropDownBuilder dropDownBuilder = new DropDownBuilder(name + "GUI");
        Element dropDownElement = dropDownBuilder.build(nifty, screen, parent);

        control = dropDownElement.getNiftyControl(DropDown.class);

        control.setViewConverter(new DropDownItemViewConverter<DropDownItem<T>>());
    }
    
    @Override
    public String getValue() {
        if(control == null)
            return "";

        return control.getSelection().getKey().toString();
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public DropDown<DropDownItem<T>> getControl() {
        return control;
    }
}

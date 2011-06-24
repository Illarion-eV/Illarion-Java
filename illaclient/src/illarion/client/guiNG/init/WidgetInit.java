/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.guiNG.init;

import java.io.Serializable;

import illarion.client.guiNG.elements.Widget;

/**
 * Classes with this interface are allowed to be registered as classes that are
 * triggered when the widget is loaded again from a serialized state. All
 * variables that are needed but not written to the serialized output can be
 * restored using such a class.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public interface WidgetInit extends Serializable {
    /**
     * This function is called after the widget was loaded from the
     * serialization.
     * 
     * @param widget the widget that is loaded
     */
    void initWidget(Widget widget);
}

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
package illarion.client.guiNG.event;

import java.io.Serializable;

import illarion.client.guiNG.elements.Widget;

/**
 * This interface allows a class to be registered as widget event handler and
 * handle actions a widget receives.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public interface WidgetEvent extends Serializable {
    /**
     * Handle the event this class was registered to.
     * 
     * @param source the widget that fired this event.
     */
    void handleEvent(Widget source);
}

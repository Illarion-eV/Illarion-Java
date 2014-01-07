/*
 * This file is part of the Illarion easyQuest Editor.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion easyQuest Editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion easyQuest Editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion easyQuest Editor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easyquest.quest;

import javax.annotation.Nonnull;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Handler implements Serializable {

    @Nonnull
    private String type;
    @Nonnull
    private Object[] parameters;

    public Handler() {
    }

    public Handler(@Nonnull final String type, @Nonnull final Object... parameters) {
        this.type = type;
        this.parameters = parameters;
    }

    public void setParameters(@Nonnull final Object[] parameters) {
        this.parameters = parameters;
    }

    public void setType(@Nonnull final String type) {
        this.type = type;
    }

    @Nonnull
    public String getType() {
        return type;
    }

    @Nonnull
    public Object[] getParameters() {
        return parameters;
    }
}
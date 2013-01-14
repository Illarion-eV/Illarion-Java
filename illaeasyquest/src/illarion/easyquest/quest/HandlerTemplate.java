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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class HandlerTemplate {
    private String name;
    @Nullable
    private String title;
    private List<TemplateParameter> parameters;
    private int playerParameterIndex;

    public HandlerTemplate(String name) {
        this.name = name;
        title = null;
        parameters = new ArrayList<TemplateParameter>();
        playerParameterIndex = -1;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public int size() {
        return parameters.size();
    }

    public TemplateParameter getParameter(int number) {
        return parameters.get(number);
    }

    public void addParameter(TemplateParameter parameter) {
        parameters.add(parameter);
    }

    public void addPlayerParameterAt(int position) {
        playerParameterIndex = position;
    }

    public int getPlayerIndex() {
        return playerParameterIndex;
    }

    public boolean isComplete() {
        return (title != null) && (parameters.size() > 0);
    }

    @Nullable
    public String toString() {
        return title;
    }
}
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
package illarion.client.util.translation.yandex;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class Response {
    @SerializedName("code")
    private int code;
    @SerializedName("lang")
    private String lang;
    @SerializedName("text")
    private List<String> text;

    @Nonnull
    public List<String> getTexts() {
        if (text == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(text);
    }
}

/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
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
package illarion.client.util.account.response;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class CharacterCreateGetResponse {
    @SerializedName("races")
    private List<RaceResponse> races;

    @SerializedName("startPacks")
    private List<StartPackResponse> startPacks;

    @SerializedName("error")
    private ErrorResponse error;

    public List<RaceResponse> getRaces() {
        if (races == null) {
            throw new IllegalStateException("This object is in error state. Can't fetch anything but the error.");
        }
        return Collections.unmodifiableList(races);
    }

    public List<StartPackResponse> getStartPacks() {
        if (startPacks == null) {
            throw new IllegalStateException("This object is in error state. Can't fetch anything but the error.");
        }
        return Collections.unmodifiableList(startPacks);
    }

    @Nullable
    public ErrorResponse getError() {
        return error;
    }
}

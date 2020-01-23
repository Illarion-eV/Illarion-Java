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

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class CheckResponse {
    @SerializedName("success")
    public boolean success;

    @SerializedName("checkedType")
    public String checkedType;

    @SerializedName("checkedValue")
    public String checkedValue;

    @SerializedName("description")
    public String description;

    public boolean isSuccess() {
        return success;
    }

    @Nullable
    public String getCheckedType() {
        return checkedType;
    }

    @Nullable
    public String getCheckedValue() {
        return checkedValue;
    }

    @Nullable
    public String getDescription() {
        return description;
    }
}

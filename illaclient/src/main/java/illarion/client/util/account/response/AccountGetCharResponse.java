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
import illarion.common.types.CharacterId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.Date;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AccountGetCharResponse {
    @Nullable
    @SerializedName("id")
    private CharacterId charId;

    @Nullable
    @SerializedName("name")
    private String name;

    @SerializedName("status")
    private int status;

    @SerializedName("race")
    private int raceId;

    @SerializedName("sex")
    private int typeId;

    @Nullable
    @SerializedName("lastSaveTime")
    private Date lastSaveTime;

    @Nullable
    @SerializedName("onlineTime")
    private Duration onlineTime;

    @Nonnull
    public CharacterId getCharId() {
        assert charId != null;

        return charId;
    }

    @Nonnull
    public String getName() {
        assert name != null;

        return name;
    }

    public int getStatus() {
        return status;
    }

    public int getRaceId() {
        return raceId;
    }

    public int getTypeId() {
        return typeId;
    }

    @Nonnull
    public Date getLastSaveTime() {
        assert lastSaveTime != null;

        return lastSaveTime;
    }

    @Nonnull
    public Duration getOnlineTime() {
        assert onlineTime != null;

        return onlineTime;
    }
}

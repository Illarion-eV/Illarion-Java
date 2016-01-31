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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * This is the deserialization object for a response of account information.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AccountGetResponse {
    @Nullable
    @SerializedName("name")
    private String name;

    @SerializedName("state")
    private int state;

    @SerializedName("maxChars")
    private int maximalCharacters;

    @Nullable
    @SerializedName("lang")
    private String language;

    @Nullable
    @SerializedName("chars")
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<AccountGetCharsResponse> chars;

    @Nullable
    @SerializedName("create")
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<AccountGetCreateResponse> createRoutes;

    @Nonnull
    public String getName() {
        assert name != null;

        return name;
    }

    public int getState() {
        return state;
    }

    public int getMaximalCharacters() {
        return maximalCharacters;
    }

    @Nonnull
    public Locale getLanguage() {
        return "de".equals(language) ? Locale.GERMAN : Locale.ENGLISH;
    }

    @Nonnull
    public List<AccountGetCharsResponse> getChars() {
        return (chars == null) ? Collections.emptyList() : Collections.unmodifiableList(chars);
    }

    @Nonnull
    public List<AccountGetCreateResponse> getCreateRoutes() {
        return (createRoutes == null) ? Collections.emptyList() : Collections.unmodifiableList(createRoutes);
    }
}

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
package illarion.client.util.account.form;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AccountCreateForm {
    @SerializedName("name")
    @Nonnull
    private String name;

    @SerializedName("email")
    @Nullable
    private String eMail;

    @SerializedName("password")
    @Nonnull
    private String password;

    public AccountCreateForm(@Nonnull String name, @Nonnull String password, @Nullable String eMail) {
        this.name = name;
        this.eMail = ((eMail == null) || eMail.isEmpty()) ? null : eMail;
        this.password = password;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(@Nonnull String name) {
        this.name = name;
    }

    @Nullable
    public String geteMail() {
        return eMail;
    }

    public void seteMail(@Nullable String eMail) {
        this.eMail = eMail;
    }

    @Nonnull
    public String getPassword() {
        return password;
    }

    public void setPassword(@Nonnull String password) {
        this.password = password;
    }
}

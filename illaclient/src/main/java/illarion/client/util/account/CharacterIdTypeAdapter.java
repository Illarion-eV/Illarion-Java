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
package illarion.client.util.account;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import illarion.common.types.CharacterId;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Objects;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class CharacterIdTypeAdapter extends TypeAdapter<CharacterId> {
    @Override
    public void write(JsonWriter out, CharacterId value) throws IOException {
        out = Objects.requireNonNull(out);
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.getAsInteger());
        }
    }

    @Nullable
    @Override
    public CharacterId read(JsonReader in) throws IOException {
        in = Objects.requireNonNull(in);
        int id = in.nextInt();
        return new CharacterId(id);
    }
}

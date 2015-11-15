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
package illarion.client.util.account;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import javax.annotation.Nullable;
import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class DurationTypeAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter out, Duration value) throws IOException {
        out = Objects.requireNonNull(out);
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.toString());
        }
    }

    @Nullable
    @Override
    public Duration read(JsonReader in) throws IOException {
        in = Objects.requireNonNull(in);
        String periodString = in.nextString();
        if (periodString == null) {
            return null;
        }
        return Duration.parse(periodString);
    }
}

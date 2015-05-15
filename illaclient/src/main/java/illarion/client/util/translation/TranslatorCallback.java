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
package illarion.client.util.translation;

import javax.annotation.Nullable;

/**
 * This interface is used to describe a callback that is called by the translator once a translation is done.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface TranslatorCallback {
    void sendTranslation(@Nullable String translation);
}

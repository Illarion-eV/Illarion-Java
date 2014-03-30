/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package illarion.client.gui;

import javax.annotation.Nonnull;

/**
 * This interface is used to access the inform GUI controller. Using it the inform messages are displayed on the
 * screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface InformGui {
    /**
     * Show a broadcast inform on the screen.
     *
     * @param message the message
     */
    void showBroadcastInform(@Nonnull String message);

    /**
     * Show a script inform message on the screen.
     *
     * @param priority the priority of the message
     * @param message the message
     */
    void showScriptInform(int priority, @Nonnull String message);

    /**
     * Show a server inform message on the screen.
     *
     * @param message the message
     */
    void showServerInform(@Nonnull String message);

    /**
     * Show a text-to inform message on the screen.
     *
     * @param message the message
     */
    void showTextToInform(@Nonnull String message);
}

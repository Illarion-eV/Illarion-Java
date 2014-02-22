/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.gui;

import de.lessvoid.nifty.tools.Color;
import illarion.client.world.Char;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This interfaces defines the access on the chat related GUI.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface ChatGui {
    /**
     * The default color of text entries.
     */
    Color COLOR_DEFAULT = new Color("#ffffff");

    /**
     * The color of shouted or important messages
     */
    Color COLOR_SHOUT = new Color("#ff0000");

    /**
     * The color of whispered text.
     */
    Color COLOR_WHISPER = new Color("#c0c0c0");

    /**
     * The color of emoted.
     */
    Color COLOR_EMOTE = new Color("#ffcc33");

    /**
     * The color of emoted.
     */
    Color COLOR_INFORM = new Color("#9298D1");

    /**
     * Activate the chat input line. All keyboard input will be forwarded to the chat line once its activated.
     */
    void activateChatBox();

    /**
     * Deactivate the chat input line. This disables the chat input. No keyboard input will be forwarded to it anymore.
     *
     * @param clear in case this is {@code true} all text in the chat input will be removed
     */
    void deactivateChatBox(boolean clear);

    /**
     * Add a entry to the chat box.
     *
     * @param message the message to add to the chat box
     * @param color   the color of the message
     */
    void addChatMessage(@Nonnull String message, @Nonnull Color color);

    /**
     * Display a chat bubble on the screen.
     *
     * @param character the character this chat message is assigned to
     * @param message   the message that is displayed
     * @param color     the color of the message
     */
    void showChatBubble(@Nullable Char character, @Nonnull String message, @Nonnull Color color);
}

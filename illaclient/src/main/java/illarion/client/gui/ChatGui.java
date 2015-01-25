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
    Color COLOR_DEFAULT = new Color("#FFFFFF");

    /**
     * The color of shouted or important messages
     */
    Color COLOR_SHOUT = new Color("#FF0000");

    /**
     * The color of whispered text.
     */
    Color COLOR_WHISPER = new Color("#C0C0C0");

    /**
     * The color of emoted.
     */
    Color COLOR_EMOTE = new Color("#FFCC33");

    /**
     * The color of inform.
     */
    Color COLOR_INFORM = new Color("#9298D1");

    /**
     * The color of high inform.
     */
    Color COLOR_HIGH_INFORM = new Color("#FF8080");

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
     * @param color the color of the message
     */
    void addChatMessage(@Nonnull String message, @Nonnull Color color);

    /**
     * Display a chat bubble on the screen.
     *
     * @param character the character this chat message is assigned to
     * @param message the message that is displayed
     * @param color the color of the message
     */
    void showChatBubble(@Nullable Char character, @Nonnull String message, @Nonnull Color color);

    /**
     * Check if the chat box is currently activated.
     *
     * @return {@code true} in case the chat box is currently active
     */
    boolean isChatBoxActive();
}

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

 /** 
  * Colour Palette: (An image of all the colours can be found in the same folder as this file, named "gui_palette.png")
  *.turquoise { color: #1abc9c; } - N/A
  *.green-sea { color: #16a085; } - !tt
  *.emerald { color: #2ecc71; } - N/A
  *.nephritis { color: #27ae60; } - N/A
  *.peter-river { color: #3498db; } - Inform
  *.belize-hole { color: #2980b9; } - N/A
  *.amethyst { color: #9b59b6; } - !bc
  *.wisteria { color: #8e44ad; } - N/A
  *.wet-asphalt { color: #34495e; } - N/A
  *.midnight-blue { color: #2c3e50; } - N/A
  *.sunflower { color: #f1c40f; } - #me
  *.orange { color: #f39c12; } - N/A
  *.carrot { color: #e67e22; } - N/A
  *.pumpkin { color: #d35400; } - High Inform
  *.alizarin { color: #e74c3c; } - N/A
  *.pomegranate { color: #c0392b; } - #s
  *.clouds { color: #ecf0f1; } - Default
  *.silver { color: #bdc3c7; } - N/A
  *.concrete { color: #95a5a6; } - N/A
  *.asbestos { color: #7f8c8d; } - #w
  */

public interface ChatGui {
    /**
     * The default color of text entries.
     */
    Color COLOR_DEFAULT = new Color("#ECF0F1");

    /**
     * The color of shouted or important messages
     */
    Color COLOR_SHOUT = new Color("#C0392B");

    /**
     * The color of whispered text.
     */
    Color COLOR_WHISPER = new Color("#7F8C8D");

    /**
     * The color of emoted.
     */
    Color COLOR_EMOTE = new Color("#F1C40F");

    /**
     * The color of inform.
     */
    Color COLOR_INFORM = new Color("#3498DB");

    /**
     * The color of high inform.
     */
    Color COLOR_HIGH_INFORM = new Color("#D35400");

    /**
     * Color for (talkto = !tt) admin messages.
     */
    Color COLOR_TT_INFORM = new Color("#16A085");

    /**
     * Color for (!bc) broadcasts.
     */
    Color COLOR_BC_INFORM = new Color("#9B59B6");



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

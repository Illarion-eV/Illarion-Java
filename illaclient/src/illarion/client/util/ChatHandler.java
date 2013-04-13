/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
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
package illarion.client.util;

import illarion.client.gui.ChatGui;
import illarion.client.world.Char;
import illarion.client.world.World;
import illarion.common.types.Location;
import org.apache.log4j.Logger;
import org.illarion.engine.graphic.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.RegEx;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This Chat handler fetches all texts send by the network interface and
 * forwards the data to the required parts of the client. It takes care for
 * transforming the text properly for each part of the client.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ChatHandler {
    /**
     * The possible speech modes that are displays on the screen.
     */
    public enum SpeechMode {
        /**
         * Speech mode for emotes.
         */
        @SuppressWarnings("nls")
        Emote(Color.WHITE, "^\\s*[/#]me(.*)\\s*$", "$1"),

        /**
         * Speech mode for normal spoken text.
         */
        Normal(Color.WHITE, null, null),

        /**
         * Speech mode for OOC messages.
         */
        @SuppressWarnings("nls")
        Ooc(Color.GRAY, "^\\s*[/#]o(oc)?\\s*(.*)\\s*$", "$2"),

        /**
         * Speech mode for shouted text.
         */
        @SuppressWarnings("nls")
        Shout(Color.RED, "^\\s*[/#]s(hout)?\\s*(.*)\\s*$", "$2"),

        /**
         * Speech mode for whispered text.
         */
        @SuppressWarnings("nls")
        Whisper(Color.GRAY, "^\\s*[/#]w(hisper)?\\s*(.*)\\s*$", "$2");

        /**
         * The color of this speech mode.
         */
        private final Color color;

        /**
         * The regular expression used to find out the type of the text.
         */
        @Nullable
        private final Pattern regexp;

        /**
         * The replacement to extract the actual text
         */
        @Nullable
        private final String replacement;

        /**
         * Constructor for the speech mode that stores the color of the mode.
         *
         * @param modeColor  the color of the speech mode
         * @param findRegexp the regular expression used to find out if the line is fits this Chat type or not
         * @param replace    the regular expression needed to isolate the actual text
         */
        SpeechMode(final org.illarion.engine.graphic.Color modeColor, @Nullable @RegEx final String findRegexp,
                   @Nullable final String replace) {
            color = modeColor;
            if (findRegexp == null) {
                regexp = null;
                replacement = null;
            } else {
                regexp = Pattern.compile(findRegexp);
                replacement = replace;
            }
        }

        /**
         * Get the color of the speech mode.
         *
         * @return the color of the speech mode
         */
        public Color getColor() {
            return color;
        }

        /**
         * Get the regular expression pattern to find out if this speech type is
         * the one used in the text.
         *
         * @return the pattern with the regular expression or {@code null}
         *         in case none applies
         */
        @Nullable
        public Pattern getRegexp() {
            return regexp;
        }

        /**
         * Get the replacement needed to extract the actual text from the line.
         *
         * @return the replacement
         */
        @Nullable
        public String getReplacement() {
            return replacement;
        }
    }

    /**
     * The logger that takes care of the logging output of the Chat handler.
     */
    private static final Logger LOGGER = Logger.getLogger(ChatHandler.class);

    /**
     * Handle a message by this processor. This method stores a message in the
     * ChatHandler thread so the handler takes care of the message later on.
     *
     * @param text     the text that was spoken
     * @param location the location where the text was spoken
     */
    public void handleMessage(@Nonnull final String text, @Nonnull final Location location, @Nonnull final SpeechMode receivedMode) {
        final Char talkingChar = World.getPeople().getCharacterAt(location);

        final ChatHandler.SpeechMode mode;
        final String resultText;

        switch (receivedMode) {
            case Whisper:
                @SuppressWarnings("ConstantConditions")
                final Matcher oocMatcher = SpeechMode.Ooc.getRegexp().matcher(text);
                if (oocMatcher.find()) {
                    mode = SpeechMode.Ooc;
                    resultText = oocMatcher.replaceAll(SpeechMode.Ooc.getReplacement()).trim();
                } else {
                    mode = SpeechMode.Whisper;
                    resultText = text.trim();
                }
                break;
            case Shout:
                mode = SpeechMode.Shout;
                resultText = text.trim();
                break;
            default:
                @SuppressWarnings("ConstantConditions")
                final Matcher emoteMatcher = SpeechMode.Emote.getRegexp().matcher(text);
                if (emoteMatcher.find()) {
                    mode = SpeechMode.Emote;
                    resultText = emoteMatcher.replaceAll(SpeechMode.Emote.getReplacement());
                } else {
                    mode = SpeechMode.Normal;
                    resultText = text.trim();
                }
        }

        final StringBuilder textBuilder = new StringBuilder();

        if (mode == ChatHandler.SpeechMode.Emote) {
            // we need some kind of name
            if (talkingChar == null) {
                textBuilder.append(Lang.getMsg("chat.someone"));
            } else {
                textBuilder.append(talkingChar.getName());
            }

            textBuilder.append(resultText);

            final String emoteText = textBuilder.toString();
            World.getPlayer().getChatLog().logText(emoteText);
            World.getGameGui().getChatGui().addChatMessage(emoteText, ChatGui.COLOR_EMOTE);
            World.getGameGui().getChatGui().showChatBubble(talkingChar, emoteText, ChatGui.COLOR_EMOTE);
        } else {
            if (talkingChar == null) {
                textBuilder.append(Lang.getMsg("chat.distantShout"));
            } else {
                textBuilder.append(talkingChar.getName());

                switch (mode) {
                    case Shout:
                        textBuilder.append(' ').append(Lang.getMsg("log.shout"));
                        break;
                    case Whisper:
                        textBuilder.append(' ').append(Lang.getMsg("log.whisper"));
                        break;
                    case Normal:
                    case Ooc:
                        textBuilder.append(' ').append(Lang.getMsg("log.say"));
                        break;
                    case Emote:
                        break;
                }
            }

            textBuilder.append(':').append(' ');

            final String bubbleText;
            if (mode == SpeechMode.Ooc) {
                bubbleText = "((" + resultText + "))";
            } else {
                bubbleText = resultText;
            }
            textBuilder.append(bubbleText);

            final de.lessvoid.nifty.tools.Color color;
            switch (mode) {
                case Shout:
                    color = ChatGui.COLOR_SHOUT;
                    break;
                case Whisper:
                    color = ChatGui.COLOR_WHISPER;
                    break;
                case Normal:
                    color = ChatGui.COLOR_DEFAULT;
                    break;
                case Ooc:
                    color = ChatGui.COLOR_WHISPER;
                    break;
                default:
                    color = ChatGui.COLOR_DEFAULT;
            }

            final String talkText = textBuilder.toString();
            World.getPlayer().getChatLog().logText(talkText);
            World.getGameGui().getChatGui().addChatMessage(talkText, color);
            World.getGameGui().getChatGui().showChatBubble(talkingChar, bubbleText, color);
        }
    }
}

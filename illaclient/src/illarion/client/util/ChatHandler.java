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

import illarion.client.world.Char;
import illarion.client.world.World;
import illarion.client.world.events.CharTalkingEvent;
import illarion.common.types.Location;
import javolution.text.TextBuilder;
import org.apache.log4j.Logger;
import org.bushe.swing.event.EventBus;
import org.newdawn.slick.Color;

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
        emote(Color.yellow, "^\\s*[/#]me(.*)\\s*$", "$1"),

        /**
         * Speech mode for normal spoken text.
         */
        normal(Color.white, null, null),

        /**
         * Speech mode for OOC messages.
         */
        @SuppressWarnings("nls")
        ooc(Color.gray, "^\\s*[/#]o(oc)?\\s*(.*)\\s*$", "$2"),

        /**
         * Speech mode for shouted text.
         */
        @SuppressWarnings("nls")
        shout(Color.red, "^\\s*[/#]s(hout)?\\s*(.*)\\s*$", "$2"),

        /**
         * Speech mode for whispered text.
         */
        @SuppressWarnings("nls")
        whisper(Color.gray, "^\\s*[/#]w(hisper)?\\s*(.*)\\s*$", "$2");

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
        SpeechMode(final Color modeColor, @Nullable @RegEx final String findRegexp,
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
         * @return the pattern with the regular expression or <code>null</code>
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

        ChatHandler.SpeechMode mode = null;
        String resultText = null;

        switch (receivedMode) {
            case whisper:
                @SuppressWarnings("ConstantConditions")
                final Matcher oocMatcher = SpeechMode.ooc.getRegexp().matcher(text);
                if (oocMatcher.find()) {
                    mode = SpeechMode.ooc;
                    resultText = oocMatcher.replaceAll(SpeechMode.ooc.getReplacement()).trim();
                } else {
                    mode = SpeechMode.whisper;
                    resultText = text.trim();
                }
                break;
            case shout:
                mode = SpeechMode.shout;
                resultText = text.trim();
                break;
            default:
                @SuppressWarnings("ConstantConditions")
                final Matcher emoteMatcher = SpeechMode.emote.getRegexp().matcher(text);
                if (emoteMatcher.find()) {
                    mode = SpeechMode.emote;
                    resultText = emoteMatcher.replaceAll(SpeechMode.emote.getReplacement());
                } else {
                    mode = SpeechMode.normal;
                    resultText = text.trim();
                }
        }

        final TextBuilder textBuilder = TextBuilder.newInstance();
        try {
            if (mode == ChatHandler.SpeechMode.emote) {
                // we need some kind of name
                if (talkingChar == null) {
                    textBuilder.append(Lang.getMsg("chat.someone"));
                } else {
                    textBuilder.append(talkingChar.getName());
                }

                textBuilder.append(resultText);
            } else {
                if (talkingChar == null) {
                    textBuilder.append(Lang.getMsg("chat.distantShout"));
                } else {
                    textBuilder.append(talkingChar.getName());

                    switch (mode) {
                        case shout:
                            textBuilder.append(' ').append(Lang.getMsg("log.shout"));
                            break;
                        case whisper:
                            textBuilder.append(' ').append(Lang.getMsg("log.whisper"));
                            break;
                        case normal:
                        case ooc:
                            textBuilder.append(' ').append(Lang.getMsg("log.say"));
                            break;
                        case emote:
                            break;
                    }
                }

                textBuilder.append(':').append(' ');
                if (mode == SpeechMode.ooc) {
                    textBuilder.append("((");
                }
                textBuilder.append(resultText);
                if (mode == SpeechMode.ooc) {
                    textBuilder.append("))");
                }
            }

            EventBus.publish(new CharTalkingEvent(mode, talkingChar, location, resultText, textBuilder.toString()));
        } finally {
            TextBuilder.recycle(textBuilder);
        }
    }
}

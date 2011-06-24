/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javolution.text.TextBuilder;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import illarion.client.IllaClient;
import illarion.client.world.Char;
import illarion.client.world.Game;

import illarion.common.config.Config;
import illarion.common.config.ConfigChangeListener;

/**
 * Class to handle the logging of the chat in the game to the logfile.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class ChatLog implements ConfigChangeListener {

    /**
     * The key used for the configuration to store if the text log is enabled or
     * not.
     */
    public static final String CFG_TEXTLOG = "textLog"; //$NON-NLS-1$

    /**
     * Singleton instance of the ChatLog class.
     */
    private static final ChatLog INSTANCE = new ChatLog();

    /**
     * The key for a chat entry in case you hear someone saying something but
     * not the source of the voice.
     */
    @SuppressWarnings("nls")
    private final String KEY_DISTANCE = "chat.distantShout";

    /**
     * The key for normal speech.
     */
    @SuppressWarnings("nls")
    private final String KEY_SAY = "log.say";

    /**
     * The key for shouting.
     */
    @SuppressWarnings("nls")
    private final String KEY_SHOUT = "log.shout";

    /**
     * The key for the language file for the generic "someone" name in the chat.
     */
    @SuppressWarnings("nls")
    private final String KEY_SOMEONE = "chat.someone";

    /**
     * The key for whispering.
     */
    @SuppressWarnings("nls")
    private final String KEY_WHISPER = "log.whisper";

    /**
     * Constant value to determine if the logger is active in general or not. In
     * case the logging is disabled by the config, nothing will be logged.
     */
    private boolean logActive;

    /**
     * Instance of the used logger.
     */
    @SuppressWarnings("nls")
    private final Logger logger = Logger.getLogger("CHAT");

    /**
     * Stores the informations if the logger is set up and working. Only in this
     * case logfiles are written.
     */
    private boolean loggerWorking = false;

    /**
     * Private constructor to avoid that any instance but the singleton instance
     * is created.
     */
    private ChatLog() {
        logActive = IllaClient.getCfg().getBoolean(CFG_TEXTLOG);
        IllaClient.getCfg().addListener(CFG_TEXTLOG, this);
    }

    /**
     * Get the singleton instance of the chatfile logger.
     * 
     * @return the singleton instance of this class
     */
    public static ChatLog getInstance() {
        return INSTANCE;
    }

    /**
     * This method is used to monitor changes of the configuration. In case a
     * change happens the configuration will set the internal values properly
     * again.
     */
    @Override
    public void configChanged(final Config cfg, final String key) {
        if (key.equals(CFG_TEXTLOG)) {
            logActive = cfg.getBoolean(CFG_TEXTLOG);
        }
    }

    /**
     * Set up the logger and all needed settings so everything is fine and
     * reading for the logging actions.
     * 
     * @param loggingProps the properties that are used to setup the loggers.
     *            These need to be modified in order to set the correct paths to
     *            the logfiles
     */
    @SuppressWarnings("nls")
    public void init(final Properties loggingProps) {
        loggingProps.put("log4j.appender.ChatAppender.file", new File(Game
            .getPlayer().getPath(), "illarion.log").getAbsolutePath());
        new PropertyConfigurator().doConfigure(loggingProps,
            logger.getLoggerRepository());

        loggerWorking = true;

        // add a entry of the staring logging session to the logfile.
        final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        logger.info("");
        logger.info(Lang.getMsg("log.newSession") + " - "
            + sdf.format(new Date()));
    }

    /**
     * Write a text that was spoken to the the logfile.
     * 
     * @param chara the character who spoke the text, null if its unknown what
     *            character said the text
     * @param talkMode the mode the message was talked in (say, whisper, shout)
     * @param text the text that was spoken itself
     */
    @SuppressWarnings("nls")
    public void logMessage(final Char chara,
        final ChatHandler.SpeechMode talkMode, final String text) {

        if (!loggerWorking || !logActive) {
            return;
        }

        final TextBuilder textBuilder = TextBuilder.newInstance();

        // get player's name
        String name = null;
        if (chara != null) {
            name = chara.getName();
        }

        if (talkMode == ChatHandler.SpeechMode.emote) {
            // we need some kind of name
            if (name == null) {
                name = Lang.getMsg(KEY_SOMEONE);
            }

            textBuilder.append(name);
            textBuilder.append(text);
        } else {
            // normal text hears a shout from the distance
            if ((name == null) && (chara == null)) {
                name = Lang.getMsg(KEY_DISTANCE);
            } else if ((name == null) && (chara != null)) {
                name =
                    Lang.getMsg(KEY_SOMEONE) + " ("
                        + Long.toString(chara.getCharId()) + ")";
            }

            textBuilder.append(name);
            if (chara != null) {
                if (talkMode == ChatHandler.SpeechMode.shout) {
                    textBuilder.append(' ').append(Lang.getMsg(KEY_SHOUT));
                } else if (talkMode == ChatHandler.SpeechMode.whisper) {
                    textBuilder.append(' ').append(Lang.getMsg(KEY_WHISPER));
                } else {
                    textBuilder.append(' ').append(Lang.getMsg(KEY_SAY));
                }
            }

            textBuilder.append(':').append(' ');
            textBuilder.append(text);
        }

        // send out the text
        logger.info(textBuilder.toString());
        TextBuilder.recycle(textBuilder);
    }
}

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

import illarion.client.IllaClient;
import illarion.client.world.World;
import illarion.client.world.events.CharTalkingEvent;
import illarion.common.config.ConfigChangedEvent;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.bushe.swing.event.annotation.EventTopicSubscriber;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Class to handle the logging of the chat in the game to the logfile.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ChatLog {
    /**
     * The key used for the configuration to store if the text log is enabled or not.
     */
    public static final String CFG_TEXTLOG = "textLog"; //$NON-NLS-1$

    /**
     * Singleton instance of the ChatLog class.
     */
    private static final ChatLog INSTANCE = new ChatLog();

    /**
     * Constant value to determine if the logger is active in general or not. In case the logging is disabled by the
     * config, nothing will be logged.
     */
    private boolean logActive;

    /**
     * Instance of the used logger.
     */
    @SuppressWarnings("nls")
    private final Logger logger = Logger.getLogger("CHAT");

    /**
     * Stores the information if the logger is set up and working. Only in this case log files are written.
     */
    private boolean loggerWorking;

    /**
     * Private constructor to avoid that any instance but the singleton instance is created.
     */
    private ChatLog() {
        logActive = IllaClient.getCfg().getBoolean(CFG_TEXTLOG);
        AnnotationProcessor.process(this);
    }

    /**
     * Get the singleton instance of the chat file logger.
     *
     * @return the singleton instance of this class
     */
    public static ChatLog getInstance() {
        return INSTANCE;
    }

    /**
     * Set up the logger and all needed settings so everything is fine and
     * reading for the logging actions.
     *
     * @param loggingProps the properties that are used to setup the loggers. These need to be modified in order to
     *                     set the correct paths to the log files
     */
    @SuppressWarnings("nls")
    public void init(final Properties loggingProps) {
        loggingProps.put("log4j.appender.ChatAppender.file", new File(World.getPlayer().getPath(),
                "illarion.log").getAbsolutePath());
        new PropertyConfigurator().doConfigure(loggingProps,
                logger.getLoggerRepository());

        loggerWorking = true;

        // add a entry of the staring logging session to the logfile.
        final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        logger.info("");
        logger.info(Lang.getMsg("log.newSession") + " - " + sdf.format(new Date()));
    }

    @EventTopicSubscriber(topic = CFG_TEXTLOG)
    public void onConfigChangedEvent(final String topic, final ConfigChangedEvent data) {
        if (topic.equals(CFG_TEXTLOG)) {
            logActive = data.getConfig().getBoolean(topic);
        }
    }

    @EventSubscriber
    public void onCharTalkingEvent(final CharTalkingEvent event) {
        if (loggerWorking && logActive) {
            logger.info(event.getLoggedText());
        }
    }
}

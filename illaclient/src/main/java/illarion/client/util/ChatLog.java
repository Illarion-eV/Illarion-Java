/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
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
package illarion.client.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import illarion.client.IllaClient;
import illarion.common.config.ConfigChangedEvent;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventTopicSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Class to handle the logging of the Chat in the game to the logfile.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ChatLog {
    /**
     * The key used for the configuration to store if the text log is enabled or not.
     */
    public static final String CFG_TEXTLOG = "textLog"; //$NON-NLS-1$
    /**
     * Instance of the used logger.
     */
    @Nonnull
    private static final Logger logger = LoggerFactory.getLogger("CHAT");
    /**
     * Stores the information if the logger is set up and working. Only in this case log files are written.
     */
    private final boolean loggerWorking;
    /**
     * Constant value to determine if the logger is active in general or not. In case the logging is disabled by the
     * config, nothing will be logged.
     */
    private boolean logActive;

    /**
     * Private constructor to avoid that any instance but the singleton instance is created.
     *
     * @param playerPath the path this chat log is supposed to be stored at
     */
    public ChatLog(@Nonnull Path playerPath) {
        logActive = IllaClient.getCfg().getBoolean(CFG_TEXTLOG);

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        appender.setContext(loggerContext);
        appender.setFile(playerPath.resolve("illarion.log").toAbsolutePath().toString());

        TimeBasedRollingPolicy<ILoggingEvent> policy = new TimeBasedRollingPolicy<>();
        policy.setFileNamePattern(playerPath.resolve("illarion-%d{yyyy-MM}.log").toAbsolutePath().toString());
        policy.setParent(appender);
        policy.setContext(loggerContext);
        policy.start();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern("%msg%n");
        encoder.start();

        appender.setTriggeringPolicy(policy);
        appender.setRollingPolicy(policy);
        appender.setEncoder(encoder);
        appender.start();

        loggerContext.getLogger("CHAT").setAdditive(false);
        loggerContext.getLogger("CHAT").detachAndStopAllAppenders();
        loggerContext.getLogger("CHAT").addAppender(appender);
        loggerContext.getLogger("CHAT").setLevel(Level.ALL);

        loggerWorking = true;

        // add a entry of the staring logging session to the logfile.
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

        logger.info("");
        logger.info("{} - {}", Lang.getMsg("log.newSession"), sdf.format(new Date()));

        AnnotationProcessor.process(this);
    }

    /**
     * Write some text to the chat log in case its enabled.
     *
     * @param text the text to log
     */
    public void logText(@Nonnull String text) {
        if (loggerWorking && logActive) {
            logger.info(text);
        }
    }

    @EventTopicSubscriber(topic = CFG_TEXTLOG)
    public void onConfigChangedEvent(@Nonnull String topic, @Nonnull ConfigChangedEvent data) {
        if (topic.equals(CFG_TEXTLOG)) {
            logActive = data.getConfig().getBoolean(topic);
        }
    }
}
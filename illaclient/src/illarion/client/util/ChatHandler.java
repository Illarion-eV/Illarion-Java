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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javolution.util.FastList;

import org.apache.log4j.Logger;

import illarion.client.crash.ChatCrashHandler;
import illarion.client.graphics.Colors;
import illarion.client.guiNG.GUI;
import illarion.client.world.Char;
import illarion.client.world.Game;

import illarion.common.util.Location;

/**
 * This chat handler fetches all texts send by the network interface and
 * forwards the data to the required parts of the client. It takes care for
 * transforming the text properly for each part of the client.
 * 
 * @author Martin Karing
 * @version 1.00
 * @since 1.22
 */
public final class ChatHandler implements Runnable, SessionMember {
    /**
     * The possible speech modes that are displays on the screen.
     * 
     * @author Martin Karing
     * @since 1.22
     */
    public static enum SpeechMode {
        /**
         * Speech mode for emotes.
         */
        @SuppressWarnings("nls")
        emote(Colors.yellow, "^\\s*[!#]me(.*)\\s*$", "$1"),

        /**
         * Speech mode for normal spoken text.
         */
        normal(Colors.white, null, null),

        /**
         * Speech mode for OOC messages.
         */
        @SuppressWarnings("nls")
        ooc(Colors.gray, "^\\s*[!#]o(oc)?\\s*(.*)\\s*$", "$2"),

        /**
         * Speech mode for shouted text.
         */
        @SuppressWarnings("nls")
        shout(Colors.red, "^\\s*[!#]s(hout)?\\s*(.*)\\s*$", "$2"),

        /**
         * Speech mode for whispered text.
         */
        @SuppressWarnings("nls")
        whisper(Colors.gray, "^\\s*[!#]w(hisper)?\\s*(.*)\\s*$", "$2");

        /**
         * The color of this speech mode.
         */
        private final Colors color;

        /**
         * The regular expression used to find out the type of the text.
         */
        private final Pattern regexp;

        /**
         * The replacement to extract the actual text
         */
        private final String replacement;

        /**
         * Constructor for the speech mode that stores the color of the mode.
         * 
         * @param modeColor the color of the speech mode
         * @param findRegexp the regular expression used to find out if the line
         *            is fits this chat type or not
         * @param replace the regular expression needed to isolate the actual
         *            text
         */
        private SpeechMode(final Colors modeColor, final String findRegexp,
            final String replace) {
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
        public Colors getColor() {
            return color;
        }

        /**
         * Get the regular expression pattern to find out if this speech type is
         * the one used in the text.
         * 
         * @return the pattern with the regular expression or <code>null</code>
         *         in case none applies
         */
        public Pattern getRegexp() {
            return regexp;
        }

        /**
         * Get the replacement needed to extract the actual text from the line.
         * 
         * @return the replacement
         */
        public String getReplacement() {
            return replacement;
        }
    }

    /**
     * This nodes are used to temporary store the chat entries that were
     * received.
     * 
     * @author Martin Karing
     * @version 1.00
     * @since 1.22
     */
    private static final class TextNode {
        /**
         * The message stored in this node.
         */
        private String message;

        /**
         * The position where the text that is stored in this node was spoken.
         */
        private Location pos;

        /**
         * Constructor of the text node to ensure that the parent class can use
         * this TextNodes properly.
         */
        public TextNode() {
            super();
        }

        /**
         * Cleanup this instance of the TextNode. This results in removing the
         * message and the location references stored in this class.
         */
        public void clean() {
            message = null;
            pos = null;
        }

        /**
         * Get the location where the message that was spoken was said.
         * 
         * @return the location where the message was spoken
         */
        public Location getLocation() {
            return pos;
        }

        /**
         * Get the message stored in this node.
         * 
         * @return The message stored in this node
         */
        public String getMessage() {
            return message;
        }

        /**
         * Set the location where the message was spoken.
         * 
         * @param position the location where the message was spoken
         */
        public void setLocation(final Location position) {
            pos = position;
        }

        /**
         * Set the message that was spoken and shall be stored in this node.
         * 
         * @param msg the message
         */
        public void setMessage(final String msg) {
            message = msg;
        }
    }

    /**
     * The singleton instance of this class.
     */
    private static final ChatHandler INSTANCE = new ChatHandler();

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(ChatHandler.class);

    /**
     * The list of TextNodes that are created but currently not in use.
     */
    private final FastList<TextNode> buffer;

    /**
     * The thread of the chat handler.
     */
    private Thread chatThread;

    /**
     * The list of text messages that were send but yet not processed.
     */
    private final FastList<TextNode> dirtyList;

    /**
     * Running flag, if set to false, the thread is stopped.
     */
    private boolean running;

    /**
     * Private constructor that prepares that class to work properly and ensures
     * that only the singleton instance is active and running.
     */
    private ChatHandler() {
        buffer = new FastList<TextNode>();
        dirtyList = new FastList<TextNode>();
        running = false;
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of this class
     */
    public static ChatHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Restart the chat handler by removing the active instance and creating a
     * new one. This also causes that the Chat Handler is started right away.
     */
    @SuppressWarnings("nls")
    public static void restart() {
        synchronized (ChatHandler.class) {
            INSTANCE.stop();
            INSTANCE.reset();
            INSTANCE.start();
        }
    }

    @Override
    public void endSession() {
        saveShutdown();
    }

    /**
     * Handle a message by this processor. This method stores a message in the
     * ChatHandler thread so the handler takes care of the message later on.
     * 
     * @param text the text that was spoken
     * @param location the location where the text was spoken
     */
    public void handleMessage(final String text, final Location location) {
        TextNode node = null;
        synchronized (buffer) {
            if (!buffer.isEmpty()) {
                node = buffer.removeFirst();
            }
        }
        if (node == null) {
            node = new TextNode();
        }

        node.setMessage(text);
        node.setLocation(location);

        synchronized (dirtyList) {
            dirtyList.addLast(node);
            dirtyList.notify();
        }
    }

    @Override
    public void initSession() {
        // nothing to do
    }

    @SuppressWarnings("nls")
    @Override
    public void run() {
        while (running) {
            TextNode node = null;

            synchronized (dirtyList) {
                if (dirtyList.isEmpty() || (Game.getPlayer() == null)
                    || (Game.getPeople() == null)
                    || (ChatLog.getInstance() == null)) {
                    try {
                        dirtyList.wait();
                    } catch (final InterruptedException e) {
                        // waiting was interrupted, but that does not matter.
                    }
                    continue;
                }
                node = dirtyList.removeFirst();
            }

            SpeechMode mode = null;
            String resultText = null;
            for (final SpeechMode testMode : SpeechMode.values()) {
                if (testMode.getRegexp() == null) {
                    if (mode == null) {
                        mode = testMode;
                        resultText = node.getMessage();
                    }
                    continue;
                }

                final Matcher testMatcher =
                    testMode.getRegexp().matcher(node.getMessage());
                if (testMatcher.find()) {
                    mode = testMode;
                    resultText =
                        testMatcher.replaceAll(testMode.getReplacement());
                    break;
                }
            }

            if (resultText == null) {
                LOGGER.error("Extracting the text message failed.");
                return;
            }

            Char chara;
            if (Game.getPlayer().getLocation().equals(node.getLocation())) {
                chara = Game.getPlayer().getCharacter();
            } else {
                chara = Game.getPeople().getCharacterAt(node.getLocation());
            }

            ChatLog.getInstance().logMessage(chara, mode, resultText);
            GUI.getInstance().getChatText()
                .showText(resultText, chara, node.getLocation(), mode);
            GUI.getInstance().getJournal().addText(resultText, chara, mode);

            node.clean();
            synchronized (buffer) {
                buffer.addLast(node);
            }
            node = null;
        }
    }

    /**
     * Stop the thread the next time its safely possible.
     */
    public void saveShutdown() {
        running = false;

        synchronized (dirtyList) {
            dirtyList.notify();
        }
    }

    @Override
    public void shutdownSession() {
        stop();
    }

    @Override
    public void startSession() {
        start();
    }

    /**
     * Calling this function causes the entire class to reset. This removed all
     * previously stored data.
     */
    private void reset() {
        running = false;
        chatThread = null;
        synchronized (dirtyList) {
            buffer.clear();
            dirtyList.clear();
        }
    }

    /**
     * Start the thread and ensure that the ChatHandler keeps working. This also
     * adds this thread to the StoppableStorage in order to shut it down
     * properly once the client is shutting down.
     */
    private void start() {
        synchronized (ChatHandler.class) {
            if (!running) {
                running = true;
                if (chatThread == null) {
                    chatThread = new Thread(this, "Chat Handler");
                    chatThread.setUncaughtExceptionHandler(ChatCrashHandler
                        .getInstance());
                    chatThread.start();
                } else {
                    restart();
                }
            }
        }
    }

    /**
     * Calling this function causes the chat handler to stop and removes thread.
     * Even in case the thread is not correctly working anymore this function
     * will most likely force it to shutdown after a while.
     */
    private void stop() {
        if (chatThread != null) {
            running = false;

            synchronized (dirtyList) {
                dirtyList.notify();
            }

            int i = 100;
            while (--i > 0) {
                if (!chatThread.isAlive()) {
                    break;
                }
                try {
                    Thread.sleep(10);
                } catch (final InterruptedException e) {
                    // nothing to do
                }
            }

            if (!chatThread.isAlive()) {
                chatThread.interrupt();
            }

            i = 100;
            while (--i > 0) {
                if (!chatThread.isAlive()) {
                    break;
                }
                try {
                    Thread.sleep(10);
                } catch (final InterruptedException e) {
                    // nothing to do
                }
            }

            if (!chatThread.isAlive()) {
                LOGGER.error("Failed to stop Chat handler thread!");
            }
        }
    }
}

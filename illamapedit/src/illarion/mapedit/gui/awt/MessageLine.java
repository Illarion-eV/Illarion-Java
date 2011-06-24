/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute i and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Mapeditor is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Mapeditor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.gui.awt;

import java.awt.Font;
import java.awt.Label;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import illarion.common.util.Scheduler;

/**
 * This class takes care for showing a label that displays some messages.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class MessageLine extends Label {
    /**
     * The serialization UID of this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The text showing that the RAM value is messured in MegaByte.
     */
    @SuppressWarnings("nls")
    private static final String TEXT_MB = "MB";

    /**
     * The intro text that is displayed in front of the ram usage informations.
     */
    @SuppressWarnings("nls")
    private static final String TEXT_RAM = "Ram usage: ";

    /**
     * The string that is displayed in case there are no messages to be
     * displayed.
     */
    @SuppressWarnings("nls")
    private static final String TEXT_READY = "Ready";

    /**
     * The seperator text that is displayed between the individual messages.
     */
    @SuppressWarnings("nls")
    private static final String TEXT_SEP = " - ";

    /**
     * The string buffer used to build the text that is shown on the label.
     */
    private final StringBuffer buildBuffer;

    /**
     * The list of messages that are currently displayed in this message line.
     */
    private final List<String> messages;

    /**
     * Constructor for a message line. This prepares the label and ensures its
     * ready to use. Also it will schedule a task for regular updates of the
     * labels contents.
     */
    @SuppressWarnings("nls")
    public MessageLine() {
        super();

        setFont(Font.decode(Font.SANS_SERIF + "-BOLD-13"));
        messages = new ArrayList<String>();
        buildBuffer = new StringBuffer();
        Scheduler.getInstance().schedule(new TimerTask() {
            @Override
            public void run() {
                rebuildText();
            }
        }, 0, 1000);
    }

    /**
     * Add a message to the message line.
     * 
     * @param message the message to add
     */
    public void addMessage(final String message) {
        messages.add(message);
        rebuildText();
    }

    /**
     * Remove a message from the message line.
     * 
     * @param message the message to remove from the message line, must equal a
     *            formerly added message
     */
    public void removeMessage(final String message) {
        messages.remove(message);
        rebuildText();
    }

    /**
     * Rebuild the text content of the label.
     */
    synchronized void rebuildText() {
        buildBuffer.setLength(0);
        for (int i = 0, n = messages.size(); i < n; i++) {
            buildBuffer.append(messages.get(i));
            buildBuffer.append(TEXT_SEP);
        }
        if (buildBuffer.length() == 0) {
            buildBuffer.append(TEXT_READY);
            buildBuffer.append(TEXT_SEP);
        }
        buildBuffer.append(TEXT_RAM);
        buildBuffer.append(
            Long.toString((Runtime.getRuntime().totalMemory() - Runtime
                .getRuntime().freeMemory()) / 1024 / 1024)).append(TEXT_MB);
        setText(buildBuffer.toString());
    }
}

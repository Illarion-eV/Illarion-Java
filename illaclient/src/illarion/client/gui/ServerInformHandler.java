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
package illarion.client.gui;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.net.server.events.ServerInformReceivedEvent;
import org.apache.log4j.Logger;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The task of this handler is to accept and display server informs.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public final class ServerInformHandler implements EventSubscriber<ServerInformReceivedEvent>, ScreenController {
    /**
     * The logger that is used for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(ServerInformHandler.class);

    /**
     * This is a reference to the panel that is supposed to be used as container of the server inform messages.
     */
    private Element parentPanel;

    /**
     * The reference to the thread that updates the Nifty-GUI.
     */
    private Thread niftyUpdateThread;

    /**
     * The instance of the Nifty-GUI this handler is bound to.
     */
    private Nifty parentNifty;

    /**
     * The instance of the screen this handler is operating on.
     */
    private Screen parentScreen;

    /**
     * This queue stores the builder of server inform labels until they are executed.
     */
    private final Queue<LabelBuilder> builderQueue;

    public ServerInformHandler() {
        builderQueue = new ConcurrentLinkedQueue<LabelBuilder>();
    }

    @Override
    public void onEvent(final ServerInformReceivedEvent event) {
        if (parentPanel == null) {
            LOGGER.warn("Received server inform before the GUI became ready.");
            return;
        }

        final LabelBuilder labelBuilder = new LabelBuilder();
        labelBuilder.label("> " + event.getMessage());
        labelBuilder.font("console");
        builderQueue.offer(labelBuilder);
    }

    @Override
    public void bind(final Nifty nifty, final Screen screen) {
        parentNifty = nifty;
        parentScreen = screen;
        parentPanel = screen.findElementByName("serverMsgPanel");
        niftyUpdateThread = Thread.currentThread();
    }

    @Override
    public void onStartScreen() {
        EventBus.subscribe(ServerInformReceivedEvent.class, this);
    }

    @Override
    public void onEndScreen() {
        EventBus.unsubscribe(ServerInformReceivedEvent.class, this);
    }
}

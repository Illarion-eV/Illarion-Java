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

import de.lessvoid.nifty.EndNotify;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.ElementBuilder;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.net.server.events.BroadcastInformReceivedEvent;
import illarion.client.util.GameLoopUpdateEvent;
import org.apache.log4j.Logger;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The task of this handler is to accept and display broadcast informs.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public final class BroadcastInformHandler implements EventSubscriber<BroadcastInformReceivedEvent>, ScreenController {
    /**
     * This utility class is a end notification that will trigger the removal of a target element. This is needed to
     * remove the server messages again from the screen.
     */
    private static final class RemoveEndNotify implements EndNotify {
        /**
         * The target element.
         */
        private final Element target;

        /**
         * The constructor of this class.
         *
         * @param element the element to remove
         */
        RemoveEndNotify(final Element element) {
            target = element;
        }

        @Override
        public void perform() {
            target.markForRemoval(new BroadcastInformHandler.LayoutElementsEndNotify(target.getParent()));
        }
    }

    /**
     * This utility class is a end notification that will trigger the calculation of the layout of a target element.
     * This is needed to put the parent container of the server informs back into shape.
     */
    private static final class LayoutElementsEndNotify implements EndNotify {
        /**
         * The target element.
         */
        private final Element target;

        /**
         * The constructor of this class.
         *
         * @param element the element to layout
         */
        LayoutElementsEndNotify(final Element element) {
            target = element;
        }

        @Override
        public void perform() {
            target.layoutElements();
            if (target.getElements().isEmpty()) {
                target.hide();
            }
        }
    }

    /**
     * This handler is used to monitor update events of the game loop and trigger the creation of all message entries
     * that were yet not created.
     */
    private final class GameLoopUpdateEventSubscriber implements EventSubscriber<GameLoopUpdateEvent> {
        @Override
        public void onEvent(final GameLoopUpdateEvent event) {
            while (true) {
                final ElementBuilder builder = builderQueue.poll();
                if (builder == null) {
                    return;
                }

                if (!parentPanel.isVisible()) {
                    parentPanel.show();
                }

                final Element msg = builder.build(parentNifty, parentScreen, parentPanel);
                msg.showWithoutEffects();
                msg.hide(new BroadcastInformHandler.RemoveEndNotify(msg));

                parentPanel.layoutElements();
            }
        }
    }

    /**
     * The logger that is used for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(BroadcastInformHandler.class);

    /**
     * This is a reference to the panel that is supposed to be used as container of the server inform messages.
     */
    private Element parentPanel;

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
    private final Queue<ElementBuilder> builderQueue;

    /**
     * This is the update event handler that takes care for building the labels during the update call of the game
     * loop. This needs to be done in order to stay in synchronization with the Nifty-GUI.
     */
    private final EventSubscriber<GameLoopUpdateEvent> updateEventSubscriber;

    /**
     * Default constructor that prepares the structures needed for this handler to work properly.
     */
    public BroadcastInformHandler() {
        builderQueue = new ConcurrentLinkedQueue<ElementBuilder>();

        updateEventSubscriber = new BroadcastInformHandler.GameLoopUpdateEventSubscriber();
    }

    @Override
    public void bind(final Nifty nifty, final Screen screen) {
        parentNifty = nifty;
        parentScreen = screen;
        parentPanel = screen.findElementByName("broadcastMsgPanel");
    }

    @Override
    public void onEndScreen() {
        EventBus.unsubscribe(BroadcastInformReceivedEvent.class, this);
        EventBus.unsubscribe(GameLoopUpdateEvent.class, updateEventSubscriber);
    }

    @Override
    public void onEvent(final BroadcastInformReceivedEvent event) {
        if (parentPanel == null) {
            LOGGER.warn("Received server inform before the GUI became ready.");
            return;
        }

        final LabelBuilder labelBuilder = new LabelBuilder();
        labelBuilder.label(event.getMessage());
        labelBuilder.font("menuFont");
        labelBuilder.visible(false);
        labelBuilder.invisibleToMouse();

        final EffectBuilder effectBuilder = new EffectBuilder("hide");
        effectBuilder.startDelay(10000 + (event.getMessage().length() * 50));
        labelBuilder.onHideEffect(effectBuilder);

        builderQueue.offer(labelBuilder);
    }

    @Override
    public void onStartScreen() {
        EventBus.subscribe(GameLoopUpdateEvent.class, updateEventSubscriber);
        EventBus.subscribe(BroadcastInformReceivedEvent.class, this);
    }
}

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
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import illarion.client.net.server.events.BroadcastInformReceivedEvent;
import illarion.client.net.server.events.ScriptInformReceivedEvent;
import illarion.client.net.server.events.ServerInformReceivedEvent;
import illarion.client.net.server.events.TextToInformReceivedEvent;
import org.apache.log4j.Logger;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This handler is used to show and hide all the temporary inform messages on the screen. It provides the required
 * facilities to create and remove the elements.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public final class InformHandler implements ScreenController, UpdatableHandler {
    /**
     * This task is created as storage for the creation on the information display.
     */
    private static final class InformBuildTask {
        /**
         * The element builder that is executed to create the inform message.
         */
        private ElementBuilder builder;

        /**
         * The element that will be parent to the elements created by the builder.
         */
        private Element parent;

        /**
         * Create a new instance of this build task that stores all the information needed to create the inform
         * message.
         *
         * @param informBuilder the element builder that creates the message
         * @param parentElement the parent element that will store the elements created by the element builder
         */
        InformBuildTask(final ElementBuilder informBuilder, final Element parentElement) {
            builder = informBuilder;
            parent = parentElement;
        }

        /**
         * Get the element builder that will create the information line.
         *
         * @return the element builder of the inform
         */
        public ElementBuilder getBuilder() {
            return builder;
        }

        /**
         * Get the parent element. Inside this element the builder will be executed.
         *
         * @return the parent element
         */
        public Element getParent() {
            return parent;
        }
    }

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
            target.markForRemoval(new InformHandler.LayoutElementsEndNotify(target.getParent()));
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
     * The logger that is used for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(InformHandler.class);

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
    private final Queue<InformHandler.InformBuildTask> builderQueue;

    /**
     * This is the panel that will be parent to all broadcast messages.
     */
    private Element broadcastParentPanel;

    /**
     * This is the panel that will be parent to all server messages.
     */
    private Element serverParentPanel;

    /**
     * This is the panel that will be parent to all text to messages.
     */
    private Element textToParentPanel;

    /**
     * This is the panel that will be parent to all text-to messages.
     */
    private Element scriptParentPanel;

    /**
     * Default constructor.
     */
    public InformHandler() {
        builderQueue = new ConcurrentLinkedQueue<InformHandler.InformBuildTask>();
    }

    @Override
    public void bind(final Nifty nifty, final Screen screen) {
        parentNifty = nifty;
        parentScreen = screen;

        broadcastParentPanel = screen.findElementByName("broadcastMsgPanel");
        serverParentPanel = screen.findElementByName("serverMsgPanel");
        textToParentPanel = screen.findElementByName("textToMsgPanel");
        scriptParentPanel = screen.findElementByName("scriptMessagePanel");
    }

    @Override
    public void onEndScreen() {
        AnnotationProcessor.unprocess(this);
    }

    @Override
    public void onStartScreen() {
        AnnotationProcessor.process(this);
    }

    @Override
    public void update(final int delta) {
        while (true) {
            final InformHandler.InformBuildTask task = builderQueue.poll();
            if (task == null) {
                return;
            }

            final Element parentPanel = task.getParent();

            if (!parentPanel.isVisible()) {
                parentPanel.show();
            }

            final Element msg = task.getBuilder().build(parentNifty, parentScreen, parentPanel);
            msg.showWithoutEffects();
            msg.hide(new InformHandler.RemoveEndNotify(msg));
        }
    }

    @EventSubscriber
    public void onBroadcastInformReceivedEvent(final BroadcastInformReceivedEvent event) {
        if (broadcastParentPanel == null) {
            LOGGER.warn("Received server inform before the GUI became ready.");
            return;
        }

        final LabelBuilder labelBuilder = new LabelBuilder();
        labelBuilder.label(event.getMessage());
        labelBuilder.font("menuFont");
        labelBuilder.invisibleToMouse();

        final EffectBuilder effectBuilder = new EffectBuilder("hide");
        effectBuilder.startDelay(10000 + (event.getMessage().length() * 50));
        labelBuilder.onHideEffect(effectBuilder);

        showInform(labelBuilder, broadcastParentPanel);
    }

    @EventSubscriber
    public void onServerInformReceivedEvent(final ServerInformReceivedEvent event) {
        if (serverParentPanel == null) {
            LOGGER.warn("Received server inform before the GUI became ready.");
            return;
        }

        final PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.childLayoutHorizontal();

        final LabelBuilder labelBuilder = new LabelBuilder();
        panelBuilder.control(labelBuilder);
        labelBuilder.label("Server> " + event.getMessage());
        labelBuilder.font("consoleFont");
        labelBuilder.invisibleToMouse();

        final EffectBuilder effectBuilder = new EffectBuilder("hide");
        effectBuilder.startDelay(10000 + (event.getMessage().length() * 50));
        panelBuilder.onHideEffect(effectBuilder);

        showInform(panelBuilder, serverParentPanel);
    }

    @EventSubscriber
    public void onTextToInformReceivedEvent(final TextToInformReceivedEvent event) {
        if (textToParentPanel == null) {
            LOGGER.warn("Received server inform before the GUI became ready.");
            return;
        }

        final LabelBuilder labelBuilder = new LabelBuilder();
        labelBuilder.label(event.getMessage());
        labelBuilder.font("textFont");
        labelBuilder.invisibleToMouse();

        final EffectBuilder effectBuilder = new EffectBuilder("hide");
        effectBuilder.startDelay(10000 + (event.getMessage().length() * 50));
        labelBuilder.onHideEffect(effectBuilder);

        showInform(labelBuilder, textToParentPanel);
    }

    @EventSubscriber
    public void onScriptInformReceivedEvent(final ScriptInformReceivedEvent event) {
        if (scriptParentPanel == null) {
            LOGGER.warn("Received script inform before the GUI became ready.");
            return;
        }

        final PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.childLayoutHorizontal();

        final LabelBuilder labelBuilder = new LabelBuilder();
        panelBuilder.control(labelBuilder);
        labelBuilder.label(event.getMessage());
        labelBuilder.font("textFont");
        switch (event.getInformPriority()) {
            case 1:
                labelBuilder.color(Color.WHITE);
                break;
            case 2:
                labelBuilder.color(new Color(1.0f, 0.5f, 0.5f, 1.0f));
                break;
            default:
                labelBuilder.color(new Color(0.9f, 0.9f, 0.9f, 1.0f));
        }
        labelBuilder.invisibleToMouse();
        labelBuilder.valignCenter();
        labelBuilder.alignCenter();
        labelBuilder.width(labelBuilder.percentage(100));
        labelBuilder.textHAlign(ElementBuilder.Align.Center);
        labelBuilder.parameter("wrap", "true");

        final EffectBuilder moveEffectBuilder = new EffectBuilder("move");
        moveEffectBuilder.length(getScriptInformDisplayTime(event.getMessage(), event.getInformPriority()));
        moveEffectBuilder.startDelay(0);
        moveEffectBuilder.effectParameter("mode", "toOffset");
        moveEffectBuilder.effectParameter("direction", "bottom");
        moveEffectBuilder.effectParameter("offsetY", "-80");
        labelBuilder.onHideEffect(moveEffectBuilder);

        final EffectBuilder fadeOutBuilder = new EffectBuilder("fade");
        fadeOutBuilder.length(getScriptInformDisplayTime(event.getMessage(), event.getInformPriority()));
        fadeOutBuilder.startDelay(0);
        fadeOutBuilder.effectParameter("start", "FF");
        fadeOutBuilder.effectParameter("end", "00");
        labelBuilder.onHideEffect(fadeOutBuilder);

        panelBuilder.onHideEffect(new EffectBuilder("hide"));

        showInform(panelBuilder, scriptParentPanel);
    }

    private static int getScriptInformDisplayTime(final CharSequence text, final int priority) {
        if (priority == 0) {
            return 5000 + (text.length() * 50);
        }
        return 8000 + (text.length() * 50);
    }

    /**
     * Show a inform on the screen.
     *
     * @param informBuilder the builder that is meant to create the inform message
     * @param parent        the parent element that stores the inform message
     */
    public void showInform(final ElementBuilder informBuilder, final Element parent) {
        builderQueue.offer(new InformHandler.InformBuildTask(informBuilder, parent));
    }
}

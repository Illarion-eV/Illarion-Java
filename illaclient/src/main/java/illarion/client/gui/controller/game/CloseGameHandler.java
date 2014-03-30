/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package illarion.client.gui.controller.game;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.IllaClient;
import illarion.client.input.InputReceiver;
import illarion.client.world.events.CloseGameEvent;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.illarion.engine.GameContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This handler takes action in case the user requests the application to quit. It will display a dialog and once it is
 * confirmed the application will shut down.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CloseGameHandler
        implements ScreenController, UpdatableHandler, EventTopicSubscriber<ButtonClickedEvent> {
    /**
     * The parent instance of Nifty-GUI.
     */
    private Nifty parentNifty;

    /**
     * The screen this popup is assigned to.
     */
    private Screen parentScreen;

    /**
     * The popup that is supposed to be displayed in case closing the client is requested.
     */
    @Nullable
    private Element popup;

    /**
     * This variable is toggled to {@code true} in case the handler is supposed to display the close confirmation
     * dialog.
     */
    private boolean showDialog;

    /**
     * This variable is {@code true} as long as the close dialog is active.
     */
    private boolean dialogActive;

    @Override
    public void bind(@Nonnull final Nifty nifty, @Nonnull final Screen screen) {
        parentNifty = nifty;
        parentScreen = screen;

        popup = nifty.createPopup("closeApplication");
    }

    @Override
    public void onStartScreen() {
        AnnotationProcessor.process(this);
    }

    @Override
    public void onEndScreen() {
        AnnotationProcessor.unprocess(this);
    }

    @Override
    public void update(final GameContainer container, final int delta) {
        if (showDialog && !dialogActive) {
            parentNifty.showPopup(parentScreen, popup.getId(), null);
            parentNifty
                    .subscribe(parentScreen, popup.findElementById("#closeYesButton").getId(), ButtonClickedEvent.class,
                               this);
            parentNifty
                    .subscribe(parentScreen, popup.findElementById("#closeNoButton").getId(), ButtonClickedEvent.class,
                               this);
            dialogActive = true;
            showDialog = false;
        }
    }

    @org.bushe.swing.event.annotation.EventTopicSubscriber(topic = InputReceiver.EB_TOPIC)
    public void onInputEventReceived(final String topic, final String command) {
        if ("CloseGame".equals(command)) {
            EventBus.publish(new CloseGameEvent());
        }
    }

    @EventSubscriber
    public void onCloseGameEventReceived(final CloseGameEvent event) {
        if (!dialogActive) {
            showDialog = true;
        }
    }

    @Override
    public void onEvent(@Nonnull final String topic, final ButtonClickedEvent data) {
        if (topic.endsWith("#closeYesButton")) {
            IllaClient.ensureExit();
        } else {
            parentNifty.closePopup(popup.getId());
            dialogActive = false;
        }
    }
}

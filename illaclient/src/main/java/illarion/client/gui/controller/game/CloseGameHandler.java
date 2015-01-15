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
import illarion.client.gui.CloseGameGui;
import illarion.client.input.InputReceiver;
import illarion.client.util.UpdateTask;
import illarion.client.world.World;
import org.bushe.swing.event.EventTopicSubscriber;
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
        implements ScreenController, EventTopicSubscriber<ButtonClickedEvent>, CloseGameGui {
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
     * This variable is {@code true} as long as the close dialog is active.
     */
    private boolean dialogActive;

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        parentNifty = nifty;
        parentScreen = screen;

        popup = nifty.createPopup("closeApplication");
    }

    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {
    }

    private void showExitDialog() {
        if (!dialogActive) {
            parentNifty.showPopup(parentScreen, popup.getId(), null);
            subscribeButtonClick("#closeLogoutButton");
            subscribeButtonClick("#closeExitButton");
            subscribeButtonClick("#closeCancelButton");

            dialogActive = true;
        }
    }

    public void subscribeButtonClick(@Nonnull String id) {
        if ((popup == null) || (parentNifty == null) || (parentScreen == null)) {
            return;
        }

        Element element = popup.findElementById(id);
        if (element == null) {
            return;
        }
        String elementId = element.getId();
        if (elementId == null) {
            return;
        }

        parentNifty.subscribe(parentScreen, elementId, ButtonClickedEvent.class, this);
    }

    @org.bushe.swing.event.annotation.EventTopicSubscriber(topic = InputReceiver.EB_TOPIC)
    public void onInputEventReceived(String topic, String command) {
        if ("CloseGame".equals(command)) {
            showClosingDialog();
        }
    }

    @Override
    public void onEvent(@Nonnull String topic, ButtonClickedEvent data) {
        if (topic.endsWith("#closeExitButton")) {
            IllaClient.ensureExit();
        } else if (topic.endsWith("#closeLogoutButton")) {
            parentNifty.closePopup(popup.getId());
            dialogActive = false;
            IllaClient.performLogout();
        } else if (topic.endsWith("#closeCancelButton")) {
            parentNifty.closePopup(popup.getId());
            dialogActive = false;
        }
    }

    @Override
    public void showClosingDialog() {
        World.getUpdateTaskManager().addTask(new UpdateTask() {
            @Override
            public void onUpdateGame(@Nonnull GameContainer container, int delta) {
                showExitDialog();
            }
        });
    }
}

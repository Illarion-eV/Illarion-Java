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
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.IllaClient;
import illarion.client.world.events.ConnectionLostEvent;
import illarion.client.world.events.ServerNotFoundEvent;
import org.bushe.swing.event.EventTopicSubscriber;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.illarion.engine.GameContainer;

import javax.annotation.Nonnull;

/**
 * @author Fredrik K
 */
public class DisconnectHandler implements ScreenController, UpdatableHandler, EventTopicSubscriber<ButtonClickedEvent> {
    private Element popup;
    private boolean isVisible;
    private boolean isActive;
    private Nifty parentNifty;
    private Screen parentScreen;

    public DisconnectHandler() {
        AnnotationProcessor.process(this);
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        parentNifty = nifty;
        parentScreen = screen;
        popup = nifty.createPopup("noServerFound");
    }

    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {
    }

    @Override
    public void update(GameContainer container, int delta) {
        if (isVisible && !isActive) {
            parentNifty.showPopup(parentScreen, popup.getId(), null);
            parentNifty
                    .subscribe(parentScreen, popup.findElementById("#closeOkButton").getId(), ButtonClickedEvent.class,
                               this);
            isActive = true;
            isVisible = false;
        }
    }

    @EventSubscriber
    public void onCloseGameEventReceived(final ServerNotFoundEvent event) {
        if (!isActive) {
            isVisible = true;
        }
    }

    @EventSubscriber
    public void onConnectionLostEventReceived(final ConnectionLostEvent event) {
        if (isActive) {
            return;
        }
        isVisible = true;
        final Element msgLabel = popup.findElementById("#closeMsg");
        if (msgLabel == null) {
            return;
        }
        final TextRenderer valueTextRenderer = msgLabel.getRenderer(TextRenderer.class);
        if (valueTextRenderer == null) {
            return;
        }
        valueTextRenderer.setText(event.getMessage());
    }

    @Override
    public void onEvent(String topic, ButtonClickedEvent data) {
        IllaClient.ensureExit();
    }
}

/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
package org.illarion.nifty.controls.dialog.message;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.Parameters;
import de.lessvoid.nifty.controls.window.WindowControl;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.SizeValue;
import org.bushe.swing.event.EventTopicSubscriber;
import org.illarion.nifty.controls.DialogMessage;
import org.illarion.nifty.controls.DialogMessageConfirmedEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the main control class for message dialogs.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Deprecated
public class DialogMessageControl extends WindowControl
        implements DialogMessage, EventTopicSubscriber<ButtonClickedEvent> {
    /**
     * The instance of the Nifty-GUI that is parent to this control.
     */
    private Nifty niftyInstance;

    /**
     * The screen that displays this control.
     */
    private Screen currentScreen;

    /**
     * The ID of this dialog.
     */
    private int dialogId;

    /**
     * Helper variable to prevent double firing close events.
     */
    private boolean alreadyClosed;

    /**
     * The message that is displayed in this dialog.
     */
    @Nullable
    private String message;

    /**
     * The label of the button that is displayed in this dialog.
     */
    @Nullable
    private String buttonLabel;

    @Override
    public void bind(
            @Nonnull Nifty nifty, @Nonnull Screen screen, @Nonnull Element element, @Nonnull Parameters parameter) {
        super.bind(nifty, screen, element, parameter);
        niftyInstance = nifty;
        currentScreen = screen;

        dialogId = Integer.parseInt(parameter.get("dialogId"));

        message = parameter.get("text");
        buttonLabel = parameter.get("button");

        alreadyClosed = false;
    }

    @Override
    public void onStartScreen() {
        setText(message);
        setButton(buttonLabel);

        super.onStartScreen();

        Element element = getElement();
        Element parent = element.getParent();

        int x = (parent.getWidth() - element.getWidth()) / 2;
        int y = (parent.getHeight() - element.getHeight()) / 2;

        element.setConstraintX(SizeValue.px(x));
        element.setConstraintY(SizeValue.px(y));

        parent.layoutElements();
    }

    @Override
    public void setText(@Nonnull String text) {
        Label label = getContent().findNiftyControl("#text", Label.class);
        label.getElement().getRenderer(TextRenderer.class).setLineWrapping(true);
        label.setText(text);
    }

    @Override
    public void setButton(@Nonnull String text) {
        Button button = getContent().findNiftyControl("#button", Button.class);
        button.setText(text);
        niftyInstance.subscribe(currentScreen, button.getId(), ButtonClickedEvent.class, this);
    }

    @Override
    public void onEvent(String topic, ButtonClickedEvent data) {
        if (alreadyClosed) {
            return;
        }
        niftyInstance.publishEvent(getId(), new DialogMessageConfirmedEvent(dialogId));
        closeWindow();
    }

    @Override
    public void closeWindow() {
        getElement().hide(() -> getElement().markForRemoval());
        alreadyClosed = true;
    }
}

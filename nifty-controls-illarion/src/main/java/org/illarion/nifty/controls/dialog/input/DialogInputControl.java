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
package org.illarion.nifty.controls.dialog.input;

import de.lessvoid.nifty.EndNotify;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.*;
import de.lessvoid.nifty.controls.window.WindowControl;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.SizeValue;
import org.bushe.swing.event.EventTopicSubscriber;
import org.illarion.nifty.controls.DialogInput;
import org.illarion.nifty.controls.DialogInputConfirmedEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the main control class for input dialogs.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Deprecated
public class DialogInputControl extends WindowControl implements DialogInput, EventTopicSubscriber<ButtonClickedEvent> {
    /**
     * The instance of the Nifty-GUI that is parent to this control.
     */
    @Nullable
    private Nifty niftyInstance;

    /**
     * The screen that displays this control.
     */
    @Nullable
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
     * The label of the left button that is displayed in this dialog.
     */
    @Nullable
    private String buttonLabelLeft;

    /**
     * The label of the right button that is displayed in this dialog.
     */
    @Nullable
    private String buttonLabelRight;

    /**
     * The maximal amount of characters allowed to be typed into the input dialog.
     */
    private int maxLength;

    /**
     * The text that is displayed as description in this dialog.
     */
    @Nullable
    private String description;

    @Nonnull
    private String initialText;

    @Override
    public void bind(
            @Nonnull Nifty nifty, @Nonnull Screen screen, @Nonnull Element element, @Nonnull Parameters parameter) {
        super.bind(nifty, screen, element, parameter);
        niftyInstance = nifty;
        currentScreen = screen;

        dialogId = Integer.parseInt(parameter.get("dialogId"));

        buttonLabelLeft = parameter.get("buttonLeft");
        buttonLabelRight = parameter.get("buttonRight");
        maxLength = parameter.getAsInteger("maxLength", 65535);

        description = parameter.getWithDefault("description", "");
        initialText = parameter.getWithDefault("initialText", "");

        alreadyClosed = false;
    }

    @Override
    public void onStartScreen() {
        assert buttonLabelLeft != null : "Control was not bound correctly";
        assert buttonLabelRight != null : "Control was not bound correctly";
        assert description != null : "Control was not bound correctly";

        setButtonLabel(DialogButton.LeftButton, buttonLabelLeft);
        setButtonLabel(DialogButton.RightButton, buttonLabelRight);
        setDescription(description);
        setMaximalLength(maxLength);
        setInputText(initialText);

        super.onStartScreen();

        Element element = getElement();
        Element parent = element.getParent();

        int x = (parent.getWidth() - element.getWidth()) / 2;
        int y = (parent.getHeight() - element.getHeight()) / 2;

        element.setConstraintX(new SizeValue(Integer.toString(x) + "px"));
        element.setConstraintY(new SizeValue(Integer.toString(y) + "px"));

        parent.layoutElements();
    }

    @Override
    public void setButtonLabel(@Nonnull DialogButton button, @Nonnull String label) {
        Button buttonControl = null;
        switch (button) {
            case LeftButton:
                buttonControl = getContent().findNiftyControl("#buttonLeft", Button.class);
                break;
            case RightButton:
                buttonControl = getContent().findNiftyControl("#buttonRight", Button.class);
        }

        if (buttonControl == null) {
            throw new IllegalStateException("Failure while fetching button.");
        }

        buttonControl.setText(label);
        assert niftyInstance != null : "Control was not bound correctly.";
        niftyInstance.subscribe(currentScreen, buttonControl.getId(), ButtonClickedEvent.class, this);
    }

    @Override
    public void setMaximalLength(int length) {
        TextField field = getContent().findNiftyControl("#input", TextField.class);
        if (field == null) {
            throw new IllegalArgumentException("Failed to fetch input field.");
        }
        field.setMaxLength(length);
    }

    @Override
    public void setDescription(@Nonnull String text) {
        Label label = getContent().findNiftyControl("#description", Label.class);
        if (label == null) {
            throw new IllegalArgumentException("Failed to fetch description label.");
        }
        label.setText(text);
    }

    @Override
    public void onEvent(@Nonnull String topic, ButtonClickedEvent data) {
        assert niftyInstance != null : "Control was not bound correctly.";

        if (alreadyClosed) {
            return;
        }

        if (topic.contains("#buttonLeft")) {
            niftyInstance.publishEvent(getId(), new DialogInputConfirmedEvent(dialogId, DialogButton.LeftButton,
                                                                              getInputText()));
        } else {
            niftyInstance.publishEvent(getId(), new DialogInputConfirmedEvent(dialogId, DialogButton.RightButton,
                                                                              getInputText()));
        }
        closeWindow();
    }

    @Override
    public void closeWindow() {
        getElement().hide(new EndNotify() {
            @Override
            public void perform() {
                getElement().markForRemoval();
            }
        });
        alreadyClosed = true;
    }

    @Nonnull
    private String getInputText() {
        TextField field = getContent().findNiftyControl("#input", TextField.class);
        if (field == null) {
            throw new IllegalArgumentException("Failed to fetch input field.");
        }

        return field.getRealText();
    }

    private void setInputText(@Nonnull CharSequence text) {
        TextField field = getContent().findNiftyControl("#input", TextField.class);
        if (field == null) {
            throw new IllegalArgumentException("Failed to fetch input field.");
        }

        field.setText(text);
    }
}

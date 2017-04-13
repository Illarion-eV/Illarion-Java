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
package org.illarion.nifty.controls.dialog.input;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.*;
import de.lessvoid.nifty.controls.window.WindowControl;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyStandardInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.SizeValue;
import org.bushe.swing.event.EventTopicSubscriber;
import org.illarion.nifty.controls.DialogInput;
import org.illarion.nifty.controls.DialogInputConfirmedEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the main control class for Character View window
 *
 */
@Deprecated
public class DialogCharacterControl extends WindowControl implements DialogInput, EventTopicSubscriber<ButtonClickedEvent> {
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

    @Nullable
    private String lookAt;

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
        lookAt = parameter.getWithDefault("lookAt", "");
        initialText = parameter.getWithDefault("initialText", "");

        alreadyClosed = false;
    }

    @Override
    public void onStartScreen() {
        assert buttonLabelLeft != null : "Control was not bound correctly";
        assert buttonLabelRight != null : "Control was not bound correctly";
        assert description != null : "Control was not bound correctly";
        assert lookAt != null : "Control was not bound correctly";

        setButtonLabel(DialogButton.LeftButton, buttonLabelLeft);
        setButtonLabel(DialogButton.RightButton, buttonLabelRight);
        setDescription(description);
        setLookAt(lookAt);
        setMaximalLength(maxLength);
        setInputText(initialText);

        super.onStartScreen();

        Element element = getElement();
        Element parent = element.getParent();

        int x = (parent.getWidth() - element.getWidth()) / 2;
        int y = (parent.getHeight() - element.getHeight()) / 2;

        element.setConstraintX(SizeValue.px(x));
        element.setConstraintY(SizeValue.px(y));

        parent.layoutElements();

        element.addInputHandler(inputEvent -> {
            if (inputEvent instanceof NiftyStandardInputEvent) {
                switch ((NiftyStandardInputEvent) inputEvent) {
                    case SubmitText:
                        fireResponse(DialogButton.LeftButton);
                        return true;
                    case Escape:
                        fireResponse(DialogButton.RightButton);
                        return true;
                    default:
                        return false;
                }
            }

            return false;
        });
    }

    private void setLookAt(String text) {
        Label label = getContent().findNiftyControl("#lookAt", Label.class);
        if (label == null) {
            throw new IllegalArgumentException("Failed to fetch description label.");
        }
        label.setText(text);
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
                break;
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
        getTextField().setMaxLength(length);
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
    public void setFocus() {
        bringToFront();
        Element textField = getTextField().getElement();
        if (textField == null) {
            throw new IllegalStateException("Element of the text field is null. WTF?!");
        }
        textField.setFocus();
    }

    @Override
    public void onEvent(@Nonnull String topic, ButtonClickedEvent data) {
        if (topic.contains("#buttonLeft")) {
            fireResponse(DialogButton.LeftButton);
        } else {
            fireResponse(DialogButton.RightButton);
        }
    }

    private void fireResponse(@Nonnull DialogButton button) {
        assert niftyInstance != null : "Control was not bound correctly.";
        if (alreadyClosed) {
            return;
        }

        niftyInstance.publishEvent(getId(), new DialogInputConfirmedEvent(dialogId, button, getInputText()));
        closeWindow();
    }

    @Override
    public void closeWindow() {
        getElement().hide(() -> getElement().markForRemoval());
        alreadyClosed = true;
    }

    @Nonnull
    String getInputText() {
        return getTextField().getRealText();
    }

    private void setInputText(@Nonnull CharSequence text) {
        getTextField().setText(text);
    }

    @Nonnull
    private TextField getTextField() {
        Element content = getContent();
        if (content == null) {
            throw new IllegalStateException("Control doesn't seem to be bound properly. Content is null.");
        }

        TextField inputArea = content.findNiftyControl("#input", TextField.class);
        if (inputArea == null) {
            throw new IllegalStateException("Control is not bound correctly or the underlying object is faulty. Input" +
                                                    " area not available.");
        }
        return inputArea;
    }
}

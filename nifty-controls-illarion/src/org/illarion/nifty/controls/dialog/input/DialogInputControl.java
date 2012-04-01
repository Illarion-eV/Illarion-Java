/*
 * This file is part of the Illarion Nifty-GUI Controls.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Nifty-GUI Controls is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Nifty-GUI Controls is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Nifty-GUI Controls.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.nifty.controls.dialog.input;

import de.lessvoid.nifty.EndNotify;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.controls.window.WindowControl;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.xml.xpp3.Attributes;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import org.illarion.nifty.controls.DialogInput;
import org.illarion.nifty.controls.DialogInputConfirmedEvent;

import java.util.Properties;

/**
 * This is the main control class for input dialogs.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Deprecated
public class DialogInputControl
        extends WindowControl
        implements DialogInput, EventTopicSubscriber<ButtonClickedEvent> {
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
     * The label of the left button that is displayed in this dialog.
     */
    private String buttonLabelLeft;

    /**
     * The label of the right button that is displayed in this dialog.
     */
    private String buttonLabelRight;

    /**
     * The maximal amount of characters allowed to be typed into the input dialog.
     */
    private int maxLength;

    @Override
    public void bind(final Nifty nifty, final Screen screen, final Element element, final Properties parameter,
                     final Attributes controlDefinitionAttributes) {
        super.bind(nifty, screen, element, parameter, controlDefinitionAttributes);
        niftyInstance = nifty;
        currentScreen = screen;

        dialogId = Integer.parseInt(controlDefinitionAttributes.get("dialogId"));

        buttonLabelLeft = controlDefinitionAttributes.get("buttonLeft");
        buttonLabelRight = controlDefinitionAttributes.get("buttonRight");
        maxLength = controlDefinitionAttributes.getAsInteger("maxLength", 65535);

        alreadyClosed = false;
    }

    @Override
    public void onStartScreen() {
        setButtonLabel(DialogInput.DialogButton.left, buttonLabelLeft);
        setButtonLabel(DialogInput.DialogButton.right, buttonLabelRight);
        setMaximalLength(maxLength);

        super.onStartScreen();

        final Element element = getElement();
        final Element parent = element.getParent();

        final int x = (parent.getWidth() - element.getWidth()) / 2;
        final int y = (parent.getHeight() - element.getHeight()) / 2;

        element.setConstraintX(new SizeValue(Integer.toString(x) + "px"));
        element.setConstraintY(new SizeValue(Integer.toString(y) + "px"));

        parent.layoutElements();
    }

    @Override
    public void setButtonLabel(final DialogInput.DialogButton button, final String label) {
        Button buttonControl = null;
        switch (button) {
            case left:
                buttonControl = getContent().findNiftyControl("#buttonLeft", Button.class);
                break;
            case right:
                buttonControl = getContent().findNiftyControl("#buttonRight", Button.class);
        }

        if (buttonControl == null) {
            throw new IllegalStateException("Failure while fetching button.");
        }

        buttonControl.setText(label);
        niftyInstance.subscribe(currentScreen, buttonControl.getId(), ButtonClickedEvent.class, this);
    }

    @Override
    public void setMaximalLength(final int length) {
        final TextField field = getContent().findNiftyControl("#input", TextField.class);
        if (field == null) {
            throw new IllegalArgumentException("Failed to fetch input field.");
        }
        field.setMaxLength(length);
    }

    @Override
    public void onEvent(final String topic, final ButtonClickedEvent data) {
        if (alreadyClosed) {
            return;
        }

        if (topic.contains("#buttonLeft")) {
            EventBus.publish(new DialogInputConfirmedEvent(dialogId, DialogButton.left, getInputText()));
        } else {
            EventBus.publish(new DialogInputConfirmedEvent(dialogId, DialogButton.right, getInputText()));
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

    /**
     * Get the text that was typed into the input area of this control.
     *
     * @return the text of the input area
     */
    private String getInputText() {
        final TextField field = getContent().findNiftyControl("#input", TextField.class);
        if (field == null) {
            throw new IllegalArgumentException("Failed to fetch input field.");
        }

        return field.getText();
    }
}

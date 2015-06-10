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
package illarion.client.gui.controller.game;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.controls.textfield.filter.input.TextFieldInputFilter;
import de.lessvoid.nifty.controls.textfield.format.TextFieldDisplayFormat;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyStandardInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.IntStream;

/**
 * This class takes care for displaying and controlling the number select popup properly.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class NumberSelectPopupHandler implements ScreenController {

    /**
     * This is the callback interface for this class. Once the number select popup is closed for confirmed this one
     * is called.
     */
    public interface Callback {
        /**
         * This function is called in case the popup is canceled.
         */
        void popupCanceled();

        /**
         * This function is called in case the user confirms the popup.
         *
         * @param value the confirmation value
         */
        void popupConfirmed(int value);
    }

    /**
     * The logging instance for this class.
     */
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(NumberSelectPopupHandler.class);

    /**
     * The parent instance of Nifty-GUI.
     */
    private Nifty parentNifty;

    /**
     * The screen this popup is assigned to.
     */
    private Screen parentScreen;

    /**
     * The currently active popup.
     */
    @Nullable
    private Element activePopup;

    /**
     * The callback assigned to the popup.
     */
    @Nullable
    private Callback activeCallback;

    /**
     * The largest number allowed.
     */
    private int maxNumber;

    /**
     * The smallest number allowed.
     */
    private int minNumber;

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        parentNifty = nifty;
        parentScreen = screen;
    }

    @Override
    public void onStartScreen() {
        parentNifty.subscribeAnnotations(this);
    }

    /**
     * Request a new popup. This will cancel any number input popup that was already opened.
     *
     * @param minValue the minimal value that is allowed to be selected by this number select popup
     * @param maxValue the maximal value that is allowed to be selected by this number select popup
     * @param callback the callback that is called in case the user interacts with the popup
     */
    public void requestNewPopup(
            int minValue, int maxValue, @Nonnull Callback callback) {
        World.getUpdateTaskManager().addTask((container, delta) -> internalCreateNewPopup(minValue, maxValue, callback));
    }

    /**
     * This function really creates the new topic.
     *
     * @param minValue the minimal value that is allowed to be selected by this number select popup
     * @param maxValue the maximal value that is allowed to be selected by this number select popup
     * @param callback the callback that is called in case the user interacts with the popup
     */
    private void internalCreateNewPopup(
            int minValue, int maxValue, @Nonnull Callback callback) {
        cancelActivePopup();

        activePopup = parentNifty.createPopup("numberSelect");
        parentNifty.showPopup(parentScreen, activePopup.getId(), activePopup.findElementById("#numberInput"));
        activeCallback = callback;
        maxNumber = maxValue;
        minNumber = minValue;

        TextField textField = getTextField();

        assert textField != null;

        textField.enableInputFilter(new InputFilter(textField, maxValue, minValue));

        textField.setFormat(new TextFieldDisplayFormat() {
            @Nonnull
            @Override
            public CharSequence getDisplaySequence(
                    @Nonnull CharSequence original, int start, int end) {
                if (original.length() == 0) {
                    return Integer.toString(minValue);
                }
                return original.subSequence(start, end);
            }
        });

        activePopup.addInputHandler(inputEvent -> {
            if (!(inputEvent instanceof NiftyStandardInputEvent)) {
                return false;
            }
            switch ((NiftyStandardInputEvent) inputEvent) {
                case Escape:
                    cancelActivePopup();
                    return true;
                case SubmitText:
                    confirmActivePopup();
                    return true;
                default:
                    return false;
            }
        });

        textField.setText("");
    }

    /**
     * Event that arrives in case the right button (+1) is clicked.
     *
     * @param topic the topic of the event
     * @param event the button pressed event
     */
    @NiftyEventSubscriber(pattern = ".+#numberSelectPopup#buttonRight")
    public void onButtonRightEvent(String topic, ButtonClickedEvent event) {
        if (activePopup == null) {
            return;
        }

        int currentValue = getCurrentValue();
        if ((currentValue + 1) > maxNumber) {
            returnFocusToTextField();
            return;
        }

        TextField textField = getTextField();
        assert textField != null;
        textField.setText(Integer.toString(currentValue + 1));
        returnFocusToTextField();
    }

    /**
     * Event that arrives in case the left button (-1) is clicked.
     *
     * @param topic the topic of the event
     * @param event the button pressed event
     */
    @NiftyEventSubscriber(pattern = ".+#numberSelectPopup#buttonLeft")
    public void onButtonLeftEvent(String topic, ButtonClickedEvent event) {
        if (activePopup == null) {
            return;
        }

        int currentValue = getCurrentValue();
        if ((currentValue - 1) < minNumber) {
            returnFocusToTextField();
            return;
        }

        TextField textField = getTextField();
        assert textField != null;
        textField.setText(Integer.toString(currentValue - 1));
        returnFocusToTextField();
    }

    /**
     * Event that arrives in case the confirmation button is clicked.
     *
     * @param topic the topic of the event
     * @param event the button pressed event
     */
    @NiftyEventSubscriber(pattern = ".+#numberSelectPopup#buttonOkay")
    public void onButtonOkayEvent(String topic, ButtonClickedEvent event) {
        if (activePopup == null) {
            return;
        }

        confirmActivePopup();
    }

    /**
     * Event that arrives in case the cancel button is clicked.
     *
     * @param topic the topic of the event
     * @param event the button pressed event
     */
    @NiftyEventSubscriber(pattern = ".+#numberSelectPopup#buttonCancel")
    public void onButtonCancelEvent(String topic, ButtonClickedEvent event) {
        if (activePopup == null) {
            return;
        }

        cancelActivePopup();
    }

    @Override
    public void onEndScreen() {
        cancelActivePopup();
        parentNifty.unsubscribeAnnotations(this);
    }

    /**
     * Get the current value that is displayed inside the text input.
     *
     * @return the displayed value or {@code 0} in case there is currently no popup
     */
    private int getCurrentValue() {
        if (activePopup == null) {
            return 0;
        }

        TextField textField = getTextField();
        assert textField != null;
        return Integer.parseInt(textField.getDisplayedText());
    }

    /**
     * Set the focus to the text field and on the last written character.
     */
    private void returnFocusToTextField() {
        TextField field = getTextField();
        if (field != null) {
            field.getElement().setFocus();
            field.setCursorPosition(field.getRealText().length());
        }
    }

    /**
     * Get the text field control of the currently active popup.
     *
     * @return the text field of the popup
     */
    @Nullable
    private TextField getTextField() {
        if (activePopup == null) {
            return null;
        }

        return activePopup.findNiftyControl("#numberInput", TextField.class);
    }

    /**
     * Cancel and destroy the currently active popup. This sends a cancel to the callback and removes the active popup.
     */
    private void cancelActivePopup() {
        if (activePopup != null) {
            if (activeCallback == null) {
                LOGGER.error("Number select Callback gone missing!");
            } else {
                activeCallback.popupCanceled();
            }
            parentNifty.closePopup(activePopup.getId());
            activePopup = null;
            activeCallback = null;
        }
    }

    /**
     * Confirm and destroy the currently active popup. This sends a confirmation to the callback and removes the
     * active popup.
     */
    private void confirmActivePopup() {
        if (activePopup != null) {
            if (activeCallback == null) {
                LOGGER.error("Number select Callback gone missing!");
            } else {
                activeCallback.popupConfirmed(getCurrentValue());
            }
            parentNifty.closePopup(activePopup.getId());
            activePopup = null;
            activeCallback = null;
        }
    }

    private static class InputFilter implements TextFieldInputFilter {
        @Nonnull
        private final TextField textField;
        private final int maxValue;
        private final int minValue;

        public InputFilter(@Nonnull TextField textField, int maxValue, int minValue) {
            this.textField = textField;
            this.maxValue = maxValue;
            this.minValue = minValue;
        }

        @Override
        public boolean acceptInput(int index, @Nonnull CharSequence newChars) {
            try (IntStream newCharStream = newChars.chars()) {
                if (!newCharStream.allMatch(Character::isDigit)) {
                    return false;
                }
            }

            String currentText = textField.getRealText();
            StringBuilder buffer = new StringBuilder(currentText);
            buffer.insert(index, newChars);

            int value = Integer.parseInt(buffer.toString());
            return !((value > maxValue) || (value < minValue));
        }

        @Override
        public boolean acceptInput(int index, char newChar) {
            if (!Character.isDigit(newChar)) {
                return false;
            }
            String currentText = textField.getRealText();
            StringBuilder buffer = new StringBuilder(currentText);
            buffer.insert(index, newChar);

            int value = Integer.parseInt(buffer.toString());
            return !((value > maxValue) || (value < minValue));
        }
    }
}

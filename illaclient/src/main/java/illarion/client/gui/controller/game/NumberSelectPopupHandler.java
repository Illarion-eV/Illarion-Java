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
package illarion.client.gui.controller.game;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.controls.textfield.filter.input.TextFieldInputCharFilter;
import de.lessvoid.nifty.controls.textfield.format.TextFieldDisplayFormat;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.input.NiftyStandardInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.util.UpdateTask;
import illarion.client.world.World;
import org.apache.log4j.Logger;
import org.illarion.engine.GameContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    private NumberSelectPopupHandler.Callback activeCallback;

    /**
     * The largest number allowed.
     */
    private int maxNumber;

    /**
     * The smallest number allowed.
     */
    private int minNumber;

    @Override
    public void bind(final Nifty nifty, final Screen screen) {
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
    public void requestNewPopup(final int minValue, final int maxValue,
                                @Nonnull final NumberSelectPopupHandler.Callback callback) {
        World.getUpdateTaskManager().addTask(new UpdateTask() {
            @Override
            public void onUpdateGame(@Nonnull final GameContainer container, final int delta) {
                internalCreateNewPopup(minValue, maxValue, callback);
            }
        });
    }

    /**
     * This function really creates the new topic.
     *
     * @param minValue the minimal value that is allowed to be selected by this number select popup
     * @param maxValue the maximal value that is allowed to be selected by this number select popup
     * @param callback the callback that is called in case the user interacts with the popup
     */
    private void internalCreateNewPopup(final int minValue, final int maxValue,
                                        @Nonnull final NumberSelectPopupHandler.Callback callback) {
        cancelActivePopup();

        activePopup = parentNifty.createPopup("numberSelect");
        parentNifty.showPopup(parentScreen, activePopup.getId(), activePopup.findElementById("#numberInput"));
        activeCallback = callback;
        maxNumber = maxValue;
        minNumber = minValue;

        final TextField textField = getTextField();

        assert textField != null;
        textField.enableInputFilter(new TextFieldInputCharFilter() {
            @Override
            public boolean acceptInput(final int index, final char newChar) {
                if (!Character.isDigit(newChar)) {
                    return false;
                }
                final String currentText = textField.getRealText();
                final StringBuilder buffer = new StringBuilder(currentText);
                buffer.insert(index, newChar);

                final int value = Integer.parseInt(buffer.toString());
                return !((value > maxValue) || (value < minValue));
            }
        });

        textField.setFormat(new TextFieldDisplayFormat() {
            @Nonnull
            @Override
            public CharSequence getDisplaySequence(@Nonnull final CharSequence original, final int start, final int end) {
                if (original.length() == 0) {
                    return Integer.toString(minValue);
                }
                return original.subSequence(start, end);
            }
        });

        activePopup.addInputHandler(new KeyInputHandler() {
            @Override
            public boolean keyEvent(final NiftyInputEvent inputEvent) {
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
    public void onButtonRightEvent(final String topic, final ButtonClickedEvent event) {
        if (activePopup == null) {
            return;
        }

        final int currentValue = getCurrentValue();
        if ((currentValue + 1) > maxNumber) {
            returnFocusToTextField();
            return;
        }

        final TextField textField = getTextField();
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
    public void onButtonLeftEvent(final String topic, final ButtonClickedEvent event) {
        if (activePopup == null) {
            return;
        }

        final int currentValue = getCurrentValue();
        if ((currentValue - 1) < minNumber) {
            returnFocusToTextField();
            return;
        }

        final TextField textField = getTextField();
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
    public void onButtonOkayEvent(final String topic, final ButtonClickedEvent event) {
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
    public void onButtonCancelEvent(final String topic, final ButtonClickedEvent event) {
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

        final TextField textField = getTextField();
        assert textField != null;
        return Integer.parseInt(textField.getDisplayedText());
    }

    /**
     * Set the focus to the text field and on the last written character.
     */
    private void returnFocusToTextField() {
        final TextField field = getTextField();
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
     * The logging instance for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(NumberSelectPopupHandler.class);

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
}

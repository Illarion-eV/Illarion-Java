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

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.net.CommandFactory;
import illarion.client.net.CommandList;
import illarion.client.net.client.CloseDialogInputCmd;
import illarion.client.net.client.CloseDialogMessageCmd;
import illarion.client.net.server.events.DialogInputReceivedEvent;
import illarion.client.net.server.events.DialogMessageReceivedEvent;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.illarion.nifty.controls.DialogInput;
import org.illarion.nifty.controls.DialogInputConfirmedEvent;
import org.illarion.nifty.controls.DialogMessageConfirmedEvent;
import org.illarion.nifty.controls.dialog.input.builder.DialogInputBuilder;
import org.illarion.nifty.controls.dialog.message.builder.DialogMessageBuilder;

/**
 * This class is the dialog handler that takes care for receiving events to show dialogs. It opens and maintains those
 * dialogs and notifies the server in case a dialog is closed.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DialogHandler
        implements ScreenController {
    private final EventSubscriber<DialogMessageReceivedEvent> messageEventHandler;
    private final EventSubscriber<DialogMessageConfirmedEvent> messageConfirmationEventHandler;
    private final EventSubscriber<DialogInputReceivedEvent> inputEventHandler;
    private final EventSubscriber<DialogInputConfirmedEvent> inputConfirmationEventHandler;

    private Nifty nifty;
    private Screen screen;

    public DialogHandler() {
        messageEventHandler = new EventSubscriber<DialogMessageReceivedEvent>() {
            @Override
            public void onEvent(final DialogMessageReceivedEvent event) {
                showDialogMessage(event.getId(), event.getTitle(), event.getMessage());
            }
        };

        inputEventHandler = new EventSubscriber<DialogInputReceivedEvent>() {
            @Override
            public void onEvent(final DialogInputReceivedEvent event) {
                showDialogInput(event.getId(), event.getTitle(), event.getMaxLength(), event.hasMultipleLines());
            }
        };

        messageConfirmationEventHandler = new EventSubscriber<DialogMessageConfirmedEvent>() {
            @Override
            public void onEvent(final DialogMessageConfirmedEvent event) {
                final CommandFactory factory = CommandFactory.getInstance();
                final CloseDialogMessageCmd cmd = factory.getCommand(CommandList.CMD_CLOSE_DIALOG_MSG,
                        CloseDialogMessageCmd.class);
                cmd.setDialogId(event.getDialogId());
                cmd.send();
            }
        };

        inputConfirmationEventHandler = new EventSubscriber<DialogInputConfirmedEvent>() {
            @Override
            public void onEvent(final DialogInputConfirmedEvent event) {
                final CommandFactory factory = CommandFactory.getInstance();
                final CloseDialogInputCmd cmd = factory.getCommand(CommandList.CMD_CLOSE_DIALOG_INPUT,
                        CloseDialogInputCmd.class);
                cmd.setDialogId(event.getDialogId());
                if (event.getPressedButton() == DialogInput.DialogButton.left) {
                    cmd.setSuccess(true);
                    cmd.setText(event.getText());
                } else {
                    cmd.setSuccess(false);
                    cmd.setText("");
                }
                cmd.send();
            }
        };
    }

    public void bind(final Nifty parentNifty, final Screen parentScreen) {
        nifty = parentNifty;
        screen = parentScreen;

    }

    @Override
    public void onStartScreen() {
        EventBus.subscribe(DialogMessageReceivedEvent.class, messageEventHandler);
        EventBus.subscribe(DialogMessageConfirmedEvent.class, messageConfirmationEventHandler);
        EventBus.subscribe(DialogInputReceivedEvent.class, inputEventHandler);
        EventBus.subscribe(DialogInputConfirmedEvent.class, inputConfirmationEventHandler);
    }

    @Override
    public void onEndScreen() {
        EventBus.unsubscribe(DialogMessageReceivedEvent.class, messageEventHandler);
        EventBus.unsubscribe(DialogMessageConfirmedEvent.class, messageConfirmationEventHandler);
        EventBus.unsubscribe(DialogInputReceivedEvent.class, inputEventHandler);
        EventBus.unsubscribe(DialogInputConfirmedEvent.class, inputConfirmationEventHandler);
    }

    public void showDialogMessage(final int id, final String title, final String message) {
        final Element parentAra = screen.findElementByName("windows");
        final DialogMessageBuilder builder = new DialogMessageBuilder("msgDialog" + id, title);
        builder.text(message);
        builder.button("OK");
        builder.dialogId(id);
        builder.width(builder.pixels(400));
        builder.build(nifty, screen, parentAra);
    }

    public void showDialogInput(final int id, final String title, final int maxCharacters,
                                final boolean multipleLines) {
        final Element parentAra = screen.findElementByName("windows");
        final DialogInputBuilder builder = new DialogInputBuilder("inputDialog" + id, title);
        builder.buttonLeft("OK");
        builder.buttonRight("Cancel");
        builder.dialogId(id);
        builder.maxLength(maxCharacters);
        if (multipleLines) {
            builder.style("llarion-dialog-input-multi");
        } else {
            builder.style("llarion-dialog-input-single");
        }
        builder.width(builder.pixels(400));
        builder.build(nifty, screen, parentAra);
    }
}

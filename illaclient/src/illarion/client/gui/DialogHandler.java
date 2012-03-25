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

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.illarion.nifty.controls.DialogMessageConfirmedEvent;
import org.illarion.nifty.controls.dialog.message.builder.DialogMessageBuilder;

import illarion.client.net.CommandFactory;
import illarion.client.net.CommandList;
import illarion.client.net.client.CloseDialogMessageCmd;
import illarion.client.net.server.events.DialogMessageReceivedEvent;

/**
 * This class is the dialog handler that takes care for receiving events to show dialogs. It opens and maintains those
 * dialogs and notifies the server in case a dialog is closed.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DialogHandler
        implements ScreenController {
    private final EventSubscriber<DialogMessageReceivedEvent> messageEventHandler;
    private final EventSubscriber<DialogMessageConfirmedEvent> confirmationEventHandler;

    private Nifty nifty;
    private Screen screen;

    public DialogHandler() {
        messageEventHandler = new EventSubscriber<DialogMessageReceivedEvent>() {
            @Override
            public void onEvent(final DialogMessageReceivedEvent event) {
                showDialogMessage(event.getId(), event.getTitle(), event.getMessage());
            }
        };

        confirmationEventHandler = new EventSubscriber<DialogMessageConfirmedEvent>() {
            @Override
            public void onEvent(final DialogMessageConfirmedEvent event) {
                final CommandFactory factory = CommandFactory.getInstance();
                final CloseDialogMessageCmd cmd = factory.getCommand(CommandList.CMD_CLOSE_DIALOG_MSG,
                                                                     CloseDialogMessageCmd.class);
                cmd.setDialogId(event.getDialogId());
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
        EventBus.subscribe(DialogMessageConfirmedEvent.class, confirmationEventHandler);
    }

    @Override
    public void onEndScreen() {
        EventBus.unsubscribe(DialogMessageReceivedEvent.class, messageEventHandler);
        EventBus.unsubscribe(DialogMessageConfirmedEvent.class, confirmationEventHandler);
    }

    public void showDialogMessage(final int id, final String title, final String message) {
        final Element parentAra = screen.findElementByName("windows");
        final DialogMessageBuilder builder = new DialogMessageBuilder("dialog" + id, title);
        builder.text(message);
        builder.button("OK");
        builder.dialogId(id);
        builder.width(builder.pixels(400));
        builder.build(nifty, screen, parentAra);
    }
}

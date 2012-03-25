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
package org.illarion.nifty.controls.dialog.message;

import de.lessvoid.nifty.EndNotify;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.window.WindowControl;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.xml.xpp3.Attributes;

import java.util.Properties;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import org.illarion.nifty.controls.DialogMessage;
import org.illarion.nifty.controls.DialogMessageConfirmedEvent;

/**
 * Created by IntelliJ IDEA. User: Martin Karing Date: 17.03.12 Time: 19:45 To change this template use File | Settings
 * | File Templates.
 */
@Deprecated
public class DialogMessageControl
        extends WindowControl
        implements DialogMessage, EventTopicSubscriber<ButtonClickedEvent> {
    private Nifty niftyInstance;
    private Screen currentScreen;

    private int dialogId;

    private boolean alreadyClosed;

    private String message;
    private String buttonLabel;

    @Override
    public void bind(Nifty nifty, Screen screen, Element element, Properties parameter,
                     Attributes controlDefinitionAttributes) {
        super.bind(nifty, screen, element, parameter, controlDefinitionAttributes);
        niftyInstance = nifty;
        currentScreen = screen;

        dialogId = Integer.parseInt(controlDefinitionAttributes.get("dialogId"));

        message = controlDefinitionAttributes.get("text");
        buttonLabel = controlDefinitionAttributes.get("button");

        alreadyClosed = false;
    }

    @Override
    public void onStartScreen() {
        setText(message);
        setButton(buttonLabel);

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
    public void setText(String text) {
        final Label label = getContent().findNiftyControl("#text", Label.class);
        label.setText(text);
    }

    public void setButton(final String text) {
        final Button button = getContent().findNiftyControl("#button", Button.class);
        button.setText(text);
        niftyInstance.subscribe(currentScreen, button.getId(), ButtonClickedEvent.class, this);
    }

    @Override
    public void onEvent(final String topic, final ButtonClickedEvent data) {
        if (alreadyClosed) {
            return;
        }
        EventBus.publish(new DialogMessageConfirmedEvent(dialogId));
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
}

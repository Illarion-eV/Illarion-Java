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
package illarion.client.gui.controller;

import de.lessvoid.nifty.EndNotify;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.input.NiftyStandardInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.util.Lang;
import illarion.common.data.Credits;
import illarion.common.data.CreditsList;
import illarion.common.data.CreditsPerson;

import java.util.Iterator;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CreditsMultiScreenController implements ScreenController, KeyInputHandler {
    private Nifty nifty;
    private Screen screen;

    private Element displayParent;
    private Label titleLabel;
    private Element namesPanel;

    @Override
    public void bind(final Nifty nifty, final Screen screen) {
        this.nifty = nifty;
        this.screen = screen;

        displayParent = screen.findElementByName("nameDisplay");
        titleLabel = displayParent.findNiftyControl("title", Label.class);
        namesPanel = displayParent.findElementByName("names");
    }

    @Override
    public void onStartScreen() {
        final Iterator<CreditsList> creditsIterator = Credits.getInstance().getMultiLists();
        showNextEntry(creditsIterator);
    }

    private void showNextEntry(final Iterator<CreditsList> iterator) {
        if (!iterator.hasNext()) {
            gotoNextScreen();
            return;
        }

        final CreditsList list = iterator.next();
        if (Lang.getInstance().isGerman()) {
            titleLabel.setText(list.getNameGerman());
        } else {
            titleLabel.setText(list.getNameEnglish());
        }

        for (final Element element : namesPanel.getElements()) {
            element.markForRemoval();
        }

        for (final CreditsPerson person : list) {
            final LabelBuilder entry = new LabelBuilder();
            entry.style("nifty-label");
            entry.font("textFont");
            entry.width("400px");
            entry.text(person.getName());
            entry.build(nifty, screen, namesPanel);
        }

        nifty.executeEndOfFrameElementActions();
        displayParent.getParent().layoutElements();

        displayParent.show(new EndNotify() {
            @Override
            public void perform() {
                displayParent.hide(new EndNotify() {
                    @Override
                    public void perform() {
                        showNextEntry(iterator);
                    }
                });
            }
        });

    }

    private void gotoNextScreen() {
        nifty.gotoScreen("login");
    }

    @Override
    public void onEndScreen() {
        // nothing
    }

    @Override
    public boolean keyEvent(final NiftyInputEvent inputEvent) {
        if (inputEvent == NiftyStandardInputEvent.Escape) {
            nifty.gotoScreen("login");
            return true;
        }
        return false;
    }
}

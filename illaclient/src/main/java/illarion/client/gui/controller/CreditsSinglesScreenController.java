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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CreditsSinglesScreenController implements ScreenController, KeyInputHandler {
    private Nifty nifty;

    private Element displayParent;
    private Label titleLabel;
    private Label nameLabel;

    @Override
    public void bind(final Nifty nifty, @Nonnull final Screen screen) {
        this.nifty = nifty;

        displayParent = screen.findElementById("nameDisplay");
        titleLabel = displayParent.findNiftyControl("title", Label.class);
        nameLabel = displayParent.findNiftyControl("name", Label.class);
    }

    @Override
    public void onStartScreen() {
        final Iterator<CreditsList> creditsIterator = Credits.getInstance().getSingleLists();

        showNextEntry(creditsIterator);
    }

    private void showNextEntry(@Nonnull final Iterator<CreditsList> iterator) {
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

        final List<String> names = new ArrayList<String>();
        for (final CreditsPerson person : list) {
            names.add(person.getName());
        }
        final StringBuilder builder = new StringBuilder();
        final int nameCount = names.size();
        for (int i = 0; i < nameCount; i++) {
            builder.append(names.get(i));
            if (i < (nameCount - 2)) {
                builder.append(", ");
            } else if (i < (nameCount - 1)) {
                if (Lang.getInstance().isGerman()) {
                    builder.append(" und ");
                } else {
                    builder.append(" and ");
                }
            }
        }
        nameLabel.setText(builder.toString());
        displayParent.layoutElements();

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
        nifty.gotoScreen("creditsMulti");
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

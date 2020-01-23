/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
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
package illarion.client.gui.controller;

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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CreditsMultiScreenController implements ScreenController, KeyInputHandler {
    private Nifty nifty;
    private Screen screen;

    @Nullable
    private Element displayParent;
    @Nullable
    private Label titleLabel;
    @Nullable
    private Element namesPanel;

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;
        this.screen = screen;

        displayParent = screen.findElementById("nameDisplay");
        titleLabel = displayParent.findNiftyControl("title", Label.class);
        namesPanel = displayParent.findElementById("names");
    }

    @Override
    public void onStartScreen() {
        Iterator<CreditsList> creditsIterator = Credits.getInstance().getMultiLists();
        showNextEntry(creditsIterator);
    }

    @Override
    public void onEndScreen() {
        // nothing
    }

    private void showNextEntry(@Nonnull Iterator<CreditsList> iterator) {
        if (!iterator.hasNext()) {
            gotoNextScreen();
            return;
        }

        CreditsList list = iterator.next();
        if (Lang.getInstance().isGerman()) {
            titleLabel.setText(list.getNameGerman());
        } else {
            titleLabel.setText(list.getNameEnglish());
        }

        namesPanel.getChildren().forEach(Element::markForRemoval);

        for (CreditsPerson person : list) {
            LabelBuilder entry = new LabelBuilder();
            entry.style("nifty-label");
            entry.font("textFont");
            entry.width("400px");
            entry.text(person.getName());
            entry.build(nifty, screen, namesPanel);
        }

        nifty.executeEndOfFrameElementActions();
        displayParent.getParent().layoutElements();

        displayParent.show(() -> displayParent.hide(() -> showNextEntry(iterator)));
    }

    private void gotoNextScreen() {
        nifty.gotoScreen("creditsEnd");
    }

    @Override
    public boolean keyEvent(@Nonnull NiftyInputEvent inputEvent) {
        if (inputEvent == NiftyStandardInputEvent.Escape) {
            nifty.gotoScreen("login");
            return true;
        }
        return false;
    }
}

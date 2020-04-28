/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package org.illarion.nifty.controls.dialog.input.builder;

import de.lessvoid.nifty.builder.ControlBuilder;

import javax.annotation.Nonnull;

/**
 * The builder of a character view window..
 *
 */
public class DialogCharacterBuilder extends ControlBuilder {
    /**
     * Build a new input dialog with a set title and ID.
     *
     * @param id the nifty-gui ID of the dialog
     * @param title the title of the dialog
     */
    public DialogCharacterBuilder(@Nonnull String id, @Nonnull String title) {
        super(id, CreateDialogCharacterControl.NAME);
        set("title", title);
        set("closeable", "false");
        set("hideOnClose", "false");

        alignCenter();
        valignCenter();
    }

    /**
     * Set the text of the left button displayed in this dialog.
     *
     * @param text the text displayed in the left button of this dialog
     */
    public void buttonLeft(@Nonnull String text) {
        set("buttonLeft", text);
    }

    /**
     * Set the text of the right button displayed in this dialog.
     *
     * @param text the text displayed in the right button of this dialog
     */
    public void buttonRight(@Nonnull String text) {
        set("buttonRight", text);
    }

    /**
     * Set the ID of this dialog.
     *
     * @param id the ID of the dialog
     */
    public void dialogId(int id) {
        set("dialogId", Integer.toString(id));
    }

    /**
     * Set the maximal amount of characters that are allowed to be typed in into the dialog.
     *
     * @param maxChars the maximal amount of characters allowed in this input dialog
     */
    public void maxLength(int maxChars) {
        set("maxLength", Integer.toString(maxChars));
    }

    /**
     * Set the text that is displayed as description.
     *
     * @param text the description text
     */
    public void description(@Nonnull String text) {
        set("description", text);
    }

    /**
     * Set the text that is displayed as description.
     *
     * @param text the lookAt text
     */
    public void lookAt(@Nonnull String text) {
        set("lookAt", text);
    }

    public void initalText(@Nonnull String text) {
        set("initialText", text);
    }
}

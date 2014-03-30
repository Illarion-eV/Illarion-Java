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
package org.illarion.nifty.controls.dialog.message.builder;

import de.lessvoid.nifty.builder.ControlBuilder;

import javax.annotation.Nonnull;

/**
 * The builder of a message dialog.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class DialogMessageBuilder extends ControlBuilder {
    /**
     * Build a new message dialog with a set title and ID.
     *
     * @param id the nifty-gui ID of the dialog
     * @param title the title of the dialog
     */
    public DialogMessageBuilder(@Nonnull final String id, final String title) {
        super(id, CreateDialogMessageControl.NAME);
        set("title", title);
        set("closeable", "false");
        set("hideOnClose", "false");

        alignCenter();
        valignCenter();
    }

    /**
     * Set the text that is displayed in this dialog.
     *
     * @param text the text displayed in this dialog
     */
    public void text(@Nonnull final String text) {
        set("text", text);
    }

    /**
     * Set the label of the button that is displayed in this dialog.
     *
     * @param text the button label in this dialog
     */
    public void button(final String text) {
        set("button", text);
    }

    /**
     * Set the ID of this dialog.
     *
     * @param id the dialog ID
     */
    public void dialogId(final int id) {
        set("dialogId", Integer.toString(id));
    }
}

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
package org.illarion.nifty.controls.dialog.message.builder;

import de.lessvoid.nifty.builder.ControlBuilder;
import de.lessvoid.nifty.builder.ElementBuilder;

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
    public DialogMessageBuilder(@Nonnull String id, @Nonnull String title) {
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
    @Override
    public ElementBuilder text(@Nonnull String text) {
        set("text", text);
        return this;
    }

    /**
     * Set the label of the button that is displayed in this dialog.
     *
     * @param text the button label in this dialog
     */
    public void button(@Nonnull String text) {
        set("button", text);
    }

    /**
     * Set the ID of this dialog.
     *
     * @param id the dialog ID
     */
    public void dialogId(int id) {
        set("dialogId", Integer.toString(id));
    }
}

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

import javax.annotation.Nonnull;
import java.util.concurrent.Executor;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class NiftyExecutor implements Executor {
    @Nonnull
    private final Nifty nifty;

    public NiftyExecutor(@Nonnull Nifty nifty) {
        this.nifty = nifty;
    }

    @Override
    public void execute(@Nonnull Runnable command) {
        nifty.scheduleEndOfFrameElementAction(command::run, null);
    }
}

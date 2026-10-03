/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2026 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 */
package illarion.client.gui.controller.game;

import de.lessvoid.nifty.controls.ScrollPanel;
import de.lessvoid.nifty.controls.Scrollbar;
import de.lessvoid.nifty.elements.Element;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A reading position that survives chat layout changes and removal of older entries.
 */
final class ChatScrollPosition {
    @Nonnull
    private final ScrollPanel scrollPanel;
    @Nonnull
    private final Element content;
    private final boolean atBottom;
    @Nullable
    private final Element anchor;
    private final float offset;

    ChatScrollPosition(@Nonnull ScrollPanel scrollPanel) {
        this.scrollPanel = scrollPanel;
        content = scrollPanel.getElement().findElementById("chatLog");
        Scrollbar scrollbar = scrollPanel.getElement().findNiftyControl(
                "#nifty-internal-vertical-scrollbar", Scrollbar.class);
        float position = scrollPanel.getVerticalPos();
        atBottom = (scrollbar != null)
                && (position >= (scrollbar.getWorldMax() - scrollbar.getWorldPageSize()));
        anchor = findAnchor(position);
        offset = (anchor == null) ? 0 : position - (anchor.getY() - content.getY());
    }

    @Nullable
    private Element findAnchor(float position) {
        for (Element entry : content.getChildren()) {
            if (entry.isVisible() && ((entry.getY() - content.getY() + entry.getHeight()) > position)) {
                return entry;
            }
        }

        return null;
    }

    void restore() {
        if (atBottom) {
            scrollPanel.setAutoScroll(ScrollPanel.AutoScroll.BOTTOM);
            scrollPanel.setAutoScroll(ScrollPanel.AutoScroll.OFF);
        } else if ((anchor != null) && content.getChildren().contains(anchor) && anchor.isVisible()) {
            float entryOffset = Math.min(offset, Math.max(0, anchor.getHeight() - 1));
            scrollPanel.setVerticalPos(Math.max(0, anchor.getY() - content.getY() + entryOffset));
        } else {
            // The entry being read has expired; show the oldest remaining entry.
            scrollPanel.setVerticalPos(0);
        }
    }
}

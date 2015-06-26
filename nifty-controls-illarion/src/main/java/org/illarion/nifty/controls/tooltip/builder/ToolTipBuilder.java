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
package org.illarion.nifty.controls.tooltip.builder;

import de.lessvoid.nifty.builder.ControlBuilder;
import de.lessvoid.nifty.tools.Color;

import javax.annotation.Nonnull;

/**
 * Build the tooltip.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ToolTipBuilder extends ControlBuilder {
    public ToolTipBuilder() {
        super("tooltip");
    }

    public ToolTipBuilder(@Nonnull String id) {
        super(id, "tooltip");
    }

    public void title(@Nonnull String value) {
        set("title", value);
    }

    public void titleColor(@Nonnull Color value) {
        titleColor(value.getColorString());
    }

    public void titleColor(@Nonnull String value) {
        set("titleColor", value);
    }

    public void description(@Nonnull String value) {
        set("description", value);
    }

    public void producer(@Nonnull String value) {
        set("producer", value);
    }

    public void type(@Nonnull String value) {
        set("itemtype", value);
    }

    public void level(int value) {
        set("level", Integer.toString(value));
    }

    public void levelColor(@Nonnull Color value) {
        levelColor(value.getColorString());
    }

    public void levelColor(@Nonnull String value) {
        set("levelColor", value);
    }

    public void weight(@Nonnull String value) {
        set("weight", value);
    }

    public void worth(long value) {
        set("worth", Long.toString(value));
    }

    public void quality(@Nonnull String value) {
        set("quality", value);
    }

    public void durability(@Nonnull String value) {
        set("durability", value);
    }

    public void diamondLevel(int value) {
        set("diamondLevel", Integer.toString(value));
    }

    public void emeraldLevel(int value) {
        set("emeraldLevel", Integer.toString(value));
    }

    public void rubyLevel(int value) {
        set("rubyLevel", Integer.toString(value));
    }

    public void obsidianLevel(int value) {
        set("obsidianLevel", Integer.toString(value));
    }

    public void sapphireLevel(int value) {
        set("sapphireLevel", Integer.toString(value));
    }

    public void amethystLevel(int value) {
        set("amethystLevel", Integer.toString(value));
    }

    public void topazLevel(int value) {
        set("topazLevel", Integer.toString(value));
    }

    public void gemBonus(@Nonnull String value) {
        set("gemBonus", value);
    }
}

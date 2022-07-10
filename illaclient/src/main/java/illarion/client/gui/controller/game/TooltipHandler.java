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
package illarion.client.gui.controller.game;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.client.gui.Tooltip;
import illarion.client.gui.events.TooltipsRemovedEvent;
import illarion.client.world.World;
import illarion.common.types.Rectangle;
import org.bushe.swing.event.EventBus;
import org.illarion.engine.GameContainer;
import org.illarion.engine.input.Input;
import org.illarion.nifty.controls.tooltip.builder.ToolTipBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This tooltip handler takes care of showing and hiding the item tooltips on the screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class TooltipHandler implements ScreenController, UpdatableHandler {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(TooltipHandler.class);

    /**
     * The Nifty that is the parent to this handler.
     */
    private Nifty parentNifty;

    /**
     * The screen that is the parent to this handler.
     */
    private Screen parentScreen;

    /**
     * The layer-element that contains all the tooltips.
     */
    @Nullable
    private Element toolTipLayer;

    /**
     * The area the mouse as the remain inside to keep the tooltip active.
     */
    @Nullable
    private Rectangle activeTooltipArea;

    /**
     * The last reported x coordinate of the mouse cursor.
     */
    private int lastMouseX;

    /**
     * The last reported y coordinate of the mouse cursor.
     */
    private int lastMouseY;

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        parentNifty = nifty;
        parentScreen = screen;

        toolTipLayer = screen.findElementById("tooltipLayer");
    }

    @Override
    public void onStartScreen() {
        // nothing
    }

    @Override
    public void onEndScreen() {
        hideToolTipImpl();
    }

    @Override
    public void update(@Nonnull GameContainer container, int delta) {
        Input input = container.getEngine().getInput();
        lastMouseX = input.getMouseX();
        lastMouseY = input.getMouseY();
        if (activeTooltipArea != null) {
            if (!activeTooltipArea.isInside(lastMouseX, lastMouseY)) {
                log.debug("Removing active tooltip. Mouse (x:{} y:{}) is outside of {}", lastMouseX, lastMouseY,
                          activeTooltipArea);
                hideToolTip();
                EventBus.publish(new TooltipsRemovedEvent());
            }
        }
    }

    /**
     * Hide all current tooltips.
     */
    public void hideToolTip() {
        World.getUpdateTaskManager().addTask((container, delta) -> hideToolTipImpl());
    }

    private void hideToolTipImpl() {
        if (toolTipLayer == null) {
            return;
        }
        for (Element element : toolTipLayer.getChildren()) {
            element.hide(element::markForRemoval);
            activeTooltipArea = null;
        }
    }

    /**
     * Create a new tooltip.
     *
     * @param location the tooltip should be place around, the area of this rectangle won't be hidden by the tooltip.
     * Also the mouse is required to remain inside this area to keep the tooltip active
     * @param tooltip the tooltip to display
     */
    public void showToolTip(@Nonnull Rectangle location, @Nonnull Tooltip tooltip) {
        if (!tooltip.isValid()) {
            log.warn("Received a invalid tooltip from the server!");
            return;
        }

        if (!location.isInside(lastMouseX, lastMouseY)) {
            return;
        }

        log.debug("Showing tooltip {} for {}", tooltip, location);

        World.getUpdateTaskManager().addTask((container, delta) -> {
            hideToolTipImpl();
            showToolTipImpl(location, tooltip);
            activeTooltipArea = location;
        });
    }

    public boolean isTooltipActive() {
        return !toolTipLayer.getChildren().isEmpty();
    }

    private long count = Long.MIN_VALUE;

    /**
     * Create a new tooltip. This is the internal implementation that is only called from the update loop.
     *
     * @param location the tooltip should be place around, the area of this rectangle won't be hidden by the tooltip
     * @param tooltip the tooltip to display
     */
    private void showToolTipImpl(@Nonnull Rectangle location, @Nonnull Tooltip tooltip) {
        ToolTipBuilder builder = new ToolTipBuilder("tooltip-" + Long.toString(count++));
        builder.title(tooltip.getName());

        switch (tooltip.getRareness()) {
            case Tooltip.RARENESS_UNCOMMON:
                builder.titleColor("#00a500ff");
                break;
            case Tooltip.RARENESS_RARE:
                builder.titleColor("#003fbfff");
                break;
            case Tooltip.RARENESS_EPIC:
                builder.titleColor("#bb00ffff");
                break;
            case Tooltip.RARENESS_COMMON:
            default:
                builder.titleColor(Color.WHITE);
                break;
        }

        builder.description(tooltip.getDescription());
        builder.producer(tooltip.getCraftedBy());
        builder.type(tooltip.getType());
        builder.level(tooltip.getLevel());

        if (tooltip.isUsable()) {
            builder.levelColor(Color.WHITE);
        } else {
            builder.levelColor("#ff0000ff");
        }

        builder.worth(tooltip.getWorth().getTotalCopper() / 20);
        if (tooltip.getWeight() > 0) {
            builder.weight(Integer.toString(tooltip.getWeight()));
        }
        builder.quality(tooltip.getQualityText());
        builder.durability(tooltip.getDurabilityText());
        builder.amethystLevel(tooltip.getAmethystLevel());
        builder.obsidianLevel(tooltip.getObsidianLevel());
        builder.sapphireLevel(tooltip.getSapphireLevel());
        builder.diamondLevel(tooltip.getDiamondLevel());
        builder.emeraldLevel(tooltip.getEmeraldLevel());
        builder.rubyLevel(tooltip.getRubyLevel());
        builder.topazLevel(tooltip.getTopazLevel());

        if (tooltip.getBonus() > 0.0) {
            String gemBonusString = Double.toString(tooltip.getBonus());
            builder.gemBonus(gemBonusString);
        }

        Element toolTip = builder.build(parentNifty, parentScreen, toolTipLayer);
        toolTip.getParent().layoutElements();

        if (toolTip.getHeight() == 0) {
            parentNifty.update();
            toolTip.getParent().layoutElements();
        }

        int toolTipWidth = toolTip.getWidth();
        int toolTipHeight = toolTip.getHeight();

        boolean topSide = (location.getBottom() - toolTipHeight) > 0;
        boolean rightSide = (location.getRight() - toolTipWidth) < 0;

        if (topSide) {
            toolTip.setConstraintY(SizeValue.px(location.getBottom() - toolTip.getHeight()));
        } else {
            toolTip.setConstraintY(SizeValue.px(location.getTop()));
        }

        if (rightSide) {
            toolTip.setConstraintX(SizeValue.px(location.getLeft()));
        } else {
            toolTip.setConstraintX(SizeValue.px(location.getRight() - toolTip.getWidth()));
        }

        toolTip.getParent().layoutElements();
    }
}

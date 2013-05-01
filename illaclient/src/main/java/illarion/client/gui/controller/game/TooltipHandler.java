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
package illarion.client.gui.controller.game;

import de.lessvoid.nifty.EndNotify;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.client.gui.Tooltip;
import illarion.client.gui.events.TooltipsRemovedEvent;
import illarion.client.util.UpdateTask;
import illarion.client.world.World;
import illarion.common.types.Rectangle;
import org.bushe.swing.event.EventBus;
import org.illarion.engine.GameContainer;
import org.illarion.engine.input.Input;
import org.illarion.nifty.controls.tooltip.builder.ToolTipBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This tooltip handler takes care of showing and hiding the item tooltips on the screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class TooltipHandler implements ScreenController, UpdatableHandler {
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

    /**
     * The task that will clean all opened tooltip.
     */
    @Nonnull
    private UpdateTask cleanToolTips = new UpdateTask() {
        @Override
        public void onUpdateGame(@Nonnull final GameContainer container, final int delta) {
            for (final Element element : toolTipLayer.getElements()) {
                element.hide(new EndNotify() {
                    @Override
                    public void perform() {
                        element.markForRemoval();
                    }
                });
                activeTooltipArea = null;
            }
        }
    };

    @Override
    public void bind(final Nifty nifty, @Nonnull final Screen screen) {
        parentNifty = nifty;
        parentScreen = screen;

        toolTipLayer = screen.findElementByName("tooltipLayer");
    }

    @Override
    public void onStartScreen() {
        // nothing
    }

    @Override
    public void onEndScreen() {
        hideToolTip();
    }

    @Override
    public void update(@Nonnull final GameContainer container, final int delta) {
        final Input input = container.getEngine().getInput();
        lastMouseX = input.getMouseX();
        lastMouseY = input.getMouseY();
        if (activeTooltipArea != null) {
            if (!activeTooltipArea.isInside(lastMouseX, lastMouseY)) {
                hideToolTip();
                EventBus.publish(new TooltipsRemovedEvent());
            }
        }
    }

    /**
     * Hide all current tooltips.
     */
    public void hideToolTip() {
        World.getUpdateTaskManager().addTask(cleanToolTips);
    }

    /**
     * Create a new tooltip.
     *
     * @param location the tooltip should be place around, the area of this rectangle won't be hidden by the tooltip.
     *                 Also the mouse is required to remain inside this area to keep the tooltip active
     * @param tooltip  the tooltip to display
     */
    public void showToolTip(@Nonnull final Rectangle location, @Nonnull final Tooltip tooltip) {
        hideToolTip();

        if (!location.isInside(lastMouseX, lastMouseY)) {
            return;
        }

        World.getUpdateTaskManager().addTask(new UpdateTask() {
            @Override
            public void onUpdateGame(@Nonnull final GameContainer container, final int delta) {
                showToolTipImpl(location, tooltip);
                activeTooltipArea = location;
            }
        });
    }

    public boolean isTooltipActive() {
        return !toolTipLayer.getElements().isEmpty();
    }

    private long count = Long.MIN_VALUE;

    /**
     * Create a new tooltip. This is the internal implementation that is only called from the update loop.
     *
     * @param location the tooltip should be place around, the area of this rectangle won't be hidden by the tooltip
     * @param tooltip  the tooltip to display
     */
    private void showToolTipImpl(@Nonnull final Rectangle location, @Nonnull final Tooltip tooltip) {
        final ToolTipBuilder builder = new ToolTipBuilder("tooltip-" + Long.toString(count++));
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


        if (tooltip.getBonus() > 0) {
            builder.gemBonus(Integer.toString(tooltip.getBonus()));
        }

        final Element toolTip = builder.build(parentNifty, parentScreen, toolTipLayer);
        toolTip.getParent().layoutElements();

        if (toolTip.getHeight() == 0) {
            parentNifty.update();
            toolTip.getParent().layoutElements();
        }

        final int toolTipWidth = toolTip.getWidth();
        final int toolTipHeight = toolTip.getHeight();

        final boolean topSide = (location.getBottom() - toolTipHeight) > 0;
        final boolean rightSide = (location.getRight() - toolTipWidth) < 0;

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

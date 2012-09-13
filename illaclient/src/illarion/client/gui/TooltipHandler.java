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
package illarion.client.gui;

import de.lessvoid.nifty.EndNotify;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.client.gui.events.TooltipsRemovedEvent;
import illarion.client.net.server.events.AbstractItemLookAtEvent;
import illarion.common.util.Rectangle;
import org.bushe.swing.event.EventBus;
import org.illarion.nifty.controls.tooltip.builder.ToolTipBuilder;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This tooltip handler takes care of showing and hiding the item tooltips on the screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class TooltipHandler implements ScreenController, UpdatableHandler {
    /**
     * The queue that contains the tasks for the tooltips that need to be executed upon the next update.
     */
    private final Queue<Runnable> toolTipTasks;

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
    private Rectangle activeTooltipArea;

    /**
     * The task that will clean all opened tooltip.
     */
    private Runnable cleanToolTips = new Runnable() {
        @Override
        public void run() {
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

    /**
     * The default constructor.
     */
    public TooltipHandler() {
        toolTipTasks = new ConcurrentLinkedQueue<Runnable>();
    }

    @Override
    public void bind(final Nifty nifty, final Screen screen) {
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
        cleanToolTips.run();
    }

    @Override
    public void update(final GameContainer container, final int delta) {
        while (true) {
            final Runnable task = toolTipTasks.poll();
            if (task == null) {
                break;
            }

            task.run();
        }

        final Input input = container.getInput();
        if (activeTooltipArea != null) {
            if (!activeTooltipArea.isInside(input.getMouseX(), input.getMouseY())) {
                hideToolTip();
                EventBus.publish(new TooltipsRemovedEvent());
            }
        }
    }

    /**
     * Hide all current tooltips.
     */
    public void hideToolTip() {
        toolTipTasks.offer(cleanToolTips);
    }

    /**
     * Create a new tooltip.
     *
     * @param location the tooltip should be place around, the area of this rectangle won't be hidden by the tooltip.
     *                 Also the mouse is required to remain inside this area to keep the tooltip active
     * @param event    the event that contains the data of the tooltip
     */
    public void showToolTip(final Rectangle location, final AbstractItemLookAtEvent event) {
        hideToolTip();
        toolTipTasks.offer(new Runnable() {
            @Override
            public void run() {
                showToolTipImpl(location, event);
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
     * @param event    the event that contains the data of the tooltip
     */
    private void showToolTipImpl(final Rectangle location, final AbstractItemLookAtEvent event) {
        final ToolTipBuilder builder = new ToolTipBuilder("tooltip-" + Long.toString(count++));
        builder.title(event.getName());

        switch (event.getRareness()) {
            case AbstractItemLookAtEvent.RARENESS_UNCOMMON:
                builder.titleColor("#00a500ff");
                break;
            case AbstractItemLookAtEvent.RARENESS_RARE:
                builder.titleColor("#003fbfff");
                break;
            case AbstractItemLookAtEvent.RARENESS_EPIC:
                builder.titleColor("#bb00ffff");
                break;
            case AbstractItemLookAtEvent.RARENESS_COMMON:
            default:
                builder.titleColor(Color.WHITE);
                break;
        }

        builder.description(event.getDescription());
        builder.producer(event.getProducer());
        builder.worth(event.getWorth().getTotalCopper() / 20);
        if (event.getWeight() > 0) {
            builder.weight(Integer.toString(event.getWeight()));
        }
        builder.quality(event.getQualityText());
        builder.durability(event.getDurabilityText());
        builder.amethystLevel(event.getAmethystLevel());
        builder.obsidianLevel(event.getObsidianLevel());
        builder.sapphireLevel(event.getSapphireLevel());
        builder.diamondLevel(event.getDiamondLevel());
        builder.emeraldLevel(event.getEmeraldLevel());
        builder.rubyLevel(event.getRubyLevel());
        builder.topazLevel(event.getTopazLevel());


        if (event.getBonus() > 0) {
            builder.gemBonus(Integer.toString(event.getBonus()));
        }

        final Element toolTip = builder.build(parentNifty, parentScreen, toolTipLayer);
        toolTip.getParent().layoutElements();

        if (toolTip.getHeight() == 0) {
            parentNifty.update();
        }

        final int toolTipWidth = toolTip.getWidth();
        final int toolTipHeight = toolTip.getHeight();

        final boolean topSide = (location.getBottom() - toolTipHeight) > 0;
        final boolean leftSide = (location.getRight() - toolTipWidth) < 0;

        if (topSide) {
            toolTip.setConstraintY(SizeValue.px(location.getBottom() - toolTip.getHeight()));
        } else {
            toolTip.setConstraintY(SizeValue.px(location.getTop()));
        }

        if (leftSide) {
            toolTip.setConstraintX(SizeValue.px(location.getLeft()));
        } else {
            toolTip.setConstraintX(SizeValue.px(location.getRight() - toolTip.getWidth()));
        }

        toolTip.getParent().layoutElements();
    }
}

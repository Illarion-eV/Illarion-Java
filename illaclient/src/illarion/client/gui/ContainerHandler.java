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

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import gnu.trove.iterator.TIntObjectIterator;
import illarion.client.graphics.Item;
import illarion.client.net.server.events.OpenContainerEvent;
import illarion.client.resources.ItemFactory;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.illarion.nifty.controls.InventorySlot;
import org.illarion.nifty.controls.ItemContainer;
import org.illarion.nifty.controls.itemcontainer.builder.ItemContainerBuilder;

/**
 * This handler that care for properly managing the displaying of containers on the game screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ContainerHandler implements ScreenController {
    private final EventSubscriber<OpenContainerEvent> eventSubscriberOpenContainer;

    /**
     * The Nifty-GUI instance that is handling the GUI display currently.
     */
    private Nifty activeNifty;

    /**
     * The screen that takes care for the display currently.
     */
    private Screen activeScreen;

    public ContainerHandler() {
        eventSubscriberOpenContainer = new EventSubscriber<OpenContainerEvent>() {
            @Override
            public void onEvent(final OpenContainerEvent event) {
                try {
                    if ((activeNifty == null) || (activeScreen == null)) {
                        return;
                    }

                    final ItemContainerBuilder builder = new ItemContainerBuilder("container" + event.getContainerId(),
                            "Tasche");
                    builder.slots(event.getSlotCount());
                    builder.slotDim(48, 48);
                    builder.width(builder.pixels(288));
                    final Element container = builder.build(activeNifty, activeScreen,
                            activeScreen.findElementByName("windows"));
                    final ItemContainer conControl = container.getNiftyControl(ItemContainer.class);

                    final TIntObjectIterator<OpenContainerEvent.Item> itr = event.getItemIterator();
                    while (itr.hasNext()) {
                        itr.advance();
                        final InventorySlot conSlot = conControl.getSlot(itr.key());
                        final int itemId = itr.value().getItemId();
                        final int count = itr.value().getCount();

                        if (itemId > 0) {
                            final Item displayedItem = ItemFactory.getInstance().getPrototype(itemId);

                            final NiftyImage niftyImage = new NiftyImage(activeNifty.getRenderEngine(),
                                    new EntitySlickRenderImage(displayedItem));

                            conSlot.setImage(niftyImage);
                            conSlot.setLabelText(Integer.toString(count));
                            if (count > 1) {
                                conSlot.showLabel();
                            } else {
                                conSlot.hideLabel();
                            }
                        } else {
                            conSlot.setImage(null);
                            conSlot.hideLabel();
                        }
                    }

                    container.getParent().layoutElements();
                } catch (final RuntimeException e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        };
    }

    @Override
    public void bind(final Nifty nifty, final Screen screen) {
        activeNifty = nifty;
        activeScreen = screen;
    }

    @Override
    public void onStartScreen() {
        EventBus.subscribe(OpenContainerEvent.class, eventSubscriberOpenContainer);
    }

    @Override
    public void onEndScreen() {
        EventBus.unsubscribe(OpenContainerEvent.class, eventSubscriberOpenContainer);
    }
}

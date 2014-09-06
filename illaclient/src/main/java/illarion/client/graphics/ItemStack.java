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
package illarion.client.graphics;

import illarion.client.input.AbstractMouseLocationEvent;
import illarion.client.world.World;
import illarion.common.types.Rectangle;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Graphics;
import org.illarion.engine.graphic.SceneElement;
import org.illarion.engine.graphic.SceneEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ItemStack implements DisplayItem, List<Item> {
    private static final Logger log = LoggerFactory.getLogger(ItemStack.class);
    private boolean shown;
    private final List<Item> items;

    private boolean rectangleDirty;
    private final Rectangle interactiveRectangle;

    private boolean orderNotSet;
    private int order;

    public ItemStack() {
        shown = false;
        items = new ArrayList<>();
        rectangleDirty = false;
        interactiveRectangle = new Rectangle();

        orderNotSet = true;
        order = 0;
    }

    @Override
    public void hide() {
        if (shown) {
            World.getMapDisplay().getGameScene().removeElement(this);
            shown = false;
        }
    }

    @Override
    public void show() {
        if (shown) {
            log.error("Added item stack {} twice.", this);
        } else {
            World.getMapDisplay().getGameScene().addElement(this);
            shown = true;
        }
    }

    private void updateInteractiveRectangle() {
        if (!rectangleDirty) {
            return;
        }
        rectangleDirty = false;
        interactiveRectangle.reset();
        for (Item item : items) {
            Rectangle itemRect = item.getInteractionRect();
            if (itemRect.isEmpty()) {
                rectangleDirty = true;
            }
            if (interactiveRectangle.isEmpty()) {
                interactiveRectangle.set(itemRect);
            } else {
                interactiveRectangle.add(itemRect);
            }
        }
    }

    private boolean isItemGoodForInsert(@Nonnull SceneElement newItem) {
        return orderNotSet || (newItem.getOrder() == order);
    }

    private void setOrder(int newOrder) {
        orderNotSet = false;
        order = newOrder;
    }

    private void unsetOrder() {
        orderNotSet = true;
    }

    private void postProcessItemInsert(@Nonnull Item newItem) {
        if (orderNotSet) {
            setOrder(newItem.getOrder());
            show();
        }
        rectangleDirty = true;
        newItem.show();
    }

    private void postProcessItemRemove(@Nonnull Item removedItem) {
        if (items.isEmpty()) {
            unsetOrder();
            hide();
        }
        rectangleDirty = true;
        removedItem.hide();
    }

    @Override
    public int getOrder() {
        return orderNotSet ? 0 : order;
    }

    @Override
    public void render(@Nonnull Graphics graphics) {
        for (Item item : items) {
            item.render(graphics);
        }
    }

    @Override
    public void update(@Nonnull GameContainer container, int delta) {
        int size = items.size();
        for (int i = 0; i < size; i++) {
            Item item = items.get(i);
            item.enableNumbers(i == (size - 1));
            item.update(container, delta);
        }
    }

    @Override
    public boolean isEventProcessed(
            @Nonnull GameContainer container, int delta, @Nonnull SceneEvent event) {
        if (orderNotSet) {
            return false;
        }

        if (event instanceof AbstractMouseLocationEvent) {
            AbstractMouseLocationEvent mouseEvent = (AbstractMouseLocationEvent) event;
            int mouseXonDisplay = mouseEvent.getX() + Camera.getInstance().getViewportOffsetX();
            int mouseYonDisplay = mouseEvent.getY() + Camera.getInstance().getViewportOffsetY();

            updateInteractiveRectangle();

            if (!interactiveRectangle.isInside(mouseXonDisplay, mouseYonDisplay)) {
                return false;
            }
        }

        for (int i = items.size() - 1; i >= 0; i--) {
            Item item = items.get(i);
            if (item.isEventProcessed(container, delta, event)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return items.contains(o);
    }

    @Nonnull
    @Override
    public Iterator<Item> iterator() {
        return items.iterator();
    }

    @Nonnull
    @Override
    public Object[] toArray() {
        return items.toArray();
    }

    @Nonnull
    @Override
    public <T> T[] toArray(@Nonnull T[] a) {
        return items.toArray(a);
    }

    @Override
    public boolean add(@Nonnull Item item) {
        if (!isItemGoodForInsert(item)) {
            throw new IllegalArgumentException("Item is not valid in this stack.");
        }
        if (items.add(item)) {
            postProcessItemInsert(item);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        if (items.remove(o)) {
            postProcessItemRemove((Item) o);
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(@Nonnull Collection<?> c) {
        return items.containsAll(c);
    }

    @Override
    public boolean addAll(@Nonnull Collection<? extends Item> c) {
        boolean result = false;
        for (Item item : c) {
            if (add(item)) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public boolean addAll(int index, @Nonnull Collection<? extends Item> c) {
        int indexCounter = index;
        for (Item item : c) {
            add(indexCounter, item);
            indexCounter++;
        }
        return indexCounter != index;
    }

    @Override
    public boolean removeAll(@Nonnull Collection<?> c) {
        boolean result = false;
        for (Object item : c) {
            if (remove(item)) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public boolean retainAll(@Nonnull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        items.clear();
        unsetOrder();
        hide();
    }

    @Override
    public Item get(int index) {
        return items.get(index);
    }

    @Override
    public Item set(int index, Item element) {
        if (!isItemGoodForInsert(element)) {
            throw new IllegalArgumentException("Item is not valid in this stack.");
        }
        Item oldItem = items.set(index, element);
        postProcessItemInsert(element);
        postProcessItemRemove(oldItem);
        return oldItem;
    }

    @Override
    public void add(int index, Item element) {
        if (!isItemGoodForInsert(element)) {
            throw new IllegalArgumentException("Item is not valid in this stack.");
        }
        items.add(index, element);
        postProcessItemInsert(element);
    }

    @Override
    public Item remove(int index) {
        Item removedItem = items.remove(index);
        postProcessItemRemove(removedItem);
        return removedItem;
    }

    @Override
    public int indexOf(Object o) {
        return items.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return items.lastIndexOf(o);
    }

    @Nonnull
    @Override
    public ListIterator<Item> listIterator() {
        return items.listIterator();
    }

    @Nonnull
    @Override
    public ListIterator<Item> listIterator(int index) {
        return items.listIterator(index);
    }

    @Nonnull
    @Override
    public List<Item> subList(int fromIndex, int toIndex) {
        return items.subList(fromIndex, toIndex);
    }
}

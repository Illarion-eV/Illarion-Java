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
package illarion.client.graphics;

import illarion.client.input.AbstractMouseLocationEvent;
import illarion.client.world.World;
import illarion.common.types.DisplayCoordinate;
import illarion.common.types.Rectangle;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Graphics;
import org.illarion.engine.graphic.SceneEvent;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ItemStack implements DisplayItem, List<Item> {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(ItemStack.class);
    private boolean shown;
    @Nonnull
    private final List<Item> items;

    private boolean rectangleDirty;
    @Nonnull
    private final Rectangle interactiveRectangle;

    @Nonnull
    private final DisplayCoordinate stackLocation;

    @Nonnull
    private final ReadWriteLock lock;

    public ItemStack(@Nonnull DisplayCoordinate location) {
        shown = false;
        items = new ArrayList<>();
        rectangleDirty = false;
        interactiveRectangle = new Rectangle();

        stackLocation = location;

        lock = new ReentrantReadWriteLock();
    }

    @Nonnull
    public ReadWriteLock getLock() {
        return lock;
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

    @Contract(pure = true)
    public boolean isShown() {
        return shown;
    }

    private void updateInteractiveRectangle() {
        if (!rectangleDirty) {
            return;
        }
        rectangleDirty = false;
        interactiveRectangle.reset();
        lock.readLock().lock();
        try {
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
        } finally {
            lock.readLock().unlock();
        }
    }

    public int getItemCount() {
        return items.size();
    }

    public int getElevation() {
        lock.readLock().lock();
        try {
            return getElevationForIndex(items.size() - 1);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Nonnull
    public Item getTopItem() {
        lock.readLock().lock();
        try {
            int count = getItemCount();
            if (count == 0) {
                throw new IllegalStateException("Requesting the top item of a empty item stack is not valid.");
            }
            return items.get(count - 1);
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean hasItems() {
        return !isEmpty();
    }

    private int getElevationForIndex(int index) {
        if (index == 0) {
            return 0;
        }

        lock.readLock().lock();
        try {
            int elevation = 0;
            for (int i = 0; i < index; i++) {
                Item itemAtIndex = get(i);
                elevation += itemAtIndex.getTemplate().getItemInfo().getLevel();
            }
            return elevation;
        } finally {
            lock.readLock().unlock();
        }
    }

    private void postProcessItemInsert(int index, @Nonnull Item newItem) {
        int elevation = getElevationForIndex(index);
        setScreenPos(newItem, elevation);
        if (newItem.getTemplate().getItemInfo().getLevel() != 0) {
            elevation += newItem.getTemplate().getItemInfo().getLevel();
            for (int i = index + 1; i < items.size(); i++) {
                Item item = get(i);
                setScreenPos(item, elevation);
                elevation += item.getTemplate().getItemInfo().getLevel();
            }
        }

        if (!shown) {
            show();
        }

        rectangleDirty = true;
        newItem.show(this);
    }

    private void postProcessItemRemove(@Nonnull Item removedItem) {
        if (items.isEmpty()) {
            hide();
        } else {
            if (removedItem.getTemplate().getItemInfo().getLevel() != 0) {
            /* Update the elevation of every item. */
                int elevation = 0;
                for (Item item : items) {
                    setScreenPos(item, elevation);
                    elevation += item.getTemplate().getItemInfo().getLevel();
                }
            }
        }

        rectangleDirty = true;
        removedItem.hide();
    }

    private void setScreenPos(@Nonnull Item item, int elevation) {
        if (elevation == 0) {
            item.setScreenPos(stackLocation);
        } else {
            item.setScreenPos(new DisplayCoordinate(stackLocation, 0, -1, 0));
        }
    }

    @Override
    public int getOrder() {
        return stackLocation.getLayer();
    }

    @Override
    public void render(@Nonnull Graphics graphics) {
        lock.readLock().lock();
        try {
            for (Item item : items) {
                item.render(graphics);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void update(@Nonnull GameContainer container, int delta) {
        lock.readLock().lock();
        try {
            int size = items.size();
            for (int i = 0; i < size; i++) {
                Item item = items.get(i);
                item.enableNumbers(i == (size - 1));
                item.update(container, delta);
            }
        } finally {
            lock.readLock().unlock();
        }

        updateInteractiveRectangle();
    }

    @Override
    public boolean isEventProcessed(@Nonnull GameContainer container, int delta, @Nonnull SceneEvent event) {
        if (interactiveRectangle.isEmpty()) {
            return false;
        }

        if (event instanceof AbstractMouseLocationEvent) {
            AbstractMouseLocationEvent mouseEvent = (AbstractMouseLocationEvent) event;
            int mouseXonDisplay = mouseEvent.getX() + Camera.getInstance().getViewportOffsetX();
            int mouseYonDisplay = mouseEvent.getY() + Camera.getInstance().getViewportOffsetY();

            if (!interactiveRectangle.isInside(mouseXonDisplay, mouseYonDisplay)) {
                return false;
            }
        }
        lock.readLock().lock();
        try {
            for (int i = items.size() - 1; i >= 0; i--) {
                Item item = items.get(i);
                if (item.isEventProcessed(container, delta, event)) {
                    return true;
                }
            }
        } finally {
            lock.readLock().unlock();
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
        lock.readLock().lock();
        try {
            return items.toArray();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Nonnull
    @Override
    public <T> T[] toArray(@Nonnull T[] a) {
        lock.readLock().lock();
        try {
            return items.toArray(a);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean add(@Nonnull Item e) {
        lock.writeLock().lock();
        try {
            if (items.add(e)) {
                postProcessItemInsert(items.size() - 1, e);
                return true;
            }
        } finally {
            lock.writeLock().unlock();
        }
        return false;
    }

    @Override
    public boolean remove(@Nonnull Object o) {
        lock.writeLock().lock();
        try {
            if (items.remove(o)) {
                postProcessItemRemove((Item) o);
                return true;
            }
        } finally {
            lock.writeLock().unlock();
        }
        return false;
    }

    @Override
    public boolean containsAll(@Nonnull Collection<?> c) {
        lock.readLock().lock();
        try {
            return items.containsAll(c);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean addAll(@Nonnull Collection<? extends Item> c) {
        boolean result = false;
        lock.writeLock().lock();
        try {
            for (Item item : c) {
                if (add(item)) {
                    result = true;
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
        return result;
    }

    @Override
    public boolean addAll(int index, @Nonnull Collection<? extends Item> c) {
        int indexCounter = index;
        lock.writeLock().lock();
        try {
            for (Item item : c) {
                add(indexCounter, item);
                indexCounter++;
            }
        } finally {
            lock.writeLock().unlock();
        }
        return indexCounter != index;
    }

    @Override
    public boolean removeAll(@Nonnull Collection<?> c) {
        boolean result = false;
        lock.writeLock().lock();
        try {
            for (Object item : c) {
                if (remove(item)) {
                    result = true;
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
        return result;
    }

    @Override
    public boolean retainAll(@Nonnull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            items.clear();
        } finally {
            lock.writeLock().unlock();
        }
        rectangleDirty = true;
        hide();
    }

    @Override
    @Nonnull
    public Item get(int index) {
        lock.readLock().lock();
        try {
            return items.get(index);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Nullable
    @Override
    public Item set(int index, @Nonnull Item element) {
        Item oldItem;
        lock.writeLock().lock();
        try {
            oldItem = items.set(index, element);
            postProcessItemInsert(index, element);
            if (oldItem != null) {
                postProcessItemRemove(oldItem);
            }
        } finally {
            lock.writeLock().unlock();
        }
        return oldItem;
    }

    @Override
    public void add(int index, @Nonnull Item element) {
        lock.writeLock().lock();
        try {
            items.add(index, element);
            postProcessItemInsert(index, element);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    @Nonnull
    public Item remove(int index) {
        Item removedItem;
        lock.writeLock().lock();
        try {
            removedItem = items.remove(index);
            assert removedItem != null;
            postProcessItemRemove(removedItem);
        } finally {
            lock.writeLock().unlock();
        }
        return removedItem;
    }

    @Override
    public int indexOf(Object o) {
        lock.readLock().lock();
        try {
            return items.indexOf(o);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        lock.readLock().lock();
        try {
            return items.lastIndexOf(o);
        } finally {
            lock.readLock().unlock();
        }
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
        lock.readLock().lock();
        try {
            return items.subList(fromIndex, toIndex);
        } finally {
            lock.readLock().unlock();
        }
    }
}

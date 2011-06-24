/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute i and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Mapeditor is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Mapeditor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.graphics;

import javolution.context.ObjectFactory;
import javolution.util.FastTable;

import illarion.mapedit.MapEditor;

import illarion.common.util.Reusable;

/**
 * This class is used to implement a stack of items. This is needed to ensure
 * the correct rendering of multiple items on one tile.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.00
 */
public final class ItemStack implements DisplayItem, Reusable {
    /**
     * The item stack factory is used to create, store and reuse the instances
     * of the item stack class.
     * 
     * @author Martin Karing
     * @since 1.00
     * @version 1.00
     */
    private static final class ItemStackFactory extends
        ObjectFactory<ItemStack> {
        /**
         * Create public constructor for this factory class, that is needed so
         * the parent class is able to create a instance of this class.
         */
        public ItemStackFactory() {
            super();
        }

        /**
         * Create a new instance of the item stack class.
         */
        @Override
        protected ItemStack create() {
            return new ItemStack();
        }
    }

    /**
     * The factory that is used to create and reuse the instances of this class.
     */
    private static final ItemStackFactory FACTORY = new ItemStackFactory();

    /**
     * This value stores if the entire item stack is currently displayed or not.
     */
    private boolean shown;

    /**
     * This is the list of items stored on the stack.
     */
    private FastTable<Item> stack;

    /**
     * Constructor to create a new instance of this item stack.
     */
    ItemStack() {
        super();
        shown = false;
    }

    /**
     * Get a instance of this class. This will either be a old reused one or a
     * newly created instance.
     * 
     * @return the instance of the ItemStack class that is now ready to be used
     */
    public static ItemStack getInstance() {
        return FACTORY.object();
    }

    /**
     * Add a single item to the stack. This does also set the state of the item
     * to the correct values.
     * 
     * @param addItem the item to add to this stack
     * @return <code>true</code>
     */
    public boolean add(final Item addItem) {
        if (stack == null) {
            stack = FastTable.newInstance();
        }
        if (shown) {
            addItem.show();
        } else {
            addItem.hide();
        }
        return stack.add(addItem);
    }

    /**
     * Draw all items on this stack in the order they were added.
     */
    @Override
    public boolean draw() {
        if (stack == null) {
            return false;
        }

        boolean result = false;
        final int count = stack.size();
        for (int i = 0; i < count; i++) {
            result |= stack.get(i).draw();
        }
        return result;
    }

    /**
     * This function fetches the z order value of the first item on this stack.
     * Since all items are on the same tile, the value is equal for all.
     */
    @Override
    public int getZOrder() {
        if ((stack == null) || stack.isEmpty()) {
            return 0;
        }
        return stack.get(0).getZOrder();
    }

    /**
     * Hide the entire item stack.
     */
    @Override
    public void hide() {
        if (shown) {
            shown = false;
            MapEditor.getDisplay().remove(this);
            if (stack != null) {
                final int count = stack.size();
                for (int i = 0; i < count; i++) {
                    stack.get(i).updateGraphic();
                }
            }
        }
    }

    /**
     * Put the instance of this class back into the recycler so it can be reused
     * later.
     */
    @Override
    public void recycle() {
        reset();
        FACTORY.recycle(this);
    }

    /**
     * Remove one item from the stack.
     * 
     * @param remItem the item to remove
     * @return <code>true</code> in case the item was removed
     */
    public boolean remove(final Item remItem) {
        if (stack == null) {
            return false;
        }

        final boolean result = stack.remove(remItem);
        if (stack.isEmpty()) {
            FastTable.recycle(stack);
            stack = null;
        }

        return result;
    }

    @Override
    public void reset() {
        hide();
        if (stack != null) {
            final int count = stack.size();
            for (int i = 0; i < count; i++) {
                stack.get(i).recycle();
            }
            stack.clear();
            FastTable.recycle(stack);
            stack = null;
        }
    }

    /**
     * Show the entire item stack.
     */
    @Override
    public void show() {
        if (!shown) {
            shown = true;
            MapEditor.getDisplay().add(this);
            if (stack != null) {
                final int count = stack.size();
                for (int i = 0; i < count; i++) {
                    stack.get(i).updateGraphic();
                }
            }
        }
    }
}

/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Common Library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Common Library. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This support class is a enumerator that works on arrays. It allows to be used
 * as any other enumerator.
 * 
 * @author Martin Karing
 * @version 1.22
 * @since 1.22
 * @param <T> the type of the array
 */
public final class ArrayEnumeration<T> implements Enumeration<T>, Iterator<T> {
    /**
     * The array this enumerator is using.
     */
    private final T[] array;

    /**
     * The index the enumerator is currently pointing at.
     */
    private int currentIndex;

    /**
     * The last index that is a valid target.
     */
    private int lastIndex;

    /**
     * Create a new instance of the array enumerator. Once created it can be
     * used to walk over the array content. This constructor is a shortcut that
     * allows creating a array enumerator that enumerates every element in the
     * array.
     * 
     * @param targetArray the array this enumerator is using
     */
    public ArrayEnumeration(final T[] targetArray) {
        this(targetArray, 0, targetArray.length);
    }

    /**
     * Create a new instance of the array enumerator. Once created it can be
     * used to walk over the array content.
     * 
     * @param targetArray the array this enumerator is using
     * @param startIndex the first valid instance of the array
     * @param length the amount of elements this enumerator shall enumerate
     */
    @SuppressWarnings("nls")
    public ArrayEnumeration(final T[] targetArray, final int startIndex,
        final int length) {
        if (targetArray == null) {
            throw new IllegalArgumentException(
                "The target array must not be NULL.");
        }
        if ((startIndex < 0) || (startIndex >= targetArray.length)) {
            throw new IllegalArgumentException(
                "The starting index is smaller then 0 or larger then the length of the array.");
        }
        if ((length < 0)
            || (((length + startIndex) - 1) >= targetArray.length)) {
            throw new IllegalArgumentException(
                "The length is either smaller then 0 or larger then the remaining elements in the array.");
        }
        currentIndex = startIndex;
        lastIndex = (startIndex + length) - 1;
        array = targetArray;
    }

    @Override
    public boolean hasMoreElements() {
        return (currentIndex <= lastIndex);
    }

    @Override
    public boolean hasNext() {
        return hasMoreElements();
    }

    @Override
    public T next() {
        return nextElement();
    }

    /**
     * @throws NoSuchElementException in case there are no elements remaining
     */
    @SuppressWarnings("nls")
    @Override
    public T nextElement() {
        if (!hasMoreElements()) {
            throw new NoSuchElementException("No elements left to get.");
        }
        return array[currentIndex++];
    }

    /**
     * Since arrays are unchangeable in length its impossible to remove a
     * element from the array. This method will throw a exception in all cases.
     * 
     * @throws UnsupportedOperationException in any case its called
     */
    @SuppressWarnings("nls")
    @Override
    public void remove() {
        throw new UnsupportedOperationException(
            "Removing array entries is not supported");
    }
}

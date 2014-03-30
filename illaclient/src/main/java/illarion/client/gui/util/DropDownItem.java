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
package illarion.client.gui.util;

/**
 * This is just a simple class to have dropdown item with a value that is visually displayed
 * and a hidden id to be used into the code.
 *
 * @author Stefano Bonicatti &lt;smjert@gmail.com&gt;
 */
public class DropDownItem<T> {
    private final String value;
    private T key;

    public DropDownItem(T key, String value) {
        this.key = key;
        this.value = value;
    }

    public T getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}

/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Mapeditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Mapeditor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.data;

import javolution.text.TextBuilder;
import javolution.util.FastList;

import java.io.*;
import java.util.Scanner;

/**
 * @author Tim
 */
public class ItemData {


    private FastList<Item> itemData;

    public ItemData() {
        itemData = new FastList<Item>();
    }

    public ItemData(ItemData old) {
        itemData = new FastList<Item>(old.itemData);
    }

    private void addItem(final Item item) {
        itemData.add(item);
    }

    private void removeItem(final Item item) {
        itemData.remove(item);
    }

    public static ItemData fromInputStream(final InputStream is) throws IOException {
        ItemData data = new ItemData();
        Scanner scanner = new Scanner(is);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            data.addItem(Item.fromString(line));
        }
        return data;
    }

    public void saveToFile(final File file) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
        TextBuilder builder = TextBuilder.newInstance();
        for (Item w : itemData) {
            builder.append(w.getX()).append(Map.DM);
            builder.append(w.getY()).append(Map.DM);
            builder.append(0).append(Map.DM);
            builder.append(w.getId()).append(Map.DM);
            builder.append(w.getItemData()).append(Map.DM);
            builder.append(
                    w.getQuality() == Item.QUALITY_NONE ?
                            Item.QUALITY_DEFAULT : w.getQuality()).append(Map.NL);
        }
        writer.write(builder.toString());
        writer.close();
        TextBuilder.recycle(builder);
    }
}

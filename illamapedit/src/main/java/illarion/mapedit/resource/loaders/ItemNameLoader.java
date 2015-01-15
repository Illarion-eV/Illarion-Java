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
package illarion.mapedit.resource.loaders;

import illarion.mapedit.Lang;
import illarion.mapedit.resource.Resource;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

/**
 * @author Fredrik K
 */
public class ItemNameLoader implements Resource {
    private static final String ITEM_NAME_URL = "http://illarion.org/data/itemnames.php";
    @Nonnull
    private static ItemNameLoader INSTANCE = new ItemNameLoader();

    private HashMap<Integer, String> itemNames;

    public static ItemNameLoader getInstance() {
        return INSTANCE;
    }

    private ItemNameLoader() {
        itemNames = new HashMap<>();
    }

    @Override
    public void load() throws IOException {
        URL url = new URL(ITEM_NAME_URL);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String[] entry = inputLine.trim().split(",");
                if (entry.length != 3) {
                    continue;
                }
                Integer id = Integer.parseInt(entry[0]);
                if (itemNames.containsKey(id)) {
                    continue;
                }
                String name;
                if (Lang.getInstance().isGerman()) {
                    name = entry[1];
                } else {
                    name = entry[2];
                }
                name = name.trim().replace("\"", "").toLowerCase();
                name = Character.toString(name.charAt(0)).toUpperCase() + name.substring(1);
                itemNames.put(id, name);
            }
            in.close();
        } catch (FileNotFoundException ignored) {

        }
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Item names";
    }

    public String getItemName(int itemID) {
        if (!itemNames.containsKey(itemID)) {
            return null;
        }
        return itemNames.get(itemID);
    }
}

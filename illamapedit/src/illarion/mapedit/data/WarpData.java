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
import java.util.List;
import java.util.Scanner;

/**
 * @author Tim
 */
public class WarpData {

    private List<Warp> warpData;

    public WarpData() {
        warpData = new FastList<Warp>();
    }

    public WarpData(WarpData old) {
        warpData = new FastList<Warp>(old.warpData);
    }

    private void addWarp(Warp warp) {
        warpData.add(warp);
    }

    private void removeWarp(Warp warp) {
        warpData.remove(warp);
    }

    public static WarpData fromInputStream(final InputStream is) throws IOException {
        WarpData data = new WarpData();
        Scanner scanner = new Scanner(is);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            data.addWarp(Warp.fromString(line));
        }

        return data;
    }

    public void saveToFile(final File file) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
        TextBuilder builder = TextBuilder.newInstance();
        for (Warp w : warpData) {
            builder.append(w.getXStart()).append(Map.DM);
            builder.append(w.getYStart()).append(Map.DM);
            builder.append(w.getXTarget()).append(Map.DM);
            builder.append(w.getYTarget()).append(Map.DM);
            builder.append(w.getZTarget()).append(Map.NL);
        }
        writer.write(builder.toString());
        writer.close();
        TextBuilder.recycle(builder);
    }
}

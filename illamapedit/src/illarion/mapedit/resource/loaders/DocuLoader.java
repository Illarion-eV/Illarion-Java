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
package illarion.mapedit.resource.loaders;

import illarion.mapedit.resource.Resource;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @author Tim
 */
public class DocuLoader implements Resource {
    private static final Logger LOGGER = Logger.getLogger(DocuLoader.class);

    private static final String BASE = "/docu/";

    @Override
    public void load() throws IOException {

    }

//    private void loadFolder(final Folder parent, final String[] files) {
//        for (final String file : files) {
//            try {
//                final File f;
//                if (file.endsWith(".html")) {
//                    f = new File(getFilename(file), file);
//                } else {
//                    f = new Folder(getFilename(file));
//                    final BufferedReader reader =
//                            new BufferedReader(
//                                    new InputStreamReader(
//                                            CLASS_LOADER.getResourceAsStream(parent.getPath() + "/list.txt")));
//                    String line;
//                    final List<String> s = new FastList<String>();
//                    while ((line = reader.readLine()) != null) {
//                        if (line.endsWith(".html")) {
//                            s.add(line);
//                        }
//                    }
//                    loadFolder((Folder) f, s.toArray(new String[s.size()]));
//                }
//                parent.addFile(f);
//            } catch (IOException e) {
//                LOGGER.warn("Cant load list.txt file for " + file, e);
//            }
//        }
//    }

    @Override
    public String getDescription() {
        return "Documentation";
    }
}

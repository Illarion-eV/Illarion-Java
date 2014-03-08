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

import illarion.mapedit.Lang;
import illarion.mapedit.resource.Resource;
import javolution.util.FastTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author Tim
 */
public class DocuLoader implements Resource, TreeModel {

    @Nonnull
    public static DocuLoader getInstance() {
        return INSTANCE;
    }

    public static class Folder extends File {

        private final List<File> files = new FastTable<>();

        private Folder(final String name, final String path) {
            super(name, path);
        }

        public void addFile(final File file) {
            files.add(file);
        }

        @Override
        public boolean isFile() {
            return false;
        }

        @Nonnull
        public List<File> getFiles() {
            return files;
        }
    }

    public static class File {

        private final String name;
        private final String path;

        private File(final String name, final String path) {
            this.name = name;
            this.path = path;
        }

        public String getName() {
            return name;
        }

        public boolean isFile() {
            return true;
        }

        public String getPath() {
            return path;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static final DocuLoader INSTANCE = new DocuLoader();
    private static final Logger LOGGER = LoggerFactory.getLogger(DocuLoader.class);

    private static final String BASE = "/docu/";

    private final Folder root = new Folder("Docu", BASE + ((Lang.getInstance().isGerman()) ? "de" : "en"));

    @Override
    public void load() throws IOException {
        final String lang = (Lang.getInstance().isGerman()) ? "de" : "en";
        loadFolder(root);
    }

    @Nonnull
    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(final Object parent, final int index) {
        final File f = (File) parent;
        if (f.isFile()) {
            return f;
        }
        return ((Folder) f).getFiles().get(index);
    }

    @Override
    public int getChildCount(final Object parent) {
        final File f = (File) parent;
        if (f.isFile()) {
            return 0;
        }
        return ((Folder) f).getFiles().size();
    }

    @Override
    public boolean isLeaf(final Object node) {
        final File f = (File) node;
        return f.isFile();
    }

    @Override
    public void valueForPathChanged(final TreePath path, final Object newValue) {
    }

    @Override
    public int getIndexOfChild(final Object parent, final Object child) {
        final Folder f = (Folder) parent;
        return f.getFiles().indexOf(child);
    }

    @Override
    public void addTreeModelListener(final TreeModelListener l) {
    }

    @Override
    public void removeTreeModelListener(final TreeModelListener l) {
    }

    private static void loadFolder(@Nonnull final Folder parent) throws IOException {
        final String[] files = loadList(parent);
        for (final String file : files) {
            if (!isFile(file)) {
                final Folder f = new Folder(getFolderName(file), file);
                parent.addFile(f);
                loadFolder(f);
                return;
            }
            final File f = new File(getFileName(file), file);
            parent.addFile(f);
        }
    }

    @Nonnull
    private static String getFileName(@Nonnull final String file) {
        final int i = file.lastIndexOf('/') + 1;
        return file.substring(i, file.lastIndexOf(".html"));
    }

    @Nonnull
    private static String[] loadList(@Nonnull final Folder parent) throws IOException {
        final String path = parent.getPath() + "/list.txt";
        final InputStream is = DocuLoader.class.getResourceAsStream(path);
        if (is == null) {
            LOGGER.warn("Can't load docu from " + path);
            return new String[0];
        }
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        final List<String> stringList = new FastTable<>();
        String s;
        while ((s = reader.readLine()) != null) {
            stringList.add(s);
        }
        return stringList.toArray(new String[stringList.size()]);
    }

    @Nonnull
    public static String getFolderName(@Nonnull final String f) {
        final String folder;

        if (f.endsWith("/")) {
            folder = f.substring(0, f.length());
        } else {
            folder = f;
        }

        final int i = folder.lastIndexOf('/');
        return folder.substring(i + 1);
    }

    private static boolean isFile(@Nonnull final String s) {
        return s.endsWith(".html");
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Documentation";
    }
}

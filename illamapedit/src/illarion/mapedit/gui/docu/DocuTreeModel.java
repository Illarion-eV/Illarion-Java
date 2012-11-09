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
package illarion.mapedit.gui.docu;

import illarion.mapedit.Lang;
import javolution.util.FastList;
import org.apache.log4j.Logger;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tim
 */

/**
 * The methods in this class allow the JTree component to traverse
 * the file system tree, and display the files and directories.
 */
public class DocuTreeModel implements TreeModel {

    private static class Folder extends File {

        private final List<File> files = new FastList<File>();

        private Folder(final String name) {
            super(name, null);
        }

        public void addFile(final File file) {
            files.add(file);
        }

        @Override
        public boolean isFile() {
            return false;
        }
    }

    private static class File {

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
    }

    private static final Logger LOGGER = Logger.getLogger(DocuTreeModel.class);
    private static final Pattern FILENAME_PATTERN = Pattern.compile("([^/]+)$");
    private static final ClassLoader CLASS_LOADER = DocuTreeModel.class.getClassLoader();

    protected File root;

    public DocuTreeModel() {
        final String baseDir = "/docu/" + ((Lang.getInstance().isGerman()) ? "de" : "en");

    }


    private String getFilename(final CharSequence path) {
        final Matcher matcher = FILENAME_PATTERN.matcher(path);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    @Override
    public Object getRoot() {
        if (root == null) {
            return Lang.getMsg("gui.docu.IOError");
        }
        return root;
    }

    @Override
    public boolean isLeaf(final Object node) {
        return (root == null) || ((File) node).isFile();
    }

    @Override
    public int getChildCount(final Object parent) {
        return 0;
    }

    @Override
    public Object getChild(final Object parent, final int index) {
        return 0;
    }

    @Override
    public int getIndexOfChild(final Object parent, final Object child) {
        return 0;
    }

    @Override
    public void valueForPathChanged(final TreePath path, final Object newvalue) {
    }

    @Override
    public void addTreeModelListener(final TreeModelListener l) {
    }

    @Override
    public void removeTreeModelListener(final TreeModelListener l) {
    }
}

/*
 * This file is part of the Illarion easyNPC Editor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion easyNPC Editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion easyNPC Editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion easyNPC Editor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easynpc.gui;

import illarion.common.util.Base64;
import illarion.easynpc.EasyNpcScript;
import illarion.easynpc.Lang;
import illarion.easynpc.ParsedNpc;
import illarion.easynpc.ScriptWriter;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * A small set of static utility functions that help at some points.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
final class Utils {
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    @Nullable
    public static ResizableIcon getResizableIconFromResource(
            final String resource) {
        Image image;
        try {
            image = ImageIO.read(Utils.class.getClassLoader().getResource(resource));
        } catch (@Nonnull final IOException e) {
            LOGGER.error("Failed to read image: \"" + resource + "\"");
            return null;
        }
        final int height = image.getHeight(null);
        final int width = image.getWidth(null);
        final ResizableIcon resizeIcon = ImageWrapperResizableIcon.getIcon(image, new Dimension(width, height));
        return resizeIcon;
    }

    @Nullable
    @SuppressWarnings("nls")
    @Deprecated
    public static ResizableIcon getResizableIconFromResource(
            final String resource, final int dim) {
        return getResizableIconFromResource(resource);
    }

    /**
     * Parse the script shown in this editor but don't do anything with it but
     * showing the errors.
     *
     * @param editor the editor that contains the script to parse
     */
    public static void reparseSilent(@Nonnull final Editor editor) { // NO_UCD
        editor.getParsedData();
    }

    /**
     * Open a script from a file and display it in a new editor.
     *
     * @param file the file that is the source of this script
     */
    protected static void openScript(@Nonnull final Path file) {
        try {
            final int editorIndex = MainFrame.getInstance().alreadyOpen(file);
            if (editorIndex > -1) {
                MainFrame.getInstance().setCurrentEditorTab(editorIndex);
                return;
            }
            final EasyNpcScript easyScript = new EasyNpcScript(file);
            MainFrame.getInstance().addNewScript().loadScript(easyScript);
            MainFrame.getInstance().setCurrentTabTitle(file.getFileName().toString());
            Config.getInstance().addLastOpenedFile(file);
        } catch (@Nonnull final IOException e1) {
            LOGGER.error("Reading the script failed.", e1); //$NON-NLS-1$
        }
    }

    /**
     * Parse the script again and write the new state to the display in case
     * parsing the script was successfully. Else the errors are displayed.
     *
     * @param editor the editor that contains the script to parse
     */
    @SuppressWarnings("nls")
    protected static void reparseScript(@Nonnull final Editor editor) {
        final ParsedNpc npc = editor.getParsedData();
        if (npc == null) {
            return;
        }

        StringWriter writer = null;
        try {
            writer = new StringWriter();
            final ScriptWriter scriptWriter = new ScriptWriter();
            scriptWriter.setSource(npc);
            scriptWriter.setTargetLanguage(ScriptWriter.ScriptWriterTarget.EasyNPC);
            scriptWriter.setWritingTarget(writer);
            scriptWriter.write();
        } catch (@Nonnull final IOException e) {
            LOGGER.error("Error occured while writing a script.", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (@Nonnull final IOException ignored) {
                    // nothing
                }
            }
        }

        EasyNpcScript newScript = new EasyNpcScript();
        newScript.readNPCScript(writer.toString());
        editor.setScriptText(newScript);
    }

    /**
     * Save a script in the editor as easyNPC script. This saves the current
     * state of the script and does not parse it again.
     *
     * @param editor the editor that supplies the script text
     */
    protected static void saveEasyNPC(@Nonnull final Editor editor) {
        final Path targetFile = editor.getScriptFile();
        if (targetFile == null) {
            selectAndSaveEasyNPC(editor);
            return;
        }

        saveEasyNPCImpl(editor, targetFile);
        editor.saved();
    }

    /**
     * Save the script in the editor as a LUA script. This results in parsing
     * the script and in case this went fine the user is ordered to select a
     * location to save the LUA version of the script.
     *
     * @param editor the editor containing the original script.
     */
    @SuppressWarnings("nls")
    protected static void saveLuaScript(@Nonnull final Editor editor) {
        final ParsedNpc npc = editor.getParsedData();
        if (npc == null) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(), Lang.getMsg(Utils.class, "saveLuaErrors"),
                                          Lang.getMsg(Utils.class, "saveLuaErrorsTitle"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        final JFileChooser fileDiag = new JFileChooser();
        fileDiag.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(@Nonnull final File f) {
                return !f.isFile() || f.getName().endsWith(".lua"); //$NON-NLS-1$
            }

            @Override
            public String getDescription() {
                return Lang.getMsg(Utils.class, "luaScriptsFileType"); //$NON-NLS-1$
            }
        });
        fileDiag.setCurrentDirectory(new File(Config.getInstance().getLuaNpcFolder()));
        fileDiag.setSelectedFile(
                new File(Config.getInstance().getLuaNpcFolder() + File.separator + npc.getLuaFilename()));

        final int fileReturn = fileDiag.showSaveDialog(MainFrame.getInstance());
        if (fileReturn == JFileChooser.APPROVE_OPTION) {
            final File targetFile = fileDiag.getSelectedFile();
            final File backupFile = new File(targetFile.getAbsolutePath() + ".bak");

            if (targetFile.exists()) {
                targetFile.renameTo(backupFile);
            }

            try {
                final ScriptWriter writer = new ScriptWriter();
                writer.setTargetLanguage(ScriptWriter.ScriptWriterTarget.LUA);
                writer.setSource(npc);
                final Writer outputWriter = new OutputStreamWriter(new FileOutputStream(targetFile), "ISO-8859-1");
                writer.setWritingTarget(outputWriter);
                writer.write();
                outputWriter.close();
                if (backupFile.exists()) {
                    backupFile.delete();
                }
            } catch (@Nonnull final IOException ex) {
                if (backupFile.exists()) {
                    if (targetFile.exists()) {
                        targetFile.delete();
                    }
                    backupFile.renameTo(targetFile);
                }
            }
        }
    }

    /**
     * Display a file selection dialog to allow the player to select a easyNPC
     * script that is load after.
     */
    protected static void selectAndOpenScript() {
        final JFileChooser fileDiag = new JFileChooser();
        fileDiag.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(@Nonnull final File f) {
                return !f.isFile() || f.getName().endsWith(".npc"); //$NON-NLS-1$
            }

            @Override
            public String getDescription() {
                return Lang.getMsg(Utils.class, "easyNpcScriptsFileType"); //$NON-NLS-1$
            }
        });
        fileDiag.setCurrentDirectory(new File(Config.getInstance().getEasyNpcFolder()));

        final int fileReturn = fileDiag.showOpenDialog(MainFrame.getInstance());
        if (fileReturn == JFileChooser.APPROVE_OPTION) {
            final File selectedFile = fileDiag.getSelectedFile();
            openScript(selectedFile.toPath());
        }
    }

    /**
     * Allow the user to select the folder and the file name where to store the
     * easyNPC script. Once selected the function saves the current state of the
     * script.
     *
     * @param editor the editor that supplies the script text
     */
    protected static void selectAndSaveEasyNPC(@Nonnull final Editor editor) {
        final JFileChooser fileDiag = new JFileChooser();
        fileDiag.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(@Nonnull final File f) {
                return !f.isFile() || f.getName().endsWith(".npc"); //$NON-NLS-1$
            }

            @Override
            public String getDescription() {
                return Lang.getMsg(Utils.class, "easyNpcScriptsFileType"); //$NON-NLS-1$
            }
        });
        fileDiag.setCurrentDirectory(new File(Config.getInstance().getEasyNpcFolder()));
        Path scriptFile = editor.getScriptFile();
        fileDiag.setSelectedFile(scriptFile == null ? null : scriptFile.toFile());
        final int fileReturn = fileDiag.showSaveDialog(MainFrame.getInstance());
        if (fileReturn == JFileChooser.APPROVE_OPTION) {
            String targetFile = fileDiag.getSelectedFile().getAbsolutePath();
            if (!targetFile.endsWith(".npc")) {
                targetFile += ".npc";
            }

            final Path realTargetFile = Paths.get(targetFile);
            saveEasyNPCImpl(editor, realTargetFile);
            editor.setLoadScriptFile(realTargetFile);
            MainFrame.getInstance().setTabTitle(editor, realTargetFile.getFileName().toString());
        }
        editor.saved();
    }

    @SuppressWarnings("nls")
    protected static void uploadLuaScript(@Nonnull final Editor editor) {
        final ParsedNpc npc = editor.getParsedData();
        if (npc == null) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(), Lang.getMsg(Utils.class, "uploadLuaErrors"),
                                          Lang.getMsg(Utils.class, "uploadLuaErrorsTitle"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        Writer output = null;
        Reader input = null;
        try {
            final URL url = new URL("http://illarion.org/~nitram/test_npc.php");
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);

            final ScriptWriter writer = new ScriptWriter();
            writer.setTargetLanguage(ScriptWriter.ScriptWriterTarget.LUA);
            writer.setSource(npc);
            final StringWriter stringWriter = new StringWriter();
            writer.setWritingTarget(stringWriter);
            writer.write();
            final String script = stringWriter.toString();
            final String base64Script = Base64.encode(script, "UTF-8");
            final String fixedScript = base64Script.replace('\\', '_').replace('+', '-').replace('=', '*');

            final String query = "script=" + URLEncoder.encode(fixedScript, "UTF-8");

            output = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
            output.write(query);
            output.flush();

            input = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            input.close();

            JOptionPane.showMessageDialog(MainFrame.getInstance(), Lang.getMsg(Utils.class, "luaUploadInfos"),
                                          Lang.getMsg(Utils.class, "luaUploadInfosTitle"),
                                          JOptionPane.INFORMATION_MESSAGE);
        } catch (@Nonnull final IOException ex) {
            LOGGER.error("Connection to host failed", ex);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (@Nonnull final IOException e) {
                    // nothing
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (@Nonnull final IOException e) {
                    // nothing
                }
            }
        }
    }

    /**
     * The private implementation of the of the save easyScript function. This
     * writes a text to a file using the correct encoding.
     *
     * @param editor the editor supplying the script
     * @param targetFile the file that is the target of this writing operation
     */
    private static void saveEasyNPCImpl(@Nonnull final Editor editor, @Nonnull final Path targetFile) {
        try {
            Path backupFile = Files.createTempFile(targetFile.getParent(), "npc_", ".bak");
            if (Files.isReadable(targetFile)) {
                Files.copy(targetFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.deleteIfExists(backupFile);
                backupFile = null;
            }

            try (Writer bufferedWriter = Files.newBufferedWriter(targetFile, EasyNpcScript.DEFAULT_CHARSET)) {
                final ParsedNpc npc = editor.getParsedData();
                if (npc == null) {
                    final String scriptText = editor.getScriptText();
                    bufferedWriter.write(scriptText);
                } else {
                    ScriptWriter writer = new ScriptWriter();
                    writer.setGenerated(false);
                    writer.setSource(npc);
                    writer.setWritingTarget(bufferedWriter);
                    writer.setTargetLanguage(ScriptWriter.ScriptWriterTarget.EasyNPC);
                    writer.write();
                }
            } catch (@Nonnull final Exception e) {
                if (backupFile != null) {
                    Files.copy(backupFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            if (backupFile != null) {
                Files.deleteIfExists(backupFile);
            }
        } catch (@Nonnull final Exception e) {
            LOGGER.error("Writing the easyNPC Script failed.", e); //$NON-NLS-1$
        }
    }
}

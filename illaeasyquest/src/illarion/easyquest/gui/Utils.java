package illarion.easyquest.gui;

import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import illarion.easyquest.Lang;

final class Utils {

    private static final Logger LOGGER = Logger.getLogger(Utils.class);

    public static ResizableIcon getResizableIconFromResource(
        final String resource) {
        Image image;
        try {
            image =
                ImageIO.read(Utils.class.getClassLoader()
                    .getResource(resource));
        } catch (final IOException e) {
            LOGGER.error("Failed to read image: \"" + resource + "\"");
            return null;
        }
        final int height = image.getHeight(null);
        final int width = image.getWidth(null);
        final ResizableIcon resizeIcon =
            ImageWrapperResizableIcon.getIcon(image, new Dimension(width,
                height));
        return resizeIcon;
    }
    
    protected static void saveEasyQuest(final Editor editor) {
        final File file = editor.getQuestFile();
        if (file == null) {
            selectAndSaveEasyQuest(editor);
            return;
        }

        final String quest = editor.getQuestXML();
        saveEasyQuestImpl(quest, file);
        editor.saved();
    }
    
    protected static void selectAndSaveEasyQuest(final Editor editor) {
        final String quest = editor.getQuestXML();
        final JFileChooser fileDiag = new JFileChooser();
        fileDiag.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(final File f) {
                return !f.isFile() || f.getName().endsWith(".quest"); //$NON-NLS-1$
            }

            @Override
            public String getDescription() {
                return Lang.getMsg(Utils.class, "easyQuestFileType"); //$NON-NLS-1$
            }
        });
        //fileDiag.setCurrentDirectory(new File(Config.getInstance()
        //    .getEasyNpcFolder()));
        fileDiag.setSelectedFile(editor.getQuestFile());
        final int fileReturn =
            fileDiag.showSaveDialog(MainFrame.getInstance());
        if (fileReturn == JFileChooser.APPROVE_OPTION) {
            final File targetFile = fileDiag.getSelectedFile();
            saveEasyQuestImpl(quest, targetFile);
            editor.setQuestFile(targetFile);
            MainFrame.getInstance().setTabTitle(editor, targetFile.getName());
        }
        editor.saved();
    }
    
    private static void saveEasyQuestImpl(final String quest,
        final File targetFile) {
        final File backupFile =
            new File(targetFile.getAbsolutePath() + ".bak"); //$NON-NLS-1$
            
        FileWriter fw = null;
        try {
            if (backupFile.exists()) {
                backupFile.delete();
            }
            if (targetFile.exists()) {
                targetFile.renameTo(backupFile);
            }

            fw = new FileWriter(targetFile);
			fw.write(quest);

            if (backupFile.exists()) {
                backupFile.delete();
            }
        } catch (final Exception e) {
            if (backupFile.exists()) {
                if (targetFile.exists()) {
                    targetFile.delete();
                }
                backupFile.renameTo(targetFile);
            }
            //LOGGER.error("Writing the easyNPC Script failed.", e); //$NON-NLS-1$
        } finally {
            if ( fw != null ) 
                try { fw.close(); } catch ( IOException e ) { } 
        }
    }
}
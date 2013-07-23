/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2013 - Illarion e.V.
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
package illarion.mapedit.gui;

import illarion.common.config.Config;
import illarion.common.config.ConfigChangedEvent;
import illarion.common.config.ConfigDialog;
import illarion.common.config.ConfigSystem;
import illarion.common.config.entries.CheckEntry;
import illarion.common.config.entries.DirectoryEntry;
import illarion.common.config.entries.SelectEntry;
import illarion.common.util.DirectoryManager;
import illarion.mapedit.Lang;
import javolution.util.FastTable;
import org.apache.log4j.Logger;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventTopicSubscriber;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.SkinInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Locale;
import java.util.Map;

/**
 * @author Fredrik K
 */
public class MapEditorConfig {
    private static final Logger LOGGER = Logger.getLogger(MapEditorConfig.class);
    private static final String DEFAULT_LOOK_AND_FEEL = "org.pushingpixels.substance.api.skin.OfficeSilver2007Skin";
    private static final String[] LANGUAGES = {"English", "German", ""};
    private static final MapEditorConfig INSTANCE = new MapEditorConfig();

    private static final String MAPEDIT_FOLDER = "mapeditFolder";
    private static final String USE_WINDOW_DECO = "useWindowDeco";
    private static final String USED_LOOK_AND_FEEL = "usedLookAndFeel";
    private static final String USED_LANGUAGE = "usedLanguage";
    private static final String SHOW_MAP_POSITION = "showMapPosition";

    @Nullable
    private ConfigSystem configSystem;

    /**
     * Private constructor to ensure that no instance but the singleton instance
     * is created.
     */
    private MapEditorConfig() {
        AnnotationProcessor.process(this);
    }

    /**
     * Get the singleton instance of this class.
     *
     * @return the singleton instance
     */
    @Nonnull
    public static MapEditorConfig getInstance() {
        return INSTANCE;
    }

    /**
     * Create a ConfigDialog
     *
     * @return the dialog
     */
    public ConfigDialog createDialog() {
        if (configSystem == null) {
            throw new IllegalStateException("Configuration system not initialized yet.");
        }
        final ConfigDialog dialog = new ConfigDialog();
        dialog.setConfig(configSystem);
        dialog.setMessageSource(Lang.getInstance());
        dialog.setDisplaySystem(ConfigDialog.DISPLAY_SWING);

        final ConfigDialog.Page generalPage = new ConfigDialog.Page("gui.config.generalTab");
        generalPage.addEntry(new ConfigDialog.Entry("gui.config.MapeditFolderLabel", new DirectoryEntry(MAPEDIT_FOLDER,
                null)));

        generalPage.addEntry(new ConfigDialog.Entry("gui.config.language", new SelectEntry(USED_LANGUAGE,
                SelectEntry.STORE_VALUE, LANGUAGES)));

        generalPage.addEntry(new ConfigDialog.Entry("gui.config.showMapPostion", new CheckEntry(SHOW_MAP_POSITION)));

        dialog.addPage(generalPage);

        final ConfigDialog.Page lookAndFeelPage = new ConfigDialog.Page("gui.config.lookAndFeelTab");
        lookAndFeelPage.addEntry(new ConfigDialog.Entry("gui.config.useWindowDecoLabel", new CheckEntry(USE_WINDOW_DECO)));

        final FastTable<String> themeObject = FastTable.newInstance();
        final FastTable<String> themeLabel = FastTable.newInstance();

        for (final Map.Entry<String, SkinInfo> skin : SubstanceLookAndFeel.getAllSkins().entrySet()) {
            themeObject.add(skin.getValue().getClassName());
            themeLabel.add(skin.getValue().getDisplayName());
        }

        lookAndFeelPage.addEntry(new ConfigDialog.Entry("gui.config.usedThemeLabel", new SelectEntry(USED_LOOK_AND_FEEL,
                SelectEntry.STORE_VALUE, themeObject.toArray(), themeLabel.toArray(new String[themeLabel.size()]))));

        dialog.addPage(lookAndFeelPage);
        FastTable.recycle(themeObject);

        FastTable.recycle(themeLabel);

        return dialog;
    }

    /**
     * Get the the language for the editor
     * @return Locale
     */
    public Locale getLanguage() {
        if (configSystem == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return getDefaultLanguage();
        }
        final String language = configSystem.getString(USED_LANGUAGE);
        if ((language != null) && language.equals(LANGUAGES[0])) {
            return Locale.ENGLISH;
        }
        if ((language != null) && language.equals(LANGUAGES[1])) {
            return Locale.GERMAN;
        }
        return getDefaultLanguage();
    }

    private static Locale getDefaultLanguage() {
        Locale locale = Locale.getDefault();
        if (locale.getLanguage().equalsIgnoreCase(Locale.GERMAN.getLanguage())) {
            locale = Locale.GERMAN;
        } else {
            locale = Locale.ENGLISH;
        }
        return locale;
    }

    public boolean isShowPosition() {
        if (configSystem == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return true;
        }
        return configSystem.getBoolean(SHOW_MAP_POSITION);
    }

    @EventTopicSubscriber(topic = USED_LOOK_AND_FEEL)
    public void onConfigChanged(@Nonnull final String topic, final ConfigChangedEvent event) {
        if (topic.equals(USED_LOOK_AND_FEEL)) {
            SubstanceLookAndFeel.setSkin(getLookAndFeel());
            SwingUtilities.updateComponentTreeUI(MainFrame.getInstance());
        }
    }

    /**
     * Get the look and feel that shall be used.
     *
     * @return the class path of the look and feel that shall be used
     */
    @Nonnull
    public String getLookAndFeel() {
        if (configSystem == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return DEFAULT_LOOK_AND_FEEL;
        }
        final String lookAndFeel = configSystem.getString(USED_LOOK_AND_FEEL);
        if (lookAndFeel == null) {
            return DEFAULT_LOOK_AND_FEEL;
        }
        return lookAndFeel;
    }

    /**
     * Init the config system
     */
    public void init() {
        final String userDir = checkFolder();
        configSystem = new ConfigSystem(userDir + File.separator + "MapEdit.xcfgz");

        configSystem.setDefault(MAPEDIT_FOLDER, new File(System.getProperty("user.home")));

    }

    /**
     * This function determines the user data directory and requests the folder
     * to store the client data in case it is needed. It also performs checks to
     * see if the folder is valid.
     *
     * @return a string with the path to the folder or null in case no folder is
     *         set
     */
    private static String checkFolder() {
        if (!DirectoryManager.getInstance().hasUserDirectory()) {
            SplashScreen.getInstance().setVisible(false);
            JOptionPane.showMessageDialog(null,
                    "Installation ist fehlerhaft. Bitte neu ausführen.\n\n"
                            + "Installation is corrupted, please run it again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        final File userDir = DirectoryManager.getInstance().getUserDirectory();
        assert userDir != null;
        return userDir.getAbsolutePath();
    }

    /**
     * Set the size of the window
     * @param windowSize size of the window
     */
    public void setWindowSize(final Dimension windowSize) {
        if (configSystem == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return;
        }
            configSystem.set("windowSizeW", windowSize.width);
            configSystem.set("windowSizeH", windowSize.height);
    }

    /**
     * Get the last saved size of the window
     *
     * @return last saved size of the window
     */
    public Dimension getWindowSize() {
        if (configSystem == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return new Dimension(0,0);
        }
        return new Dimension(configSystem.getInteger("windowSizeW"), configSystem.getInteger("windowSizeH"));
    }

    /**
     * Save the configuration file to the filesystem of the local system.
     */
    public void save() {
        if (configSystem == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return;
        }
        configSystem.save();
    }

    /**
     * Get the internal used config object.
     *
     * @return the internal used config object
     */
    @Nullable
    public Config getInternalCfg() {
        return configSystem;
    }

    /**
     * Get the folder where to store the maps.
     *
     * @return the folder to store the maps
     */
    public File getMapFolder() {
        if (configSystem == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return new File(System.getProperty("user.home"));
        }
        final File mapFolder = configSystem.getFile(MAPEDIT_FOLDER);
        if (mapFolder == null) {
            return new File(System.getProperty("user.home"));
        }
        return mapFolder;
    }

    /**
     * Set the folder where to store the maps.
     *
     * @param newFolder the folder where to store the maps
     */
    public void setMapFolder(final File newFolder) {
        if (configSystem == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return;
        }
        configSystem.set(MAPEDIT_FOLDER, newFolder);
    }

    /**
     * Get the flag if the editor is supposed to decorate the windows.
     *
     * @return {@code true} in case the editor is expected to decorate the
     *         windows
     */
    public boolean isUseWindowDecoration() {
        if (configSystem == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return true;
        }
        return configSystem.getBoolean(USE_WINDOW_DECO);
    }
}

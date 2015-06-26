/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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

import illarion.mapedit.resource.Resource;
import javolution.util.FastMap;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author Tim
 */
public class ImageLoader implements Resource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageLoader.class);
    private static final ImageLoader INSTANCE = new ImageLoader();
    private static final Map<String, Image> IMAGES = new FastMap<>();

    private static final String[] FILES = {"sound.png", "messagebox_critical.png", "singleSelect.png", "viewmag.png",
                                           "viewmag1.png", "viewmag-.png", "viewmag+.png", "mapedit64.png",
                                           "fileopen.png", "filenew.png", "filesave.png", "editcopy.png",
                                           "editpaste.png", "editcut.png", "file_tiles.png", "file_items.png",
                                           "messagebox_warning.png", "viewGrid.png", "reload.png", "undo.png",
                                           "redo.png", "close.png", "help.png", "player_play.png", "edit_add.png",
                                           "edit_remove.png", "1uparrow.png", "1downarrow.png", "render.png",
                                           "configure.png", "info.png", "annotation.png"};

    private ImageLoader() {

    }

    @Override
    public void load() throws IOException {
        Class<?> clazz = ImageLoader.class;
        for (String file : FILES) {
            String filePath = '/' + file;
            String key = filePath.substring(1, filePath.length() - 4);
            InputStream is = clazz.getResourceAsStream(filePath);
            if (is == null) {
                throw new IOException(filePath + " does not exist!");
            }
            IMAGES.put(key, ImageIO.read(is));
        }
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Images";
    }

    @Nonnull
    public static ImageLoader getInstance() {
        return INSTANCE;
    }

    public static Image getImage(String key) {
        if (!IMAGES.containsKey(key)) {
            LOGGER.warn("Image [" + key + "] does not exist!");
            throw new RuntimeException("Image [" + key + "] does not exist!");
            //            return null;
        }
        return IMAGES.get(key);
    }

    @Nonnull
    public static ResizableIcon getResizableIcon(String key) {
        Image image = getImage(key);

        int height = image.getHeight(null);
        int width = image.getWidth(null);
        ResizableIcon resizeIcon = ImageWrapperResizableIcon.getIcon(image, new Dimension(width, height));
        return resizeIcon;
    }

    @Nonnull
    public static ImageIcon getImageIcon(String key) {
        return new ImageIcon(getImage(key));
    }
}

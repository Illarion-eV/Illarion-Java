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
package illarion.download.gui.view;

import illarion.download.gui.controller.Controller;
import illarion.download.gui.model.GuiModel;
import javafx.fxml.FXMLLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
final class Util {
    private Util() {
    }

    /**
     * Load a FXML file.
     *
     * @param file the name of the resource to load
     * @return the FXML node
     * @throws IOException in case the file does not exist or is not a valid FXML file
     */
    @Nonnull
    public static <T> T loadFXML(
            @Nonnull String file, @Nonnull GuiModel model, @Nonnull ResourceBundle bundle)
            throws IOException {
        URL resource = Util.class.getResource(file);
        if (resource == null) {
            throw new FileNotFoundException("Failed to locate the resource: " + file);
        }

        FXMLLoader loader = new FXMLLoader(resource, bundle);

        T obj = loader.load();

        Controller controller = loader.getController();
        if (controller != null) {
            controller.setModel(model);
        }

        return obj;
    }

    public static ResourceBundle loadResourceBundle(@Nonnull String name) {
        String dir = "illarion/download/gui/view/";
        final ResourceBundle bundle;
        if (Locale.getDefault().getLanguage().equals(Locale.GERMAN.getLanguage())) {
            bundle = ResourceBundle.getBundle(dir + name, Locale.GERMAN, Util.class.getClassLoader());
        } else {
            bundle = ResourceBundle.getBundle(dir + name, Locale.ENGLISH, Util.class.getClassLoader());
        }
        return bundle;
    }

    @Nullable
    public static String getCssReference(@Nonnull String baseName) {
        @Nullable URL bss = Util.class.getResource(baseName + ".bss");
        if (bss != null) {
            return bss.toExternalForm();
        }
        @Nullable URL css = Util.class.getResource(baseName + ".css");
        if (css != null) {
            return css.toExternalForm();
        }
        return null;
    }
}

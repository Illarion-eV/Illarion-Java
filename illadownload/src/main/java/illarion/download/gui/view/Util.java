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
class Util {
    /**
     * Load a FXML file.
     *
     * @param file the name of the resource to load
     * @return the FXML node
     * @throws IOException in case the file does not exist or is not a valid FXML file
     */
    @Nonnull
    public static <T>  T loadFXML(@Nonnull final String file, @Nonnull final GuiModel model,
                                  @Nonnull final ResourceBundle bundle) throws IOException {
        final URL resource = Util.class.getResource(file);
        if (resource == null) {
            throw new FileNotFoundException("Failed to locate the resource: " + file);
        }

        final FXMLLoader loader = new FXMLLoader(resource, bundle);

        final T obj = (T) loader.load();

        final Controller controller = loader.getController();
        if (controller != null) {
            controller.setModel(model);
        }

        return obj;
    }

    public static ResourceBundle loadResourceBundle(@Nonnull final String name) {
        final String dir = "illarion/download/gui/view/";
        final ResourceBundle bundle;
        if (Locale.getDefault().getLanguage().equals(Locale.GERMAN.getLanguage())) {
            bundle = ResourceBundle.getBundle(dir + name, Locale.GERMAN, Util.class.getClassLoader());
        } else {
            bundle = ResourceBundle.getBundle(dir + name, Locale.ENGLISH, Util.class.getClassLoader());
        }
        return bundle;
    }

    @Nullable
    public static String getCssReference(@Nonnull final String baseName) {
        @Nullable final URL bss = Util.class.getResource(baseName + ".bss");
        if (bss != null) {
            return bss.toExternalForm();
        }
        @Nullable final URL css = Util.class.getResource(baseName + ".css");
        if (css != null) {
            return css.toExternalForm();
        }
        return null;
    }
}

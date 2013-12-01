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
        final URL resource = Thread.currentThread().getContextClassLoader().getResource(file);
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
        final ResourceBundle bundle;
        if (Locale.getDefault().getLanguage().equals(Locale.GERMAN.getLanguage())) {
            bundle = ResourceBundle.getBundle(name, Locale.GERMAN);
        } else {
            bundle = ResourceBundle.getBundle(name, Locale.ENGLISH);
        }
        return bundle;
    }

    @Nullable
    public static String getCssReference(@Nonnull final String baseName) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        @Nullable final URL bss = classLoader.getResource(baseName + ".bss");
        if (bss != null) {
            return bss.toExternalForm();
        }
        @Nullable final URL css = classLoader.getResource(baseName + ".css");
        if (css != null) {
            return css.toExternalForm();
        }
        return null;
    }
}

package illarion.easyquest.gui;

import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

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
}
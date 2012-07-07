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
package illarion.mapedit.graphics;

import illarion.common.graphics.AbstractTextureLoader;
import illarion.mapedit.resource.Resource;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * This texture loader fetches textures as AWT images.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public final class TextureLoaderAwt extends AbstractTextureLoader<TextureAtlasAwt, BufferedImage> implements Resource {
    /**
     * This logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(TextureLoaderAwt.class);

    /**
     * The singleton instance of this class.
     */
    private static final TextureLoaderAwt INSTANCE = new TextureLoaderAwt();

    /**
     * Get the singleton instance of this class.
     *
     * @return the singleton instance of this class
     */
    public static TextureLoaderAwt getInstance() {
        return INSTANCE;
    }

    /**
     * The private constructor to prevent the creation of additional instances of this class.
     */
    private TextureLoaderAwt() {
        // nothing to do
    }

    @Override
    protected TextureAtlasAwt createTextureAtlas(final String image, final String xmlDefinition) {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final URL imageURL = loader.getResource(image);
        final URL xmlURL = loader.getResource(xmlDefinition);

        if ((imageURL == null) || (xmlURL == null)) {
            throw new IllegalArgumentException("Texture files requested to load were not found.");
        }

        InputStream in = null;
        try {
            final BufferedImage bufferedImage = readImage(imageURL);
            in = xmlURL.openStream();
            final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
            return new TextureAtlasAwt(bufferedImage, doc);
        } catch (final SAXException e) {
            LOGGER.error("Failed to parse texture atlas XML file.", e);
        } catch (final ParserConfigurationException e) {
            LOGGER.error("Parser for texture atlas XML file has a invalid configuration.", e);
        } catch (IOException e) {
            LOGGER.error("Failed to read texture atlas data.", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (final IOException ignored) {
                    // ignore
                }
            }
        }

        throw new IllegalStateException("Failed to load texture data for unknown reasons.");
    }

    /**
     * Generate a buffered image that is optimized in matters of possible hardware acceleration. This will use the read
     * image in case it by chance meats the requirements of the graphic environment provided by java. In any other
     * case a new image will be generated that has the same content but meats the requirements for hardware
     * acceleration.
     *
     * @param imageURL the URL of the image to load
     * @return the image loaded from the URL and if needed altered to meat the requirements for hardware acceleration
     * @throws IOException in case loading the image fails
     */
    private BufferedImage readImage(final URL imageURL) throws IOException {
        final BufferedImage orgImage = ImageIO.read(imageURL);

        final GraphicsConfiguration gfxConfig = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDefaultConfiguration();

        if (orgImage.getColorModel().equals(gfxConfig.getColorModel())) {
            return orgImage;
        }

        final BufferedImage newImage = gfxConfig.createCompatibleImage(orgImage.getWidth(), orgImage.getHeight(),
                orgImage.getTransparency());

        final Graphics2D g2d = (Graphics2D) newImage.getGraphics();

        g2d.drawImage(orgImage, 0, 0, null);
        g2d.dispose();

        return newImage;
    }

    /**
     * Loads all textures at once
     *
     * @throws IOException never
     */
    @Override
    public void load() throws IOException {
        while (!areAllAtlasLoaded()) {
            loadNextAtlas();
        }
    }

    @Override
    public String getDescription() {
        return "Textures";
    }
}

/*
 * This file is part of the Illarion Build Utility.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Build Utility is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Build Utility is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Build Utility. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.build;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import illarion.common.util.FastMath;

import illarion.graphics.common.Font;

/**
 * The task of this utility class is it to prepare the fonts that can be used in
 * the client. This will create all the textures that are needed for this font
 * so it should be ensured that this is done before textures are packed.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class FontBuilder extends Task {
    /**
     * The amount of characters for this font.
     */
    private int characters;

    /**
     * The file that contains the font.
     */
    private File fontFile;

    /**
     * The output name of the font.
     */
    private String fontName;

    /**
     * The size of the font to generate in pixel.
     */
    private int fontSize;

    /**
     * The type of the font that is to be used.
     */
    private int fontType;

    /**
     * The name of the font that is created.
     */
    private String outputFontName;

    /**
     * The target folder for the object file and the image folder.
     */
    private File targetFolder;

    /**
     * The public constructor.
     */
    public FontBuilder() {
        // nothing to do
    }

    /**
     * Main function to call when creating a font. This is to be called from
     * outside.
     * <p>
     * Possible parameters:
     * <ul>
     * <li>--fontname &lt;Systemname of the font&gt;</li>
     * <li>--fonttype &lt;normal/bold/italic/bold italic&gt;</li>
     * <li>--fontsize &lt;size of the font in pt&gt;</li>
     * <li>--characters &lt;the amount of characters created (256)&gt;</li>
     * <li>--outputName &lt;the name of the font inside the client&gt;</li>
     * <li>--outputFolder &lt;the folder the font and the resources are written
     * to&gt;</li>
     * </ul>
     * </p>
     * 
     * @param args the argument for the font creation
     */
    @SuppressWarnings("nls")
    public static void main(final String[] args) {
        String fontName = null;
        int fontSize = 0;
        String fontType = null;
        int characters = 256;
        String outputName = null;
        String outputFolder = null;

        for (int i = 0; i < (args.length - 1); i += 2) {
            if (args[i].equals("--fontname")) {
                fontName = args[i + 1];
            } else if (args[i].equals("--fontsize")) {
                fontSize = Integer.parseInt(args[i + 1]);
            } else if (args[i].equals("--fonttype")) {
                fontType = args[i + 1];
            } else if (args[i].equals("--characters")) {
                characters = Integer.parseInt(args[i + 1]);
            } else if (args[i].equals("--outputName")) {
                outputName = args[i + 1];
            } else if (args[i].equals("--outputFolder")) {
                outputFolder = args[i + 1];
            }
        }

        try {
            final FontBuilder builder = new FontBuilder();
            builder.setTargetFolder(new File(outputFolder));
            builder.setCharakterCount(characters);
            builder.setFontName(fontName);
            builder.setFontType(fontType);
            builder.setFontSize(fontSize);
            builder.setOutputName(outputName);
            builder.validate();
            builder.buildFont();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete a directory and all files inside recursively.
     * 
     * @param path the path to the directory that shall be deleted
     * @return <code>true</code> in case the directory got deleted successfully
     */
    private static boolean deleteDirectory(final File path) {
        if (path.exists()) {
            final File[] files = path.listFiles();
            for (final File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        return (path.delete());
    }

    /**
     * Private the graphics with the required settings for perfect font font
     * rendering. This function will set the graphics2d object to maximal
     * quality, not caring for the speed reduction caused by this.
     * 
     * @param g2d the graphics object that is setup
     */
    private static void initGraphics(final Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
            RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
            RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING,
            RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
            RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
            RenderingHints.VALUE_STROKE_NORMALIZE);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    /**
     * Execute the task and pack the jar files that are specified.
     */
    @Override
    public void execute() throws BuildException {
        validate();
        try {
            buildFont();
        } catch (final Exception e) {
            throw new BuildException(e);
        }
    }

    /**
     * Set the amount of characters used.
     * 
     * @param count the count of characters to be used
     */
    public void setCharakterCount(final int count) {
        characters = count;
    }

    /**
     * Set the name of the font.
     * 
     * @param file the file that contains the font
     */
    public void setFontFile(final File file) {
        fontFile = file;
    }

    /**
     * Set the name of the font.
     * 
     * @param name the name of the font
     */
    public void setFontName(final String name) {
        fontName = name;
    }

    /**
     * Set the font size that is supposed to be used for the font to generate.
     * 
     * @param size the size of the font
     */
    public void setFontSize(final int size) {
        fontSize = size;
    }

    /**
     * Set the font type that is supposed to be used.
     * 
     * @param fontTypeStr the string that describes the type of the font
     */
    @SuppressWarnings("nls")
    public void setFontType(final String fontTypeStr) {
        fontType = java.awt.Font.PLAIN;
        if (fontTypeStr.contains("bold")) {
            fontType |= java.awt.Font.BOLD;
        }
        if (fontTypeStr.contains("italic")) {
            fontType |= java.awt.Font.ITALIC;
        }
    }

    /**
     * Set the name of the font that is created.
     * 
     * @param name the name of the font created
     */
    public void setOutputName(final String name) {
        outputFontName = name;
    }

    /**
     * The target directory.
     * 
     * @param dir the working and target directory where the fonts are stored
     *            inside
     */
    public void setTargetFolder(final File dir) {
        targetFolder = dir;
    }

    /**
     * Create the font itself. This function creates all images and the object
     * file of the font.
     * 
     * @throws Exception in case anything went wrong. See the exception for
     *             details
     */
    @SuppressWarnings("nls")
    private void buildFont() throws Exception {
        final File targetImageFolder =
            new File(targetFolder.getAbsolutePath() + File.separatorChar
                + outputFontName);
        final File targetFile =
            new File(targetFolder.getAbsolutePath() + File.separatorChar
                + outputFontName + ".illaFont");

        final java.awt.Font javaFont;

        if (fontName != null) {
            javaFont = new java.awt.Font(fontName, fontType, fontSize);
        } else if (fontFile != null) {
            javaFont =
                java.awt.Font
                    .createFont(java.awt.Font.TRUETYPE_FONT, fontFile)
                    .deriveFont(fontType, fontSize);
        } else {
            return;
        }
        // create the directory for the font
        if (targetImageFolder.exists()) {
            deleteDirectory(targetImageFolder);
        }
        targetImageFolder.mkdir();
        if (targetFile.exists()) {
            targetFile.delete();
        }

        // create the render context for the font
        final BufferedImage tempImage =
            new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
        final Graphics2D gl2d = (Graphics2D) tempImage.getGraphics();
        initGraphics(gl2d);
        gl2d.setFont(javaFont);
        final FontMetrics metrics = gl2d.getFontMetrics();
        final FontRenderContext frc = gl2d.getFontRenderContext();

        // Create the glyphs for the first MAX_GLYPHS characters
        final GlyphVector[] gv = new GlyphVector[characters];
        final int[] mapping = new int[characters];
        final int[] temp = new int[1];
        final char[] tempChar = new char[1];
        for (int i = 0; i < characters; i++) {
            temp[0] = i;
            tempChar[0] = (char) i;
            gv[i] = javaFont.createGlyphVector(frc, temp);
            mapping[i] =
                javaFont.createGlyphVector(frc, tempChar).getGlyphCode(0);
        }

        final int numGlyphs = Math.min(characters, javaFont.getNumGlyphs());
        final Font.Glyph[] glyph = new Font.Glyph[numGlyphs];

        final Map<Integer, File> fileNames = new HashMap<Integer, File>();

        for (int i = 0; i < numGlyphs; i++) {
            final Integer index = Integer.valueOf(i);
            if (gv[i] == null) {
                continue;
            }

            final Shape shape = gv[i].getGlyphOutline(0);
            final Rectangle bounds = shape.getBounds();

            String imageName = null;

            if ((bounds.height != 0) && (bounds.width != 0)) {
                imageName = "c" + index.toString();

                // draw the image
                final WritableRaster raster =
                    Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
                        bounds.width + 1, bounds.height + 1, 2, null);

                final BufferedImage result =
                    new BufferedImage(new ComponentColorModel(
                        ColorSpace.getInstance(ColorSpace.CS_GRAY), new int[] {
                            8, 8 }, true, false, Transparency.TRANSLUCENT,
                        DataBuffer.TYPE_BYTE), raster, false,
                        new Hashtable<Object, Object>());

                final Graphics2D outputGraphics =
                    (Graphics2D) result.getGraphics();
                initGraphics(outputGraphics);
                outputGraphics.translate(-bounds.x, -bounds.y);
                outputGraphics.fill(shape);

                fileNames.put(index,
                    new File(targetImageFolder.getAbsolutePath()
                        + File.separatorChar + imageName + ".png"));

                ImageIO.write(result, "PNG", fileNames.get(index));
            }

            // now calculate the specifications of the glyphes itself.
            final GlyphMetrics gmetrics = gv[i].getGlyphMetrics(0);
            final float glyphAdvance = gmetrics.getAdvance();

            // Calculate kerning with all other glyphs.
            final List<Integer> kerningList = new ArrayList<Integer>();
            for (int left = 0; left < numGlyphs; left++) {
                if (gv[left] == null) {
                    kerningList.add(Integer.valueOf(0));
                    continue;
                }
                final GlyphVector kerningVector =
                    javaFont.createGlyphVector(frc, new int[] { left, i });

                final Point2D pos1 = kerningVector.getGlyphPosition(0);
                final Point2D pos2 = kerningVector.getGlyphPosition(1);

                final GlyphMetrics gmetrics2 = gv[left].getGlyphMetrics(0);
                final float glyphAdvance2 = gmetrics2.getAdvance();

                final float xdif = (float) (pos2.getX() - pos1.getX());
                if (xdif != glyphAdvance2) {
                    kerningList.add(new Integer((int) Math.rint((0.25 + xdif)
                        - glyphAdvance2)));
                } else {
                    kerningList.add(Integer.valueOf(0));
                }
            }

            int[] kerning;

            if (kerningList.size() > 0) {
                kerning = new int[kerningList.size()];
                for (int q = 0; q < kerningList.size(); q++) {
                    kerning[q] = kerningList.get(q).intValue();
                }
            } else {
                kerning = null;
            }

            glyph[i] =
                new Font.Glyph(i, bounds.x, (-(bounds.height + bounds.y)),
                    FastMath.floor(glyphAdvance), kerning, imageName);
        }

        for (int i = 0; i < characters; i++) {
            fileNames.remove(Integer.valueOf(mapping[i]));
        }

        for (final Entry<Integer, File> uselessFile : fileNames.entrySet()) {
            if (uselessFile.getValue() != null) {
                uselessFile.getValue().delete();
                glyph[uselessFile.getKey().intValue()] = null;
            }
        }

        final Font newFont =
            new Font(outputFontName, javaFont.isBold(), javaFont.isItalic(),
                glyph, javaFont.getSize(), metrics.getAscent(),
                metrics.getDescent(), metrics.getLeading(), mapping);

        final ObjectOutputStream oos =
            new ObjectOutputStream(new BufferedOutputStream(
                new FileOutputStream(targetFile)));
        oos.writeObject(newFont);
        oos.flush();
        oos.close();

        System.out.println("Building font \"" + outputFontName + "\" done.");
        System.out.println();
    }

    /**
     * Check if the settings of this task are good to be executed.
     * 
     * @throws BuildException in case anything at the settings for this task is
     *             wrong
     */
    @SuppressWarnings("nls")
    private void validate() throws BuildException {
        if (targetFolder == null) {
            throw new BuildException("Target folder is not set.");
        }
        if (outputFontName == null) {
            throw new BuildException("Output font name is not set.");
        }
        if ((fontName == null) && (fontFile == null)) {
            throw new BuildException(
                "Source font name or source font file needs to be set.");
        }
        if ((fontName != null) && (fontFile != null)) {
            throw new BuildException(
                "Source font name and file are mutally exclusive.");
        }
        if (fontSize == 0) {
            throw new BuildException("Size of the font is not set");
        }
        if (characters == 0) {
            throw new BuildException("character count of the font is not set");
        }
    }
}

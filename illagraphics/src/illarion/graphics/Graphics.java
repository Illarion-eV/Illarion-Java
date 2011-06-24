/*
 * This file is part of the Illarion Graphics Engine.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Graphics Engine is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Graphics Engine is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Graphics Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.graphics;

import java.lang.reflect.InvocationTargetException;

import javolution.lang.Reflection;
import javolution.text.TextBuilder;

import org.apache.log4j.Logger;

/**
 * Port for working with the rest of the graphic classes for Illarion. This
 * class determines what kind of render engine is used and redirects all actions
 * further to the render engine.
 * 
 * @author Martin Karing
 * @author Ralf Schumacher
 * @version 2.00
 * @since 2.00
 */
public final class Graphics {
    /**
     * This parameter makes the graphic interface disabling the speed limiting
     * functions so the game runs at a maximum game loop speed.
     */
    public static final boolean NO_SLOWDOWN = false;

    /**
     * Integer value for high quality. With this settings the drawing functions
     * will use high quality drawing methods but disable the last stage of
     * quality optimizing. The quality is not the best but its really good.
     */
    public static final int QUALITY_HIGH = 4;

    /**
     * Low graphic quality leads to a clear reduce of the graphical quality. The
     * client will run faster with this settings clearly also.
     */
    public static final int QUALITY_LOW = 2;

    /**
     * Integer value for maximal quality. With the settings set to maximal
     * quality the drawing function will use the best quality methods at all
     * time.
     */
    public static final int QUALITY_MAX = 5;

    /**
     * The minimal graphic settings will cause that all graphic settings are set
     * to a maximal speed of the client at the minimal graphic quality.
     */
    public static final int QUALITY_MIN = 1;

    /**
     * Normal quality leads to a good graphic quality and a good speed.
     */
    public static final int QUALITY_NORMAL = 3;

    /**
     * The start of the path of to the classes that are included by reflection.
     */
    @SuppressWarnings("nls")
    private static final String CLASS_PATH = "illarion.graphics.";

    /**
     * The singleton instance of this class that is used to access this class.
     */
    private static final Graphics INSTANCE = new Graphics();

    /**
     * The logger class that takes the log output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(Graphics.class);

    /**
     * The singleton instance of the drawer class. This class is a utility class
     * that has to be used as singleton utility class.
     */
    private Drawer drawerInstance;

    /**
     * This value is set true in case anything was created using the set engine.
     * In this case the method to change the engine will throw a error right
     * away.
     */
    private volatile boolean engineLocked = false;

    /**
     * The singleton instance of the mask that is used to limit the render area.
     */
    private MaskUtil maskInstance;

    /**
     * The quality settings for the graphical interface. Changing this to false
     * will cause that some functions will use lower quality settings.
     */
    private int quality = QUALITY_HIGH;

    /**
     * The singleton instance of the render display that is used.
     */
    private RenderDisplay renderDisplayInstance;

    /**
     * The singleton instance of the render manager used to show the graphics on
     * the screen.
     */
    private RenderManager renderManagerInstance;

    /**
     * The selection of the graphic engine that is currently in use. By default
     * JOGL is selected.
     */
    private Engines usedEngine = Engines.jogl;

    /**
     * Private Constructor to ensure that there is only the singleton instance
     * of this class.
     */
    private Graphics() {
        // singleton constructor, nothing needed to do.
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of the graphics port.
     */
    public static Graphics getInstance() {
        return INSTANCE;
    }

    /**
     * Get the instance of the implemented drawer that is used to render some
     * primitive objects using the implemented render engine.
     * 
     * @return the instance of the drawer object
     * @see illarion.graphics.Drawer
     */
    public Drawer getDrawer() {
        if (drawerInstance == null) {
            drawerInstance = create(Drawer.class, usedEngine);
        }

        return drawerInstance;
    }

    /**
     * Load a font that can be rendered in the client.
     * 
     * @param font the font data that is used for the font to load
     * @return the loaded font
     * @see illarion.graphics.RenderableFont
     */
    public RenderableFont getFont(final FontData font) {
        return create(RenderableFont.class, usedEngine,
            new Class<?>[] { FontData.class }, new Object[] { font });
    }

    /**
     * Get the instance of the implemented mask utility that is used to limit
     * the render area.
     * 
     * @return the instance of the mask utility
     * @see illarion.graphics.MaskUtil
     */
    public MaskUtil getMask() {
        if (maskInstance == null) {
            maskInstance = create(MaskUtil.class, usedEngine);
        }
        return maskInstance;
    }

    /**
     * Get the current quality setting.
     * 
     * @return the value for the current quality setting
     * @see #QUALITY_MAX
     * @see #QUALITY_HIGH
     * @see #QUALITY_NORMAL
     * @see #QUALITY_LOW
     * @see #QUALITY_MIN
     */
    public int getQuality() {
        return quality;
    }

    /**
     * Get the display handler of the current implementation. This allows
     * grabbing the render target canvas and toggling the full screen mode in
     * case it is allowed.
     * 
     * @return the RenderDisplay of the current engine
     * @see illarion.graphics.RenderDisplay
     */
    public RenderDisplay getRenderDisplay() {
        if (renderDisplayInstance == null) {
            renderDisplayInstance = create(RenderDisplay.class, usedEngine);
        }
        return renderDisplayInstance;
    }

    /**
     * Get the singleton instance of the render manager that is used to display
     * the graphics on the screen.
     * 
     * @return the singleton instance of the render manager
     */
    public RenderManager getRenderManager() {
        if (renderManagerInstance == null) {
            renderManagerInstance = create(RenderManager.class, usedEngine);
        }
        return renderManagerInstance;
    }

    /**
     * Get a instance of a sprite. The sprite is a renderable object that is
     * linked with some texture and allows to draw them.
     * 
     * @param frames the amount of frames that shall be stored in the sprite
     * @return the new instance of a sprite object
     * @see illarion.graphics.Sprite
     */
    public Sprite getSprite(final int frames) {
        return create(Sprite.class, usedEngine, new Class<?>[] { int.class },
            new Object[] { Integer.valueOf(frames) });
    }

    /**
     * Get a instance of a sprite color object that can be used to store color
     * values and set them as active drawing color.
     * 
     * @return the instance of the sprite color object
     * @see illarion.graphics.SpriteColor
     */
    public SpriteColor getSpriteColor() {
        return create(SpriteColor.class, usedEngine);
    }

    /**
     * Get a instance of a text line. Such lines can be used to render text to
     * the screen using a font implementation created with
     * {@link #getFont(FontData)}. Those line have no alignment and do never
     * contain more then one line.
     * 
     * @return the text line instance that is used to render a font
     * @see illarion.graphics.TextLine
     */
    public TextLine getTextLine() {
        return create(TextLine.class, usedEngine);
    }

    /**
     * Get a instance of a TextureAtlas. A handler for textures that include
     * multiple other texture objects and supplies the texture instances that
     * are used to access objects on the texture in special
     * 
     * @return the new instance of a TextureAtlas object
     * @see illarion.graphics.TextureAtlas
     */
    public TextureAtlas getTextureAtlas() {
        return create(TextureAtlas.class, usedEngine);
    }

    /**
     * Change the engine used to display the graphics.
     * 
     * @param newEngine the new engine to use
     * @throws IllegalStateException in case the graphics environment already
     *             created a object with the set engine
     */
    @SuppressWarnings("nls")
    public void setEngine(final Engines newEngine) {
        if (engineLocked) {
            throw new IllegalStateException("Engine can't be changed anymore.");
        }
        usedEngine = newEngine;
    }

    /**
     * Set the quality settings of the client.
     * 
     * @param newQual the quality settings of the client
     * @see #QUALITY_MAX
     * @see #QUALITY_HIGH
     * @see #QUALITY_NORMAL
     * @see #QUALITY_LOW
     * @see #QUALITY_MIN
     */
    public void setQuality(final int newQual) {
        quality = newQual;
    }

    /**
     * Create the instance of a object by using the interface class name and the
     * implementation. The class will try to instantiate the needed class.
     * 
     * @param <T> the object type that is created by this class
     * @param className the basic name of the class. The suffix of the
     *            implementation is added to this name.
     * @param implementation the implementation itself. It contains the name of
     *            the class folder and the suffix of the class name
     * @return the create instance or null
     */
    private <T> T create(final Class<T> className, final Engines implementation) {
        return create(className, implementation, null, null);
    }

    /**
     * Create the instance of a object by using the interface class name and the
     * implementation. The class will try to instantiate the needed class.
     * 
     * @param <T> the object type that is created by this class
     * @param className the basic name of the class. The suffix of the
     *            implementation is added to this name.
     * @param implementation the implementation itself. It contains the name of
     *            the class folder and the suffix of the class name
     * @param parameterTypes a list with the types of the parameters needed for
     *            the constructor. In case no parameters are needed its possible
     *            to set this list to <code>null</code>
     * @param parameters the actual values of the parameters needed for the
     *            constructor. The amount of entries has to fit the amount in
     *            the parameterTypes list
     * @return the create instance or null
     */
    @SuppressWarnings({ "nls", "unchecked" })
    private <T> T create(final Class<T> className,
        final Engines implementation, final Class<?>[] parameterTypes,
        final Object[] parameters) {
        engineLocked = true;

        T instance = null;
        Class<T> clazz = null;

        final TextBuilder builder = TextBuilder.newInstance();
        builder.append(CLASS_PATH);
        builder.append(implementation.getFolder());
        builder.append('.');
        builder.append(className.getSimpleName());
        builder.append(implementation.getSuffix());

        clazz = Reflection.getInstance().getClass(builder);
        TextBuilder.recycle(builder);

        if (clazz != null) {
            try {
                if ((parameterTypes == null) || (parameterTypes.length == 0)) {
                    instance = clazz.newInstance();
                } else {
                    instance =
                        clazz.getConstructor(parameterTypes).newInstance(
                            parameters);
                }
            } catch (final InstantiationException ex) {
                LOGGER.fatal("Failed to instantiate the class.", ex);
            } catch (final IllegalAccessException ex) {
                LOGGER.fatal("Failed to access the class.", ex);
            } catch (final IllegalArgumentException ex) {
                LOGGER.fatal("Illegal arguments for calling the constructor.",
                    ex);
            } catch (final SecurityException ex) {
                LOGGER.fatal(
                    "Secutiry problem while creating the new instance", ex);
            } catch (final InvocationTargetException ex) {
                LOGGER.fatal("Constructor created an exception", ex);
            } catch (final NoSuchMethodException ex) {
                LOGGER.fatal("Constructor not found", ex);
            }
        }

        return instance;
    }
}

/*
 * This file is part of the Illarion Input Engine.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Input Engine is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Input Engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Input Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.input;

import java.lang.reflect.InvocationTargetException;

import javolution.lang.Reflection;
import javolution.text.TextBuilder;

import org.apache.log4j.Logger;

/**
 * The central input manager that controls the implementation of the mouse and
 * the keyboard.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class InputManager {
    /**
     * The start of the path of to the classes that are included by reflection.
     */
    @SuppressWarnings("nls")
    private static final String CLASS_PATH = "illarion.input.";

    /**
     * The singleton instance of this class that is used to access this class.
     */
    private static final InputManager INSTANCE = new InputManager();

    /**
     * The logger class that takes the log output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(InputManager.class);

    /**
     * This value is set true in case anything was created using the set engine.
     * In this case the method to change the engine will throw a error right
     * away.
     */
    private volatile boolean engineLocked = false;

    /**
     * The instance of the keyboard manager that is in use. This instance is
     * returned in order to avoid that multiple instances are created.
     */
    private KeyboardManager keyboardInstance = null;

    /**
     * The instance of the mouse manager that is in use. This instance is
     * returned in order to avoid that multiple instances are created.
     */
    private MouseManager mouseInstance = null;

    /**
     * The selection of the input engine that is currently in use. By default
     * LWJGL is selected.
     */
    private Engines usedEngine = Engines.java;

    /**
     * Private Constructor to ensure that there is only the singleton instance
     * of this class.
     */
    private InputManager() {
        // nothing to do
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of the graphics port.
     */
    public static InputManager getInstance() {
        return INSTANCE;
    }

    /**
     * Get the keyboard manager of the current implementation.
     * 
     * @return the keyboard manager of the current implementation
     */
    public KeyboardManager getKeyboardManager() {
        if (keyboardInstance == null) {
            keyboardInstance = create(KeyboardManager.class, usedEngine);
        }
        return keyboardInstance;
    }

    /**
     * Get the mouse manager of the current implementation.
     * 
     * @return the mouse manager of the current implementation
     */
    public MouseManager getMouseManager() {
        if (mouseInstance == null) {
            mouseInstance = create(MouseManager.class, usedEngine);
        }
        return mouseInstance;
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

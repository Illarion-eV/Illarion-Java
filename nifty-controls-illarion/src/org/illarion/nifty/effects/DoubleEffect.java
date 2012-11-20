/*
 * This file is part of the Illarion Nifty-GUI Controls.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Nifty-GUI Controls is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Nifty-GUI Controls is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Nifty-GUI Controls.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.nifty.effects;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.NiftyControl;
import de.lessvoid.nifty.effects.EffectImpl;
import de.lessvoid.nifty.effects.EffectProperties;
import de.lessvoid.nifty.effects.Falloff;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.render.NiftyRenderEngine;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This effect does a very simple thing. It calls for a specified {@link NiftyControl} a specified method and hands
 * over a double value between {@code 0.0} and {@code 1.0}.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DoubleEffect implements EffectImpl {
    /**
     * The logger for this class that keeps track on the problems occurring in this effect.
     */
    private static final Logger LOGGER = Logger.getLogger(DoubleEffect.class);

    /**
     * The target class that contains the method that will be called. This class is requested as Nifty-Control from
     * the target element.
     */
    private Class<? extends NiftyControl> targetControlClass;

    /**
     * The method that is called in the target class.
     */
    private Method targetMethod;

    /**
     * initialize effect.
     *
     * @param nifty     Nifty
     * @param element   Element
     * @param parameter parameters
     */
    @Override
    @SuppressWarnings("unchecked")
    public void activate(final Nifty nifty, final Element element, final EffectProperties parameter) {
        try {
            targetControlClass = (Class<? extends NiftyControl>) Class.forName(
                    String.valueOf(parameter.get("targetClass")));
        } catch (ClassNotFoundException e) {
            LOGGER.error("Illegal target class for double effect.");
            return;
        }

        try {
            targetMethod = targetControlClass.getMethod(String.valueOf(parameter.get("targetMethod")), double.class);
        } catch (NoSuchMethodException e) {
            LOGGER.error("Illegal target method for double effect.", e);
        }
    }

    /**
     * execute the effect.
     *
     * @param element    the Element
     * @param effectTime current effect time
     * @param falloff    the Falloff class for hover effects. This is supposed to be null for none hover effects.
     * @param r          RenderDevice to use
     */
    @Override
    public void execute(final Element element, final float effectTime, final Falloff falloff, final NiftyRenderEngine r) {
        final NiftyControl targetControl = element.getNiftyControl(targetControlClass);
        if (targetControl == null) {
            return;
        }

        try {
            targetMethod.invoke(targetControl, (double) effectTime);
        } catch (IllegalAccessException e) {
            LOGGER.error("Executing effect is not allowed on the target method.");
        } catch (InvocationTargetException e) {
            LOGGER.error("Executing effect is not possible on the target method.");
        }
    }

    /**
     * deactivate the effect.
     */
    @Override
    public void deactivate() {
        targetControlClass = null;
        targetMethod = null;
    }
}

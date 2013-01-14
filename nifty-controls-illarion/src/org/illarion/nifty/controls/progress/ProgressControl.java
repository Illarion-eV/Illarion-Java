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
package org.illarion.nifty.controls.progress;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.AbstractController;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.render.image.ImageMode;
import de.lessvoid.nifty.render.image.ImageModeFactory;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.xml.xpp3.Attributes;
import org.illarion.nifty.controls.Progress;

import javax.annotation.Nonnull;
import java.util.Properties;

/**
 * The control of the progress bar.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @deprecated Use {@link Progress}
 */
public final class ProgressControl extends AbstractController implements Progress {
    private int minImageWidth;
    private int maxWidth;
    private ImageMode originalImageMode;
    private ImageMode unscaledImageMode;
    private boolean currentOriginalMode;
    private double currentProgress;

    @Override
    public void bind(final Nifty nifty, final Screen screen, final Element element, @Nonnull final Properties parameter,
                     final Attributes controlDefinitionAttributes) {
        bind(element);

        minImageWidth = Integer.parseInt(parameter.getProperty("minImageWidth", "0"));
        final Element fill = getElement().findElementByName("#fill");
        originalImageMode = fill.getRenderer(ImageRenderer.class).getImage().getImageMode();
        unscaledImageMode = ImageModeFactory.getSharedInstance().createImageMode("fullimage", "direct");
        currentOriginalMode = true;
        currentProgress = 0.0;
    }

    @Override
    public void onStartScreen() {
        maxWidth = getElement().findElementByName("#fillArea").getWidth();
        final double oldCurrentProgress = currentProgress;
        currentProgress = 2.f;
        setProgress(oldCurrentProgress);
    }

    @Override
    public void layoutCallback() {
        maxWidth = getElement().findElementByName("#fillArea").getWidth();
    }

    @Override
    public boolean inputEvent(final NiftyInputEvent inputEvent) {
        return false;
    }

    /**
     * Set the value of the progress. All values will be clamped to {@code 0.f} and {@code 1.f}.
     *
     * @param value the progress value
     */
    @Override
    public void setProgress(final double value) {
        final Element wrapper = getElement().findElementByName("#fillWrapper");
        final Element fill = getElement().findElementByName("#fill");

        final double usedValue;
        if (value < 0.f) {
            usedValue = 0.f;
        } else if (value > 1.f) {
            usedValue = 1.f;
        } else {
            usedValue = value;
        }

        if (Math.abs(currentProgress - usedValue) < 0.001) {
            return;
        }

        currentProgress = usedValue;

        final int width = (int) Math.round(maxWidth * usedValue);

        fill.setConstraintWidth(SizeValue.px(width));
        wrapper.setConstraintWidth(SizeValue.px(width));

        if ((width < minImageWidth) && currentOriginalMode) {
            fill.getRenderer(ImageRenderer.class).getImage().setImageMode(unscaledImageMode);
            currentOriginalMode = false;
        } else if (!currentOriginalMode) {
            fill.getRenderer(ImageRenderer.class).getImage().setImageMode(originalImageMode);
            currentOriginalMode = true;
        }

        getElement().layoutElements();
    }
}

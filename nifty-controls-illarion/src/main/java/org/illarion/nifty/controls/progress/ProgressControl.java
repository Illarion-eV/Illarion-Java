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
import de.lessvoid.nifty.controls.Parameters;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.render.image.ImageMode;
import de.lessvoid.nifty.render.image.ImageModeFactory;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.SizeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.illarion.nifty.controls.Progress;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The control of the progress bar.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @deprecated Use {@link Progress}
 */
public final class ProgressControl extends AbstractController implements Progress {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProgressControl.class);
    private int minImageWidth;
    private int maxWidth;
    @Nullable
    private ImageMode originalImageMode;
    @Nullable
    private ImageMode unscaledImageMode;
    private boolean currentOriginalMode;
    private double currentProgress;
    @Nullable
    private Element fill;
    @Nullable
    private Element fillWrapper;
    @Nullable
    private Element fillArea;

    @Override
    public void bind(@Nonnull final Nifty nifty,
                     @Nonnull final Screen screen,
                     @Nonnull final Element element,
                     @Nonnull final Parameters parameter) {
        bind(element);

        minImageWidth = parameter.getAsInteger("minImageWidth", 0);
        currentOriginalMode = true;
        currentProgress = 0.0;
    }

    @Override
    public void init(@Nonnull final Parameters parameter) {
        super.init(parameter);

        NiftyImage fillImage = getFillImage();
        if (fillImage != null) {
            originalImageMode = fillImage.getImageMode();
            unscaledImageMode = ImageModeFactory.getSharedInstance().createImageMode("fullimage", "direct");
        } else {
            LOGGER.error("Progress control does not seem to have a image. This progress control will not work.");
        }
        setProgress(currentProgress, true);
    }

    @Nullable
    private Element getFillArea() {
        if (fillArea != null) {
            return fillArea;
        }
        Element element = getElement();
        if (element != null) {
            fillArea = element.findElementById("#fillArea");
        }
        return fillArea;
    }

    @Nullable
    private Element getFillWrapper() {
        if (fillWrapper != null) {
            return fillWrapper;
        }
        Element fillArea = getFillArea();
        if (fillArea != null) {
            fillWrapper = fillArea.findElementById("#fillWrapper");
        }
        return fillWrapper;
    }

    @Nullable
    private Element getFill() {
        if (fill != null) {
            return fill;
        }
        Element fillWrapper = getFillWrapper();
        if (fillWrapper != null) {
            fill = fillWrapper.findElementById("#fill");
        }
        return fill;
    }

    @Nullable
    private NiftyImage getFillImage() {
        Element fill = getFill();
        if (fill == null) {
            return null;
        }
        ImageRenderer renderer = fill.getRenderer(ImageRenderer.class);
        if (renderer == null) {
            return null;
        }
        return renderer.getImage();
    }

    @Override
    public void onStartScreen() {
        layoutCallback();
    }

    @Override
    public void layoutCallback() {
        Element fillArea = getFillArea();
        if (fillArea == null) {
            return;
        }
        final int oldWidth = maxWidth;
        maxWidth = fillArea.getWidth();
        if (maxWidth != oldWidth) {
            setProgress(currentProgress, true);
        }
    }

    @Override
    public boolean inputEvent(@Nonnull final NiftyInputEvent inputEvent) {
        return false;
    }

    /**
     * Set the value of the progress. All values will be clamped to {@code 0.f} and {@code 1.f}.
     *
     * @param value  the progress value
     * @param forced {@code true} in case the values are supposed to be updated event if the old and the new progress
     *               value are equal
     */
    private void setProgress(final double value, final boolean forced) {
        final double usedValue;
        if (value < 0.f) {
            usedValue = 0.f;
        } else if (value > 1.f) {
            usedValue = 1.f;
        } else {
            usedValue = value;
        }

        if (!forced && (Math.abs(currentProgress - usedValue) < 0.001)) {
            return;
        }

        currentProgress = usedValue;

        if (unscaledImageMode == null || originalImageMode == null) {
            return;
        }
        final Element wrapper = getFillWrapper();
        final Element fill = getFill();

        if (wrapper == null || fill == null) {
            return;
        }

        final int width = (int) Math.round(maxWidth * usedValue);

        fill.setConstraintWidth(SizeValue.px(width));
        wrapper.setConstraintWidth(SizeValue.px(width));

        if ((width < minImageWidth) && currentOriginalMode) {
            NiftyImage image = getFillImage();
            if (image != null) {
                image.setImageMode(unscaledImageMode);
            }
            currentOriginalMode = false;
        } else if (!currentOriginalMode) {
            NiftyImage image = getFillImage();
            if (image != null) {
                image.setImageMode(originalImageMode);
            }
            currentOriginalMode = true;
        }

        Element element = getElement();
        if (element != null) {
            element.layoutElements();
        }
    }

    /**
     * Set the value of the progress. All values will be clamped to {@code 0.f} and {@code 1.f}.
     *
     * @param value the progress value
     */
    @Override
    public void setProgress(final double value) {
        setProgress(value, false);
    }
}

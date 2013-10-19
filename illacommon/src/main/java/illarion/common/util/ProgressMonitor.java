/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Common Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Common Library.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This utility class is used to track the progress of any action. Its very handy of complex tasks need to be
 * monitored.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ProgressMonitor {
    /**
     * The progress of this monitor. This value does not apply in case there are any children applied to this class.
     */
    private float progress;

    /**
     * The weight of this loading operation. This applies in case this monitor is a child to another progress monitor,
     * The weight is compared to the siblings of this monitor. A monitor with a weight of two has twice the effect to
     * the resulting progress of the monitor compared to a monitor with a weight of one.
     */
    private float weight;

    /**
     * The list of children of this monitor.
     */
    @Nullable
    private List<ProgressMonitor> children;

    /**
     * Create a new progress monitor with a custom weight value.
     *
     * @param weight the weight value of this progress monitor
     * @throws IllegalArgumentException in case the weight is set to a value less then {@code 0.f} or to NaN or to
     *                                  Infinity
     */
    public ProgressMonitor(final float weight) {
        if (weight < 0.f) {
            throw new IllegalArgumentException("weight may not be less then 0");
        }
        if (Float.isInfinite(weight) || Float.isNaN(weight)) {
            throw new IllegalArgumentException("weight may not infinite or NaN");
        }
        this.weight = weight;
    }

    /**
     * Create a new progress monitor with the default weight of one.
     */
    public ProgressMonitor() {
        this(1.f);
    }

    /**
     * Add a children to this progress tracker.
     *
     * @param childMonitor the children that is supposed to be monitored now
     */
    public void addChild(@Nonnull final ProgressMonitor childMonitor) {
        if (children == null) {
            children = new ArrayList<ProgressMonitor>();
        }
        children.add(childMonitor);
    }

    /**
     * Set the new value of the progress. This operation is only allowed in case there are no children applied to
     * this monitor.
     *
     * @param progress the new progress value, the value is capped to {@code 0.f} to {@code 1.f}
     * @throws IllegalStateException in case this monitor has children applied to it
     */
    public void setProgress(final float progress) {
        if (children != null) {
            throw new IllegalStateException("Setting the progress of a monitor with children is not allowed.");
        }
        if (progress > 1.f) {
            this.progress = 1.f;
        } else if (progress < 0.f) {
            this.progress = 0.f;
        } else {
            this.progress = progress;
        }
    }

    /**
     * Set the weight of this monitor to a new value.
     *
     * @param weight the new weight of the monitor
     */
    public void setWeight(final float weight) {
        this.weight = weight;
    }

    /**
     * Get the progress of this monitor. This either returns the progress of this monitor or the total weighted
     * progress of all the children progress monitors.
     *
     * @return the progress as a value between {@code 0.f} and {@code 1.f}
     */
    public float getProgress() {
        if (children == null) {
            return progress;
        }
        float totalProgress = 0.f;
        float totalWeight = 0.f;
        for (@Nonnull final ProgressMonitor childMonitor : children) {
            totalProgress += childMonitor.getProgress() * childMonitor.weight;
            totalWeight += childMonitor.weight;
        }

        return totalProgress / totalWeight;
    }
}

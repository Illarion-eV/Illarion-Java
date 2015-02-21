/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package org.illarion.engine.graphic;

import illarion.common.types.Location;
import illarion.common.util.Stoppable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manager class that handles the light. It stores the pre-calculated light rays
 * as well as the light sources that are currently in use. Also it creates and
 * removes the light sources on request.
 * <p>
 * The whole calculations are threaded, so the light map that is the target of
 * all calculation results needs to be thread save.
 * </p>
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class LightTracer implements Stoppable {
    private class CalculateLightTask implements Callable<Void> {
        @Nonnull
        private final LightSource light;

        private CalculateLightTask(@Nonnull LightSource light) {
            this.light = light;
        }

        @Override
        public Void call() throws Exception {
            for (; ; ) {
                if (isShutDown) {
                    return null;
                }
                light.calculateShadows();
                if (!light.isDirty()) {
                    tidyLights.add(light);
                    notifyLightCalculationDone();
                    return null;
                }
            }
        }
    }

    /**
     * The executor service that takes care for calculating the lights.
     */
    @Nonnull
    private final ExecutorService lightCalculationService;

    /**
     * This integer stores the amount of lights that are currently calculated.
     */
    @Nonnull
    private final AtomicInteger lightsInProgress;

    /**
     * The lighting map that is the data source and the target for the light
     * calculating results for all light sources handled by this light tracer.
     */
    @Nonnull
    private final LightingMap mapSource;

    /**
     * The lists of lights that are fully processed and are ready to be published to the map.
     */
    @Nonnull
    private final List<LightSource> tidyLights;

    /**
     * Is set true once the shutdown of the light tracer is triggered.
     */
    private boolean isShutDown;

    /**
     * Default constructor of the light tracer. This tracer handles all light
     * sources that are on the map source that is set with the parameter.
     *
     * @param tracerMapSource the map the lights this tracer handles are on
     */
    @SuppressWarnings("nls")
    public LightTracer(@Nonnull LightingMap tracerMapSource) {
        mapSource = tracerMapSource;
        tidyLights = new CopyOnWriteArrayList<>();
        lightCalculationService = Executors.newCachedThreadPool();
        lightsInProgress = new AtomicInteger(0);
    }

    /**
     * Add a light source to the list of light sources of this tracer. This
     * causes that this light source is taken into account and is rendered by
     * this light tracer if requested.
     *
     * @param light the light that shall be added to the light tracer and so to
     * the game screen
     */
    public void addLight(@Nonnull LightSource light) {
        if (isShutDown) {
            return;
        }
        light.setMapSource(mapSource);
        if (light.isDirty()) {
            lightsInProgress.incrementAndGet();
            lightCalculationService.submit(new CalculateLightTask(light));
        } else {
            tidyLights.add(light);
        }
    }

    /**
     * Check if there are no lights set.
     *
     * @return true in case this tracer does not handle any lights currently
     */
    public boolean isEmpty() {
        return tidyLights.isEmpty() && (lightsInProgress.get() == 0);
    }

    /**
     * Notify the light system about a change on the map. This notify is
     * forwarded to all light sources and those only take the notify into
     * account in case its within the range of their rays. So every change on
     * the map should be reported to the tracer no matter if a light is around
     * this location or not.
     *
     * @param loc the location the change occurred at
     */
    public void notifyChange(@Nonnull Location loc) {
        if (isShutDown) {
            return;
        }
        for (LightSource light : tidyLights) {
            light.notifyChange(loc);
            if (light.isDirty()) {
                refreshLight(light);
            }
        }
    }

    /**
     * Refresh the light tracer and force all lights to recalculate the values.
     */
    public void refresh() {
        if (isShutDown) {
            return;
        }
        Iterable<LightSource> tempList = new ArrayList<>(tidyLights);
        tidyLights.clear();
        for (LightSource light : tempList) {
            refreshLight(light);
        }
    }

    private void notifyLightCalculationDone() {
        if (lightsInProgress.decrementAndGet() == 0) {
            publishTidyLights();
        }
    }

    /**
     * Move a light to the dirty lights list to have it updated at the next run.
     *
     * @param light the light that shall be updated.
     */
    public void refreshLight(@Nonnull LightSource light) {
        if (isShutDown) {
            return;
        }
        light.refresh();
        tidyLights.remove(light);
        addLight(light);
    }

    /**
     * Remove a light source from this tracer. This causes that the light is not
     * any longer calculated and rendered.
     *
     * @param light the light source that shall be removed
     */
    public void remove(@Nonnull LightSource light) {
        if (isShutDown) {
            return;
        }
        light.dispose();
        if (lightsInProgress.get() == 0) {
            publishTidyLights();
        }
    }

    /**
     * Publish all tidy lights.
     */
    private void publishTidyLights() {
        if (isShutDown) {
            return;
        }
        List<LightSource> disposedList = null;
        for (LightSource light : tidyLights) {
            if (light.isDisposed()) {
                if (disposedList == null) {
                    disposedList = new ArrayList<>();
                }
                disposedList.add(light);
            } else {
                light.apply();
            }
        }
        mapSource.renderLights();

        if (disposedList != null) {
            tidyLights.removeAll(disposedList);
        }
    }

    /**
     * Stop the thread as soon as possible.
     */
    @Override
    public void saveShutdown() {
        isShutDown = true;
        lightCalculationService.shutdown();
        while (!lightCalculationService.isTerminated()) {
            try {
                lightCalculationService.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                // ignore
            }
        }
        tidyLights.clear();
    }
}

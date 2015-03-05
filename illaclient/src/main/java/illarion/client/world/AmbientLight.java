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
package illarion.client.world;

import illarion.common.util.PoolThreadFactory;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.ImmutableColor;

import javax.annotation.Nonnull;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The purpose of this class is to calculate and maintain the current ambient light.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
final class AmbientLight {
    private static class GradientColorKey {
        @Nonnull
        public final Color color;
        public final double key;

        GradientColorKey(@Nonnull Color color, double key) {
            this.color = color;
            this.key = key;
        }
    }

    @Nonnull
    private final Color ambientLight1;
    @Nonnull
    private final Color ambientLight2;
    private boolean ambientLightToggle;
    private double overcast;

    @Nonnull
    private final ScheduledExecutorService calculationExecutor;

    @Nonnull
    private static final GradientColorKey[] SUN_RISE_GRADIENT = {
            new GradientColorKey(Color.BLACK, 0.0),
            new GradientColorKey(new ImmutableColor(80, 70, 0), 0.1),
            new GradientColorKey(new ImmutableColor(255, 210, 170), 0.3),
            new GradientColorKey(Color.WHITE, 1.0)
    };

    @Nonnull
    private static final Color STARLIGHT_COLOR = new ImmutableColor(0.15f, 0.15f, 0.3f);

    AmbientLight() {
        ambientLight1 = new Color(Color.BLACK);
        ambientLight2 = new Color(Color.BLACK);
        ambientLightToggle = false;
        calculationExecutor = Executors.newSingleThreadScheduledExecutor(
                new PoolThreadFactory("AmbientLightCalculation", true));
        calculationExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                calculate();
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    /**
     * Get the phase of the sun based on the day of the year.
     *
     * @param dayOfYear the day of the year.
     * @return {@code 1.0} for the longest day, {@code -1.0} for the shortest day.
     */
    private static double getPhaseOfTheSun(double dayOfYear) {
        int daysInYear = 365; /* The amount of days in one year. */
        int longDay = 184;  /* The longest day in the year. */

        double usedDay = dayOfYear - longDay;
        if (usedDay < 0) {
            usedDay += daysInYear;
        }

        return Math.cos((usedDay / daysInYear) * Math.PI * 2.0);
    }

    /**
     * Get the time in seconds the sun is shining at the specified day.
     *
     * @param phaseOfSun the phase of the sun
     * @return the time in seconds of sunshine
     */
    private static double getDaylightSpan(double phaseOfSun) {
        if ((phaseOfSun < -1) || (phaseOfSun > 1)) {
            throw new IllegalArgumentException("The phase of the sun is out of bounds: " + phaseOfSun);
        }

        double timeVariation = 3.85; /* The variation of the normal length day to the shortest and the longest day. */
        return (12.0 + (timeVariation * phaseOfSun)) * 60.0 * 60.0;
    }

    /**
     * Get the time in seconds the sun requires to rise or go down.
     *
     * @param phaseOfSun the current phase of the sun
     * @return the time for the raise or the down in seconds
     */
    private static double getSunRiseSetTime(double phaseOfSun) {
        if ((phaseOfSun < -1) || (phaseOfSun > 1)) {
            throw new IllegalArgumentException("The phase of the sun is out of bounds: " + phaseOfSun);
        }

        double defaultSunRaiseTime = 2.0; /* Mean time for a sun rise/set in hours. */
        double sunRaiseVariation = -1.0; /* The variation of the time for the sun rise/set in hours */
        return (defaultSunRaiseTime + (sunRaiseVariation * phaseOfSun)) * 60.0 * 60.0;
    }

    /**
     * Complex luminance calculation.
     *
     * @param color the source color
     * @return the calculated luminance
     */
    private static double getLuminance(@Nonnull Color color) {
        double sqrRed = color.getRedf() * color.getRedf();
        double sqrGreen = color.getGreenf() * color.getGreenf();
        double sqrBlue = color.getBluef() * color.getBluef();
        return Math.sqrt((0.299 * sqrRed) + (0.587 * sqrGreen) + (0.114 * sqrBlue));
    }

    @Nonnull
    private static Color getColorFromGradient(double key, @Nonnull GradientColorKey[] gradientKeys,
                                              @Nonnull Color resultStorage) {
        if (gradientKeys.length < 2) {
            throw new IllegalArgumentException("Supplied gradient is no gradient. Too few values.");
        }
        assert gradientKeys[0] != null;
        if (key <= gradientKeys[0].key) {
            resultStorage.setColor(gradientKeys[0].color);
            return resultStorage;
        }
        if (key >= gradientKeys[gradientKeys.length - 1].key) {
            resultStorage.setColor(gradientKeys[gradientKeys.length - 1].color);
            return resultStorage;
        }
        for (int i = 0; i < (gradientKeys.length - 1); i++) {
            double startKey = gradientKeys[i].key;
            double stopKey = gradientKeys[i + 1].key;
            if ((key >= startKey) && (key < stopKey)) {
                /* Found the gradient range it is in. */
                double processWithinRange = getProgressInRange(startKey, stopKey, key);
                Color startColor = gradientKeys[i].color;
                Color stopColor = gradientKeys[i + 1].color;

                resultStorage.setAlphaf((float) getInterpolated(startColor.getAlphaf(),
                        stopColor.getAlphaf(), processWithinRange));
                resultStorage.setRedf((float) getInterpolated(startColor.getRedf(),
                        stopColor.getRedf(), processWithinRange));
                resultStorage.setGreenf((float) getInterpolated(startColor.getGreenf(),
                        stopColor.getGreenf(), processWithinRange));
                resultStorage.setBluef((float) getInterpolated(startColor.getBluef(),
                        stopColor.getBluef(), processWithinRange));
                return resultStorage;

            }
        }
        return resultStorage;
    }

    private static double getProgressInRange(double start, double stop, double currentValue) {
        return (currentValue - start) * (1.0 / (stop - start));
    }

    private static double getInterpolated(double start, double stop, double process) {
        return ((stop - start) * process) + start;
    }

    public void setOvercast(double newValue) {
        if ((newValue < 0) || (newValue > 1)) {
            throw new IllegalArgumentException("Overcast value is out of range.");
        }
        overcast = newValue;
    }

    private void calculate() {
        if (!World.getClock().isSet()) {
            return;
        }
        Color usedColor = getFreeColorStorage();
        usedColor.setColor(Color.BLACK);

        double dayInYear = World.getClock().getTotalDayInYear();
        double secondOfDay = World.getClock().getTotalHour() * 60.0 * 60.0;
        double middleOfDay = 12.0 * 60.0 * 60.0; /* The time in a day where the run is highest. */

        double phaseOfTheSun = getPhaseOfTheSun(dayInYear);

        double daylightTimeSpan = getDaylightSpan(phaseOfTheSun);
        double sunRiseSetSpan = getSunRiseSetTime(phaseOfTheSun);

        /* Light of the sun. */
        if (secondOfDay < middleOfDay) {
            /* Before noon. So there may be a sunrise at hand. */
            double middleOfSunrise = middleOfDay - (daylightTimeSpan / 2.0);
            double beginOfSunrise = middleOfSunrise - (sunRiseSetSpan / 2.0);
            double endOfSunrise = middleOfSunrise + (sunRiseSetSpan / 2.0);
            if ((secondOfDay > beginOfSunrise) && (secondOfDay < endOfSunrise)) {
                /* We are currently within the time span of a sunrise. */
                double riseProgress = getProgressInRange(beginOfSunrise, endOfSunrise, secondOfDay);
                getColorFromGradient(riseProgress, SUN_RISE_GRADIENT, usedColor);
            } else if (secondOfDay >= endOfSunrise) {
                usedColor.setColor(Color.WHITE);
            } else {
                usedColor.setColor(Color.BLACK);
            }
        } else {
            /* Before noon. So there may be a sunset at hand. */
            double middleOfSunset = middleOfDay + (daylightTimeSpan / 2.0);
            double beginOfSunset = middleOfSunset - (sunRiseSetSpan / 2.0);
            double endOfSunset = middleOfSunset + (sunRiseSetSpan / 2.0);
            if ((secondOfDay > beginOfSunset) && (secondOfDay < endOfSunset)) {
                /* We are currently within the time span of a sunset. */
                double setProgress = getProgressInRange(beginOfSunset, endOfSunset, secondOfDay);
                /* Follow the gradient inverse. */
                getColorFromGradient(1.0 - setProgress, SUN_RISE_GRADIENT, usedColor);
            } else if (secondOfDay >= endOfSunset) {
                usedColor.setColor(Color.BLACK);
            } else {
                usedColor.setColor(Color.WHITE);
            }
        }

        usedColor.setRedf(Math.max(usedColor.getRedf(), STARLIGHT_COLOR.getRedf()));
        usedColor.setGreenf(Math.max(usedColor.getGreenf(), STARLIGHT_COLOR.getGreenf()));
        usedColor.setBluef(Math.max(usedColor.getBluef(), STARLIGHT_COLOR.getBluef()));

        if (overcast > 1.0e-8) {
            /* Calculate the darkening effect of the overcast. */
            /* We need HSB for that. */

            java.awt.Color.RGBtoHSB(usedColor.getRed(), usedColor.getGreen(), usedColor.getBlue(), HSB_ARRAY);

            HSB_ARRAY[1] = Math.max(0, HSB_ARRAY[1] - (float) (0.3f * overcast)); /* Clouds make everything gray */
            HSB_ARRAY[2] = Math.max(0.1f, HSB_ARRAY[2] - (float) (0.3f * overcast)); /* Clouds make it darker. */

            int rgb = java.awt.Color.HSBtoRGB(HSB_ARRAY[0], HSB_ARRAY[1], HSB_ARRAY[2]);
            usedColor.setRed((0xFF0000 & rgb) >> 16);
            usedColor.setGreen((0xFF00 & rgb) >> 8);
            usedColor.setBlue(0xFF & rgb);
        }

        /* Activate the new color */
        ambientLightToggle = !ambientLightToggle;
    }

    private static final float[] HSB_ARRAY = new float[3];

    public void shutdown() {
        calculationExecutor.shutdown();
        while (!calculationExecutor.isTerminated()) {
            try {
                calculationExecutor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                // ignored.
            }
        }
    }

    @Nonnull
    private Color getFreeColorStorage() {
        return ambientLightToggle ? ambientLight1 : ambientLight2;
    }

    @Nonnull
    public Color getCurrentAmbientLight() {
        return ambientLightToggle ? ambientLight2 : ambientLight1;
    }
}

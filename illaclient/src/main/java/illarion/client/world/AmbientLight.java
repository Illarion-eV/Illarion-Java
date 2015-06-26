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
import org.jetbrains.annotations.Contract;

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
    @Nonnull
    private static final GradientColorKey[] SUN_RISE_GRADIENT = {
            new GradientColorKey(Color.BLACK, 0.0),
            new GradientColorKey(new ImmutableColor(80, 70, 0), 0.1),
            new GradientColorKey(new ImmutableColor(255, 210, 170), 0.3),
            new GradientColorKey(Color.WHITE, 1.0)
    };
    @Nonnull
    private static final Color STARLIGHT_COLOR = new ImmutableColor(0.15f, 0.15f, 0.3f);
    @Nonnull
    private final Color ambientLight1;
    @Nonnull
    private final Color ambientLight2;
    @Nonnull
    private final ScheduledExecutorService calculationExecutor;
    private boolean ambientLightToggle;
    private double overcast;

    AmbientLight() {
        ambientLight1 = new Color(Color.BLACK);
        ambientLight2 = new Color(Color.BLACK);
        ambientLightToggle = false;
        calculationExecutor = Executors.newSingleThreadScheduledExecutor(
                new PoolThreadFactory("AmbientLightCalculation", true));
        calculationExecutor.scheduleAtFixedRate(this::calculate, 0, 500, TimeUnit.MILLISECONDS);
    }

    /**
     * Get the phase of the sun based on the day of the year.
     *
     * @param dayOfYear the day of the year.
     * @return {@code 1.0} for the longest day, {@code -1.0} for the shortest day.
     */
    private static double getPhaseOfTheSun(double dayOfYear) {
        int daysInYear = 365; /* The amount of days in one year. */
        int longDay = 182;  /* The longest day in the year. */

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

    @Nonnull
    private static Color getColorFromGradient(double key, @Nonnull GradientColorKey... gradientKeys) {
        if (gradientKeys.length < 2) {
            throw new IllegalArgumentException("Supplied gradient is no gradient. Too few values.");
        }
        assert gradientKeys[0] != null;
        if (key <= gradientKeys[0].getKey()) {
            return gradientKeys[0].getColor();
        }
        if (key >= gradientKeys[gradientKeys.length - 1].getKey()) {
            return gradientKeys[gradientKeys.length - 1].getColor();
        }
        for (int i = 0; i < (gradientKeys.length - 1); i++) {
            double startKey = gradientKeys[i].getKey();
            double stopKey = gradientKeys[i + 1].getKey();
            if ((key >= startKey) && (key < stopKey)) {
                /* Found the gradient range it is in. */
                double processWithinRange = getProgressInRange(startKey, stopKey, key);
                Color startColor = gradientKeys[i].getColor();
                Color stopColor = gradientKeys[i + 1].getColor();

                return new ImmutableColor(
                        getInterpolated(startColor.getRedf(), stopColor.getRedf(), processWithinRange),
                        getInterpolated(startColor.getGreenf(), stopColor.getGreenf(), processWithinRange),
                        getInterpolated(startColor.getBluef(), stopColor.getBluef(), processWithinRange),
                        getInterpolated(startColor.getAlphaf(), stopColor.getAlphaf(), processWithinRange));

            }
        }
        throw new IllegalStateException("Feature at gradient calculation. This poit must not ever be reached.");
    }

    @Contract(pure = true)
    private static double getProgressInRange(double start, double stop, double currentValue) {
        return (currentValue - start) * (1.0 / (stop - start));
    }

    @Contract(pure = true)
    private static float getInterpolated(float start, float stop, double process) {
        return (float) (((stop - start) * process) + start);
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

        usedColor.setColor(calculateSunlight(dayInYear, secondOfDay, middleOfDay));

        usedColor.setRedf(Math.max(usedColor.getRedf(), STARLIGHT_COLOR.getRedf()));
        usedColor.setGreenf(Math.max(usedColor.getGreenf(), STARLIGHT_COLOR.getGreenf()));
        usedColor.setBluef(Math.max(usedColor.getBluef(), STARLIGHT_COLOR.getBluef()));

        if (overcast > 1.0e-8) {
            /* Calculate the darkening effect of the overcast. */
            /* We need HSB for that. */
            float[] hsb = java.awt.Color.RGBtoHSB(usedColor.getRed(), usedColor.getGreen(), usedColor.getBlue(), null);

            hsb[1] = Math.max(0, hsb[1] - (float) (0.3f * overcast)); /* Clouds make everything gray */
            hsb[2] = Math.max(0.1f, hsb[2] - (float) (0.3f * overcast)); /* Clouds make it darker. */

            int rgb = java.awt.Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
            usedColor.setRed((0xFF0000 & rgb) >> 16);
            usedColor.setGreen((0xFF00 & rgb) >> 8);
            usedColor.setBlue(0xFF & rgb);
        }

        /* Activate the new color */
        ambientLightToggle = !ambientLightToggle;
    }

    /**
     * Calculate the color the sun is shining with.
     *
     * @param dayInYear   the day within the year
     * @param secondOfDay the second within the current day
     * @param middleOfDay the second of the day that marks the middle
     * @return the color of the sun
     */
    @Nonnull
    private static Color calculateSunlight(double dayInYear, double secondOfDay, double middleOfDay) {
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
                return getColorFromGradient(riseProgress, SUN_RISE_GRADIENT);
            } else {
                return (secondOfDay >= endOfSunrise) ? Color.WHITE : Color.BLACK;
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
                return getColorFromGradient(1.0 - setProgress, SUN_RISE_GRADIENT);
            } else {
                return (secondOfDay >= endOfSunset) ? Color.BLACK : Color.WHITE;
            }
        }
    }

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
    @Contract(pure = true)
    private Color getFreeColorStorage() {
        return ambientLightToggle ? ambientLight1 : ambientLight2;
    }

    @Nonnull
    @Contract(pure = true)
    public Color getCurrentAmbientLight() {
        return ambientLightToggle ? ambientLight2 : ambientLight1;
    }

    private static class GradientColorKey {
        @Nonnull
        private final ImmutableColor color;
        private final double key;

        GradientColorKey(@Nonnull ImmutableColor color, double key) {
            this.color = color;
            this.key = key;
        }

        @Nonnull
        public ImmutableColor getColor() {
            return color;
        }

        public double getKey() {
            return key;
        }
    }
}

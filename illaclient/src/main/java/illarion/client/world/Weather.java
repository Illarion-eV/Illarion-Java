/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
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

import illarion.client.graphics.AnimationUtility;
import org.illarion.engine.graphic.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Weather control class. Generated and stores all effects caused by the weather.
 */
public final class Weather {
    /**
     * Precipitation type rain.
     */
    public static final int RAIN = 1;
    /**
     * Value of the cloud overcast where it reaches the maximal amount of darken
     * effect.
     */
    private static final int CLOUD_LIMIT = 60;
    /**
     * The maximal possible value for clouds.
     */
    private static final int CLOUDS_MAX = 100;
    /**
     * The minimal possible value for clouds.
     */
    private static final int CLOUDS_MIN = 0;
    /**
     * The step value for the cloud changes.
     */
    private static final int CLOUDS_STEP = 1;
    /**
     * The value the coverage of the visibility is reduced by due a flash.
     */
    private static final int FLASH_COVERAGE = 40;
    /**
     * Time between a fast row of flashes.
     */
    private static final int FLASH_WAIT = 4 * 1000;
    /**
     * Additional visibility coverage caused by fog.
     */
    private static final float FOG_COVERAGE = 2.5f;
    /**
     * Biggest allowed value for the fog.
     */
    private static final int FOG_MAXIMAL_VALUE = 100;
    /**
     * Smallest allowed value for the fog.
     */
    private static final int FOG_MINIMAL_VALUE = 0;
    /**
     * The step value the fog changes per cycle. The higher the value the faster
     * the fog approaches the target value.
     */
    private static final int FOG_STEP = 4;
    /**
     * Hours one day in Illarion has.
     */
    private static final int HOUR_PER_DAY = 24;
    /**
     * The Brightness share of the current outside brightness in case the
     * character is inside a building.
     */
    private static final float INSIDE_BRIGHTNESS = 0.6f;
    /**
     * The additional coverage caused by the light illumination.
     */
    private static final int LIGHT_COLOR_COVERAGE = 12;
    /**
     * Maximal allowed value for {@link #lightning}.
     */
    private static final int LIGHTNING_MAX = 100;
    /**
     * Minimal allowed value for {@link #lightning}.
     */
    private static final int LIGHTNING_MIN = 0;
    /**
     * The instance of the logger that is used to write out the data.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Weather.class);
    /**
     * General value for no effect in the used environment.
     */
    private static final int NONE = -1;
    /**
     * Conversation between the internal storage and the server values for the precipitation strength value.
     */
    private static final int PREC_CONVERSATION_VALUE = 5;
    /**
     * Maximal precipitation value send by the server.
     */
    private static final int PREC_SERVER_MAX = 100;
    /**
     * Minimal precipitation value send by the server.
     */
    private static final int PREC_SERVER_MIN = 0;
    /**
     * Changing speed of the precipitation types.
     */
    private static final int PREC_STEP = 15;
    /**
     * Additional visibility coverage caused by rain.
     */
    private static final int RAIN_COVERAGE = 25;

    /**
     * Precipitation type snow.
     */
    private static final int SNOW = 2;

    /**
     * Additional visibility coverage caused by snow.
     */
    private static final int SNOW_COVERAGE = 15;

    /**
     * Brightness at underground areas.
     */
    private static final float UNDERGROUND_BRIGHT = 0.1f;

    /**
     * Conversation value for the calculation from the server value for the wind
     * to the internal value for the wind.
     */
    private static final int WIND_CONVERSATION_VALUE;

    /**
     * Maximal value for the wind gusts.
     */
    private static final int WIND_GUST_MAX = 100;

    /**
     * Minimal value for the wind gusts.
     */
    private static final int WIND_GUST_MIN = 0;

    /**
     * Maximal allowed value for the wind in the internal storage.
     */
    private static final int WIND_INTERAL_MAX = 500;

    /**
     * Minimal allowed value for the wind in the internal storage.
     */
    private static final int WIND_INTERAL_MIN = -500;

    /**
     * Maximal allowed value for the wind send by the server.
     */
    private static final int WIND_SERVER_MAX = 100;

    /**
     * Minimal allowed value for the wind send by the server.
     */
    private static final int WIND_SERVER_MIN = -100;

    /**
     * Speed the wind is interpolated with. A higher value means the it takes
     * longer until the target value for the wind is reached.
     */
    private static final int WIND_STEP = 8;

    static {
        WIND_CONVERSATION_VALUE = WIND_INTERAL_MAX / WIND_SERVER_MAX;
    }

    /**
     * Color of the ambient light.
     */
    @Nonnull
    private final Color ambientLight;

    /**
     * The color the ambient light is approaching.
     */
    @Nonnull
    private final Color ambientTargetColor;
    /**
     * The random value generator used by this class.
     */
    private final Random rnd = new Random();
    @Nonnull
    private final AmbientLight ambientLightCalculation;
    /**
     * The current state of the clouds at the sky. Values between 0 for no
     * clouds and 100 for fully clouded are possible.
     */
    private int cloud;
    /**
     * The target state of the clouds at the sky. This is used together with
     * {@link #cloud} for the smooth interpolation of the clouds.
     */
    private int cloudTarget;
    /**
     * The current value of the fog. 0 for no fog, 100 for maximum fog.
     */
    private int fog;
    /**
     * The target fog value. Has the same range as {@link #fog} and works as the
     * smooth fading target for the intensity of the fog.
     */
    private int fogTarget;
    /**
     * The strength of the wind gusts.
     */
    private int gusts;
    /**
     * The target strength of the wind gusts.
     */
    private int gustsTarget;
    /**
     * Current lighting state. A greater number causes lightings in shorter
     * intervals. 0 for no lightnings and 100 for the shortest intervals of
     * lightnings.
     */
    private int lightning;
    /**
     * Time to the next flashes.
     */
    private int nextFlash;
    /**
     * Time until the next gust.
     */
    private int nextGust;
    /**
     * Time to the next thunder sound effect.
     */
    private int nextThunder;
    /**
     * Flag if the player character is currently inside a house or a cave or
     * outside. True means outside.
     */
    private boolean outside;
    /**
     * The target strength of the rain. Values between {@code 0} and {@code 500}.
     */
    private int rainTarget;
    /**
     * The target strength of the snow fall. Values between {@code 0} and {@code 500}.
     */
    private int snowTarget;
    /**
     * The strength of the rain. Values between {@code 0} and {@code 500}.
     */
    private int rain;
    /**
     * The strength of the snow fall. Values between {@code 0} and {@code 500}.
     */
    private int snow;
    /**
     * Amount of flashes shown in a short time interval. A real shown amount is
     * the amount stored here / {@link #FLASH_WAIT}.
     */
    private int showFlash;
    /**
     * Current effective wind value.
     */
    private int wind;
    /**
     * The real target of the current wind. Used as smooth interpolation target
     * for the effective {@link #wind} value.
     */
    private int windTarget;

    /**
     * Default constructor. Prepare and active everything needed to show the
     * weather.
     */
    public Weather() {
        ambientLightCalculation = new AmbientLight();
        ambientLight = new Color(Color.BLACK);
        ambientTargetColor = new Color(ambientLight);
    }

    public void shutdown() {
        ambientLightCalculation.shutdown();
    }

    /**
     * Calculate the current ambient light, depending on the time of day, the environment (inside,
     * outside) and the current weather.
     */
    private void calculateLight() {
        // if we are underground it is simply very dark
        if (World.getPlayer().getBaseLevel() < 0) {
            // average brightness underground
            ambientTargetColor.setRedf(UNDERGROUND_BRIGHT);
            ambientTargetColor.setGreenf(UNDERGROUND_BRIGHT);
            ambientTargetColor.setBluef(UNDERGROUND_BRIGHT);
            return;
        }

        ambientLightCalculation.setOvercast(cloud / (double) CLOUDS_MAX);
        ambientTargetColor.setColor(ambientLightCalculation.getCurrentAmbientLight());

        // it is somewhat darker in buildings
        if (!outside) {
            ambientTargetColor.multiply(INSIDE_BRIGHTNESS);
            ambientTargetColor.setAlpha(Color.MAX_INT_VALUE);
        }
    }

    /**
     * Update the current weather conditions to the next values. This function
     * also performs the smooth change of the values.
     *
     * @param delta the time since the last call of this function
     */
    private void changeWeather(int delta) {
        calculateLight();
    }

    /**
     * Get the current ambient light.
     *
     * @return the current ambient light
     */
    @Nonnull
    public Color getAmbientLight() {
        return ambientLight;
    }

    /**
     * Get the current value of the cloud coverage.
     *
     * @return the current cloud coverage
     */
    public int getClouds() {
        return cloud;
    }

    /**
     * Get the current value of the fog.
     *
     * @return the current fog value
     */
    public float getFog() {
        return (float) fog / (float) FOG_MAXIMAL_VALUE;
    }

    /**
     * Check if there is currently any fog.
     *
     * @return {@code true} in case its even slightly foggy
     */
    public boolean isFog() {
        return fog > 0;
    }

    /**
     * Set the density of the fog in percent.
     *
     * @param newFog New value for the fog.
     */
    public void setFog(int newFog) {
        if ((newFog < FOG_MINIMAL_VALUE) || (newFog > FOG_MAXIMAL_VALUE)) {
            LOGGER.warn("Illegal fog value: {}", newFog);
            return;
        }

        fogTarget = newFog;
    }

    /**
     * Get the current intensity of the lightings.
     *
     * @return current lighting intensity
     */
    public int getLighting() {
        return lightning;
    }

    /**
     * Check if it is currently raining.
     *
     * @return {@code true} in case its raining
     */
    public boolean isRain() {
        return getRain() > 0;
    }

    public int getRain() {
        return rain / 5;
    }

    /**
     * Determine how much the weather obstructs visibility.
     *
     * @return The obstruction of the visiblity caused by the weather
     */
    public int getVisiblity() {
        int coverage = 0;
        if (outside) {
            if ((showFlash > 0) && ((showFlash % FLASH_WAIT) != 0)) {
                coverage -= FLASH_COVERAGE;
            } else {
                float lum = ambientLight.getLuminancef();
                coverage += (int) ((1 - lum) * LIGHT_COLOR_COVERAGE);
            }

            if (rain > 0) {
                coverage += rain / RAIN_COVERAGE;
            }

            if (snow > 0) {
                coverage += snow / SNOW_COVERAGE;
            }

            coverage += (int) (fog / FOG_COVERAGE);
        }
        return coverage;
    }

    /**
     * Get the current strength of the wind.
     *
     * @return the current wind value
     */
    public int getWind() {
        return wind / WIND_CONVERSATION_VALUE;
    }

    /**
     * Get the current strength of the wind gusts.
     *
     * @return the current strength of the gusts
     */
    public int getGusts() {
        return gusts / WIND_CONVERSATION_VALUE;
    }

    /**
     * Check if the players character is currently outside at the fresh air.
     *
     * @return true if the character is outside
     */
    public boolean isOutside() {
        return outside && (World.getPlayer().getBaseLevel() >= 0);
    }

    /**
     * Set if the players character is currently inside or outside.
     *
     * @param newOutside true if the character is outside
     */
    public void setOutside(boolean newOutside) {
        outside = newOutside;
    }

    /**
     * Set the new cloud cover value.
     *
     * @param newCloud new value for the clouds between {@link #CLOUDS_MIN} and
     * {@link #CLOUDS_MAX}
     */
    public void setCloud(int newCloud) {
        if ((newCloud < CLOUDS_MIN) || (newCloud > CLOUDS_MAX)) {
            LOGGER.warn("Illegal clounds value: {}", newCloud);
            return;
        }
        cloudTarget = newCloud;
    }

    /**
     * Set the new lighting intensity value.
     *
     * @param newLightning New value for the lightnings between
     * {@link #LIGHTNING_MIN} and {@link #LIGHTNING_MAX}
     */
    public void setLightning(int newLightning) {
        if ((lightning < LIGHTNING_MIN) || (lightning > LIGHTNING_MAX)) {
            LOGGER.warn("Illegal lightning value: {}", newLightning);
            return;
        }

        lightning = newLightning;
    }

    /**
     * Set the precipitation type and strength.
     *
     * @param type Type of precipitation. Possible values are {@link #RAIN} and
     * {@link #SNOW}
     * @param strength new precipitation strength value
     */
    public void setPrecipitation(int type, int strength) {
        if ((type < 0) || (type > 2) || (strength < PREC_SERVER_MIN) || (strength > PREC_SERVER_MAX)) {
            LOGGER.warn("Illegal precipitation value: {} strength: {}", type, strength);
            return;
        }

        if (type == RAIN) {
            rainTarget = strength * PREC_CONVERSATION_VALUE;
            snowTarget = 0;
        } else if (type == SNOW) {
            snowTarget = strength * PREC_CONVERSATION_VALUE;
            rainTarget = 0;
        }
    }

    /**
     * Set the strength of the wind and the frequency and strength of the wind
     * gusts.
     *
     * @param newWind the new value for the wind
     * @param newGusts the new value for the wind gusts
     */
    public void setWind(int newWind, int newGusts) {
        if ((newWind < WIND_SERVER_MIN) || (newWind > WIND_SERVER_MAX) || (newGusts < WIND_GUST_MIN) ||
                (newGusts > WIND_GUST_MAX)) {
            LOGGER.warn("Illegal wind value: {} gusts: {}", newWind, newGusts);
            return;
        }

        windTarget = newWind * WIND_CONVERSATION_VALUE;
        gustsTarget = newGusts * WIND_CONVERSATION_VALUE;
    }


    private void animateWeather(int delta) {
        int effectiveDelta = Math.max(1, delta / 5);

        wind = AnimationUtility.approach(wind, windTarget, WIND_STEP, WIND_INTERAL_MIN, WIND_INTERAL_MAX, effectiveDelta);
        gusts = AnimationUtility.approach(gusts, gustsTarget, WIND_STEP, 0, WIND_INTERAL_MAX, effectiveDelta);
        cloud = AnimationUtility.approach(cloud, cloudTarget, CLOUDS_STEP, CLOUDS_MIN, CLOUDS_MAX, effectiveDelta);
        fog = AnimationUtility.approach(fog, fogTarget, FOG_STEP, 0, 100, effectiveDelta);
        rain = AnimationUtility.approach(rain, rainTarget, PREC_STEP, 0, 500, effectiveDelta);
        snow = AnimationUtility.approach(snow, snowTarget, PREC_STEP, 0, 500, effectiveDelta);

        if (AnimationUtility.approach(ambientLight, ambientTargetColor, effectiveDelta)) {
            World.getMap().updateAmbientLight();
        }
    }

    public void update(int delta) {
        if (World.getClock().isSet()) {
            changeWeather(delta);
            animateWeather(delta);
        }
    }
}

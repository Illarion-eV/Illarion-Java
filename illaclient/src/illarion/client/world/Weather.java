/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.world;

import java.util.Random;

import org.apache.log4j.Logger;

import illarion.client.ClientWindow;
import illarion.client.Debug;
import illarion.client.IllaClient;
import illarion.client.graphics.AnimationUtility;
import illarion.client.graphics.MapDisplayManager;
import illarion.client.graphics.particle.emitter.WeatherRainEmitter2;
import illarion.client.sound.SoundFactory;
import illarion.client.util.SessionMember;

import illarion.graphics.Graphics;
import illarion.graphics.Sprite;
import illarion.graphics.SpriteColor;

import illarion.sound.SoundManager;
import illarion.sound.SoundSource;

/**
 * Weather control class. Generated and stores all effects caused by the weather
 * and the ingame time.
 */
public final class Weather implements SessionMember {
    /**
     * Precipitation type rain.
     */
    public static final int RAIN = 1;

    /**
     * Ambient light colors for every hour and every overcast type. The list
     * builds up with the daytime in the first level, the clear and overcast
     * colors in the second level and the red, green and blue color value in the
     * third level.
     */
    private static final float[][][] AMBIENTLIGHT_COLORS = new float[][][] {
        { { 0.2f, 0.2f, 0.4f }, { 0.15f, 0.15f, 0.2f } },
        { { 0.15f, 0.15f, 0.3f }, { 0.1f, 0.1f, 0.15f } },
        { { 0.15f, 0.15f, 0.3f }, { 0.1f, 0.1f, 0.15f } },
        { { 0.15f, 0.15f, 0.3f }, { 0.2f, 0.2f, 0.3f } },
        { { 0.7f, 0.7f, 0.75f }, { 0.4f, 0.4f, 0.45f } },
        { { 1, 0.95f, 0.8f }, { 0.6f, 0.6f, 0.6f } },
        { { 1f, 0.98f, 0.9f }, { 0.7f, 0.7f, 0.7f } }, // 6:00
        { { 1f, 1f, 1f }, { 0.8f, 0.8f, 0.8f } },
        { { 1f, 1f, 1f }, { 0.8f, 0.8f, 0.8f } },
        { { 1f, 1f, 1f }, { 0.8f, 0.8f, 0.8f } },
        { { 1f, 1f, 1f }, { 0.8f, 0.8f, 0.8f } },
        { { 1f, 1f, 1f }, { 0.8f, 0.8f, 0.8f } },
        { { 1f, 1f, 1f }, { 0.8f, 0.8f, 0.8f } }, // 12:00
        { { 1f, 1f, 1f }, { 0.8f, 0.8f, 0.8f } },
        { { 1f, 1f, 1f }, { 0.8f, 0.8f, 0.8f } },
        { { 1f, 1f, 1f }, { 0.8f, 0.8f, 0.8f } },
        { { 1f, 1f, 1f }, { 0.8f, 0.8f, 0.8f } },
        { { 1f, 1f, 1f }, { 0.8f, 0.8f, 0.8f } },
        { { 1f, 1f, 1f }, { 0.8f, 0.8f, 0.8f } }, // 18:00
        { { 1f, 0.9f, 0.8f }, { 0.7f, 0.7f, 0.7f } },
        { { 1, 0.8f, 0.7f }, { 0.6f, 0.6f, 0.6f } },
        { { 0.7f, 0.6f, 0.7f }, { 0.4f, 0.4f, 0.45f } },
        { { 0.2f, 0.2f, 0.4f }, { 0.2f, 0.2f, 0.3f } },
        { { 0.2f, 0.2f, 0.4f }, { 0.15f, 0.15f, 0.2f } } };

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
     * Value lower then this, cause that there are single flashes with a
     * variable time in between. Values higher then this value cause that there
     * is a set time between the flashes, but multiple flashes at once.
     */
    private static final int FLASH_CENTER_VALUE = 22;

    /**
     * Color value of a flash. Listed within the array: red, green, blue
     */
    private static final SpriteColor FLASH_COLOR = Graphics.getInstance()
        .getSpriteColor();

    /**
     * The value the coverage of the visibility is reduced by due a flash.
     */
    private static final int FLASH_COVERAGE = 40;

    /**
     * Time between a fast row of flashes.
     */
    private static final int FLASH_WAIT = 4;

    /**
     * Time between two sets of flashes.
     */
    private static final int FLASH_WAIT_LONG = 6;

    /**
     * Additional visibility coverage caused by fog.
     */
    private static final int FOG_COVERAGE = 40;

    /**
     * Biggest allowed value for the fog.
     */
    private static final int FOG_MAXIMAL_VALUE = 100;

    /**
     * Minimal value of fog needed that is becomes visible.
     */
    private static final float FOG_MIN = 0.01f;

    /**
     * Smallest allowed value for the fog.
     */
    private static final int FOG_MINIMAL_VALUE = 0;

    /**
     * The step value the fog changes per cycle. The higher the value the faster
     * the fog approaches the target value.
     */
    private static final float FOG_STEP = 0.01f;

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
    private static final Logger LOGGER = Logger.getLogger(Weather.class);

    /**
     * The width of the overlay area that is used in case anything shall be
     * drawn over the map.
     */
    private static final int MAP_OVERLAY_HEIGHT =
        12 + MapDisplayManager.MAP_HEIGHT;

    /**
     * The height of the overlay area that is used in case anything shall be
     * drawn over the map.
     */
    private static final int MAP_OVERLAY_WIDTH =
        10 + MapDisplayManager.MAP_WIDTH;

    /**
     * Maximal amount of snow flakes or rain drops on the screen.
     */
    private static final int MAXIMUM_DROPS = 500;

    /**
     * General value for no effect in the used environment.
     */
    private static final int NONE = -1;

    /**
     * Conversation between the internal storage and the server values for the
     * precipitation strength value.
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
     * Alpha value of the snow flakes.
     */
    private static final float SNOW_ALPHA = 0.7f;

    /**
     * Additional visibility coverage caused by snow.
     */
    private static final int SNOW_COVERAGE = 15;

    /**
     * Speed of the snow flakes falling. The value means x times the height of
     * the snow flake per render cycle.
     */
    private static final float SNOW_FALLING_SPEED = 3.f;

    /**
     * The minimal effect of the wind to the snow flakes.
     */
    private static final float SNOW_MINIMAL_WIND_SPEED = 0.5f;

    /**
     * This value tells the conversation of precipitation width to effective
     * drawn pixels.
     */
    private static final int SNOW_WIDTH_PX = 30;

    /**
     * The vertical speed of the snow flakes caused by the wind. Higher values
     * slow the snow flakes down.
     */
    private static final float SNOW_WIND_SPEED = 50.f;

    /**
     * Sound effect ID of a thunder.
     */
    private static final int THUNDER_SOUND_ID = 4;

    /**
     * Time until the next thunder, in relation to the {@link #lightning} value.
     * A larger value results in a shorter time.
     */
    private static final int THUNDER_WAIT_TIME = 20;

    /**
     * Brightness at underground areas.
     */
    private static final float UNDERGROUND_BRIGHT = 0.1f;

    /**
     * Interval for updates of the weather.
     */
    private static final int UPDATE_INTERVAL = 1000;

    /**
     * Conversation value for the calculation from the server value for the wind
     * to the internal value for the wind.
     */
    private static final int WIND_CONVERSATION_VALUE;

    /**
     * Share how much a wind gust effects a actual wind. Higher value cause a
     * bigger effect.
     */
    private static final int WIND_GUST_EFFECT = 4;

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
     * Color of the ambient light. This value is used by various graphics
     * elements.
     */
    private SpriteColor ambientLight = Graphics.getInstance().getSpriteColor();

    /**
     * The current state of the clouds at the sky. Values between 0 for no
     * clouds and 100 for fully clouded are possible.
     */
    private int cloud = 0;

    /**
     * The target state of the clouds at the sky. This is used together with
     * {@link #cloud} for the smooth interpolation of the clouds.
     */
    private int cloudTarget = 0;

    /**
     * List of size values of the rain drops or snow flakes.
     */
    private final float[] dropW = new float[MAXIMUM_DROPS];

    /**
     * List of X-Coordinates of the rain drops or snow flakes.
     */
    private final float[] dropX = new float[MAXIMUM_DROPS];

    /**
     * List of Y-Coordinates of the rain drops or snow flakes.
     */
    private final float[] dropY = new float[MAXIMUM_DROPS];

    /**
     * The current value of the fog. 0.f for no fog, 1.f for maximum fog.
     */
    private float fog = -1.f;

    /**
     * Graphical object of the fog effect. A large half transparent picture that
     * is more or less faded out depending on the fog value.
     */
    private final Sprite fogMap;

    /**
     * The target fog value. Has the same range as {@link #fog} and works as the
     * smooth fading target for the intensity of the fog.
     */
    private float fogTarget = 0.f;

    /**
     * The general value for the wind. Possible values are between
     * {@link #WIND_INTERAL_MIN} and {@link #WIND_INTERAL_MAX}.
     */
    private int generalWind = 0;

    /**
     * Amount and strength of wind gusts. {@link #WIND_GUST_MIN} stands for no
     * gusts, {@link #WIND_GUST_MAX} for the shortest interval between the gusts
     * and the strongest gusts.
     */
    private int gusts = 0;

    /**
     * Current lighting state. A greater number causes lightings in shorter
     * intervals. 0 for no lightnings and 100 for the shortest intervals of
     * lightnings.
     */
    private int lightning = 0;

    /**
     * Time in milliseconds until the next update of the weather.
     */
    private long nextChange = 0;

    /**
     * Time to the next flashes.
     */
    private int nextFlash = 0;

    /**
     * Time until the next gust.
     */
    private int nextGust = 0;

    /**
     * Time to the next thunder sound effect.
     */
    private int nextThunder = 0;

    /**
     * Flag if the player character is currently inside a house or a cave or
     * outside. True means outside.
     */
    private boolean outside = false;

    /**
     * Current strength of precipitation. 0 for no precipitation, 500 for
     * maximum precipitation.
     */
    private int prec = -1;

    /**
     * Target strength of precipitation. Used as interpolation target for
     * {@link #prec}
     */
    private int precTarget = 0;

    /**
     * Current precipitation type. Possible values are {@link #RAIN} and
     * {@link #SNOW} and {@link #NONE}
     */
    private int precType = NONE;

    /**
     * The particle emitter handling the rain.
     */
    private WeatherRainEmitter2 rainEmitter;

    /**
     * The random value generator used by this class.
     */
    private final Random rnd = new Random();

    /**
     * Amount of flashes shown in a short time interval. A real shown amount is
     * the amount stored here / {@link #FLASH_WAIT}.
     */
    private int showFlash = 0;

    /**
     * Graphical object of a snow flake. That objects are drawn many times to
     * show the precipitation type {@link #SNOW}
     */
    private final Sprite snowFlake;

    /**
     * Current effective wind value. Results from the {@link #generalWind} and
     * the wind gusts.
     */
    private int wind = 0;

    /**
     * The real target of the current wind. Used as smooth interpolation target
     * for the effective {@link #wind} value.
     */
    private int windTarget = 0;

    /**
     * Default constructor. Prepare and active everything needed to show the
     * weather.
     */
    @SuppressWarnings("nls")
    public Weather() {
        // fogMap =
        // SpriteBuffer.getInstance().getSprite(Gui.GUI_PATH, "fog_overlay",
        // true);
        // snowFlake =
        // SpriteBuffer.getInstance().getSprite(Gui.GUI_PATH, "snowflake",
        // true);
        fogMap = null;
        snowFlake = null;
    }

    /**
     * Calculate the current ambient light, depending on the time of day, the
     * enviroment (inside, outside) and the current weather.
     */
    public void calculateLight() {
        // if we are underground it is simply very dark
        if (Game.getPlayer().getBaseLevel() < 0) {
            // average brightness underground
            ambientLight.set(UNDERGROUND_BRIGHT);
            return;
        }

        final int hour = 12;// Gui.getInstance().getClock().getHour();
        final int nextHour = (hour + 1) % HOUR_PER_DAY;
        final float timeAlpha = 0.5f;
        // Gui.getInstance().getClock().getMinute() / MIN_PER_HOUR;

        // heavily overcast - all grey
        if (cloud > CLOUD_LIMIT) {
            ambientLight.set((AMBIENTLIGHT_COLORS[nextHour][1][0] * timeAlpha)
                + (AMBIENTLIGHT_COLORS[hour][1][0] * (1f - timeAlpha)),
                (AMBIENTLIGHT_COLORS[nextHour][1][1] * timeAlpha)
                    + (AMBIENTLIGHT_COLORS[hour][1][1] * (1f - timeAlpha)),
                (AMBIENTLIGHT_COLORS[nextHour][1][2] * timeAlpha)
                    + (AMBIENTLIGHT_COLORS[hour][1][2] * (1f - timeAlpha)));
        } else { // partially cloudy, interpolate color
            final float cloudAlpha = (float) cloud / CLOUD_LIMIT;
            ambientLight
                .set(
                    (((AMBIENTLIGHT_COLORS[nextHour][0][0] * timeAlpha) + (AMBIENTLIGHT_COLORS[hour][0][0] * (1f - timeAlpha))) * (1f - cloudAlpha))
                        + (((AMBIENTLIGHT_COLORS[nextHour][1][0] * timeAlpha) + (AMBIENTLIGHT_COLORS[hour][1][0] * (1f - timeAlpha))) * cloudAlpha),
                    (((AMBIENTLIGHT_COLORS[nextHour][0][1] * timeAlpha) + (AMBIENTLIGHT_COLORS[hour][0][1] * (1f - timeAlpha))) * (1f - cloudAlpha))
                        + (((AMBIENTLIGHT_COLORS[nextHour][1][1] * timeAlpha) + (AMBIENTLIGHT_COLORS[hour][1][1] * (1f - timeAlpha))) * cloudAlpha),
                    (((AMBIENTLIGHT_COLORS[nextHour][0][2] * timeAlpha) + (AMBIENTLIGHT_COLORS[hour][0][2] * (1f - timeAlpha))) * (1f - cloudAlpha))
                        + (((AMBIENTLIGHT_COLORS[nextHour][1][2] * timeAlpha) + (AMBIENTLIGHT_COLORS[hour][1][2] * (1f - timeAlpha))) * cloudAlpha));
        }

        // it is somewhat darker in buildings
        if (!outside) {
            ambientLight.multiply(INSIDE_BRIGHTNESS);
        }

        Game.getLights().refresh();
    }

    /**
     * End a game session and free the required resources. In this case the
     * particle emitter that handles the rain is terminated.
     */
    @Override
    public void endSession() {
        Game.getParticleSystem().removeEmitter(rainEmitter);
        rainEmitter = null;
    }

    /**
     * Get the current ambientlight.
     * 
     * @return the current ambientlight
     */
    public SpriteColor getAmbientLight() {
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
        return fog;
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
     * Get the current precipitation strength.
     * 
     * @return the current precipitation strength
     */
    public int getPrecStrength() {
        return prec / PREC_CONVERSATION_VALUE;
    }

    /**
     * Get the current type of the precipation.
     * 
     * @return type of the precipation, possible values are {@link #RAIN} and
     *         {@link #SNOW}.
     */
    public int getPrecType() {
        return precType;
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
                coverage +=
                    (int) ((1 - ambientLight.getLuminationf()) * LIGHT_COLOR_COVERAGE);
            }

            if (precType == RAIN) {
                coverage += prec / RAIN_COVERAGE;
            } else if (precType == SNOW) {
                coverage += prec / SNOW_COVERAGE;
            }

            coverage += (int) (fog * FOG_COVERAGE);
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

    @Override
    public void initSession() {
        // nothing to do (the real init is done while creating the object)
    }

    /**
     * Check if the players character is currently outside at the fresh air.
     * 
     * @return true if the character is outside
     */
    public boolean isOutside() {
        return outside;
    }

    /**
     * Perform all render actions for the map and calculate the changes to the
     * weather.
     * 
     * @param delta the time since the last update of the weather
     */
    public void render(final int delta) {
        // no weather if inside or in debug mode
        if (!outside || !Game.getDisplay().isActive()
            || IllaClient.isDebug(Debug.mapLayout)) {
            return;
        }

        // possible change weather every second
        final long now = System.currentTimeMillis();
        if (now > nextChange) {
            changeWeather(UPDATE_INTERVAL + (int) (now - nextChange));
            nextChange = now + UPDATE_INTERVAL;
        }
        wind =
            AnimationUtility.translate(wind, windTarget, WIND_STEP,
                WIND_INTERAL_MIN, WIND_INTERAL_MAX, delta);

        renderFog();

        switch (precType) {
            case RAIN:
                break;

            case SNOW:
                renderSnow();
                break;

            default:
                break;
        }

        renderFlash();
    }

    /**
     * Perform all render actions for the map and calculate the changes to the
     * weather.
     * 
     * @param delta the time since the last update of the weather
     * @param width the width of the render area
     * @param height the height of the render area
     */
    public void render(final int delta, final int width, final int height) {
        // no weather if inside or while the display is inactive
        if (!outside || !Game.getDisplay().isActive()) {
            return;
        }

        nextChange -= delta;
        if (nextChange <= 0) {
            changeWeather(UPDATE_INTERVAL - (int) nextChange);
            nextChange += UPDATE_INTERVAL;
        }

        wind =
            AnimationUtility.translate(wind, windTarget, WIND_STEP,
                WIND_INTERAL_MIN, WIND_INTERAL_MAX, delta);

        renderFog(width, height);

        renderFlash(width, height);
    }

    /**
     * Change the current ambient light.
     * 
     * @param newAmbientLight The new ambient light color
     */
    public void setAmbientLight(final SpriteColor newAmbientLight) {
        ambientLight = newAmbientLight;
    }

    /**
     * Set the new cloud cover value.
     * 
     * @param newCloud new value for the clouds between {@link #CLOUDS_MIN} and
     *            {@link #CLOUDS_MAX}
     */
    @SuppressWarnings("nls")
    public void setCloud(final int newCloud) {
        if ((newCloud < CLOUDS_MIN) || (newCloud > CLOUDS_MAX)) {
            LOGGER.warn("Illegal clounds value: " + newCloud);
            return;
        }
        cloudTarget = newCloud;
        if (!outside) {
            cloud = newCloud;
            calculateLight();
        }
    }

    /**
     * Set the density of the fog in percent.
     * 
     * @param newFog New value for the fog.
     */
    @SuppressWarnings("nls")
    public void setFog(final int newFog) {
        if ((newFog < FOG_MINIMAL_VALUE) || (newFog > FOG_MAXIMAL_VALUE)) {
            LOGGER.warn("Illegal fog value: " + newFog);
            return;
        }

        fogTarget = (float) newFog / FOG_MAXIMAL_VALUE;
        if (!outside) {
            fog = fogTarget;
        }
    }

    /**
     * Set the new lighting intensity value.
     * 
     * @param newLightning New value for the lightnings between
     *            {@link #LIGHTNING_MIN} and {@link #LIGHTNING_MAX}
     */
    @SuppressWarnings("nls")
    public void setLightning(final int newLightning) {
        if ((lightning < LIGHTNING_MIN) || (lightning > LIGHTNING_MAX)) {
            LOGGER.warn("Illegal lightning value: " + newLightning);
            return;
        }

        lightning = newLightning;
    }

    /**
     * Set if the players character is currently inside or outside.
     * 
     * @param newOutside true if the character is outside
     */
    public void setOutside(final boolean newOutside) {
        outside = newOutside;
        calculateLight();
    }

    /**
     * Set the precipitation type and strength.
     * 
     * @param type Type of precipitation. Possible values are {@link #RAIN} and
     *            {@link #SNOW}
     * @param strength new precipitation strength value
     */
    @SuppressWarnings("nls")
    public void setPrecipitation(final int type, final int strength) {
        if ((type < 0) || (type > 2) || (strength < PREC_SERVER_MIN)
            || (strength > PREC_SERVER_MAX)) {
            LOGGER.warn("Illegal precipitation value: " + type + " strength: "
                + strength);
            return;
        }

        if (type != precType) {
            if (outside) {
                precTarget = 0;
            } else {
                precType = type;
                for (int i = 0; i < dropY.length; i++) {
                    dropY[i] = 0;
                }
                precTarget = strength * PREC_CONVERSATION_VALUE;
            }
        } else {
            precTarget = strength * PREC_CONVERSATION_VALUE;
        }
    }

    /**
     * Set the strength of the wind and the frequency and strength of the wind
     * gusts.
     * 
     * @param newWind the new value for the wind
     * @param newGusts the new value for the wind gusts
     */
    @SuppressWarnings("nls")
    public void setWind(final int newWind, final int newGusts) {
        if ((newWind < WIND_SERVER_MIN) || (newWind > WIND_SERVER_MAX)
            || (newGusts < WIND_GUST_MIN) || (newGusts > WIND_GUST_MAX)) {
            LOGGER.warn("Illegal wind value: " + newWind + " gusts: "
                + newGusts);
            return;
        }

        generalWind = newWind * WIND_CONVERSATION_VALUE;
        gusts = newGusts;

        nextGust = 0;
    }

    @Override
    public void shutdownSession() {
        // nothing to do
    }

    @Override
    public void startSession() {
        ambientLight.set(SpriteColor.COLOR_MIN);
        ambientLight.setAlpha(SpriteColor.COLOR_MAX);
        cloud = 0;
        cloudTarget = 0;
        for (int i = 0; i < MAXIMUM_DROPS; i++) {
            dropW[i] = 0.f;
            dropX[i] = 0.f;
            dropY[i] = 0.f;
        }

        fog = -1.f;
        fogTarget = 0.f;
        generalWind = 0;
        gusts = 0;
        lightning = 0;
        nextChange = 0;
        nextFlash = 0;
        nextGust = 0;
        nextThunder = 0;
        outside = false;
        prec = -1;
        precTarget = 0;
        precType = NONE;
        showFlash = 0;
        wind = 0;
        windTarget = 0;

        rainEmitter = new WeatherRainEmitter2(Game.getParticleSystem(), this);
    }

    /**
     * Update the current weather conditions to the next values. This function
     * also performs the smooth change of the values.
     * 
     * @param delta the time since the last call of this function
     */
    private void changeWeather(final int delta) {
        // fog
        fog = AnimationUtility.translate(fog, fogTarget, FOG_STEP, delta);

        if (cloud != cloudTarget) {
            cloud =
                AnimationUtility.translate(cloud, cloudTarget, CLOUDS_STEP,
                    CLOUDS_MIN, CLOUDS_MAX, delta);
            calculateLight();
        }

        // soft change between rain and snow
        prec =
            AnimationUtility.translate(prec, precTarget, PREC_STEP, 0, 500,
                delta);

        // changing winds
        if (--nextGust <= 0) {
            windTarget = generalWind;
            // vary wind by gust strength
            if (gusts > 0) {
                windTarget +=
                    rnd.nextInt(WIND_GUST_EFFECT * gusts)
                        - ((WIND_GUST_EFFECT / 2) * gusts);
            }
            // time to next wind change
            nextGust =
                rnd.nextInt(((WIND_GUST_MAX / 2) + 1) - (gusts / 2)) + 1;
        }

        // scheduling lightning
        if (lightning > 0) {
            if (nextThunder >= 0) {
                if (nextThunder-- <= 0) {
                    // play thunder sound effect
                    final SoundSource sound =
                        SoundManager.getInstance().getSoundSource();
                    sound.setSoundClip(SoundFactory.getInstance().getSound(
                        THUNDER_SOUND_ID));
                    sound.setEndOperation(SoundSource.OP_RECYCLE);
                    sound.setType(SoundSource.TYPE_EFFECT);
                    sound.setNoPosition();
                    sound.start();
                    // Toolkit.getDefaultToolkit().beep();
                    nextThunder = -1;
                }
            }

            if (nextFlash-- <= 0) {
                showFlash =
                    FLASH_WAIT_LONG
                        + rnd.nextInt((lightning / (FLASH_WAIT_LONG - 1)) + 1);
                nextThunder =
                    FLASH_WAIT_LONG - (lightning / THUNDER_WAIT_TIME);

                nextFlash =
                    (FLASH_CENTER_VALUE - (lightning / FLASH_WAIT_LONG))
                        + rnd.nextInt(FLASH_CENTER_VALUE
                            - (lightning / FLASH_WAIT_LONG));
            }
        }
    }

    /**
     * Remove a drop from the list. Eigther when it reaches the bottom of the
     * screen or when the removing is forced
     * 
     * @param i The index of the drop in the drop array
     * @param impact true to force the removing of the drop
     */
    private void removeOutside(final int i, final boolean impact) {
        if (impact || ((wind < 0) && (dropX[i] < 0))
            || ((wind > 0) && (dropX[i] > MapDisplayManager.MAP_WIDTH))) {
            dropY[i] = 0;
        }
    }

    /**
     * Render a flash if needed and count the waiting times down.
     */
    private void renderFlash() {
        if (lightning == 0) {
            return;
        }

        if ((showFlash > 0) && ((showFlash-- % FLASH_WAIT) != 0)) {
            if (FLASH_COLOR.getGreeni() == 0) {
                FLASH_COLOR.set(SpriteColor.COLOR_MAX, SpriteColor.COLOR_MAX,
                    SpriteColor.COLOR_MAX);
            }

            //Graphics
            //    .getInstance()
            //    .getDrawer()
            //    .drawRectangle(0, MapDisplayManager.MAP_BASE,
            //        MAP_OVERLAY_WIDTH, MAP_OVERLAY_HEIGHT, FLASH_COLOR);
        }

    }

    /**
     * Render a flash if needed and count the waiting times down.
     */
    private void renderFlash(final int width, final int height) {
        if (lightning == 0) {
            return;
        }

        if ((showFlash > 0) && ((showFlash-- % FLASH_WAIT) != 0)) {
            if (FLASH_COLOR.getGreeni() == 0) {
                FLASH_COLOR.set(SpriteColor.COLOR_MAX, SpriteColor.COLOR_MAX,
                    SpriteColor.COLOR_MAX);
            }

            Graphics.getInstance().getDrawer()
                .drawRectangle(0, 0, width, height, FLASH_COLOR);
        }

    }

    /**
     * Render fog by overlaying the whole map with a fog image.
     */
    private void renderFog() {
        if (fog >= FOG_MIN) {
            ambientLight.setAlpha(fog);
            //fogMap.draw(0, MapDisplayManager.MAP_BASE, MAP_OVERLAY_WIDTH,
            //    MAP_OVERLAY_HEIGHT, ambientLight);
        }
    }

    /**
     * Render fog by overlaying the whole map with a fog image.
     */
    private void renderFog(final int width, final int height) {
        if (fog >= FOG_MIN) {
            ambientLight.setAlpha(fog);
            fogMap.draw(0, 0, width, height, ambientLight);
        }
    }

    /**
     * Render animated snow flakes using the snow flake sprites.
     */
    private void renderSnow() {
        for (int i = 0; i < dropX.length; i++) {
            // draw it when visible
            if (dropY[i] > 0) {
                // snowFlake.draw((int) dropX[i], (int) dropY[i], 0, dropW[i]);
                ambientLight.setAlpha(SNOW_ALPHA);
                snowFlake.draw((int) dropX[i], (int) dropY[i],
                    (int) (dropW[i] * SNOW_WIDTH_PX),
                    (int) (dropW[i] * SNOW_WIDTH_PX), ambientLight);

                // animate it
                dropY[i] -= dropW[i] * SNOW_FALLING_SPEED;
                dropX[i] +=
                    (wind * (SNOW_MINIMAL_WIND_SPEED + dropW[i]))
                        / SNOW_WIND_SPEED;

                // splash smaller raindrops before they reach the bottom
                removeOutside(i,
                    dropY[i] < (ClientWindow.SCREEN_HEIGHT - (dropW[i]
                        * MapDisplayManager.MAP_HEIGHT * SNOW_FALLING_SPEED)));
            }
        }

    }
}

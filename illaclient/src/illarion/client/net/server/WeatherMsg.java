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
package illarion.client.net.server;

import java.io.IOException;

import illarion.client.net.CommandList;
import illarion.client.net.NetCommReader;
import illarion.client.world.Game;
import illarion.client.world.Weather;

/**
 * Servermessage: Update of the current weather (
 * {@link illarion.client.net.CommandList#MSG_WEATHER}).
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class WeatherMsg extends AbstractReply {
    /**
     * The new value for the clouds.
     */
    private short clouds;

    /**
     * The new value for the fog.
     */
    private short fog;

    /**
     * The new value for the gust strength.
     */
    private short gusts;

    /**
     * The new value for the lightning intensity.
     */
    private short lightning;

    /**
     * The new value for the precipitation strength.
     */
    private short precipitation;

    /**
     * The new value for the precipitation type.
     */
    private short precType;

    /**
     * The new value for the temperature.
     */
    private byte temperature;

    /**
     * The new wind value.
     */
    private byte wind;

    /**
     * Default constructor for the weather update message.
     */
    public WeatherMsg() {
        super(CommandList.MSG_WEATHER);
    }

    /**
     * Create a new instance of the weather update message as recycle object.
     * 
     * @return a new instance of this message object
     */
    @Override
    public WeatherMsg clone() {
        return new WeatherMsg();
    }

    /**
     * Decode the weather update data the receiver got and prepare it for the
     * execution.
     * 
     * @param reader the receiver that got the data from the server that needs
     *            to be decoded
     * @throws IOException thrown in case there was not enougth data received to
     *             decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        clouds = reader.readUByte();
        fog = reader.readUByte();
        wind = reader.readByte();
        gusts = reader.readUByte();
        precipitation = reader.readUByte();
        precType = reader.readUByte();
        lightning = reader.readUByte();
        temperature = reader.readByte();
    }

    /**
     * Execute the weather update message and send the decoded data to the rest
     * of the client.
     * 
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        final Weather weather = Game.getWeather();
        weather.setFog(fog);
        weather.setLightning(lightning);
        weather.setPrecipitation(precType, precipitation);
        weather.setWind(wind, gusts);
        weather.setCloud(clouds);

        // Gui.getInstance().getClock().setTemperature(temperature);

        return true;
    }

    /**
     * Get the data of this weather update message as string.
     * 
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("clouds: ");
        builder.append(clouds);
        builder.append(", fog: ");
        builder.append(fog);
        builder.append(", wind: ");
        builder.append(wind);
        builder.append(", gusts: ");
        builder.append(gusts);
        builder.append(", precipitation: ");
        builder.append(precipitation);
        builder.append(", precType: ");
        builder.append(precType);
        builder.append(", lightning: ");
        builder.append(lightning);
        builder.append(", temperature: ");
        builder.append(temperature);
        return toString(builder.toString());
    }
}

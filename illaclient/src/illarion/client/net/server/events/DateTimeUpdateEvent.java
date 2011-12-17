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
package illarion.client.net.server.events;

/**
 * This event is published to inform the entire client about a new date and
 * time that was send by the server.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DateTimeUpdateEvent {
    /**
     * Day of the current IG time.
     */
    private final int day;

    /**
     * Hour of the current IG time.
     */
    private final int hour;

    /**
     * Minute of the current IG time.
     */
    private final int minute;

    /**
     * Month of the current IG time.
     */
    private final int month;

    /**
     * Year of the current IG time.
     */
    private final int year;
    
    /**
     * Create and setup a instance of this event.
     * 
     * @param year the year of the new date
     * @param month the month of the new date
     * @param day the day of the new date
     * @param hour the hour of the new date
     * @param minute the minute of the new date
     */
    public DateTimeUpdateEvent(final int year, final int month, final int day, final int hour, final int minute) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }
    
    /**
     * Get the day of the new date.
     * 
     * @return the day
     */
    public int getDay() {
        return day;
    }
    
    /**
     * Get the month of the new date.
     * 
     * @return the month
     */
    public int getMonth() {
        return month;
    }
    
    /**
     * Get the year of the new date.
     * 
     * @return the year
     */
    public int getYear() {
        return year;
    }
    
    /**
     * Get the hour of the new date.
     * 
     * @return the hour
     */
    public int getHour()  {
        return hour;
    }
    
    /**
     * Get the minute of the new date.
     * 
     * @return the minute
     */
    public int getMinute() {
        return minute;
    }
}

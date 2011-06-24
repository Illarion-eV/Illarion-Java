/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute i and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Mapeditor is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Mapeditor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javolution.util.FastTable;

import org.apache.log4j.Logger;

import illarion.common.util.Location;

/**
 * This class is only used while loading the map database. It is used to collect
 * the informations on the warp fields and assign them once then loading is done
 * to the different maps.
 * 
 * @author Martin Karing
 * @since 1.01
 * @version 1.01
 */
public final class WarpLoader {
    /**
     * This is the singleton instance of this class.
     */
    private static final WarpLoader INSTANCE = new WarpLoader();

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(WarpLoader.class);

    /**
     * The name of the global warp file.
     */
    private static final String WARP_FILE_NAME = "warp.txt"; //$NON-NLS-1$

    /**
     * The list of warp fields that yet need to be processed.
     */
    private List<WarpData> warpFields;

    /**
     * Private constructor to ensure that no instances but the singleton
     * instance are created.
     */
    private WarpLoader() {
        // nothing to do
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of this class
     */
    public static WarpLoader getInstance() {
        return INSTANCE;
    }

    /**
     * Decode a line of a warp field file.
     * 
     * @param line the line to decode
     * @param origin the origin of the warp point source coordinates
     */
    public void decodeWarpFieldLine(final String line, final Location origin) {
        decodeWarpFieldLine(line, origin, false);
    }

    /**
     * Prepare all defined warp fields for use.
     * 
     * @param db the map database that stores all the maps.
     */
    public void layoutWarpPoints(final MapDatabase db) {
        if ((warpFields == null) || warpFields.isEmpty()) {
            return;
        }

        for (final WarpData field : warpFields) {
            layoutWarpPoint(db, field);
        }

        warpFields.clear();
        if (warpFields instanceof FastTable) {
            FastTable.recycle((FastTable<?>) warpFields);
        }
    }

    /**
     * Read the file of global warp points and add them to the list. This part
     * is depreciated, as the global warp point file is removed. Now ever this
     * method remains so the map editor works properly with older repositories.
     * 
     * @param directory the directory to read the map from
     */
    public void readGlobalWarpFile(final File directory) {
        final File warpFile = new File(directory, WARP_FILE_NAME);

        if (!warpFile.exists() || !warpFile.isFile() || !warpFile.canRead()) {
            // File for global warp data is optional
            return;
        }

        BufferedReader reader = null;
        final Location originLoc = Location.getInstance();
        originLoc.setSC(0, 0, 0);

        try {
            reader = new BufferedReader(new FileReader(warpFile));

            String line;
            while ((line = reader.readLine()) != null) {
                decodeWarpFieldLine(line, originLoc, true);
            }
        } catch (final IOException e) {
            // error while reading -> ignore that
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    // nothing to do
                }
            }
        }
    }

    /**
     * Add a new warp field that needs to be processed.
     * 
     * @param field the field to add
     */
    private void addWarpField(final WarpData field) {
        if (warpFields == null) {
            warpFields = FastTable.newInstance();
        }
        warpFields.add(field);
    }

    /**
     * Decode a line of a warp field file.
     * 
     * @param line the line to decode
     * @param origin the origin of the warp point source coordinates
     * @param global flag to handle this line as a global warpfield line that
     *            contains the full locations
     */
    private void decodeWarpFieldLine(final String line, final Location origin,
        final boolean global) {
        if (line == null) {
            return;
        }

        final String[] lineParts = line.split(";"); //$NON-NLS-1$
        if ((global && (lineParts.length != 6)) || (lineParts.length != 5)) {
            // apparently invalid data
            return;
        }

        int indexCnt = 0;
        final int srcX =
            Integer.parseInt(lineParts[indexCnt++]) + origin.getScX();
        final int srcY =
            Integer.parseInt(lineParts[indexCnt++]) + origin.getScY();
        int srcZ = 0;
        if (global) {
            srcZ = Integer.parseInt(lineParts[indexCnt++]);
        }
        srcZ += origin.getScZ();

        final int targetX = Integer.parseInt(lineParts[indexCnt++]);
        final int targetY = Integer.parseInt(lineParts[indexCnt++]);
        final int targetZ = Integer.parseInt(lineParts[indexCnt++]);

        final Location tempLoc = Location.getInstance();

        final WarpData data = new WarpData();
        tempLoc.setSC(srcX, srcY, srcZ);
        data.setOriginLocation(tempLoc);
        tempLoc.setSC(targetX, targetY, targetZ);
        data.setTargetLocation(tempLoc);

        tempLoc.recycle();

        addWarpField(data);
    }

    /**
     * Resolve a single warp point. Once this is done the warp point has all the
     * required references and is assigned to its parent maps.
     * 
     * @param db the map database that holds all the maps
     * @param warpField the warpfield to process
     */
    @SuppressWarnings("nls")
    private void layoutWarpPoint(final MapDatabase db, final WarpData warpField) {
        // Find the origin
        final Location tempPos = Location.getInstance();
        warpField.getOriginLocation(tempPos);
        final MapData originMap = db.getMapAt(tempPos);

        warpField.getTargetLocation(tempPos);
        final MapData targetMap = db.getMapAt(tempPos);

        if ((originMap == null) && (targetMap == null)) {
            LOGGER
                .warn("Warppoints with two illegal sides found and removed.");
            tempPos.recycle();
            return;
        } else if (originMap == null) {
            warpField.getOriginLocation(tempPos);
            LOGGER.warn("Warppoint with illegal starting location at: "
                + tempPos.toString());
        } else if (targetMap == null) {
            warpField.getTargetLocation(tempPos);
            LOGGER.warn("Warppoint with illegal target location at: "
                + tempPos.toString());
        }

        if (originMap != null) {
            warpField.setOriginMap(originMap);
            originMap.addWarpField(warpField);
        }
        if (targetMap != null) {
            warpField.setTargetMap(targetMap);
            targetMap.addWarpField(warpField);
        }
        tempPos.recycle();
    }
}

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
package illarion.client.util;

import javolution.util.FastComparator;
import javolution.util.FastTable;

import illarion.client.crash.PathfinderCrashHandler;

import illarion.common.util.Location;
import illarion.common.util.Stoppable;
import illarion.common.util.StoppableStorage;

/**
 * Pathfinder to search the best way between two locations. Using the
 * A*-algorithm.
 * 
 * @author Martin Karing
 * @since 1.2.1
 */
public final class Pathfinder extends Thread implements Stoppable {
    /**
     * The singleton instance of the pathfinder class.
     */
    private static Pathfinder instance;

    /**
     * The maximum length of a path, that is accepted, before the pathfinder
     * gives up searching.
     */
    private static final int MAX_LENGTH = 30;

    /**
     * Minimum cost of a one step to the next tile. This is used for the
     * Heuristic of the path finding
     */
    private static final int MIN_MOVE_COST = 5;

    /**
     * List of path nodes we tried already.
     */
    private FastTable<PathNode> closed;

    /**
     * The end location of the current path finding action.
     */
    private final Location endLoc;

    /**
     * List of path nodes that were not considered fully for searching yet.
     */
    private FastTable<PathNode> open;

    /**
     * The class that is supposed to receive the generated path.
     */
    private PathReceiver receiver = null;

    /**
     * The restart flag that is set true in case a new path shall be searched.
     */
    private boolean restart = false;

    /**
     * The flag that stores of the thread is running or not.
     */
    private boolean running = false;

    /**
     * The start location of the current path finding action.
     */
    private final Location startLoc;

    /**
     * Private constructor.
     */
    @SuppressWarnings("nls")
    private Pathfinder() {
        super("Pathfinder");
        setDaemon(true);

        endLoc = new Location();
        startLoc = new Location();

        start();
        StoppableStorage.getInstance().add(this);
    }

    /**
     * Get instance of singleton.
     * 
     * @return The instance of the pathfinder class.
     */
    public synchronized static Pathfinder getInstance() {
        if (instance == null) {
            instance = new Pathfinder();
            instance.setUncaughtExceptionHandler(PathfinderCrashHandler
                .getInstance());
        }
        return instance;
    }

    /**
     * Restart the pathfinder by creating a new instance and starting this one.
     */
    public synchronized static void restartPathfinder() {
        if (instance != null) {
            instance.saveShutdown();
            StoppableStorage.getInstance().remove(instance);
        }
        instance = new Pathfinder();
        instance.setUncaughtExceptionHandler(PathfinderCrashHandler
            .getInstance());
    }

    /**
     * Search a path between two locations.
     * 
     * @param pathStart the location where the path starts
     * @param pathDest the location where the path ends
     * @param pathRec the class that receives the resulting path
     */
    public void findPath(final Location pathStart, final Location pathDest,
        final PathReceiver pathRec) {
        startLoc.set(pathStart);
        endLoc.set(pathDest);
        receiver = pathRec;
        restart = true;
        synchronized (this) {
            notify();
        }
    }

    /**
     * Run the thread. This function keeps running as long as the path finder
     * thread is working. It will sleep as long as the the path finder is idle.
     * To quit this function use the {@link #saveShutdown()} function.
     */
    @SuppressWarnings("null")
    @Override
    public void run() {
        Location searchStartLoc = null;
        Location searchEndLoc = null;
        boolean searching = false;
        PathReceiver searchReceiver = null;
        int maxDepth = 0;
        boolean outputPath = false;
        while (running) {
            if (!searching && !restart) {
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (final InterruptedException e) {
                        // nothing
                    }
                }
                continue;
            }

            if (restart) {
                if (searchStartLoc == null) {
                    searchStartLoc = Location.getInstance();
                }
                if (searchEndLoc == null) {
                    searchEndLoc = Location.getInstance();
                }
                if (open == null) {
                    open = FastTable.newInstance();
                    open.setValueComparator(FastComparator.IDENTITY);
                }
                if (closed == null) {
                    closed = FastTable.newInstance();
                }
                restart = false;
                searchStartLoc.set(startLoc);
                searchEndLoc.set(endLoc);
                searchReceiver = receiver;
                if (searchReceiver == null) {
                    continue;
                }
                searching = true;

                PathNode.clearCache();
                closed.clear();
                open.clear();
                maxDepth = 0;
                outputPath = false;

                if (PathNode.getNode(searchEndLoc).isBlocked()) {
                    searching = false;
                    continue;
                }

                addToOpen(PathNode.getNode(searchStartLoc));
            }

            if (outputPath) {
                outputPath = false;
                searching = false;
                if (PathNode.getNode(searchEndLoc).getParent() == null) {
                    continue;
                }

                final Path resultPath = new Path();
                PathNode currentNode = PathNode.getNode(searchEndLoc);
                final PathNode searchNode = PathNode.getNode(searchStartLoc);
                while (!currentNode.equals(searchNode)) {
                    resultPath.prependStep(currentNode);
                    currentNode = currentNode.getParent();
                }
                resultPath.prependStep(searchNode);
                searchReceiver.handlePath(resultPath);
                searchStartLoc.recycle();
                searchStartLoc = null;
                searchEndLoc.recycle();
                searchEndLoc = null;
                FastTable.recycle(open);
                open = null;
                FastTable.recycle(closed);
                closed = null;
                continue;
            }

            if ((maxDepth > MAX_LENGTH) || open.isEmpty()) {
                searching = false;
                continue;
            }

            final PathNode currentNode = open.get(0);
            maxDepth = currentNode.getDepth();
            final Location currentLoc = currentNode.getLocation();

            if (currentNode.getLocation().equals(searchEndLoc)) {
                outputPath = true;
                continue;
            }

            removeFromOpen(currentNode);
            addToClosed(currentNode);

            final Location searchLoc = Location.getInstance();
            for (int dir = 0; dir < Location.DIR_MOVE8; ++dir) {
                searchLoc.set(currentLoc);
                searchLoc.moveSC8(dir);
                final PathNode searchNode = PathNode.getNode(searchLoc);

                if (!searchNode.isBlocked()) {
                    int newMoveCost =
                        currentNode.getCost() + searchNode.getValue();

                    if ((dir == Location.DIR_NORTHEAST)
                        || (dir == Location.DIR_NORTHWEST)
                        || (dir == Location.DIR_SOUTHEAST)
                        || (dir == Location.DIR_SOUTHWEST)) {
                        newMoveCost *= 1.1f;
                    }

                    if (newMoveCost < searchNode.getCost()) {
                        if (searchNode.getOpen()) {
                            removeFromOpen(searchNode);
                        }
                        if (searchNode.getClosed()) {
                            removeFromClosed(searchNode);
                        }
                    }

                    if (!searchNode.getOpen() && !searchNode.getClosed()) {
                        searchNode.setCost(newMoveCost);
                        searchNode.setParent(currentNode);

                        maxDepth = Math.max(maxDepth, searchNode.getDepth());

                        searchNode.setHeuristic(MIN_MOVE_COST
                            * searchLoc.getDistance(searchEndLoc));

                        addToOpen(searchNode);
                    }
                }
            }
            searchLoc.recycle();
            open.sort();
        }
    }

    /**
     * Stop the thread at the next chance.
     */
    @Override
    public void saveShutdown() {
        running = false;
        synchronized (this) {
            notify();
        }
    }

    /**
     * Start the thread.
     */
    @Override
    public synchronized void start() {
        running = true;
        super.start();
    }

    /**
     * Add a path node to the list of nodes that were considered already.
     * 
     * @param node the path node that shall be added
     */
    private void addToClosed(final PathNode node) {
        node.setClosed(true);
        closed.add(node);
    }

    /**
     * Add a path node to the list of nodes that were not fully checked yet.
     * 
     * @param node the path node that shall be added
     */
    private void addToOpen(final PathNode node) {
        open.add(node);
        node.setOpen(true);
    }

    /**
     * Remove a node from the list of tiles that were considered already.
     * 
     * @param node the node that shall be removed from the list
     */
    private void removeFromClosed(final PathNode node) {
        node.setClosed(false);
        closed.remove(node);
    }

    /**
     * Remove a node from the list of tiles that were not fully checked yet.
     * 
     * @param node the node that shall be removed from the list
     */
    private void removeFromOpen(final PathNode node) {
        node.setOpen(false);
        open.remove(node);
    }
}

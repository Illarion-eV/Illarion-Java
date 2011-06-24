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
package illarion.client.guiNG;

import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;

import illarion.client.ClientWindow;
import illarion.client.IllaClient;
import illarion.client.graphics.MarkerFactory;
import illarion.client.guiNG.elements.DragLayer;
import illarion.client.guiNG.elements.Image;
import illarion.client.guiNG.elements.ImageRepeated;
import illarion.client.guiNG.elements.ScrollArea;
import illarion.client.guiNG.elements.Widget;
import illarion.client.guiNG.elements.Window;
import illarion.client.guiNG.init.ImageInit;
import illarion.client.guiNG.init.IndicatorInit;
import illarion.client.guiNG.init.InventoryInit;
import illarion.client.guiNG.init.JournalInit;
import illarion.client.util.Lang;
import illarion.client.util.SessionMember;
import illarion.client.world.Game;

import illarion.input.KeyboardEvent;
import illarion.input.MouseEvent;

/**
 * This is the main class of the GUI environment of the client. It builds up the
 * GUI, loads and stores it, takes the forwards the input for the GUI and so on.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class GUI implements SessionMember {
    /**
     * The name of the file used to store the state of the GUI.
     */
    @SuppressWarnings("nls")
    private static final String CONFIG_FILE = "gui.dat";

    /**
     * The singleton instance of the GUI.
     */
    private static final GUI INSTANCE = new GUI();

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(GUI.class);

    /**
     * The book that is used by the GUI.
     */
    private Book book = null;

    /**
     * The window that displays the book.
     */
    private Window bookWindow;

    /**
     * The layer that displays all text on the screen.
     */
    private ChatLayer chatText;

    /**
     * The containers shown by the GUI.
     */
    private final ArrayList<Container> containers = new ArrayList<Container>();

    /**
     * The implementation of the mouse cursor.
     */
    private MouseCursor cursor;

    /**
     * The chat editor that is used.
     */
    private ChatEditor editor = null;

    /**
     * This variable stores a widget that requested exclusive mouse access. In
     * case this variable is not <code>null</code> all mouse events will be
     * forwarded only to this widget.
     */
    private Widget exclusiveMouse = null;

    /**
     * A simple object that holds the lock for the {@link #exclusiveMouse}
     * variable and is used to synchronize the access correctly.
     */
    private final Object exclusiveMouseLock = new Object();

    /**
     * This variable is set <code>true</code> in case the GUI is ready to be
     * rendered.
     */
    private boolean guiReady;

    /**
     * The indicators that are used to display health, mana and food states.
     */
    private transient Indicators indicators;

    /**
     * The handler of the input data.
     */
    private InputHandler input;

    /**
     * The inventory of the GUI.
     */
    private Inventory inventory;

    /**
     * The window that displays the inventory.
     */
    private Window inventoryWindow;

    private Journal journal;

    private Window journalWindow;

    /**
     * The overviewmap of the GUI.
     */
    private MapOverview mapoverview;

    /**
     * The window that displays the map overview.
     */
    private Window mapOverviewWindow;

    /**
     * The scrollarea of the overviewmap.
     */
    private ScrollArea mapScrollArea;

    /**
     * The root node of the GUI environment.
     */
    private Widget rootNode;

    /**
     * Private constructor to ensure that no instances but the singleton
     * instance exist.
     */
    private GUI() {
        guiReady = false;
    }

    /**
     * Get the singleton instance of the GUI.
     * 
     * @return the singleton instance of this class
     */
    public static GUI getInstance() {
        return INSTANCE;
    }

    /**
     * Adds a new container window to the GUI tree. If that ContainerID already
     * exists, no new container will be created, but the container will be
     * updated.
     * 
     * @return the new container instance
     */
    public Container addContainer(final byte ContainerID, final int[] itemId,
        final short[] count, final int[] itemX, final int[] itemY) {
        if (containers.get(ContainerID) != null) {
            containers.get(ContainerID).updateItems(ContainerID, itemId,
                count, itemX, itemY);
            return containers.get(ContainerID);
        }
        final Window parentWindow = Utility.buildWindow(500, 500);
        final Container container = new Container();
        container.updateItems(ContainerID, itemId, count, itemX, itemY);
        parentWindow.addChild(container);
        rootNode.addChild(parentWindow);
        return container;
    }

    /**
     * Draw the GUI.
     * 
     * @param delta the time since the last rendering run.
     */
    public void draw(final int delta) {
        if (!guiReady) {
            return;
        }

        Game.getMap().getMinimap().render();

        rootNode.draw(delta);
        cursor.render(delta);
    }

    @Override
    public void endSession() {
        input.saveShutdown();
        input = null;
        save();
        guiReady = false;
    }

    /**
     * Get the book that is used by the GUI.
     * 
     * @return the used book
     */
    public Book getBook() {
        return book;
    }

    /**
     * Get the window that is used to display the book.
     * 
     * @return the book window
     */
    public Window getBookWindow() {
        return bookWindow;
    }

    /**
     * Get the chat editor that is used by the GUI.
     * 
     * @return the used chat editor
     */
    public ChatEditor getChatEditor() {
        return editor;
    }

    /**
     * Get the chat layer. This layer stores and displays all text that was
     * spoken on the screen.
     * 
     * @return the chat text
     */
    public ChatLayer getChatText() {
        return chatText;
    }

    /**
     * Returns the container with the given id.
     * 
     * @param ContainerID the id of the container
     * @return the container with the given id
     */
    public Container getContainer(final short ContainerID) {
        return containers.get(ContainerID);
    }

    /**
     * Get the indicators used to display the state of health, food and mana
     * points.
     * 
     * @return the state of food, health and mana points
     */
    public Indicators getIndicators() {
        synchronized (Indicators.LOCK) {
            if (indicators == null) {
                indicators = new Indicators();
            }
            return indicators;
        }
    }

    /**
     * Get the inventory. This is needed to send the required data to the
     * inventory.
     * 
     * @return the inventory that is displayed
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Get the instance of the journal that displays the text on the GUI.
     * 
     * @return the journal instance
     */
    public Journal getJournal() {
        return journal;
    }

    /**
     * The the instance of the mouse cursor.
     * 
     * @return the mouse cursor instance used by this GUI.
     */
    public MouseCursor getMouseCursor() {
        return cursor;
    }

    /**
     * Hide the inventory from the screen.
     */
    public void hideInventory() {
        inventoryWindow.setVisible(false);
    }

    /**
     * Hide the journal.
     */
    public void hideJournal() {
        journalWindow.setVisible(false);
    }

    /**
     * Hide the map overview from the screen.
     */
    public void hideMapOverview() {
        mapOverviewWindow.setVisible(false);
    }

    @Override
    public void initSession() {
        // nothing to do
    }

    /**
     * Process a keyboard event and send it to all widgets in the GUI.
     * 
     * @param event the event to process
     */
    public void processKeyboardEvent(final KeyboardEvent event) {
        if (rootNode == null) {
            return;
        }
        if (!rootNode.handleKeyboardEvent(event)) {
            if (event.getKey() == KeyEvent.VK_ESCAPE) {
                IllaClient.ensureExit();
            }
        }
    }

    /**
     * Process a mouse event and send it to the widgets of this GUI.
     * 
     * @param event the mouse event that needs to be handled
     */
    public void processMouseEvent(final MouseEvent event) {
        if (rootNode == null) {
            return;
        }
        synchronized (exclusiveMouseLock) {
            if (exclusiveMouse != null) {
                exclusiveMouse.handleMouseEvent(event);
                return;
            }
        }

        final Widget targetWidget =
            rootNode.getWidgetAt(event.getPosX(), event.getPosY());
        if (targetWidget != null) {
            targetWidget.handleMouseEvent(event);
        }
    }

    /**
     * Register the instance of the inventory once the initialization is done.
     * This is needed so the journal can be used properly by the rest of the
     * GUI.
     * 
     * @param invent the inventory that is used to display the text
     * @param window the window the inventory is wrapped in
     */
    public void registerInventory(final Inventory invent, final Window window) {
        inventory = invent;
        inventoryWindow = window;
    }

    /**
     * Register the instance of the journal once the initialization is done.
     * This is needed so the journal can be used properly by the rest of the
     * GUI.
     * 
     * @param journ the journal that is used to display the text
     * @param window the window the journal is wrapped in
     */
    public void registerJournal(final Journal journ, final Window window) {
        journal = journ;
        journalWindow = window;
    }

    /**
     * Register the instance of the overview map once the initialization is
     * done. This is needed so the overview map can be used properly by the rest
     * of the GUI.
     * 
     * @param invent the overview map that is used to display the text
     * @param invent the ScrollArea that is initialized
     * @param window the window the overview map is wrapped in
     */
    public void registerOverviewMap(final MapOverview mapOverV,
        final ScrollArea mapScrollA, final Window window) {
        mapoverview = mapOverV;
        mapScrollArea = mapScrollA;
        mapOverviewWindow = window;
    }

    /**
     * Request a exclusive access to the mouse. In case you want to remove the
     * exclusive mouse you can just call this function with <code>null</code> as
     * its parameter.
     * 
     * @param requester the widget that needs exclusive mouse access or
     *            <code>null</code>
     */
    public void requestExclusiveMouse(final Widget requester) {
        synchronized (exclusiveMouseLock) {
            exclusiveMouse = requester;
        }
    }

    /**
     * Show the inventory on the screen.
     */
    public void showInventory() {
        inventoryWindow.setVisible(true);
    }

    /**
     * Show the journal.
     */
    public void showJournal() {
        journalWindow.setVisible(true);
    }

    /**
     * Show the map overview on the screen.
     */
    public void showMapOverview() {
        mapOverviewWindow.setVisible(true);
    }

    /**
     * Show the really exit dialog and set the functions executed on the two
     * buttons.
     * 
     * @param closeTask the task executed in case the "Yes" button is clicked
     * @param cancelTask the task executed in case the "No" button is clicked
     */
    public void showReallyExitDialog(final Runnable closeTask,
        final Runnable cancelTask) {
        if (!guiReady) {
            return;
        }
        final ReallyExit dialog = new ReallyExit();
        dialog.setCloseTask(closeTask);
        dialog.setCancelTask(cancelTask);
        rootNode.addChild(dialog);
        dialog.setVisible(true);
    }

    @Override
    public void shutdownSession() {
        // nothing to do
    }

    /**
     * Prepare and load up the GUI.
     */
    @Override
    @SuppressWarnings("unused")
    public void startSession() {
        if (true || !load()) {
            build();
        }

        editor = new ChatEditor();
        editor.setVisible(false);
        rootNode.addChild(editor);

        book = new Book();
        bookWindow = Utility.buildWindow(book.getWidth(), book.getHeight());
        bookWindow.setVisible(false);
        bookWindow.getContentPane().addChild(book);
        rootNode.addChild(bookWindow);

        cursor = new MouseCursor();

        input = new InputHandler(this);
        input.start();
        guiReady = true;
    }

    /**
     * Build up the GUI. That should be done in case loading the GUI failed.
     */
    private void build() {
        final MapDesktop desk = new MapDesktop();
        rootNode = desk;

        desk.setRelPos(0, 0);
        desk.setWallpaper(new MapWallpaper());

        chatText = new ChatLayer();
        desk.addChild(chatText);

        buildToolbar();
        buildStatusbars();

        final Widget minimap = new Minimap();
        rootNode.addChild(minimap);
        Utility.topWidgetY(minimap);
        Utility.rightWidgetX(minimap);

        inventory = new Inventory();
        inventory.setVisible(false);

        inventoryWindow =
            Utility.buildWindow(inventory.getWidth(), inventory.getHeight());
        inventoryWindow.setVisible(false);
        inventoryWindow.setPersistent(true);
        inventoryWindow.getContentPane().addChild(inventory);
        inventoryWindow.getTitleText().setText(Lang.getMsg("inventory.title")); //$NON-NLS-1$
        desk.addChild(inventoryWindow);
        inventory.setInitScript(new InventoryInit(inventory, inventoryWindow));

        mapoverview = new MapOverview();
        mapoverview.setVisible(true);

        mapScrollArea = new ScrollArea();
        mapScrollArea.setVisible(true);
        mapScrollArea.setMaxiumSize();
        mapScrollArea.setViewportSize(mapoverview.getWidth(),
            mapoverview.getHeight());
        mapScrollArea.setVirtualSize(1024, 1024);
        mapScrollArea.setScrollOffset(-256, -256);
        mapScrollArea.addChild(mapoverview);
        mapScrollArea.setDrag(true);

        mapOverviewWindow =
            Utility.buildWindow(mapoverview.getWidth(),
                mapoverview.getHeight());
        mapOverviewWindow.setVisible(false);
        mapOverviewWindow.setPersistent(true);
        mapOverviewWindow.getContentPane().addChild(mapScrollArea);
        mapOverviewWindow.getTitleText().setText(
            Lang.getMsg("inventory.title"));

        mapOverviewWindow
            .setAbsX((ClientWindow.getInstance().getScreenWidth() / 2)
                - ((ClientWindow.getInstance().getScreenWidth() / 2) / 2)); // not
                                                                            // nice
                                                                            // but
                                                                            // is
                                                                            // actually
                                                                            // works
        mapOverviewWindow.setAbsY((ClientWindow.getInstance()
            .getScreenHeight() / 2)
            - ((ClientWindow.getInstance().getScreenWidth() / 2) / 2)); // not
                                                                        // nice
                                                                        // but
                                                                        // is
                                                                        // actually
                                                                        // works

        desk.addChild(mapOverviewWindow);

        journal = new Journal();
        journal.setVisible(false);
        journal.setRelPos(0, 0);

        journalWindow = Utility.buildWindow(300, 150);
        journalWindow.setVisible(false);
        journalWindow.setPersistent(true);
        journalWindow.getContentPane().addChild(journal);
        journalWindow.getTitleText().setText(Lang.getMsg("journal.title")); //$NON-NLS-1$
        desk.addChild(journalWindow);
        journal.setMaxiumSize();
        journal.setInitScript(new JournalInit(journal, journalWindow));

        desk.refreshLayout();
    }

    /**
     * Build the GUI representations of the health, mana and food status bars.
     */
    private void buildStatusbars() {
        // health bar
        final DragLayer healthBar = new DragLayer();
        healthBar.setDragTarget(healthBar);
        healthBar.setWidth(35);
        healthBar.setHeight(194);
        healthBar.setRelPos(10, 10);
        healthBar.setShapeSource(DragLayer.SHAPE_WIDGET);
        healthBar.enableDragging();

        final Image healthBarFill = new Image();
        healthBarFill.setInitScript(ImageInit.getInstance().setImageID(
            MarkerFactory.STATUS_FILL));
        healthBarFill.setSizeToImage();
        healthBarFill.setRelPos(0, 0);

        final IndicatorMask healthBarMask = new IndicatorMask();
        healthBarMask.setMaximumValue(10000);
        healthBarMask.setHeight(healthBarFill.getHeight());
        healthBarMask.setWidth(healthBarFill.getWidth());
        healthBarMask.setRelPos(6, 0);
        healthBarMask.addChild(healthBarFill);
        healthBarMask.setInitScript(IndicatorInit.getInstance().setType(
            IndicatorInit.TYPE_HEALTH));

        healthBar.addChild(healthBarMask);

        final Image healthBarOverlay = new Image();
        healthBarOverlay.setInitScript(ImageInit.getInstance().setImageID(
            MarkerFactory.STATUS_HEALTH));
        healthBarOverlay.setSizeToImage();
        healthBarOverlay.setRelPos(0, 0);

        healthBar.addChild(healthBarOverlay);

        final Widget healthLock = Utility.buildLock(healthBar);
        healthLock.setRelY(0);
        healthBar.addChild(healthLock);
        Utility.centerWidgetX(healthLock);
        rootNode.addChild(healthBar);

        // food bar
        final DragLayer foodBar = new DragLayer();
        foodBar.setDragTarget(foodBar);
        foodBar.setWidth(35);
        foodBar.setHeight(194);
        foodBar.setRelPos(55, 10);
        foodBar.setShapeSource(DragLayer.SHAPE_WIDGET);
        foodBar.enableDragging();

        final Image foodBarFill = new Image();
        foodBarFill.setInitScript(ImageInit.getInstance().setImageID(
            MarkerFactory.STATUS_FILL));
        foodBarFill.setSizeToImage();
        foodBarFill.setRelPos(0, 0);

        final IndicatorMask foodBarMask = new IndicatorMask();
        foodBarMask.setMaximumValue(60000);
        foodBarMask.setHeight(foodBarFill.getHeight());
        foodBarMask.setWidth(foodBarFill.getWidth());
        foodBarMask.setRelPos(6, 0);
        foodBarMask.addChild(foodBarFill);
        foodBarMask.setInitScript(IndicatorInit.getInstance().setType(
            IndicatorInit.TYPE_FOOD));

        foodBar.addChild(foodBarMask);

        final Image foodBarOverlay = new Image();
        foodBarOverlay.setInitScript(ImageInit.getInstance().setImageID(
            MarkerFactory.STATUS_FOOD));
        foodBarOverlay.setSizeToImage();
        foodBarOverlay.setRelPos(0, 0);

        foodBar.addChild(foodBarOverlay);

        final Widget foodLock = Utility.buildLock(foodBar);
        foodLock.setRelY(0);
        foodBar.addChild(foodLock);
        Utility.centerWidgetX(foodLock);
        rootNode.addChild(foodBar);

        // mana bar
        final DragLayer manaBar = new DragLayer();
        manaBar.setDragTarget(manaBar);
        manaBar.setWidth(35);
        manaBar.setHeight(194);
        manaBar.setRelPos(100, 10);
        manaBar.setShapeSource(DragLayer.SHAPE_WIDGET);
        manaBar.enableDragging();

        final Image manaBarFill = new Image();
        manaBarFill.setInitScript(ImageInit.getInstance().setImageID(
            MarkerFactory.STATUS_FILL));
        manaBarFill.setSizeToImage();
        manaBarFill.setRelPos(0, 0);

        final IndicatorMask manaBarMask = new IndicatorMask();
        manaBarMask.setMaximumValue(10000);
        manaBarMask.setHeight(manaBarFill.getHeight());
        manaBarMask.setWidth(manaBarFill.getWidth());
        manaBarMask.setRelPos(6, 0);
        manaBarMask.addChild(manaBarFill);
        manaBarMask.setInitScript(IndicatorInit.getInstance().setType(
            IndicatorInit.TYPE_MANA));

        manaBar.addChild(manaBarMask);

        final Image manaBarOverlay = new Image();
        manaBarOverlay.setInitScript(ImageInit.getInstance().setImageID(
            MarkerFactory.STATUS_MANA));
        manaBarOverlay.setSizeToImage();
        manaBarOverlay.setRelPos(0, 0);

        manaBar.addChild(manaBarOverlay);

        final Widget manaLock = Utility.buildLock(manaBar);
        manaLock.setRelY(0);
        manaBar.addChild(manaLock);
        Utility.centerWidgetX(manaLock);
        rootNode.addChild(manaBar);
    }

    /**
     * Build up the tool bar of the client.
     */
    private void buildToolbar() {
        final Widget toolbarBase = new Widget();
        toolbarBase.setWidth(390);
        toolbarBase.setHeight(50);
        rootNode.addChild(toolbarBase);
        Utility.centerWidgetX(toolbarBase);
        toolbarBase.setRelY(20);

        final DragLayer toolbar = new DragLayer();
        toolbarBase.addChild(toolbar);
        toolbar.setDragTarget(null);
        toolbar.setWidth(toolbarBase.getWidth());
        toolbar.setHeight(toolbarBase.getHeight());
        toolbar.setShapeSource(DragLayer.SHAPE_CHILDREN);
        toolbar.enableDragging();

        final ImageRepeated graphicalBar = new ImageRepeated();
        graphicalBar.setWidth(toolbar.getWidth() - 26);
        graphicalBar.setHeight(11);
        graphicalBar.setRelPos(13, 3);
        graphicalBar.setInitScript(ImageInit.getInstance().setImageID(
            MarkerFactory.GUI_BAR));
        toolbar.addChild(graphicalBar);

        final Image graphicalBarLeft = new Image();
        graphicalBarLeft.setInitScript(ImageInit.getInstance().setImageID(
            MarkerFactory.GUI_BAR_LEFT));
        graphicalBarLeft.setSizeToImage();
        graphicalBarLeft.setRelPos(0, 0);
        toolbar.addChild(graphicalBarLeft);

        final Image graphicalBarRight = new Image();
        graphicalBarRight.setInitScript(ImageInit.getInstance().setImageID(
            MarkerFactory.GUI_BAR_RIGHT));
        graphicalBarRight.setSizeToImage();
        graphicalBarRight.setRelPos(
            toolbar.getWidth() - graphicalBarRight.getWidth(), 0);
        toolbar.addChild(graphicalBarRight);

        final ImageInit buttonInit =
            ImageInit.getInstance().setImageID(MarkerFactory.GUI_BAR_BUTTON);
        final Image graphicalBarButton1 = new Image();
        graphicalBarButton1.setInitScript(buttonInit);
        graphicalBarButton1.setSizeToImage();
        graphicalBarButton1.setRelPos(25, 2);
        toolbar.addChild(graphicalBarButton1);

        final Image graphicalBarButton2 = new Image();
        graphicalBarButton2.setInitScript(buttonInit);
        graphicalBarButton2.setSizeToImage();
        graphicalBarButton2.setRelPos(96, 2);
        toolbar.addChild(graphicalBarButton2);

        final Image graphicalBarButton3 = new Image();
        graphicalBarButton3.setInitScript(buttonInit);
        graphicalBarButton3.setSizeToImage();
        graphicalBarButton3.setRelPos(167, 2);
        toolbar.addChild(graphicalBarButton3);

        final Image graphicalBarButton4 = new Image();
        graphicalBarButton4.setInitScript(buttonInit);
        graphicalBarButton4.setSizeToImage();
        graphicalBarButton4.setRelPos(238, 2);
        toolbar.addChild(graphicalBarButton4);

        final Image graphicalBarButton5 = new Image();
        graphicalBarButton5.setInitScript(buttonInit);
        graphicalBarButton5.setSizeToImage();
        graphicalBarButton5.setRelPos(309, 2);
        toolbar.addChild(graphicalBarButton5);

        final Widget lockButton = Utility.buildLock(toolbar);
        lockButton.setRelPos(5, 15);
        toolbar.addChild(lockButton);
    }

    /**
     * Load the GUI settings from its default location.
     * 
     * @return <code>true</code> in case the GUI was loaded, false if not.
     */
    @SuppressWarnings("nls")
    private boolean load() {
        final File inFile = new File(Game.getPlayer().getPath(), CONFIG_FILE);
        if (!inFile.exists() || !inFile.canRead()) {
            return false;
        }

        ObjectInputStream objIn = null;
        rootNode = null;
        try {
            objIn =
                new ObjectInputStream(new BufferedInputStream(
                    new GZIPInputStream(new FileInputStream(inFile))));
            rootNode = (Widget) objIn.readObject();
        } catch (final FileNotFoundException e) {
            LOGGER.error("Loading the GUI failed. Building new.");
            rootNode = null;
        } catch (final IOException e) {
            LOGGER.error("Failed while reading the GUI file");
            rootNode = null;
        } catch (final ClassNotFoundException e) {
            LOGGER.error("Invalid GUI data, discarding everything");
            rootNode = null;
        } finally {
            if (objIn != null) {
                try {
                    objIn.close();
                } catch (final IOException e) {
                    LOGGER
                        .error("Failed closing the reading stream for the GUI");
                }
            }
            if (rootNode == null) {
                return false;
            }
            rootNode.initWidget();
            rootNode.layoutInvalid();
            rootNode.refreshLayout();
        }

        return true;
    }

    /**
     * Save the current state of a GUI to a file.
     */
    @SuppressWarnings("nls")
    private void save() {
        rootNode.cleanup();

        final File outFile = new File(Game.getPlayer().getPath(), CONFIG_FILE);
        final File tmpFile = new File(outFile.getAbsolutePath() + ".bak");
        if (tmpFile.exists()) {
            if (!tmpFile.delete()) {
                LOGGER.error("Failed deleting old temporary GUI file");
            }
        }
        if (outFile.exists()) {
            if (!outFile.renameTo(tmpFile)) {
                LOGGER.error("Failed removing old GUI file");
                return;
            }
        }
        ObjectOutputStream objOut = null;
        try {
            objOut =
                new ObjectOutputStream(new BufferedOutputStream(
                    new GZIPOutputStream(new FileOutputStream(outFile))));
            objOut.writeObject(rootNode);
            objOut.flush();
        } catch (final Exception e) {
            if (outFile.exists()) {
                if (!outFile.delete()) {
                    LOGGER.error("Failed removing old GUI file");
                }
            }
            if (tmpFile.exists()) {
                if (!tmpFile.renameTo(outFile)) {
                    LOGGER.error("Failed renaming the temporary file");
                }
            }
            LOGGER.error("Saving the state of the GUI failed.", e);
        } finally {
            if (objOut != null) {
                try {
                    objOut.close();
                } catch (final IOException e) {
                    LOGGER.error("Failed closing the GUI output stream", e);
                }
            }
        }

        if (tmpFile.exists()) {
            if (!tmpFile.delete()) {
                tmpFile.deleteOnExit();
            }
        }
        rootNode = null;
    }
}

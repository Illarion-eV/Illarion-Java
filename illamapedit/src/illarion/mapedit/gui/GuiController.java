/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Mapeditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Mapeditor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.gui;

import illarion.mapedit.Lang;
import illarion.mapedit.MapEditor;
import illarion.mapedit.data.*;
import illarion.mapedit.events.*;
import illarion.mapedit.events.map.MapPositionEvent;
import illarion.mapedit.events.map.RepaintRequestEvent;
import illarion.mapedit.events.menu.*;
import illarion.mapedit.history.HistoryManager;
import illarion.mapedit.history.ItemPlacedAction;
import illarion.mapedit.render.RendererManager;
import illarion.mapedit.util.SwingLocation;
import javolution.util.FastList;
import org.apache.log4j.Logger;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.bushe.swing.event.annotation.EventTopicSubscriber;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;

/**
 * This class should contain the model, and control the view, nicely separated from each other.
 *
 * @author Tim
 */
public class GuiController extends WindowAdapter {

    private static final Logger LOGGER = Logger.getLogger(GuiController.class);

    @Nonnull
    private final MainFrame mainFrame;

    @Nonnull
    private final List<Map> maps;

    @Nonnull
    private final HistoryManager historyManager;

    @Nonnull
    private final AnnotationChecker annotationChecker;


    @Nullable
    private Map selected;

    private boolean started;

    private boolean notSaved;

    @Nullable
    private MapSelection clipboard;

    public GuiController() {
        AnnotationProcessor.process(this);
        mainFrame = new MainFrame(this);
        historyManager = new HistoryManager();
        annotationChecker = new AnnotationChecker();
        maps = new FastList<Map>(1);
        notSaved = false;
    }

    @Nonnull
    public AnnotationChecker getAnnotationChecker() {
        return annotationChecker;
    }

    public void start() {
        if (started) {
            throw new IllegalStateException("The Controller can't be started twice");
        }
        started = true;
        startGui();
    }

    @Nonnull
    public List<Map> getMaps() {
        return maps;
    }

    public boolean isMapLoaded() {
        return !maps.isEmpty();
    }

    /**
     * This method starts up the gui.
     */
    private void startGui() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SubstanceLookAndFeel.setSkin(MapEditorConfig.getInstance().getLookAndFeel());
                mainFrame.initialize(GuiController.this);
                mainFrame.setLocationRelativeTo(null);
                mainFrame.setVisible(true);
                SplashScreen.getInstance().setVisible(false);
            }
        });

    }

    public void initialize() {

    }

    @Nullable
    public Map getSelected() {
        return selected;
    }

    public void addMap(@Nullable final Map map) {
        if (!maps.contains(map) && (map != null)) {
            maps.add(map);
            if (maps.size() == 1) {
                selected = map;
            }
        }
        EventBus.publish(new UpdateMapListEvent(maps, maps.size() - 1));
        EventBus.publish(new RepaintRequestEvent());
    }

    private void removeMap(final int index) {
        if (index < maps.size()) {
            final Map map = maps.remove(index);
            setSelectedMap(map);
        }
    }

    private void setSelectedMap(final Map map) {
        if (selected == map) {
            if (maps.isEmpty()) {
                selected = null;
            } else {
                selected = maps.get(0);
            }
        }
        EventBus.publish(new UpdateMapListEvent(maps, maps.indexOf(selected)));
        EventBus.publish(new RepaintRequestEvent());
    }

    public void removeMap(@Nullable final Map map) {
        if (maps.contains(map) && (map != null)) {
            maps.remove(map);
            setSelectedMap(map);
        }
    }

    @Nonnull
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public void setSaved(final boolean saved) {
        notSaved = !saved;
    }

    @Override
    public void windowClosing(final WindowEvent e) {
        if (notSaved) {
            if (MapDialogs.isShowSaveDialog()) {
                onMapSave(new MapSaveEvent());
            }
        }
        LOGGER.debug("Closing window.");
        MapEditor.exit();
    }

    @Override
    public void windowClosed(final WindowEvent e) {
        LOGGER.debug("Exit");
        System.exit(0);
    }

    @EventSubscriber
    public void onMapNew(final MapNewEvent e) {
        addMap(MapDialogs.showNewMapDialog(mainFrame));
    }

    @EventSubscriber
    public void onMapSave(final MapSaveEvent e) {
        for (final Map map : maps) {
            try {

                if (map != null) {
                    MapIO.saveMap(map);
                }

            } catch (IOException ex) {
                LOGGER.warn("Can't save map", ex);
                JOptionPane.showMessageDialog(MainFrame.getInstance(),
                        Lang.getMsg("gui.error.SaveMap"),
                        Lang.getMsg("gui.error"),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        notSaved = false;
        EventBus.publish(new UpdateMapListEvent(maps, maps.indexOf(selected)));
    }

    @EventSubscriber
    public void onMapSelected(@Nonnull final MapSelectedEvent e) {
        selected = maps.get(e.getIndex());
        final int x = SwingLocation.displayCoordinateX(selected.getX(), selected.getY(), 0);
        final int y = SwingLocation.displayCoordinateY(selected.getX(), selected.getY(), 0);
        final RendererManager manager = mainFrame.getRendererManager();
        manager.setZoom(1f);
        manager.setSelectedLevel(selected.getZ());
        manager.setTranslationX(-x);
        manager.setTranslationY(-y);
        manager.setDefaultTranslationX(-x);
        manager.setDefaultTranslationY(-y);

        EventBus.publish(new RepaintRequestEvent());
    }

    @EventSubscriber
    public void onMapLoaded(@Nonnull final MapLoadedEvent e) {
        if (!maps.contains(e.getMap())) {
            addMap(e.getMap());
        }
    }

    @EventSubscriber
    public void onMapOpen(@Nonnull final MapOpenEvent e) {
        MapIO.loadMap(e.getPath(), e.getName());
    }

    @EventTopicSubscriber(topic = GlobalActionEvents.CLOSE_MAP)
    public void onMapClosed(final String topic, final ActionEvent event) {
        removeMap(selected);
    }

    @EventSubscriber
    public void onMapCloseAtIndex(@Nonnull final CloseMapEvent e) {
        removeMap(e.getIndex());
    }

    @EventSubscriber
    public void onCopyClipboard(@Nonnull final ClipboardCopyEvent e) {
        if (getSelected() != null) {
            clipboard = getSelected().copySelectedTiles();
        }
    }

    @EventSubscriber
    public void onCutClipboard(@Nonnull final ClipboardCutEvent e) {
        if ((getSelected() != null) && !annotationChecker.isAnnotatedFill(getSelected())) {
            clipboard = getSelected().cutSelectedTiles();
            EventBus.publish(new RepaintRequestEvent());
            setSaved(false);
        }
    }

    @EventSubscriber
    public void onPasteClipboard(@Nonnull final PasteEvent e) {
        EventBus.publish(new DidPasteEvent());
        if ((getSelected() != null) && (clipboard != null) && !annotationChecker.isAnnotated(e.getX(),e.getY(),
                getSelected(), clipboard)) {
            getSelected().pasteTiles(e.getX(), e.getY(), clipboard);
            EventBus.publish(new RepaintRequestEvent());
            setSaved(false);
        }
    }

    @EventSubscriber
    public void onItemRemove(@Nonnull final ItemRemoveEvent e) {
        if (getSelected() != null) {
            final ItemPlacedAction historyAction = getSelected().removeItemOnActiveTile(e.getIndex());
            if (historyAction != null) {
                historyManager.addEntry(historyAction);
                setSaved(false);
            }
            EventBus.publish(new RepaintRequestEvent());
            EventBus.publish(new ItemsUpdatedEvent(getSelected().getItemsOnActiveTile()));
        }
    }

    @EventSubscriber
    public void onItemReplace(@Nonnull final ItemReplaceEvent e) {
        if (getSelected() != null) {
            getSelected().replaceItemOnActiveTile(e.getIndex(), e.getNewIndex());
            EventBus.publish(new RepaintRequestEvent());
            EventBus.publish(new ItemsUpdatedEvent(getSelected().getItemsOnActiveTile()));
        }
    }

    @EventSubscriber
    public void onMapPosition(final MapPositionEvent e) {
        if (getSelected() != null) {
            getSelected().setMapPosition(e.getMapX(), e.getMapY());
            EventBus.publish(new RepaintRequestEvent());
        }
    }

    @EventSubscriber
    public void onItemDataAnnotation(@Nonnull final TileAnnotationEvent e) {
        if (getSelected() == null) {
            return;
        }
        final MapTile tile = getSelected().getActiveTile();
        if (tile != null) {
            tile.setAnnotation(e.getText());
            EventBus.publish(new RepaintRequestEvent());
        }
    }
}

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

import illarion.common.config.Config;
import illarion.mapedit.Lang;
import illarion.mapedit.MapEditor;
import illarion.mapedit.crash.exceptions.FormatCorruptedException;
import illarion.mapedit.data.Map;
import illarion.mapedit.data.MapIO;
import illarion.mapedit.data.MapSelection;
import illarion.mapedit.events.*;
import illarion.mapedit.events.map.RepaintRequestEvent;
import illarion.mapedit.events.menu.MapNewEvent;
import illarion.mapedit.events.menu.MapOpenEvent;
import illarion.mapedit.events.menu.MapSaveEvent;
import illarion.mapedit.events.menu.MapSelectedEvent;
import illarion.mapedit.history.HistoryManager;
import illarion.mapedit.render.RendererManager;
import illarion.mapedit.resource.loaders.ImageLoader;
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

    private final SplashScreen splashScreen;

    @Nonnull
    private final List<Map> maps;

    @Nonnull
    private final HistoryManager historyManager;

    @Nullable
    private Map selected;

    private boolean started;

    private boolean notSaved;

    @Nullable
    private MapSelection clipboard;

    public GuiController(final Config config) {
        AnnotationProcessor.process(this);
        splashScreen = SplashScreen.getInstance();
        mainFrame = new MainFrame(this, config);
        historyManager = new HistoryManager();
        maps = new FastList<Map>(1);
        notSaved = false;
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
                SubstanceLookAndFeel.setSkin("org.pushingpixels.substance.api.skin.OfficeSilver2007Skin");
                mainFrame.initialize(GuiController.this);
                mainFrame.setLocationRelativeTo(null);
                mainFrame.setVisible(true);
                splashScreen.setVisible(false);
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

    public void removeMap(@Nullable final Map map) {
        if (maps.contains(map) && (map != null)) {
            maps.remove(map);
            if (selected == map) {
                if (maps.isEmpty()) {
                    selected = null;
                } else {
                    selected = maps.get(0);
                }
            }
        }
        EventBus.publish(new UpdateMapListEvent(maps, maps.indexOf(selected)));
        EventBus.publish(new RepaintRequestEvent());
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
    public void onMapOpen(@Nonnull final MapOpenEvent e) {
        try {
            final Map[] map;
            if (e.getPath() == null) {
                map = MapDialogs.showOpenMapDialog(mainFrame);
            } else {
                map = new Map[1];
                map[0] = MapIO.loadMap(e.getPath(), e.getName());
            }
            for (final Map m : map) {
                if (!maps.contains(m)) {
                    addMap(m);
                }
            }
        } catch (FormatCorruptedException ex) {
            LOGGER.warn("Format wrong.", ex);
            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                    ex.getMessage(),
                    Lang.getMsg("gui.error"),
                    JOptionPane.ERROR_MESSAGE,
                    ImageLoader.getImageIcon("messagebox_critical"));
        } catch (IOException ex) {
            LOGGER.warn("Can't load map", ex);
            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                    Lang.getMsg("gui.error.LoadMap"),
                    Lang.getMsg("gui.error"),
                    JOptionPane.ERROR_MESSAGE,
                    ImageLoader.getImageIcon("messagebox_critical"));
        }
    }

    @EventTopicSubscriber(topic = GlobalActionEvents.CLOSE_MAP)
    public void onMapClosed(final String topic, final ActionEvent event) {
        removeMap(selected);
    }

    @EventSubscriber
    public void onCopyClipboard(@Nonnull final ClipboardCopyEvent e) {
        if (getSelected() != null) {
            clipboard = getSelected().copySelectedTiles();
        }
    }

    @EventSubscriber
    public void onCutClipboard(@Nonnull final ClipboardCutEvent e) {
        if (getSelected() != null) {
            clipboard = getSelected().cutSelectedTiles();
            EventBus.publish(new RepaintRequestEvent());
            setSaved(false);
        }
    }

    @EventSubscriber
    public void onPasteClipboard(@Nonnull final PasteEvent e) {
        EventBus.publish(new DidPasteEvent());
        if (getSelected() != null && clipboard != null) {
            getSelected().pasteTiles(e.getX(),e.getY(),clipboard);
            EventBus.publish(new RepaintRequestEvent());
            setSaved(false);
        }
    }

    @EventSubscriber
    public void onSelectTool(@Nonnull final ToolSelectedEvent e) {
        if (getSelected() != null) {
            getSelected().removeActiveTile();
        }
    }
}

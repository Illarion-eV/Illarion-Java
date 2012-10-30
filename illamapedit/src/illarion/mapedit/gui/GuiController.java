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
import illarion.mapedit.crash.exceptions.UnhandlableException;
import illarion.mapedit.data.Map;
import illarion.mapedit.data.MapIO;
import illarion.mapedit.events.MessageStringEvent;
import illarion.mapedit.events.UpdateMapListEvent;
import illarion.mapedit.events.map.RepaintRequestEvent;
import illarion.mapedit.events.menu.MapNewEvent;
import illarion.mapedit.events.menu.MapOpenEvent;
import illarion.mapedit.events.menu.MapSaveEvent;
import illarion.mapedit.events.menu.MapSelectedEvent;
import illarion.mapedit.history.HistoryManager;
import illarion.mapedit.render.RendererManager;
import illarion.mapedit.resource.ResourceManager;
import illarion.mapedit.resource.loaders.ImageLoader;
import illarion.mapedit.resource.loaders.ItemLoader;
import illarion.mapedit.resource.loaders.TextureLoaderAwt;
import illarion.mapedit.resource.loaders.TileLoader;
import illarion.mapedit.util.SwingLocation;
import javolution.util.FastList;
import org.apache.log4j.Logger;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.List;

/**
 * This class should contain the model, and control the view, nicely separated from each other.
 *
 * @author Tim
 */
public class GuiController implements WindowListener {

    private static final Logger LOGGER = Logger.getLogger(GuiController.class);

    private final MainFrame mainFrame;

    private final SplashScreen splashScreen;

    private final ResourceManager resourceManager;

    private final List<Map> maps;

    private final HistoryManager historyManager;

    private Map selected;

    private boolean started;

    private boolean saved;

    public GuiController(final Config config) {
        AnnotationProcessor.process(this);
        splashScreen = SplashScreen.getInstance();
        mainFrame = new MainFrame(this, config);
        resourceManager = ResourceManager.getInstance();
        historyManager = new HistoryManager();
        maps = new FastList<Map>(1);
        saved = true;
    }

    public void start() {
        if (started) {
            throw new IllegalStateException("The Controller can't be started twice");
        }
        started = true;

        loadResources();
        startGui();
    }

    private void loadResources() {
        resourceManager.addResources(
                ImageLoader.getInstance(),
                TextureLoaderAwt.getInstance(),
                TileLoader.getInstance(),
                ItemLoader.getInstance()
        );
        while (resourceManager.hasNextToLoad()) {
            try {
                LOGGER.debug("Loading " + resourceManager.getNextDescription());
                EventBus.publish(new MessageStringEvent("Loading " + resourceManager.getNextDescription()));
                resourceManager.loadNext();
            } catch (IOException e) {
                LOGGER.warn(resourceManager.getPrevDescription() + " failed!");
//                Crash the editor
                throw new UnhandlableException("Can't load " + resourceManager.getPrevDescription(), e);
            }
        }
    }

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
        mainFrame.initialize(this);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SubstanceLookAndFeel.setSkin("org.pushingpixels.substance.api.skin.OfficeSilver2007Skin");
                mainFrame.setLocationRelativeTo(null);
                mainFrame.setVisible(true);
                splashScreen.setVisible(false);
            }
        });

    }

    public void initialize() {
        splashScreen.setVisible(true);
    }

    public Map getSelected() {
        return selected;
    }

    public void addMap(final Map map) {
        if (!maps.contains(map) && (map != null)) {
            maps.add(map);
            if (maps.size() == 1) {
                selected = map;
            }
        }
        EventBus.publish(new UpdateMapListEvent(maps));
        EventBus.publish(new RepaintRequestEvent());
    }

    public void removeMap(final Map map) {
        if (maps.contains(map) && (map != null)) {
            maps.add(map);
        }
        EventBus.publish(new UpdateMapListEvent(maps));
        EventBus.publish(new RepaintRequestEvent());
    }


    @EventSubscriber
    public void onMapSelected(final MapSelectedEvent e) {
        selected = maps.get(e.getIndex());
        int x = SwingLocation.displayCoordinateX(selected.getX(), selected.getY(), 0);
        int y = SwingLocation.displayCoordinateY(selected.getX(), selected.getY(), 0);
        RendererManager manager = mainFrame.getRendererManager();
        manager.setZoom(1f);
        manager.setSelectedLevel(selected.getZ());
        manager.setTranslationX(-x);
        manager.setTranslationY(-y);
        manager.setDefaultTranslationX(-x);
        manager.setDefaultTranslationY(-y);

        EventBus.publish(new RepaintRequestEvent());
    }


    @Override
    public void windowOpened(final WindowEvent e) {
        //DO NOTHING
    }

    @Override
    public void windowClosing(final WindowEvent e) {
        if (saved) {
            LOGGER.debug("Closing window.");
            mainFrame.dispose();
            MapEditor.exit();
        } else {
            if (MapDialogs.isShowSaveDialog()) {
                onMapSave(new MapSaveEvent());
            }
        }
    }

    @Override
    public void windowClosed(final WindowEvent e) {
        System.exit(0);
    }

    @Override
    public void windowIconified(final WindowEvent e) {
        //DO NOTHING
    }

    @Override
    public void windowDeiconified(final WindowEvent e) {
        //DO NOTHING
    }

    @Override
    public void windowActivated(final WindowEvent e) {
        //DO NOTHING
    }

    @Override
    public void windowDeactivated(final WindowEvent e) {

    }

    @EventSubscriber
    public void onMapNew(final MapNewEvent e) {
        addMap(MapDialogs.showNewMapDialog(mainFrame));
    }

    @EventSubscriber
    public void onMapSave(final MapSaveEvent e) {
        for (Map map : maps) {
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
        saved = true;
        EventBus.publish(new UpdateMapListEvent(maps));
    }

    @EventSubscriber
    public void onMapOpen(final MapOpenEvent e) {
        try {
            if (e.getPath() == null) {
                addMap(MapDialogs.showOpenMapDialog(mainFrame));
            } else {
                addMap(MapIO.loadMap(e.getPath(), e.getName()));
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

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public void setSaved(final boolean saved) {
        this.saved = saved;
    }
}

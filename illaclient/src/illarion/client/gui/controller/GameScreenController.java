/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.gui.controller;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Droppable;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.ScrollPanel;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

import illarion.client.gui.*;
import illarion.client.world.World;

public class GameScreenController
        implements ScreenController {

    private Screen screen;
    private Label main;
    private TextField chatMsg;
    private ScrollPanel chatLog;
    private Nifty parentNifty;

    private Droppable mapDropTarget;

    private GUIChatHandler chatHandler;
    private GameMapClickHandler mapClickHandler;
    private GameMapDoubleClickHandler mapDoubleClickHandler;
    private GameMapDragHandler mapDragHandler;
    private GUIInventoryHandler inventoryHandler;
    private CharListHandler charListHandler;

    private boolean notifyResolutionChanged;

    @SuppressWarnings("unchecked")
    @Override
    public void bind(final Nifty nifty, final Screen screen) {
        this.screen = screen;
        parentNifty = nifty;
        chatMsg = screen.findNiftyControl("chatMsg", TextField.class);
        chatLog = screen.findNiftyControl("chatPanel", ScrollPanel.class);

        mapDropTarget = screen.findNiftyControl("mapDropTarget", Droppable.class);

        chatHandler = new GUIChatHandler(screen, chatLog, chatMsg);
        chatMsg.getElement().addInputHandler(chatHandler);

        inventoryHandler = new GUIInventoryHandler();
        inventoryHandler.bind(nifty, screen);
        mapClickHandler = new GameMapClickHandler();
        mapClickHandler.bind(parentNifty, screen);
        mapDoubleClickHandler = new GameMapDoubleClickHandler();
        mapDoubleClickHandler.bind(parentNifty, screen);
        mapDragHandler = new GameMapDragHandler();
        mapDragHandler.bind(parentNifty, screen);

        charListHandler = new CharListHandler();
        charListHandler.bind(nifty, screen);
    }

    @Override
    public void onStartScreen() {
        if (notifyResolutionChanged) {
            parentNifty.resolutionChanged();
            notifyResolutionChanged = false;
        }
        World.getChatHandler().addChatReceiver(chatHandler);
        parentNifty.subscribeAnnotations(mapDragHandler);
        inventoryHandler.onStartScreen();
        charListHandler.onStartScreen();
    }

    @Override
    public void onEndScreen() {
        World.getChatHandler().removeChatReceiver(chatHandler);
    }

    public void resolutionChanged() {
        notifyResolutionChanged = true;
    }
}

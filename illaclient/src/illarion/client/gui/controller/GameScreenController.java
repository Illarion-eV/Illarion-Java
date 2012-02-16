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
package illarion.client.gui.controller;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Droppable;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.gui.GUIChatHandler;
import illarion.client.gui.GUIInventoryHandler;
import illarion.client.gui.GameMapDoubleClickHandler;
import illarion.client.gui.GameMapDragHandler;
import illarion.client.gui.xml.GameMapClickHandler;
import illarion.client.world.World;

public class GameScreenController implements ScreenController {

    private Screen screen;
    private Label main;
    private TextField chatMsg;
    private ListBox<String> chatLog;
    private Nifty parentNifty;
    
    private Droppable mapDropTarget;
    
    private GUIChatHandler chatHandler;
    private GameMapClickHandler mapClickHandler;
    private GameMapDoubleClickHandler mapDoubleClickHandler;
    private GameMapDragHandler mapDragHandler;
    private GUIInventoryHandler inventoryHandler;
    
    private boolean notifyResolutionChanged;

    @SuppressWarnings("unchecked")
    @Override
    public void bind(final Nifty nifty, final Screen screen) {
        this.screen = screen;
        parentNifty = nifty;
        chatMsg = screen.findNiftyControl("chatMsg", TextField.class);
        chatLog =
            (ListBox<String>) screen
                .findNiftyControl("chatLog", ListBox.class);
        
        mapDropTarget = screen.findNiftyControl("mapDropTarget", Droppable.class);
        
        chatHandler = new GUIChatHandler(screen, chatLog, chatMsg);
        chatMsg.getElement().addInputHandler(chatHandler);
        
        screen.findElementByName("chatLog#scrollpanel#panel").setFocusable(false);
        screen.findElementByName("chatLog#scrollpanel#vertical-scrollbar").setFocusable(false);

        inventoryHandler = new GUIInventoryHandler();
        inventoryHandler.bind(nifty, screen);
        mapClickHandler = new GameMapClickHandler();
        mapClickHandler.bind(parentNifty, screen);
        mapDoubleClickHandler = new GameMapDoubleClickHandler();
        mapDoubleClickHandler.bind(parentNifty, screen);
        mapDragHandler = new GameMapDragHandler();
        mapDragHandler.bind(parentNifty, screen);
    }

    @Override
    public void onStartScreen() {
        if(notifyResolutionChanged)
        {
            parentNifty.resolutionChanged();
            notifyResolutionChanged = false;
        }
        World.getChatHandler().addChatReceiver(chatHandler);
        parentNifty.subscribeAnnotations(mapDragHandler);
        inventoryHandler.onStartScreen();
    }

    @Override
    public void onEndScreen() {
        World.getChatHandler().removeChatReceiver(chatHandler);
    }
    
    public void resolutionChanged(){
        notifyResolutionChanged = true;
    }
}

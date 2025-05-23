/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client.input;

import illarion.client.IllaClient;
import illarion.client.gui.GameGui;
import illarion.client.net.client.CloseShowcaseCmd;
import illarion.client.net.client.PickUpAllItemsCmd;
import illarion.client.util.Lang;
import illarion.client.world.Char;
import illarion.client.world.MapTile;
import illarion.client.world.Player;
import illarion.client.world.World;
import illarion.client.world.items.InventorySlot;
import illarion.client.world.movement.KeyboardMovementHandler;
import illarion.common.config.Config;
import illarion.common.config.ConfigChangedEvent;
import illarion.common.types.Direction;
import illarion.common.types.ServerCoordinate;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventTopicSubscriber;
import org.illarion.engine.input.Input;
import org.illarion.engine.input.Key;
import illarion.common.types.ItemId;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.Set;

/**
 * This class is used to generate events based on keys that got pressed.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class KeyMapper {
    @Nonnull
    private final Input input;

    @Nonnull
    private final Set<Key> keyPressed;
    private boolean useWasdWalking;
    private boolean useZqsdWalking;
    private boolean useItemWithE;

    public KeyMapper(@Nonnull Input input) {
        this.input = input;
        keyPressed = EnumSet.noneOf(Key.class);

        applyWasdWalkSettings();
        applyZqsdWalkSettings();
        applyuseItemWithE();
        AnnotationProcessor.process(this);
    }

    private static void handleEscape() {
        GameGui gameGui = World.getGameGui();

        if (gameGui.getChatGui().isChatBoxActive()) {
            gameGui.getChatGui().deactivateChatBox(false);
            return;
        }

        if (gameGui.getCloseGameGui().isClosingDialogShown()) {
            gameGui.getCloseGameGui().hideClosingDialog();
        } else {
            gameGui.getCloseGameGui().showClosingDialog();
        }
    }

    private static void startMovingTowards(@Nonnull Direction direction, boolean firstPressed) {
        if (World.getPlayer().isLocationSet()) {
            KeyboardMovementHandler handler = World.getPlayer().getMovementHandler().getKeyboardHandler();
            handler.startMovingTowards(direction);
            if (firstPressed) {
                handler.assumeControl();
            }
        }
    }

    private static void cyclePermanentAvatarTag() {
        Config config = IllaClient.getCfg();
        int currentEntry = config.getInteger("showAvatarTagPermanently");
        int newEntry = (currentEntry + 1) % 3;

        switch (newEntry) {
            case 0:
                World.getGameGui().getInformGui().showServerInform(Lang.getMsg("info.nameDisplay.noNames"));
                break;
            case 1:
                World.getGameGui().getInformGui().showServerInform(Lang.getMsg("info.nameDisplay.humanNames"));
                break;
            case 2:
                World.getGameGui().getInformGui().showServerInform(Lang.getMsg("info.nameDisplay.allNames"));
                break;
            default:
                break;
        }
        config.set("showAvatarTagPermanently", newEntry);
    }

    private void applyWasdWalkSettings() {
        useWasdWalking = IllaClient.getCfg().getBoolean("wasdWalk");
    }

    private void applyZqsdWalkSettings() {
        useZqsdWalking = IllaClient.getCfg().getBoolean("zqsdWalk");
    }

    private void applyuseItemWithE() {
        useItemWithE = IllaClient.getCfg().getBoolean("useItemWithE");
    }

    @EventTopicSubscriber(topic = "wasdWalk")
    public void onWasdSettingsChanged(@Nonnull String configKey, @Nonnull ConfigChangedEvent event) {
        if ("wasdWalk".equals(configKey)) {
            applyWasdWalkSettings();
        }
    }

    @EventTopicSubscriber(topic = "zqsdWalk")
    public void onZqsdSettingsChanged(@Nonnull String configKey, @Nonnull ConfigChangedEvent event) {
        if ("zqsdWalk".equals(configKey)) {
            applyZqsdWalkSettings();
        }
    }

    @EventTopicSubscriber(topic = "useItemWithE")
    public void onuseItemWithEChanged(@Nonnull String configKey, @Nonnull ConfigChangedEvent event) {
        if ("useItemWithE".equals(configKey)) {
            applyuseItemWithE();
        }
    }

    public void handleKeyReleasedInput(@Nonnull Key key) {
        keyPressed.remove(key);
        KeyboardMovementHandler handler = World.getPlayer().getMovementHandler().getKeyboardHandler();
        IllaClient.lastInputTime = System.currentTimeMillis() / 1000L;
        switch (key) {
            case CursorUp:
            case NumPad8:
                handler.stopMovingTowards(Direction.NorthEast);
                break;
            case W:
                if (useWasdWalking) {
                    handler.stopMovingTowards(Direction.NorthEast);
                }
                break;
            case Z:
                if (useZqsdWalking) {
                    handler.stopMovingTowards(Direction.NorthEast);
                }
                break;

            case CursorLeft:
            case NumPad4:
                handler.stopMovingTowards(Direction.NorthWest);
                break;
            case A:
                if (useWasdWalking) {
                    handler.stopMovingTowards(Direction.NorthWest);
                }
                break;
            case Q:
                if (useZqsdWalking) {
                    handler.stopMovingTowards(Direction.NorthWest);
                }

            case CursorDown:
            case NumPad2:
                handler.stopMovingTowards(Direction.SouthWest);
                break;
            case S:
                if (useWasdWalking || useZqsdWalking) {
                    handler.stopMovingTowards(Direction.SouthWest);
                }
                break;

            case CursorRight:
            case NumPad6:
                handler.stopMovingTowards(Direction.SouthEast);
                break;
            case D:
                if (useWasdWalking || useZqsdWalking) {
                    handler.stopMovingTowards(Direction.SouthEast);
                }
                break;

            case NumPad1:
            case End:
                handler.stopMovingTowards(Direction.West);
                break;

            case NumPad3:
            case PageDown:
                handler.stopMovingTowards(Direction.South);
                break;

            case NumPad7:
            case Home:
                handler.stopMovingTowards(Direction.North);
                break;

            case NumPad9:
            case PageUp:
                handler.stopMovingTowards(Direction.East);
                break;
        }
    }

    public void handleKeyPressedInput(@Nonnull Key key) {
        boolean firstPressed = keyPressed.add(key);
        IllaClient.lastInputTime = System.currentTimeMillis() / 1000L;
        switch (key) {
            case B:
                if (firstPressed) {
                    if (World.getPlayer().hasContainer(0)) {
                        World.getPlayer().removeContainer(0);
                        World.getNet().sendCommand(new CloseShowcaseCmd(0));
                    } else {
                        InventorySlot slot = World.getPlayer().getInventory().getItem(0);
                        if (slot.containsItem()) {
                            slot.getInteractive().openContainer();
                        }
                    }
                }
                break;
            case M:
                World.getGameGui().getMiniMapGui().toggleMiniMap();
                break;
            case C:
                if (firstPressed) {
                    World.getGameGui().getSkillGui().toggleSkillWindow();
                }
                break;
            case I:
                if (firstPressed) {
                    World.getGameGui().getInventoryGui().toggleInventory();
                }
                break;
            case Q:
                if (useZqsdWalking) {

                    startMovingTowards(Direction.NorthWest, firstPressed);

                    break;
                }
            case J:
                if (firstPressed) {
                    World.getGameGui().getQuestGui().toggleQuestLog();
                }
                break;
            case F1:
                if (firstPressed) {
                    World.getGameGui().getDocumentationGui().toggleDocumentation();
                }
                break;
            case P:
                if (firstPressed) {
                    World.getNet().sendCommand(new PickUpAllItemsCmd());
                }
                break;
            case Space:
                if (firstPressed) {
                    Player player = World.getPlayer();
                    Char character = player.getCharacter();
                    ServerCoordinate front = new ServerCoordinate(player.getLocation(), character.getDirection());
                    MapTile mapTile = World.getMap().getMapAt(front);
                    if (mapTile != null) {
                        mapTile.getInteractive().use();
                    }
                }
                break;

            case E:
                if (useItemWithE) {
                    illarion.client.world.items.InventorySlot slot = World.getPlayer().getInventory().getItem(6);
                    if (!slot.containsItem()) {
                        slot = World.getPlayer().getInventory().getItem(5);
                        if (!slot.containsItem()) {
                            break;
                            }
                        else {
                            slot.getInteractive().use();
                            break;
                        }
                    }

                    ItemId itemId = slot.getItemID();
                    Integer occupied = 228;

                    if (itemId.getValue() == occupied){
                        slot = World.getPlayer().getInventory().getItem(5);
                        if (!slot.containsItem()) {
                        break;
                        }
                    }
                    slot.getInteractive().use();
                }
                break;

            case Enter:
                if (firstPressed) {
                    World.getGameGui().getChatGui().activateChatBox();
                }
                break;

            case CursorUp:
            case NumPad8:
                startMovingTowards(Direction.NorthEast, firstPressed);
                break;
            case W:
                if (useWasdWalking) {
                    startMovingTowards(Direction.NorthEast, firstPressed);
                }
                break;
            case Z:
                if (useZqsdWalking) {
                    startMovingTowards(Direction.NorthEast, firstPressed);
                }
                break;

            case CursorLeft:
            case NumPad4:
                startMovingTowards(Direction.NorthWest, firstPressed);
                break;
            case A:
                if (useWasdWalking) {
                    startMovingTowards(Direction.NorthWest, firstPressed);
                }
                break;

            case CursorDown:
            case NumPad2:
                startMovingTowards(Direction.SouthWest, firstPressed);
                break;
            case S:
                if (useWasdWalking || useZqsdWalking) {
                    startMovingTowards(Direction.SouthWest, firstPressed);
                }
                break;

            case CursorRight:
            case NumPad6:
                startMovingTowards(Direction.SouthEast, firstPressed);
                break;
            case D:
                if (useWasdWalking || useZqsdWalking) {
                    startMovingTowards(Direction.SouthEast, firstPressed);
                }
                break;

            case NumPad1:
            case End:
                startMovingTowards(Direction.West, firstPressed);
                break;

            case NumPad3:
            case PageDown:
                startMovingTowards(Direction.South, firstPressed);
                break;

            case NumPad7:
            case Home:
                startMovingTowards(Direction.North, firstPressed);
                break;

            case NumPad9:
            case PageUp:
                startMovingTowards(Direction.East, firstPressed);
                break;

            case LeftCtrl:
            case RightCtrl:
                if (!input.isAnyKeyDown(Key.RightAlt)) {
                    World.getGameGui().getGameMapGui().toggleRunMode();
                }
                break;
            case F12:
                if (firstPressed) {
                    cyclePermanentAvatarTag();
                }
                break;
            case Escape:
                if (firstPressed) {
                    handleEscape();
                }
                break;
            case F:
                if (firstPressed) {
                    if (input.isAnyKeyDown(Key.LeftShift, Key.RightShift)) {
                        World.getPlayer().getCombatHandler().standDown();
                    } else {
                        World.getPlayer().getCombatHandler().attackNearestMonster();
                    }
                }
                break;
        }
    }
}
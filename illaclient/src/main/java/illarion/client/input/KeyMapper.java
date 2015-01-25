/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
import illarion.client.world.World;
import illarion.client.world.items.InventorySlot;
import illarion.client.world.movement.KeyboardMovementHandler;
import illarion.common.config.Config;
import illarion.common.config.ConfigChangedEvent;
import illarion.common.types.Direction;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventTopicSubscriber;
import org.illarion.engine.input.Input;
import org.illarion.engine.input.Key;

import javax.annotation.Nonnull;

/**
 * This class is used to generate events based on keys that got pressed.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class KeyMapper {
    private final Input input;

    public KeyMapper(Input input) {
        this.input = input;

        applyWasdWalkSettings();
        AnnotationProcessor.process(this);
    }

    private boolean useWasdWalking;

    private void applyWasdWalkSettings() {
        useWasdWalking = IllaClient.getCfg().getBoolean("wasdWalk");
    }

    @EventTopicSubscriber(topic = "wasdWalk")
    public void onWasdSettingsChanged(@Nonnull String configKey, @Nonnull ConfigChangedEvent event) {
        if ("wasdWalk".equals(configKey)) {
            applyWasdWalkSettings();
        }
    }

    public void handleKeyReleasedInput(@Nonnull Key key) {
        KeyboardMovementHandler handler = World.getPlayer().getMovementHandler().getKeyboardHandler();
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

            case CursorLeft:
            case NumPad4:
                handler.stopMovingTowards(Direction.NorthWest);
                break;
            case A:
                if (useWasdWalking) {
                    handler.stopMovingTowards(Direction.NorthWest);
                }
                break;

            case CursorDown:
            case NumPad2:
                handler.stopMovingTowards(Direction.SouthWest);
                break;
            case S:
                if (useWasdWalking) {
                    handler.stopMovingTowards(Direction.SouthWest);
                }
                break;

            case CursorRight:
            case NumPad6:
                handler.stopMovingTowards(Direction.SouthEast);
                break;
            case D:
                if (useWasdWalking) {
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
        switch (key) {
            case B:
                if (World.getPlayer().hasContainer(0)) {
                    World.getPlayer().removeContainer(0);
                    World.getNet().sendCommand(new CloseShowcaseCmd(0));
                } else {
                    InventorySlot slot = World.getPlayer().getInventory().getItem(0);
                    if (slot.containsItem()) {
                        slot.getInteractive().openContainer();
                    }
                }
                break;
            case C:
                World.getGameGui().getSkillGui().toggleSkillWindow();
                break;
            case I:
                World.getGameGui().getInventoryGui().toggleInventory();
                break;
            case Q:
            case J:
                World.getGameGui().getQuestGui().toggleQuestLog();
                break;
            case F1:
                World.getGameGui().getDocumentationGui().toggleDocumentation();
                break;
            case P:
                World.getNet().sendCommand(new PickUpAllItemsCmd());
                break;
            case Enter:
                World.getGameGui().getChatGui().activateChatBox();
                break;

            case CursorUp:
            case NumPad8:
                startMovingTowards(Direction.NorthEast);
                break;
            case W:
                if (useWasdWalking) {
                    startMovingTowards(Direction.NorthEast);
                }
                break;

            case CursorLeft:
            case NumPad4:
                startMovingTowards(Direction.NorthWest);
                break;
            case A:
                if (useWasdWalking) {
                    startMovingTowards(Direction.NorthWest);
                }
                break;

            case CursorDown:
            case NumPad2:
                startMovingTowards(Direction.SouthWest);
                break;
            case S:
                if (useWasdWalking) {
                    startMovingTowards(Direction.SouthWest);
                }
                break;

            case CursorRight:
            case NumPad6:
                startMovingTowards(Direction.SouthEast);
                break;
            case D:
                if (useWasdWalking) {
                    startMovingTowards(Direction.SouthEast);
                }
                break;

            case NumPad1:
            case End:
                startMovingTowards(Direction.West);
                break;

            case NumPad3:
            case PageDown:
                startMovingTowards(Direction.South);
                break;

            case NumPad7:
            case Home:
                startMovingTowards(Direction.North);
                break;

            case NumPad9:
            case PageUp:
                startMovingTowards(Direction.East);
                break;

            case LeftCtrl:
            case RightCtrl:
                if (!input.isAnyKeyDown(Key.RightAlt)) {
                    World.getGameGui().getGameMapGui().toggleRunMode();
                }
                break;
            case F12:
                cyclePermanentAvatarTag();
                break;
            case Escape:
                handleEscape();
                break;
        }
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

    private static void startMovingTowards(@Nonnull Direction direction) {
        KeyboardMovementHandler handler = World.getPlayer().getMovementHandler().getKeyboardHandler();
        handler.startMovingTowards(direction);
        handler.assumeControl();
    }

    private static void cyclePermanentAvatarTag() {
        Config config = IllaClient.getCfg();
        config.set("showAvatarTagPermanently", (config.getInteger("showAvatarTagPermanently") + 1) % 3);
    }
}

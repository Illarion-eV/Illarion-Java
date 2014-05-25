/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
import illarion.client.net.client.CloseShowcaseCmd;
import illarion.client.net.client.PickUpAllItemsCmd;
import illarion.client.world.World;
import illarion.client.world.items.InventorySlot;
import illarion.client.world.movement.KeyboardMovementHandler;
import illarion.common.config.ConfigChangedEvent;
import illarion.common.types.Location;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventTopicSubscriber;
import org.illarion.engine.input.Input;
import org.illarion.engine.input.Key;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;

/**
 * This class is used to generate events based on keys that got pressed.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class KeyMapper {
    @Nonnull
    private final Map<Key, String> inputMap;

    private final Input input;

    public KeyMapper(Input input) {
        inputMap = new EnumMap<>(Key.class);

        inputMap.put(Key.Escape, "CloseGame");

        this.input = input;

        applyWasdWalkSettings();
        AnnotationProcessor.process(this);
    }

    private boolean useWasdWalking;

    private boolean useClassicWasdWalking;

    private void applyWasdWalkSettings() {
        useWasdWalking = IllaClient.getCfg().getBoolean("wasdWalk");
        useClassicWasdWalking = IllaClient.getCfg().getBoolean("classicWalk");
    }

    @EventTopicSubscriber(topic = "wasdWalk")
    public void onWasdSettingsChanged(@Nonnull String configKey, @Nonnull ConfigChangedEvent event) {
        if ("wasdWalk".equals(configKey)) {
            applyWasdWalkSettings();
        }
    }

    @EventTopicSubscriber(topic = "classicWalk")
    public void onClassicSettingsChanged(@Nonnull String configKey, @Nonnull ConfigChangedEvent event) {
        if ("classicWalk".equals(configKey)) {
            applyWasdWalkSettings();
        }
    }

    public void handleKeyReleasedInput(@Nonnull Key key) {
        KeyboardMovementHandler handler = World.getPlayer().getMovementHandler().getKeyboardHandler();
        switch (key) {
            case CursorUp:
            case NumPad8:
                if (useClassicWasdWalking) {
                    handler.stopMovingTowards(Location.DIR_NORTH);
                } else {
                    handler.stopMovingTowards(Location.DIR_NORTHEAST);
                }
                break;
            case W:
                if (useWasdWalking && !useClassicWasdWalking) {
                    handler.stopMovingTowards(Location.DIR_NORTHEAST);
                }
                if (useWasdWalking && useClassicWasdWalking) {
                    handler.stopMovingTowards(Location.DIR_NORTH);
                }
                break;

            case CursorLeft:
            case NumPad4:
                if (useClassicWasdWalking) {
                    handler.stopMovingTowards(Location.DIR_WEST);
                } else {
                    handler.stopMovingTowards(Location.DIR_NORTHWEST);
                }
                break;
            case A:
                if (useWasdWalking && !useClassicWasdWalking) {
                    handler.stopMovingTowards(Location.DIR_NORTHWEST);
                }
                if (useWasdWalking && useClassicWasdWalking) {
                    handler.stopMovingTowards(Location.DIR_WEST);
                }
                break;

            case CursorDown:
            case NumPad2:
                if (useClassicWasdWalking) {
                    handler.stopMovingTowards(Location.DIR_SOUTH);
                } else {
                    handler.stopMovingTowards(Location.DIR_SOUTHWEST);
                }
                break;
            case S:
                if (useWasdWalking && !useClassicWasdWalking) {
                    handler.stopMovingTowards(Location.DIR_SOUTHWEST);
                }
                if (useWasdWalking && useClassicWasdWalking) {
                    handler.stopMovingTowards(Location.DIR_SOUTH);
                }
                break;

            case CursorRight:
            case NumPad6:
                if (useClassicWasdWalking) {
                    handler.stopMovingTowards(Location.DIR_EAST);
                } else {
                    handler.stopMovingTowards(Location.DIR_SOUTHEAST);
                }
                break;
            case D:
                if (useWasdWalking && !useClassicWasdWalking) {
                    handler.stopMovingTowards(Location.DIR_SOUTHEAST);
                }
                if (useWasdWalking && useClassicWasdWalking) {
                    handler.stopMovingTowards(Location.DIR_EAST);
                }
                break;

            case NumPad1:
            case End:
                if (useClassicWasdWalking) {
                    handler.stopMovingTowards(Location.DIR_SOUTHWEST);
                } else {
                    handler.stopMovingTowards(Location.DIR_WEST);
                }
                break;

            case NumPad3:
            case PageDown:
                if (useClassicWasdWalking) {
                    handler.stopMovingTowards(Location.DIR_SOUTHEAST);
                } else {
                    handler.stopMovingTowards(Location.DIR_SOUTH);
                }
                break;

            case NumPad7:
            case Home:
                if (useClassicWasdWalking) {
                    handler.stopMovingTowards(Location.DIR_NORTHWEST);
                } else {
                    handler.stopMovingTowards(Location.DIR_NORTH);
                }
                break;

            case NumPad9:
            case PageUp:
                if (useClassicWasdWalking) {
                    handler.stopMovingTowards(Location.DIR_NORTHEAST);
                } else {
                    handler.stopMovingTowards(Location.DIR_EAST);
                }
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
                if (useClassicWasdWalking) {
                    startMovingTowards(Location.DIR_NORTH);
                } else {
                    startMovingTowards(Location.DIR_NORTHEAST);
                }
                break;
            case W:
                if (useWasdWalking && !useClassicWasdWalking) {
                    startMovingTowards(Location.DIR_NORTHEAST);
                }
                if (useWasdWalking && useClassicWasdWalking) {
                    startMovingTowards(Location.DIR_NORTH);
                }
                break;

            case CursorLeft:
            case NumPad4:
                if (useClassicWasdWalking) {
                    startMovingTowards(Location.DIR_WEST);
                } else {
                    startMovingTowards(Location.DIR_NORTHWEST);
                }
                break;
            case A:
                if (useWasdWalking && !useClassicWasdWalking) {
                    startMovingTowards(Location.DIR_NORTHWEST);
                }
                if (useWasdWalking && useClassicWasdWalking) {
                    startMovingTowards(Location.DIR_WEST);
                }
                break;

            case CursorDown:
            case NumPad2:
                if (useClassicWasdWalking) {
                    startMovingTowards(Location.DIR_SOUTH);
                } else {
                    startMovingTowards(Location.DIR_SOUTHWEST);
                }
                break;
            case S:
                if (useWasdWalking && !useClassicWasdWalking) {
                    startMovingTowards(Location.DIR_SOUTHWEST);
                }
                if (useWasdWalking && useClassicWasdWalking) {
                    startMovingTowards(Location.DIR_SOUTH);
                }
                break;

            case CursorRight:
            case NumPad6:
                if (useClassicWasdWalking) {
                    startMovingTowards(Location.DIR_EAST);
                } else {
                    startMovingTowards(Location.DIR_SOUTHEAST);
                }
                break;
            case D:
                if (useWasdWalking && !useClassicWasdWalking) {
                    startMovingTowards(Location.DIR_SOUTHEAST);
                }
                if (useWasdWalking && useClassicWasdWalking) {
                    startMovingTowards(Location.DIR_EAST);
                }
                break;

            case NumPad1:
            case End:
                if (useClassicWasdWalking) {
                    startMovingTowards(Location.DIR_SOUTHWEST);
                } else {
                    startMovingTowards(Location.DIR_WEST);
                }
                break;

            case NumPad3:
            case PageDown:
                if (useClassicWasdWalking) {
                    startMovingTowards(Location.DIR_SOUTHEAST);
                } else {
                    startMovingTowards(Location.DIR_SOUTH);
                }
                break;

            case NumPad7:
            case Home:
                if (useClassicWasdWalking) {
                    startMovingTowards(Location.DIR_NORTHWEST);
                } else {
                    startMovingTowards(Location.DIR_NORTH);
                }
                break;

            case NumPad9:
            case PageUp:
                if (useClassicWasdWalking) {
                    startMovingTowards(Location.DIR_NORTHEAST);
                } else {
                    startMovingTowards(Location.DIR_EAST);
                }
                break;

            case LeftCtrl:
            case RightCtrl:
                World.getGameGui().getGameMapGui().toggleRunMode();
                break;

            default:
                if (inputMap.containsKey(key)) {
                    EventBus.publish(InputReceiver.EB_TOPIC, inputMap.get(key));
                }
        }
    }

    private static void startMovingTowards(int direction) {
        if (!Location.isValidDirection(direction)) {
            throw new IllegalArgumentException("Direction has invalid value: " + direction);
        }
        KeyboardMovementHandler handler = World.getPlayer().getMovementHandler().getKeyboardHandler();
        handler.startMovingTowards(direction);
        handler.assumeControl();
    }
}

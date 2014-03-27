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
package illarion.client.input;

import illarion.client.IllaClient;
import illarion.client.net.client.CloseShowcaseCmd;
import illarion.client.net.client.PickUpAllItemsCmd;
import illarion.client.world.World;
import illarion.client.world.items.InventorySlot;
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

    public KeyMapper(final Input input) {
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
    public void onWasdSettingsChanged(@Nonnull final String configKey, @Nonnull final ConfigChangedEvent event) {
        if ("wasdWalk".equals(configKey)) {
            applyWasdWalkSettings();
        }
    }

    @EventTopicSubscriber(topic = "classicWalk")
    public void onClassicSettingsChanged(@Nonnull final String configKey, @Nonnull final ConfigChangedEvent event) {
        if ("classicWalk".equals(configKey)) {
            applyWasdWalkSettings();
        }
    }

    public void handleKeyReleasedInput(@Nonnull final Key key) {
        switch (key) {
            case CursorUp:
            case NumPad8:
                if (useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().stopMovingToDirection(Location.DIR_NORTH);
                } else {
                    World.getPlayer().getMovementHandler().stopMovingToDirection(Location.DIR_NORTHEAST);
                }
                break;
            case W:
                if (useWasdWalking && !useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().stopMovingToDirection(Location.DIR_NORTHEAST);
                }
                if (useWasdWalking && useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().stopMovingToDirection(Location.DIR_NORTH);
                }
                break;

            case CursorLeft:
            case NumPad4:
                if (useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().stopMovingToDirection(Location.DIR_WEST);
                } else {
                    World.getPlayer().getMovementHandler().stopMovingToDirection(Location.DIR_NORTHWEST);
                }
                break;
            case A:
                if (useWasdWalking && !useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().stopMovingToDirection(Location.DIR_NORTHWEST);
                }
                if (useWasdWalking && useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().stopMovingToDirection(Location.DIR_WEST);
                }
                break;

            case CursorDown:
            case NumPad2:
                if (useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().stopMovingToDirection(Location.DIR_SOUTH);
                } else {
                    World.getPlayer().getMovementHandler().stopMovingToDirection(Location.DIR_SOUTHWEST);
                }
                break;
            case S:
                if (useWasdWalking && !useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().stopMovingToDirection(Location.DIR_SOUTHWEST);
                }
                if (useWasdWalking && useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().stopMovingToDirection(Location.DIR_SOUTH);
                }
                break;

            case CursorRight:
            case NumPad6:
                if (useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().stopMovingToDirection(Location.DIR_EAST);
                } else {
                    World.getPlayer().getMovementHandler().stopMovingToDirection(Location.DIR_SOUTHEAST);
                }
                break;
            case D:
                if (useWasdWalking && !useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().stopMovingToDirection(Location.DIR_SOUTHEAST);
                }
                if (useWasdWalking && useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().stopMovingToDirection(Location.DIR_EAST);
                }
                break;

            case NumPad1:
            case End:
                if (useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().stopMovingToDirection(Location.DIR_SOUTHWEST);
                } else {
                    World.getPlayer().getMovementHandler().stopMovingToDirection(Location.DIR_WEST);
                }
                break;

            case NumPad3:
            case PageDown:
                if (useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().stopMovingToDirection(Location.DIR_SOUTHEAST);
                } else {
                    World.getPlayer().getMovementHandler().stopMovingToDirection(Location.DIR_SOUTH);
                }
                break;

            case NumPad7:
            case Home:
                if (useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().stopMovingToDirection(Location.DIR_NORTHWEST);
                } else {
                    World.getPlayer().getMovementHandler().stopMovingToDirection(Location.DIR_NORTH);
                }
                break;

            case NumPad9:
            case PageUp:
                if (useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().stopMovingToDirection(Location.DIR_NORTHEAST);
                } else {
                    World.getPlayer().getMovementHandler().stopMovingToDirection(Location.DIR_EAST);
                }
                break;
        }
    }

    public void handleKeyPressedInput(@Nonnull final Key key) {
        switch (key) {
            case B:
                if (World.getPlayer().hasContainer(0)) {
                    World.getPlayer().removeContainer(0);
                    World.getNet().sendCommand(new CloseShowcaseCmd(0));
                } else {
                    final InventorySlot slot = World.getPlayer().getInventory().getItem(0);
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
                    World.getPlayer().getMovementHandler().startMovingToDirection(Location.DIR_NORTH);
                } else {
                    World.getPlayer().getMovementHandler().startMovingToDirection(Location.DIR_NORTHEAST);
                }
                break;
            case W:
                if (useWasdWalking && !useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().startMovingToDirection(Location.DIR_NORTHEAST);
                }
                if (useWasdWalking && useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().startMovingToDirection(Location.DIR_NORTH);
                }
                break;

            case CursorLeft:
            case NumPad4:
                if (useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().startMovingToDirection(Location.DIR_WEST);
                } else {
                    World.getPlayer().getMovementHandler().startMovingToDirection(Location.DIR_NORTHWEST);
                }
                break;
            case A:
                if (useWasdWalking && !useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().startMovingToDirection(Location.DIR_NORTHWEST);
                }
                if (useWasdWalking && useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().startMovingToDirection(Location.DIR_WEST);
                }
                break;

            case CursorDown:
            case NumPad2:
                if (useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().startMovingToDirection(Location.DIR_SOUTH);
                } else {
                    World.getPlayer().getMovementHandler().startMovingToDirection(Location.DIR_SOUTHWEST);
                }
                break;
            case S:
                if (useWasdWalking && !useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().startMovingToDirection(Location.DIR_SOUTHWEST);
                }
                if (useWasdWalking && useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().startMovingToDirection(Location.DIR_SOUTH);
                }
                break;

            case CursorRight:
            case NumPad6:
                if (useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().startMovingToDirection(Location.DIR_EAST);
                } else {
                    World.getPlayer().getMovementHandler().startMovingToDirection(Location.DIR_SOUTHEAST);
                }
                break;
            case D:
                if (useWasdWalking && !useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().startMovingToDirection(Location.DIR_SOUTHEAST);
                }
                if (useWasdWalking && useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().startMovingToDirection(Location.DIR_EAST);
                }
                break;

            case NumPad1:
            case End:
                if (useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().startMovingToDirection(Location.DIR_SOUTHWEST);
                } else {
                    World.getPlayer().getMovementHandler().startMovingToDirection(Location.DIR_WEST);
                }
                break;

            case NumPad3:
            case PageDown:
                if (useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().startMovingToDirection(Location.DIR_SOUTHEAST);
                } else {
                    World.getPlayer().getMovementHandler().startMovingToDirection(Location.DIR_SOUTH);
                }
                break;

            case NumPad7:
            case Home:
                if (useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().startMovingToDirection(Location.DIR_NORTHWEST);
                } else {
                    World.getPlayer().getMovementHandler().startMovingToDirection(Location.DIR_NORTH);
                }
                break;

            case NumPad9:
            case PageUp:
                if (useClassicWasdWalking) {
                    World.getPlayer().getMovementHandler().startMovingToDirection(Location.DIR_NORTHEAST);
                } else {
                    World.getPlayer().getMovementHandler().startMovingToDirection(Location.DIR_EAST);
                }
                break;

            case LeftCtrl:
            case RightCtrl:
                World.getPlayer().getMovementHandler().toggleRunWalk();
                break;

            default:
                if (inputMap.containsKey(key)) {
                    EventBus.publish(InputReceiver.EB_TOPIC, inputMap.get(key));
                }
        }
    }
}

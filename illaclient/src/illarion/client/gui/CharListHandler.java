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
package illarion.client.gui;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.controls.DropDownSelectionChangedEvent;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.ListBoxSelectionChangedEvent;
import de.lessvoid.nifty.screen.Screen;
import gnu.trove.procedure.TObjectProcedure;

import java.lang.ref.WeakReference;
import java.util.*;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.bushe.swing.event.EventTopicSubscriber;

import illarion.client.util.Lang;
import illarion.client.world.Char;
import illarion.client.world.CombatHandler;
import illarion.client.world.World;
import illarion.client.world.events.*;
import illarion.common.util.FastMath;
import illarion.common.util.Location;

/**
 * This class takes care for loading the list of nearby characters, NPCs and monsters and to make sure that this list
 * is
 * kept up to date. Also it handles the input events on this list.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CharListHandler
        implements EventSubscriber<AbstractCharEvent> {
    private ListBox<GUIEntryWrapper> guiList;
    private DropDown<String> filterSelection;

    private boolean ignoreFocusEvents;

    private static final class GUIEntryWrapper {
        private final String name;
        private final WeakReference<Char> chara;

        public GUIEntryWrapper(final Char character) {
            final String tempName = character.getName();
            if (tempName.isEmpty()) {
                name = "<Empty>";
            } else {
                name = tempName;
            }
            chara = new WeakReference<Char>(character);
        }

        public String toString() {
            return name;
        }

        public Char getCharacter() {
            return chara.get();
        }
    }

    private final EventTopicSubscriber<DropDownSelectionChangedEvent<String>> filterChangedEvent;
    private final EventTopicSubscriber<ListBoxSelectionChangedEvent<GUIEntryWrapper>> selectionChanged;

    private final List<WeakReference<Char>> charList;
    private boolean dirty;

    public CharListHandler() {
        charList = new ArrayList<WeakReference<Char>>(5);
        dirty = true;

        filterChangedEvent = new EventTopicSubscriber<DropDownSelectionChangedEvent<String>>() {
            @Override
            public void onEvent(final String topic, final DropDownSelectionChangedEvent<String> data) {
                updateGUI();
            }
        };

        selectionChanged = new EventTopicSubscriber<ListBoxSelectionChangedEvent<GUIEntryWrapper>>() {
            @Override
            public void onEvent(final String topic, final ListBoxSelectionChangedEvent<GUIEntryWrapper> data) {
                if (ignoreFocusEvents) {
                    return;
                }
                if (data.getSelection().isEmpty()) {
                    CombatHandler.getInstance().setCombatMode(false);
                }
                final Char selectedChar = data.getSelection().get(0).getCharacter();
                if ((selectedChar != null) && CombatHandler.getInstance().canBeAttacked(selectedChar)) {
                    CombatHandler.getInstance().setCombatMode(true);
                    CombatHandler.getInstance().setAttackTarget(selectedChar);
                }
            }
        };
    }

    public void bind(final Nifty nifty, final Screen screen) {
        guiList = screen.findNiftyControl("charlist-list", ListBox.class);
        filterSelection = screen.findNiftyControl("charlist-viewselection", DropDown.class);

        filterSelection.addItem(Lang.getMsg("charlist.filter.all"));
        filterSelection.addItem(Lang.getMsg("charlist.filter.players_monsters"));
        filterSelection.addItem(Lang.getMsg("charlist.filter.players"));
        filterSelection.addItem(Lang.getMsg("charlist.filter.monsters"));

        EventBus.subscribe(AbstractCharEvent.class, this);
        nifty.subscribe(screen, "charlist-viewselection", DropDownSelectionChangedEvent.class, filterChangedEvent);
        nifty.subscribe(screen, "charlist-list", ListBoxSelectionChangedEvent.class, selectionChanged);

        ignoreFocusEvents = false;

        World.getPeople().forAllChars(new TObjectProcedure<Char>() {
            @Override
            public boolean execute(final Char chara) {
                if (!World.getPlayer().isPlayer(chara.getCharId())) {
                    charList.add(new WeakReference<Char>(chara));
                }
                dirty = true;
                return true;
            }
        });
        sortList();
    }

    private void cleanupList() {
        final Iterator<WeakReference<Char>> itr = charList.iterator();
        while (itr.hasNext()) {
            final WeakReference<Char> weakChar = itr.next();
            if (weakChar.get() == null) {
                itr.remove();
                dirty = true;
            }
        }
    }

    private void insertSorted(final Char chara) {
        if (World.getPlayer().isPlayer(chara.getCharId())) {
            return;
        }

        if (charList.isEmpty()) {
            charList.add(new WeakReference<Char>(chara));
            return;
        }

        final Location playerLoc = World.getPlayer().getLocation();
        final int charDist = playerLoc.getDistance(chara.getLocation());
        boolean insertDone = false;

        final ListIterator<WeakReference<Char>> lItr = charList.listIterator();
        while (lItr.hasNext()) {
            final Char nextChar = lItr.next().get();
            if (nextChar == null) {
                lItr.remove();
                dirty = true;
            } else {
                final int nextCharDist = playerLoc.getDistance(nextChar.getLocation());
                if (nextCharDist > charDist) {
                    lItr.previous();
                    lItr.add(new WeakReference<Char>(chara));
                    dirty = true;
                    insertDone = true;
                    return;
                }
            }
        }

        if (!insertDone) {
            charList.add(new WeakReference<Char>(chara));
            dirty = true;
        }
    }

    private void sortList() {
        Collections.sort(charList, new Comparator<WeakReference<Char>>() {
            @Override
            public int compare(WeakReference<Char> o1, WeakReference<Char> o2) {
                final Char char1 = o1.get();
                final Char char2 = o2.get();
                if ((char1 == null) || (char2 == null)) {
                    return 0;
                }

                final Location playerLoc = World.getPlayer().getLocation();
                final int char1Dist = playerLoc.getDistance(char1.getLocation());
                final int char2Dist = playerLoc.getDistance(char2.getLocation());

                dirty = true;
                return FastMath.sign(char1Dist - char2Dist);
            }
        });
    }

    private void removeChar(final long charId) {
        final Iterator<WeakReference<Char>> itr = charList.iterator();
        while (itr.hasNext()) {
            final WeakReference<Char> weakChar = itr.next();
            if ((weakChar.get() == null) || (weakChar.get().getCharId() == charId)) {
                itr.remove();
                dirty = true;
            }
        }
    }

    private boolean isAlreadyListed(final Char chara) {
        for (WeakReference<Char> weakChar : charList) {
            if (chara.equals(weakChar.get())) {
                return true;
            }
        }

        return false;
    }

    private void updateGUI() {
        if (!dirty) {
            return;
        }

        dirty = false;

        ignoreFocusEvents = true;
        GUIEntryWrapper selectEntry = null;
        guiList.clear();
        for (final WeakReference<Char> weakChar : charList) {
            final Char chara = weakChar.get();
            if (showInList(chara)) {
                final GUIEntryWrapper entry = new GUIEntryWrapper(chara);
                guiList.addItem(entry);
                if (CombatHandler.getInstance().isAttacking(chara)) {
                    selectEntry = entry;
                }
            }
        }

        if (selectEntry == null) {
            if (!guiList.getSelection().isEmpty()) {
                guiList.deselectItem(guiList.getSelection().get(0));
            }
        } else {
            guiList.selectItem(selectEntry);
        }

        guiList.refresh();
        //guiList.getElement().getParent().layoutElements();
        ignoreFocusEvents = false;
    }

    private boolean showInList(final Char chara) {
        if (chara == null) {
            return false;
        }

        switch (filterSelection.getSelectedIndex()) {
            case 0:
                return true;
            case 1:
                return chara.isMonster() || chara.isHuman();
            case 2:
                return chara.isHuman();
            case 3:
                return chara.isMonster();
        }

        return false;
    }

    @Override
    public void onEvent(final AbstractCharEvent event) {
        if (event.getEvent().equals(CharRemovedEvent.EVENT)) {
            removeChar(event.getCharId());
        } else if (event.getEvent().equals(CharVisibilityEvent.EVENT)) {
            final CharVisibilityEvent visEvent = (CharVisibilityEvent) event;
            if (!visEvent.isPlayerCharacter()) {
                if (visEvent.isVisible()) {
                    final Char eventChar = World.getPeople().getCharacter(event.getCharId());
                    if (!isAlreadyListed(eventChar)) {
                        cleanupList();
                        insertSorted(eventChar);
                    }
                } else {
                    removeChar(event.getCharId());
                }
            }
        } else if (event.getEvent().equals(CharMoveEvent.EVENT)) {
            if (event.isPlayerCharacter()) {
                sortList();
            } else {
                removeChar(event.getCharId());
                insertSorted(World.getPeople().getCharacter(event.getCharId()));
            }
        } else if (event.getEvent().equals(CharNameEvent.EVENT)) {
            dirty = true;
        }

        updateGUI();
    }

    public void onStartScreen() {
        dirty = true;
        cleanupList();
        updateGUI();
    }
}

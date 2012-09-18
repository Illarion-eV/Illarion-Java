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
package illarion.client.states;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.slick2d.NiftyBasicGameState;
import illarion.client.Game;
import illarion.client.IllaClient;
import illarion.client.gui.controller.CharScreenController;
import illarion.client.gui.controller.LoginScreenController;
import illarion.client.util.Lang;
import illarion.common.config.ConfigChangedEvent;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import org.newdawn.slick.state.StateBasedGame;

/**
 * This game state is used to display the login and character selection dialog. Also the option dialog is displayed in
 * this state.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class LoginState
        extends NiftyBasicGameState implements EventTopicSubscriber<ConfigChangedEvent> {
    /**
     * Create the game state that handles the login with the identifier that is needed to access it.
     */
    public LoginState() {
        super("login");
        EventBus.subscribe(IllaClient.CFG_RESOLUTION, this);
    }

    @Override
    protected void prepareNifty(final Nifty nifty, final StateBasedGame game) {
        nifty.setLocale(Lang.getInstance().getLocale());
        nifty.registerScreenController(new LoginScreenController(game), new CharScreenController(game));

        try {
            nifty.validateXml("illarion/client/gui/xml/login.xml");
            nifty.validateXml("illarion/client/gui/xml/charselect.xml");
            nifty.validateXml("illarion/client/gui/xml/options.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
        nifty.addXml("illarion/client/gui/xml/login.xml");
        nifty.addXml("illarion/client/gui/xml/charselect.xml");
        nifty.addXml("illarion/client/gui/xml/options.xml");
    }

    @Override
    public int getID() {
        return Game.STATE_LOGIN;
    }

    /**
     * Handle an event published on a topic.
     * <p/>
     * The EventService calls this method on each publication on a matching topic name passed to one of the
     * EventService's topic-based subscribe methods, specifically, {@link org.bushe.swing.event.EventService#subscribe(String,
     * org.bushe.swing.event.EventTopicSubscriber)} {@link org.bushe.swing.event.EventService#subscribe(java.util.regex.Pattern, org.bushe.swing.event.EventTopicSubscriber)} {@link
     * org.bushe.swing.event.EventService#subscribeStrongly(String, org.bushe.swing.event.EventTopicSubscriber)} and {@link org.bushe.swing.event.EventService#subscribeStrongly(java.util.regex.Pattern,
     * org.bushe.swing.event.EventTopicSubscriber)}.
     *
     * @param topic the name of the topic published on
     * @param data  the data object published on the topic
     */
    @Override
    public void onEvent(final String topic, final ConfigChangedEvent data) {
        if (getNifty() != null) {
            getNifty().resolutionChanged();
        }
    }
}

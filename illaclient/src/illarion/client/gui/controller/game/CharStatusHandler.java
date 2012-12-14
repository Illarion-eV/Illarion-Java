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
package illarion.client.gui.controller.game;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.graphics.AnimationUtility;
import illarion.client.net.server.events.AttributeUpdateReceivedEvent;
import illarion.client.world.World;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.illarion.nifty.controls.Progress;
import org.newdawn.slick.GameContainer;

/**
 * This handler takes care for showing the hit points, mana points and food points of the character on the screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CharStatusHandler implements ScreenController, UpdatableHandler {
    /**
     * The progress bar that shows the hit points.
     */
    private Progress hitPointBar;

    /**
     * The progress bar that shows the mana points.
     */
    private Progress manaPointBar;

    /**
     * The progress bar that shows the food points.
     */
    private Progress foodPointBar;

    /**
     * The last reported value of the hit points.
     */
    private int hitPoints;

    /**
     * The last reported value of the mana points.
     */
    private int manaPoints;

    /**
     * The last reported value of the food points.
     */
    private int foodPoints;

    /**
     * The currently displayed hit points.
     */
    private int currentHitPoints;

    /**
     * The currently displayed food points.
     */
    private int currentFoodPoints;

    /**
     * The currently displayed mana points.
     */
    private int currentManaPoints;

    @Override
    public void bind(final Nifty nifty, final Screen screen) {
        hitPointBar = screen.findNiftyControl("healthBar", Progress.class);
        manaPointBar = screen.findNiftyControl("manaBar", Progress.class);
        foodPointBar = screen.findNiftyControl("foodBar", Progress.class);
    }

    /**
     * This function receives the attribute update events.
     *
     * @param event the received event
     */
    @EventSubscriber
    public void onAttributeMessageReceived(final AttributeUpdateReceivedEvent event) {
        if (event.getTargetCharId().equals(World.getPlayer().getPlayerId())) {
            switch (event.getAttribute()) {
                case HitPoints:
                    hitPoints = event.getValue();
                    break;
                case FoodPoints:
                    foodPoints = event.getValue();
                    break;
                case ManaPoints:
                    manaPoints = event.getValue();
                    break;
            }
        }
    }

    @Override
    public void onStartScreen() {
        AnnotationProcessor.process(this);
    }

    @Override
    public void onEndScreen() {
        AnnotationProcessor.unprocess(this);
    }

    @Override
    public void update(final GameContainer container, final int delta) {
        if (hitPoints != currentHitPoints) {
            currentHitPoints = AnimationUtility.approach(currentHitPoints, hitPoints, 0, 10000, delta);
            hitPointBar.setProgress((float) currentHitPoints / 10000.f);
        }
        if (manaPoints != currentManaPoints) {
            currentManaPoints = AnimationUtility.approach(currentManaPoints, manaPoints, 0, 10000, delta);
            manaPointBar.setProgress((float) currentManaPoints / 10000.f);
        }
        if (foodPoints != currentFoodPoints) {
            currentFoodPoints = AnimationUtility.approach(currentFoodPoints, foodPoints, 0, 60000, delta);
            foodPointBar.setProgress((float) currentFoodPoints / 60000.f);
        }
    }
}

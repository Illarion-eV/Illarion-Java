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
package illarion.client.gui.controller.game;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.elements.Element;
import illarion.client.graphics.AnimationUtility;
import illarion.client.gui.PlayerStatusGui;
import illarion.client.world.Char;
import illarion.client.world.Player;
import illarion.client.world.World;
import illarion.client.world.characters.CharacterAttribute;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.illarion.engine.GameContainer;
import org.illarion.nifty.controls.Progress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This handler takes care for showing the hit points, mana points and food points of the character on the screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CharStatusHandler implements PlayerStatusGui, ScreenController, UpdatableHandler {
    /**
     * The progress bar that shows the hit points.
     */
    private static final Logger log = LoggerFactory.getLogger(CharStatusHandler.class);
    
    @Nullable
    private Progress hitPointBar;

    /**
     * The progress bar that shows the mana points.
     */
    @Nullable
    private Progress manaPointBar;

    /**
     * The progress bar that shows the food points.
     */
    @Nullable
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

    private Element manaPointElement;

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        hitPointBar = screen.findNiftyControl("healthBar", Progress.class);
        manaPointBar = screen.findNiftyControl("manaBar", Progress.class);
        foodPointBar = screen.findNiftyControl("foodBar", Progress.class);
        manaPointElement = screen.findElementById("manaBar");
    }

    @Override
    public void onStartScreen() {
        AnnotationProcessor.process(this);

        Player player = World.getPlayer();
        Char playerChar = player.getCharacter();
        hitPoints = playerChar.getAttribute(CharacterAttribute.HitPoints);
        foodPoints = playerChar.getAttribute(CharacterAttribute.FoodPoints);
        manaPoints = playerChar.getAttribute(CharacterAttribute.ManaPoints);
    }

    @Override
    public void onEndScreen() {
        AnnotationProcessor.unprocess(this);
    }

    @Override
    public void update(GameContainer container, int delta) {

        boolean manaIsEnabled = manaPointElement.isVisible();

        if ((manaPoints == 0) && manaIsEnabled){
            manaPointElement.hide();
        }
        if ((manaPoints >= 1) && !manaIsEnabled){
            manaPointElement.show();
        }

        if ((hitPoints != currentHitPoints) && (hitPointBar != null)) {
            currentHitPoints = AnimationUtility.approach(currentHitPoints, hitPoints, 0, 10000, delta);
            hitPointBar.setProgress(currentHitPoints / 10000.f);
        }
        if ((manaPoints != currentManaPoints) && (manaPointBar != null)) {
            currentManaPoints = AnimationUtility.approach(currentManaPoints, manaPoints, 0, 10000, delta);
            manaPointBar.setProgress(currentManaPoints / 10000.f);
        }
        if ((foodPoints != currentFoodPoints) && (foodPointBar != null)) {
            currentFoodPoints = AnimationUtility.approach(currentFoodPoints, foodPoints, 0, 60000, delta);
            foodPointBar.setProgress(currentFoodPoints / 60000.f);
        }
    }

    @Override
    public void setAttribute(@Nonnull CharacterAttribute attribute, int value) {
        switch (attribute) {
            case HitPoints:
                hitPoints = value;
                break;
            case FoodPoints:
                foodPoints = value;
                break;
            case ManaPoints:
                manaPoints = value;
                break;
            default:
                break;
        }
    }
}

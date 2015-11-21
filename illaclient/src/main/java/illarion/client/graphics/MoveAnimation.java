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
package illarion.client.graphics;

import illarion.common.types.DisplayCoordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created: 23.08.2005 23:42:22
 */
public class MoveAnimation extends AbstractAnimation<AnimatedMove> {
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(MoveAnimation.class);
    @Nullable
    private DisplayCoordinate start;
    @Nullable
    private DisplayCoordinate target;

    public MoveAnimation(AnimatedMove target) {
        super(target);
    }

    @Override
    public boolean animate(int delta) {
        if ((start == null) || (target == null)) {
            throw new IllegalStateException("Animating a move while there is no start and target location set.");
        }

        // animation has ended
        if (updateCurrentTime(delta)) {
            setRunning(false);
            setPosition(target);
            return false;
        }

        // calc values
        float animationPos = getStoryboardProgress(false);
        int x = start.getX() + Math.round(animationPos * (target.getX() - start.getX()));
        int y = start.getY() + Math.round(animationPos * (target.getY() - start.getY()));
        int layer = start.getLayer() + Math.round(animationPos * (target.getLayer() - start.getLayer()));

        // update only for real changes
        setPosition(new DisplayCoordinate(x, y, layer));

        return true;
    }

    @Override
    public void restart() {
        if (start == null) {
            throw new IllegalStateException("Starting a animation while there is no starting position set.");
        }
        start();

        // set start position immediately
        setPosition(start);
        setSkipNextUpdate(true);
    }

    @Override
    public void stop() {
        if (!isRunning()) {
            return;
        }

        setRunning(false);
        if (target == null) {
            LOGGER.warn("Stopping animation received while there is not target location set. Something is wrong.");
        } else {
            setPosition(target);
        }
        animationFinished(false);
    }

    /**
     * Start a movement animation
     *
     * @param start the start location of the animated move
     * @param target the target location of the move
     * @param duration the duration of the move
     */
    public void start(@Nonnull DisplayCoordinate start, @Nonnull DisplayCoordinate target, int duration) {
        this.start = start;
        this.target = target;

        setDuration(duration);
        restart();
    }

    private void setPosition(@Nonnull DisplayCoordinate currentPos) {
        int targetCnt = getTargetCount();
        for (int i = 0; i < targetCnt; i++) {
            AnimatedMove animation = getAnimationTarget(i);
            if (animation == null) {
                LOGGER.error("Found NULL animation.");
            } else {
                animation.setPosition(currentPos);
            }
        }
    }
}

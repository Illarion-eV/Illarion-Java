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
package illarion.client.graphics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created: 23.08.2005 23:42:22
 */
public class MoveAnimation extends AbstractAnimation<AnimatedMove> {

    private int dstX;
    private int dstY;
    private int dstZ;
    private int lastX;
    private int lastY;
    private int lastZ;
    // move animation parameters
    private int srcX;
    private int srcY;
    private int srcZ;

    public MoveAnimation(AnimatedMove target) {
        super(target);
    }

    @Override
    public boolean animate(int delta) {
        // animation has ended
        if (updateCurrentTime(delta)) {
            setRunning(false);
            setPosition(dstX, dstY, dstZ);
            return false;
        }

        // calc values
        float animationPos = animationProgress();
        int x = srcX + Math.round(animationPos * (dstX - srcX));
        int y = srcY + Math.round(animationPos * (dstY - srcY));
        int z = srcZ + Math.round(animationPos * (dstZ - srcZ));

        // update only for real changes
        if ((x != lastX) || (y != lastY) || (z != lastZ)) {
            setPosition(x, y, z);
        }

        lastX = x;
        lastY = y;
        lastZ = z;

        return true;
    }

    @Override
    public void restart() {
        start();

        // set start position immediately
        setPosition(srcX, srcY, srcZ);
        lastX = srcX;
        lastY = srcY;
        lastZ = srcZ;
    }

    /**
     * Start a movement animation
     *
     * @param srcX
     * @param srcY
     * @param dstX
     * @param dstY
     * @param speed
     */
    public void start(int srcX, int srcY, int srcZ, int dstX, int dstY, int dstZ, int speed) {
        this.srcX = srcX;
        this.srcY = srcY;
        this.srcZ = srcZ;
        this.dstX = dstX;
        this.dstY = dstY;
        this.dstZ = dstZ;

        setDuration(speed * ANIMATION_FRAME);

        restart();
    }

    @Override
    public void stop() {
        if (!isRunning()) {
            return;
        }

        setRunning(false);
        setPosition(dstX, dstY, dstZ);
        animationFinished(false);
    }

    protected void start(
            int srcX, int srcY, int dstX, int dstY, int speed) {
        start(srcX, srcY, 0, dstX, dstY, 0, speed);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MoveAnimation.class);

    private void setPosition(int x, int y, int z) {
        int targetCnt = getTargetCount();
        for (int i = 0; i < targetCnt; i++) {
            AnimatedMove animation = getAnimationTarget(i);
            if (animation == null) {
                LOGGER.error("Found NULL animation.");
            } else {
                animation.setPosition(x, y, z);
            }
        }
    }
}

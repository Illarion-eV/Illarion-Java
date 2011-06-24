/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.graphics;

/**
 * Created: 23.08.2005 23:42:22
 */
public class MoveAnimation extends AbstractAnimation {

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

    public MoveAnimation(final AnimatedMove target) {
        super(target);
    }

    @Override
    public boolean animate(final int delta) {
        // animation has ended
        if (updateCurrentTime(delta)) {
            setRunning(false);
            setPosition(dstX, dstY, dstZ);
            return false;
        }

        // calc values
        final float animationPos = animationProgress();
        final int x = srcX + Math.round(animationPos * (dstX - srcX));
        final int y = srcY + Math.round(animationPos * (dstY - srcY));
        final int z = srcZ + Math.round(animationPos * (dstZ - srcZ));

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
        // set start position immediately
        setPosition(srcX, srcY, srcZ);
        lastX = srcX;
        lastY = srcY;
        lastZ = srcZ;

        super.start();
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
    public void start(final int srcX, final int srcY, final int srcZ,
        final int dstX, final int dstY, final int dstZ, final int speed) {
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

    protected void start(final int srcX, final int srcY, final int dstX,
        final int dstY, final int speed) {
        start(srcX, srcY, 0, dstX, dstY, 0, speed);
    }

    private void setPosition(final int x, final int y, final int z) {
        final int targetCnt = getTargetCount();
        for (int i = 0; i < targetCnt; i++) {
            ((AnimatedMove) getAnimationTarget(i)).setPosition(x, y, z);
        }
    }
}

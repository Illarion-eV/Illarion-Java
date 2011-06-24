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
 * A frame animation is a animation based on different images.
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.95
 */
final class FrameAnimation extends AbstractAnimation {
    /**
     * Run animation backwards.
     */
    protected static final int BACKWARDS = 1;
    /**
     * End animation with same frame as it started.
     */
    protected static final int CYCLIC = 4;
    /**
     * Keep running in an endless loop.
     */
    protected static final int LOOPED = 2;

    /**
     * The amount of frames of this animation.
     */
    private int frames;

    /**
     * The frame that was selected during the last animation update.
     */
    private int lastFrame;

    /**
     * The mode the animation is running in.
     */
    private int mode;

    /**
     * The speed the animation is running with. The higher this value, the
     * slower the animation itself.
     */
    private int speed;

    /**
     * The first and the last frame of this animation.
     */
    private int stillFrame;

    /**
     * Create a new frame animation with the default parameters and a first
     * target. Note that a animation created by this can't be started with
     * {@link #restart()}. It first needs it running data set with
     * {@link #start(int, int, int, int)} or {@link #setup(int, int, int, int)}.
     * 
     * @param target the first target of the animation
     */
    protected FrameAnimation(final AnimatedFrame target) {
        super(target);
    }

    /**
     * A copy constructor. This one creates a new frame animation based on an
     * old one by coping all data set to the source animation.
     * 
     * @param target the first target of the new animation
     * @param source the frame animation that supplies the data for the new
     *            animation
     */
    protected FrameAnimation(final AnimatedFrame target,
        final FrameAnimation source) {
        super(target);
        setup(source.frames, source.stillFrame, source.speed, source.mode);
    }

    /**
     * Update the animation. This calculates based on the supplied delta time
     * the new state of the animation and by this the frame the animation has to
     * display currently. Also it restarts the animation automatically in case
     * the animation is looped or it stops the animation correctly and reports
     * to the targets.
     * <p>
     * Only in case the frame has really changed, the change is reported to the
     * animation targets.
     * </p>
     * 
     * @param delta the time in milliseconds since the animation was updated
     *            last time
     */
    @Override
    public boolean animate(final int delta) {
        // animation has ended
        if (updateCurrentTime(delta)) {
            // just restart timers and keep going
            if (isLooped()) {
                setTiming();
                return true;
            }
            // stop animation
            setRunning(false);
            if (isCyclic()) {
                setFrame(stillFrame);
            }
            return false;
        }

        // calculate frame
        int frame = (int) (frames * animationProgress());
        // inverse animation
        if (isBackwards()) {
            frame *= -1;
        }
        frame = (stillFrame + frames + frame) % frames;

        // report only real changes
        if (lastFrame != frame) {
            setFrame(frame);
        }

        lastFrame = frame;
        return true;
    }

    /**
     * Start the animation again right away. The animation is set to its
     * starting parameters and launched right after.
     */
    @Override
    public void restart() {
        // set start position immediately
        setFrame(stillFrame);
        lastFrame = stillFrame;

        super.start();
    }

    /**
     * Stop the animation at its current state. A cyclic animation will set the
     * set stillFrame as the currently shown frame for all animation targets.
     * Calling this function will cause that all targets are reported that the
     * animation is finished with the parameter of this function set to
     * <code>false</code>.
     */
    @Override
    public void stop() {
        if (!isRunning()) {
            return;
        }

        setRunning(false);
        if (isCyclic()) {
            setFrame(stillFrame);
        }
        animationFinished(false);
    }

    /**
     * Setup the frame animation by setting the parameters of the animation
     * without starting the animation itself.
     * 
     * @param animFrames the amount of frames of this animation
     * @param animStillFrame the first and the last frame of the animation
     * @param animSpeed the speed the animation runs with, the larger the number
     *            the slower the animation
     * @param animMode the mode of the animation
     */
    protected void setup(final int animFrames, final int animStillFrame,
        final int animSpeed, final int animMode) {
        frames = animFrames;
        stillFrame = animStillFrame;
        mode = animMode;
        speed = animSpeed;

        setDuration(animSpeed * ANIMATION_FRAME);
    }

    /**
     * Setup the animation by setting its parameters and start it right away.
     * 
     * @param animFrames the amount of frames of this animation
     * @param animStillFrame the first and the last frame of the animation
     * @param animSpeed the speed the animation runs with, the larger the number
     *            the slower the animation
     * @param animMode the mode of the animation
     */
    protected void start(final int animFrames, final int animStillFrame,
        final int animSpeed, final int animMode) {
        setup(animFrames, animStillFrame, animSpeed, animMode);

        restart();
    }

    /**
     * Change the mode the animation runs with.
     * 
     * @param newMode the new mode of the animation
     */
    protected void updateMode(final int newMode) {
        mode = newMode;
    }

    /**
     * Change the speed of the animation and update the internal values for
     * this.
     * 
     * @param newSpeed the new speed of this animation, the larger the value the
     *            slower the animation
     */
    protected void updateSpeed(final int newSpeed) {
        if (newSpeed != speed) {
            speed = newSpeed;
            setDuration(newSpeed * ANIMATION_FRAME);
        }
    }

    /**
     * Check if the animation runs backwards.
     * 
     * @return <code>true</code> in case the animation runs backwards
     */
    private boolean isBackwards() {
        return (mode & BACKWARDS) > 0;
    }

    /**
     * Check if the animation runs cyclic, so it sets the {@link #stillFrame}
     * again after the animation is done.
     * 
     * @return <code>true</code> in case the animation shall run cyclic
     */
    private boolean isCyclic() {
        return (mode & CYCLIC) > 0;
    }

    /**
     * Check if the animation runs in a constant loop and does not stop itself.
     * 
     * @return <code>true</code> if the animation runs in a loop
     */
    private boolean isLooped() {
        return (mode & LOOPED) > 0;
    }

    /**
     * Update the frame for every target of this frame animation.
     * 
     * @param frame the frame that shall value that needs to be reported to
     *            every animation target
     */
    private void setFrame(final int frame) {
        final int targetCnt = getTargetCount();
        for (int i = 0; i < targetCnt; i++) {
            final Animated target = getAnimationTarget(i);
            if (target != null) {
                ((AnimatedFrame) target).setFrame(frame);
            }
        }
    }
}

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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumSet;

/**
 * A frame animation is a animation based on different images.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public final class FrameAnimation extends AbstractAnimation<AnimatedFrame> {
    public enum Mode {
        /**
         * Run animation backwards.
         */
        Backwards,

        /**
         * End animation with same frame as it started.
         */
        Cyclic,

        /**
         * Keep running in an endless loop.
         */
        Looped
    }

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
    private final EnumSet<Mode> mode;

    /**
     * The first and the last frame of this animation.
     */
    private int stillFrame;

    /**
     * Create a new frame animation with the default parameters. Note that a animation created by this can't be
     * started with {@link #restart()}. It first needs it running data set with {@link #start(int, int, int,
     * Mode...)} or {@link #setup(int, int, int, Mode...)}.
     */
    public FrameAnimation() {
        this(null);
    }

    /**
     * Create a new frame animation with the default parameters and a first
     * target. Note that a animation created by this can't be started with
     * {@link #restart()}. It first needs it running data set with
     * {@link #start(int, int, int, Mode...)} or {@link #setup(int, int, int, Mode...)}.
     *
     * @param target the first target of the animation
     */
    public FrameAnimation(@Nullable AnimatedFrame target) {
        super(target);
        mode = EnumSet.noneOf(Mode.class);
    }

    /**
     * A copy constructor. This one creates a new frame animation based on an
     * old one by coping all data set to the source animation.
     *
     * @param target the first target of the new animation
     * @param source the frame animation that supplies the data for the new
     * animation
     */
    public FrameAnimation(@Nullable AnimatedFrame target, @Nonnull FrameAnimation source) {
        super(target, source);
        frames = source.frames;
        stillFrame = source.stillFrame;
        mode = source.mode;
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
     * last time
     */
    @Override
    public boolean animate(int delta) {
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
        int frame = (int) (frames * getStoryboardProgress(true));
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
        start();

        // set start position immediately
        animate(0);
        lastFrame = stillFrame;
    }

    /**
     * Stop the animation at its current state. A cyclic animation will set the
     * set stillFrame as the currently shown frame for all animation targets.
     * Calling this function will cause that all targets are reported that the
     * animation is finished with the parameter of this function set to
     * {@code false}.
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
     * @param duration The time needed for the animation
     * @param animMode the mode of the animation
     */
    public void setup(int animFrames, int animStillFrame, int duration, Mode... animMode) {
        frames = animFrames;
        stillFrame = animStillFrame;
        updateMode(animMode);

        setDuration(duration);
    }

    /**
     * Setup the animation by setting its parameters and start it right away.
     *
     * @param animFrames the amount of frames of this animation
     * @param animStillFrame the first and the last frame of the animation
     * @param duration The time needed for the animation
     * @param animMode the mode of the animation
     */
    void start(int animFrames, int animStillFrame, int duration, Mode... animMode) {
        setup(animFrames, animStillFrame, duration, animMode);

        restart();
    }

    /**
     * Change the mode the animation runs with.
     *
     * @param newModes the new mode of the animation
     */
    void updateMode(Mode... newModes) {
        mode.clear();
        Collections.addAll(mode, newModes);
    }

    /**
     * Check if the animation runs backwards.
     *
     * @return {@code true} in case the animation runs backwards
     */
    private boolean isBackwards() {
        return mode.contains(Mode.Backwards);
    }

    /**
     * Check if the animation runs cyclic, so it sets the {@link #stillFrame}
     * again after the animation is done.
     *
     * @return {@code true} in case the animation shall run cyclic
     */
    private boolean isCyclic() {
        return mode.contains(Mode.Cyclic);
    }

    /**
     * Check if the animation runs in a constant loop and does not stop itself.
     *
     * @return {@code true} if the animation runs in a loop
     */
    private boolean isLooped() {
        return mode.contains(Mode.Looped);
    }

    /**
     * Update the frame for every target of this frame animation.
     *
     * @param frame the frame that shall value that needs to be reported to
     * every animation target
     */
    private void setFrame(int frame) {
        int targetCnt = getTargetCount();
        for (int i = 0; i < targetCnt; i++) {
            AnimatedFrame target = getAnimationTarget(i);
            if (target != null) {
                target.setFrame(frame);
            }
        }
    }
}

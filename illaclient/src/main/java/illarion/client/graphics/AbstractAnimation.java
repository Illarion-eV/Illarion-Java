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

import illarion.client.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The animation handler is the base class for all animations. It offers some
 * abstract class functions for animated objects like entities and some static
 * function for animations that handle the approaching of values.
 * <p>
 * This class handles all kind of animations. This could be frame based
 * animations as well as movement animations.
 * </p>
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
abstract class AbstractAnimation<T extends Animated> {
    private static final Logger log = LoggerFactory.getLogger(AbstractAnimation.class);

    /**
     * The current time of the animation. This value is always between the 0 and
     * {@link #duration}.
     */
    private int currentTime;

    /**
     * The time the full animation needs to run. After this time the animation
     * either stops or starts looping again from the start. The duration is
     * stored in milliseconds.
     * <p>
     * The maximal duration is 2^31 - 1 what is around 24 days.
     * </p>
     */
    private int duration;

    /**
     * A flag to determine if a animation is currently running or not. If the
     * animation is currently running this variable is true.
     */
    private boolean running;

    /**
     * Make the animation skip the next timing update.
     */
    private boolean skipNextUpdate;

    /**
     * The animation targets. This contains the objects that are handled by this
     * animation. All targets handled by one animation are updated at the same
     * time in a synchronized way.
     */
    @Nonnull
    private final List<T> targets;

    private float storyboardStart;
    private float storyboardEnd;

    /**
     * The constructor for a new animation. It does not start the animation
     * right away it rather prepares the animation and takes the first animation
     * target. In case there are more animation targets needed use
     * {@link #addTarget(Animated, boolean)}.
     *
     * @param firstTarget the first animation target, so the first object that
     * is actually animated
     */
    protected AbstractAnimation(@Nullable T firstTarget) {
        targets = new CopyOnWriteArrayList<>();
        if (firstTarget != null) {
            targets.add(firstTarget);
        }
        storyboardStart = 0.f;
        storyboardEnd = 1.f;
    }

    /**
     * The constructor for a new animation. It does not start the animation
     * right away it rather prepares the animation and takes the first animation
     * target. In case there are more animation targets needed use
     * {@link #addTarget(Animated, boolean)}.
     *
     * @param firstTarget the first animation target, so the first object that
     * is actually animated
     */
    protected AbstractAnimation(@Nullable T firstTarget, @Nonnull AbstractAnimation<T> sourceAnimation) {
        this(firstTarget);
        duration = sourceAnimation.duration;
    }

    /**
     * Add an animation target to this animation. All animations are handled
     * synchronized. So they start in the same moment and stop in the same
     * moment.
     *
     * @param target the new animation target that shall be added this animation
     * @param autoStart true in case the animation shall start right away after
     * the new animation target is added. The auto start will invoke
     * the {@link #restart()} method so this one needs all data to
     * start this animation in case its needed to do so
     */
    public final void addTarget(T target, boolean autoStart) {
        if (!targets.contains(target)) {
            targets.add(target);
        }

        // connect to animation
        if (autoStart && !isRunning()) {
            restart();
        }
    }

    /**
     * Called by game loop to execute animations. This function executes the
     * animation itself.
     *
     * @param delta the time since the last update of this animation
     * @return {@code true} in case the animation is in process,
     * {@code false} if its done
     */
    public abstract boolean animate(int delta);

    /**
     * Get the progress of the animation.
     *
     * @return a value between 0 and 1. 0 means the animation just started, 1
     * means the animation ended
     */
    public final float animationProgress() {
        return (float) currentTime / duration;
    }

    protected final float getStoryboardProgress(boolean wrapped) {
        float animationProgress = animationProgress();
        float storyboardSpan = storyboardEnd - storyboardStart;
        float unwrappedStoryboard = (storyboardSpan * animationProgress) + storyboardStart;
        if (wrapped) {
            return unwrappedStoryboard % 1.f;
        }
        return unwrappedStoryboard;
    }

    public final void setStoryboard(float start, float end) {
        storyboardStart = start;
        storyboardEnd = end;
    }

    public final void continueStoryboard(float length) {
        setStoryboard(storyboardEnd, storyboardEnd + length);
        log.debug("Expanding storyboard from {} to {}", storyboardStart, storyboardEnd);
    }

    public final void resetStoryboard() {
        setStoryboard(0.f, 1.f);
    }

    /**
     * Checks if the animation is currently running.
     *
     * @return true in case the animation is currently running
     */
    public final boolean isRunning() {
        return running;
    }

    /**
     * Remove an animation target from the animation. In case the removing of
     * the target empties the target list the animation stops automatically.
     *
     * @param target the animation target that shall be removed from the target
     * list
     */
    public final void removeTarget(T target) {
        targets.remove(target);

        // stop animation when last target disappears
        if (targets.isEmpty()) {
            stop();
        }
    }

    /**
     * Restarts an animation with the last set of parameters.
     */
    public abstract void restart();

    /**
     * Set the running state to a new value to either report that the animation
     * Stopped or that it started.
     *
     * @param newState the new value for the running state. True means the
     * animation is still running, false means that it stopped
     */
    public final void setRunning(boolean newState) {
        running = newState;
    }

    /**
     * Stop a animation. Do not forget to report the changed animation state by
     * setting the running indicator to false using {@link #setRunning(boolean)}
     * .
     */
    public abstract void stop();

    /**
     * Get the time remaining until the end of the animation.
     *
     * @return the time in ms that is still left to the end of the animation
     */
    public final int timeRemaining() {
        return duration - currentTime;
    }

    /**
     * Signals that an animation has ended. This reports that the animation is
     * finished to all animation targets.
     *
     * @param finished true in case the animation finished, false if not
     */
    protected final void animationFinished(boolean finished) {
        for (@Nonnull Animated target : targets) {
            target.animationFinished(finished);
        }
    }

    protected final void animationStarted() {
        targets.forEach(Animated::animationStarted);
    }

    /**
     * Get on of the animation targets of this animation. This is needed for
     * some update functions.
     *
     * @param index the list index of the animation target that is wanted. This
     * value needs to be between 0 and {@link #getTargetCount()}
     * @return the requested target
     */
    @Nullable
    protected final T getAnimationTarget(int index) {
        if (targets.size() <= index) {
            return null;
        }
        return targets.get(index);
    }

    /**
     * Get the amount of targets that are set to this animation.
     *
     * @return the amount of targets set to this animation
     */
    protected final int getTargetCount() {
        return targets.size();
    }

    /**
     * Set the value of the duration of this animation.
     *
     * @param newDuration the new value for the duration of this animation
     */
    public final void setDuration(int newDuration) {
        duration = newDuration;
    }

    /**
     * Get the duration of the animation.
     *
     * @return the duration of the animation
     */
    public final int getDuration() {
        return duration;
    }

    /**
     * Set the timing values of this animation. This sets the start time to the
     * current local time and sets the current time to the start time. The end
     * time is calculated by the start time plus the duration. So the duration
     * need to be known before this function is called.
     */
    protected final void setTiming() {
        currentTime = 0;
    }

    protected final void setSkipNextUpdate(boolean skip) {
        skipNextUpdate = skip;
    }

    /**
     * Start a animation. That causes that the timing values are set up and the
     * animation is registered to AnimationManager. Before calling this function
     * the {@link #duration} of the animation need to be set.
     */
    protected final void start() {
        // set timing
        setTiming();

        if (!running) {
            running = true;
            World.getAnimationManager().register(this);
            animationStarted();
        }
    }

    /**
     * Increase the current time by a set delta time. The time values are handled in milliseconds.
     *
     * @param delta the time in milliseconds that shall be added to the current time
     * @return true in case the animation ended by this update, means the current time reached the total duration of
     * the animation
     */
    protected final boolean updateCurrentTime(int delta) {
        if (skipNextUpdate) {
            skipNextUpdate = false;
        } else {
            currentTime += delta;
        }

        return currentTime > duration;
    }
}

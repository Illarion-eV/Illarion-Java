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
 * Class for a marker that is animated permanently.
 * 
 * @author Nop
 * @author Martin Karing
 * @since 0.95
 * @version 1.22
 */
class AnimatedMarker extends Marker {
    /**
     * The speed of this animation. The smaller this number the faster the
     * animation runs.
     */
    private static final int ANIMATION_SPEED = 6;

    /**
     * The frame animation that is used to show the animation of this marker.
     */
    private transient final FrameAnimation ani;

    /**
     * Create a animated marker object. This also instantiates the looped
     * animation that is used to display this marker. All parameters are
     * forwarded to the super function.
     * 
     * @param markerID the ID number of this marker
     * @param name the name of this marker
     * @param frames the amount of frames of this marker
     * @param offX the x offset of the marker
     * @param offY the y offset of the marker
     * @see illarion.client.graphics.Marker#Marker(int, String, int, int, int)
     */
    protected AnimatedMarker(final int markerID, final String name,
        final int frames, final int offX, final int offY) {
        super(markerID, name, frames, offX, offY);

        if (frames > 1) {
            // start animation right away. All tiles of this type will share it
            ani = new FrameAnimation(null);
            ani.setup(frames, 0, ANIMATION_SPEED, FrameAnimation.LOOPED);
        } else {
            ani = null;
        }
    }

    /**
     * Copy constructor. Copy a Entity to a new frame animation object. The new
     * object contains the same values as the old one.
     * 
     * @param org the entity that needs to be copied
     */
    private AnimatedMarker(final AnimatedMarker org) {
        super(org);
        ani = org.ani;
    }

    /**
     * Create a exact duplicate of this AnimatedMarker object.
     * 
     * @return the clone of this object instance
     */
    @Override
    public AnimatedMarker clone() {
        return new AnimatedMarker(this);
    }

    /**
     * Hide the animated marker and remove the marker from the animation target
     * list.
     */
    @Override
    public void hide() {
        super.hide();
        if (ani != null) {
            ani.removeTarget(this);
        }
    }

    /**
     * Fade in the animated marker and start the animation in case that was not
     * already done.
     */
    @Override
    public void show() {
        if (ani != null) {
            ani.addTarget(this, true);
        }
        super.show();
    }
}

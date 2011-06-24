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

import illarion.graphics.Sprite;
import illarion.graphics.SpriteColor;

/**
 * Created: 02.09.2005 22:29:57
 */
public class Marker extends AbstractEntity {
    public static final String GUI_PATH = "data/gui/";

    /*
     * TODO UCdetector: Remove unused code: public static final FadingCorridor
     * dummyCorr = new FadingCorridor();
     */

    protected Marker(final AbstractEntity org) {
        super(org);
        reset();
    }

    protected Marker(final int id, final String name, final int frames,
        final int offX, final int offY) {
        super(id, GUI_PATH, name, frames, 0, offX, offY, 0,
            Sprite.HAlign.center, Sprite.VAlign.middle, true);

        reset();
    }

    @SuppressWarnings("nls")
    public static Marker create(final int markerId) {
        return MarkerFactory.getInstance().getCommand(markerId);
    }

    @Override
    public Marker clone() {
        return new Marker(this);
    }

    @Override
    public boolean draw() {
        super.draw();

        return isVisible();
    }

    /**
     * Fade out marker
     */
    @Override
    public void hide() {
        setAlphaTarget(SpriteColor.COLOR_MIN);
    }

    public final void makeInvisible() {
        hide();
        setAlpha(SpriteColor.COLOR_MIN);
    }

    public final void makeVisible() {
        show();
        setAlpha(SpriteColor.COLOR_MAX);
    }

    @Override
    public void recycle() {
        MarkerFactory.getInstance().recycle(this);
    }

    @Override
    public void reset() {
        super.reset();
        makeInvisible();
    }

    /**
     * Fade in marker
     */
    @Override
    public void show() {
        setAlphaTarget(SpriteColor.COLOR_MAX);
    }

    @Override
    public void update(final int delta) {
        final int resultAlpha =
            AnimationUtility.translateAlpha(getAlpha(), getTargetAlpha(),
                FADING_SPEED, delta);
        setAlpha(resultAlpha);
    }
}

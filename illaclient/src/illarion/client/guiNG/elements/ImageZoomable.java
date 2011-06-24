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
package illarion.client.guiNG.elements;

import illarion.client.graphics.AnimationUtility;

import illarion.graphics.Sprite;

/**
 * This image variant allows to apply a zoom level that will be applied once the
 * image is rendered.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class ImageZoomable extends AbstractImage {
    /**
     * The serialization UID of this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The zoom factor that is applied to the image
     */
    private boolean currentZoomSTATE = false;

    /**
     * The offset that is applied to the drawing origin. Using this could be
     * handy from time to time in case the sprite is not aligned to the bottom
     * left edge. This variable store the x share of the offset.
     */
    private int drawOffsetX = 0;

    /**
     * The offset that is applied to the drawing origin. Using this could be
     * handy from time to time in case the sprite is not aligned to the bottom
     * left edge. This variable store the y share of the offset.
     */
    private int drawOffsetY = 0;

    /**
     * The zoom factor that is applied to the image
     */
    private float drawZoom = 1;

    /**
     * Maximal zoom
     */
    private final float MAX_ZOOM = 2.0f;

    /**
     * Minimal zoom
     */
    private final float MIN_ZOOM = 0.5f;

    /**
     * The targeted zoom factor
     */
    private float targetZoom = 1;

    /**
     * Zoom step size
     */
    private final float ZOOM_STEP_SIZE = 0.2f;

    /**
     * Draw the image.
     * 
     * @param delta the time since the render function was called last time
     */
    @Override
    public void draw(final int delta) {
        if (!isVisible()) {
            return;
        }
        final Sprite targetImage = getImage();
        if (targetImage != null) {

            drawZoom =
                AnimationUtility.translate(drawZoom, targetZoom,
                    ZOOM_STEP_SIZE, MIN_ZOOM, MAX_ZOOM, delta);
            targetImage.draw(getRelX() + drawOffsetX, getRelY() + drawOffsetY,
                getColor(), 0, drawZoom);
        }

        // if (drawZoom != targetZoom) {
        // this.draw(delta);
        // }

        super.draw(delta);
    }

    /**
     * Set the offset that is applied to the drawing origin. This is useful in
     * case the origin of the sprite is not located in the lower left edge.
     * 
     * @param offX the x share of the offset
     * @param offY the y share of the offset
     */
    public void setDrawingOffset(final int offX, final int offY) {
        drawOffsetX = offX;
        drawOffsetY = offY;
    }

    /**
     * Set the size of the widget to the size of the image. This function has no
     * effect in case the image is set to <code>null</code>.
     */
    public void setSizeToImage() {
        final Sprite targetImage = getImage();
        if (targetImage != null) {
            setWidth(targetImage.getWidth());
            setHeight(targetImage.getHeight());
        }
    }

    /**
     * Set the zoom of the image This function zooms in
     */
    public void zoomIN() {

        if (currentZoomSTATE) {

            targetZoom = drawZoom;
            currentZoomSTATE = false;
        } else {

            currentZoomSTATE = true;
            targetZoom = MAX_ZOOM;
        }
    }

    /**
     * Set the zoom of the image This function zooms out
     */
    public void zoomOUT() {

        if (currentZoomSTATE) {

            targetZoom = drawZoom;
            currentZoomSTATE = false;
        } else {

            currentZoomSTATE = true;
            targetZoom = MIN_ZOOM;
        }
    }
}

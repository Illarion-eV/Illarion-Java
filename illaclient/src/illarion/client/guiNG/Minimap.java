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
package illarion.client.guiNG;

import illarion.client.guiNG.elements.Button;
import illarion.client.guiNG.elements.DragLayer;
import illarion.client.guiNG.elements.Image;
import illarion.client.guiNG.elements.ImageZoomable;
import illarion.client.guiNG.elements.KeyDownButton;
import illarion.client.guiNG.elements.Mask;
import illarion.client.guiNG.elements.SolidColor;
import illarion.client.guiNG.elements.Widget;
import illarion.client.guiNG.event.MiniMapButtonEvent;
import illarion.client.guiNG.init.MinimapInit;
import illarion.client.guiNG.init.MinimapMaskInit;
import illarion.client.guiNG.init.MinimapOverlayInit;
import illarion.client.guiNG.init.SolidColorInit;

/**
 * This minimap widget takes care for building up and preparing the minimap that
 * is displayed in the client GUI. All requires texture bindings are prepared
 * here as well as the required controls.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class Minimap extends DragLayer {
    /**
     * The serialization UID of this widget.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor of the minimap that builds up the display properly.
     */
    public Minimap() {
        super();

        setDragTarget(this);
        setWidth(181);
        setHeight(160);
        setRelPos(getWidth() - getWidth(), getHeight() - getHeight());
        setShapeSource(DragLayer.SHAPE_WIDGET);
        enableDragging();

        final Mask mapMask = new Mask();
        mapMask.setRelPos(0, 0);
        mapMask.setWidth(160);
        mapMask.setHeight(160);
        mapMask.setInitScript(MinimapMaskInit.getInstance());
        addChild(mapMask);

        final SolidColor backColor = new SolidColor();
        backColor.setRelPos(0, 0);
        backColor.setWidth(mapMask.getWidth());
        backColor.setHeight(mapMask.getHeight());
        backColor.setInitScript(SolidColorInit.getInstance().setColor(0, 0, 0,
            180));
        mapMask.addChild(backColor);

        final ImageZoomable minimapImage = new ImageZoomable();
        minimapImage.setRelPos(0, 0);
        minimapImage.setWidth(mapMask.getWidth());
        minimapImage.setHeight(mapMask.getHeight());
        minimapImage.setInitScript(MinimapInit.getInstance());
        mapMask.addChild(minimapImage);

        final Image minimapOverlay = new Image();
        minimapOverlay.setRelPos(0, 0);
        minimapOverlay.setWidth(getWidth());
        minimapOverlay.setHeight(getHeight());
        minimapOverlay.setInitScript(MinimapOverlayInit.getInstance());
        addChild(minimapOverlay);

        final Widget minimapLock = Utility.buildLock(this);
        minimapLock.setRelPos(getWidth() - minimapLock.getWidth(), getHeight()
            - minimapLock.getHeight());
        addChild(minimapLock);

        // ZOOM IN BUTTON
        final KeyDownButton minimapButtonZoomIn = new KeyDownButton();
        addChild(minimapButtonZoomIn);
        minimapButtonZoomIn.setWidth(21);
        minimapButtonZoomIn.setHeight(21);
        minimapButtonZoomIn.setRelPos(158, 70);
        minimapButtonZoomIn.setVisible(true);
        final MiniMapButtonEvent zoomInEvent =
            MiniMapButtonEvent.getInstance();
        zoomInEvent.setImageZoomable(minimapImage);
        zoomInEvent.setButtonType(MiniMapButtonEvent.TYPE_ZOOMIN);
        minimapButtonZoomIn.setClickHandler(zoomInEvent);

        // ZOOM OUT BUTTON
        final KeyDownButton minimapButtonZoomOut = new KeyDownButton();
        addChild(minimapButtonZoomOut);
        minimapButtonZoomOut.setWidth(21);
        minimapButtonZoomOut.setHeight(21);
        minimapButtonZoomOut.setRelPos(155, 45);
        minimapButtonZoomOut.setVisible(true);
        final MiniMapButtonEvent zoomOutEvent =
            MiniMapButtonEvent.getInstance();
        zoomOutEvent.setImageZoomable(minimapImage);
        zoomOutEvent.setButtonType(MiniMapButtonEvent.TYPE_ZOOMOUT);
        minimapButtonZoomOut.setClickHandler(zoomOutEvent);

        // OVERVIEW BUTTON
        final Button minimapButtonOverview = new Button();
        addChild(minimapButtonOverview);
        minimapButtonOverview.setWidth(21);
        minimapButtonOverview.setHeight(21);
        minimapButtonOverview.setRelPos(155, 95);
        minimapButtonOverview.setVisible(true);
        final MiniMapButtonEvent overviewEvent =
            MiniMapButtonEvent.getInstance();
        overviewEvent.setButtonType(MiniMapButtonEvent.TYPE_OVERVIEW);
        minimapButtonOverview.setClickHandler(overviewEvent);

        // RED POINT IN THE MIDDLE
        final SolidColor playerColor = new SolidColor();
        playerColor.setRelPos(160 / 2, 158 / 2);
        playerColor.setHeight(3);
        playerColor.setWidth(3);
        playerColor.setInitScript(SolidColorInit.getInstance().setColor(256,
            0, 0, 256));
        addChild(playerColor);

    }
}

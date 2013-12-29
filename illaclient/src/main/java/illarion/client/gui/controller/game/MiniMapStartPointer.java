package illarion.client.gui.controller.game;

import de.lessvoid.nifty.elements.Element;
import illarion.client.gui.MiniMapGui;
import illarion.client.resources.MiscImageFactory;
import illarion.client.world.World;
import illarion.common.types.Location;
import illarion.common.util.FastMath;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.Graphics;
import org.illarion.engine.graphic.Sprite;
import org.illarion.engine.nifty.IgeRenderImage;

import javax.annotation.Nonnull;

/**
 * This is the implementation of a mini map pointer that marks the starting location of a quest.
 */
final class MiniMapStartPointer implements IgeRenderImage, MiniMapGui.Pointer {
    /**
     * The sprite that contains the point, that is displayed on the map.
     */
    @Nonnull
    private final Sprite pointSprite;

    /**
     * The location the arrow is supposed to point to.
     */
    @Nonnull
    private final Location targetLocation;

    /**
     * The Nifty-GUI element this pointer is assigned to.
     */
    @Nonnull
    private final Element parentElement;

    /**
     * The current delta value of the X coordinates.
     */
    private int currentDeltaX;

    /**
     * The current delta value of the Y coordinates.
     */
    private int currentDeltaY;

    /**
     * This is {@code} true if the quest is available right now.
     */
    private boolean available;

    /**
     * Create a new arrow pointer.
     */
    MiniMapStartPointer(@Nonnull final Element parentElement) {
        pointSprite = MiscImageFactory.getInstance().getTemplate(MiscImageFactory.MINI_MAP_EXCLAMATION).getSprite();
        targetLocation = new Location();
        this.parentElement = parentElement;
    }

    @Override
    public int getWidth() {
        return pointSprite.getWidth();
    }

    @Override
    public int getHeight() {
        return pointSprite.getHeight();
    }

    @Override
    public void dispose() {
        // nothing to do
    }

    @Override
    public void renderImage(
            @Nonnull final Graphics g,
            final int x,
            final int y,
            final int width,
            final int height,
            @Nonnull final Color color,
            final float imageScale) {
        renderImage(g, x, y, width, height, 0, 0, pointSprite.getWidth(), pointSprite.getHeight(), color, imageScale,
                    pointSprite.getWidth() / 2, pointSprite.getHeight() / 2);
    }

    @Override
    public void renderImage(
            @Nonnull final Graphics g,
            final int x,
            final int y,
            final int w,
            final int h,
            final int srcX,
            final int srcY,
            final int srcW,
            final int srcH,
            @Nonnull final Color color,
            final float scale,
            final int centerX,
            final int centerY) {
        final int scaledWidth = Math.round(w * scale);
        final int scaledHeight = Math.round(h * scale);

        final int fixedX = x + Math.round((w - scaledWidth) * ((float) centerX / (float) w));
        final int fixedY = y + Math.round((h - scaledHeight) * ((float) centerY / (float) h));

        if (isOnMapArea()) {
            final int offsetX = (FastMath.sqrt(FastMath.sqr(currentDeltaX) / 2) * -FastMath.sign(currentDeltaX)) +
                    (FastMath.sqrt(FastMath.sqr(currentDeltaY) / 2) * -FastMath.sign(currentDeltaY));
            final int offsetY = (FastMath.sqrt(FastMath.sqr(currentDeltaX) / 2) * FastMath.sign(currentDeltaX)) +
                    (FastMath.sqrt(FastMath.sqr(currentDeltaY) / 2) * -FastMath.sign(currentDeltaY));

            final Color renderColor;
            if (available) {
                renderColor = POINTER_COLOR;
            } else {
                renderColor = color;
            }

            g.drawTexture(pointSprite.getFrame(0), fixedX - offsetX, fixedY - offsetY, scaledWidth, scaledHeight, srcX,
                          srcY, srcW, srcH, centerX - fixedX, centerY - fixedY, 0.f, renderColor);
        }
    }

    @Override
    public void setCurrentQuest(boolean currentQuest) {

    }

    /**
     * This functions returns if this pointer is on the mini map right now or outside of it.
     *
     * @return {@code true} in case the pointer is on the map
     */
    private boolean isOnMapArea() {
        return FastMath.sqrt(FastMath.sqr(currentDeltaX) + FastMath.sqr(currentDeltaY)) < 71;
    }

    void setAvailable(final boolean available) {
        this.available = available;
    }

    /**
     * Update the angle of arrow.
     *
     * @param delta the time since the last update
     */
    void update(final int delta) {
        currentDeltaX = targetLocation.getScX() - World.getPlayer().getLocation().getScX();
        currentDeltaY = targetLocation.getScY() - World.getPlayer().getLocation().getScY();
    }

    @Override
    public void setTarget(@Nonnull final Location loc) {
        targetLocation.set(loc);
    }

    /**
     * Get the parent Nifty-GUI element.
     *
     * @return the parent element
     */
    @Nonnull
    public Element getParentElement() {
        return parentElement;
    }
}

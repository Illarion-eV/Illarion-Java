package illarion.client.gui.controller.game;

import de.lessvoid.nifty.elements.Element;
import illarion.client.graphics.AnimationUtility;
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
 * This is the implementation of the pointers. This class is the image that is displayed on the GUI in order to
 * show the arrow on the mini map. It takes care for rendering it with the proper rotation applied.
 */
final class MiniMapArrowPointer implements IgeRenderImage, MiniMapGui.Pointer {
    /**
     * The sprite that contains the arrow. This is displayed in case the point is outside the area of the mini map.
     */
    @Nonnull
    private final Sprite arrowSprite;

    /**
     * The sprite that contains the point. This is displayed in case the target location is on the area of the
     * mini map.
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
     * The current angle of the arrow in (1/10)°
     */
    private int currentAngle;

    /**
     * The target angle of the arrow in (1/10)°
     */
    private int targetAngle;

    /**
     * The current delta value of the X coordinates.
     */
    private int currentDeltaX;

    /**
     * The current delta value of the Y coordinates.
     */
    private int currentDeltaY;

    /**
     * Create a new arrow pointer.
     */
    MiniMapArrowPointer(@Nonnull final Element parentElement) {
        arrowSprite = MiscImageFactory.getInstance().getTemplate(MiscImageFactory.MINI_MAP_ARROW).getSprite();
        pointSprite = MiscImageFactory.getInstance().getTemplate(MiscImageFactory.MINI_MAP_POINT).getSprite();
        targetLocation = new Location();
        this.parentElement = parentElement;
    }

    @Override
    public int getWidth() {
        return arrowSprite.getWidth();
    }

    @Override
    public int getHeight() {
        return arrowSprite.getHeight();
    }

    @Override
    public void dispose() {
        // nothing to do
    }

    private float getAngle() {
        final double angle = Math.toDegrees(Math.atan2(currentDeltaY, currentDeltaX));

        return (float) angle + 45.f;
    }

    @Override
    public void renderImage(@Nonnull final Graphics g, final int x, final int y, final int width, final int height,
                            @Nonnull final Color color, final float imageScale) {
        renderImage(g, x, y, width, height, 0, 0, arrowSprite.getWidth(), arrowSprite.getHeight(), color,
                imageScale, arrowSprite.getWidth() / 2, arrowSprite.getHeight() / 2);
    }

    @Override
    public void renderImage(@Nonnull final Graphics g, final int x, final int y, final int w, final int h, final int srcX,
                            final int srcY, final int srcW, final int srcH, @Nonnull final Color color, final float scale,
                            final int centerX, final int centerY) {
        final int scaledWidth = Math.round(w * scale);
        final int scaledHeight = Math.round(h * scale);

        final int fixedX = x + Math.round((w - scaledWidth) * ((float) centerX / (float) w));
        final int fixedY = y + Math.round((h - scaledHeight) * ((float) centerY / (float) h));

        if (isOnMapArea()) {
            final int offsetX = (FastMath.sqrt(FastMath.sqr(currentDeltaX) / 2) * -FastMath.sign(currentDeltaX)) +
                    (FastMath.sqrt(FastMath.sqr(currentDeltaY) / 2) * -FastMath.sign(currentDeltaY));
            final int offsetY = (FastMath.sqrt(FastMath.sqr(currentDeltaX) / 2) * FastMath.sign(currentDeltaX)) +
                    (FastMath.sqrt(FastMath.sqr(currentDeltaY) / 2) * -FastMath.sign(currentDeltaY));

            g.drawTexture(pointSprite.getFrame(0), fixedX - offsetX, fixedY - offsetY,
                    scaledWidth, scaledHeight, srcX, srcY, srcW, srcH, centerX - fixedX, centerY - fixedY,
                    0.f, POINTER_COLOR);
        } else {
            final float angle = (float) currentAngle / 10.f;

            final int spriteOffsetX = arrowSprite.getOffsetX();
            final int spriteOffsetY = arrowSprite.getOffsetY();

            final float radianAngle = FastMath.toRadians(angle);
            final float sinAngle = FastMath.sin(radianAngle);
            final float cosAngle = FastMath.cos(radianAngle);

            final int rotationOffsetX = Math.round((spriteOffsetX * cosAngle) + (spriteOffsetY * sinAngle));
            final int rotationOffsetY = Math.round((spriteOffsetX * sinAngle) + (spriteOffsetY * cosAngle));

            g.drawTexture(arrowSprite.getFrame(0), fixedX + rotationOffsetX, fixedY - rotationOffsetY,
                    scaledWidth, scaledHeight, srcX, srcY, srcW, srcH, centerX - fixedX, centerY - fixedY,
                    angle, POINTER_COLOR);
        }
    }

    /**
     * This functions returns if this pointer is on the mini map right now or outside of it.
     *
     * @return {@code true} in case the pointer is on the map
     */
    private boolean isOnMapArea() {
        return FastMath.sqrt(FastMath.sqr(currentDeltaX) + FastMath.sqr(currentDeltaY)) < 71;
    }

    /**
     * Update the angle of arrow.
     *
     * @param delta the time since the last update
     */
    void update(final int delta) {
        final int dX = targetLocation.getScX() - World.getPlayer().getLocation().getScX();
        final int dY = targetLocation.getScY() - World.getPlayer().getLocation().getScY();

        if ((currentDeltaX != dX) || (currentDeltaY != dY)) {
            currentDeltaX = dX;
            currentDeltaY = dY;

            targetAngle = ((Math.round(getAngle() * 10.f) % 3600) + 3600) % 3600;
        }

        animateAngle(delta);
    }

    /**
     * Perform the animation of this arrow.
     *
     * @param delta the time since the last update
     */
    private void animateAngle(final int delta) {
        if (targetAngle == currentAngle) {
            return;
        }

        final int angleDiff = targetAngle - currentAngle;
        if (Math.abs(angleDiff) <= 1800) {
            currentAngle += AnimationUtility.approach(0, angleDiff, -1800, 1800, delta);
        } else if (angleDiff > 0) {
            currentAngle += AnimationUtility.approach(0, angleDiff - 3600, -3600, 0, delta);
        } else {
            currentAngle += AnimationUtility.approach(0, angleDiff + 3600, 0, 3600, delta);
        }
        currentAngle = ((currentAngle % 3600) + 3600) % 3600;
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

package illarion.client.gui;

import illarion.client.graphics.AbstractEntity;
import illarion.common.graphics.Layer;
import illarion.common.types.DisplayCoordinate;
import illarion.common.types.Rectangle;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.Graphics;
import org.illarion.engine.graphic.Texture;
import org.illarion.engine.nifty.IgeRenderImage;
import org.illarion.engine.nifty.IgeTextureRenderImage;

import javax.annotation.Nonnull;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class EntityRenderImage implements IgeRenderImage {
    @Nonnull
    private final GameContainer container;
    @Nonnull
    private final AbstractEntity<?> entity;
    @Nonnull
    private final Rectangle displayArea;

    public EntityRenderImage(@Nonnull GameContainer container, @Nonnull AbstractEntity<?> entity) {
        this.container = container;
        this.entity = entity;

        displayArea = new Rectangle();
    }

    @Override
    public void renderImage(@Nonnull Graphics g, int x, int y, int width, int height, @Nonnull Color color,
                            float imageScale) {
        entity.getTemplate().getSprite().getDisplayArea(x, y, imageScale, 0.f, displayArea);

        entity.setScreenPos(new DisplayCoordinate(x + (x - displayArea.getX()), y + (y - displayArea.getY()), 0));
        entity.setBaseColor(color);
        entity.setScale(imageScale);
        entity.update(container, 0);
        entity.render(g);
    }

    @Override
    public void renderImage(@Nonnull Graphics g, int x, int y, int w, int h, int srcX, int srcY, int srcW, int srcH,
                            @Nonnull Color color, float scale, int centerX, int centerY) {
        renderImage(g, x, y, w, h, color, scale);
    }

    @Override
    public int getWidth() {
        return entity.getTemplate().getSprite().getWidth();
    }

    @Override
    public int getHeight() {
        return entity.getTemplate().getSprite().getHeight();
    }

    @Override
    public void dispose() {

    }
}

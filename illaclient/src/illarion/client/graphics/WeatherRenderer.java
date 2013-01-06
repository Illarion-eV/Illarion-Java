/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.graphics;

import illarion.client.graphics.shader.FogShader;
import illarion.client.graphics.shader.RainShader;
import illarion.client.graphics.shader.Shader;
import illarion.client.graphics.shader.ShaderManager;
import illarion.client.world.World;
import org.lwjgl.opengl.GL13;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * This class takes care for applying the weather effects to the screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class WeatherRenderer {
    /**
     * The shader that is used to display fog.
     */
    private FogShader fogShader;

    /**
     * The texture for the rain.
     */
    private Image rainTexture;

    /**
     * The shader that is used to render the rain.
     */
    private RainShader rainShader;

    /**
     * This is set {@code true} once loading is done.
     */
    private boolean loadingDone;

    /**
     * This function is used to check and load the required resources.
     */
    private void load() {
        if (loadingDone) {
            return;
        }

        loadingDone = true;

        fogShader = ShaderManager.getShader(Shader.Fog, FogShader.class);
        rainTexture = TextureLoader.getInstance().getTexture("data/gui/", "rain");
        rainShader = ShaderManager.getShader(Shader.Rain, RainShader.class);
    }

    /**
     * This function is called to update the animations of the renderer. Its called once in every render loop.
     *
     * @param c     the container that displays the game
     * @param delta the time since the last render loop in milliseconds
     */
    public void update(final GameContainer c, final int delta) {
        rainDropping += (1.f * delta) / 2000.f;
        rainDropping %= 1.f;

        gustAnimation += (1.f * delta) / 5000.f;
    }

    private Image processImage0;
    private Image processImage1;
    private int lastImage = 0;
    private float rainDropping;
    private float gustAnimation;

    private Image getNextProcessImage(final int width, final int height) throws SlickException {
        if (lastImage == 1) {
            if (processImage0 == null) {
                processImage0 = Image.createOffscreenImage(width, height);
            }
            lastImage = 0;
            return processImage0;
        }
        if (processImage1 == null) {
            processImage1 = Image.createOffscreenImage(width, height);
        }
        lastImage = 1;
        return processImage1;
    }


    /**
     * This function applies the post processing to the image.
     *
     * @param renderImage the image that is rendered to the screen, this image remains unchanged
     * @return the new image with the post processed graphics
     */
    public Image postProcess(final Image renderImage) throws SlickException {
        load();

        Image currentImage = renderImage;

        if (World.getWeather().isFog() && World.getWeather().isOutside()) {
            final Image tempImage = getNextProcessImage(renderImage.getWidth(), renderImage.getHeight());
            final Graphics g = tempImage.getGraphics();
            Graphics.setCurrent(g);

            fogShader.bind();
            fogShader.setTexture(0);
            final float x = 0.5f * renderImage.getTextureWidth();
            final float y = 0.5f * renderImage.getTextureHeight();
            fogShader.setCenter(x, y);
            fogShader.setDensity(World.getWeather().getFog() * ((float) renderImage.getHeight() / 200.f));

            g.drawImage(currentImage, 0, 0);

            fogShader.unbind();

            currentImage = tempImage;
        }

        if (World.getWeather().isRain() && World.getWeather().isOutside()) {
            final Image tempImage = getNextProcessImage(renderImage.getWidth(), renderImage.getHeight());
            final Graphics g = tempImage.getGraphics();
            Graphics.setCurrent(g);

            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            rainTexture.bind();
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            renderImage.bind();

            rainShader.bind();
            rainShader.setBackgroundTexture(0);
            rainShader.setRainTexture(1);
            rainShader.setRainTextureOffset(rainTexture.getTextureOffsetX(), rainTexture.getTextureOffsetY());
            rainShader.setRainTextureSize(rainTexture.getTextureWidth(), rainTexture.getTextureHeight());
            rainShader.setRainTextureScale(
                    (float) renderImage.getTexture().getTextureWidth() / (float) rainTexture.getTexture().getTextureWidth(),
                    (float) renderImage.getTexture().getTextureHeight() / (float) rainTexture.getTexture().getTextureHeight());
            rainShader.setIntensity(World.getWeather().getRain() / 100.f);
            rainShader.setGustStrength(Math.abs(World.getWeather().getGusts()) / 100.f);
            rainShader.setWindDirection(World.getWeather().getWind() / 100.f);
            rainShader.setAnimation(rainDropping);
            rainShader.setGustAnimation(gustAnimation);
            rainShader.setMapOffset(Camera.getInstance().getViewportOffsetX(), Camera.getInstance().getViewportOffsetY());

            g.drawImage(currentImage, 0, 0);

            rainShader.unbind();
            currentImage = tempImage;
        }

        return currentImage;
    }
}

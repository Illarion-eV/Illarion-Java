/*
 * This file is part of the Illarion project.
 *
 * Copyright 2026 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 */
package org.illarion.engine.backend.gdx;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;
import illarion.common.util.ProgressMonitor;
import org.illarion.engine.backend.shared.TextureAtlasFinalizeTask;
import org.testng.annotations.Test;

import java.util.concurrent.FutureTask;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class GdxTextureManagerTest extends PixmapTestSupport {
    @Test
    public void successfulUploadReleasesPixelsButKeepsTheUnmanagedTexture() {
        TrackingPixmap pixels = createPixels();
        GdxTexture result = new GdxTextureManager().loadTexture("test", pixels);
        assertNotNull(result);
        Texture texture = result.getTextureRegion().getTexture();

        try {
            assertTrue(pixels.isDisposed());
            assertEquals(pixels.disposals, 1);
            assertEquals(uploads, 1);
            assertEquals(result.getWidth(), 4);
            assertEquals(result.getHeight(), 4);
            assertFalse(texture.isManaged(), "Context recreation must not depend on the released pixels");
            result.getSubTexture(0, 0, 2, 2).dispose();
            result.dispose();
            assertEquals(deletions, 0, "Atlas regions must not delete their shared texture");
        } finally {
            texture.dispose();
        }

        assertEquals(deletions, 1);
        assertEquals(pixels.disposals, 1);
    }

    @Test
    public void failedUploadReleasesPixelsAndPreservesTheNullResult() {
        TrackingPixmap pixels = createPixels();
        uploadFailure = new GdxRuntimeException("Simulated upload failure");

        assertNull(new GdxTextureManager().loadTexture("test", pixels));
        assertTrue(pixels.isDisposed());
        assertEquals(pixels.disposals, 1);
    }

    @Test
    public void unexpectedFailureReleasesPixelsAndPropagates() {
        TrackingPixmap pixels = createPixels();
        uploadFailure = new IllegalStateException("Simulated unexpected failure");

        try {
            new GdxTextureManager().loadTexture("test", pixels);
            fail("Expected upload failure");
        } catch (IllegalStateException failure) {
            assertSame(failure, uploadFailure);
        }

        assertTrue(pixels.isDisposed());
        assertEquals(pixels.disposals, 1);
    }

    @Test
    public void atlasFinalisationKeepsRegionsUsableAfterReleasingPixels() {
        TrackingPixmap pixels = createPixels();
        GdxTextureManager manager = new GdxTextureManager();
        manager.addTextureDirectory("test");
        FutureTask<Pixmap> preload = new FutureTask<>(() -> pixels);
        preload.run();
        TextureAtlasFinalizeTask<Pixmap> task = new TextureAtlasFinalizeTask<>(
                preload, "test/atlas", manager, new ProgressMonitor(), 1.f);
        task.addSprite("test/sprite", 1, 1, 2, 2);
        task.run();
        GdxTexture atlas = (GdxTexture) manager.getTexture("test", "atlas");
        assertNotNull(atlas);

        try {
            assertTrue(task.isDone());
            assertTrue(pixels.isDisposed());
            assertEquals(pixels.disposals, 1);
            GdxTexture sprite = (GdxTexture) manager.getTexture("test", "sprite");
            assertNotNull(sprite);
            assertEquals(sprite.getWidth(), 2);
            assertEquals(sprite.getHeight(), 2);
            assertSame(sprite.getTextureRegion().getTexture(), atlas.getTextureRegion().getTexture());
        } finally {
            atlas.getTextureRegion().getTexture().dispose();
        }
    }
}

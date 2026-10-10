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

import com.badlogic.gdx.utils.GdxRuntimeException;
import illarion.common.types.ServerCoordinate;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.WorldMap;
import org.testng.annotations.Test;

import java.lang.reflect.Proxy;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class GdxWorldMapTest extends PixmapTestSupport {
    private static final ServerCoordinate ORIGIN = new ServerCoordinate(0, 0, 0);
    private static final ServerCoordinate TILE = new ServerCoordinate(1, 1, 0);
    private static final int GREEN_TILE = 1;
    private static final int GREEN_PIXEL = 0xb6d69eff;
    private static final int BLACK_PIXEL = 0x000000ff;
    private static final int TIMEOUT_SECONDS = 5;
    private static final GameContainer CONTAINER = (GameContainer) Proxy.newProxyInstance(
            GameContainer.class.getClassLoader(), new Class<?>[]{GameContainer.class}, (proxy, method, arguments) -> {
                throw new AssertionError("Unexpected game container access: " + method.getName());
            });

    @Test
    public void disposalReleasesBothResourcesExactlyOnce() {
        TrackingPixmap pixels = createPixels();
        GdxWorldMap map = createMap(pixels);
        map.dispose();

        assertTrue(pixels.isDisposed());
        assertEquals(pixels.disposals, 1);
        assertEquals(deletions, 1);
        map.dispose();
        assertEquals(pixels.disposals, 1);
        assertEquals(deletions, 1);
    }

    @Test
    public void normalUpdatesAndClearingRetainPixelsUntilDisposal() {
        TrackingPixmap pixels = createPixels();
        GdxWorldMap map = createMap(pixels);
        map.setTile(TILE, GREEN_TILE, WorldMap.NO_TILE, false);
        assertEquals(pixels.getPixel(1, 1), GREEN_PIXEL);
        map.render(CONTAINER);
        map.render(CONTAINER);
        assertEquals(updates, 1, "An unchanged map must not upload again");
        assertFalse(pixels.isDisposed());

        map.clear();
        assertEquals(pixels.getPixel(1, 1), BLACK_PIXEL);
        map.render(CONTAINER);
        assertEquals(updates, 2);
        assertFalse(pixels.isDisposed());
        map.dispose();
        assertTrue(pixels.isDisposed());
    }

    @Test
    public void callbacksClearingAndRenderingAfterDisposalDoNotAccessPixels() {
        TrackingPixmap pixels = createPixels();
        GdxWorldMap map = createMap(pixels);
        map.dispose();
        map.setTile(TILE, GREEN_TILE, WorldMap.NO_TILE, false);
        map.clear();
        map.setMapOrigin(TILE);
        map.render(CONTAINER);

        assertEquals(updates, 0);
        assertEquals(pixels.disposals, 1);
    }

    @Test
    public void queuedRefreshesAfterDisposalDoNotRequestTiles() {
        TrackingPixmap pixels = createPixels();
        GdxWorldMap map = new GdxWorldMap((location, callback) -> fail("Disposed map requested a tile"), pixels);
        map.dispose();

        // A queued refresh must also be harmless if the origin was never initialised.
        map.setTileChanged(TILE);
        map.setMapChanged();
        map.setTile(TILE, GREEN_TILE, WorldMap.NO_TILE, false);
        map.clear();
        map.render(CONTAINER);
        assertEquals(pixels.disposals, 1);
    }

    @Test
    public void failedConstructionReleasesTheOwnedPixelBuffer() {
        TrackingPixmap pixels = createPixels();
        uploadFailure = new GdxRuntimeException("Simulated map texture upload failure");

        try {
            createMap(pixels);
            fail("Expected map construction to fail");
        } catch (GdxRuntimeException failure) {
            assertSame(failure, uploadFailure);
        }

        assertTrue(pixels.isDisposed());
        assertEquals(pixels.disposals, 1);
    }

    @Test
    public void failedTextureDeletionStillReleasesPixelsAndClosesTheMap() {
        TrackingPixmap pixels = createPixels();
        GdxWorldMap map = createMap(pixels);
        deletionFailure = new GdxRuntimeException("Simulated texture deletion failure");

        try {
            map.dispose();
            fail("Expected texture deletion to fail");
        } catch (GdxRuntimeException failure) {
            assertSame(failure, deletionFailure);
        }

        assertTrue(pixels.isDisposed());
        map.dispose();
        map.setTile(TILE, GREEN_TILE, WorldMap.NO_TILE, false);
        assertEquals(pixels.disposals, 1);
        assertEquals(deletions, 1);
    }

    @Test
    public void delayedProviderCallbackIsDiscardedAndTheRefreshStops() throws Exception {
        TrackingPixmap pixels = createPixels();
        CountDownLatch providerEntered = new CountDownLatch(1);
        CountDownLatch releaseProvider = new CountDownLatch(1);
        AtomicInteger requests = new AtomicInteger();
        GdxWorldMap map = new GdxWorldMap((location, callback) -> {
            requests.incrementAndGet();
            providerEntered.countDown();
            await(releaseProvider);
            callback.setTile(location, GREEN_TILE, WorldMap.NO_TILE, false);
        }, pixels);
        map.setMapOrigin(ORIGIN);
        FutureTask<Void> refresh = new FutureTask<>(map::setMapChanged, null);
        Thread refreshThread = start(refresh);
        FutureTask<Void> disposal = new FutureTask<>(map::dispose, null);
        Thread disposalThread = null;

        try {
            await(providerEntered);
            disposalThread = start(disposal);
            disposal.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            assertTrue(pixels.isDisposed(), "Disposal must not wait for an external provider");
        } finally {
            releaseProvider.countDown();
            join(refreshThread);
            if (disposalThread != null) {
                join(disposalThread);
            }
        }

        refresh.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertEquals(requests.get(), 1, "The full-map scan must stop after disposal");
        assertEquals(pixels.disposals, 1);
    }

    @Test
    public void disposalWaitsForAnActivePixelWrite() throws Exception {
        TrackingPixmap pixels = createPixels();
        GdxWorldMap map = createMap(pixels);
        CountDownLatch writeEntered = new CountDownLatch(1);
        CountDownLatch releaseWrite = new CountDownLatch(1);
        pixels.beforeDraw = () -> {
            writeEntered.countDown();
            await(releaseWrite);
        };
        FutureTask<Void> write = new FutureTask<>(() -> map.setTile(TILE, GREEN_TILE, WorldMap.NO_TILE, false), null);
        Thread writer = start(write);
        FutureTask<Void> disposal = new FutureTask<>(map::dispose, null);
        Thread disposer = null;

        try {
            await(writeEntered);
            disposer = start(disposal);
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(TIMEOUT_SECONDS);
            while (disposer.isAlive() && (disposer.getState() != Thread.State.BLOCKED)
                    && (System.nanoTime() < deadline)) {
                Thread.sleep(1);
            }

            assertEquals(disposer.getState(), Thread.State.BLOCKED, "Disposal must share the pixel-write lock");
            assertFalse(pixels.isDisposed());
        } finally {
            releaseWrite.countDown();
            join(writer);
            if (disposer != null) {
                join(disposer);
            }
        }

        write.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        disposal.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertTrue(pixels.isDisposed());
        assertEquals(pixels.disposals, 1);
    }

    @Test
    public void repeatedMapLifecyclesReleaseEveryBuffer() {
        for (int cycle = 0; cycle < 10; cycle++) {
            TrackingPixmap pixels = createPixels();
            GdxWorldMap map = createMap(pixels);
            map.setTile(TILE, GREEN_TILE, WorldMap.NO_TILE, false);
            map.render(CONTAINER);
            map.dispose();
            assertTrue(pixels.isDisposed());
            assertEquals(pixels.disposals, 1);
        }

        assertEquals(deletions, 10);
    }

    private static GdxWorldMap createMap(TrackingPixmap pixels) {
        GdxWorldMap map = new GdxWorldMap((location, callback) -> fail("Unexpected tile request"), pixels);
        map.setMapOrigin(ORIGIN);
        return map;
    }

    private static Thread start(FutureTask<Void> task) {
        Thread thread = new Thread(task, "Pixmap lifecycle test");
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    private static void await(CountDownLatch latch) {
        try {
            assertTrue(latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS), "Timed out waiting for test synchronisation");
        } catch (InterruptedException failure) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Interrupted while waiting for test synchronisation", failure);
        }
    }

    private static void join(Thread thread) throws InterruptedException {
        thread.join(TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS));
        assertFalse(thread.isAlive(), "Test worker did not terminate");
    }
}

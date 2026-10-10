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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.GdxNativesLoader;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/** Uses real native pixel buffers, with recorded OpenGL calls instead of a desktop context. */
public abstract class PixmapTestSupport {
    private static final ReentrantLock GRAPHICS_LOCK = new ReentrantLock();

    private GL20 previousGl;
    private GL20 previousGl20;
    private Graphics previousGraphics;
    private final List<TrackingPixmap> pixmaps = new ArrayList<>();
    int uploads;
    int updates;
    int deletions;
    RuntimeException uploadFailure;
    RuntimeException deletionFailure;

    @BeforeMethod
    public void prepareGraphics() {
        GRAPHICS_LOCK.lock();
        previousGl = Gdx.gl;
        previousGl20 = Gdx.gl20;
        previousGraphics = Gdx.graphics;
        uploads = 0;
        updates = 0;
        deletions = 0;
        uploadFailure = null;
        deletionFailure = null;
        GdxNativesLoader.load();
        Gdx.gl = (GL20) Proxy.newProxyInstance(GL20.class.getClassLoader(), new Class<?>[]{GL20.class},
                (proxy, method, arguments) -> {
                    switch (method.getName()) {
                        case "glGenTexture":
                            return 1;
                        case "glTexImage2D":
                            uploads++;
                            if (uploadFailure != null) {
                                throw uploadFailure;
                            }

                            return null;
                        case "glTexSubImage2D":
                            updates++;
                            return null;
                        case "glDeleteTexture":
                            deletions++;
                            if (deletionFailure != null) {
                                throw deletionFailure;
                            }

                            return null;
                        case "glBindTexture":
                        case "glTexParameterf":
                        case "glTexParameteri":
                        case "glPixelStorei":
                            return null;
                        default:
                            throw new AssertionError("Unexpected GL operation: " + method.getName());
                    }
                });
        Gdx.gl20 = Gdx.gl;
        Gdx.graphics = (Graphics) Proxy.newProxyInstance(Graphics.class.getClassLoader(),
                new Class<?>[]{Graphics.class}, (proxy, method, arguments) -> {
                    if ("supportsExtension".equals(method.getName())) {
                        return false;
                    }

                    throw new AssertionError("Unexpected graphics operation: " + method.getName());
                });
    }

    @AfterMethod(alwaysRun = true)
    public void restoreGraphics() {
        try {
            for (TrackingPixmap pixels : pixmaps) {
                if (!pixels.isDisposed()) {
                    pixels.dispose();
                }
            }
        } finally {
            pixmaps.clear();
            Gdx.gl = previousGl;
            Gdx.gl20 = previousGl20;
            Gdx.graphics = previousGraphics;
            GRAPHICS_LOCK.unlock();
        }
    }

    TrackingPixmap createPixels() {
        TrackingPixmap pixels = new TrackingPixmap();
        pixmaps.add(pixels);
        return pixels;
    }

    static class TrackingPixmap extends Pixmap {
        int disposals;
        Runnable beforeDraw;

        TrackingPixmap() {
            super(4, 4, Format.RGB888);
        }

        @Override
        public void dispose() {
            disposals++;
            checkAlive();
            super.dispose();
        }

        @Override
        public void drawPixel(int x, int y) {
            checkAlive();
            if (beforeDraw != null) {
                beforeDraw.run();
            }

            checkAlive();
            super.drawPixel(x, y);
        }

        @Override
        public void fill() {
            checkAlive();
            super.fill();
        }

        private void checkAlive() {
            if (isDisposed()) {
                throw new AssertionError("Native pixel buffer accessed after disposal");
            }
        }
    }
}

/*
 * This file is part of the Illarion Graphics Engine.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Graphics Engine is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Graphics Engine is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Graphics Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.graphics.jogl;

import java.awt.Cursor;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLCapabilitiesImmutable;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import org.apache.log4j.Logger;

import com.jogamp.newt.awt.NewtCanvasAWT;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.glsl.fixedfunc.FixedFuncUtil;

import illarion.graphics.Graphics;
import illarion.graphics.RenderTask;
import illarion.graphics.generic.AbstractTextureAtlas;

/**
 * This Display canvas uses the NEWT windowing system to display the graphics.
 * 
 * @author Martin Karing
 * @since 2.00
 * @version 2.00
 */
public final class DisplayNewtAWT extends NewtCanvasAWT implements Display,
    GLEventListener {
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger
        .getLogger(DisplayNewtAWT.class);

    /**
     * The serialization UID of this canvas.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The animator that takes care for the display speed of this canvas.
     */
    private AnimatorBase animator;

    /**
     * The JOGL render manager that supplies the data to display.
     */
    private final RenderManagerJOGL manager;

    /**
     * The GLWindow that is wrapped by this canvas.
     */
    private final GLWindow window;

    /**
     * Constructor that allows setting the capabilities of this display.
     * 
     * @param capabilities capabilities of the canvas
     */
    public DisplayNewtAWT(final GLCapabilities capabilities) {
        this(GLWindow.create(capabilities));
    }

    /**
     * Constructor that allows setting the capabilities of this display as well
     * as the size.
     * 
     * @param width width of the canvas
     * @param height height of the canvas
     * @param capabilities capabilities of the canvas
     */
    public DisplayNewtAWT(final int width, final int height,
        final GLCapabilities capabilities) {
        this(capabilities);
        setSize(width, height);
    }

    /**
     * Private constructor that is needed to fetch the created window instance.
     * 
     * @param glWindow the window that is wrapped by this class
     */
    private DisplayNewtAWT(final GLWindow glWindow) {
        super(glWindow);
        window = glWindow;
        manager =
            (RenderManagerJOGL) Graphics.getInstance().getRenderManager();
        window.addGLEventListener(this);
        window.setAutoSwapBufferMode(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    @Override
    public void addInputListener(final Object listener) {
        if (listener instanceof com.jogamp.newt.event.MouseListener) {
            window
                .addMouseListener((com.jogamp.newt.event.MouseListener) listener);
        }
        if (listener instanceof com.jogamp.newt.event.KeyListener) {
            window
                .addKeyListener((com.jogamp.newt.event.KeyListener) listener);
        }
    }

    /**
     * Main drawing function. Inside this function the main part of the drawing
     * is done.
     * 
     * @param drawable the drawable object that is used to trigger the drawing
     *            actions
     */
    @Override
    public void display(final GLAutoDrawable drawable) {
        final GL gl = drawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        manager.draw();
    }

    /**
     * Dispose function called when the rendering of this canvas stopped. It can
     * be used to free some resources.
     * 
     * @param drawable the drawable object used to free some resources
     */
    @Override
    public void dispose(final GLAutoDrawable drawable) {
        if (animator != null) {
            animator.stop();
            animator = null;
        }
        TextureJOGL.dispose();
        AbstractTextureAtlas.dispose();
    }

    /**
     * Get the auto drawable target of this class.
     */
    @Override
    public GLAutoDrawable getDrawable() {
        return window;
    }

    /**
     * Init the rendering of this canvas. This function is called right before
     * the first rendering run. Its used to setup everything correctly.
     * 
     * @param drawable the drawable object used to access the openGL functions
     */
    @Override
    public void init(final GLAutoDrawable drawable) {
        if (drawable.getContext() == null) {
            return;
        }
        displayOpenGLStatusInfo();
        // drawable.setGL(new DebugGL(drawable.getGL()));
        setupViewport(drawable);

        boolean releaseContext = false;
        if (GLContext.getCurrent() == null) {
            drawable.getContext().makeCurrent();
            releaseContext = true;
        }

        final GL gl = drawable.getGL();
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        // getContext().setSynchronized(true);
        gl.setSwapInterval(0); // disable vsync

        if (releaseContext) {
            drawable.getContext().release();
        }
    }

    @Override
    public boolean isInputListenerSupported(final Object listener) {
        if (listener instanceof com.jogamp.newt.event.MouseListener) {
            return true;
        }
        if (listener instanceof com.jogamp.newt.event.KeyListener) {
            return true;
        }
        return false;
    }

    @Override
    public void paint(final java.awt.Graphics g) {
        if (animator != null) {
            super.paint(g);
        } else {
            window.display();
        }
    }

    @Override
    public void removeInputListener(final Object listener) {
        if (listener instanceof com.jogamp.newt.event.MouseListener) {
            window
                .removeMouseListener((com.jogamp.newt.event.MouseListener) listener);
        }
        if (listener instanceof com.jogamp.newt.event.KeyListener) {
            window
                .removeKeyListener((com.jogamp.newt.event.KeyListener) listener);
        }
    }

    @Override
    public void renderTask(final RenderTask task) {
        manager.addTask(task);
    }

    /**
     * Reshaping function. This is called in case the size of the render canvas
     * changed and the viewport needs to be fixed.
     * 
     * @param drawable the drawable object used to change the viewport
     * @param x the x offset to the old location
     * @param y the y offset to the old location
     * @param width the new width of the canvas
     * @param height the new height of the canvas
     */
    @Override
    public void reshape(final GLAutoDrawable drawable, final int x,
        final int y, final int width, final int height) {
        setupViewport(drawable);
        ((RenderDisplayJOGL) Graphics.getInstance().getRenderDisplay())
            .setDisplaySize(width, height);
    }

    @Override
    public void setCursor(final Cursor cur) {
        LOGGER.debug("CALL setCursor"); //$NON-NLS-1$
        super.setCursor(cur);
    }

    /*
     * (non-Javadoc)
     * 
     * @see illarion.graphics.jogl.Display#setFPS(int)
     */
    @Override
    public void setFPS(final int fps) {
        manager.renderStarted();
        if (animator != null) {
            animator.stop();
            animator.remove(window);
            animator = null;
        }
        if (fps > -1) {
            setIgnoreRepaint(true);
            // animator = new Animator(this);
            animator = new FPSAnimator(window, fps, true);
            animator.start();
        } else {
            setIgnoreRepaint(false);
        }
    }

    @Override
    public void shutdown() {
        destroy();
    }

    /**
     * Overwritten update function to ensure the paint function is called.
     * 
     * @param g the graphics object that is used to draw
     */
    @Override
    public void update(final java.awt.Graphics g) {
        // window.getContext().makeCurrent();
        paint(g);
        // window.getContext().release();
    }

    /**
     * Display all informations about the currently activated OpenGL mode.
     */
    @SuppressWarnings("nls")
    private void displayOpenGLStatusInfo() {
        final GLCapabilitiesImmutable activeCaps =
            window.getChosenGLCapabilities();

        if (activeCaps.getHardwareAccelerated()) {
            LOGGER.debug("OpenGL Hardware acceleration active");
        } else {
            LOGGER.warn("OpenGL Hardware acceleration inactive");
        }
        LOGGER.debug("Active Samples: "
            + Integer.toString(activeCaps.getNumSamples()));

        if (activeCaps.getGLProfile().isGLES1()) {
            LOGGER.debug("OpenGL ES 1.x supported");
        } else {
            LOGGER.debug("OpenGL ES 1.x not supported");
        }
        if (activeCaps.getGLProfile().isGLES2()) {
            LOGGER.debug("OpenGL ES 2.x supported");
        } else {
            LOGGER.debug("OpenGL ES 2.x not supported");
        }
        if (activeCaps.getGLProfile().isGL2()) {
            LOGGER.debug("OpenGL 1.x, 2.x, 3.0 supported");
        } else {
            LOGGER.debug("OpenGL 1.x, 2.x, 3.0 not supported");
        }
        if (activeCaps.getGLProfile().isGL3()) {
            LOGGER.debug("OpenGL 3.x supported");
        } else {
            LOGGER.debug("OpenGL 3.x not supported");
        }
        if (activeCaps.getGLProfile().isGL4()) {
            LOGGER.debug("OpenGL 4.x supported");
        } else {
            LOGGER.debug("OpenGL 4.x not supported");
        }
        if (activeCaps.getGLProfile().isGL2GL3()) {
            LOGGER.debug("OpenGL 2.x, 3.x supported");
        } else {
            LOGGER.debug("OpenGL 2.x, 3.x not supported");
        }
        if (activeCaps.getGLProfile().isGL2ES1()) {
            LOGGER.debug("OpenGL 1.x, 2.x, 3.0 and OpenGL ES 1.x supported");
        } else {
            LOGGER
                .debug("OpenGL 1.x, 2.x, 3.0 and OpenGL ES 1.x not supported");
        }
        if (activeCaps.getGLProfile().isGL2ES2()) {
            LOGGER.debug("OpenGL 1.x, 2.x, 3.0 and OpenGL ES 2.x supported");
        } else {
            LOGGER
                .debug("OpenGL 1.x, 2.x, 3.0 and OpenGL ES 2.x not supported");
        }
        if (activeCaps.getGLProfile().hasGLSL()) {
            LOGGER.debug("OpenGL shader language supported");
        } else {
            LOGGER.debug("OpenGL shader language not supported");
        }
        if (activeCaps.getGLProfile().usesNativeGLES()) {
            LOGGER.debug("OpenGL ES native supported");
        } else {
            LOGGER.debug("OpenGL ES native not supported");
        }
        if (activeCaps.getGLProfile().usesNativeGLES1()) {
            LOGGER.debug("OpenGL ES 1.x native supported");
        } else {
            LOGGER.debug("OpenGL ES 1.x native not supported");
        }
        if (activeCaps.getGLProfile().usesNativeGLES2()) {
            LOGGER.debug("OpenGL ES 2.x native supported");
        } else {
            LOGGER.debug("OpenGL ES 2.x native not supported");
        }
    }

    /**
     * Setup the viewport correctly in case it was changed.
     * 
     * @param drawable the GLAutoDrawable object to access openGL
     */
    @SuppressWarnings("nls")
    private void setupViewport(final GLAutoDrawable drawable) {

        if (drawable.getContext() == null) {
            return;
        }

        boolean releaseContext = false;
        if (GLContext.getCurrent() == null) {
            drawable.getContext().makeCurrent();
            releaseContext = true;
        }
        GL2ES1 gl = null;

        if (drawable.getGLProfile().hasGLSL()) {
            gl = FixedFuncUtil.getFixedFuncImpl(drawable.getGL());
        } else if (drawable.getGLProfile().isGL2ES1()) {
            gl = drawable.getGL().getGL2ES1();
        } else {
            if (releaseContext) {
                drawable.getContext().release();
            }
            throw new GraphicsJOGLException(
                "Invalid GL profile. Failed to setup Viewport.",
                drawable.getGLProfile());
        }

        gl.glViewport(0, 0, drawable.getWidth(), drawable.getHeight());
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.createGLU().gluOrtho2D(0, drawable.getWidth(), 0,
            drawable.getHeight());
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();

        if (releaseContext) {
            drawable.getContext().release();
        }
    }
}

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

import java.awt.Component;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLCapabilitiesImmutable;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import org.apache.log4j.Logger;

import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.glsl.fixedfunc.FixedFuncUtil;

import illarion.graphics.Graphics;
import illarion.graphics.RenderTask;
import illarion.graphics.generic.AbstractTextureAtlas;

/**
 * This class defines the display canvas that is used to render the graphics in
 * the game. The render area bases on a AWT Canvas that is best used in a pure
 * AWT frame.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class DisplayAWT extends GLCanvas implements Display,
    GLEventListener {
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(DisplayAWT.class);

    /**
     * The serialization UID of this canvas.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The animator that takes care for the display speed of this canvas.
     */
    private AnimatorBase animator;

    /**
     * Constant to store of this object is double buffered or not
     */
    private boolean doubleBuffered;

    /**
     * The JOGL render manager that supplies the data to display.
     */
    private final RenderManagerJOGL manager;

    /**
     * Constructor that allows setting the capabilities of this display.
     * 
     * @param capabilities capabilities of the canvas
     */
    public DisplayAWT(final GLCapabilities capabilities) {
        super(capabilities);
        manager =
            (RenderManagerJOGL) Graphics.getInstance().getRenderManager();
        addGLEventListener(this);
        setAutoSwapBufferMode(true);

        doubleBuffered = false;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    /**
     * Constructor that allows setting the capabilities of this display as well
     * as the size.
     * 
     * @param width width of the canvas
     * @param height height of the canvas
     * @param capabilities capabilities of the canvas
     */
    public DisplayAWT(final int width, final int height,
        final GLCapabilities capabilities) {
        this(capabilities);
        setSize(width, height);
    }

    @Override
    public void addInputListener(final Object listener) {
        if (listener instanceof java.awt.event.MouseListener) {
            addMouseListener((java.awt.event.MouseListener) listener);
        }
        if (listener instanceof java.awt.event.MouseWheelListener) {
            addMouseWheelListener((java.awt.event.MouseWheelListener) listener);
        }
        if (listener instanceof java.awt.event.MouseMotionListener) {
            addMouseMotionListener((java.awt.event.MouseMotionListener) listener);
        }
        if (listener instanceof java.awt.event.KeyListener) {
            addKeyListener((java.awt.event.KeyListener) listener);
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

    @Override
    public GLAutoDrawable getDrawable() {
        return this;
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

    /**
     * Overwritten function to properly return if the display is double buffered
     * or not.
     * 
     * @return <code>true</code> in case the display is double buffered
     */
    @Override
    public boolean isDoubleBuffered() {
        return doubleBuffered;
    }

    @Override
    public boolean isInputListenerSupported(final Object listener) {
        if (listener instanceof java.awt.event.MouseListener) {
            return true;
        }
        if (listener instanceof java.awt.event.MouseWheelListener) {
            return true;
        }
        if (listener instanceof java.awt.event.MouseMotionListener) {
            return true;
        }
        if (listener instanceof java.awt.event.KeyListener) {
            return true;
        }

        return false;
    }

    @Override
    public void paint(final java.awt.Graphics g) {
        if (animator != null) {
            super.paint(g);
        } else {
            display();
        }
    }

    @Override
    public void removeInputListener(final Object listener) {
        if (listener instanceof java.awt.event.MouseListener) {
            removeMouseListener((java.awt.event.MouseListener) listener);
        }
        if (listener instanceof java.awt.event.MouseWheelListener) {
            removeMouseWheelListener((java.awt.event.MouseWheelListener) listener);
        }
        if (listener instanceof java.awt.event.MouseMotionListener) {
            removeMouseMotionListener((java.awt.event.MouseMotionListener) listener);
        }
        if (listener instanceof java.awt.event.KeyListener) {
            removeKeyListener((java.awt.event.KeyListener) listener);
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

        final Component parent = getParent();
        if (parent != null) {
            parent.validate();
        }
    }

    /**
     * Set the frames per second the display should be shown with.
     * 
     * @param fps the target frames per second
     */
    @Override
    public void setFPS(final int fps) {
        manager.renderStarted();
        if (animator != null) {
            animator.stop();
            animator.remove(this);
            animator = null;
        }
        if (fps > -1) {
            setIgnoreRepaint(true);
            // animator = new Animator(this);
            animator = new FPSAnimator(this, fps, true);
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
        final GLContext context = getContext();
        if (context == null) {
            return;
        }

        boolean releaseContext = false;
        if (!context.isCurrent()) {
            context.makeCurrent();
            releaseContext = true;
        }
        paint(g);
        if (releaseContext) {
            context.release();
        }
    }

    /**
     * Display all informations about the currently activated OpenGL mode.
     */
    @SuppressWarnings("nls")
    private void displayOpenGLStatusInfo() {
        final GLCapabilitiesImmutable activeCaps = getChosenGLCapabilities();
        doubleBuffered = activeCaps.getDoubleBuffered();

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

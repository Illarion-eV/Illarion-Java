package org.illarion.nifty.slick;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.illarion.nifty.renderer.render.RenderDeviceIllarion;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.SlickCallable;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyInputConsumer;
import de.lessvoid.nifty.input.keyboard.KeyboardInputEvent;
import de.lessvoid.nifty.lwjglslick.input.LwjglKeyboardInputEventCreator;
import de.lessvoid.nifty.lwjglslick.sound.SlickSoundDevice;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.slick.MouseEvent;
import de.lessvoid.nifty.spi.input.InputSystem;
import de.lessvoid.nifty.spi.render.RenderDevice;
import de.lessvoid.nifty.spi.sound.SoundDevice;
import de.lessvoid.nifty.tools.TimeProvider;

/**
 * A Slick Nifty GameState.
 * 
 * @author void
 * @author Martin Karing
 */
public class NiftyGameState extends BasicGameState {

    /**
     * nifty instance to use.
     */
    protected Nifty nifty;

    /**
     * the slick game state id.
     */
    protected int id;

    /**
     * mouse x.
     */
    protected int mouseX;

    /**
     * mouse y.
     */
    protected int mouseY;

    /**
     * mouse offset x.
     */
    protected int cursorOffsetX;

    /**
     * mouse offset y.
     */
    protected int cursorOffsetY;

    /**
     * mouse down.
     */
    protected boolean mouseDown;
    protected int mouseButton;

    private List<MouseEvent> mouseEvents = new ArrayList<MouseEvent>();
    private List<KeyboardInputEvent> keyEvents =
        new ArrayList<KeyboardInputEvent>();

    private GameContainer container;
    private LwjglKeyboardInputEventCreator inputEventCreator =
        new LwjglKeyboardInputEventCreator();
    
    private RenderDeviceIllarion renderDevice;

    /**
     * create the nifty game state.
     * 
     * @param slickGameStateId the slick gamestate id for this state
     */
    public NiftyGameState(final int slickGameStateId) {
        this(slickGameStateId, new RenderDeviceIllarion(), new SlickSoundDevice());
    }
    
    /**
     * create the nifty game state.
     * 
     * @param slickGameStateId the slick gamestate id for this state
     * @param rDevice the render device to use
     * @param sDevice the sound device to use
     */
    public NiftyGameState(final int slickGameStateId, final RenderDevice rDevice, final SoundDevice sDevice) {
        this.id = slickGameStateId;
        
        if (rDevice instanceof RenderDeviceIllarion) {
            renderDevice = (RenderDeviceIllarion) rDevice;
        }

        SlickCallable.enterSafeBlock();
        this.nifty =
            new Nifty(rDevice, sDevice,
                new InputSystem() {
                    public void forwardEvents(
                        final NiftyInputConsumer inputEventConsumer) {
                        for (MouseEvent event : mouseEvents) {
                            inputEventConsumer.processMouseEvent(event.mouseX,
                                event.mouseY, event.mouseWheel, event.button,
                                event.buttonDown);
                        }
                        mouseEvents.clear();

                        for (KeyboardInputEvent event : keyEvents) {
                            inputEventConsumer.processKeyboardEvent(event);
                        }
                        keyEvents.clear();
                    }

                    @Override
                    public void setMousePosition(final int x, final int y) {
                    }
                }, new TimeProvider());
        SlickCallable.leaveSafeBlock();
    }

    /**
     * load xml.
     * 
     * @param filename file to load
     * @param controllers controllers to use
     */
    public void fromXml(final String filename,
        final ScreenController... controllers) {
        SlickCallable.enterSafeBlock();
        nifty.registerScreenController(controllers);
        nifty.fromXmlWithoutStartScreen(filename);
        SlickCallable.leaveSafeBlock();
    }

    /**
     * load xml.
     * 
     * @param filename file to load
     * @param controllers controllers to use
     */
    public void fromXml(final String fileId, final InputStream xmlData,
        final ScreenController... controllers) {
        SlickCallable.enterSafeBlock();
        nifty.registerScreenController(controllers);
        nifty.fromXmlWithoutStartScreen(fileId, xmlData);
        SlickCallable.leaveSafeBlock();
    }

    /**
     * get slick game state id.
     * 
     * @return slick game state id
     */
    public int getID() {
        return id;
    }

    /**
     * initialize.
     * 
     * @param container GameContainer
     * @param game StateBasedGame
     * @throws SlickException exception
     */
    public void init(final GameContainer container, final StateBasedGame game)
        throws SlickException {
        this.container = container;
    }

    /**
     * render.
     * 
     * @param container GameContainer
     * @param game StateBasedGame
     * @param g Graphics
     * @throws SlickException exception
     */
    public void render(final GameContainer container,
        final StateBasedGame game, final Graphics g) throws SlickException {
        if (renderDevice != null) {
            renderDevice.updateGraphicInstance(g);
        }
        SlickCallable.enterSafeBlock();
        nifty.render(false);
        SlickCallable.leaveSafeBlock();
    }

    /**
     * update.
     * 
     * @param container GameContainer
     * @param game StateBasedGame
     * @param d delta thing
     * @throws SlickException exception
     */
    public void update(final GameContainer container,
        final StateBasedGame game, final int d) throws SlickException {
        nifty.update();
    }

    /**
     * @see org.newdawn.slick.InputListener#keyPressed(int, char)
     */
    public void keyPressed(final int key, final char c) {
        keyEvents.add(inputEventCreator.createEvent(key, c, true));
    }

    /**
     * @see org.newdawn.slick.InputListener#keyReleased(int, char)
     */
    public void keyReleased(final int key, final char c) {
        keyEvents.add(inputEventCreator.createEvent(key, c, false));
    }

    /**
     * @see org.newdawn.slick.InputListener#mouseMoved(int, int, int, int)
     */
    public void mouseMoved(final int oldx, final int oldy, final int newx,
        final int newy) {
        mouseX = newx;
        mouseY = newy;
        forwardMouseEventToNifty(mouseX, mouseY, mouseDown, mouseButton);
    }

    /**
     * @see org.newdawn.slick.InputListener#mousePressed(int, int, int)
     */
    public void mousePressed(final int button, final int x, final int y) {
        mouseX = x;
        mouseY = y;
        mouseDown = true;
        mouseButton = button;
        forwardMouseEventToNifty(mouseX, mouseY, mouseDown, mouseButton);
    }

    /**
     * @see org.newdawn.slick.InputListener#mouseReleased(int, int, int)
     */
    public void mouseReleased(final int button, final int x, final int y) {
        mouseX = x;
        mouseY = y;
        mouseDown = false;
        mouseButton = button;
        forwardMouseEventToNifty(mouseX, mouseY, mouseDown, mouseButton);
    }

    /**
     * enter state.
     * 
     * @param container container
     * @param game game
     */
    public void enter(final GameContainer container, final StateBasedGame game)
        throws SlickException {
        SlickCallable.enterSafeBlock();

        mouseDown = false;
        SlickCallable.leaveSafeBlock();
    }

    /**
     * Activate the given ScreenId.
     * 
     * @param screenId
     */
    public void gotoScreen(final String screenId) {
        nifty.gotoScreen(screenId);
    }

    /**
     * @param mouseX
     * @param mouseY
     * @param mouseDown
     * @param mouseButton
     */
    private void forwardMouseEventToNifty(final int mouseX, final int mouseY,
        final boolean mouseDown, final int mouseButton) {
        mouseEvents
            .add(new MouseEvent(mouseX, mouseY, mouseDown, mouseButton));
    }

    public Nifty getNifty() {
        return nifty;
    }
}

package org.illarion.nifty.slick;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyInputConsumer;
import de.lessvoid.nifty.input.keyboard.KeyboardInputEvent;
import de.lessvoid.nifty.lwjglslick.input.LwjglKeyboardInputEventCreator;
import de.lessvoid.nifty.lwjglslick.sound.SlickSoundDevice;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.slick.MouseEvent;
import de.lessvoid.nifty.spi.input.InputSystem;
import de.lessvoid.nifty.spi.render.RenderDevice;
import de.lessvoid.nifty.spi.sound.SoundDevice;
import de.lessvoid.nifty.tools.TimeProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.illarion.nifty.renderer.render.RenderDeviceIllarion;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.SlickCallable;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * Abstract Slick GameState that renders a Nifty UI overlay over it.
 * Requires both Slick and Nifty packages.
 * Extend from this GameState to implement your own.
 * 
 * To use nifty, you have to first call initNifty().
 * You can then access getNifty() to build your screens or load an file
 * using the loadXml() methods.
 * Use gotoScreenIfNotActive() to switch between screens.
 * 
 * Dont forget to call super.render(), super.update(), super.enter()
 * and super.leave() if you override those methods.
 * 
 * Input Event handling:
 * As Nifty may catch keyboard or mouse events it forwards any leftover events
 * in the following methods. Override and use these in stead of the Slick Input 
 * events. You wont receive mouseDragged and mouseClicked events, but you can
 * override those methods.
 * - abstract processMouseEvent()
 * - abstract processKeyboardEvent()
 * 
 * Keyboard Input can be stolen with the stealKeyInput() and restoreKeyInput()
 * methods. All events will be sent to the game state as if the overlay was hidden.
 * 
 * Overlays can be hidden and, in fact, made inactive by using
 * {@code .showOverlay(false); }
 * in which case all Input events fall straight through to the game via the
 * aforementioned input event handlers. 
 * 
 * Note that since all the GameStates are started at the same time, all their screens 
 * are too, so only if you're quick to switch will you see screen2's start animation
 * finish.
 * 
 * @author Durandal
 * @see mail to: nifty(at)durandal.nl
 * @author charly
 * @see mail to charlyghislain@gmail.com
 */
public abstract class NiftyOverlayGameState extends BasicGameState implements NiftyInputConsumer {
   //private to force the usage of getNifty() which will throw an exception if not initialized
	private Nifty nifty;
   // Remember last mouse event for each button (which are id'ed 0 1 2, a map should be used otherwise)
   private MouseEvent[] lastMouseEvents;
   private int lastMouseEventButton;
   // remembers the last activated screen
   private String currentScreen;
	// shows or hides the Nifty overlay and routes messages the right way
	private boolean showOverlay = true;
   private boolean inputStolen = false;
   private String startScreen = "start";
   private ScreenController[] controllers;
   // Portected fields because subclasses might want to override
   protected OverlayInputSystem overlayInputSystem;
   protected boolean acceptingInput = false;

	/**
    * Create a new GameState. isAcceptingInput will return true if the
    * state if active.
    * To use nifty overlay, you have to call initNifty().
    * Note that you will not receive mouseDragged and MouseClocked event,
    * they will have to be extracted from the mouse pressed/released/moved events
    * Your wheel event will be a copy of the last event with the correct mousWheel value.
    * Check that value first, !=0 means its a wheel event. If you don't, you might
    * process the same event twice.
	 */
   public NiftyOverlayGameState() {
      this.overlayInputSystem = new OverlayInputSystem(this);
      controllers = new ScreenController[]{new BasicScreenController()};
      lastMouseEvents = new MouseEvent[]{
         new MouseEvent(-1, -1, false, Input.MOUSE_LEFT_BUTTON),
         new MouseEvent(-1, -1, false, Input.MOUSE_MIDDLE_BUTTON),
         new MouseEvent(-1, -1, false, Input.MOUSE_RIGHT_BUTTON)
      };
	}

	/**
    * Initialize Nifty. 
	 */
   protected void initNifty() {
      initNifty(new RenderDeviceIllarion(), new SlickSoundDevice());
   }

   /**
    * Initialize Nifty. 
    */
   protected void initNifty(RenderDevice renderDevice, SoundDevice soundDevice) {
		// activate Nifty
		nifty = new Nifty(renderDevice, soundDevice, overlayInputSystem, new TimeProvider());
		}

	/**
    * Loads UI data from an XML file
    * @param xmlFile
    * @param controllers 
	 */
   public void loadXml(final String xmlFile, final ScreenController... controllers) {
		this.controllers = controllers;
      loadXML(xmlFile);
	}

	/**
    * Load the UI data from an XML file
    * @param xmlFile
    * @param startScreen
    * @param controllers 
	 */
   public void loadXml(final String xmlFile, final String startScreen) {
		this.startScreen = startScreen;
      loadXML(xmlFile);
	}

	/**
    * Load the UI data from an XML file
    * @param xmlFile
    * @param startScreen
    * @param controllers 
	 */
   public void loadXml(final String xmlFile, final String startScreen, final ScreenController... controllers) {
      this.startScreen = startScreen;
		this.controllers = controllers;
      loadXML(xmlFile);
	}

   private void loadXML(String file) throws RuntimeException {
      // load the UI script
      getNifty().fromXml(file, startScreen, controllers);
      currentScreen = startScreen;
   }

	/**
    * Please call super.enter when overriding
    * @param container
    * @param game
    * @throws SlickException 
	 */
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		// reactivate the screen but do not restart it if it is already been loaded 
		super.enter(container, game);
      gotoScreenIfNotActive(currentScreen);
      acceptingInput = true;
	}

   @Override
   public void leave(GameContainer container, StateBasedGame game) throws SlickException {
      acceptingInput = false;
      // we have to release buttons before leaving or nifty will think they are still
      // down if we go to a non-nifty state
      for (MouseEvent event : lastMouseEvents) {
         if (event.buttonDown) {
            int x = event.mouseX;
            int y = event.mouseY;
            int button = event.button;
            if (showOverlay) {
               overlayInputSystem.addMouseEvent(x, correctYAxis(y), 0, button, false);
            } else {
               overlayInputSystem.addUserMouseEvent(x, correctYAxis(y), 0, button, false);
            }
         }
      }
      super.leave(container, game);
   }

   @Override
   public boolean isAcceptingInput() {
      return acceptingInput;
   }

	/**
	 * Goes to and (re)starts a Nifty screen if that screen is not already active.
	 * If the the screen is already active nothing is done. It is not restarted
	 * and keeps it current state.
	 * @param screenID Nifty xml screen ID
	 */
   public void gotoScreenIfNotActive(final String screenID) {
      if (nifty == null) {
         return;
      }
      if (nifty.getCurrentScreen().getScreenId() == null ? screenID != null : !nifty.getCurrentScreen().getScreenId().equals(screenID)) {
			currentScreen = screenID;
			nifty.gotoScreen(screenID);
		}
	}
	
	 @Override
   public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
      if (nifty != null) {
	    nifty.update();
	  }
   }

	// invokes the rendering of the Nifty overlay
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
      if (showOverlay && nifty != null) {
			SlickCallable.enterSafeBlock();
			nifty.render(false);
			SlickCallable.leaveSafeBlock();
		}
	}

	@Override
   public final void keyReleased(final int key, final char c) {
      if (showOverlay && !inputStolen) {
			overlayInputSystem.addKeyEvent(key, c, false);
		} else {
			overlayInputSystem.addUserKeyEvent(key, c, false);
		}
	}

	@Override
   public final void keyPressed(final int key, final char c) {
      if (showOverlay && !inputStolen) {
			overlayInputSystem.addKeyEvent(key, c, true);
		} else {
			overlayInputSystem.addUserKeyEvent(key, c, true);
		}
	}

	@Override
   public final void mousePressed(final int button, final int x, final int y) {
      if (showOverlay) {
         overlayInputSystem.addMouseEvent(x, correctYAxis(y), 0, button, true);
		} else {
         overlayInputSystem.addUserMouseEvent(x, correctYAxis(y), 0, button, true);
		}
	}

	@Override
   public final void mouseReleased(final int button, final int x, final int y) {
      if (showOverlay) {
         overlayInputSystem.addMouseEvent(x, correctYAxis(y), 0, button, false);
		} else {
         overlayInputSystem.addUserMouseEvent(x, correctYAxis(y), 0, button, false);
		}
	}

	@Override
   public final void mouseWheelMoved(int newValue) {
      MouseEvent lastEvent = lastMouseEvents[lastMouseEventButton];
      if (showOverlay) {
         overlayInputSystem.addMouseEvent(lastEvent.mouseX, lastEvent.mouseY, newValue, lastEvent.button, lastEvent.buttonDown);
		} else {
         overlayInputSystem.addUserMouseEvent(lastEvent.mouseX, lastEvent.mouseY, newValue, lastEvent.button, lastEvent.buttonDown);
		}
	}

   @Override
   public final void mouseMoved(final int oldx, final int oldy, final int newx, final int newy) {
      boolean buttonDown = lastMouseEvents[lastMouseEventButton].buttonDown;
      if (showOverlay) {
         overlayInputSystem.addMouseEvent(newx, correctYAxis(newy), 0, lastMouseEventButton, buttonDown);
      } else {
         overlayInputSystem.addUserMouseEvent(newx, correctYAxis(newy), 0, lastMouseEventButton, buttonDown);
      }
   }

   @Override
   public void mouseClicked(int button, int x, int y, int clickCount) {
   }

   @Override
   public void mouseDragged(int oldx, int oldy, int newx, int newy) {
      mouseMoved(oldx, oldy, newx, newy);
   }

	/**
    * Corrects for inverted Y axis on Nifty. Override if neeeded
	 * 
	 * @param mouseY
	 * @return
	 */
   protected int correctYAxis(final int mouseY) {
      return mouseY;
	}

	/**
	 * Determines if the overlay is shown or hidden
	 * @return true if the overlay is visible
	 */
	public boolean isOverlayShown() {
		return showOverlay;
	}

	/**
	 * Hides or shows the overlay.
	 * @param showOverlay true if the overlay is visible
	 */
	public void showOverlay(boolean showOverlay) {
		// turn off passing events to Nifty if it's not being rendered
		this.showOverlay = showOverlay;
	}

	/**
    * stals keyboard input, all subsequent KeyInput event will be sent
    * to the game state
	 */
   public void stealKeyInput() {
      inputStolen = true;
	}

	/**
    * Restore keyboard input events. Subsequent event will be sent to nifty
    * and then, if not consumed, to the game state
	 */
   public void restoreKeyInput() {
      inputStolen = false;
	}

	/**
    * Nifty instance. Valid after the game state has become active, else null.
    * 
    * @return Nifty the Nifty instance
	 */
   public Nifty getNifty() {
      if (nifty == null) {
         throw new RuntimeException("Please load nifty first!");
	}
      return nifty;
	}

	/**
	 * Handles input events (probably from Slick) and forwards them to Nifty
	 * upon request.
	 * 
	 * Any unused events will be passed to the user event consumer.
	 * 
	 * Note that Nifty runs in the render() method, so any handling of events by
	 * the event consumer must be quick. Buffer this with a ConcurrentQueue to
	 * convert to a polling mechanism and consume user events in a separate
	 * thread as not to influence rendering speed.
	 * 
	 * @author Durandal
	 * @see mail to: nifty(at)durandal.nl
	 * 
	 */
   public class OverlayInputSystem implements InputSystem {
      private final LwjglKeyboardInputEventCreator inputEventCreator;
      private final List<MouseEvent> mouseEvents;
      private final List<KeyboardInputEvent> keyEvents;
      private final NiftyInputConsumer userConsumer; // unused events go here

		public OverlayInputSystem(NiftyInputConsumer userConsumer) {
			this.userConsumer = userConsumer;
         inputEventCreator = new LwjglKeyboardInputEventCreator();
         mouseEvents = Collections.synchronizedList(new ArrayList<MouseEvent>());
         keyEvents = Collections.synchronizedList(new ArrayList<KeyboardInputEvent>());
		}

		@Override
		public void forwardEvents(NiftyInputConsumer inputEventConsumer) {
         synchronized (mouseEvents) {
			for (MouseEvent event : mouseEvents) {
				if (!inputEventConsumer.processMouseEvent(event.mouseX, event.mouseY, event.mouseWheel, event.button, event.buttonDown)) {
					// Event not used by Nifty, so send to the given user event consumer
					userConsumer.processMouseEvent(event.mouseX, event.mouseY, event.mouseWheel, event.button, event.buttonDown);
					// TODO because nifty runs in the render() method, I am concerned that the
					// user consumer may eat too much time. Perhaps a polling mechanism with
					// a ConcurrentQueue would be preferable.
				}
			}
            mouseEvents.clear();
         }
         synchronized (keyEvents) {
			for (KeyboardInputEvent event : keyEvents) {
				if (!inputEventConsumer.processKeyboardEvent(event)) {
					// Event not used by Nifty, so send to the given user event consumer
					userConsumer.processKeyboardEvent(event);
				}
			}
            keyEvents.clear();
		}
      }

		public void addKeyEvent(final int key, final char c, final boolean keyDown) {
         synchronized (keyEvents) {
            keyEvents.add(inputEventCreator.createEvent(key, c, keyDown));
		}

		}

		// Adds an Event directly to the user, bypassing the Nifty queue. 
		// May be required if Nifty is not being rendered since it will then also not read its queue
		public void addUserKeyEvent(final int key, final char c, final boolean keyDown) {
			userConsumer.processKeyboardEvent(inputEventCreator.createEvent(key, c, keyDown));
		}

      @Override
      public void setMousePosition(int x, int y) {
		}

      public void addMouseEvent(final int x, final int y, final int mouseWheel, final int button, final boolean buttonDown) {
         MouseEvent event = new MouseEvent(x, y, buttonDown, button);
         event.mouseWheel = mouseWheel;
         synchronized (mouseEvents) {
            mouseEvents.add(event);
         }
         lastMouseEventButton = button;
         lastMouseEvents[lastMouseEventButton] = event;
      }

      public void addUserMouseEvent(final int x, final int y, final int mouseWheel, final int button, final boolean buttonDown) {
         userConsumer.processMouseEvent(x, y, mouseWheel, button, buttonDown);
         lastMouseEventButton = button;
         MouseEvent event = new MouseEvent(x, y, buttonDown, button);
         event.mouseWheel = mouseWheel;
         lastMouseEvents[lastMouseEventButton] = event;
      }
   }

   private class BasicScreenController implements ScreenController {
    @Override
      public void bind(Nifty nifty, Screen screen) {
         Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
                                                         "Default empty ScreenController attached to Nifty screen " + screen + ". "
                 + "This is probably not what you had in mind.");
    }

      @Override
      public void onEndScreen() {
	}

      @Override
      public void onStartScreen() {
}
   }
}
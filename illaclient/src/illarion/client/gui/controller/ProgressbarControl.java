package illarion.client.gui.controller;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Controller;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.xml.xpp3.Attributes;

import java.util.Properties;

public class ProgressbarControl implements Controller {
	private Element progressBarElement;
	
	public void bind(
	    final Nifty nifty,
	    final Screen screenParam,
	    final Element element,
	    final Properties parameter,
	    final Attributes controlDefinitionAttributes) {
		progressBarElement = element.findElementByName("progress");
	}
	
	@Override
	public void init(final Properties parameter, final Attributes controlDefinitionAttributes) {
	}

	public void onStartScreen() {
	}

	public void onFocus(final boolean getFocus) {
	}

	public boolean inputEvent(final NiftyInputEvent inputEvent) {
	    return false;
	}
	
	public void setProgress(final float progressValue) {
		float progress = progressValue;
		if (progress < 0.0f) {
			progress = 0.0f;
		} else if (progress > 1.0f) {
		    progress = 1.0f;
		}

		final int MIN_WIDTH = 28; 
		int pixelWidth = (int) (Math.max(MIN_WIDTH, progressBarElement.getParent().getWidth()) * progress);
		progressBarElement.setConstraintWidth(new SizeValue(pixelWidth + "px"));
		progressBarElement.getParent().layoutElements();
	}
}

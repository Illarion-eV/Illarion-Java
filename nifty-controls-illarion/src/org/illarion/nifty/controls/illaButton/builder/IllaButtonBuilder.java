package org.illarion.nifty.controls.illaButton.builder;

import de.lessvoid.nifty.builder.ControlBuilder;

import de.lessvoid.nifty.builder.ControlBuilder;

public class IllaButtonBuilder extends ControlBuilder  {
	 public IllaButtonBuilder(final String id) {
		    super(id, "illaButton");
		  }

		  public IllaButtonBuilder(final String id, final String buttonLabel) {
		    super(id, "illaButton");
		    label(buttonLabel);
		  }

		  public void label(final String label) {
		    set("label", label);
		  }
}

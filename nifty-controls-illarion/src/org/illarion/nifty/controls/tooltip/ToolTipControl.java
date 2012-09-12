/*
 * This file is part of the Illarion Nifty-GUI Controls.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Nifty-GUI Controls is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Nifty-GUI Controls is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Nifty-GUI Controls.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.nifty.controls.tooltip;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.AbstractController;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.xml.xpp3.Attributes;
import illarion.common.util.Money;
import org.illarion.nifty.controls.ToolTip;

import java.util.Properties;

/**
 * This is the control that takes care to display the Illarion item tooltip.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @deprecated Use {@link ToolTip}
 */
@Deprecated
public final class ToolTipControl extends AbstractController implements ToolTip {
    @Override
    public void bind(final Nifty nifty, final Screen screen, final Element element,
                     final Properties parameter, final Attributes controlDefinitionAttributes) {
        bind(element);

        final Label title = element.findNiftyControl("#title", Label.class);
        title.getElement().getRenderer(TextRenderer.class).setLineWrapping(true);
        title.setText(controlDefinitionAttributes.get("title"));
        title.setColor(controlDefinitionAttributes.getAsColor("titleColor"));

        if (controlDefinitionAttributes.isSet("producer")) {
            final Label producedBy = element.findNiftyControl("#createdByLabel", Label.class);
            producedBy.setText(controlDefinitionAttributes.get("producer"));
        } else {
            element.findElementByName("#createByLine").markForRemoval();
        }

        final String money = controlDefinitionAttributes.get("worth");

        if (isAllNull(money)) {
            element.findElementByName("#worthLine").markForRemoval();
        } else {
            final Money moneyValue = new Money(Integer.parseInt(money));
            applyMoney(element, moneyValue.getGold(), "#worthGoldCount", "#worthGoldImage");
            applyMoney(element, moneyValue.getSilver(), "#worthSilverCount", "#worthSilverImage");
            applyMoney(element, moneyValue.getCopper(), "#worthCopperCount", "#worthCopperImage");
        }

        if (controlDefinitionAttributes.isSet("quality")) {
            final Label producedBy = element.findNiftyControl("#qualityText", Label.class);
            producedBy.setText(controlDefinitionAttributes.get("quality"));
        } else {
            element.findElementByName("#qualityLine").markForRemoval();
        }

        if (controlDefinitionAttributes.isSet("durability")) {
            final Label producedBy = element.findNiftyControl("#durabilityText", Label.class);
            producedBy.setText(controlDefinitionAttributes.get("durability"));
        } else {
            element.findElementByName("#durabilityLine").markForRemoval();
        }

        final String diamond = controlDefinitionAttributes.get("diamondLevel");
        final String emerald = controlDefinitionAttributes.get("emeraldLevel");
        final String ruby = controlDefinitionAttributes.get("rubyLevel");
        final String blackStone = controlDefinitionAttributes.get("blackStoneLevel");
        final String blueStone = controlDefinitionAttributes.get("blueStoneLevel");
        final String amethyst = controlDefinitionAttributes.get("amethystLevel");
        final String topaz = controlDefinitionAttributes.get("topazLevel");

        if (isAllNull(diamond, emerald, ruby, blackStone, blueStone, amethyst, topaz)) {
            element.findElementByName("#gemsLine").markForRemoval();
        } else {
            applyGem(nifty, element, diamond, "#diamondImage", "diamond");
            applyGem(nifty, element, emerald, "#emeraldImage", "emerald");
            applyGem(nifty, element, ruby, "#rubyImage", "ruby");
            applyGem(nifty, element, blueStone, "#blueStoneImage", "bluestone");
            applyGem(nifty, element, blackStone, "#blackStoneImage", "blackstone");
            applyGem(nifty, element, amethyst, "#amethystImage", "amethyst");
            applyGem(nifty, element, topaz, "#topazImage", "topaz");
        }

        element.layoutElements();
    }

    private static boolean isAllNull(final String... strings) {
        for (final String value : strings) {
            if ((value != null) && (Integer.parseInt(value) > 0)) {
                return false;
            }
        }
        return true;
    }

    private static void applyGem(final Nifty nifty, final Element element, final String gemText,
                                 final String elementImage, final String images) {
        final Element image = element.findElementByName(elementImage);
        if (gemText == null) {
            image.markForRemoval();
            return;
        }
        final int gemLevel = Integer.parseInt(gemText);
        if (gemLevel == 0) {
            image.markForRemoval();
            return;
        }

        image.getRenderer(ImageRenderer.class).setImage(nifty.createImage(
                "data/items/" + images + '-' + Integer.toString(gemLevel - 1) + ".png", false));
    }

    private static void applyMoney(final Element element, final int money, final String elementCount,
                                   final String elementImage) {
        if (money > 0) {
            element.findNiftyControl(elementCount, Label.class).setText(Integer.toString(money));
        } else {
            element.findElementByName(elementImage).markForRemoval();
            element.findElementByName(elementCount).markForRemoval();
        }
    }

    @Override
    public void onStartScreen() {
        // not to do
    }

    /**
     * This element never ever consumes mouse events.
     *
     * @param inputEvent the input event
     * @return {@code false} in any case
     */
    @Override
    public boolean inputEvent(final NiftyInputEvent inputEvent) {
        return false;
    }
}

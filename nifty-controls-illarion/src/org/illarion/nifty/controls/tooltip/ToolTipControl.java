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
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.SizeValue;
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

        final String description = controlDefinitionAttributes.get("description");
        if (!isNullOrEmpty(description)) {
            final Label descriptionLabel = element.findNiftyControl("#description", Label.class);
            descriptionLabel.getElement().getRenderer(TextRenderer.class).setLineWrapping(true);
            descriptionLabel.setText(description);
        } else {
            removeElement(element.findElementByName("#description"));
        }

        final String producer = controlDefinitionAttributes.get("producer");
        if (!isNullOrEmpty(producer)) {
            final Label producedBy = element.findNiftyControl("#createdByLabel", Label.class);
            applyTextToLabel(producedBy, controlDefinitionAttributes.get("producer"));
        } else {
            removeElement(element.findElementByName("#createByLine"));
        }

        final String money = controlDefinitionAttributes.get("worth");

        if (isAllNull(money)) {
            removeElement(element.findElementByName("#worthLine"));
        } else {
            final Money moneyValue = new Money(Long.parseLong(money));
            applyMoney(element, moneyValue.getGold(), "#worthGoldCount", "#worthGoldImage");
            applyMoney(element, moneyValue.getSilver(), "#worthSilverCount", "#worthSilverImage");
            applyMoney(element, moneyValue.getCopper(), "#worthCopperCount", "#worthCopperImage");
        }

        final String weight = controlDefinitionAttributes.get("weight");
        if (!isNullOrEmpty(weight)) {
            final Label qualityText = element.findNiftyControl("#weightLabel", Label.class);
            applyTextToLabel(qualityText, weight);
        } else {
            removeElement(element.findElementByName("#weightLine"));
        }

        final String quality = controlDefinitionAttributes.get("quality");
        if (!isNullOrEmpty(quality)) {
            final Label qualityText = element.findNiftyControl("#qualityText", Label.class);
            applyTextToLabel(qualityText, quality);
        } else {
            removeElement(element.findElementByName("#qualityLine"));
        }

        final String durability = controlDefinitionAttributes.get("durability");
        if (!isNullOrEmpty(durability)) {
            final Label qualityText = element.findNiftyControl("#durabilityText", Label.class);
            applyTextToLabel(qualityText, durability);
        } else {
            removeElement(element.findElementByName("#durabilityLine"));
        }

        final String diamond = controlDefinitionAttributes.get("diamondLevel");
        final String emerald = controlDefinitionAttributes.get("emeraldLevel");
        final String ruby = controlDefinitionAttributes.get("rubyLevel");
        final String obsidian = controlDefinitionAttributes.get("obsidianLevel");
        final String sapphire = controlDefinitionAttributes.get("sapphireLevel");
        final String amethyst = controlDefinitionAttributes.get("amethystLevel");
        final String topaz = controlDefinitionAttributes.get("topazLevel");

        if (isAllNull(diamond, emerald, ruby, obsidian, sapphire, amethyst, topaz)) {
            removeElement(element.findElementByName("#gemsLine"));
        } else {
            applyGem(nifty, element, diamond, "#diamondImage", "diamond");
            applyGem(nifty, element, emerald, "#emeraldImage", "emerald");
            applyGem(nifty, element, ruby, "#rubyImage", "ruby");
            applyGem(nifty, element, sapphire, "#sapphireImage", "sapphire");
            applyGem(nifty, element, obsidian, "#obsidianImage", "obsidian");
            applyGem(nifty, element, amethyst, "#amethystImage", "amethyst");
            applyGem(nifty, element, topaz, "#topazImage", "topaz");
        }

        final String gemBonus = controlDefinitionAttributes.get("gemBonus");
        if (!isNullOrEmpty(gemBonus)) {
            final Label qualityText = element.findNiftyControl("#gemBonusText", Label.class);
            applyTextToLabel(qualityText, gemBonus);
        } else {
            removeElement(element.findElementByName("#gemBonusLine"));
        }

        element.layoutElements();
    }

    private static void removeElement(final Element element) {
        element.markForRemoval();
    }

    private static boolean isNullOrEmpty(final String value) {
        return (value == null) || value.isEmpty();
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

        final NiftyImage gemImage = nifty.createImage(
                "data/items/" + images + '-' + Integer.toString(gemLevel - 1) + ".png", false);
        image.getRenderer(ImageRenderer.class).setImage(gemImage);
        image.setConstraintHeight(SizeValue.px(gemImage.getHeight()));
        image.setConstraintWidth(SizeValue.px(gemImage.getWidth()));
    }

    private static void applyMoney(final Element element, final int money, final String elementCount,
                                   final String elementImage) {
        if (money > 0) {
            applyTextToLabel(element.findNiftyControl(elementCount, Label.class), Integer.toString(money));
        } else {
            element.findElementByName(elementImage).markForRemoval();
            element.findElementByName(elementCount).markForRemoval();
        }
    }

    private static void applyTextToLabel(final Label label, final String text) {
        final Element labelElement = label.getElement();
        final TextRenderer renderer = labelElement.getRenderer(TextRenderer.class);
        renderer.setText(text);
        labelElement.setConstraintWidth(SizeValue.px(renderer.getTextWidth()));
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

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

import de.lessvoid.nifty.EndNotify;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.AbstractController;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.xml.xpp3.Attributes;
import illarion.common.types.Money;
import org.illarion.nifty.controls.ToolTip;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    public void bind(@Nonnull final Nifty nifty, final Screen screen, @Nonnull final Element element,
                     final Properties parameter, @Nonnull final Attributes controlDefinitionAttributes) {
        bind(element);

        boolean largeToolTip = false;

        //nifty.setDebugOptionPanelColors(true);

        final String description = controlDefinitionAttributes.get("description");
        if (!isNullOrEmpty(description)) {
            final Label descriptionLabel = element.findNiftyControl("#description", Label.class);
            descriptionLabel.getElement().getRenderer(TextRenderer.class).setLineWrapping(true);
            descriptionLabel.setText(description);
            largeToolTip = true;
        } else {
            removeElement(element.findElementByName("#description"));
        }

        final String type = controlDefinitionAttributes.get("itemtype");
        final String level = controlDefinitionAttributes.get("level");
        final Color levelColor = controlDefinitionAttributes.getAsColor("levelColor");
        if (!isNullOrEmpty(type)) {
            final Label typeLabel = element.findNiftyControl("#itemtype", Label.class);
            final Label levelTitle = element.findNiftyControl("#levelTitle", Label.class);
            final Label levelLabel = element.findNiftyControl("#levelLabel", Label.class);
            applyTextToLabel(typeLabel, type);
            largeToolTip = true;

            if (!isNullOrEmpty(level)) {
                levelTitle.setColor(levelColor);
                applyTextToLabel(levelLabel, " " + level);
                levelLabel.setColor(levelColor);
            } else {
                removeElement(element.findElementByName("#levelTitle"));
                removeElement(element.findElementByName("#levelLabel"));
            }

        } else {
            removeElement(element.findElementByName("#typeLevelLine"));
        }

        final String producer = controlDefinitionAttributes.get("producer");
        if (!isNullOrEmpty(producer)) {
            final Label producedBy = element.findNiftyControl("#createdByLabel", Label.class);
            applyTextToLabel(producedBy, controlDefinitionAttributes.get("producer"));
            largeToolTip = true;
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
            largeToolTip = true;
        }

        final String weight = controlDefinitionAttributes.get("weight");
        if (!isNullOrEmpty(weight)) {
            final Label qualityText = element.findNiftyControl("#weightLabel", Label.class);
            applyTextToLabel(qualityText, weight);
            largeToolTip = true;
        } else {
            removeElement(element.findElementByName("#weightLine"));
        }

        final String quality = controlDefinitionAttributes.get("quality");
        if (!isNullOrEmpty(quality)) {
            final Label qualityText = element.findNiftyControl("#qualityText", Label.class);
            applyTextToLabel(qualityText, quality);
            largeToolTip = true;
        } else {
            removeElement(element.findElementByName("#qualityLine"));
        }

        final String durability = controlDefinitionAttributes.get("durability");
        if (!isNullOrEmpty(durability)) {
            final Label qualityText = element.findNiftyControl("#durabilityText", Label.class);
            applyTextToLabel(qualityText, durability);
            largeToolTip = true;
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
            largeToolTip = true;
        }

        final String gemBonus = controlDefinitionAttributes.get("gemBonus");
        if (!isNullOrEmpty(gemBonus)) {
            final Label qualityText = element.findNiftyControl("#gemBonusText", Label.class);
            applyTextToLabel(qualityText, gemBonus);
            largeToolTip = true;
        } else {
            removeElement(element.findElementByName("#gemBonusLine"));
        }

        final Label title = element.findNiftyControl("#title", Label.class);
        final String titleText = controlDefinitionAttributes.get("title");
        final Color titleColor = controlDefinitionAttributes.getAsColor("titleColor");
        if (largeToolTip) {
            title.getElement().getRenderer(TextRenderer.class).setLineWrapping(true);
        } else {
            final TextRenderer textRenderer = title.getElement().getRenderer(TextRenderer.class);
            textRenderer.setFont(nifty.createFont("textFont"));
            final int width = textRenderer.getFont().getWidth(titleText);
            if (width >= 250) {
                textRenderer.setLineWrapping(true);
            } else {
                title.getElement().setMarginBottom(SizeValue.px(0));
                title.getElement().setConstraintWidth(SizeValue.px(width));
                title.getElement().setConstraintHeight(SizeValue.px(textRenderer.getFont().getHeight()));
            }
        }
        title.setText(titleText);
        title.setColor(titleColor);

        element.layoutElements();
    }

    /**
     * Remove a element from the tooltip.
     *
     * @param element the element to remove
     */
    private static void removeElement(@Nonnull final Element element) {
        element.markForRemoval(new EndNotify() {
            @Override
            public void perform() {
                element.getParent().layoutElements();
            }
        });
    }

    /**
     * Check if a string is {@code null} or a empty string.
     *
     * @param value the string to test
     * @return {@code true} in case the value is {@code null} or in case its a empty string
     */
    private static boolean isNullOrEmpty(@Nullable final String value) {
        return (value == null) || value.isEmpty();
    }

    /**
     * Check if all the strings are {@code null}, or represent the integer value {@code 0}.
     *
     * @param strings the stings to test
     * @return {@code true} in case all strings are {@code null} or equal to {@code Integer.toString(0)}
     */
    private static boolean isAllNull(@Nonnull final String... strings) {
        for (final String value : strings) {
            if ((value != null) && (Integer.parseInt(value) > 0)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Apply the the image of a gem to the element.
     *
     * @param nifty        the nifty instance
     * @param element      the root element where all gem images are located
     * @param gemText      the text that was stores in the attribute that contains the level of the gem
     * @param elementImage the name of the image element
     * @param images       the name of the gems
     */
    private static void applyGem(@Nonnull final Nifty nifty, @Nonnull final Element element, @Nullable final String gemText,
                                 final String elementImage, final String images) {
        final Element image = element.findElementByName(elementImage);
        if (gemText == null) {
            removeElement(image);
            return;
        }
        final int gemLevel = Integer.parseInt(gemText);
        if (gemLevel == 0) {
            removeElement(image);
            return;
        }

        final NiftyImage gemImage = nifty.createImage(
                "data/gui/items/" + images + Integer.toString(gemLevel - 1) + ".png", false);
        image.getRenderer(ImageRenderer.class).setImage(gemImage);
        image.setConstraintHeight(SizeValue.px(gemImage.getHeight()));
        image.setConstraintWidth(SizeValue.px(gemImage.getWidth()));
    }

    /**
     * Apply the money values to the element.
     *
     * @param element      the root element where the money values are stored in
     * @param money        the money component that is supposed to be displayed
     * @param elementCount the element that contains the count of the money component
     * @param elementImage the element that contains the image of the money component
     */
    private static void applyMoney(@Nonnull final Element element, final int money, final String elementCount,
                                   final String elementImage) {
        if (money > 0) {
            applyTextToLabel(element.findNiftyControl(elementCount, Label.class), Integer.toString(money));
        } else {
            removeElement(element.findElementByName(elementImage));
            removeElement(element.findElementByName(elementCount));
        }
    }

    /**
     * Apply some text to a label and resize the label to fit the text.
     *
     * @param label the label
     * @param text  the text to be stored in the label
     */
    private static void applyTextToLabel(@Nonnull final Label label, final String text) {
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

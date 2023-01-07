/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package org.illarion.nifty.controls.tooltip;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.AbstractController;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.Parameters;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.common.types.Money;
import org.illarion.nifty.controls.ToolTip;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the control that takes care to display the Illarion item tooltip.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @deprecated Use {@link ToolTip}
 */
@Deprecated
public final class ToolTipControl extends AbstractController implements ToolTip {
    @Override
    public void bind(
            @Nonnull Nifty nifty,
            @Nonnull Screen screen,
            @Nonnull Element element,
            @Nonnull Parameters parameter) {
        bind(element);

        boolean largeToolTip = false;

        //nifty.setDebugOptionPanelColors(true);

        String description = parameter.get("description");
        if (!isNullOrEmpty(description)) {
            Label descriptionLabel = element.findNiftyControl("#description", Label.class);
            descriptionLabel.getElement().getRenderer(TextRenderer.class).setLineWrapping(true);
            descriptionLabel.setText(description);
            largeToolTip = true;
        } else {
            removeElement(element.findElementById("#description"));
        }

        String type = parameter.get("itemtype");
        String level = parameter.get("level");
        Color levelColor = parameter.getAsColor("levelColor");
        if (!isNullOrEmpty(type)) {
            Label typeLabel = element.findNiftyControl("#itemtype", Label.class);
            Label levelTitle = element.findNiftyControl("#levelTitle", Label.class);
            Label levelLabel = element.findNiftyControl("#levelLabel", Label.class);
            applyTextToLabel(typeLabel, type);
            largeToolTip = true;

            if (!isNullOrEmpty(level)) {
                levelTitle.setColor(levelColor != null ? levelColor : new Color(0.93f, 0.94f, 0.95f, 1.0f));
                applyTextToLabel(levelLabel, " " + level);
                levelLabel.setColor(levelColor != null ? levelColor : new Color(0.93f, 0.94f, 0.95f, 1.0f));
            } else {
                removeElement(element.findElementById("#levelTitle"));
                removeElement(element.findElementById("#levelLabel"));
            }
        } else {
            removeElement(element.findElementById("#typeLevelLine"));
        }

        String producer = parameter.get("producer");
        if (!isNullOrEmpty(producer)) {
            Label producedBy = element.findNiftyControl("#createdByLabel", Label.class);
            applyTextToLabel(producedBy, parameter.get("producer"));
            largeToolTip = true;
        } else {
            removeElement(element.findElementById("#createByLine"));
        }

        String money = parameter.get("worth");

        if (isAllNull(money)) {
            removeElement(element.findElementById("#worthLine"));
        } else {
            Money moneyValue = new Money(Long.parseLong(money));
            applyMoney(element, moneyValue.getGold(), "#worthGoldCount", "#worthGoldImage");
            applyMoney(element, moneyValue.getSilver(), "#worthSilverCount", "#worthSilverImage");
            applyMoney(element, moneyValue.getCopper(), "#worthCopperCount", "#worthCopperImage");
            largeToolTip = true;
        }

        String weight = parameter.get("weight");
        if (!isNullOrEmpty(weight)) {
            Label qualityText = element.findNiftyControl("#weightLabel", Label.class);
            applyTextToLabel(qualityText, weight);
            largeToolTip = true;
        } else {
            removeElement(element.findElementById("#weightLine"));
        }

        String quality = parameter.get("quality");
        if (!isNullOrEmpty(quality)) {
            Label qualityText = element.findNiftyControl("#qualityText", Label.class);
            applyTextToLabel(qualityText, quality);
            largeToolTip = true;
        } else {
            removeElement(element.findElementById("#qualityLine"));
        }

        String durability = parameter.get("durability");
        if (!isNullOrEmpty(durability)) {
            Label qualityText = element.findNiftyControl("#durabilityText", Label.class);
            applyTextToLabel(qualityText, durability);
            largeToolTip = true;
        } else {
            removeElement(element.findElementById("#durabilityLine"));
        }

        String diamond = parameter.get("diamondLevel");
        String emerald = parameter.get("emeraldLevel");
        String ruby = parameter.get("rubyLevel");
        String obsidian = parameter.get("obsidianLevel");
        String sapphire = parameter.get("sapphireLevel");
        String amethyst = parameter.get("amethystLevel");
        String topaz = parameter.get("topazLevel");

        if (isAllNull(diamond, emerald, ruby, obsidian, sapphire, amethyst, topaz)) {
            removeElement(element.findElementById("#gemsLine"));
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

        String gemBonus = parameter.get("gemBonus");
        if (!isNullOrEmpty(gemBonus)) {
            Label qualityText = element.findNiftyControl("#gemBonusText", Label.class);
            applyTextToLabel(qualityText, gemBonus);
            largeToolTip = true;
        } else {
            removeElement(element.findElementById("#gemBonusLine"));
        }

        Label title = element.findNiftyControl("#title", Label.class);
        String titleText = parameter.get("title");
        Color titleColor = parameter.getAsColor("titleColor");
        if (largeToolTip) {
            title.getElement().getRenderer(TextRenderer.class).setLineWrapping(true);
        } else {
            TextRenderer textRenderer = title.getElement().getRenderer(TextRenderer.class);
            textRenderer.setFont(nifty.createFont("textFont"));
            int width = textRenderer.getFont().getWidth(titleText);
            if (width >= 250) {
                textRenderer.setLineWrapping(true);
            } else {
                title.getElement().setMarginBottom(SizeValue.px(0));
                title.getElement().setConstraintWidth(SizeValue.px(width));
                title.getElement().setConstraintHeight(SizeValue.px(textRenderer.getFont().getHeight()));
            }
        }
        title.setText(titleText);
        title.setColor(titleColor != null ? titleColor : Color.WHITE);

        element.layoutElements();
    }

    /**
     * Remove a element from the tooltip.
     *
     * @param element the element to remove
     */
    private static void removeElement(@Nonnull Element element) {
        element.markForRemoval(() -> element.getParent().layoutElements());
    }

    /**
     * Check if a string is {@code null} or a empty string.
     *
     * @param value the string to test
     * @return {@code true} in case the value is {@code null} or in case its a empty string
     */
    private static boolean isNullOrEmpty(@Nullable String value) {
        return (value == null) || value.isEmpty();
    }

    /**
     * Check if all the strings are {@code null}, or represent the integer value {@code 0}.
     *
     * @param strings the stings to test
     * @return {@code true} in case all strings are {@code null} or equal to {@code Integer.toString(0)}
     */
    private static boolean isAllNull(@Nonnull String... strings) {
        for (String value : strings) {
            if ((value != null) && (Integer.parseInt(value) > 0)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Apply the the image of a gem to the element.
     *
     * @param nifty the nifty instance
     * @param element the root element where all gem images are located
     * @param gemText the text that was stores in the attribute that contains the level of the gem
     * @param elementImage the name of the image element
     * @param images the name of the gems
     */
    private static void applyGem(
            @Nonnull Nifty nifty,
            @Nonnull Element element,
            @Nullable String gemText,
            String elementImage,
            String images) {
        Element image = element.findElementById(elementImage);
        if (gemText == null) {
            removeElement(image);
            return;
        }
        int gemLevel = Integer.parseInt(gemText);
        if (gemLevel == 0) {
            removeElement(image);
            return;
        }

        NiftyImage gemImage = nifty
                .createImage("gui/items/" + images + Integer.toString(gemLevel - 1) + ".png", false);
        image.getRenderer(ImageRenderer.class).setImage(gemImage);
        image.setConstraintHeight(SizeValue.px(gemImage.getHeight()));
        image.setConstraintWidth(SizeValue.px(gemImage.getWidth()));
    }

    /**
     * Apply the money values to the element.
     *
     * @param element the root element where the money values are stored in
     * @param money the money component that is supposed to be displayed
     * @param elementCount the element that contains the count of the money component
     * @param elementImage the element that contains the image of the money component
     */
    private static void applyMoney(
            @Nonnull Element element,
            int money,
            @Nonnull String elementCount,
            String elementImage) {
        if (money > 0) {
            applyTextToLabel(element.findNiftyControl(elementCount, Label.class), Integer.toString(money));
        } else {
            removeElement(element.findElementById(elementImage));
            removeElement(element.findElementById(elementCount));
        }
    }

    /**
     * Apply some text to a label and resize the label to fit the text.
     *
     * @param label the label
     * @param text the text to be stored in the label
     */
    private static void applyTextToLabel(@Nonnull Label label, String text) {
        Element labelElement = label.getElement();
        TextRenderer renderer = labelElement.getRenderer(TextRenderer.class);
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
    public boolean inputEvent(@Nonnull NiftyInputEvent inputEvent) {
        return false;
    }
}

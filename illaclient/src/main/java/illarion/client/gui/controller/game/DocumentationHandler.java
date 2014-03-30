/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package illarion.client.gui.controller.game;

import de.lessvoid.nifty.EndNotify;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.Window;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.client.IllaClient;
import illarion.client.docu.DocuEntry;
import illarion.client.docu.DocuRoot;
import illarion.client.gui.DocumentationGui;
import org.illarion.engine.GameContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Fredrik K
 */
public class DocumentationHandler implements DocumentationGui, ScreenController, UpdatableHandler {
    /**
     * The logging instance of this class.
     */
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentationHandler.class);

    private Nifty nifty;
    private Screen screen;
    private Window documentationWindow;

    @Override
    public void toggleDocumentation() {
        if (isDocumentationVisible()) {
            hideDocumentation();
        } else {
            showDocumentation();
        }
    }

    @Nullable
    private Window getDocumentationWindow() {
        if (screen == null) {
            LOGGER.error("Can't fetch the documentation window as long as the quest handler is not bound to a screen.");
            return null;
        }
        if (documentationWindow == null) {
            documentationWindow = screen.findNiftyControl("helpDialog", Window.class);
        }
        if (documentationWindow == null) {
            LOGGER.error("Fetching the documentation window failed. Seems its not yet created.");
        }
        return documentationWindow;
    }

    @Nullable
    private Element getDocumentationWindowElement() {
        Window documentationWindow = getDocumentationWindow();
        if (documentationWindow == null) {
            return null;
        }
        return documentationWindow.getElement();
    }

    public boolean isDocumentationVisible() {
        Element documentationWindow = getDocumentationWindowElement();
        return documentationWindow != null && documentationWindow.isVisible();
    }

    public void hideDocumentation() {
        Window documentationWindow = getDocumentationWindow();
        if (documentationWindow != null) {
            documentationWindow.closeWindow();
        }
    }

    public void showDocumentation() {
        Element documentationWindow = getDocumentationWindowElement();
        if (documentationWindow == null) {
            LOGGER.error("Showing the documentation failed. The required GUI element can't be located.");
        } else {
            documentationWindow.show(new EndNotify() {
                @Override
                public void perform() {
                    Window documentationWindow = getDocumentationWindow();
                    if (documentationWindow != null) {
                        documentationWindow.moveToFront();
                    }
                }
            });
        }
    }

    @Override
    public void bind(@Nonnull final Nifty nifty, @Nonnull final Screen screen) {
        this.nifty = nifty;
        this.screen = screen;

        documentationWindow = getDocumentationWindow();
        if ((documentationWindow != null && documentationWindow.getElement() != null)) {
            documentationWindow.getElement()
                    .setConstraintX(new SizeValue(IllaClient.getCfg().getString("docuWindowPosX")));
            documentationWindow.getElement()
                    .setConstraintY(new SizeValue(IllaClient.getCfg().getString("docuWindowPosY")));
        }
        createDocumentationEntries();
    }

    private void createDocumentationEntries() {
        if (documentationWindow == null || documentationWindow.getElement() == null) {
            return;
        }
        final Element content = documentationWindow.getElement().findElementById("#textContent");
        if (content == null) {
            return;
        }

        int groupCnt = 0;
        for (final DocuEntry group : DocuRoot.getInstance()) {
            final String groupId = content.getId() + "#group" + Integer.toString(groupCnt++);
            final PanelBuilder groupPanel = new PanelBuilder(groupId);
            groupPanel.childLayoutVertical();
            groupPanel.height(SizeValue.def());
            groupPanel.marginBottom("10px");
            groupPanel.marginLeft("5px");
            groupPanel.alignCenter();
            groupPanel.valignTop();

            final LabelBuilder headline = new LabelBuilder(groupId + "#headline");
            headline.font("menuFont");
            headline.label(group.getTitle());
            headline.width("*");
            headline.height("30px");
            groupPanel.control(headline);

            int entryCnt = 0;
            for (final DocuEntry child : group) {
                final String entryId = groupId + "#entry" + Integer.toString(entryCnt++);
                final PanelBuilder entryPanel = new PanelBuilder(entryId);
                entryPanel.childLayoutCenter();
                entryPanel.width(content.getConstraintWidth().toString());
                entryPanel.marginBottom("5px");

                final LabelBuilder entryKey = new LabelBuilder(entryId + "#key");
                entryKey.label(child.getTitle());
                entryKey.font("textFont");
                entryKey.width("40%");
                entryKey.height(SizeValue.def());
                entryKey.alignLeft();
                entryKey.textHAlignLeft();
                entryKey.wrap(true);
                entryKey.valignTop();
                entryPanel.control(entryKey);

                final LabelBuilder entryText = new LabelBuilder(entryId + "#text");
                entryText.label(child.getDescription());
                entryText.width("60%");
                entryText.font("textFont");
                entryText.height(SizeValue.def());
                entryText.wrap(true);
                entryText.alignRight();
                entryText.textHAlignLeft();
                entryText.valignTop();
                entryPanel.control(entryText);

                entryPanel.height(SizeValue.def());
                groupPanel.panel(entryPanel);
            }
            groupPanel.build(nifty, screen, content);
        }
    }

    @Override
    public void onStartScreen() {
        nifty.subscribeAnnotations(this);
    }

    @Override
    public void onEndScreen() {
        nifty.unsubscribeAnnotations(this);
        IllaClient.getCfg().set("docuWindowPosX", Integer.toString(documentationWindow.getElement().getX()) + "px");
        IllaClient.getCfg().set("docuWindowPosY", Integer.toString(documentationWindow.getElement().getY()) + "px");
    }

    @Override
    public void update(GameContainer container, int delta) {

    }

    @NiftyEventSubscriber(id = "openHelpBtn")
    public void onInventoryButtonClicked(final String topic, final ButtonClickedEvent data) {
        toggleDocumentation();
    }
}

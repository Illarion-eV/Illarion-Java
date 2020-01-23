/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
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

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.*;
import de.lessvoid.nifty.controls.ScrollPanel.AutoScroll;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.client.IllaClient;
import illarion.client.gui.BookGui;
import illarion.client.resources.BookFactory;
import illarion.client.util.Lang;
import illarion.common.data.*;
import org.illarion.engine.GameContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is used to manage the displaying of the books.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class BookHandler implements BookGui, ScreenController, UpdatableHandler {

    /**
     * Indicates that update() needs to be called
     */
    private boolean dirty;
    private int showPage;
    @Nullable
    private BookLanguage showBook;

    @Nullable
    private Window bookDisplay;
    @Nullable
    private Element bookTextContent;
    @Nullable
    private ScrollPanel bookScrollArea;
    @Nullable
    private Label pageNumberLabel;

    private Nifty nifty;
    private Screen screen;

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;
        this.screen = screen;

        bookDisplay = screen.findNiftyControl("book", Window.class);
        bookTextContent = bookDisplay.getElement().findElementById("#textContent");
        bookScrollArea = bookDisplay.getElement().findNiftyControl("#scrollArea", ScrollPanel.class);
        pageNumberLabel = bookDisplay.getElement().findNiftyControl("#pageNumber", Label.class);

        bookDisplay.getElement().setConstraintX(new SizeValue(IllaClient.getCfg().getString("bookDisplayPosX")));
        bookDisplay.getElement().setConstraintY(new SizeValue(IllaClient.getCfg().getString("bookDisplayPosY")));
        //bookDisplay.getElement().getParent().layoutElements();
    }

    @Override
    public void onStartScreen() {
        nifty.subscribeAnnotations(this);
    }

    @Override
    public void onEndScreen() {
        nifty.unsubscribeAnnotations(this);

        IllaClient.getCfg().set("bookDisplayPosX", Integer.toString(bookDisplay.getElement().getX()) + "px");
        IllaClient.getCfg().set("bookDisplayPosY", Integer.toString(bookDisplay.getElement().getY()) + "px");
    }

    @Override
    public void update(GameContainer container, int delta) {
        if (!dirty) {
            return;
        }

        dirty = false;

        if (showBook == null) {
            bookDisplay.closeWindow();
            return;
        }

        bookTextContent.getChildren().forEach(Element::markForRemoval);

        if ((showPage == 0) && showBook.hasTitlePage()) {
            BookTitlePage titlePage = showBook.getTitlePage();
            pageNumberLabel.setText("");

            LabelBuilder title = new LabelBuilder();
            title.label(titlePage.getTitle());
            title.font("menuFont");
            title.width(bookTextContent.getConstraintWidth().toString());
            title.wrap(true);
            title.marginBottom("30px");
            title.marginTop("25px");
            title.textHAlignLeft();
            title.build(nifty, screen, bookTextContent);

            if (titlePage.hasAuthor()) {
                LabelBuilder author = new LabelBuilder();
                author.label(titlePage.getAuthor());
                author.font("textFont");
                author.width(bookTextContent.getConstraintWidth().toString());
                author.wrap(true);
                author.textHAlignRight();
                author.build(nifty, screen, bookTextContent);
            }
        } else {
            int realPage = showBook.hasTitlePage() ? (showPage - 1) : showPage;

            if ((realPage < 0) || (realPage >= getTotalPageCount())) {
                dirty = true;
                showPage = 0;
                return;
            }

            BookPage page = showBook.getPage(realPage);

            for (BookPageEntry entry : page) {
                LabelBuilder entryLabel = new LabelBuilder();
                entryLabel.label(entry.getText());
                if (entry.isHeadline()) {
                    entryLabel.font("menuFont");
                    entryLabel.marginBottom("8px");
                } else {
                    entryLabel.font("textFont");
                    entryLabel.marginBottom("5px");
                }
                switch (entry.getAlignment()) {
                    case Left:
                        entryLabel.textHAlignLeft();
                        break;
                    case Right:
                        entryLabel.textHAlignRight();
                        break;
                    case Center:
                        entryLabel.textHAlignCenter();
                        break;
                }
                entryLabel.width(bookTextContent.getConstraintWidth().toString());
                entryLabel.wrap(true);
                entryLabel.marginTop("5px");
                entryLabel.build(nifty, screen, bookTextContent);
            }

            pageNumberLabel.setText(Integer.toString(realPage + 1));
        }

        bookDisplay.getElement().show();

        Element nextButton = bookDisplay.getElement().findElementById("book#buttonNext");
        if ((showPage + 1) < getTotalPageCount()) {
            nextButton.show();
        } else {
            nextButton.hide();
        }

        Element backButton = bookDisplay.getElement().findElementById("book#buttonBack");
        if (showPage > 0) {
            backButton.show();
        } else {
            backButton.hide();
        }

        bookDisplay.getElement().getParent().layoutElements();

        bookScrollArea.setAutoScroll(AutoScroll.TOP);
        bookScrollArea.setAutoScroll(AutoScroll.OFF);
    }

    private int getTotalPageCount() {
        if (showBook == null) {
            return 0;
        }
        int pageCount = showBook.getPageCount();
        if (showBook.hasTitlePage()) {
            return pageCount + 1;
        }
        return pageCount;
    }

    @NiftyEventSubscriber(id = "book#buttonNext")
    public void onNextButtonClickedEvent(String topic, ButtonClickedEvent data) {
        if ((showPage + 1) < getTotalPageCount()) {
            showPage++;
            dirty = true;
        }
    }

    @NiftyEventSubscriber(id = "book#buttonBack")
    public void onBackButtonClickedEvent(String topic, ButtonClickedEvent data) {
        if (showPage > 0) {
            showPage--;
            dirty = true;
        }
    }

    @NiftyEventSubscriber(id = "book")
    public void onHideWindow(String topic, WindowClosedEvent data) {
        hideBook();
    }

    /**
     * Show the book with the specified ID.
     *
     * @param id the id of the book to show
     */
    @Override
    public void showBook(int id) {
        Book book = BookFactory.getInstance().getBook(id);
        if (book != null) {
            showBook = book.getLocalisedBook(Lang.getInstance().getLocale());
            showPage = 0;
        } else {
            showBook = null;
        }
        dirty = true;
    }

    @Override
    public void hideBook() {
        showBook = null;
        dirty = true;
    }
}

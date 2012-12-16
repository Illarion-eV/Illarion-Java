/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.gui.controller.game;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.Window;
import de.lessvoid.nifty.controls.WindowClosedEvent;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.net.server.events.ShowBookEvent;
import illarion.client.resources.BookFactory;
import illarion.client.util.Lang;
import illarion.common.data.*;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.newdawn.slick.GameContainer;

/**
 * This class is used to manage the displaying of the books.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class BookHandler implements ScreenController, UpdatableHandler {

    private boolean dirty;
    private int showPage;
    private BookLanguage showBook;

    private Window bookDisplay;
    private Element bookTextContent;
    private Label pageNumberLabel;

    private Nifty nifty;
    private Screen screen;

    @Override
    public void bind(final Nifty nifty, final Screen screen) {
        this.nifty = nifty;
        this.screen = screen;

        bookDisplay = screen.findNiftyControl("book", Window.class);
        bookTextContent = bookDisplay.getElement().findElementByName("#textContent");
        pageNumberLabel = bookDisplay.getElement().findNiftyControl("#pageNumber", Label.class);
    }

    @Override
    public void onStartScreen() {
        AnnotationProcessor.process(this);
        nifty.subscribeAnnotations(this);
    }

    @Override
    public void onEndScreen() {
        AnnotationProcessor.unprocess(this);
        nifty.unsubscribeAnnotations(this);
    }

    @Override
    public void update(final GameContainer container, final int delta) {
        if (!dirty) {
            return;
        }

        dirty = false;

        if (showBook == null) {
            bookDisplay.closeWindow();
            return;
        }

        for (final Element child : bookTextContent.getElements()) {
            child.markForRemoval();
        }

        if ((showPage == 0) && showBook.hasTitlePage()) {
            final BookTitlePage titlePage = showBook.getTitlePage();
            pageNumberLabel.setText("");

            final LabelBuilder title = new LabelBuilder();
            title.label(titlePage.getTitle());
            title.font("menuFont");
            title.width(bookTextContent.getConstraintWidth().toString());
            title.wrap(true);
            title.marginBottom("30px");
            title.marginTop("25px");
            title.textHAlignLeft();
            title.build(nifty, screen, bookTextContent);

            if (titlePage.hasAuthor()) {
                final LabelBuilder author = new LabelBuilder();
                author.label(titlePage.getAuthor());
                author.font("textFont");
                author.width(bookTextContent.getConstraintWidth().toString());
                author.wrap(true);
                author.textHAlignRight();
                author.build(nifty, screen, bookTextContent);
            }
        } else {
            final int realPage;
            if (showBook.hasTitlePage()) {
                realPage = showPage - 1;
            } else {
                realPage = showPage;
            }

            if ((realPage < 0) || (realPage >= getTotalPageCount())) {
                dirty = true;
                showPage = 0;
                return;
            }

            final BookPage page = showBook.getPage(realPage);

            for (final BookPageEntry entry : page) {
                final LabelBuilder entryLabel = new LabelBuilder();
                entryLabel.label(entry.getText());
                if (entry.isHeadline()) {
                    entryLabel.font("menuFont");
                    entryLabel.marginBottom("8px");
                } else {
                    entryLabel.font("textFont");
                    entryLabel.marginBottom("5px");
                }
                entryLabel.width(bookTextContent.getConstraintWidth().toString());
                entryLabel.wrap(true);
                entryLabel.marginTop("5px");
                entryLabel.textHAlignLeft();
                entryLabel.build(nifty, screen, bookTextContent);
            }

            pageNumberLabel.setText(Integer.toString(realPage + 1));
        }

        bookDisplay.getElement().show();

        final Element nextButton = bookDisplay.getElement().findElementByName("book#buttonNext");
        if ((showPage + 1) < getTotalPageCount()) {
            nextButton.show();
        } else {
            nextButton.hide();
        }

        final Element backButton = bookDisplay.getElement().findElementByName("book#buttonBack");
        if (showPage > 0) {
            backButton.show();
        } else {
            backButton.hide();
        }

        bookDisplay.getElement().getParent().layoutElements();
    }

    @EventSubscriber
    public void onBookReceivedEvent(final ShowBookEvent data) {
        final Book book = BookFactory.getInstance().getBook(data.getBookId());
        if (book != null) {
            showBook = book.getLocalisedBook(Lang.getInstance().getLocale());
            showPage = 0;
        } else {
            showBook = null;
        }
        dirty = true;
    }

    private int getTotalPageCount() {
        if (showBook == null) {
            return 0;
        }
        final int pageCount = showBook.getPageCount();
        if (showBook.hasTitlePage()) {
            return pageCount + 1;
        }
        return pageCount;
    }

    @NiftyEventSubscriber(id = "book#buttonNext")
    public void onNextButtonClickedEvent(final String topic, final ButtonClickedEvent data) {
        if ((showPage + 1) < getTotalPageCount()) {
            showPage++;
            dirty = true;
        }
    }

    @NiftyEventSubscriber(id = "book#buttonBack")
    public void onBackButtonClickedEvent(final String topic, final ButtonClickedEvent data) {
        if (showPage > 0) {
            showPage--;
            dirty = true;
        }
    }

    @NiftyEventSubscriber(id = "book")
    public void onHideWindow(final String topic, final WindowClosedEvent data) {
        showBook = null;
        dirty = true;
    }
}

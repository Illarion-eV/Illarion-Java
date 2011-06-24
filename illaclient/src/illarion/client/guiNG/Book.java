/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.guiNG;

import java.awt.Rectangle;
import java.util.ArrayList;

import illarion.client.graphics.Colors;
import illarion.client.graphics.MarkerFactory;
import illarion.client.guiNG.elements.Button;
import illarion.client.guiNG.elements.Image;
import illarion.client.guiNG.elements.Text;
import illarion.client.guiNG.elements.TextArea;
import illarion.client.guiNG.elements.Widget;
import illarion.client.guiNG.event.BookButtonEvent;
import illarion.client.guiNG.event.ToggleWidgetsEvent;
import illarion.client.guiNG.init.ImageInit;

import illarion.graphics.RenderableFont;
import illarion.graphics.common.FontLoader;

/**
 * The book is displayed on the screen if the user reads a book.
 * 
 * @author Blay09
 * @since 1.22
 */
public final class Book extends Widget {

    /**
     * The serialization UID of the book.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The text of the currently displayed book.
     */
    private ArrayList<String> bookPages;

    /**
     * The title of the currently displayed book.
     */
    private String bookTitle;

    /**
     * The button used to go to the next page.
     */
    private final Button buttonNextPage;

    /**
     * The button used to go to the previous page.
     */
    private final Button buttonPrevPage;

    /**
     * The currently displayed page of the book.
     */
    private int currentPage;

    /**
     * The dirty flag for the book.
     */
    private boolean dirtyBook;

    /**
     * The image shown in buttonNextPage
     */
    private final Image imageNextPage;

    /**
     * The image shown in buttonNextPage when hovering
     */
    private final Image imageNextPageHover;

    /**
     * The image shown in buttonPrevPage
     */
    private final Image imagePrevPage;

    /**
     * The image shown in buttonPrevPage when hovering
     */
    private final Image imagePrevPageHover;

    /**
     * The current maximum of pages.
     */
    private int maxPages;

    /**
     * The text element which displays the current page number.
     */
    private final Text pageNumber;

    /**
     * The background image of the current page number.
     */
    private final Image pageNumberBackground;

    /**
     * The text area used to display the text.
     */
    private final TextArea pageText;

    /**
     * The font used to render the text.
     */
    private transient final RenderableFont textFont = FontLoader.getInstance()
        .getFont(FontLoader.TEXT_FONT);

    /**
     * Setup the book so a text can be assigned.
     */
    public Book() {
        super();

        maxPages = 0;
        currentPage = 1;
        dirtyBook = true;
        bookPages = new ArrayList<String>();
        bookTitle = "";

        setWidth(300);
        setHeight(400);
        setVisible(true);

        pageText = new TextArea();
        pageText.setMaximalWidth(getWidth() - 30);
        pageText.setFont(textFont);
        pageText.setColor(Colors.black.getColor());
        addChild(pageText);

        buttonNextPage = new Button();
        addChild(buttonNextPage);
        buttonNextPage.setWidth(50);
        buttonNextPage.setHeight(50);
        buttonNextPage.setRelPos(160, 18);
        buttonNextPage.setVisible(true);
        final BookButtonEvent nextpageevent = BookButtonEvent.getInstance();
        nextpageevent.setButtonType(BookButtonEvent.TYPE_NEXT);
        buttonNextPage.setClickHandler(nextpageevent);
        buttonNextPage.setAllowDoubleClick(true);

        imageNextPage = new Image();
        imageNextPage.setRelPos(0, 0);
        imageNextPage.setInitScript(ImageInit.getInstance().setImageID(
            MarkerFactory.GUI_BOOK_ARROWR));
        imageNextPage.setSizeToImage();
        imageNextPage.setVisible(true);
        buttonNextPage.addChild(imageNextPage);
        imageNextPageHover = new Image();
        imageNextPageHover.setRelPos(0, 0);
        imageNextPageHover.setInitScript(ImageInit.getInstance().setImageID(
            MarkerFactory.GUI_BOOK_ARROWR_HOVER));
        imageNextPageHover.setSizeToImage();
        imageNextPageHover.setVisible(false);
        buttonNextPage.addChild(imageNextPageHover);
        final ToggleWidgetsEvent nextpagehov =
            ToggleWidgetsEvent.getInstance();
        nextpagehov.setEffectedWidgets(imageNextPageHover, imageNextPage);
        final ToggleWidgetsEvent nextpageunhov =
            ToggleWidgetsEvent.getInstance();
        nextpageunhov.setEffectedWidgets(imageNextPage, imageNextPageHover);
        buttonNextPage.setHoverHandler(nextpagehov, nextpageunhov);

        pageNumberBackground = new Image();
        pageNumberBackground.setRelPos(135, 10);
        pageNumberBackground.setInitScript(ImageInit.getInstance().setImageID(
            MarkerFactory.GUI_BOOK_STOP));
        pageNumberBackground.setSizeToImage();
        pageNumberBackground.setVisible(true);
        addChild(pageNumberBackground);
        pageNumber = new Text();
        pageNumber.setFont(textFont);
        pageNumber.setColor(Colors.black.getColor());
        pageNumber.setText(String.valueOf(currentPage));
        pageNumber.setWidth(pageNumberBackground.getWidth());
        pageNumber.setHeight(pageNumberBackground.getHeight());
        pageNumber.setRelPos(9, 22);
        pageNumber.setVisible(true);
        pageNumberBackground.addChild(pageNumber);

        buttonPrevPage = new Button();
        addChild(buttonPrevPage);
        buttonPrevPage.setWidth(50);
        buttonPrevPage.setHeight(50);
        buttonPrevPage.setRelPos(85, 18);
        buttonPrevPage.setVisible(true);
        final BookButtonEvent prevpageevent = BookButtonEvent.getInstance();
        prevpageevent.setButtonType(BookButtonEvent.TYPE_PREV);
        buttonPrevPage.setClickHandler(prevpageevent);
        buttonPrevPage.setAllowDoubleClick(true);

        imagePrevPage = new Image();
        imagePrevPage.setRelPos(0, 0);
        imagePrevPage.setInitScript(ImageInit.getInstance().setImageID(
            MarkerFactory.GUI_BOOK_ARROWL));
        imagePrevPage.setSizeToImage();
        imagePrevPage.setVisible(true);
        buttonPrevPage.addChild(imagePrevPage);
        imagePrevPageHover = new Image();
        imagePrevPageHover.setRelPos(0, 0);
        imagePrevPageHover.setInitScript(ImageInit.getInstance().setImageID(
            MarkerFactory.GUI_BOOK_ARROWL_HOVER));
        imagePrevPageHover.setSizeToImage();
        imagePrevPageHover.setVisible(false);
        buttonPrevPage.addChild(imagePrevPageHover);

        final ToggleWidgetsEvent prevpagehov =
            ToggleWidgetsEvent.getInstance();
        prevpagehov.setEffectedWidgets(imagePrevPageHover, imagePrevPage);
        final ToggleWidgetsEvent prevpageunhov =
            ToggleWidgetsEvent.getInstance();
        prevpageunhov.setEffectedWidgets(imagePrevPage, imagePrevPageHover);
        buttonPrevPage.setHoverHandler(prevpagehov, prevpageunhov);
    }

    /**
     * Cleanup the book before saving. That results in removing the book from
     * the tree because it's only displayed when reading anyway.
     */
    @Override
    public void cleanup() {
        super.cleanup();
        if (hasParent()) {
            getParent().removeChild(this);
        }
    }

    /**
     * Returns the number of pages in a book.
     * 
     * @return the number of pages in the book
     */
    public int countPages() {
        return maxPages;
    }

    /**
     * Draw the book.
     * 
     * @param delta the time in milliseconds since the last render
     */
    @Override
    public void draw(final int delta) {
        if (!isVisible()) {
            return;
        }
        if (dirtyBook == true) {
            update();
        }
        super.draw(delta);
    }

    /**
     * Returns the page the player is looking at currently.
     * 
     * @return the currently displayed page
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Navigate to a page in a book.
     * 
     * @param Page the page to which should be navigated to
     */
    public void gotoPage(final int Page) {
        if ((Page > maxPages) || (Page < 1)) {
            return;
        }
        currentPage = Page;
        dirtyBook = true;
    }

    /**
     * Navigate to the next page in a book.
     */
    public void nextPage() {
        if (currentPage >= maxPages) {
            return;
        }
        currentPage++;
        dirtyBook = true;
    }

    /**
     * Navigate to the previous page in a book.
     */
    public void prevPage() {
        if (currentPage <= 1) {
            return;
        }
        currentPage--;
        dirtyBook = true;
    }

    /**
     * Sets the text of the book.
     * 
     * @param BookText the array with the text of the book - one element
     *            corresponds to one page.
     */
    public void setBookText(final ArrayList<String> BookText) {
        bookPages = BookText;
        maxPages = BookText.size();
        currentPage = 1;
        dirtyBook = true;
    }

    /**
     * Sets the title of the book.
     * 
     * @param BookTitle the new title for the book
     */
    public void setBookTitle(final String BookTitle) {
        bookTitle = BookTitle;
        dirtyBook = true;
    }

    /**
     * Update the book text + title (e.g. when on another page) and the
     * navigation buttons.
     */
    private void update() {
        GUI.getInstance().getBookWindow().getTitleText().setText(bookTitle);
        pageText.setText(bookPages.get(currentPage - 1));
        final Rectangle bounds = pageText.getBounds();
        pageText.setHeight(bounds.height + 30);
        pageText.setWidth(bounds.width);
        pageText.setRelPos(15, getHeight() - bounds.height);

        if ((currentPage + 1) > maxPages) {
            imageNextPage.setVisible(true);
            imageNextPageHover.setVisible(false);
            buttonNextPage.setVisible(false);
        } else {
            buttonNextPage.setVisible(true);
        }
        if ((currentPage - 1) < 1) {
            imagePrevPage.setVisible(true);
            imagePrevPageHover.setVisible(false);
            buttonPrevPage.setVisible(false);
        } else {
            buttonPrevPage.setVisible(true);
        }

        pageNumber.setText(String.valueOf(currentPage));
        if (currentPage > 9) {
            pageNumber.setRelPos(4, 22);
        } else {
            pageNumber.setRelPos(9, 22);
        }
        dirtyBook = false;
    }

}

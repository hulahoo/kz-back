package kz.uco.tsadv.web.book;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.config.BookConfig;
import kz.uco.tsadv.entity.Book;
import kz.uco.tsadv.entity.BookReview;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.web.gui.components.WebRateStars;
import kz.uco.tsadv.web.toolkit.ui.ratestarscomponent.RateStarsComponent;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;

public class BookInfo extends AbstractWindow {

    @Inject
    protected Datasource<Book> bookDs;
    @Inject
    protected CollectionDatasource<BookReview, UUID> reviewsDs;
    @Inject
    protected Label book_name;
    @Inject
    protected Label book_author;
    @Inject
    protected Image bookImage;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected HBoxLayout rateStarsWrap;
    @Inject
    protected ButtonsPanel buttonsPanel_1;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected ExportDisplay exportDisplay;
    @Inject
    protected PopupButton download_book;
    @Inject
    protected VBoxLayout book_reviews;
    @Inject
    protected Button new_review;
    @Inject
    protected Metadata metadata;
    @Inject
    protected UserSession userSession;
    @Inject
    protected BookConfig bookConfig;
    @Inject
    protected VBoxLayout imageVBox;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.containsKey("book")) {
            Book book = (Book) params.get("book");
            book = dataManager.reload(book, "book-edit-view");
            bookDs.setItem(book);

            book_name.setValue(book.getBookNameLang1());
            book_author.setValue(book.getAuthorLang1());
            getDefaultImage();

// Отображение текущей оценки книги
            rateStarsWrap.add(fillRateStarsWrap(book));

// Определение видимости форматов на кнопке скачивания книги
            setPopupButtonActionsVisibility(book);

            fillReviewsTab(book);
        }
    }

    protected void getDefaultImage() {
        if (bookImage.getSource() == null){
            Image image = componentsFactory.createComponent(Image.class);
            image.setSource(ThemeResource.class)
                    .setPath(bookConfig.getDefaultImage());
            image.setHeight("350px");
            image.setWidth("250px");
            image.setAlignment(Alignment.TOP_CENTER);
            image.setScaleMode(Image.ScaleMode.CONTAIN);
            imageVBox.removeAll();
            imageVBox.add(image);
        }
    }

    /**
     * Наполнение вкладки отзывов
     *
     * @param book
     */
    private void fillReviewsTab(Book book) {
        List<BookReview> reviews = new ArrayList<>(book.getReviews());
        reviews.sort(Comparator.comparing(BookReview::getPostDate));
        for (BookReview review : reviews) {
            VBoxLayout card = componentsFactory.createComponent(VBoxLayout.class);
            card.setSpacing(true);
            card.setWidth("100%");
            card.setHeightAuto();
            card.setMargin(true);

            HBoxLayout titleBox = componentsFactory.createComponent(HBoxLayout.class);
            titleBox.setSpacing(true);
            titleBox.setWidthAuto();
            titleBox.setHeight("20px");

            Label authorLabel = componentsFactory.createComponent(Label.class);
            authorLabel.setAlignment(Alignment.TOP_LEFT);
            authorLabel.setValue(review.getAuthor());
            authorLabel.setStyleName("author-name");

            WebRateStars ratingStars = componentsFactory.createComponent(WebRateStars.class);
            ratingStars.setId("ratingStars");
            ratingStars.setAlignment(Alignment.TOP_RIGHT);
            RateStarsComponent rateStarsComponent = (RateStarsComponent) ratingStars.getComponent();
            rateStarsComponent.setListener(rateStarsComponent::setValue);
            rateStarsComponent.setReadOnly(true);
            rateStarsComponent.setValue(review.getRating().doubleValue());

            titleBox.add(authorLabel);
            titleBox.add(ratingStars);

            Label bookReviewText = componentsFactory.createComponent(Label.class);
            bookReviewText.setAlignment(Alignment.MIDDLE_LEFT);
            bookReviewText.setValue(review.getReviewText());
            bookReviewText.setWidth("100%");

            Label postDateLabel = componentsFactory.createComponent(Label.class);
            postDateLabel.setAlignment(Alignment.TOP_RIGHT);
            postDateLabel.setValue(review.getPostDate());
            postDateLabel.setStyleName("author-name");

            card.add(titleBox);
            card.add(bookReviewText);
            card.add(postDateLabel);
            card.expand(bookReviewText);

            book_reviews.add(card);
        }
    }

    /**
     * Отображение средней оценки книги
     */
    protected WebRateStars fillRateStarsWrap(Book book) {
        WebRateStars stars = componentsFactory.createComponent(WebRateStars.class);
        stars.setId("stars");
        RateStarsComponent rateStarsComponent = (RateStarsComponent) stars.getComponent();
        rateStarsComponent.setListener(rateStarsComponent::setValue);
        rateStarsComponent.setReadOnly(true);
        fillAvgRate(book);
        rateStarsComponent.setValue(book.getAverageScore().doubleValue());
        return stars;
    }

    /**
     * Установить видимость опции у кнопки "Скачать книгу"
     *
     * @param book
     */
    protected void setPopupButtonActionsVisibility(Book book) {
        if (book.getPdf() == null) download_book.getAction("pdf").setVisible(false);
        else download_book.getAction("pdf").setVisible(true);

        if (book.getDjvu() == null) download_book.getAction("djvu").setVisible(false);
        else download_book.getAction("djvu").setVisible(true);

        if (book.getKf8() == null) download_book.getAction("kf8").setVisible(false);
        else download_book.getAction("kf8").setVisible(true);

        if (book.getFb2() == null) download_book.getAction("fb2").setVisible(false);
        else download_book.getAction("fb2").setVisible(true);

        if (book.getMobi() == null) download_book.getAction("mobi").setVisible(false);
        else download_book.getAction("mobi").setVisible(true);

        if (book.getEpub() == null) download_book.getAction("epub").setVisible(false);
        else download_book.getAction("epub").setVisible(true);
    }


    public void onDownloadEpub(Component source) {
        Book book = bookDs.getItem();
        if (book.getEpub() != null)
            exportDisplay.show(book.getEpub(), ExportFormat.getByExtension("epub"));
    }

    public void onDownloadPdf(Component source) {
        Book book = bookDs.getItem();
        if (book.getPdf() != null)
            exportDisplay.show(book.getPdf(), ExportFormat.PDF);
    }

    public void onDownloadDjvu(Component source) {
        Book book = bookDs.getItem();
        if (book.getDjvu() != null)
            exportDisplay.show(book.getDjvu(), ExportFormat.getByExtension("djvu"));
    }

    public void onDownloadMobi(Component source) {
        Book book = bookDs.getItem();
        if (book.getMobi() != null)
            exportDisplay.show(book.getMobi(), ExportFormat.getByExtension("mobi"));
    }

    public void onDownloadFb2(Component source) {
        Book book = bookDs.getItem();
        if (book.getFb2() != null)
            exportDisplay.show(book.getFb2(), ExportFormat.getByExtension("fb2"));
    }

    public void onDownloadKf8(Component source) {
        Book book = bookDs.getItem();
        if (book.getKf8() != null)
            exportDisplay.show(book.getKf8(), ExportFormat.getByExtension("kf8"));
    }

    public void addToMyBooks(Component source) {
        showNotification(getMessage("addToMyBooks"), NotificationType.TRAY);
        source.setVisible(false);
        Button button = componentsFactory.createComponent(Button.class);
        button.setCaption("Удалить из полки");
        button.setStyleName("add_my_books");
        button.setResponsive(true);
        button.setWidth("100%");
        buttonsPanel_1.add(button);
    }


    /**
     * Расчет средней оценки книги на основе оценок отзывов
     *
     * @param book
     */
    protected void fillAvgRate(Book book) {
        LoadContext<BookReview> loadContext = LoadContext.create(BookReview.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e " +
                        "from tsadv$BookReview e " +
                        "where e.book.id = :bookId")
                .setParameter("bookId", book.getId()))
                .setView("bookReview-edit-view");
        List<BookReview> reviewList = dataManager.loadList(loadContext);

        BigDecimal sum = BigDecimal.ZERO;

        book.setAverageScore(BigDecimal.ZERO);

        if (!reviewList.isEmpty()) {
            for (BookReview courseReview : reviewList) {
                sum = sum.add(courseReview.getRating() == null ? BigDecimal.ZERO : courseReview.getRating());
            }
            book.setAverageScore(sum.divide(BigDecimal.valueOf(reviewList.size()), 2));
        }
        dataManager.commit(book);
        bookDs.refresh();
    }

    public void addNewReview() {
        Book book = bookDs.getItem();
        BookReview review = metadata.create(BookReview.class);
        review.setBook(book);
        review.setAuthor(userSession.getAttribute(StaticVariable.USER_PERSON_GROUP));
        review.setPostDate(CommonUtils.getSystemDate());
        AbstractEditor editor = openEditor("tsadv$BookReview.edit",
                review,
                WindowManager.OpenType.DIALOG);
        editor.addCloseListener(e -> {
            refreshReviews();
        });
    }

    private void refreshReviews() {
        book_reviews.removeAll();
        bookDs.refresh();
        Book book = bookDs.getItem();
        fillReviewsTab(book);
        rateStarsWrap.remove(rateStarsWrap.getComponent("stars"));
        rateStarsWrap.add(fillRateStarsWrap(book));
    }
}
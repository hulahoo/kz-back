package kz.uco.tsadv.web.book;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.entity.Book;
import kz.uco.tsadv.entity.DicBookCategory;
import kz.uco.tsadv.service.BookService;
import kz.uco.tsadv.web.gui.components.WebRateStars;
import kz.uco.tsadv.web.toolkit.ui.ratestarscomponent.RateStarsComponent;

import javax.inject.Inject;
import java.util.*;

public class BookLibraryBrowse extends AbstractLookup {
    @Inject
    protected CollectionDatasource<Book, UUID> booksDs;
    @Inject
    protected HierarchicalDatasource<DicBookCategory, UUID> bookCategoriesDs;
    @Inject
    protected FlowBoxLayout bookCategories;
    @Inject
    protected FlowBoxLayout flowBox;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected BookService bookService;
    @Inject
    protected Tree<DicBookCategory> bookCategoriesTree;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected Metadata metadata;
    @Inject
    protected WindowConfig windowConfig;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        // Отрисовка дерева категории книг - добавление корневого элемента "Все" и добавление стилей
        paintTree();

        booksDs.addCollectionChangeListener(e -> {
            List<Book> books = new ArrayList<>(e.getDs().getItems());
            bookCategories.removeAll();
            if (!books.isEmpty()) {
                books.sort(Comparator.comparing(Book::getBookNameLang1));
                for (Book book : books) {
                    if (book.getActive() != null && book.getActive()) {
                        bookCategories.add(createCardAllBooks(book, getWindowManager()));
                    }
                }
            }
        });

        bookCategoriesDs.addItemChangeListener(e -> {
            if (e.getItem() != null) {
                if (e.getItem().getLangValue().equals("Все")){
                    booksDs.setQuery("select e " +
                            "from tsadv$Book e " +
                            "where e.active = true order by e.bookNameLang1");
                    ((CollectionDatasource.SupportsPaging) booksDs).setFirstResult(0);
                    booksDs.refresh();
                } else {
                    String categories = getCategoryHierarchy(e.getItem().getId());
                    booksDs.setQuery(String.format("select e " +
                            "from tsadv$Book e " +
                            "where e.category.id in (%s) " +
                            "and e.active = true order by e.bookNameLang1", categories));
                    ((CollectionDatasource.SupportsPaging) booksDs).setFirstResult(0);
                    booksDs.refresh();
                }
            }else {
                booksDs.setQuery("select e from tsadv$Book e where 1<>1 ");
                ((CollectionDatasource.SupportsPaging) booksDs).setFirstResult(0);
                booksDs.refresh();
            }
        });
    }

    /**
     * Отрисовка дерева категории книг - добавление корневого элемента "Все" и добавление стилей
     */
    protected void paintTree() {
        bookCategoriesDs.setAllowCommit(false);
        DicBookCategory root = metadata.create(DicBookCategory.class);
        root.setLangValue1("Все");
        root.setActive(true);
        bookCategoriesDs.includeItem(root);

        ArrayList<DicBookCategory> treeBook = new ArrayList<>(bookCategoriesDs.getItems());
        for (DicBookCategory category : treeBook) {
            if (!category.getLangValue().equals("Все")){
                category.setParentBookCategory(root);
            }
        }
        bookCategoriesTree.expandTree();
        bookCategoriesTree.setStyleProvider(entity -> entity.getLangValue1().equals("Все") ? "book-name" : "author-name");
    }


    public FlowBoxLayout createCardAllBooks(Book book, WindowManager windowManager){
        FlowBoxLayout card = componentsFactory.createComponent(FlowBoxLayout.class);
        card.setSpacing(true);
        card.setHeightAuto();
        card.setWidth("300px");
        card.addLayoutClickListener(event -> {
            WindowInfo windowInfo = windowConfig.getWindowInfo("book-info");
            windowManager.openWindow(windowInfo, WindowManager.OpenType.NEW_TAB, new HashMap<String, Object>(){{
                put("book", book);
            }});
        });

        Label nameLabel = getBookLabel(book.getBookNameLang1());
        nameLabel.setStyleName("book-name-library");

        Label authorLabel = getBookLabel(book.getAuthorLang1());
        authorLabel.setStyleName("author-name");

        Image image = getBookImage(book);
        if (image != null){
            image.setStyleName("book-image");
            card.add(image);
        }

// Отображение текущей оценки книги
        HBoxLayout rateStarsWrap = getRateStarsWrap(book);
        card.add(rateStarsWrap);
        card.add(nameLabel);
        card.add(authorLabel);
        return card;
    }

    /**
     * возвращает компонент рейтинга книги (звёздочки)
    **/
    protected HBoxLayout getRateStarsWrap(Book book) {
        HBoxLayout rateStarsWrap = componentsFactory.createComponent(HBoxLayout.class);
        rateStarsWrap.setId("rateStarsWrap");

        WebRateStars rateStars = componentsFactory.createComponent(WebRateStars.class);
        rateStars.setId("rateStars");
        RateStarsComponent rateStarsComponent = (RateStarsComponent) rateStars.getComponent();
        rateStarsComponent.setListener(rateStarsComponent::setValue);
        rateStarsComponent.setReadOnly(true);
        rateStarsComponent.setValue(book.getAverageScore().doubleValue());
        rateStarsWrap.add(rateStars);
        return rateStarsWrap;
    }

    protected Image getBookImage(Book book) {
        Image image = componentsFactory.createComponent(Image.class);
        image.setScaleMode(Image.ScaleMode.CONTAIN);
        image.setHeight("300px");
        image.setWidth("200px");
        image.setAlignment(Alignment.TOP_LEFT);
        FileDescriptor bookImage = book.getImage();
        if (bookImage != null)
            image.setSource(FileDescriptorResource.class).setFileDescriptor(bookImage);
        else image.setSource(ThemeResource.class)
                .setPath("images/default-book.jpeg");
        return image;
    }

    protected Label getBookLabel(String text) {
        Label authorLabel = componentsFactory.createComponent(Label.class);
        authorLabel.setAlignment(Alignment.BOTTOM_CENTER);
        authorLabel.setWidth("250px");
        authorLabel.setValue(text);
        return authorLabel;
    }

    protected String getCategoryHierarchy(UUID categoryId) {
        List<UUID> list = bookService.getCategoryHierarchy(String.valueOf(categoryId));

        StringBuilder sb = new StringBuilder("");
        for (Object uuid : list) {
            sb.append("'").append(uuid).append("',");
        }
        return sb.toString().substring(0, sb.toString().length() - 1);
    }
}
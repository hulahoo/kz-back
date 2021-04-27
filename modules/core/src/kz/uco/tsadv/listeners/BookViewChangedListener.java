package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.SilentException;
import com.haulmont.cuba.core.global.View;
import kz.uco.tsadv.entity.Book;
import kz.uco.tsadv.modules.learning.model.BookView;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.util.UUID;

@Component("tsadv_BookViewChangedListener")
public class BookViewChangedListener {

    @Inject
    private TransactionalDataManager transactionalDataManager;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void beforeCommit(EntityChangedEvent<BookView, UUID> event) {
        BookView saveBookView = transactionalDataManager.load(event.getEntityId())
                .view("bookView.personGroup.book")
                .one();
        final BookView existsBookView = transactionalDataManager.load(LoadContext.create(BookView.class).setQuery(LoadContext.createQuery("" +
                "select e " +
                "from tsadv$BookView e " +
                "where e.personGroup = :personGroup" +
                "   and e.book = :book" +
                "   and not e.id = :currentBvId")
                .setParameter("personGroup", saveBookView.getPersonGroup())
                .setParameter("currentBvId", event.getEntityId().getValue())
                .setParameter("book", saveBookView.getBook()))
                .setView(View.MINIMAL));
        if (existsBookView == null) {
            addViewedToBook(saveBookView.getBook());
        } else {
            throw new SilentException();
        }
    }

    private void addViewedToBook(Book book) {
        book.setViewed(ObjectUtils.defaultIfNull(book.getViewed(), 0) + 1);
        transactionalDataManager.save(book);
    }
}
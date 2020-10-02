package kz.uco.tsadv.service;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Service(BookService.NAME)
public class BookServiceBean implements BookService {
    @Inject
    protected Persistence persistence;

    @Override
    public List<UUID> getCategoryHierarchy(String categoryId) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(String.format("WITH RECURSIVE r AS ( " +
                    "  SELECT id " +
                    "  FROM tsadv_dic_book_category " +
                    "  WHERE id = '%s' " +
                    "  UNION " +
                    "  SELECT tdbc.id " +
                    "  FROM tsadv_dic_book_category tdbc " +
                    "    JOIN r " +
                    "      ON tdbc.parent_category_id = r.id and tdbc.delete_ts is null " +
                    ") " +
                    "SELECT * FROM r", categoryId));
            return query.getResultList();
        }
    }
}
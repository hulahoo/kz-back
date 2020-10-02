package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import kz.uco.tsadv.modules.learning.model.BudgetHeader;
import kz.uco.tsadv.modules.learning.model.BudgetHeaderHistory;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.service.EmployeeService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

@Component("tsadv_BudgetHeaderListener")
public class BudgetHeaderListener implements AfterInsertEntityListener<BudgetHeader> {
    @Inject
    private Metadata metadata;
    @Inject
    private EmployeeService employeeService;
    @Inject
    private UserSessionSource userSessionSource;
    @Inject
    private Persistence persistence;

    //todo call on onBeforeInsert or use queryRunner
    @Override
    public void onAfterInsert(BudgetHeader entity, Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.commit();//todo
            }

            try (Transaction tx = persistence.createTransaction()) {
                EntityManager em = persistence.getEntityManager();
                em.persist(createHistory(entity));
                tx.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private BudgetHeaderHistory createHistory(BudgetHeader entity) {
        UUID userId = userSessionSource.getUserSession().getUser().getId();
        PersonGroupExt personGroup = employeeService.getPersonGroupByUserId(userId);

        BudgetHeaderHistory history = metadata.create(BudgetHeaderHistory.class);
        history.setBudgetHeader(entity);
        history.setChangePerson(personGroup);
        history.setBudgetHeaderStatus(entity.getStatus());
        return history;
    }


}
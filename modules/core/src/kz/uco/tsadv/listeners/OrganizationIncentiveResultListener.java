package kz.uco.tsadv.listeners;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.tsadv.modules.personal.model.OrganizationIncentiveMonthResult;
import kz.uco.tsadv.modules.personal.model.OrganizationIncentiveResult;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Component("tsadv_OrganizationIncentiveResultListener")
public class OrganizationIncentiveResultListener {

    @Inject
    private TransactionalDataManager transactionalDataManager;
    @Inject
    private Metadata metadata;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void beforeCommit(EntityChangedEvent<OrganizationIncentiveResult, UUID> event) {

        if (event.getType() == EntityChangedEvent.Type.CREATED) {
            OrganizationIncentiveResult incentiveResult = loadEventEntity(event);
            OrganizationIncentiveMonthResult incentiveMonthResult = findOrganizationIncentiveMonthResult(
                    incentiveResult.getOrganizationGroup().getCompany(),
                    incentiveResult.getPeriodDate()
            );

            if (incentiveMonthResult == null) {
                incentiveMonthResult = metadata.create(OrganizationIncentiveMonthResult.class);
                incentiveMonthResult.setId(UUID.randomUUID());
                incentiveMonthResult.setCompany(incentiveResult.getOrganizationGroup().getCompany());
                Date incentiveMonthResultPeriod = formatDateToFirstDayOfMonth(incentiveResult.getPeriodDate());
                incentiveMonthResult.setPeriod(incentiveMonthResultPeriod);
            }

            incentiveResult.setOrganizationIncentiveMonthResult(incentiveMonthResult);
            if (incentiveResult.getPeriodDate() != null)
                incentiveResult.setPeriodDate(DateUtils.setDays(incentiveResult.getPeriodDate(), 1));
            transactionalDataManager.save(incentiveResult);
        }

    }

    protected OrganizationIncentiveResult loadEventEntity(EntityChangedEvent<OrganizationIncentiveResult, UUID> event) {
        return transactionalDataManager
                .load(event.getEntityId())
                .view("organizationIncentiveResults-for-incentiveMonthResult")
                .one();
    }

    protected OrganizationIncentiveMonthResult findOrganizationIncentiveMonthResult(DicCompany company, Date period) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(period);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month++; // increment month - because java.util.Calendar bases on zero-index

        String query = " select e from tsadv_OrganizationIncentiveMonthResult e " +
                " where e.company = :company " +
                " and EXTRACT(YEAR from e.period) = :year " +
                " and EXTRACT(MONTH from e.period) = :month ";

        return transactionalDataManager
                .load(LoadContext.create(OrganizationIncentiveMonthResult.class)
                        .setQuery(
                                LoadContext.createQuery(query)
                                        .setParameters(ParamsMap.of(
                                                "company", company,
                                                "year", year,
                                                "month", month)
                                        )
                        )
                );
    }

    protected Date formatDateToFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        return calendar.getTime();
    }
}